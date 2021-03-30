package net.minestom.script.command.editor;

import net.kyori.adventure.text.Component;
import net.minestom.script.GlobalExecutor;
import net.minestom.script.command.RichCommand;
import net.minestom.server.MinecraftServer;
import net.minestom.server.chat.ColoredText;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.Player.Hand;
import net.minestom.server.inventory.PlayerInventory;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.item.metadata.WritableBookMeta;
import net.minestom.server.listener.manager.PacketController;
import net.minestom.server.network.packet.client.ClientPlayPacket;
import net.minestom.server.network.packet.client.play.ClientEditBookPacket;
import net.minestom.server.network.packet.server.play.OpenBookPacket;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EditorCommand extends RichCommand {

    private Map<Player, ItemStack> items = new HashMap<Player, ItemStack>();

    public EditorCommand() {
        super("editor");

        addSyntax((sender, context) -> {
            openBook(sender.asPlayer());
        }, ArgumentType.Literal("open"));

        MinecraftServer.getConnectionManager().onPacketReceive(this::handlePacket);
    }

    private void openBook(Player player) {
        final PlayerInventory inventory = player.getInventory();
        // Save main hand's item
        ItemStack mainHand = inventory.getItemInMainHand();
        items.put(player, mainHand);

        // Open Book
        ItemStack bookItem = new ItemStack(Material.WRITABLE_BOOK, (byte) 1);
        WritableBookMeta meta = new WritableBookMeta();
        meta.setPages(
                List.of(Component.text("// Put code here:\n"))
        );

        inventory.setItemInMainHand(bookItem);
        player.sendTitleSubtitleMessage(ColoredText.of("Right click to open editor"), ColoredText.of("sign the book with one of js/python"));

        OpenBookPacket bookPacket = new OpenBookPacket();
        bookPacket.hand = Hand.MAIN;
        player.getPlayerConnection().sendPacket(bookPacket);

    }

    private void handlePacket(Player player, PacketController controller, ClientPlayPacket ambiguousPacket) {
        if (ambiguousPacket instanceof ClientEditBookPacket) {

            ClientEditBookPacket packet = (ClientEditBookPacket) ambiguousPacket;

            if (packet.isSigning) {
                WritableBookMeta meta = (WritableBookMeta) packet.book.getItemMeta();

                player.sendMessage("Running Script...");

                //String fileString = String.join("\n", meta.getPages());

                final GlobalExecutor globalExecutor = new GlobalExecutor();
                //Script script = new Script("IG-Script", fileString, meta.getTitle(), globalExecutor);
                // FIXME
                //ScriptManager.addScript(script);

                player.sendMessage("Done!");
            }

            // Restore main hand's item
            player.getInventory().setItemInMainHand(items.get(player));

            items.remove(player);
        }
    }
}
