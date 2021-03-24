package net.minestom.script.property;

import net.minestom.server.entity.Entity;
import net.minestom.server.entity.Player;
import org.graalvm.polyglot.Value;
import org.graalvm.polyglot.proxy.ProxyObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jglrxavpok.hephaistos.nbt.*;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

/**
 * Represents a list of properties to be forwarded and processed by scripts.
 */
public class Properties implements ProxyObject {

    // Logic from https://github.com/oracle/graaljs/issues/281
    private static final String TO_STRING_MEMBER = "toString";

    private final Map<String, Value> properties = new ConcurrentHashMap<>();

    @Override
    public Object getMember(String key) {
        if (key.equals(TO_STRING_MEMBER)) {
            return (Supplier<String>) this::toString;
        }

        return properties.get(key);
    }

    @Override
    public Object getMemberKeys() {
        return properties.keySet().toArray(new String[0]);
    }

    @Override
    public boolean hasMember(String key) {
        if (key.equals(TO_STRING_MEMBER)) {
            return true;
        }

        return properties.containsKey(key);
    }

    @Override
    public void putMember(String key, Value value) {
        this.properties.put(key, value);
    }

    public void putMember(String key, Object object) {
        final Value value;
        if (object instanceof NBT) {
            value = toValue((NBT) object);
        } else {
            value = Value.asValue(object);
        }
        putMember(key, value);
    }

    @NotNull
    public static Properties fromEntity(@NotNull Entity entity) {
        if (entity instanceof Player) {
            return new PlayerProperty((Player) entity);
        }

        return new EntityProperty(entity);
    }

    @Nullable
    private static Value toValue(@NotNull NBT nbt) {
        Object object = null;
        if (nbt instanceof NBTNumber) {
            object = ((NBTNumber<?>) nbt).getValue();
        } else if (nbt instanceof NBTString) {
            object = ((NBTString) nbt).getValue();
        } else if (nbt instanceof NBTList) {
            NBTList<NBT> list = (NBTList<NBT>) nbt;
            Value[] array = new Value[list.getLength()];
            for (int i = 0; i < array.length; i++) {
                NBT listElement = list.get(i);
                array[i] = toValue(listElement);
            }
            object = array;
        } else if (nbt instanceof NBTCompound) {
            NBTCompound compound = (NBTCompound) nbt;
            Properties properties = new Properties();
            for (String key : compound.getKeys()) {
                final NBT value = compound.get(key);
                assert value != null;
                properties.putMember(key, value);
            }
            object = properties;
        }

        if (object == null) {
            return null;
        }

        return Value.asValue(object);
    }

}
