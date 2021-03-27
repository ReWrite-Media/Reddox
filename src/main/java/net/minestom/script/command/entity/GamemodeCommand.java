package net.minestom.script.command.entity;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.script.command.RichCommand;
import net.minestom.server.command.CommandSender;
import net.minestom.server.command.builder.CommandContext;
import net.minestom.server.command.builder.arguments.ArgumentEnum;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.command.builder.exception.ArgumentSyntaxException;
import net.minestom.server.entity.GameMode;
import net.minestom.server.entity.Player;
import net.minestom.server.utils.entity.EntityFinder;

public class GamemodeCommand extends RichCommand {
    public GamemodeCommand() {
        super("gamemode");

        setDefaultExecutor(this::usage);

        var player = ArgumentType.Entity("player")
                .onlyPlayers(true)
                .singleEntity(true);

        var mode = ArgumentType.Enum("gamemode", GameMode.class)
                .setFormat(ArgumentEnum.Format.LOWER_CASED);

        setArgumentCallback(this::targetCallback, player);
        setArgumentCallback(this::gameModeCallback, mode);

        addSyntax(this::executeOnSelf, mode);
        addSyntax(this::executeOnOther, player, mode);
    }

    private void usage(CommandSender sender, CommandContext context) {
        sender.sendMessage(Component.text("Usage: /gamemode [player] <gamemode>"));
    }

    private void executeOnSelf(CommandSender sender, CommandContext context) {
        if (!sender.isPlayer()) {
            sender.sendMessage(Component.text("The command is only available for player", NamedTextColor.RED));
            return;
        }

        Player player = (Player) sender;

        GameMode gamemode = context.get("gamemode");
        assert gamemode != null; // mode is not supposed to be null, because gamemodeName will be valid
        player.setGameMode(gamemode);
        player.sendMessage(Component.text("You are now playing in " + gamemode.toString().toLowerCase(), NamedTextColor.GREEN));
    }

    private void executeOnOther(CommandSender sender, CommandContext context) {
        GameMode gamemode = context.get("gamemode");
        EntityFinder targetFinder = context.get("player");
        Player target = targetFinder.findFirstPlayer(sender);
        assert gamemode != null; // mode is not supposed to be null, because gamemodeName will be valid
        assert target != null;
        target.setGameMode(gamemode);
        target.sendMessage(Component.text("You are now playing in " + gamemode.toString().toLowerCase(), NamedTextColor.GREEN));
    }

    private void targetCallback(CommandSender sender, ArgumentSyntaxException exception) {
        sender.sendMessage(Component.text("'" + exception.getInput() + "' is not a valid player name.", NamedTextColor.RED));
    }

    private void gameModeCallback(CommandSender sender, ArgumentSyntaxException exception) {
        sender.sendMessage(Component.text("'" + exception.getInput() + "' is not a valid gamemode!", NamedTextColor.RED));
    }
}
