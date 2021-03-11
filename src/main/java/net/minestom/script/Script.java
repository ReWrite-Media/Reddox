package net.minestom.script;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.HostAccess;
import org.graalvm.polyglot.Source;
import org.graalvm.polyglot.Value;
import org.jetbrains.annotations.NotNull;

import java.io.File;

public class Script {

    private final File file;
    private final Source source;
    private final Executor executor;

    private boolean loaded;
    private Context context;
    private Value script;

    public Script(@NotNull File file, @NotNull Source source, @NotNull Executor executor) {
        this.file = file;
        this.source = source;
        this.executor = executor;
    }

    public void load() {
        this.loaded = true;
        this.context = createContext(source.getLanguage(), executor);
        this.script = context.eval(source);
        this.executor.register();
    }

    public void unload() {
        this.loaded = false;
        this.executor.unregister();
        this.context.close();
    }

    @NotNull
    public File getFile() {
        return file;
    }

    @NotNull
    public Source getSource() {
        return source;
    }

    @NotNull
    public Executor getExecutor() {
        return executor;
    }

    public boolean isLoaded() {
        return loaded;
    }

    private static Context createContext(String language, Executor executor) {
        Context context = Context.newBuilder(language)
                .allowHostAccess(HostAccess.ALL).build();
        Value bindings = context.getBindings(language);
        bindings.putMember("executor", executor);

        return context;
    }
}
