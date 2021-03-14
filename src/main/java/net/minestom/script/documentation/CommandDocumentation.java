package net.minestom.script.documentation;

import java.util.ArrayList;
import java.util.List;

public class CommandDocumentation extends Documentation {
	
	String command;
	List<SyntaxDocumentation> syntaxes = new ArrayList<SyntaxDocumentation>();
	
	public CommandDocumentation(String command) {
		this.command = command;
	}
	
	public CommandDocumentation addSyntax(SyntaxDocumentation syntax) {
		syntaxes.add(syntax);
		return this;
	}
}