package net.minestom.script.object;

import net.minestom.server.entity.Player;
import org.graalvm.polyglot.Value;
import org.jetbrains.annotations.NotNull;

public class PlayerProperty extends Properties {

    public PlayerProperty(@NotNull Player player) {
        putMember("username", Value.asValue(player.getUsername()));
        putMember("uuid", Value.asValue(player.getUuid().toString()));
    }

}
