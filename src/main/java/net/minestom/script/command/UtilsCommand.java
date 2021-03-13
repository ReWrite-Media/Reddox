package net.minestom.script.command;

import net.minestom.script.command.utils.ScheduleCommand;

public class UtilsCommand extends RichCommand {
    public UtilsCommand() {
        super("utils");

        addSubcommand(new ScheduleCommand());
    }
}
