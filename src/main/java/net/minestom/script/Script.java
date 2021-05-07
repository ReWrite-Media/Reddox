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

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.locks.ReentrantLock;

public class Script {

    private final String name;
    private final String fileString;
    private final String language;
    private final GlobalExecutor globalExecutor;

    private boolean loaded;
    private Context context;
    private ReentrantLock lock = new ReentrantLock();

    public Script(@NotNull String name, @NotNull String fileString, @NotNull String language, @NotNull GlobalExecutor globalExecutor) {
        this.name = name;
        this.fileString = fileString;
        this.language = language;
        this.globalExecutor = globalExecutor;
    }

    public Script(@NotNull String name, @NotNull File file, @NotNull String language, @NotNull GlobalExecutor globalExecutor) {
        this(name, FileUtils.readFile(file), language, globalExecutor);
    }

    public void load() {
        if (loaded)
            return;
        this.loaded = true;

        final Source source = Source.create(language, fileString);
        assert source != null;
        this.context = createContext(source.getLanguage(), globalExecutor);
        sync(() -> context.eval(source));
        this.globalExecutor.register();
    }

    public void unload() {
        if (!loaded)
            return;
        this.loaded = false;
        this.globalExecutor.unregister();
        this.context.close();
    }

    public void sync(@NotNull Runnable runnable) {
        enter();
        runnable.run();
        leave();
    }

    protected void enter() {
        if (context == null)
            return;
        this.lock.lock();
        this.context.enter();
    }

    protected void leave() {
        if (context == null)
            return;
        this.lock.unlock();
        this.context.leave();
    }

    public @NotNull String getFileString() {
        return fileString;
    }

    public @NotNull String getName() {
        return name;
    }

    public @NotNull String getLanguage() {
        return language;
    }

    public @NotNull GlobalExecutor getExecutor() {
        return globalExecutor;
    }

    public boolean isLoaded() {
        return loaded;
    }

    public Context getContext() {
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
        Map<String, Object> eventBindings = new HashMap<String, Object>();

        for (EventSignal event : EventSignal.values()) {
            eventBindings.put(event.name(), event.name().toLowerCase());
        }

        bindings.putMember("signals", ProxyObject.fromMap(eventBindings));

        return context;
    }
}
