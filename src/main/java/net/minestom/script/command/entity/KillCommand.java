package net.minestom.script.command.entity;

import net.kyori.adventure.text.Component;
import net.minestom.script.command.RichCommand;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.LivingEntity;
import net.minestom.server.entity.Player;
import net.minestom.server.utils.entity.EntityFinder;

import java.util.List;

public class KillCommand extends RichCommand {
    public KillCommand() {
        super("kill");

        setDefaultExecutor((sender, context) -> {
            if (!sender.isPlayer()) {
                sender.sendMessage(Component.text("Usage: /kill <targets>"));
                return;
            }
            final Player player = sender.asPlayer();
            player.kill();
        });

        addSyntax((sender, context) -> {
            EntityFinder entityFinder = context.get("targets");
            final List<Entity> entities = entityFinder.find(sender);
            for (Entity entity : entities) {
                if (entity instanceof LivingEntity) {
                    ((LivingEntity) entity).kill();
                } else {
                    entity.remove();
                }
            }
            sender.sendMessage(Component.text("Entities removed!"));
        }, ArgumentType.Entity("targets").setDefaultValue(EntityFinder::new));
    }
}
