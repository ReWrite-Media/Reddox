package net.minestom.script.command.entity;

import net.minestom.script.command.ScriptCommand;
import net.minestom.server.command.CommandSender;
import net.minestom.server.command.builder.Arguments;
import net.minestom.server.entity.GameMode;
import net.minestom.server.entity.Player;
import net.minestom.server.utils.entity.EntityFinder;

import static net.minestom.server.command.builder.arguments.ArgumentType.Entities;
import static net.minestom.server.command.builder.arguments.ArgumentType.Word;

public class GamemodeCommand extends ScriptCommand {
    public GamemodeCommand() {
        super("gm");

        setDefaultExecutor(this::usage);

        final var gamemodeArgument = Word("gamemode")
                .from("adventure", "creative", "spectator", "survival");

        addSyntax((sender, args) -> {
            if (!sender.isPlayer()) {
                usage(sender, args);
                return;
            }
            final Player player = sender.asPlayer();
            final GameMode gameMode = GameMode.valueOf(args.get("gamemode").toString().toUpperCase());

            player.setGameMode(gameMode);
            player.sendMessage("Gamemode changed successfully!");
        }, gamemodeArgument);

        addSyntax((sender, args) -> {
            final GameMode gameMode = GameMode.valueOf(args.get("gamemode").toString().toUpperCase());
            final EntityFinder entityFinder = args.get("target");
            final Player target = entityFinder.findFirstPlayer(sender);
            if (target != null) {
                target.setGameMode(gameMode);
                sender.sendMessage("Gamemode changed successfully!");
            } else {
                sender.sendMessage("Unknown player");
            }

        }, gamemodeArgument, Entities("target").singleEntity(true).onlyPlayers(true));
    }

    private void usage(CommandSender sender, Arguments args) {
        sender.sendMessage("Usage: /entity gamemode (adventure|creative|spectator|survival) [<target>]");
    }
}
