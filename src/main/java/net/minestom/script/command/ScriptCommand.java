package net.minestom.script.command;

import net.minestom.script.Script;
import net.minestom.script.ScriptManager;
import net.minestom.server.chat.ChatColor;
import net.minestom.server.chat.ColoredText;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

import static net.minestom.server.command.builder.arguments.ArgumentType.Literal;
import static net.minestom.server.command.builder.arguments.ArgumentType.String;

/**
 * Manages registered scripts.
 */
public class ScriptCommand extends RichCommand {
    public ScriptCommand() {
        super("script");

        setDefaultExecutor((sender, args) -> {
            sender.sendMessage("Usage: /script <list/load/unload> [path]");
        });


        addSyntax((sender, args) -> {
            for (Script script : getScripts()) {
                ChatColor color = script.isLoaded() ? ChatColor.BRIGHT_GREEN : ChatColor.RED;
                sender.sendMessage(ColoredText.of(color, "Path: " + script.getFile()));
            }
        }, Literal("list"));

        addSyntax((sender, args) -> {
            final String path = args.get("path");
            Optional<Script> optionalScript = getScripts()
                    .stream()
                    .filter(script -> script.getFile().getPath().equals(path))
                    .findFirst();

            optionalScript.ifPresentOrElse(script -> {
                // Valid path
                if (script.isLoaded()) {
                    sender.sendMessage("Script is already loaded");
                } else {
                    script.load();
                    sender.sendMessage("Script loaded successfully!");
                }
            }, () -> {
                // Invalid path
                sender.sendMessage("Invalid path");
            });
        }, Literal("load"), String("path"));

        addSyntax((sender, args) -> {
            final String path = args.get("path");
            Optional<Script> optionalScript = getScripts()
                    .stream()
                    .filter(script -> script.getFile().getPath().equals(path))
                    .findFirst();

            optionalScript.ifPresentOrElse(script -> {
                // Valid path
                if (script.isLoaded()) {
                    script.unload();
                    sender.sendMessage("Script unloaded successfully!");
                } else {
                    sender.sendMessage("Script is already unloaded");
                }
            }, () -> {
                // Invalid path
                sender.sendMessage("Invalid path");
            });
        }, Literal("unload"), String("path"));
    }

    @NotNull
    private List<Script> getScripts() {
        return ScriptManager.getScripts();
    }
}
