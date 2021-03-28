package net.minestom.script.command.display;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.minestom.script.command.RichCommand;
import net.minestom.script.command.arguments.ArgumentFlexibleComponent;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.entity.Entity;
import net.minestom.server.utils.entity.EntityFinder;

import java.util.List;

public class ActionBarCommand extends RichCommand {
    public ActionBarCommand() {
        super("actionbar");

        setDefaultExecutor((sender, context) ->
                sender.sendMessage(Component.text("Usage: /display actionbar <targets> <message>")));

        addSyntax((sender, context) -> {
            EntityFinder entityFinder = context.get("targets");
            final Component component = context.get("component");
            final List<Entity> entities = entityFinder.find(sender);
            entities.stream()
                    .filter(Audience.class::isInstance)
                    .map(Audience.class::cast)
                    .forEach(audience -> audience.sendActionBar(component));
        }, ArgumentType.Entity("targets").onlyPlayers(true), new ArgumentFlexibleComponent("component", true));

    }
}
