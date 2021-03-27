package net.minestom.script.command.world;

import net.kyori.adventure.text.Component;
import net.minestom.script.command.RichCommand;
import net.minestom.script.utils.InstanceUtils;
import net.minestom.server.command.builder.arguments.ArgumentType;

public class TimeCommand extends RichCommand {
    public TimeCommand() {
        super("time");

        setDefaultExecutor((sender, context) ->
                sender.sendMessage(Component.text("Usage: /time set <day/night>")));

        var typeArgument = ArgumentType.Word("type").from("set");
        var timeNumberArgument = ArgumentType.Integer("time_value");
        var timeConstantArgument = ArgumentType.Word("time_constant")
                .from("day", "night");

        addSyntax((sender, context) -> {
            final String timeConstant = context.get(timeConstantArgument);
            long time = 0;
            if (timeConstant.equals("day")) {
                time = 1000;
            } else if (timeConstant.equals("night")) {
                time = 13000;
            }

            long finalTime = time;
            InstanceUtils.processInstances(sender, instance -> instance.setTime(finalTime));
            sender.sendMessage(Component.text("Set time to " + time));
        }, typeArgument, timeConstantArgument);

        addSyntax((sender, context) -> {
            final long time = context.get(timeNumberArgument);

            InstanceUtils.processInstances(sender, instance -> instance.setTime(time));
            sender.sendMessage(Component.text("Set time to " + time));
        }, typeArgument, timeNumberArgument);
    }
}
