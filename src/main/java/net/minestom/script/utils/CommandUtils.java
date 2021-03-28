package net.minestom.script.utils;

import net.minestom.server.MinecraftServer;
import net.minestom.server.command.CommandManager;
import net.minestom.server.network.ConnectionManager;

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

}
