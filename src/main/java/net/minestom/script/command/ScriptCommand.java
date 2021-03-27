package net.minestom.script.command;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.minestom.script.Script;
import net.minestom.script.ScriptManager;
import net.minestom.script.command.editor.EditorCommand;
import net.minestom.server.command.CommandSender;
import net.minestom.server.command.builder.CommandContext;
import net.minestom.server.command.builder.suggestion.Suggestion;
import net.minestom.server.command.builder.suggestion.SuggestionEntry;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

import static net.minestom.server.command.builder.arguments.ArgumentType.Literal;
import static net.minestom.server.command.builder.arguments.ArgumentType.StringArray;

/**
 * Manages registered scripts.
 */
public class ScriptCommand extends RichCommand {
    public ScriptCommand() {
        super("script");

        this.addSubcommand(new EditorCommand());

        setDefaultExecutor((sender, context) -> {
            sender.sendMessage(Component.text("Usage: /script <list/load/unload> [path]"));
        });


        var pathArgument = StringArray("path")
                .setDefaultValue(new String[0])
                .setSuggestionCallback(this::pathSuggestion);

        addSyntax((sender, context) -> {
            var scripts = getScripts();

            Component component = Component.text("Scripts (" + scripts.size() + "):", NamedTextColor.WHITE);

            sender.sendMessage(component);

            for (Script script : scripts) {
                final TextColor color = script.isLoaded() ? NamedTextColor.GREEN : NamedTextColor.RED;
                final String name = script.getName();

                Component scriptComponent = Component.text(name, color)
                        .append(Component.space())
                        .append(Component.text("[Load]")
                                .color(NamedTextColor.GRAY)
                                .hoverEvent(HoverEvent.showText(Component.text("Click to load", NamedTextColor.GRAY)))
                                .clickEvent(ClickEvent.runCommand("/script load " + name)))
                        .append(Component.space())
                        .append(Component.text("[Unload]")
                                .color(NamedTextColor.DARK_GRAY)
                                .hoverEvent(HoverEvent.showText(Component.text("Click to unload", NamedTextColor.DARK_GRAY)))
                                .clickEvent(ClickEvent.runCommand("/script unload " + name)));

                sender.sendMessage(scriptComponent);
            }
        }, Literal("list"));

        addSyntax((sender, context) -> {
            final String[] path = context.get(pathArgument);
            processPath(sender, String.join(" ", path), script -> {
                if (script.isLoaded()) {
                    sender.sendMessage(Component.text("Script is already loaded", NamedTextColor.RED));
                } else {
                    script.load();
                    sender.sendMessage(Component.text("Script loaded successfully!", NamedTextColor.GREEN));
                }
            });
        }, Literal("load"), pathArgument);

        addSyntax((sender, context) -> {
            final String[] path = context.get(pathArgument);
            processPath(sender, String.join(" ", path), script -> {
                if (script.isLoaded()) {
                    script.unload();
                    sender.sendMessage(Component.text("Script unloaded successfully!", NamedTextColor.GREEN));
                } else {
                    sender.sendMessage(Component.text("Script is already unloaded", NamedTextColor.RED));
                }
            });
        }, Literal("unload"), pathArgument);

        addSyntax((sender, context) -> {
            final String[] path = context.get("path");
            if (path.length != 0) {
                // Reload specific script
                processPath(sender, String.join(" ", path), script -> {
                    script.unload();
                    script.load();
                    sender.sendMessage(Component.text("Script reloaded", NamedTextColor.GREEN));
                });
            } else {
                // Reload all scripts
                ScriptManager.reload();
                var scripts = getScripts();
                sender.sendMessage(Component.text("You did reload " + scripts.size() + " scripts!", NamedTextColor.GREEN));
            }
        }, Literal("reload"), pathArgument);
    }

    @NotNull
    private List<Script> getScripts() {
        return ScriptManager.getScripts();
    }

    private void processPath(CommandSender sender, String path, Consumer<Script> scriptConsumer) {
        Optional<Script> optionalScript = getScripts()
                .stream()
                .filter(script -> Objects.equals(script.getName(), path))
                .findFirst();

        optionalScript.ifPresentOrElse(scriptConsumer, () ->
                sender.sendMessage(Component.text("Invalid path", NamedTextColor.RED)));
    }

    private void pathSuggestion(CommandSender sender, CommandContext context, Suggestion suggestion) {
        final String input = suggestion.getInput();
        for (Script script : getScripts()) {
            final String name = script.getName();
            if (name.toLowerCase().contains(input.toLowerCase())) {
                suggestion.addEntry(new SuggestionEntry(name));
            }
        }
    }
}
