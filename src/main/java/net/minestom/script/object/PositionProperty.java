package net.minestom.script.object;

import net.minestom.server.utils.Position;
import org.graalvm.polyglot.Value;
import org.jetbrains.annotations.NotNull;

public class PositionProperty extends Properties {

    public PositionProperty(@NotNull Position position) {
        putMember("x", Value.asValue(position.getX()));
        putMember("y", Value.asValue(position.getY()));
        putMember("z", Value.asValue(position.getZ()));

        putMember("yaw", Value.asValue(position.getYaw()));
        putMember("pitch", Value.asValue(position.getPitch()));
    }
}
