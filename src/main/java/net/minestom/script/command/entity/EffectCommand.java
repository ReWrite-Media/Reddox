package net.minestom.script.command.entity;

import net.minestom.script.command.ScriptCommand;
import net.minestom.server.command.CommandSender;
import net.minestom.server.command.builder.Arguments;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.Player;
import net.minestom.server.potion.Potion;
import net.minestom.server.potion.PotionEffect;
import net.minestom.server.utils.entity.EntityFinder;

import java.util.List;

import static net.minestom.server.command.builder.arguments.ArgumentType.Integer;
import static net.minestom.server.command.builder.arguments.ArgumentType.*;

public class EffectCommand extends ScriptCommand {
    public EffectCommand() {
        super("effect");

        setDefaultExecutor(this::usage);

        final var targetArgument = Entities("targets");
        final var effectArgument = Potion("effect");

        // 'clear'
        {
            addSyntax((sender, args) -> {
                final PotionEffect potionEffect = args.get("effect");
                final EntityFinder entityFinder = args.get("targets");
                final List<Entity> targets = entityFinder.find(sender);

                for (Entity target : targets) {
                    target.removeEffect(potionEffect);
                }

                sender.sendMessage("Potion effect removed successfully!");
            }, Literal("clear"), targetArgument, effectArgument);

            addSyntax((sender, args) -> {
                if (!sender.isPlayer()) {
                    usage(sender, args);
                    return;
                }
                final Player player = sender.asPlayer();
                player.clearEffects();
                sender.sendMessage("Your effects have been cleared!");
            }, Literal("clear"));
        }

        // 'give'
        {
            addSyntax((sender, args) -> {
                        final PotionEffect potionEffect = args.get("effect");
                        final EntityFinder entityFinder = args.get("targets");
                        final int ticks = args.get("seconds");
                        final int amplifier = args.get("amplifier");
                        final List<Entity> targets = entityFinder.find(sender);

                        Potion potion = new Potion(potionEffect, (byte) amplifier, ticks * 20);

                        for (Entity target : targets) {
                            target.addEffect(potion);
                        }

                        sender.sendMessage("Potion effect applied successfully!");
                    }, Literal("give"), targetArgument, effectArgument,
                    Integer("seconds").setDefaultValue(30),
                    Integer("amplifier").setDefaultValue(0));
        }

    }

    private void usage(CommandSender sender, Arguments args) {
        sender.sendMessage("Usage: /entity effect clear [<targets>] [<effect>]");
        sender.sendMessage("Usage: /entity effect give <targets> <effect> [<seconds>] [<amplifier>] [<hideParticles>]");
    }
}
