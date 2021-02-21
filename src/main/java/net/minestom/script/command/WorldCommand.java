package net.minestom.script.command;

import net.minestom.script.command.world.RegionCommand;

public class WorldCommand extends ScriptCommand {
    public WorldCommand() {
        super("world", ScriptCategory.WORLD_MANIPULATION);

        addSubcommand(new RegionCommand());
    }
}
