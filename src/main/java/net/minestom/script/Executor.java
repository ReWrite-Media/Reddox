package net.minestom.script;

import net.minestom.script.object.PlayerProperty;
import net.minestom.script.object.Properties;
import net.minestom.server.MinecraftServer;
import net.minestom.server.command.builder.CommandData;
import net.minestom.server.command.builder.CommandResult;
import net.minestom.server.entity.Player;
import net.minestom.server.network.ConnectionManager;
import net.minestom.server.utils.validate.Check;
import org.graalvm.polyglot.Value;
import org.graalvm.polyglot.proxy.ProxyObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

/**
 * Layer between the server and the scripts.
 * <p>
 * Responsible for all interactions with Minecraft.
 */
public class Executor {

    private static final ConnectionManager CONNECTION_MANAGER = MinecraftServer.getConnectionManager();
    private final static List<Executor> EXECUTORS = new CopyOnWriteArrayList<>();

    private final Map<String, Consumer<Properties>> functionMap = new ConcurrentHashMap<>();
    private final Map<String, List<Consumer<Properties>>> listenerMap = new ConcurrentHashMap<>();

    protected void register() {
        EXECUTORS.add(this);
    }

    protected void unregister() {
        this.functionMap.clear();
        this.listenerMap.clear();
        EXECUTORS.remove(this);
    }

    public void registerFunction(@NotNull String name, @NotNull Consumer<Properties> consumer) {
        this.functionMap.put(name, consumer);
    }

    public void onSignal(@NotNull String signal, @NotNull Consumer<Properties> consumer) {
        List<Consumer<Properties>> listeners =
                listenerMap.computeIfAbsent(signal, s -> new CopyOnWriteArrayList<>());
        listeners.add(consumer);
    }

    public boolean function(@NotNull String function, @NotNull Properties properties) {
        boolean exists = false;
        for (Executor executor : EXECUTORS) {
            Consumer<Properties> consumer = functionMap.get(function);
            if (consumer != null) {
                exists = true;
                consumer.accept(properties);
            }
        }
        return exists;
    }

    public boolean signal(@NotNull String signal, @NotNull Properties properties) {
        boolean exists = false;
        for (Executor executor : EXECUTORS) {
            List<Consumer<Properties>> listeners = executor.listenerMap.get(signal);
            if (listeners != null && !listeners.isEmpty()) {
                exists = true;
                for (Consumer<Properties> callback : listeners) {
                    callback.accept(properties);
                }
            }
        }
        return exists;
    }

    @Nullable
    public ProxyObject run(@NotNull String command) {
        final CommandResult result = MinecraftServer.getCommandManager().executeServerCommand(command);
        return retrieveCommandData(result, command);
    }

    @Nullable
    public ProxyObject runAs(@NotNull Value playerValue, @NotNull String command) {
        Check.argCondition(!playerValue.isProxyObject(), "#runAs requires a player!");
        {
            ProxyObject proxyObject = playerValue.asProxyObject();
            Check.argCondition(!(proxyObject instanceof PlayerProperty), "#runAs requires a player!");
        }
        final PlayerProperty playerProperty = playerValue.asProxyObject();

        final UUID uuid = UUID.fromString(((Value) playerProperty.getMember("uuid")).asString());
        final Player player = CONNECTION_MANAGER.getPlayer(uuid);
        if (player == null)
            return null;

        final CommandResult result = MinecraftServer.getCommandManager().execute(player, command);
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
