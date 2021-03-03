package net.minestom.script.object;

import net.minestom.server.utils.Position;
import org.jetbrains.annotations.NotNull;

public class PositionProperty extends Properties {

    private final Position position;

    public PositionProperty(@NotNull Position position) {
        this.position = position;

        putMember("x", position.getX());
        putMember("y", position.getY());
        putMember("z", position.getZ());

        putMember("yaw", position.getYaw());
        putMember("pitch", position.getPitch());
    }

    @Override
    public String toString() {
        return position.toString();
    }
}
