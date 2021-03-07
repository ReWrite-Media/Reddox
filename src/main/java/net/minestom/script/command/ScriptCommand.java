package net.minestom.script.command;

import net.minestom.script.ScriptManager;
import net.minestom.script.component.ScriptAPI;
import net.minestom.server.command.builder.Command;
import org.jetbrains.annotations.NotNull;

public class ScriptCommand extends Command {

    private final String category;

    public ScriptCommand(@NotNull String name, @NotNull String category) {
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

        setDefaultExecutor((sender, args) -> sender.sendMessage("Default script executor"));
    }

    public ScriptCommand(@NotNull String name) {
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
