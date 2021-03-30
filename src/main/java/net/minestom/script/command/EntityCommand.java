package net.minestom.script.command;

import net.minestom.script.command.entity.*;

public class EntityCommand extends RichCommand {
    public EntityCommand() {
        super("entity", ScriptCategory.ENTITY_MANIPULATION);

        addSubcommand(new TeleportCommand());
        addSubcommand(new GamemodeCommand());
        addSubcommand(new EffectCommand());
        addSubcommand(new GiveCommand());
        addSubcommand(new EntityEditorCommand());
        addSubcommand(new KillCommand());
        addSubcommand(new EntityQueryCommand());
    }
}
