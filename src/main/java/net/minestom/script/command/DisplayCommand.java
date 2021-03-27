package net.minestom.script.command;

import net.minestom.script.command.display.TellrawCommand;

public class DisplayCommand extends RichCommand {
    public DisplayCommand() {
        super("display");

        addSubcommand(new TellrawCommand());
    }
}
