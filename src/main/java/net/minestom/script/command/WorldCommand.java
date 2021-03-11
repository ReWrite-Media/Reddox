package net.minestom.script.command;

import net.minestom.script.command.world.ParticleCommand;
import net.minestom.script.command.world.RegionCommand;

public class WorldCommand extends RichCommand {
    public WorldCommand() {
        super("world", ScriptCategory.WORLD_MANIPULATION);

        addSubcommand(new RegionCommand());
        addSubcommand(new ParticleCommand());
    }
}
