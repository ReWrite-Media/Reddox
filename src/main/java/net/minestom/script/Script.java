package net.minestom.script;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.HostAccess;
import org.graalvm.polyglot.Source;
import org.graalvm.polyglot.Value;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class Script {

    private final File file;
    private final String language;
    private final Executor executor;

    private boolean loaded;
    private Context context;

    public Script(@NotNull File file, @NotNull String language, @NotNull Executor executor) {
        this.file = file;
        this.language = language;
        this.executor = executor;

    }

    public void load() {
        if (loaded)
            return;
        this.loaded = true;

        final Source source = createSource(file, language);
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
    public File getFile() {
        return file;
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

    private static Source createSource(File file, String language) {
        try {
            final String fileString = Files.readString(file.toPath());
            return Source.create(language, fileString);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static Context createContext(String language, Executor executor) {
        Context context = Context.newBuilder(language)
                .allowHostAccess(HostAccess.ALL).build();
        Value bindings = context.getBindings(language);
        bindings.putMember("executor", executor);

        return context;
    }
}
