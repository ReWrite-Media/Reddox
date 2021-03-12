package net.minestom.script.utils;

import net.minestom.script.property.Properties;
import net.minestom.server.utils.Position;
import org.graalvm.polyglot.Value;
import org.jetbrains.annotations.NotNull;

/**
 * Serializes java objects into something understandable by any languages.
 */
public class PropertySerializer {

    @NotNull
    public static Value fromPosition(@NotNull Position position) {
        Properties properties = new Properties();
        properties.putMember("x", Value.asValue(position.getX()));
        properties.putMember("y", Value.asValue(position.getY()));
        properties.putMember("z", Value.asValue(position.getZ()));

        properties.putMember("yaw", Value.asValue(position.getYaw()));
        properties.putMember("pitch", Value.asValue(position.getPitch()));

        return Value.asValue(properties);
    }

}
