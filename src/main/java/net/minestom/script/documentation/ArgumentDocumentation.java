package net.minestom.script.documentation;

import net.minestom.server.command.builder.arguments.ArgumentEnum;

public class ArgumentDocumentation extends Documentation {
	
	String name;
	String description;
	String[] examples;
	boolean recurring = false;
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

	public static <E> String[] enumStrings(Class<E> clazz) {
		return enumStrings(clazz, ArgumentEnum.Format.DEFAULT);
	}
	
	public static <E> String[] enumStrings(Class<E> clazz, ArgumentEnum.Format format) {
		E[] constants = clazz.getEnumConstants();
		
		assert(constants != null);
		
		String[] arr = new String[constants.length];
		
		for (int i = 0; i < constants.length; i++) {
			String name = ((Enum<?>) constants[i]).name();
			
			switch(format) {
				case LOWER_CASED:
					name = name.toLowerCase();
					break;
				case UPPER_CASED:
					name = name.toUpperCase();
					break;
				default:
					break;
			}
			
			arr[i] = name;
		}
		
		return arr;
	}

	public ArgumentDocumentation setRecurring(boolean recurring) {
		this.recurring = recurring;
		return this;
	}
}