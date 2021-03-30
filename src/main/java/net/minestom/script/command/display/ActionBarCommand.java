package net.minestom.script.command.display;

import java.util.List;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.minestom.script.command.RichCommand;
import net.minestom.script.command.arguments.ArgumentFlexibleComponent;
import net.minestom.script.documentation.ArgumentDocumentation;
import net.minestom.script.documentation.ArgumentDocumentationType;
import net.minestom.script.documentation.CommandDocumentation;
import net.minestom.script.documentation.Documented;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.entity.Entity;
import net.minestom.server.utils.entity.EntityFinder;

public class ActionBarCommand extends RichCommand implements Documented<CommandDocumentation> {
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

	@Override
	public CommandDocumentation getDocumentation() {
		return new CommandDocumentation(this)
			.addSyntax(
				"sends an actionbar to a player",
				new ArgumentDocumentation()
					.setName("targets")
					.setType(ArgumentDocumentationType.PLAYER)
					.setDescription("the player to send the actionbar to"),
				new ArgumentDocumentation()
					.setName("component")
					.setType(ArgumentDocumentationType.FLEXIBLE_COMPONENT)
					.setDescription("the component to send to the player")
			);
	}
}
