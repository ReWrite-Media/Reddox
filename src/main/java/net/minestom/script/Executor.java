package net.minestom.script;

import net.minestom.server.MinecraftServer;
import net.minestom.server.command.builder.CommandData;
import net.minestom.server.command.builder.CommandResult;
import org.graalvm.polyglot.proxy.ProxyObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

/**
 * Layer between the server and the scripts.
 * <p>
 * Responsible for all interactions with Minecraft.
 */
public class Executor {

    private final Map<String, Consumer<ScriptProperties>> functionMap = new ConcurrentHashMap<>();
    private final Map<String, List<Consumer<ScriptProperties>>> listenerMap = new ConcurrentHashMap<>();

    public void registerFunction(@NotNull String name, @NotNull Consumer<ScriptProperties> consumer) {
        this.functionMap.put(name, consumer);
    }

    public void registerListener(@NotNull String signal, @NotNull Consumer<ScriptProperties> consumer) {
        List<Consumer<ScriptProperties>> listeners = listenerMap.computeIfAbsent(signal, s -> new CopyOnWriteArrayList<>());
        listeners.add(consumer);
    }

    public boolean function(@NotNull String function, @NotNull ScriptProperties properties) {
        Consumer<ScriptProperties> consumer = functionMap.get(function);
        if (consumer == null)
            return false;
        consumer.accept(properties);
        return true;
    }

    public boolean signal(@NotNull String signal, @NotNull ScriptProperties properties) {
        List<Consumer<ScriptProperties>> listeners = listenerMap.get(signal);
        if (listeners == null || listeners.isEmpty())
            return false;
        for (Consumer<ScriptProperties> callback : listeners) {
            callback.accept(properties);
        }
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
