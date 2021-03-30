package net.minestom.script.command.display;

import static net.minestom.server.command.builder.arguments.ArgumentType.Entity;
import static net.minestom.server.command.builder.arguments.ArgumentType.Enum;
import static net.minestom.server.command.builder.arguments.ArgumentType.Integer;
import static net.minestom.server.command.builder.arguments.ArgumentType.Literal;
import static net.minestom.server.command.builder.arguments.ArgumentType.ResourceLocation;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.script.command.RichCommand;
import net.minestom.script.command.arguments.ArgumentFlexibleComponent;
import net.minestom.script.documentation.ArgumentDocumentation;
import net.minestom.script.documentation.ArgumentDocumentationType;
import net.minestom.script.documentation.CommandDocumentation;
import net.minestom.script.documentation.Documented;
import net.minestom.server.MinecraftServer;
import net.minestom.server.command.CommandSender;
import net.minestom.server.command.builder.arguments.ArgumentEnum;
import net.minestom.server.command.builder.arguments.minecraft.ArgumentResourceLocation;
import net.minestom.server.entity.Entity;
import net.minestom.server.utils.entity.EntityFinder;


public class BossBarCommand extends RichCommand implements Documented<CommandDocumentation> {

    private final static Map<String, BossBar> bossBarMap = new ConcurrentHashMap<>();

    private final static float DEFAULT_PROGRESS = 1;
    private final static BossBar.Color DEFAULT_COLOR = BossBar.Color.WHITE;
    private final static BossBar.Overlay DEFAULT_OVERLAY = BossBar.Overlay.PROGRESS;

    public BossBarCommand() {
        super("bossbar");

        var identifierArgument = ResourceLocation("id");
        var nameArgument = new ArgumentFlexibleComponent("name", true);

        addSyntax((sender, context) -> {
            Component component = Component.text("Boss bars (" + bossBarMap.size() + "):", NamedTextColor.WHITE);

            sender.sendMessage(component);

            Component tab = Component.newline().append(Component.text("    "));

            bossBarMap.forEach((identifier, bossBar) -> {
                Component barComponent = Component.text(identifier + ":", NamedTextColor.GRAY)
                        .append(tab)
                        .append(getPropertyComponent(identifier, "Color", bossBar.color().toString().toLowerCase()))
                        .append(tab)
                        .append(getPropertyComponent(identifier, "Name", bossBar.name()))
                        .append(tab)
                        .append(getPropertyComponent(identifier, "Players", "TODO"))
                        .append(tab)
                        .append(getPropertyComponent(identifier, "Progress", (int) (bossBar.progress() * 100) + "%"))
                        .append(tab)
                        .append(getPropertyComponent(identifier, "Style", bossBar.overlay().name().toLowerCase()));
                sender.sendMessage(barComponent);
            });
        }, Literal("list"));

        addSyntax((sender, context) -> {
            final String identifier = context.get(identifierArgument);
            final Component component = context.get(nameArgument);
            final BossBar bossBar = BossBar.bossBar(component,
                    DEFAULT_PROGRESS, DEFAULT_COLOR, DEFAULT_OVERLAY);

            synchronized (bossBarMap) {
                if (!bossBarMap.containsKey(identifier)) {
                    bossBarMap.put(identifier, bossBar);
                    sender.sendMessage(Component.text("Boss bar '" + identifier + "' created successfully!", NamedTextColor.GREEN));
                } else {
                    sender.sendMessage(Component.text("A boss bar with the identifier '" + identifier + "' already exists!", NamedTextColor.RED));
                }
            }

        }, Literal("create"), identifierArgument, nameArgument);

        addSyntax((sender, context) -> {
            final String identifier = context.get(identifierArgument);
            processBossBar(sender, identifier, bossBar -> {
                synchronized (bossBarMap){
                    bossBarMap.remove(identifier);
                    MinecraftServer.getBossBarManager().destroyBossBar(bossBar);
                    sender.sendMessage(Component.text("Bossbar '" + identifier + "' destroyed", NamedTextColor.GREEN));
                }
            });
        }, Literal("remove"), identifierArgument);

        addSubcommand(new SetSubCommand(identifierArgument, nameArgument));

    }

