package net.minestom.script.command.entity;

import net.kyori.adventure.text.Component;
import net.minestom.script.command.RichCommand;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.Player;
import net.minestom.server.item.ItemStack;
import net.minestom.server.utils.entity.EntityFinder;

import java.util.List;

import static net.minestom.server.command.builder.arguments.ArgumentType.Integer;
import static net.minestom.server.command.builder.arguments.ArgumentType.*;

public class GiveCommand extends RichCommand {
    public GiveCommand() {
        super("give");

        setDefaultExecutor((sender, context) ->
                sender.sendMessage(Component.text("Usage: /entity give <target> <item> [<count>]")));

        addSyntax((sender, context) -> {
            final EntityFinder entityFinder = context.get("target");
            final ItemStack itemStack = context.get("item");
            final int count = context.get("count");
            // FIXME: support count > 64
            itemStack.setAmount((byte) count);

            final List<Entity> targets = entityFinder.find(sender);
            for (Entity target : targets) {
                if (target instanceof Player) {
                    Player player = (Player) target;
                    player.getInventory().addItemStack(itemStack.clone());
                }
            }

            sender.sendMessage(Component.text("Items have been given successfully!"));

        }, Entity("target").onlyPlayers(true), ItemStack("item"), Integer("count").setDefaultValue(1));
    }
}
