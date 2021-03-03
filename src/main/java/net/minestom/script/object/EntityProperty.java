package net.minestom.script.object;

import net.minestom.server.entity.Entity;
import org.jetbrains.annotations.NotNull;

public class EntityProperty extends Properties {

    public EntityProperty(@NotNull Entity entity) {
        putMember("uuid", entity.getUuid().toString());
        putMember("type", entity.getEntityType().toString());
    }

}
