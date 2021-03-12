package net.minestom.script.property;

import net.minestom.server.utils.BlockPosition;
import org.jetbrains.annotations.NotNull;

public class BlockPositionProperty extends Properties {

    private final BlockPosition blockPosition;

    public BlockPositionProperty(@NotNull BlockPosition blockPosition) {
        this.blockPosition = blockPosition;

        putMember("x", blockPosition.getX());
        putMember("y", blockPosition.getY());
        putMember("z", blockPosition.getZ());
    }

    @Override
    public String toString() {
        // Command-friendly conversion to be used as a position argument
        return blockPosition.getX() + " " + blockPosition.getY() + " " + blockPosition.getZ();
    }
}
