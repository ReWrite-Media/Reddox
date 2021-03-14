package net.minestom.script.documentation;

import java.util.ArrayList;
import java.util.List;

public class SyntaxDocumentation extends Documentation {
	List<ArgumentDocumentation> arguments = new ArrayList<ArgumentDocumentation>();
	
	public SyntaxDocumentation setArguments(ArgumentDocumentation... arguments) {
		for (ArgumentDocumentation argument : arguments)
			this.arguments.add(argument);
		return this;
	}
}