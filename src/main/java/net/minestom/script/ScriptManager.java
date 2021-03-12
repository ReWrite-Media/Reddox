package net.minestom.script;

import net.minestom.script.command.EntityCommand;
import net.minestom.script.command.FunctionCommand;
import net.minestom.script.command.ScriptCommand;
import net.minestom.script.command.WorldCommand;
import net.minestom.script.component.ScriptAPI;
import net.minestom.server.MinecraftServer;
import net.minestom.server.command.CommandManager;
import net.minestom.server.entity.Player;
import org.apache.commons.io.FilenameUtils;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Function;

public class ScriptManager {

    public static final ScriptAPI API = new ScriptAPI();

    public static final String SCRIPT_FOLDER = "scripts";

    private static final List<Script> SCRIPTS = new CopyOnWriteArrayList<>();

    private static final Map<String, String> EXTENSION_MAP = Map.of(
            "js", "js",
            "py", "python");

    private static volatile boolean loaded;

    private static Function<Player, Boolean> commandPermission = player -> true;

    /**
     * Loads and evaluate all scripts in the folder {@link #SCRIPT_FOLDER}.
     */
    public static void load() {
        if (loaded) {
            System.err.println("The script manager is already loaded!");
            return;
        }
        loaded = true;

        // Init events for signals
        {
            EventSignal.init(MinecraftServer.getGlobalEventHandler());
        }

        // Load commands
        {
            CommandManager commandManager = MinecraftServer.getCommandManager();
            commandManager.register(new ScriptCommand());
            commandManager.register(new FunctionCommand());
            commandManager.register(new WorldCommand());
            commandManager.register(new EntityCommand());
        }


        final File scriptFolder = new File(SCRIPT_FOLDER);

        if (!scriptFolder.exists()) {
            return; // No script folder
        }

        final File[] folderFiles = scriptFolder.listFiles();
        if (folderFiles == null) {
            System.err.println(scriptFolder + " is not a folder!");
            return;
        }

        for (File file : folderFiles) {
            final String extension = FilenameUtils.getExtension(file.getName());

            final String language = EXTENSION_MAP.get(extension);
            if (language == null) {
                // Invalid file extension
                System.err.println("Invalid file extension for " + file + ", ignored");
                continue;
            }

            final Executor executor = new Executor();
            Script script = new Script(file, language, executor);

            // Evaluate the script (start registering listeners)
            script.load();

            SCRIPTS.add(script);
        }
    }

    public static void shutdown() {
        // TODO
        //CONTEXT.close();
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

    @NotNull
    public static Function<Player, Boolean> getCommandPermission() {
        return commandPermission;
    }

    public static void setCommandPermission(@NotNull Function<Player, Boolean> commandPermission) {
        ScriptManager.commandPermission = commandPermission;
    }
}
