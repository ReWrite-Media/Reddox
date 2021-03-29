package net.minestom.script;

import net.minestom.script.command.RichCommand;
import net.minestom.script.property.PlayerProperty;
import net.minestom.script.property.Properties;
import net.minestom.script.utils.CommandUtils;
import net.minestom.server.MinecraftServer;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.CommandData;
import net.minestom.server.command.builder.CommandResult;
import net.minestom.server.command.builder.arguments.ArgumentType;
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

    private final Map<String, List<SignalCallback>> signalMap = new ConcurrentHashMap<>();
    private final Map<String, Command> commandMap = new ConcurrentHashMap<>();

    protected void register() {
        EXECUTORS.add(this);
    }

    protected synchronized void unregister() {
        this.signalMap.clear();

        this.commandMap.forEach((s, command) ->
                MinecraftServer.getCommandManager().unregister(command));
        this.commandMap.clear();

        EXECUTORS.remove(this);
    }

    public void onSignal(@NotNull String signal, @NotNull SignalCallback callback) {
        List<SignalCallback> listeners =
                signalMap.computeIfAbsent(signal.toLowerCase(), s -> new CopyOnWriteArrayList<>());
        listeners.add(callback);
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

    public synchronized void registerCommand(@NotNull String format, @NotNull CommandCallback callback) {
        String commandName;
        if (format.contains(StringUtils.SPACE)) {
            final int index = format.indexOf(StringUtils.SPACE);
            commandName = format.substring(0, index);
            format = format.substring(index + 1);
        } else {
            // No argument
            commandName = format;
            format = "";
        }

        // Create command
        Command command = new RichCommand(commandName);
        command.addSyntax((sender, context) -> {
            if (!sender.isPlayer()) {
                // TODO console support
                System.err.println("Currently only players can use script commands");
                return;
            }
            PlayerProperty playerProperty = new PlayerProperty(sender.asPlayer());
            Properties properties = new Properties();
            context.getMap().forEach(properties::putMember);

            callback.accept(playerProperty, properties);
        }, ArgumentType.generate(format));

        this.commandMap.put(commandName, command);
        MinecraftServer.getCommandManager().register(command);
        CommandUtils.updateCommands();
    }

    @Nullable
    public ProxyObject run(@NotNull Object... inputs) {
        final String command = inputToString(inputs);
        final CommandResult result = MinecraftServer.getCommandManager().executeServerCommand(command);
        return retrieveCommandData(result, command);
    }

    @Nullable
    public ProxyObject runAs(@NotNull Value playerValue, @NotNull Object... inputs) {
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

        final String command = inputToString(inputs);
        final CommandResult result = MinecraftServer.getCommandManager().execute(player, command);
        return retrieveCommandData(result, command);
    }

    @NotNull
    public CommandFunction make(@NotNull String string) {
        return make(string, CommandMapper.DEFAULT);
    }

    @NotNull
    public CommandFunction make(@NotNull String string, @NotNull CommandMapper mapper) {
        return args -> {
            final String input = MessageFormat.format(string, args);
            return mapper.map(run(input));
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

        // Convert all members to polyglot 'Value'
        Properties properties = new Properties();
        commandData.getDataMap().forEach(properties::putMember);
        return properties;
    }

    private static String inputToString(Object... inputs) {
        return Arrays.stream(inputs)
                .map(Object::toString)
                .collect(Collectors.joining(StringUtils.SPACE));
    }

}
