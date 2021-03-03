package net.minestom.script;

import net.minestom.script.object.ItemProperty;
import net.minestom.script.object.PlayerProperty;
import net.minestom.script.object.PositionProperty;
import net.minestom.script.object.Properties;
import net.minestom.server.entity.Player;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.event.player.PlayerMoveEvent;
import net.minestom.server.event.player.PlayerUseItemEvent;
import net.minestom.server.item.ItemStack;
import net.minestom.server.utils.Position;
import org.jetbrains.annotations.NotNull;

/**
 * Calls event-related signals.
 */
public class EventSignal {

    public static final String MOVE_SIGNAL = "move";
    public static final String USE_ITEM_SIGNAL = "use_item";


    protected static void init(@NotNull GlobalEventHandler globalEventHandler, Executor executor) {

        // 'move'
        globalEventHandler.addEventCallback(PlayerMoveEvent.class, event -> {
            final Player player = event.getPlayer();
            final Position position = event.getNewPosition();

            Properties properties = new Properties();
            properties.putMember("player", new PlayerProperty(player));
            properties.putMember("position", new PositionProperty(position));
            executor.signal(MOVE_SIGNAL, properties);
        });

        // 'use_item'
        globalEventHandler.addEventCallback(PlayerUseItemEvent.class, event -> {
            final Player player = event.getPlayer();
            final ItemStack itemStack = event.getItemStack();

            Properties properties = new Properties();
            properties.putMember("player", new PlayerProperty(player));
            properties.putMember("item", new ItemProperty(itemStack));
            executor.signal(USE_ITEM_SIGNAL, properties);
        });
    }

}
