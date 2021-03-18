package net.minestom.script.property;

import java.util.UUID;

import net.minestom.server.instance.Instance;

public class WorldProperty extends Properties {

	private final UUID uuid;
	
	public WorldProperty(Instance instance) {
		this.uuid = instance.getUniqueId();
		putMember("uuid", uuid.toString());
	}

    @Override
    public String toString() {
        return uuid.toString();
    }
}
