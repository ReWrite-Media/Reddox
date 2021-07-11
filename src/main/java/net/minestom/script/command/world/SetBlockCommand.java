package net.minestom.script.command.world;

import net.kyori.adventure.text.Component;
import net.minestom.script.command.RichCommand;
import net.minestom.script.component.RegionComponent;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.instance.block.Block;
import net.minestom.server.utils.location.RelativeVec;

import static net.minestom.server.command.builder.arguments.ArgumentType.BlockState;
import static net.minestom.server.command.builder.arguments.ArgumentType.RelativeVec3;

public class SetBlockCommand extends RichCommand {
    public SetBlockCommand() {
        super("setblock");

        final RegionComponent regionComponent = getApi().getRegionHandler();

        setDefaultExecutor((sender, context) -> sender.sendMessage(Component.text("Usage: /world setblock <pos> <block>")));
        addSyntax((sender, context) -> {
            final Block block = context.get("block");
            RelativeVec relativeVec = context.get("position");
            Vec blockPosition = relativeVec.fromSender(sender);
            processInstances(sender, instance -> instance.setBlock(blockPosition, block));
        }, RelativeVec3("position"), BlockState("block"));
    }
}