    private static class SetSubCommand extends RichCommand implements Documented<CommandDocumentation> {

        public SetSubCommand(ArgumentResourceLocation identifierArgument, ArgumentFlexibleComponent nameArgument) {
            super("set");

            addSyntax((sender, context) -> {
                final String identifier = context.get(identifierArgument);
                final BossBar.Color color = context.get("value");
                processBossBar(sender, identifier, bossBar -> {
                    bossBar.color(color);
                    sender.sendMessage(Component.text("Color modified", NamedTextColor.GREEN));
                });
            }, identifierArgument, Literal("color"), Enum("value", BossBar.Color.class).setFormat(ArgumentEnum.Format.LOWER_CASED));

            addSyntax((sender, context) -> {
                final String identifier = context.get(identifierArgument);
                final Component component = context.get(nameArgument);
                processBossBar(sender, identifier, bossBar -> {
                    bossBar.name(component);
                    sender.sendMessage(Component.text("Name modified", NamedTextColor.GREEN));
                });
            }, identifierArgument, Literal("name"), nameArgument);

            addSyntax((sender, context) -> {
                final String identifier = context.get(identifierArgument);
                EntityFinder entityFinder = context.get("targets");
                final List<Entity> entities = entityFinder.find(sender);

                processBossBar(sender, identifier, bossBar -> {
                    // Remove all current viewers
                    MinecraftServer.getBossBarManager().destroyBossBar(bossBar);

                    entities.stream()
                            .filter(Audience.class::isInstance)
                            .map(Audience.class::cast)
                            .forEach(audience -> audience.showBossBar(bossBar));

                    sender.sendMessage(Component.text("Bossbar sent", NamedTextColor.GREEN));
                });
            }, identifierArgument, Literal("players"), Entity("targets").onlyPlayers(true));

            addSyntax((sender, context) -> {
                final String identifier = context.get(identifierArgument);
                final BossBar.Overlay overlay = context.get("value");
                processBossBar(sender, identifier, bossBar -> {
                    bossBar.overlay(overlay);
                    sender.sendMessage(Component.text("Style modified", NamedTextColor.GREEN));
                });
            }, identifierArgument, Literal("style"), Enum("value", BossBar.Overlay.class).setFormat(ArgumentEnum.Format.LOWER_CASED));

            addSyntax((sender, context) -> {
                final String identifier = context.get(identifierArgument);
                final int value = context.get("value");
                processBossBar(sender, identifier, bossBar -> {
                    bossBar.progress((float) value / 100);
                    sender.sendMessage(Component.text("Progress modified", NamedTextColor.GREEN));
                });
            }, identifierArgument, Literal("progress"), Integer("value").between(0, 100));
        }

