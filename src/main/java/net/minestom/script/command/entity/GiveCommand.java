package net.minestom.script.command.entity;

import net.minestom.script.command.ScriptCommand;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.Player;
import net.minestom.server.item.ItemStack;
import net.minestom.server.utils.entity.EntityFinder;

import java.util.List;

import static net.minestom.server.command.builder.arguments.ArgumentType.Integer;
import static net.minestom.server.command.builder.arguments.ArgumentType.*;

public class GiveCommand extends ScriptCommand {
    public GiveCommand() {
        super("give");

        setDefaultExecutor((sender, args) -> sender.sendMessage("Usage: /entity give <target> <item> [<count>]"));

        addSyntax((sender, args) -> {
            final EntityFinder entityFinder = args.get("target");
            final ItemStack itemStack = args.get("item");
            final int count = args.get("count");
            // FIXME: support count > 64
            itemStack.setAmount((byte) count);

            final List<Entity> targets = entityFinder.find(sender);
            for (Entity target : targets) {
                if (target instanceof Player) {
                    Player player = (Player) target;
                    player.getInventory().addItemStack(itemStack.clone());
                }
            }

            sender.sendMessage("Items have been given successfully!");

        }, Entities("target").onlyPlayers(true), ItemStack("item"), Integer("count").setDefaultValue(1));
    }
}
