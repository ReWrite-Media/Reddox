package net.minestom.script.command;

import net.minestom.script.command.display.ActionBarCommand;
import net.minestom.script.command.display.BossBarCommand;
import net.minestom.script.command.display.TellrawCommand;
import net.minestom.script.documentation.CommandDocumentation;
import net.minestom.script.documentation.Documented;

public class DisplayCommand extends RichCommand implements Documented<CommandDocumentation> {
    public DisplayCommand() {
        super("display");

        addSubcommand(new TellrawCommand());
        addSubcommand(new ActionBarCommand());
        addSubcommand(new BossBarCommand());
    }

	@Override
	public CommandDocumentation getDocumentation() {
		return new CommandDocumentation(this);
	}
}
