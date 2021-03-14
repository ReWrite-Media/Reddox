package net.minestom.script.documentation;

public class ArgumentDocumentation extends Documentation {
	
	String name;
	String description;
	String[] examples;
	ArgumentDocumentationType type;
	
	public ArgumentDocumentation setName(String name) {
		this.name = name;
		return this;
	}
	
	public ArgumentDocumentation setDescription(String description) {
		this.description = description;
		return this;
	}
	
	public ArgumentDocumentation setType(ArgumentDocumentationType type) {
		this.type = type;
		return this;
	}
	
	public ArgumentDocumentation setExamples(String... examples) {
		this.examples = examples;
		return this;
	}
}