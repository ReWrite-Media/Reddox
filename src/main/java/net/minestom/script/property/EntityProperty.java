package net.minestom.script.property;

import java.util.UUID;

import org.jetbrains.annotations.NotNull;

import net.minestom.server.entity.Entity;

public class EntityProperty extends Properties {

    private final UUID uuid;

    public EntityProperty(@NotNull Entity entity) {
        this.uuid = entity.getUuid();
        putMember("uuid", entity.getUuid().toString());
        putMember("type", entity.getEntityType().toString());
        putMember("position", new PositionProperty(entity.getPosition()));
        putMember("world", new WorldProperty(entity.getInstance()));
    }

    @Override
    public String toString() {
        return uuid.toString();
    }
}