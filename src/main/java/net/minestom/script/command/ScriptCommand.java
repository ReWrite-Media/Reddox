package net.minestom.script.command;

import net.minestom.script.Script;
import net.minestom.script.ScriptManager;
import net.minestom.server.chat.ChatColor;
import net.minestom.server.chat.ColoredText;
import net.minestom.server.command.CommandSender;
import net.minestom.server.command.builder.CommandContext;
import net.minestom.server.command.builder.suggestion.Suggestion;
import net.minestom.server.command.builder.suggestion.SuggestionEntry;
import org.jetbrains.annotations.NotNull;

import java.util.List;
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

        setDefaultExecutor((sender, args) -> {
            sender.sendMessage("Usage: /script <list/load/unload> [path]");
        });


        var pathArgument = StringArray("path")
                .setDefaultValue(new String[0])
                .setSuggestionCallback(this::pathSuggestion);

        addSyntax((sender, context) -> {
            for (Script script : getScripts()) {
                ChatColor color = script.isLoaded() ? ChatColor.BRIGHT_GREEN : ChatColor.RED;
                sender.sendMessage(ColoredText.of(color, "Path: " + script.getFile()));
            }
        }, Literal("list"));

        addSyntax((sender, context) -> {
            final String[] path = context.get(pathArgument);
            processPath(sender, String.join(" ", path), script -> {
                if (script.isLoaded()) {
                    sender.sendMessage("Script is already loaded");
                } else {
                    script.load();
                    sender.sendMessage("Script loaded successfully!");
                }
            });
        }, Literal("load"), pathArgument);

        addSyntax((sender, context) -> {
            final String[] path = context.get(pathArgument);
            processPath(sender, String.join(" ", path), script -> {
                if (script.isLoaded()) {
                    script.unload();
                    sender.sendMessage("Script unloaded successfully!");
                } else {
                    sender.sendMessage("Script is already unloaded");
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
                });
                sender.sendMessage("Script reloaded");
            } else {
                // Reload all scripts
                var scripts = getScripts();
                getScripts().forEach(Script::unload);
                getScripts().forEach(Script::load);
                sender.sendMessage("You did reload " + scripts.size() + " scripts!");
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
                .filter(script -> script.getFile().getPath().equals(path))
                .findFirst();

        // Valid path
        optionalScript.ifPresentOrElse(scriptConsumer::accept, () -> {
            // Invalid path
            sender.sendMessage("Invalid path");
        });
    }

    private void pathSuggestion(CommandSender sender, CommandContext context, Suggestion suggestion) {
        final String input = suggestion.getInput();
        for (Script script : getScripts()) {
            final String path = script.getFile().getPath();
            if (path.contains(input)) {
                suggestion.addEntry(new SuggestionEntry(path));
            }
        }
    }
}
