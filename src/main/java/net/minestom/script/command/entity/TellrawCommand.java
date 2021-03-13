package net.minestom.script.command.entity;

import net.minestom.script.command.RichCommand;
import net.minestom.server.chat.JsonMessage;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.Player;
import net.minestom.server.utils.entity.EntityFinder;

import java.util.List;

import static net.minestom.server.command.builder.arguments.ArgumentType.Component;
import static net.minestom.server.command.builder.arguments.ArgumentType.Entities;

public class TellrawCommand extends RichCommand {
    public TellrawCommand() {
        super("tellraw");

        setDefaultExecutor((sender, context) -> sender.sendMessage("Usage: /entity tellraw <targets> <message>"));

        addSyntax((sender, context) -> {
            EntityFinder entityFinder = context.get("targets");
            final JsonMessage message = context.get("message");
            final List<Entity> entities = entityFinder.find(sender);
            for (Entity entity : entities) {
                if (entity instanceof Player) {
                    final Player player = (Player) entity;
                    player.sendMessage(message);
                }
            }

            sender.sendMessage("Message sent!");
        }, Entities("targets").onlyPlayers(true), Component("message"));

    }
}
