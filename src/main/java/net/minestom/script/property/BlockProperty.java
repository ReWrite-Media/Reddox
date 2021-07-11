package net.minestom.script.property;

import net.minestom.server.coordinate.Point;
import net.minestom.server.instance.block.Block;
import org.jetbrains.annotations.NotNull;

public class BlockProperty extends Properties {

    private final Block block;

    public BlockProperty(Block block, @NotNull Point blockPosition) {
        this.block = block;
        Properties.applyExtensions(BlockProperty.class, block, this);

        putMember("type", block.toString());
        putMember("position", new BlockPositionProperty(blockPosition));
        putMember("properties", block.properties());
        putMember("nbt", block.nbt());
    }

    @Override
    public String toString() {
        return block.toString();
    }
}
