package net.minestom.script;

import net.minestom.script.property.Properties;
import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface FunctionCallback {
    void accept(@NotNull Properties properties);
}
