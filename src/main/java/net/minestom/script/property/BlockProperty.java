package net.minestom.script.property;

import net.minestom.server.instance.block.Block;
import net.minestom.server.utils.BlockPosition;
import org.jetbrains.annotations.NotNull;

public class BlockProperty extends Properties {

    private final Block block;

    public BlockProperty(Block block, @NotNull BlockPosition blockPosition) {
        this.block = block;
        Properties.applyExtensions(BlockProperty.class, block, this);

        Properties properties = new Properties();
        {
            var propertiesMap = block.properties();
            propertiesMap.forEach(properties::putMember);
        }

        putMember("type", block.toString());
        putMember("position", new BlockPositionProperty(blockPosition));
        putMember("properties", properties);
    }

    @Override
    public String toString() {
        return block.toString();
    }
}
