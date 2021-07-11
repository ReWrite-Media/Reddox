package net.minestom.script.property;

import net.minestom.server.coordinate.Pos;
import org.jetbrains.annotations.NotNull;

public class PositionProperty extends Properties {

    private final Pos position;

    public PositionProperty(@NotNull Pos position) {
        this.position = position;

        Properties.applyExtensions(PositionProperty.class, position, this);
        putMember("x", position.x());
        putMember("y", position.y());
        putMember("z", position.z());

        putMember("yaw", position.yaw());
        putMember("pitch", position.pitch());
    }

    @Override
    public String toString() {
        return position.toString();
    }
}
