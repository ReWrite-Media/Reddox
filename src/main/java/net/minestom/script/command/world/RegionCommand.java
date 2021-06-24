package net.minestom.script.command.world;

import net.kyori.adventure.text.Component;
import net.minestom.script.command.RichCommand;
import net.minestom.script.component.RegionComponent;
import net.minestom.script.utils.ArgumentUtils;
import net.minestom.server.command.builder.CommandData;
import net.minestom.server.utils.Vector;
import net.minestom.server.utils.location.RelativeVec;
import org.jglrxavpok.hephaistos.nbt.NBTCompound;

import java.lang.String;
import java.util.ArrayList;

import static net.minestom.server.command.builder.arguments.ArgumentType.*;

public class RegionCommand extends RichCommand {
    public RegionCommand() {
        super("region");

        final RegionComponent regionComponent = getApi().getRegionHandler();

        setDefaultExecutor((sender, context) ->
                sender.sendMessage(Component.text("Usage: /world region <create/edit> <identifier> [properties]")));

        // All functions related to regions (eg: know if a position is inside a region)
        addSubcommand(new RegionFunctionCommand());

        final var propertiesArgument = Loop("properties",
                Group("1", Literal("position_start"), RelativeVec3("pos1")),
                Group("2", Literal("position_end"), RelativeVec3("pos2")),
                Group("3", Literal("data"), NbtCompound("region_data")))
                .setDefaultValue(ArrayList::new);

        addSyntax((sender, context) -> {
            final String identifier = context.get("identifier");

            RelativeVec posStart = context.get("pos1");
            RelativeVec posEnd = context.get("pos2");
            NBTCompound data = context.get("region_data");

            RegionComponent.Region region = regionComponent.createRegion(identifier, ArgumentUtils.from(sender, posStart), ArgumentUtils.from(sender, posEnd), data);
            final boolean success = region != null;
            if (success) {
                sender.sendMessage(Component.text("Region '" + identifier + "' created successfully!"));
            } else {
                sender.sendMessage(Component.text("Region '" + identifier + "' already exists!"));
            }
            context.setReturnData(new CommandData().set("success", success));
        }, Literal("create"), Word("identifier"), RelativeVec3("pos1"), RelativeVec3("pos2"), NbtCompound("region_data").setDefaultValue(NBTCompound::new));

        addSyntax((sender, context) -> {
            final String identifier = context.get("identifier");
            final boolean success = regionComponent.deleteRegion(identifier);
            if (success) {
                sender.sendMessage(Component.text("Region '" + identifier + "' has been deleted"));
            } else {
                sender.sendMessage(Component.text("Region '" + identifier + "' does not exist!"));
            }
            context.setReturnData(new CommandData().set("success", success));
        }, Literal("delete"), Word("identifier"));

        addSyntax((sender, context) -> {
            // TODO edit
            System.out.println("syntax2");
        }, Literal("edit"), Word("identifier"), propertiesArgument);
    }

    private static class RegionFunctionCommand extends RichCommand {

        public RegionFunctionCommand() {
            super("function");

            final RegionComponent regionComponent = getApi().getRegionHandler();

            // 'is_inside'
            {
                // With explicit position
                addSyntax((sender, context) -> {
                    final String identifier = context.get("identifier");
                    final RegionComponent.Region region = regionComponent.getRegion(identifier);
                    boolean inside;
                    if (region != null) {
                        RelativeVec relativeVec = context.get("position");
                        final Vector vector = ArgumentUtils.from(sender, relativeVec);
                        inside = region.isInside(vector);
                        sender.sendMessage(Component.text("inside: " + inside));
                    } else {
                        inside = false;
                        sender.sendMessage(Component.text("region not found"));
                    }
                    context.setReturnData(new CommandData().set("inside", inside));
                }, Literal("is_inside"), Word("identifier"), RelativeVec3("position"));
            }

            // 'get_data'
            {
                addSyntax((sender, context) -> {
                    final String identifier = context.get("identifier");
                    final RegionComponent.Region region = regionComponent.getRegion(identifier);

                    CommandData data = new CommandData();

                    data.set("success", region != null);
                    if (region != null) {
                        final NBTCompound nbtCompound = region.getNbtCompound();
                        data.set("data", nbtCompound);
                        sender.sendMessage(Component.text("data: " + nbtCompound.toSNBT()));
                    } else {
                        sender.sendMessage(Component.text("region not found"));
                    }

                    context.setReturnData(data);
                }, Literal("get_data"), Word("identifier"));
            }

        }
    }

}
