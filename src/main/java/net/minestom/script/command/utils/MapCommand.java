package net.minestom.script.command.utils;

import net.kyori.adventure.text.Component;
import net.minestom.script.command.RichCommand;
import net.minestom.server.command.CommandSender;
import net.minestom.server.command.builder.CommandContext;
import net.minestom.server.command.builder.CommandData;
import net.minestom.server.command.builder.suggestion.Suggestion;
import net.minestom.server.command.builder.suggestion.SuggestionEntry;
import org.jglrxavpok.hephaistos.nbt.NBT;

import java.lang.String;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static net.minestom.server.command.builder.arguments.ArgumentType.*;

public class MapCommand extends RichCommand {

    private static final String NAMESPACE_SEPARATOR = ":";

    private final Map<String, NBT> nbtMap = new ConcurrentHashMap<>();

    public MapCommand() {
        super("map");

        var keyArgument = ResourceLocation("key")
                .setSuggestionCallback(this::keySuggestion);

        addSyntax((sender, context) -> {
            final String key = context.get(keyArgument);
            final NBT nbt = context.get("value");

            nbtMap.put(key.toLowerCase(), nbt);

            sender.sendMessage(Component.text("Map entry '" + key + "' updated"));
        }, Literal("set"), keyArgument, NBT("value"));

        addSyntax((sender, context) -> {
            final String key = context.get(keyArgument);

            final boolean success = nbtMap.containsKey(key.toLowerCase());
            CommandData commandData = new CommandData();
            commandData.set("success", success);
            if (success) {
                final NBT nbt = nbtMap.get(key.toLowerCase());
                commandData.set("value", nbt);
                sender.sendMessage(Component.text("Map value: " + nbt.toSNBT()));
            } else {
                sender.sendMessage(Component.text("Key not found!"));
            }

            context.setReturnData(commandData);
        }, Literal("get"), keyArgument);
    }

    private void keySuggestion(CommandSender sender, CommandContext context, Suggestion suggestion) {
        final String input = suggestion.getInput().toLowerCase();
        nbtMap.keySet().forEach(s -> {
            if (!s.toLowerCase().startsWith(input))
                return;
            suggestion.addEntry(new SuggestionEntry(s));
        });
    }
}
