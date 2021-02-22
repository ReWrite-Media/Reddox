package net.minestom.script.utils;

import net.minestom.script.ScriptProperties;
import net.minestom.server.utils.Position;
import org.graalvm.polyglot.Value;
import org.jetbrains.annotations.NotNull;

/**
 * Serializes java objects into something understandable by any languages.
 */
public class PropertySerializer {

    @NotNull
    public static Value fromPosition(@NotNull Position position) {
        ScriptProperties scriptProperties = new ScriptProperties();
        scriptProperties.putMember("x", Value.asValue(position.getX()));
        scriptProperties.putMember("y", Value.asValue(position.getY()));
        scriptProperties.putMember("z", Value.asValue(position.getZ()));

        scriptProperties.putMember("yaw", Value.asValue(position.getYaw()));
        scriptProperties.putMember("pitch", Value.asValue(position.getPitch()));

        return Value.asValue(scriptProperties);
    }

}
