package net.minestom.script.utils;

import net.minestom.server.utils.Position;
import org.jetbrains.annotations.NotNull;
import org.jglrxavpok.hephaistos.nbt.NBTCompound;

/**
 * Serializes java objects into something understandable by any languages.
 */
public class PropertySerializer {

    @NotNull
    public static NBTCompound fromPosition(@NotNull Position position) {
        NBTCompound nbtCompound = new NBTCompound();
        nbtCompound.setDouble("x", position.getX());
        nbtCompound.setDouble("y", position.getY());
        nbtCompound.setDouble("z", position.getZ());

        nbtCompound.setFloat("yaw", position.getYaw());
        nbtCompound.setFloat("pitch", position.getPitch());

        return nbtCompound;
    }

}
