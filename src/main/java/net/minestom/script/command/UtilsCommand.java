package net.minestom.script.command;

import net.minestom.script.command.utils.AliasCommand;
import net.minestom.script.command.utils.MapCommand;
import net.minestom.script.command.utils.ScheduleCommand;

public class UtilsCommand extends RichCommand {
    public UtilsCommand() {
        super("utils");

        addSubcommand(new ScheduleCommand());
        addSubcommand(new AliasCommand());
        addSubcommand(new MapCommand());
    }
}
