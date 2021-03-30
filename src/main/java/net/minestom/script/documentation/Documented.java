package net.minestom.script.documentation;

public interface Documented<E extends Documentation> {
	/**
	 * Gets the documentation object associated with this script-facing object
	 * 
	 * @return Documentation the documentation object
	 */
	public E getDocumentation();
}