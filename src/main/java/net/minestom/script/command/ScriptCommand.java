package net.minestom.script.command;

import net.kyori.adventure.text.Component;
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
            for (Script script : getScripts()) {
                final TextColor color = script.isLoaded() ? NamedTextColor.GREEN : NamedTextColor.RED;
                final String name = script.getName();
                sender.sendMessage(Component.text("Path: " + name, color));
            }
        }, Literal("list"));

        addSyntax((sender, context) -> {
            final String[] path = context.get(pathArgument);
            processPath(sender, String.join(" ", path), script -> {
                if (script.isLoaded()) {
                    sender.sendMessage(Component.text("Script is already loaded"));
                } else {
                    script.load();
                    sender.sendMessage(Component.text("Script loaded successfully!"));
                }
            });
        }, Literal("load"), pathArgument);

        addSyntax((sender, context) -> {
            final String[] path = context.get(pathArgument);
            processPath(sender, String.join(" ", path), script -> {
                if (script.isLoaded()) {
                    script.unload();
                    sender.sendMessage(Component.text("Script unloaded successfully!"));
                } else {
                    sender.sendMessage(Component.text("Script is already unloaded"));
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
                    sender.sendMessage(Component.text("Script reloaded"));
                });
            } else {
                // Reload all scripts
                ScriptManager.reload();
                var scripts = getScripts();
                sender.sendMessage(Component.text("You did reload " + scripts.size() + " scripts!"));
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

        optionalScript.ifPresentOrElse(scriptConsumer, () -> {
            // Invalid path
            sender.sendMessage(Component.text("Invalid path"));
        });
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
