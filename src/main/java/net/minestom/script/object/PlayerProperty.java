package net.minestom.script.object;

import net.minestom.server.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PlayerProperty extends Properties {

    public PlayerProperty(@NotNull Player player) {
        putMember("username", player.getUsername());
        putMember("uuid", player.getUuid().toString());
    }

}
