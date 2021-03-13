package net.minestom.script.command;

import net.minestom.script.property.Properties;
import net.minestom.server.command.builder.CommandContext;
import org.jglrxavpok.hephaistos.nbt.NBT;

import java.lang.String;
import java.util.ArrayList;
import java.util.List;

import static net.minestom.server.command.builder.arguments.ArgumentType.*;

/**
 * Command used to manage registered functions. Including running them.
 */
public class FunctionCommand extends RichCommand {
    public FunctionCommand() {
        super("function");

        setDefaultExecutor((sender, args) -> sender.sendMessage("Usage: /function run <name> [properties...]"));

        final var propertiesArgument = Loop("properties",
                Group("properties_group", Word("key"), NBT("value")))
                .setDefaultValue(new ArrayList<>());

        addSyntax((sender, context) -> {
            final String name = context.get("function_name");
            final List<CommandContext> loopArguments = context.get(propertiesArgument);

            // Build the properties object
            Properties properties = new Properties();
            for (CommandContext property : loopArguments) {
                final String key = property.get("key");
                final NBT nbt = property.get("value");
                properties.putMember(key, nbt);
            }

            final boolean success = getApi().getExecutor().function(name, properties);
            if (success) {
                sender.sendMessage("You executed the function: " + name);
            } else {
                sender.sendMessage("Unknown function name");
            }
        }, Literal("run"), Word("function_name"), propertiesArgument);
    }
}
