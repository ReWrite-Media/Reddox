package net.minestom.script;

import net.minestom.script.object.Properties;
import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface SignalCallback {
    // TODO return boolean
    void accept(@NotNull Properties properties);
}
