package net.minestom.script.command.arguments;

import com.google.gson.stream.JsonReader;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.minestom.server.command.builder.NodeMaker;
import net.minestom.server.command.builder.arguments.Argument;
import net.minestom.server.command.builder.arguments.ArgumentString;
import net.minestom.server.command.builder.exception.ArgumentSyntaxException;
import net.minestom.server.network.packet.server.play.DeclareCommandsPacket;
import net.minestom.server.utils.binary.BinaryWriter;
import org.jetbrains.annotations.NotNull;

import java.io.StringReader;

/**
 * Component argument accepting JSON or MiniMessage input.
 */
public class ArgumentFlexibleComponent extends Argument<Component> {

    private static final MiniMessage MINI_MESSAGE = MiniMessage.get();

    private final boolean infinite;

    public ArgumentFlexibleComponent(@NotNull String id, boolean infinite) {
        super(id, true, infinite);
        this.infinite = infinite;
    }

    @Override
    public @NotNull Component parse(@NotNull String input) throws ArgumentSyntaxException {
        try {
            // Verify if the input is valid json
            final JsonReader reader = new JsonReader(new StringReader(input));
            return GsonComponentSerializer.gson().serializer().getAdapter(Component.class).read(reader);
        } catch (Exception e) {
            if (!infinite) {
                // Input needs to be quoted
                input = ArgumentString.staticParse(input);
            }
            // Otherwise parse with MiniMessage
            return MINI_MESSAGE.parse(input);
        }
    }

    @Override
    public void processNodes(@NotNull NodeMaker nodeMaker, boolean executable) {
        DeclareCommandsPacket.Node stringNode = simpleArgumentNode(this, executable, false, false);
        stringNode.parser = "brigadier:string";
        stringNode.properties = BinaryWriter.makeArray(binaryWriter ->
                binaryWriter.writeVarInt(infinite ? 2 : 1)); // Greedy or quotable depending on the type

        nodeMaker.addNodes(new DeclareCommandsPacket.Node[]{stringNode});
    }
}
