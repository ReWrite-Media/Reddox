package net.minestom.script.utils;

import net.minestom.script.property.Properties;
import net.minestom.server.MinecraftServer;
import net.minestom.server.command.CommandManager;
import net.minestom.server.command.builder.CommandData;
import net.minestom.server.command.builder.CommandResult;
import net.minestom.server.network.ConnectionManager;
import org.graalvm.polyglot.proxy.ProxyObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CommandUtils {

    private static final CommandManager COMMAND_MANAGER = MinecraftServer.getCommandManager();
    private static final ConnectionManager CONNECTION_MANAGER = MinecraftServer.getConnectionManager();

    public static void updateCommands() {
        CONNECTION_MANAGER.getOnlinePlayers().forEach(player -> {
            final var playerConnection = player.getPlayerConnection();
            final var commandPacket = COMMAND_MANAGER.createDeclareCommandsPacket(player);
            playerConnection.sendPacket(commandPacket);
        });
    }

    @Nullable
    public static ProxyObject retrieveCommandData(@NotNull CommandResult result, @NotNull String input) {
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

}
