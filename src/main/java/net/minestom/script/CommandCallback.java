package net.minestom.script;

import net.minestom.script.property.PlayerProperty;
import net.minestom.script.property.Properties;
import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface CommandCallback {
    void accept(@NotNull PlayerProperty sender, @NotNull Properties context);
}
