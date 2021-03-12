package net.minestom.script.property;

import net.minestom.server.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class EntityProperty extends Properties {

    private final UUID uuid;

    public EntityProperty(@NotNull Entity entity) {
        this.uuid = entity.getUuid();
        putMember("uuid", entity.getUuid().toString());
        putMember("type", entity.getEntityType().toString());
        putMember("position", new PositionProperty(entity.getPosition()));
    }

    @Override
    public String toString() {
        return uuid.toString();
    }
}
