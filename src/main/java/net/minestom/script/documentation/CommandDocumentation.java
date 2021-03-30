package net.minestom.script.documentation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.jetbrains.annotations.NotNull;

import net.minestom.server.command.builder.Command;

public class CommandDocumentation extends Documentation {
	
	String command;
	List<SyntaxDocumentation> syntaxes = new ArrayList<SyntaxDocumentation>();
	List<CommandDocumentation> subCommands = new ArrayList<CommandDocumentation>();
	
	public CommandDocumentation(Command command) {
		this.command = command.getName();
		addSubCommands(command.getSubcommands());
	}
	
	public CommandDocumentation addSyntax(String description, ArgumentDocumentation... args) {
		syntaxes.add(new SyntaxDocumentation().setDescription(description).setArguments(args));
		return this;
	}

	private CommandDocumentation addSubCommand(@NotNull CommandDocumentation subCmd) {
		subCommands.add(subCmd);
		return this;
	}
	
	@SuppressWarnings("unchecked")
	private CommandDocumentation addSubCommands(@NotNull List<Command> subCmds) {
		subCmds.stream()
			.filter(Documented.class::isInstance)
			.map(cmd -> {
				return ((Documented<CommandDocumentation>) cmd).getDocumentation();
			})
			.forEach((subCmd) -> {
				addSubCommand(subCmd);
			});
		return this;
	}

	public Collection<SyntaxDocumentation> getSyntaxes() {
		return syntaxes;
	}
}