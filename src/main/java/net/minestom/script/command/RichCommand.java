package net.minestom.script.command;

import net.kyori.adventure.text.Component;
import net.minestom.script.ScriptManager;
import net.minestom.script.component.ScriptAPI;
import net.minestom.server.command.builder.Command;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a MineScript command, commands extending this
 * are expected to contains rich command data to be used within scripts.
 */
public class RichCommand extends Command {

    private final String category;

    public RichCommand(@NotNull String name, @NotNull String category) {
        super(name);
        this.category = category;

        setCondition((source, commandString) -> {
            if (!source.isPlayer()) {
                // Permission for server and console
                return true;
            }
            // Permission depending on the end application
            return ScriptManager.getCommandPermission().apply(source.asPlayer());
        });

        setDefaultExecutor((sender, context) -> sender.sendMessage(Component.text("Default script executor")));
    }

    public RichCommand(@NotNull String name) {
        this(name, ScriptCategory.UNKNOWN);
    }

    @NotNull
    public ScriptAPI getApi() {
        return ScriptManager.API;
    }

    @NotNull
    public String getCategory() {
        return category;
    }
}
