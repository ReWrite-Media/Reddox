package net.minestom.script;

import net.minestom.script.property.PlayerProperty;
import net.minestom.script.property.Properties;
import net.minestom.server.MinecraftServer;
import net.minestom.server.command.builder.CommandData;
import net.minestom.server.command.builder.CommandResult;
import net.minestom.server.entity.Player;
import net.minestom.server.network.ConnectionManager;
import net.minestom.server.utils.validate.Check;
import org.apache.commons.lang3.StringUtils;
import org.graalvm.polyglot.Value;
import org.graalvm.polyglot.proxy.ProxyObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.MessageFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

/**
 * Layer between the server and the scripts.
 * <p>
 * Responsible for all interactions with Minecraft.
 */
public class Executor {

    private static final ConnectionManager CONNECTION_MANAGER = MinecraftServer.getConnectionManager();
    private final static List<Executor> EXECUTORS = new CopyOnWriteArrayList<>();

    private final Map<String, FunctionCallback> functionMap = new ConcurrentHashMap<>();
    private final Map<String, List<SignalCallback>> signalMap = new ConcurrentHashMap<>();

    protected void register() {
        EXECUTORS.add(this);
    }

    protected void unregister() {
        this.functionMap.clear();
        this.signalMap.clear();
        EXECUTORS.remove(this);
    }

    public void registerFunction(@NotNull String name, @NotNull FunctionCallback callback) {
        // TODO prevent multiple scripts from registering the same function
        this.functionMap.put(name, callback);
    }

    public void onSignal(@NotNull String signal, @NotNull SignalCallback callback) {
        List<SignalCallback> listeners =
                signalMap.computeIfAbsent(signal.toLowerCase(), s -> new CopyOnWriteArrayList<>());
        listeners.add(callback);
    }

    public boolean function(@NotNull String function, @NotNull Properties properties) {
        for (Executor executor : EXECUTORS) {
            FunctionCallback callback = executor.functionMap.get(function);
            if (callback != null) {
                callback.accept(properties);
                return true;
            }
        }
        return false;
    }

    @NotNull
    public ProxyObject signal(@NotNull String signal, @NotNull Properties properties) {
        ProxyObject result = ProxyObject.fromMap(new HashMap<>());
        for (Executor executor : EXECUTORS) {
            List<SignalCallback> listeners = executor.signalMap.get(signal.toLowerCase());
            if (listeners != null && !listeners.isEmpty()) {
                for (SignalCallback callback : listeners) {
                    callback.accept(properties, result);
                }
            }
        }
        return result;
    }

    @Nullable
    public ProxyObject run(@NotNull Object... input) {
        final String command = Arrays.stream(input)
                .map(Object::toString)
                .collect(Collectors.joining(StringUtils.SPACE));
        final CommandResult result = MinecraftServer.getCommandManager().executeServerCommand(command);
        return retrieveCommandData(result, command);
    }

    @Nullable
    public ProxyObject runAs(@NotNull Value playerValue, @NotNull Object... input) {
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

        final String command = Arrays.stream(input)
                .map(Object::toString)
                .collect(Collectors.joining(StringUtils.SPACE));
        final CommandResult result = MinecraftServer.getCommandManager().execute(player, command);
        return retrieveCommandData(result, command);
    }

    @NotNull
    public CommandFunction make(@NotNull String string) {
        return args -> {
            final String input = MessageFormat.format(string, args);
            return run(input);
        };
    }

    @Nullable
    private ProxyObject retrieveCommandData(@NotNull CommandResult result, @NotNull String input) {
        final CommandResult.Type type = result.getType();
        if (type != CommandResult.Type.SUCCESS) {
            System.err.println("ERROR COMMAND '" + input + "' with result: " + type);
        }
        final CommandData commandData = result.getCommandData();
        if (commandData == null)
            return null;

        return ProxyObject.fromMap(commandData.getDataMap());
    }

}
