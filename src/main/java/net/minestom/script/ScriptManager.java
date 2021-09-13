package net.minestom.script;

import net.minestom.script.command.*;
import net.minestom.script.component.ScriptAPI;
import net.minestom.script.utils.ExceptionUtils;
import net.minestom.script.utils.FileUtils;
import net.minestom.script.utils.TypeScriptTranspiler;
import net.minestom.server.MinecraftServer;
import net.minestom.server.command.CommandManager;
import net.minestom.server.command.CommandSender;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityCreature;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.Player;
import net.minestom.server.instance.Instance;
import org.apache.commons.io.FilenameUtils;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Function;
import java.util.stream.Stream;

public final class ScriptManager {
    public static final ScriptAPI API = new ScriptAPI();

    public static final Path SCRIPT_FOLDER = Path.of("scripts");
    private static final String MAIN_SCRIPT = "main";

    private static final List<Script> SCRIPTS = new CopyOnWriteArrayList<>();

    // extension -> graalvm binding name
    private static final Map<String, String> EXTENSION_MAP = Map.of(
            "js", "js",
            "ts", "js", // -> typescript is transpiled to js
            "py", "python");
    // extension -> transpiler function
    private static final Map<String, Function<String, String>> TRANSPILER_MAP = Map.of(
            "ts", TypeScriptTranspiler::transpile);

    private static volatile boolean loaded;

    private static Function<CommandSender, Collection<Instance>> instanceSupplier = sender ->
            sender.isPlayer() ? Collections.singleton(sender.asPlayer().getInstance()) :
                    MinecraftServer.getInstanceManager().getInstances();
    private static Function<EntityType, Entity> entitySupplier = EntityCreature::new;
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
        EventSignal.init(MinecraftServer.getGlobalEventHandler());

        // Handle exception
        MinecraftServer.getExceptionManager().setExceptionHandler(ExceptionUtils::handleException);

        // Load commands
        {
            CommandManager commandManager = MinecraftServer.getCommandManager();
            commandManager.register(new ScriptCommand());
            commandManager.register(new SignalCommand());
            commandManager.register(new WorldCommand());
            commandManager.register(new EntityCommand());
            commandManager.register(new DisplayCommand());
            commandManager.register(new UtilsCommand());
        }

        // Load scripts
        loadScripts();
    }

    public static synchronized void reload() {
        shutdown();
        loadScripts();
    }

    public static synchronized void shutdown() {
        // Unload all current scripts
        for (Script script : getScripts()) {
            script.unload();
        }
        SCRIPTS.clear();
    }

    /**
     * Gets all the evaluated scripts.
     *
     * @return a list containing the scripts
     */
    public static @NotNull List<Script> getScripts() {
        return SCRIPTS;
    }

    public static @NotNull Function<CommandSender, Collection<Instance>> getInstanceSupplier() {
        return instanceSupplier;
    }

    public static void setInstanceSupplier(@NotNull Function<CommandSender, Collection<Instance>> instanceSupplier) {
        ScriptManager.instanceSupplier = instanceSupplier;
    }

    public static @NotNull Function<EntityType, Entity> getEntitySupplier() {
        return entitySupplier;
    }

    public static void setEntitySupplier(@NotNull Function<EntityType, Entity> entitySupplier) {
        ScriptManager.entitySupplier = entitySupplier;
    }

    public static @NotNull Function<Player, Boolean> getCommandPermission() {
        return commandPermission;
    }

    public static void setCommandPermission(@NotNull Function<Player, Boolean> commandPermission) {
        ScriptManager.commandPermission = commandPermission;
    }

    private static synchronized void loadScripts() {
        if (!Files.isDirectory(SCRIPT_FOLDER)) return;

        try (Stream<Path> walkStream = Files.list(SCRIPT_FOLDER)) {
            Iterator<Path> iterator = walkStream.iterator();
            while (iterator.hasNext()) {
                Path path = iterator.next();
                final String exposedName = path.getFileName().toString();
                if (Files.isDirectory(path)) {
                    // Find main file
                    path = findMainFile(path);
                }

                final String name = path.getFileName().toString();
                final String extension = FilenameUtils.getExtension(name);
                final String language = EXTENSION_MAP.get(extension);
                if (language == null) {
                    // Invalid file extension
                    System.err.println("Invalid file extension for " + path + ", ignored");
                    continue;
                }

                final GlobalExecutor globalExecutor = new GlobalExecutor();
                Script script;
                final Function<String, String> transpilerFunction = TRANSPILER_MAP.get(extension);
                if (transpilerFunction != null) {
                    // File content needs to be converted
                    final String source = transpilerFunction.apply(FileUtils.readFile(path));
                    script = Script.fromString(exposedName, language, source, globalExecutor);
                } else {
                    // Language is natively supported by GraalVM
                    script = Script.fromFile(exposedName, language, path, globalExecutor);
                }
                globalExecutor.script = script;
                SCRIPTS.add(script);
                // Evaluate the script (start registering listeners)
                script.load();
            }
        } catch (IOException e) {
            MinecraftServer.getExceptionManager().handleException(e);
        }
    }

    private static Path findMainFile(@NotNull Path directory) throws IOException {
        Iterator<Path> iterator = Files.walk(directory).iterator();
        while (iterator.hasNext()) {
            final Path path = iterator.next();
            final String name = FilenameUtils.removeExtension(path.getFileName().toString());
            if (name.equals(MAIN_SCRIPT)) {
                return path;
            }
        }
        throw new IOException("Invalid script folder");
    }
}
