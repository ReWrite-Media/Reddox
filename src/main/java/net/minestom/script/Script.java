package net.minestom.script;

import net.minestom.script.property.Properties;
import net.minestom.script.utils.NbtConversionUtils;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.HostAccess;
import org.graalvm.polyglot.Source;
import org.graalvm.polyglot.Value;
import org.graalvm.polyglot.proxy.ProxyObject;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Script {

    private final String name;
    private final String fileString;
    private final String language;
    private final Executor executor;

    private boolean loaded;
    private Context context;

    public Script(@NotNull String name, @NotNull String fileString, @NotNull String language, @NotNull Executor executor) {
        this.name = name;
        this.fileString = fileString;
        this.language = language;
        this.executor = executor;
    }

    public Script(@NotNull String name, @NotNull File file, @NotNull String language, @NotNull Executor executor) {
        this(name, readFile(file), language, executor);
    }

    public void load() {
        if (loaded)
            return;
        this.loaded = true;

        final Source source = Source.create(language, fileString);
        assert source != null;
        this.context = createContext(source.getLanguage(), executor);
        this.context.eval(source);
        this.executor.register();
    }

    public void unload() {
        if (!loaded)
            return;
        this.loaded = false;
        this.executor.unregister();
        this.context.close();
    }

    @NotNull
    public String getFileString() {
        return fileString;
    }

    @NotNull
    public String getName() {
        return name;
    }

    @NotNull
    public String getLanguage() {
        return language;
    }

    @NotNull
    public Executor getExecutor() {
        return executor;
    }

    public boolean isLoaded() {
        return loaded;
    }

    private static String readFile(File file) {
        String fileString = null;
        try {
            fileString = Files.readString(file.toPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fileString;
    }

    private static Context createContext(String language, Executor executor) {
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
                        map -> !map.containsKey(Properties.TYPE_MEMBER),
                        NbtConversionUtils::fromMap)
                .build();

        Context context = Context.newBuilder(language)
                .allowHostAccess(hostAccess).build();

        Value bindings = context.getBindings(language);

        // Command executor
        bindings.putMember("executor", executor);

        // Event Signals
        Map<String, Object> eventBindings = new HashMap<String, Object>();

        for (EventSignal event : EventSignal.values()) {
            eventBindings.put(event.name(), event.name().toLowerCase());
        }

        bindings.putMember("signals", ProxyObject.fromMap(eventBindings));

        return context;
    }
}
