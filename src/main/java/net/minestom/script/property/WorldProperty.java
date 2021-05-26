package net.minestom.script.property;

import net.minestom.server.instance.Instance;

import java.util.UUID;

public class WorldProperty extends Properties {

    private final UUID uuid;

    public WorldProperty(Instance instance) {
        this.uuid = instance.getUniqueId();
        Properties.applyExtensions(WorldProperty.class, instance, this);
        putMember("uuid", uuid.toString());
    }

    @Override
    public String toString() {
        return uuid.toString();
    }
}
