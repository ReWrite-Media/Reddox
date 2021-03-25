package net.minestom.script;

import net.minestom.script.command.*;
import net.minestom.script.component.ScriptAPI;
import net.minestom.server.MinecraftServer;
import net.minestom.server.command.CommandManager;
import net.minestom.server.entity.Player;
import org.apache.commons.io.FilenameUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Function;

public class ScriptManager {

    public static final ScriptAPI API = new ScriptAPI();

    public static final String SCRIPT_FOLDER = "scripts";
    public static final String MAIN_SCRIPT = "main";

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
            commandManager.register(new UtilsCommand());
        }

        // Load scripts
        loadScripts();
    }

    public static synchronized void reload() {
        // Unload all current scripts
        {
            for (Script script : getScripts()) {
                script.unload();
            }
            SCRIPTS.clear();
        }

        // Re-load new scripts
        loadScripts();
    }

    /**
     * Gets all the evaluated scripts.
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

    private static synchronized void loadScripts() {
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
            final String name = file.getName();
            if (file.isDirectory()) {
                // Find main file
                file = findMainFile(file);
                if (file == null) {
                    System.err.println("Directory " +
                            file + " is invalid, you need a script with the name " +
                            MAIN_SCRIPT);
                    continue;
                }
            }
            final String extension = FilenameUtils.getExtension(file.getName());

            final String language = EXTENSION_MAP.get(extension);
            if (language == null) {
                // Invalid file extension
                System.err.println("Invalid file extension for " + file + ", ignored");
                continue;
            }

            final Executor executor = new Executor();
            Script script = new Script(name, file, language, executor);

            SCRIPTS.add(script);
            // Evaluate the script (start registering listeners)
            script.load();
        }
    }

    @Nullable
    private static File findMainFile(@NotNull File directory) {
        final File[] folderFiles = directory.listFiles();
        if (folderFiles == null) {
            return null;
        }

        for (File file : folderFiles) {
            final String name = FilenameUtils.removeExtension(file.getName());
            if (name.equals(MAIN_SCRIPT)) {
                return file;
            }
        }
        return null;
    }
}
