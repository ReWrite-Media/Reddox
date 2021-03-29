package net.minestom.script;

import org.graalvm.polyglot.proxy.ProxyObject;
import org.jetbrains.annotations.Nullable;

@FunctionalInterface
public interface CommandMapper {
    CommandMapper DEFAULT = input -> input;

    @Nullable
    ProxyObject map(@Nullable ProxyObject input);
}
