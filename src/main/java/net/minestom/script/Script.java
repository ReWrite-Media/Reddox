package net.minestom.script;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Source;
import org.graalvm.polyglot.Value;
import org.jetbrains.annotations.NotNull;

public class Script {

    private final Source source;
    private Value script;

    public Script(@NotNull Source source) {
        this.source = source;
    }

    public void eval(Context context) {
        this.script = context.eval(source);
    }

    @NotNull
    public Source getSource() {
        return source;
    }
}
