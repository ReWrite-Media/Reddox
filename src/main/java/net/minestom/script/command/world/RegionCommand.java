package net.minestom.script.command.world;

import net.minestom.script.command.ScriptCommand;
import net.minestom.script.handler.RegionHandler;
import net.minestom.script.utils.ArgumentUtils;
import net.minestom.server.utils.location.RelativeVec;
import org.jglrxavpok.hephaistos.nbt.NBTCompound;

import java.lang.String;
import java.util.ArrayList;

import static net.minestom.server.command.builder.arguments.ArgumentType.*;

public class RegionCommand extends ScriptCommand {
    public RegionCommand() {
        super("region");

        final RegionHandler regionHandler = getApi().getRegionHandler();

        setDefaultExecutor((sender, args) -> sender.sendMessage("Usage: /world region <create/edit> <identifier> [properties]"));

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

            RegionHandler.Region region = regionHandler.createRegion(identifier, ArgumentUtils.from(sender, posStart), ArgumentUtils.from(sender, posEnd), data);
            if (region != null) {
                sender.sendMessage("Region '" + identifier + "' created successfully!");
            } else {
                sender.sendMessage("Region '" + identifier + "' already exists!");
            }
        }, Literal("create"), Word("identifier"), RelativeVec3("pos1"), RelativeVec3("pos2"), NbtCompound("region_data").setDefaultValue(new NBTCompound()));

        addSyntax((sender, args) -> {
            System.out.println("syntax2");
        }, Literal("edit"), Word("identifier"), propertiesArgument);
    }
}
