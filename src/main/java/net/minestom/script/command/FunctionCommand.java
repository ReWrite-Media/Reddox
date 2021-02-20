package net.minestom.script.command;

import net.minestom.script.ScriptManager;
import net.minestom.script.ScriptProperties;
import net.minestom.server.command.builder.Arguments;
import net.minestom.server.command.builder.Command;
import org.graalvm.polyglot.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jglrxavpok.hephaistos.nbt.NBT;
import org.jglrxavpok.hephaistos.nbt.*;

import java.lang.String;
import java.util.List;

import static net.minestom.server.command.builder.arguments.ArgumentType.*;

public class FunctionCommand extends Command {
    public FunctionCommand() {
        super("function");

        setDefaultExecutor((sender, args) -> sender.sendMessage("Usage: /function run <name> [properties...]"));

        addSyntax((sender, args) -> {
            final String name = args.get("function_name");
            final List<Arguments> loopArguments = args.get("properties");

            // Build the properties object
            ScriptProperties properties = new ScriptProperties();
            for (Arguments property : loopArguments) {
                final String key = property.get("key");
                final NBT nbt = property.get("value");

                Value value = Value.asValue(toObject(nbt));
                if (value == null)
                    continue;

                properties.putMember(key, value);
            }

            ScriptManager.EXECUTOR.runFunction(name, properties);

            sender.sendMessage("You executed the function: " + name);
        }, Literal("run"), Word("function_name"), Loop("properties",
                Group("properties_group", Word("key"), NBT("value"))));
    }

    @Nullable
    private static Object toObject(@NotNull NBT nbt) {
        if (nbt instanceof NBTNumber) {
            return ((NBTNumber<?>) nbt).getValue();
        } else if (nbt instanceof NBTString) {
            return ((NBTString) nbt).getValue();
        } else if (nbt instanceof NBTList) {
            NBTList<NBT> list = (NBTList<NBT>) nbt;
            Object[] array = new Object[list.getLength()];
            for (int i = 0; i < array.length; i++) {
                NBT listElement = list.get(i);
                array[i] = toObject(listElement);
            }
            return array;
        } else if (nbt instanceof NBTCompound) {
            NBTCompound compound = (NBTCompound) nbt;
            ScriptProperties properties = new ScriptProperties();
            for (String key : compound.getKeys()) {
                final NBT value = compound.get(key);
                assert value != null;
                properties.putMember(key, Value.asValue(toObject(value)));
            }
            return properties;
        }

        // Unexpected
        return null;
    }
}
