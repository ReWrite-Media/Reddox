package net.minestom.script.command;

import net.minestom.script.command.world.BlockCommand;
import net.minestom.script.command.world.ParticleCommand;
import net.minestom.script.command.world.RegionCommand;
import net.minestom.script.command.world.TimeCommand;

public class WorldCommand extends RichCommand {
    public WorldCommand() {
        super("world", ScriptCategory.WORLD_MANIPULATION);

        addSubcommand(new RegionCommand());
        addSubcommand(new ParticleCommand());
        addSubcommand(new BlockCommand());
        addSubcommand(new TimeCommand());
    }
}
