package net.minestom.script.command;

import net.kyori.adventure.text.Component;
import net.minestom.script.ScriptManager;
import net.minestom.script.component.ScriptAPI;
import net.minestom.server.command.CommandSender;
import net.minestom.server.command.builder.Command;
import net.minestom.server.instance.Instance;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

/**
 * Represents a Reddox command, commands extending this
 * are expected to contain rich command data to be used within scripts.
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

        setDefaultExecutor((sender, context) ->
                sender.sendMessage(Component.text("Default script executor: " + getClass().getSimpleName())));
    }

    public RichCommand(@NotNull String name) {
        this(name, ScriptCategory.UNKNOWN);
    }

    public @NotNull ScriptAPI getApi() {
        return ScriptManager.API;
    }

    public @NotNull String getCategory() {
        return category;
    }

    public void processInstances(@NotNull CommandSender sender,
                                 @NotNull Consumer<Instance> consumer) {
        var instances = ScriptManager.getInstanceSupplier().apply(sender);
        instances.forEach(consumer);
    }

}
