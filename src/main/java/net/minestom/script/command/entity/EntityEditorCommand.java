package net.minestom.script.command.entity;

import net.minestom.script.command.RichCommand;
import net.minestom.script.property.Properties;
import net.minestom.script.utils.ArgumentUtils;
import net.minestom.server.MinecraftServer;
import net.minestom.server.command.builder.CommandContext;
import net.minestom.server.command.builder.CommandData;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityCreature;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.pathfinding.NavigableEntity;
import net.minestom.server.utils.Vector;
import net.minestom.server.utils.entity.EntityFinder;
import net.minestom.server.utils.location.RelativeVec;

import java.util.List;

import static net.minestom.server.command.builder.arguments.ArgumentType.*;

public class EntityEditorCommand extends RichCommand {

    public EntityEditorCommand() {
        super("editor");

        setDefaultExecutor((sender, context) -> {
            sender.sendMessage("Usage: /editor <create/edit/remove>");
        });

        var entityArgument = Entity("entity").singleEntity(true);

        // /editor init
        addSyntax((sender, context) -> {
            CommandData commandData = new CommandData();
            context.setReturnData(commandData);

            final EntityType entityType = context.get("entity_type");
            final RelativeVec relativeVec = context.get("spawn_position");

            final Vector spawnPosition = relativeVec.from(sender.isPlayer() ? sender.asPlayer() : null);

            EntityCreature creature = new EntityCreature(entityType);

            MinecraftServer.getInstanceManager().getInstances().forEach(instance -> {
                // TODO select instance
                creature.setInstance(instance, spawnPosition.toPosition());
            });

            commandData.set("success", true);
            commandData.set("entity", Properties.fromEntity(creature));
            sender.sendMessage("Entity created successfully, uuid: " + creature.getUuid());

        }, Literal("create"), EntityType("entity_type"), RelativeVec3("spawn_position"));

        // /editor edit
        addSyntax((sender, context) -> {
            final EntityFinder entityFinder = context.get(entityArgument);
            final Entity entity = entityFinder.findFirstEntity(sender);

            if (entity == null) {
                sender.sendMessage("Entity not found");
                return;
            }

            List<CommandContext> properties = context.get("properties");
            for (CommandContext property : properties) {
                if (property.has("position")) {
                    final RelativeVec relativeVec = property.get("position_value");
                    final Vector vector = ArgumentUtils.from(sender, relativeVec);

                    entity.teleport(vector.toPosition());
                }

                if (property.has("path")) {
                    final RelativeVec relativeVec = property.get("path_value");
                    final Vector vector = ArgumentUtils.from(sender, relativeVec);

                    if (entity instanceof NavigableEntity) {
                        ((NavigableEntity) entity).getNavigator().setPathTo(vector.toPosition());
                    }
                }
            }

            sender.sendMessage("Entity edited!");

        }, Literal("edit"), entityArgument, Loop("properties",
                Group("position_group", Literal("position"), RelativeVec3("position_value")),
                Group("path_group", Literal("path"), RelativeVec3("path_value"))));

        // /editor remove
        addSyntax((sender, context) -> {
            final EntityFinder entityFinder = context.get(entityArgument);
            final Entity entity = entityFinder.findFirstEntity(sender);
            if (entity != null) {
                entity.remove();
                sender.sendMessage("Entity removed");
            } else {
                sender.sendMessage("Entity not found");
            }
        }, Literal("remove"), entityArgument);
    }
}
