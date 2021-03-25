package net.minestom.script.property;

import net.minestom.script.utils.NbtConversionUtils;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.Player;
import org.graalvm.polyglot.Value;
import org.graalvm.polyglot.proxy.ProxyObject;
import org.jetbrains.annotations.NotNull;
import org.jglrxavpok.hephaistos.nbt.NBT;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

/**
 * Represents a list of properties to be forwarded and processed by scripts.
 */
public class Properties implements ProxyObject {

    // Logic from https://github.com/oracle/graaljs/issues/281
    private static final String TO_STRING_MEMBER = "toString";
    public static final String TYPE_MEMBER = "_type";

    private final Map<String, Value> properties = new ConcurrentHashMap<>();

    @Override
    public Object getMember(String key) {
        if (key.equals(TO_STRING_MEMBER)) {
            return (Supplier<String>) this::toString;
        }
        if (key.equals(TYPE_MEMBER)) {
            return getClass().getSimpleName();
        }

        return properties.get(key);
    }

    @Override
    public Object getMemberKeys() {
        return properties.keySet().toArray(new String[0]);
    }

    @Override
    public boolean hasMember(String key) {
        if (key.equals(TO_STRING_MEMBER)
                || key.equals(TYPE_MEMBER)) {
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
            value = NbtConversionUtils.toValue((NBT) object);
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

}
