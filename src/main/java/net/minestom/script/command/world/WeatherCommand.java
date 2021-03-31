package net.minestom.script.command.world;

import net.minestom.script.command.RichCommand;

import static net.minestom.server.command.builder.arguments.ArgumentType.Integer;
import static net.minestom.server.command.builder.arguments.ArgumentType.Word;

public class WeatherCommand extends RichCommand {
    public WeatherCommand() {
        super("weather");

        addSyntax((sender, context) -> {
            final String type = context.get("type");
            // TODO Minestom weather API
            if (type.equals("clear")) {
            } else if (type.equals("rain")) {
            } else if (type.equals("thunder")) {
            }
        }, Word("type").from("clear", "rain", "thunder"), Integer("duration").setDefaultValue(() -> 0));

        // TODO query

    }
}
