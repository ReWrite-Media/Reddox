package net.minestom.script.command;

import net.minestom.script.ScriptManager;
import net.minestom.script.object.Properties;
import net.minestom.server.command.builder.Arguments;
import net.minestom.server.command.builder.Command;
import org.jglrxavpok.hephaistos.nbt.NBT;

import java.lang.String;
import java.util.ArrayList;
import java.util.List;

import static net.minestom.server.command.builder.arguments.ArgumentType.*;

/**
 * Command used to manage registered functions. Including running them.
 */
public class FunctionCommand extends Command {
    public FunctionCommand() {
        super("function");

        setDefaultExecutor((sender, args) -> sender.sendMessage("Usage: /function run <name> [properties...]"));

        final var propertiesArgument = Loop("properties",
                Group("properties_group", Word("key"), NBT("value")))
                .setDefaultValue(new ArrayList<>());

        addSyntax((sender, args) -> {
            final String name = args.get("function_name");
            final List<Arguments> loopArguments = args.get(propertiesArgument);

            // Build the properties object
            Properties properties = new Properties();
            for (Arguments property : loopArguments) {
                final String key = property.get("key");
                final NBT nbt = property.get("value");
                properties.putMember(key, nbt);
            }

            final boolean success = ScriptManager.EXECUTOR.function(name, properties);
            if (success) {
                sender.sendMessage("You executed the function: " + name);
            } else {
                sender.sendMessage("Unknown function name");
            }
        }, Literal("run"), Word("function_name"), propertiesArgument);
    }
}
