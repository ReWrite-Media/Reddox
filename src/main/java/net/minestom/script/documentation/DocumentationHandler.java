package net.minestom.script.documentation;

import java.util.ArrayList;
import java.util.List;

import net.minestom.server.MinecraftServer;
import net.minestom.server.command.builder.Command;

public class DocumentationHandler {

	public static final DocumentationHandler INSTANCE = new DocumentationHandler();
	
	private static List<CommandDocumentation> commands = new ArrayList<CommandDocumentation>();
	
	/**
	 * Registers a command, and loads its documentation if applicable
	 * @param command
	 */
	public void registerCommand(Command command) {
		MinecraftServer.getCommandManager().register(command);
		
		if (command instanceof Documented<?>) {
			
			Documented<?> documented = (Documented<?>) command;
			
			CommandDocumentation doc = (CommandDocumentation) documented.getDocumentation();
			
			commands.add(doc);
		}
	}
	
	public void processDocumentation() {
		
	}
}
