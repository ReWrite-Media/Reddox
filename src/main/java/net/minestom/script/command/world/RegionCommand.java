package net.minestom.script.command.world;

import net.minestom.script.command.ScriptCommand;
import net.minestom.script.component.RegionComponent;
import net.minestom.script.utils.ArgumentUtils;
import net.minestom.server.command.builder.CommandData;
import net.minestom.server.utils.Vector;
import net.minestom.server.utils.location.RelativeVec;
import org.jglrxavpok.hephaistos.nbt.NBTCompound;

import java.lang.String;
import java.util.ArrayList;

import static net.minestom.server.command.builder.arguments.ArgumentType.*;

public class RegionCommand extends ScriptCommand {
    public RegionCommand() {
        super("region");

        final RegionComponent regionComponent = getApi().getRegionHandler();

        setDefaultExecutor((sender, args) -> sender.sendMessage("Usage: /world region <create/edit> <identifier> [properties]"));

        // All functions related to regions (eg: know if a position is inside a region)
        addSubcommand(new RegionFunctionCommand());

        final var propertiesArgument = Loop("properties",
                Group("1", Literal("position_start"), RelativeVec3("pos1")),
                Group("2", Literal("position_end"), RelativeVec3("pos2")),
                Group("3", Literal("data"), NbtCompound("region_data")))
                .setDefaultValue(new ArrayList<>());

        addSyntax((sender, args) -> {
            final String identifier = args.get("identifier");

            RelativeVec posStart = args.get("pos1");
            RelativeVec posEnd = args.get("pos2");
            NBTCompound data = args.get("region_data");

            RegionComponent.Region region = regionComponent.createRegion(identifier, ArgumentUtils.from(sender, posStart), ArgumentUtils.from(sender, posEnd), data);
            final boolean success = region != null;
            if (success) {
                sender.sendMessage("Region '" + identifier + "' created successfully!");
            } else {
                sender.sendMessage("Region '" + identifier + "' already exists!");
            }
            args.setReturnData(new CommandData().set("success", success));
        }, Literal("create"), Word("identifier"), RelativeVec3("pos1"), RelativeVec3("pos2"), NbtCompound("region_data").setDefaultValue(new NBTCompound()));

        addSyntax((sender, args) -> {
            final String identifier = args.get("identifier");
            final boolean success = regionComponent.deleteRegion(identifier);
            if (success) {
                sender.sendMessage("Region '" + identifier + "' has been deleted");
            } else {
                sender.sendMessage("Region '" + identifier + "' does not exist!");
            }
            args.setReturnData(new CommandData().set("success", success));
        }, Literal("delete"), Word("identifier"));

        addSyntax((sender, args) -> {
            // TODO edit
            System.out.println("syntax2");
        }, Literal("edit"), Word("identifier"), propertiesArgument);
    }

    private static class RegionFunctionCommand extends ScriptCommand {

        public RegionFunctionCommand() {
            super("function");

            final RegionComponent regionComponent = getApi().getRegionHandler();

            // 'is_inside'
            {
                // With explicit position
                addSyntax((sender, args) -> {
                    final String identifier = args.get("identifier");
                    final RegionComponent.Region region = regionComponent.getRegion(identifier);
                    boolean inside;
                    if (region != null) {
                        final Vector vector = ArgumentUtils.from(sender, args.get("position"));
                        inside = region.isInside(vector);
                        sender.sendMessage("inside: " + inside);
                    } else {
                        inside = false;
                        sender.sendMessage("region not found");
                    }
                    args.setReturnData(new CommandData().set("inside", inside));
                }, Literal("is_inside"), Word("identifier"), RelativeVec3("position"));
            }

            // 'get_data'
            {
                addSyntax((sender, args) -> {
                    final String identifier = args.get("identifier");
                    final RegionComponent.Region region = regionComponent.getRegion(identifier);

                    CommandData data = new CommandData();

                    data.set("success", region != null);
                    if (region != null) {
                        final NBTCompound nbtCompound = region.getNbtCompound();
                        data.set("data", nbtCompound);
                        sender.sendMessage("data: " + nbtCompound.toSNBT());
                    } else {
                        sender.sendMessage("region not found");
                    }

                    args.setReturnData(data);
                }, Literal("get_data"), Word("identifier"));
            }

        }
    }

}
