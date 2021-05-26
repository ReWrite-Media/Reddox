package net.minestom.script.property;

import net.minestom.server.utils.BlockPosition;
import org.jetbrains.annotations.NotNull;

public class BlockPositionProperty extends Properties {

    public BlockPositionProperty(@NotNull BlockPosition blockPosition) {
        Properties.applyExtensions(BlockPositionProperty.class, blockPosition, this);
        putMember("x", blockPosition.getX());
        putMember("y", blockPosition.getY());
        putMember("z", blockPosition.getZ());
    }

    @Override
    public String toString() {
        // Command-friendly conversion to be used as a position argument
        return getMember("x") + " " +
                getMember("y") + " " +
                getMember("z");
    }
}
