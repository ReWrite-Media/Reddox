package net.minestom.script.command.entity;

import net.minestom.script.command.RichCommand;
import net.minestom.server.MinecraftServer;
import net.minestom.server.command.builder.CommandContext;
import net.minestom.server.command.builder.CommandData;
import net.minestom.server.entity.EntityCreature;
import net.minestom.server.entity.EntityType;
import net.minestom.server.utils.Vector;
import net.minestom.server.utils.location.RelativeVec;

import java.lang.String;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static net.minestom.server.command.builder.arguments.ArgumentType.*;

public class EntityEditorCommand extends RichCommand {

    private static final Map<String, EntityCreature> CREATURES_MAP = new ConcurrentHashMap<>();

    public EntityEditorCommand() {
        super("editor");

        setDefaultExecutor((sender, context) -> {
            System.out.println("TODO USAGE");
        });

        // /entity-studio init
        addSyntax((sender, context) -> {
            CommandData commandData = new CommandData();
            context.setReturnData(commandData);

            final String identifier = context.get("identifier");

            if (CREATURES_MAP.containsKey(identifier)) {
                sender.sendMessage("PROJECTOR WITH THIS ID ALREADY EXISTS");
                commandData.set("success", false);
                return;
            }

            final EntityType entityType = context.get("entity_type");
            final RelativeVec relativeVec = context.get("spawn_position");

            final Vector spawnPosition = relativeVec.from(sender.isPlayer() ? sender.asPlayer() : null);

            EntityCreature creature = new EntityCreature(entityType);

            MinecraftServer.getInstanceManager().getInstances().forEach(instance -> {
                // TODO select instance
                creature.setInstance(instance, spawnPosition.toPosition());
            });

            CREATURES_MAP.put(identifier, creature);

            commandData.set("success", true);
            sender.sendMessage("ENTITY CREATED SUCCESSFULLY: " + identifier);

        }, Literal("init"), Word("identifier"), EntityType("entity_type"), RelativeVec3("spawn_position"));

        // /entity-studio edit
        addSyntax((sender, context) -> {
            final String identifier = context.get("identifier");
            EntityCreature creature = CREATURES_MAP.get(identifier);
            System.out.println("ENTITY EDIT:" + creature);

            List<CommandContext> properties = context.get("properties");
            for (CommandContext property : properties) {
                if (property.has("position")) {
                    final RelativeVec relativeVec = property.get("position_value");
                    final Vector vector = relativeVec.from(sender.isPlayer() ? sender.asPlayer() : null);

                    creature.teleport(vector.toPosition());
                    sender.sendMessage("TELEPORT");
                }

                if (property.has("path")) {
                    final RelativeVec relativeVec = property.get("path_value");
                    final Vector vector = relativeVec.from(sender.isPlayer() ? sender.asPlayer() : null);

                    creature.getNavigator().setPathTo(vector.toPosition());
                    sender.sendMessage("PATH");
                }
            }

        }, Literal("edit"), Word("identifier"), Loop("properties",
                Group("position_group", Literal("position"), RelativeVec3("position_value")),
                Group("path_group", Literal("path"), RelativeVec3("path_value"))));

        // /entity-studio remove
        addSyntax((sender, context) -> {
            final String identifier = context.get("identifier");
            EntityCreature creature = CREATURES_MAP.remove(identifier);
            if (creature != null) {
                creature.remove();
                sender.sendMessage("Entity removed");
            } else {
                sender.sendMessage("Entity not found");
            }
        }, Literal("remove"), Word("identifier"));
    }
}
