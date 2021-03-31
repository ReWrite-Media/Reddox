package net.minestom.script.command;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
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
public class SignalCommand extends RichCommand {
    public SignalCommand() {
        super("signal");

        setDefaultExecutor((sender, context) ->
                sender.sendMessage(Component.text("Usage: /signal run <name> [properties...]")));

        final var propertiesArgument = Loop("properties",
                Group("properties_group", Word("key"), NBT("value")))
                .setDefaultValue(ArrayList::new);

        addSyntax((sender, context) -> {
            final String name = context.get("name");
            final List<CommandContext> loopArguments = context.get(propertiesArgument);

            // Build the properties object
            Properties properties = new Properties();
            for (CommandContext property : loopArguments) {
                final String key = property.get("key");
                final NBT nbt = property.get("value");
                properties.putMember(key, nbt);
            }

            getApi().getExecutor().signal(name, properties);
            sender.sendMessage(Component.text("You executed the signal " + name,
                    NamedTextColor.GRAY, TextDecoration.ITALIC));
        }, Literal("run"), Word("name"), propertiesArgument);
    }
}
