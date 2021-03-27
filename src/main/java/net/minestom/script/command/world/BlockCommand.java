package net.minestom.script.command.world;

import static net.minestom.server.command.builder.arguments.ArgumentType.BlockState;
import static net.minestom.server.command.builder.arguments.ArgumentType.Literal;
import static net.minestom.server.command.builder.arguments.ArgumentType.RelativeBlockPosition;
import static net.minestom.server.command.builder.arguments.ArgumentType.Word;

import java.util.UUID;

import net.kyori.adventure.text.Component;
import net.minestom.script.command.RichCommand;
import net.minestom.script.component.RegionComponent;
import net.minestom.server.MinecraftServer;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;
import net.minestom.server.utils.Position;
import net.minestom.server.utils.location.RelativeBlockPosition;

public class BlockCommand extends RichCommand {
    public BlockCommand() {
        super("block");

        final RegionComponent regionComponent = getApi().getRegionHandler();

        setDefaultExecutor((sender, context) -> sender.sendMessage(Component.text("Usage: /world block <set> worlduuid blockstate")));
        addSyntax((sender, context) -> {
            final Block block = context.get("block");
            UUID worldUUID = UUID.fromString(context.get("worldId"));
            final Instance instance = MinecraftServer.getInstanceManager().getInstance(worldUUID);

            RelativeBlockPosition position = context.get("position");

            instance.setBlock(position.from(new Position(0, 0, 0)), block);
        }, Literal("modify"), Literal("set"), BlockState("block"), Word("worldId"), RelativeBlockPosition("position"));
    }
}
