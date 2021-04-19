package net.minestom.script.command.world;

import net.kyori.adventure.text.Component;
import net.minestom.script.command.RichCommand;
import net.minestom.script.component.RegionComponent;
import net.minestom.server.instance.block.Block;
import net.minestom.server.utils.BlockPosition;
import net.minestom.server.utils.location.RelativeBlockPosition;

import static net.minestom.server.command.builder.arguments.ArgumentType.*;

public class BlockCommand extends RichCommand {
    public BlockCommand() {
        super("block");

        final RegionComponent regionComponent = getApi().getRegionHandler();

        setDefaultExecutor((sender, context) -> sender.sendMessage(Component.text("Usage: /world block <set> worlduuid blockstate")));
        addSyntax((sender, context) -> {
            final Block block = context.get("block");
            RelativeBlockPosition relativeBlockPosition = context.get("position");
            BlockPosition blockPosition = relativeBlockPosition.from(sender.isPlayer() ? sender.asPlayer() : null);
            processInstances(sender, instance -> {
                instance.setBlock(blockPosition, block);
            });
        }, Literal("set"), BlockState("block"), RelativeBlockPosition("position"));
    }
}
