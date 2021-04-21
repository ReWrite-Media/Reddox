package net.minestom.script.command.entity;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.script.command.RichCommand;
import net.minestom.script.property.Properties;
import net.minestom.script.utils.ArgumentUtils;
import net.minestom.server.command.builder.CommandContext;
import net.minestom.server.command.builder.CommandData;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityCreature;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.pathfinding.NavigableEntity;
import net.minestom.server.utils.Vector;
import net.minestom.server.utils.entity.EntityFinder;
import net.minestom.server.utils.location.RelativeVec;

import java.lang.String;
import java.util.List;

import static net.minestom.server.command.builder.arguments.ArgumentType.*;

public class EntityEditorCommand extends RichCommand {

    public EntityEditorCommand() {
        super("editor");

        setDefaultExecutor((sender, context) -> {
            sender.sendMessage(Component.text("Usage: /editor <create/edit/remove>"));
        });

        var entityArgument = Entity("entity").singleEntity(true);

        // /editor create
        addSyntax((sender, context) -> {
            CommandData commandData = new CommandData();
            context.setReturnData(commandData);

            final EntityType entityType = context.get("entity_type");
            final RelativeVec relativeVec = context.get("spawn_position");

            final Vector spawnPosition = ArgumentUtils.from(sender, relativeVec);

            EntityCreature creature = new EntityCreature(entityType);
            processInstances(sender, instance -> creature.setInstance(instance, spawnPosition.toPosition()));

            commandData.set("success", true);
            commandData.set("entity", Properties.fromEntity(creature));

            final String uuid = creature.getUuid().toString();
            Component component = Component.text("Entity created:")
                    .append(Component.space())
                    .append(Component.text(uuid)
                            .color(NamedTextColor.GRAY)
                            .hoverEvent(HoverEvent.showText(Component.text("Click to copy", NamedTextColor.GRAY)))
                            .clickEvent(ClickEvent.copyToClipboard(uuid)));

            sender.sendMessage(component);

        }, Literal("create"), EntityType("entity_type"), RelativeVec3("spawn_position"));

        // /editor edit
        addSyntax((sender, context) -> {
            final EntityFinder entityFinder = context.get(entityArgument);
            final Entity entity = entityFinder.findFirstEntity(sender);

            if (entity == null) {
                sender.sendMessage(Component.text("Entity not found", NamedTextColor.RED));
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

            sender.sendMessage(Component.text("Entity edited!", NamedTextColor.GREEN));

        }, Literal("edit"), entityArgument, Loop("properties",
                Group("position_group", Literal("position"), RelativeVec3("position_value")),
                Group("path_group", Literal("path"), RelativeVec3("path_value"))));

        // /editor remove
        addSyntax((sender, context) -> {
            final EntityFinder entityFinder = context.get(entityArgument);
            final Entity entity = entityFinder.findFirstEntity(sender);
            if (entity != null) {
                entity.remove();
                sender.sendMessage(Component.text("Entity removed", NamedTextColor.GREEN));
            } else {
                sender.sendMessage(Component.text("Entity not found", NamedTextColor.RED));
            }
        }, Literal("remove"), entityArgument);
    }
}
