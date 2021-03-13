package net.minestom.script;

import net.minestom.script.property.Properties;
import org.graalvm.polyglot.proxy.ProxyObject;
import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface SignalCallback {
    void accept(@NotNull Properties properties, @NotNull ProxyObject output);
}
