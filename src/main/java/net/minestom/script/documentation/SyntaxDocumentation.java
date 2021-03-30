package net.minestom.script.documentation;

import java.util.ArrayList;
import java.util.List;

public class SyntaxDocumentation extends Documentation {
	
	List<ArgumentDocumentation> arguments = new ArrayList<ArgumentDocumentation>();
	String description;
	
	public SyntaxDocumentation setArguments(ArgumentDocumentation... arguments) {
		for (ArgumentDocumentation argument : arguments)
			this.arguments.add(argument);
		return this;
	}

	public SyntaxDocumentation setDescription(String description) {
		this.description = description;
		return this;
	}
}