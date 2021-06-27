package net.minestom.script.command.world;

import net.kyori.adventure.text.Component;
import net.minestom.script.command.RichCommand;
import net.minestom.script.utils.ArgumentUtils;
import net.minestom.server.command.builder.CommandData;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.instance.block.Block;
import net.minestom.server.utils.BlockPosition;
import net.minestom.server.utils.location.RelativeVec;

public class GetBlockCommand extends RichCommand {
    public GetBlockCommand() {
        super("getblock");

        setDefaultExecutor((sender, context) -> sender.sendMessage(Component.text("Usage: /world getblock <pos>")));
        addSyntax((sender, context) -> {
            RelativeVec relativeVec = context.get("position");
            BlockPosition blockPosition = ArgumentUtils.from(sender, relativeVec).toPosition().toBlockPosition();
            processInstances(sender, instance -> {
                Block block = instance.getBlock(blockPosition);
                CommandData commandData = new CommandData();
                commandData.set("block", block);
                context.setReturnData(commandData);

                final var nbt = block.nbt();

                Component component = Component.text("Block: " + block.name() + block.properties());
                if (nbt != null) {
                    component = component.append(Component.newline())
                            .append(Component.text(nbt.toSNBT()));
                }
                sender.sendMessage(component);
            });
        }, ArgumentType.RelativeVec3("position"));
    }
}
