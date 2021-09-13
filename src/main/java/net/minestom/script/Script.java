package net.minestom.script;

import net.minestom.script.property.Properties;
import net.minestom.script.utils.FileUtils;
import net.minestom.script.utils.NbtConversionUtils;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.HostAccess;
import org.graalvm.polyglot.Source;
import org.graalvm.polyglot.Value;
import org.graalvm.polyglot.proxy.ProxyObject;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.locks.ReentrantLock;

public final class Script {
    private final String name;
    private final String source;
    private final String language;
    private final GlobalExecutor globalExecutor;

    private boolean loaded;
    private volatile Context context;
    private final ReentrantLock lock = new ReentrantLock();

    private Script(@NotNull String name, @NotNull String language, @NotNull String source, @NotNull GlobalExecutor globalExecutor) {
        this.name = name;
        this.source = source;
        this.language = language;
        this.globalExecutor = globalExecutor;
    }

    public static Script fromString(String name, String language, String source, GlobalExecutor executor) {
        return new Script(name, language, source, executor);
    }

    public static Script fromFile(String name, String language, Path source, GlobalExecutor executor) {
        return new Script(name, language, FileUtils.readFile(source), executor);
    }

    public void load() {
        if (loaded) return;
        this.loaded = true;
        final Source source = Source.create(language, this.source);
        assert source != null;
        this.context = createContext(source.getLanguage(), globalExecutor);
        sync(() -> context.eval(source));
        this.globalExecutor.register();
    }

    public void unload() {
        if (!loaded) return;
        this.loaded = false;
        this.globalExecutor.unregister();
        this.context.close();
    }

    public void sync(@NotNull Runnable runnable) {
        enter();
        runnable.run();
        leave();
    }

    void enter() {
        this.lock.lock();
        if (context != null) {
            this.context.enter();
        }
    }

    void leave() {
        this.lock.unlock();
        if (context != null) {
            this.context.leave();
        }
    }

    public @NotNull String name() {
        return name;
    }

    public @NotNull String language() {
        return language;
    }

    public @NotNull String source() {
        return source;
    }

    public @NotNull GlobalExecutor executor() {
        return globalExecutor;
    }

    public boolean isLoaded() {
        return loaded;
    }

    public Context context() {
        return context;
    }

    private static Context createContext(String language, GlobalExecutor globalExecutor) {
        HostAccess hostAccess = HostAccess.newBuilder(HostAccess.ALL)
                // Fix list being sent as map
                .targetTypeMapping(
                        List.class,
                        Object.class,
                        Objects::nonNull,
                        v -> v,
                        HostAccess.TargetMappingPrecedence.HIGHEST)
                // Convert all native objects to nbt compound
                .targetTypeMapping(
                        Map.class,
                        Object.class,
                        map -> map != null && !map.containsKey(Properties.TYPE_MEMBER),
                        NbtConversionUtils::fromMap)
                .build();

        Context context = Context.newBuilder(language)
                // Allows foreign object prototypes
                .allowExperimentalOptions(true)
                // Allows native js methods to be used on foreign (java) objects.
                // For example, calling Array.prototype.filter on java lists.
                .option("js.experimental-foreign-object-prototype", "true")
                .allowHostAccess(hostAccess)
                .build();

        Value bindings = context.getBindings(language);

        // Command globalExecutor
        bindings.putMember("executor", globalExecutor);

        // Event Signals
        Map<String, Object> eventBindings = new HashMap<>();
        for (EventSignal event : EventSignal.values()) {
            eventBindings.put(event.name(), event.name().toLowerCase());
        }
        bindings.putMember("signals", ProxyObject.fromMap(eventBindings));

        return context;
    }
}
