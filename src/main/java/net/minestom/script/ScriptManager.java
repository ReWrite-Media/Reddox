package net.minestom.script;

import net.minestom.script.command.FunctionCommand;
import net.minestom.server.MinecraftServer;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.HostAccess;
import org.graalvm.polyglot.Source;
import org.graalvm.polyglot.Value;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ScriptManager {

    public static final String SCRIPT_FOLDER = "scripts";

    public static final Executor EXECUTOR = new Executor();

    private static final Context CONTEXT = Context.newBuilder("js")
            .allowHostAccess(HostAccess.ALL).build();
    private static final List<Script> SCRIPTS = new CopyOnWriteArrayList<>();

    /**
     * Loads and evaluate all scripts in the folder {@link #SCRIPT_FOLDER}.
     */
    public static void load() {

        // Load commands
        {
            MinecraftServer.getCommandManager().register(new FunctionCommand());
        }


        final File scriptFolder = new File(SCRIPT_FOLDER);

        if (!scriptFolder.exists()) {
            return; // No script folder
        }

        Value bindings = CONTEXT.getBindings("js");
        bindings.putMember("executor", EXECUTOR);

        for (File file : scriptFolder.listFiles()) {
            System.out.println("LOAD " + file);
            try {
                String fileString = Files.readString(file.toPath());

                Source source = Source.create("js", fileString);
                Script script = new Script(source);

                // Evaluate the script
                script.eval(CONTEXT);

                SCRIPTS.add(script);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void shutdown() {
        CONTEXT.close();
    }

    /**
     * Gets all the evaluated python scripts.
     *
     * @return a list containing the scripts
     */
    @NotNull
    public static List<Script> getScripts() {
        return SCRIPTS;
    }

}
