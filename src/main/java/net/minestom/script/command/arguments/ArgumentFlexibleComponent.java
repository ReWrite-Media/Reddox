package net.minestom.script.command.arguments;

import com.google.gson.JsonParseException;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.minestom.server.command.builder.NodeMaker;
import net.minestom.server.command.builder.arguments.Argument;
import net.minestom.server.network.packet.server.play.DeclareCommandsPacket;
import org.jetbrains.annotations.NotNull;

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
    public @NotNull Component parse(@NotNull String input) {
        try {
            // Verify if the input is valid json
            return GsonComponentSerializer.gson().deserialize(input);
        } catch (JsonParseException e) {
            // Otherwise parse with MiniMessage
            return MINI_MESSAGE.parse(input);
        }
    }

    @Override
    public void processNodes(@NotNull NodeMaker nodeMaker, boolean executable) {
        DeclareCommandsPacket.Node stringNode = simpleArgumentNode(this, executable, false, false);
        stringNode.parser = "brigadier:string";
        stringNode.properties = packetWriter -> {
            packetWriter.writeVarInt(infinite ? 2 : 1); // Greedy or quotable depending on the type
        };

        nodeMaker.addNodes(new DeclareCommandsPacket.Node[]{stringNode});
    }
}
