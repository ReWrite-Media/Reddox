package net.minestom.script;

import net.minestom.server.MinecraftServer;
import net.minestom.server.command.builder.CommandData;
import net.minestom.server.command.builder.CommandResult;
import org.graalvm.polyglot.proxy.ProxyObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

/**
 * Layer between the server and the scripts.
 * <p>
 * Responsible for all interactions with Minecraft.
 */
public class Executor {

    private final Map<String, Consumer<ScriptProperties>> functionMap = new ConcurrentHashMap<>();

    public void registerFunction(@NotNull String name, @NotNull Consumer<ScriptProperties> consumer) {
        this.functionMap.put(name, consumer);
    }

    public boolean runFunction(@NotNull String name, @Nullable ScriptProperties properties) {
        Consumer<ScriptProperties> consumer = functionMap.get(name);
        if (consumer == null)
            return false;
        consumer.accept(properties);
        return true;
    }

    @Nullable
    public ProxyObject run(@NotNull String command) {
        final CommandResult result = MinecraftServer.getCommandManager().executeServerCommand(command);
        return retrieveCommandData(result, command);
    }

    @Nullable
    private ProxyObject retrieveCommandData(@NotNull CommandResult result, @NotNull String input) {
        final CommandResult.Type type = result.getType();
        if (type != CommandResult.Type.SUCCESS) {
            System.err.println("ERROR COMMAND " + input + " with result " + type);
        }
        final CommandData commandData = result.getCommandData();
        if (commandData == null)
            return null;

        return ProxyObject.fromMap(commandData.getDataMap());
    }

}
