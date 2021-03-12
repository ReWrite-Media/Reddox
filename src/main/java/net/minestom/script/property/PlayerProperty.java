package net.minestom.script.property;

import net.minestom.server.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PlayerProperty extends EntityProperty {

    public PlayerProperty(@NotNull Player player) {
        super(player);
        putMember("username", player.getUsername());
    }
}
