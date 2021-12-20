package net.minestom.script.utils;

import net.minestom.script.property.Properties;
import org.graalvm.polyglot.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jglrxavpok.hephaistos.nbt.*;
import org.jglrxavpok.hephaistos.nbt.mutable.MutableNBTCompound;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

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
            Value[] array = new Value[list.getSize()];
            for (int i = 0; i < array.length; i++) {
                NBT listElement = list.get(i);
                array[i] = toValue(listElement);
            }
            object = array;
        } else if (nbt instanceof NBTCompound compound) {
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
        MutableNBTCompound compound = new MutableNBTCompound();
        map.forEach((key, value) -> {
            NBTConverter<?> converter = getConverter(value);
            compound.set(key, converter.makeNBT());
        });

        return compound.toCompound();
    }

    public static NBTList<?> fromList(@NotNull List<Object> list) {
        NBTType<?> type = null;
        List<NBT> nbtList = new ArrayList<>();
        for (Object value : list) {
            NBTConverter<?> converter = getConverter(value);
            if (type == null) {
                // Use first element of the list as the list's type
                type = converter.type();
            }
            nbtList.add(converter.makeNBT());
        }

        return NBT.List(type, nbtList);
    }

    @NotNull
    private static NBTConverter<?> getConverter(@NotNull Object value) {
        // TODO byte/int/long arrays
        if (value instanceof Byte) {
            return new NBTConverter<>((Byte) value, NBTType.TAG_Byte, NBTByte::new);
        } else if (value instanceof Short) {
            return new NBTConverter<>((Short) value, NBTType.TAG_Short, NBTShort::new);
        } else if (value instanceof Integer) {
            return new NBTConverter<>((Integer) value, NBTType.TAG_Int, NBTInt::new);
        } else if (value instanceof Long) {
            return new NBTConverter<>((Long) value, NBTType.TAG_Long, NBTLong::new);
        } else if (value instanceof Float) {
            return new NBTConverter<>((Float) value, NBTType.TAG_Float, NBTFloat::new);
        } else if (value instanceof Double) {
            return new NBTConverter<>((Double) value, NBTType.TAG_Double, NBTDouble::new);
        } else if (value instanceof String) {
            return new NBTConverter<>((String) value, NBTType.TAG_String, NBTString::new);
        } else if (value instanceof List) {
            return new NBTConverter<>((List) value, NBTType.TAG_List, NbtConversionUtils::fromList);
        } else if (value instanceof NBTCompound) {
            return new NBTConverter<>((NBTCompound) value, NBTType.TAG_Compound, nbtCompound -> nbtCompound);
        }

        throw new IllegalArgumentException("Type " + value.getClass() + " is not an expected nbt value");
    }

    private record NBTConverter<T>(T value, NBTType<?> type,
                                   Function<T, NBT> converter) {
        public NBT makeNBT() {
            return converter.apply(value);
        }
    }

}
