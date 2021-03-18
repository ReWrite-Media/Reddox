package net.minestom.script;

import org.graalvm.polyglot.proxy.ProxyObject;
import org.jetbrains.annotations.Nullable;

@FunctionalInterface
public interface CommandFunction {
    @Nullable
    ProxyObject run(@Nullable Object... args);
}
