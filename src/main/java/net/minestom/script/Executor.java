package net.minestom.script;

import org.graalvm.polyglot.proxy.ProxyObject;
import org.jetbrains.annotations.NotNull;

public interface Executor {

    ProxyObject run(Object... args);

    @NotNull
    CommandFunction make(@NotNull String alias, @NotNull ProxyObjectMapper mapper);

    @NotNull
    default CommandFunction make(@NotNull String string) {
        return make(string, ProxyObjectMapper.DEFAULT);
    }

}
