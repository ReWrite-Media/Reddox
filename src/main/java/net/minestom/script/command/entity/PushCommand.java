package net.minestom.script.command.entity;

import net.minestom.script.command.RichCommand;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.utils.entity.EntityFinder;
import net.minestom.server.utils.location.RelativeVec;

import static net.minestom.server.command.builder.arguments.ArgumentType.*;


public class PushCommand extends RichCommand {
    public PushCommand() {
        super("push");

        setDefaultExecutor((sender, context) -> sender.sendMessage("Usage: /push <targets> <type> <...>"));

        // Push from a second position
        addSyntax((sender, context) -> {
            EntityFinder entityFinder = context.get("targets");
            final var entities = entityFinder.find(sender);
            RelativeVec relativeVec = context.get("position");

            for (var entity : entities) {
                final Vec vector = relativeVec.from(entity);
                entity.setVelocity(vector.sub(entity.getPosition()));
            }

            sender.sendMessage("Pushed");

        }, Entity("targets"), Literal("to"), RelativeVec3("position"));

        addSyntax((sender, context) -> {
            EntityFinder entityFinder = context.get("targets");
            final var entities = entityFinder.find(sender);
            RelativeVec relativeVec = context.get("position");

            for (var entity : entities) {
                final Vec vector = relativeVec.from(entity);
                entity.setVelocity(entity.getPosition().direction().normalize().mul(vector));
            }

            sender.sendMessage("Pushed forward");

        }, Entity("targets"), Literal("forward"), RelativeVec3("position"));
    }
}
