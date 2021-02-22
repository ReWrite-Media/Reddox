package net.minestom.script.object;

import net.minestom.server.utils.Position;
import org.jetbrains.annotations.NotNull;

public class PositionProperty extends Properties {

    public PositionProperty(@NotNull Position position) {
        putMember("x", position.getX());
        putMember("y", position.getY());
        putMember("z", position.getZ());

        putMember("yaw", position.getYaw());
        putMember("pitch", position.getPitch());
    }
}
