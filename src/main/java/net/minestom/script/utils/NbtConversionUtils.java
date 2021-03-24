package net.minestom.script.utils;

import net.minestom.script.property.Properties;
import org.graalvm.polyglot.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jglrxavpok.hephaistos.nbt.*;

import java.util.Map;

public class NbtConversionUtils {

    @Nullable
    public static Value toValue(@NotNull NBT nbt) {
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

    @NotNull
    public static NBTCompound fromMap(@NotNull Map<String, Object> map) {
        NBTCompound compound = new NBTCompound();

        map.forEach((key, value) -> {
            // FIXME: graaljs provides an empty map instead of lists
            if (value instanceof Integer) {
                compound.setInt(key, (Integer) value);
            } else if (value instanceof Double) {
                compound.setDouble(key, (Double) value);
            } else if (value instanceof String) {
                compound.setString(key, (String) value);
            } else if (value instanceof Map) {
                compound.set(key, fromMap((Map<String, Object>) value));
            } else {
                System.out.println("TODO NBT TYPE: " + value.getClass());
            }

        });

        return compound;
    }

}
