package net.minestom.script.utils;

import net.minestom.script.property.Properties;
import org.graalvm.polyglot.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jglrxavpok.hephaistos.nbt.*;

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
            NBTConverter<?> converter = getConverter(value);
            compound.set(key, converter.makeNBT());
        });

        return compound;
    }

    public static NBTList<?> fromList(@NotNull List<Object> list) {
        NBTList<NBT> nbtList = null;

        for (Object value : list) {
            NBTConverter<?> converter = getConverter(value);
            if (nbtList == null) {
                // Use first element of the list as the list's type
                nbtList = new NBTList<>(converter.getType());
            }
            nbtList.add(converter.makeNBT());
        }

        return nbtList;
    }

    @NotNull
    private static NBTConverter<?> getConverter(@NotNull Object value) {
        // TODO byte/int/long arrays
        if (value instanceof Byte) {
            return new NBTConverter<>((Byte) value, NBTTypes.TAG_Byte, NBTByte::new);
        } else if (value instanceof Short) {
            return new NBTConverter<>((Short) value, NBTTypes.TAG_Short, NBTShort::new);
        } else if (value instanceof Integer) {
            return new NBTConverter<>((Integer) value, NBTTypes.TAG_Int, NBTInt::new);
        } else if (value instanceof Long) {
            return new NBTConverter<>((Long) value, NBTTypes.TAG_Long, NBTLong::new);
        } else if (value instanceof Float) {
            return new NBTConverter<>((Float) value, NBTTypes.TAG_Float, NBTFloat::new);
        } else if (value instanceof Double) {
            return new NBTConverter<>((Double) value, NBTTypes.TAG_Double, NBTDouble::new);
        } else if (value instanceof String) {
            return new NBTConverter<>((String) value, NBTTypes.TAG_String, NBTString::new);
        } else if (value instanceof List) {
            return new NBTConverter<>((List) value, NBTTypes.TAG_List, NbtConversionUtils::fromList);
        } else if (value instanceof NBTCompound) {
            return new NBTConverter<>((NBTCompound) value, NBTTypes.TAG_Compound, nbtCompound -> nbtCompound);
        }

        throw new IllegalArgumentException("Type " + value.getClass() + " is not an expected nbt value");
    }

    private static class NBTConverter<T> {
        private final T value;
        private final int type;
        private final Function<T, NBT> converter;

        private NBTConverter(T value, int type, Function<T, NBT> converter) {
            this.value = value;
            this.type = type;
            this.converter = converter;
        }

        public int getType() {
            return type;
        }

        public NBT makeNBT() {
            return converter.apply(value);
        }
    }

}