		@Override
		public CommandDocumentation getDocumentation() {
			return new CommandDocumentation(this)
				.addSyntax(
					"sets the color of a bossbar",
					new ArgumentDocumentation()
						.setName("id")
						.setType(ArgumentDocumentationType.RESOURCE_LOCATION)
						.setDescription("the specific bossbar"),
					new ArgumentDocumentation()
						.setName("color")
						.setType(ArgumentDocumentationType.LITERAL),
					new ArgumentDocumentation()
						.setName("value")
						.setType(ArgumentDocumentationType.ENUM)
						.setExamples(ArgumentDocumentation.enumStrings(BossBar.Color.class, ArgumentEnum.Format.LOWER_CASED))
						.setDescription("the new color of the bossbar")
				)
				.addSyntax(
					"sets the name of a bossbar",
					new ArgumentDocumentation()
						.setName("id")
						.setType(ArgumentDocumentationType.RESOURCE_LOCATION)
						.setDescription("the specific bossbar"),
					new ArgumentDocumentation()
						.setName("name")
						.setType(ArgumentDocumentationType.LITERAL),
					new ArgumentDocumentation()
						.setName("name")
						.setType(ArgumentDocumentationType.FLEXIBLE_COMPONENT)
						.setDescription("the new name of the bossbar")
				)
				.addSyntax(
					"sets the players of a bossbar",
					new ArgumentDocumentation()
						.setName("id")
						.setType(ArgumentDocumentationType.RESOURCE_LOCATION)
						.setDescription("the specific bossbar"),
					new ArgumentDocumentation()
						.setName("players")
						.setType(ArgumentDocumentationType.LITERAL),
					new ArgumentDocumentation()
						.setName("value")
						.setType(ArgumentDocumentationType.PLAYER)
						.setRecurring(true)
						.setDescription("the new players of the bossbar")
				)
				.addSyntax(
					"sets the style of a bossbar",
					new ArgumentDocumentation()
						.setName("id")
						.setType(ArgumentDocumentationType.RESOURCE_LOCATION)
						.setDescription("the specific bossbar"),
					new ArgumentDocumentation()
						.setName("style")
						.setType(ArgumentDocumentationType.LITERAL),
					new ArgumentDocumentation()
						.setName("value")
						.setType(ArgumentDocumentationType.ENUM)
						.setRecurring(true)
						.setDescription("the new style of the bossbar")
						.setExamples(ArgumentDocumentation.enumStrings(BossBar.Overlay.class, ArgumentEnum.Format.LOWER_CASED))
				)
				.addSyntax(
						"sets the progress of a bossbar",
						new ArgumentDocumentation()
							.setName("id")
							.setType(ArgumentDocumentationType.RESOURCE_LOCATION)
							.setDescription("the specific bossbar"),
						new ArgumentDocumentation()
							.setName("progress")
							.setType(ArgumentDocumentationType.LITERAL),
						new ArgumentDocumentation()
							.setName("value")
							.setType(ArgumentDocumentationType.INTEGER)
							.setDescription("the new progress of the bossbar")
							.setExamples("0, 2, 76... Any integer between 0 and 100")
					);
		}
    }

    private static void processBossBar(CommandSender sender, String identifier, Consumer<BossBar> consumer) {
        final BossBar bossBar = bossBarMap.get(identifier);
        if (bossBar != null) {
            consumer.accept(bossBar);
        } else {
            sender.sendMessage(Component.text("Invalid identifier", NamedTextColor.RED));
        }
    }

    private static Component getPropertyComponent(String identifier, String property, Component valueComponent) {
        final String propertyId = property.toLowerCase(Locale.ROOT);
        HoverEvent<Component> hoverEvent = HoverEvent.showText(Component.text("Edit '" + identifier + "' " + property));
        ClickEvent clickEvent = ClickEvent.suggestCommand("/display bossbar set " + identifier + " " + propertyId + " ");
        return Component.empty()
                .append(Component.text(property + ": ", NamedTextColor.GRAY).hoverEvent(hoverEvent).clickEvent(clickEvent))
                .append(valueComponent.hoverEvent(hoverEvent).clickEvent(clickEvent));
    }

    private static Component getPropertyComponent(String identifier, String property, String value) {
        return getPropertyComponent(identifier, property, Component.text(value, NamedTextColor.WHITE));
    }

	@Override
	public CommandDocumentation getDocumentation() {
		return new CommandDocumentation(this)
			.addSyntax(
				"lists the currently active bossbars",
				new ArgumentDocumentation()
					.setName("list")
					.setType(ArgumentDocumentationType.LITERAL)
			)
			.addSyntax(
				"creates a bossbar",
				new ArgumentDocumentation()
					.setName("create")
					.setType(ArgumentDocumentationType.LITERAL),
				new ArgumentDocumentation()
					.setName("id")
					.setType(ArgumentDocumentationType.RESOURCE_LOCATION),
				new ArgumentDocumentation()
					.setName("name")
					.setType(ArgumentDocumentationType.FLEXIBLE_COMPONENT)
					.setDescription("the name of the bossbar")
			)
			.addSyntax("removes a bossbar",
				new ArgumentDocumentation()
					.setName("remove")
					.setType(ArgumentDocumentationType.LITERAL),
				new ArgumentDocumentation()
					.setName("id")
					.setType(ArgumentDocumentationType.RESOURCE_LOCATION)
			);
	}

}
