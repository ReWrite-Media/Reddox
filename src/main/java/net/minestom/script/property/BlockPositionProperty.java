package net.minestom.script.property;

import net.minestom.server.coordinate.Point;
import org.jetbrains.annotations.NotNull;

public class BlockPositionProperty extends Properties {

    public BlockPositionProperty(@NotNull Point blockPosition) {
        Properties.applyExtensions(BlockPositionProperty.class, blockPosition, this);
        putMember("x", blockPosition.blockX());
        putMember("y", blockPosition.blockY());
        putMember("z", blockPosition.blockZ());
    }

    @Override
    public String toString() {
        // Command-friendly conversion to be used as a position argument
        return getMember("x") + " " +
                getMember("y") + " " +
                getMember("z");
    }
}
