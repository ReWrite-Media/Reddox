package net.minestom.script.property;

import net.minestom.script.utils.NbtConversionUtils;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.Player;
import net.minestom.server.instance.Instance;
import net.minestom.server.item.ItemStack;
import net.minestom.server.particle.Particle;
import net.minestom.server.utils.BlockPosition;
import net.minestom.server.utils.Position;
import org.graalvm.polyglot.Value;
import org.graalvm.polyglot.proxy.ProxyObject;
import org.jetbrains.annotations.NotNull;
import org.jglrxavpok.hephaistos.nbt.NBT;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

/**
 * Represents a list of properties to be forwarded and processed by scripts.
 */
public class Properties implements ProxyObject {

    private static final Map<Class<?>, List<BiConsumer<Object, Properties>>> EXTENSIONS = new ConcurrentHashMap<>();

    // Logic from https://github.com/oracle/graaljs/issues/281
    private static final String TO_STRING_MEMBER = "toString";
    public static final String TYPE_MEMBER = "_type";

    private final Map<String, Value> properties = new ConcurrentHashMap<>();

    public Properties() {
        putMember(TYPE_MEMBER, getClass().getSimpleName());
    }

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
        putMember(key, toValue(object));
    }

    public static synchronized <T> void applyExtensions(@NotNull Class<?> type, @NotNull T value, @NotNull Properties properties) {
        var consumers = EXTENSIONS.get(type);
        if (consumers == null || consumers.isEmpty()) {
            return;
        }
        consumers.forEach(consumer -> consumer.accept(value, properties));
    }

    public static synchronized <T> void registerExtension(@NotNull Class<?> type, @NotNull Class<T> valueClass,
                                                          @NotNull BiConsumer<T, Properties> consumer) {
        var consumers = EXTENSIONS.computeIfAbsent(type,
                aClass -> new ArrayList<>());
        consumers.add((BiConsumer<Object, Properties>) consumer);
    }

    public static @NotNull Properties fromEntity(@NotNull Entity entity) {
        if (entity instanceof Player) {
            return new PlayerProperty((Player) entity);
        }
        return new EntityProperty(entity);
    }

    private static @NotNull Value toValue(@NotNull Object object) {
        Value value = null;
        if (object instanceof NBT) {
            value = NbtConversionUtils.toValue((NBT) object);
        } else if (object instanceof List) {
            List<?> objects = (List<?>) object;
            Value[] values = new Value[objects.size()];
            for (int i = 0; i < objects.size(); i++) {
                values[i] = toValue(objects.get(i));
            }
            value = Value.asValue(values);
        } else if (object instanceof Entity) {
            value = Value.asValue(fromEntity((Entity) object));
        } else if (object instanceof BlockPosition) {
            value = Value.asValue(new BlockPositionProperty((BlockPosition) object));
        } else if (object instanceof Position) {
            value = Value.asValue(new PositionProperty((Position) object));
        } else if (object instanceof ItemStack) {
            value = Value.asValue(new ItemProperty((ItemStack) object));
        } else if (object instanceof Instance) {
            value = Value.asValue(new WorldProperty((Instance) object));
        } else if (object instanceof Particle) {
            value = Value.asValue(((Particle) object).getNamespaceID());
        }
        if (value == null) {
            // Custom property does not exist, cast to value
            value = Value.asValue(object);
        }
        return value;
    }
}
