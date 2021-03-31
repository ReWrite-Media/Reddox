package net.minestom.script.documentation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SyntaxDocumentation extends Documentation {
    List<ArgumentDocumentation> arguments = new ArrayList<ArgumentDocumentation>();

    public SyntaxDocumentation setArguments(ArgumentDocumentation... arguments) {
        this.arguments.addAll(Arrays.asList(arguments));
        return this;
    }
}