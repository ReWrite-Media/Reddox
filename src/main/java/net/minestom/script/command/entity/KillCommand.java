package net.minestom.script.command.entity;

import net.minestom.script.command.ScriptCommand;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.LivingEntity;
import net.minestom.server.entity.Player;
import net.minestom.server.utils.entity.EntityFinder;

import java.util.List;

public class KillCommand extends ScriptCommand {
    public KillCommand() {
        super("kill");

        setDefaultExecutor((sender, args) -> {
            if (!sender.isPlayer()) {
                sender.sendMessage("Usage: /kill <targets>");
                return;
            }
            final Player player = sender.asPlayer();
            player.kill();
        });

        addSyntax((sender, args) -> {
            EntityFinder entityFinder = args.get("targets");
            final List<Entity> entities = entityFinder.find(sender);
            for (Entity entity : entities) {
                if (entity instanceof LivingEntity) {
                    ((LivingEntity) entity).kill();
                } else {
                    entity.remove();
                }
            }
            sender.sendMessage("Entities removed!");
        }, ArgumentType.Entities("targets").setDefaultValue(new EntityFinder()));
    }
}
