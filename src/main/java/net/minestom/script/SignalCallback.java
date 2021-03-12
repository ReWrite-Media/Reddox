package net.minestom.script;

import net.minestom.script.property.Properties;
import org.graalvm.polyglot.proxy.ProxyObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@FunctionalInterface
public interface SignalCallback {
    @Nullable
    ProxyObject accept(@NotNull Properties properties);
}
