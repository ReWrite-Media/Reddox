package net.minestom.script.command.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.script.command.RichCommand;
import net.minestom.server.MinecraftServer;
import net.minestom.server.command.CommandManager;
import net.minestom.server.command.builder.CommandResult;
import net.minestom.server.command.builder.ParsedCommand;
import net.minestom.server.network.ConnectionManager;
import net.minestom.server.network.player.PlayerConnection;
import org.jetbrains.annotations.NotNull;

import java.lang.String;

import static net.minestom.server.command.builder.arguments.ArgumentType.*;

public class AliasCommand extends RichCommand {
    public AliasCommand() {
        super("alias");

        setDefaultExecutor((sender, context) ->
                sender.sendMessage(Component.text("Usage: /alias create <name> <command>")));

        final CommandManager commandManager = MinecraftServer.getCommandManager();
        final ConnectionManager connectionManager = MinecraftServer.getConnectionManager();

        addSyntax((sender, context) -> {
            final String name = context.get("name");

            CommandResult commandResult = context.get("alias");
            if (commandResult.getParsedCommand() == null) {
                sender.sendMessage(Component.text("Invalid command", NamedTextColor.RED));
                return;
            }

            final String shortcut = commandResult.getInput();

            AliasedCommand aliasedCommand = new AliasedCommand(name, shortcut);
            commandManager.register(aliasedCommand);
            connectionManager.getOnlinePlayers().forEach(player -> {
                final PlayerConnection connection = player.getPlayerConnection();
                connection.sendPacket(commandManager.createDeclareCommandsPacket(player));
            });

            sender.sendMessage(Component.text("Alias created successfully!", NamedTextColor.GREEN));
        }, Literal("create"), Word("name"), Command("alias"));
    }


    private static class AliasedCommand extends RichCommand {

        public AliasedCommand(@NotNull String name, @NotNull String shortcut) {
            super(name);

            addSyntax((sender, context) -> {
                CommandResult commandResult = context.get("cmd");
                ParsedCommand parsedCommand = commandResult.getParsedCommand();
                if (parsedCommand != null) {
                    parsedCommand.execute(sender);
                } else {
                    sender.sendMessage(Component.text("Alias is incorrect", NamedTextColor.RED));
                }

            }, Command("cmd").setShortcut(shortcut));
        }
    }

}
