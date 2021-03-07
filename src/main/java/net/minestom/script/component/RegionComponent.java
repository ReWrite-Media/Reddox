package net.minestom.script.component;

import net.minestom.server.utils.Position;
import net.minestom.server.utils.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jglrxavpok.hephaistos.nbt.NBTCompound;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RegionComponent extends ScriptComponent {

    private final Map<String, Region> regionMap = new ConcurrentHashMap<>();

    protected RegionComponent() {
        // TODO signals
    }

    @Nullable
    public Region createRegion(String identifier, Vector pos1, Vector pos2, NBTCompound nbtCompound) {
        if (regionMap.containsKey(identifier)) {
            return null;
        }

        final double minX = Math.min(pos1.getX(), pos2.getX());
        final double minY = Math.min(pos1.getY(), pos2.getY());
        final double minZ = Math.min(pos1.getZ(), pos2.getZ());

        final double maxX = Math.max(pos1.getX(), pos2.getX());
        final double maxY = Math.max(pos1.getY(), pos2.getY());
        final double maxZ = Math.max(pos1.getZ(), pos2.getZ());

        Region region = new Region(identifier,
                new Vector(minX, minY, minZ),
                new Vector(maxX, maxY, maxZ),
                nbtCompound);

        this.regionMap.put(identifier, region);
        return region;
    }

    public boolean deleteRegion(String identifier) {
        return regionMap.remove(identifier) != null;
    }

    @Nullable
    public Region getRegion(String identifier) {
        return regionMap.get(identifier);
    }

    public static class Region {
        private String identifier;
        private Vector minPos, maxPos;
        private NBTCompound nbtCompound;

        protected Region(String identifier, Vector minPos, Vector maxPos, NBTCompound nbtCompound) {
            this.identifier = identifier;
            this.minPos = minPos;
            this.maxPos = maxPos;
            this.nbtCompound = nbtCompound;
        }

        public boolean isInside(@NotNull Vector vector) {
            final double x = vector.getX();
            final double y = vector.getY();
            final double z = vector.getZ();
            return x >= minPos.getX() && x <= maxPos.getX() &&
                    y >= minPos.getY() && y <= maxPos.getY() &&
                    z >= minPos.getZ() && z <= maxPos.getZ();
        }

        public boolean isInside(@NotNull Position position) {
            return isInside(position.toVector());
        }

        public String getIdentifier() {
            return identifier;
        }

        public Vector getMinPos() {
            return minPos;
        }

        public Vector getMaxPos() {
            return maxPos;
        }

        public NBTCompound getNbtCompound() {
            return nbtCompound;
        }

    }

}
