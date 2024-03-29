package net.minestom.script.command.entity;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.script.command.RichCommand;
import net.minestom.script.utils.EntityUtils;
import net.minestom.server.command.builder.CommandData;
import net.minestom.server.entity.Entity;
import net.minestom.server.utils.entity.EntityFinder;

import java.util.List;

import static net.minestom.server.command.builder.arguments.ArgumentType.Entity;

public class EntityQueryCommand extends RichCommand {
    public EntityQueryCommand() {
        super("query");

        addSyntax((sender, context) -> {
            EntityFinder entityFinder = context.get("targets");
            List<Entity> entities = entityFinder.find(sender);

            Component component = Component.text("Entities(" + entities.size() + "):", NamedTextColor.WHITE);
            for (Entity entity : entities) {
                component = component.append(Component.newline())
                        .append(EntityUtils.getDisplayComponent(entity));
            }
            sender.sendMessage(component);

            CommandData commandData = new CommandData();
            commandData.set("entities", entities);
            context.setReturnData(commandData);

        }, Entity("targets"));
    }
}
