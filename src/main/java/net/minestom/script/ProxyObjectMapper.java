package net.minestom.script;

import org.graalvm.polyglot.Value;
import org.graalvm.polyglot.proxy.ProxyObject;
import org.jetbrains.annotations.Nullable;

@FunctionalInterface
public interface ProxyObjectMapper {
    ProxyObjectMapper DEFAULT = Value::asValue;

    @Nullable
    Value map(@Nullable ProxyObject input);
}
