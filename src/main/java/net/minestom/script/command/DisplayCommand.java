package net.minestom.script.command;

import net.minestom.script.command.display.ActionBarCommand;
import net.minestom.script.command.display.BossBarCommand;
import net.minestom.script.command.display.TellrawCommand;

public class DisplayCommand extends RichCommand {
    public DisplayCommand() {
        super("display");

        addSubcommand(new TellrawCommand());
        addSubcommand(new ActionBarCommand());
        addSubcommand(new BossBarCommand());
    }
}
