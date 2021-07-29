package net.minestom.script.component;

import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Vec;
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
    public Region createRegion(String identifier, Point pos1, Point pos2, NBTCompound nbtCompound) {
        if (regionMap.containsKey(identifier)) {
            return null;
        }

        final double minX = Math.min(pos1.x(), pos2.x());
        final double minY = Math.min(pos1.y(), pos2.y());
        final double minZ = Math.min(pos1.z(), pos2.z());

        final double maxX = Math.max(pos1.x(), pos2.x());
        final double maxY = Math.max(pos1.y(), pos2.y());
        final double maxZ = Math.max(pos1.z(), pos2.z());

        Region region = new Region(identifier,
                new Vec(minX, minY, minZ),
                new Vec(maxX, maxY, maxZ),
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
        private final String identifier;
        private final Point minPos, maxPos;
        private final NBTCompound nbtCompound;

        protected Region(String identifier, Point minPos, Point maxPos, NBTCompound nbtCompound) {
            this.identifier = identifier;
            this.minPos = minPos;
            this.maxPos = maxPos;
            this.nbtCompound = nbtCompound;
        }

        public boolean isInside(@NotNull Point vector) {
            final double x = vector.x();
            final double y = vector.y();
            final double z = vector.z();
            return x >= minPos.x() && x <= maxPos.x() &&
                    y >= minPos.y() && y <= maxPos.y() &&
                    z >= minPos.z() && z <= maxPos.z();
        }

        public String getIdentifier() {
            return identifier;
        }

        public Point getMinPos() {
            return minPos;
        }

        public Point getMaxPos() {
            return maxPos;
        }

        public NBTCompound getNbtCompound() {
            return nbtCompound;
        }

    }
}
