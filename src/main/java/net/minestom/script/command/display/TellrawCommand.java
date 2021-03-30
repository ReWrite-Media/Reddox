package net.minestom.script.command.display;

import static net.minestom.server.command.builder.arguments.ArgumentType.Entity;

import java.util.List;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.minestom.script.command.RichCommand;
import net.minestom.script.command.arguments.ArgumentFlexibleComponent;
import net.minestom.script.documentation.ArgumentDocumentation;
import net.minestom.script.documentation.ArgumentDocumentationType;
import net.minestom.script.documentation.CommandDocumentation;
import net.minestom.script.documentation.Documented;
import net.minestom.server.entity.Entity;
import net.minestom.server.utils.entity.EntityFinder;

public class TellrawCommand extends RichCommand implements Documented<CommandDocumentation> {
    public TellrawCommand() {
        super("tellraw");

        setDefaultExecutor((sender, context) ->
                sender.sendMessage(Component.text("Usage: /display tellraw <targets> <message>")));

        addSyntax((sender, context) -> {
            EntityFinder entityFinder = context.get("targets");
            final Component component = context.get("component");
            final List<Entity> entities = entityFinder.find(sender);
            entities.stream()
                    .filter(Audience.class::isInstance)
                    .map(Audience.class::cast)
                    .forEach(audience -> audience.sendMessage(component));
        }, Entity("targets").onlyPlayers(true), new ArgumentFlexibleComponent("component", true));

    }

	@Override
	public CommandDocumentation getDocumentation() {
		return new CommandDocumentation(this)
			.addSyntax(
				"sends a chat message to a player",
				new ArgumentDocumentation()
					.setName("target")
					.setType(ArgumentDocumentationType.PLAYER)
					.setDescription("The player to send this message to."),
				new ArgumentDocumentation()
					.setName("component")
					.setType(ArgumentDocumentationType.FLEXIBLE_COMPONENT)
					.setDescription("The text to send to the player")
			);
	}
}
