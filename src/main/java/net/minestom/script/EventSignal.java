package net.minestom.script;

import net.minestom.script.object.*;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.Player;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.event.entity.EntityAttackEvent;
import net.minestom.server.event.player.PlayerEntityInteractEvent;
import net.minestom.server.event.player.PlayerMoveEvent;
import net.minestom.server.event.player.PlayerUseItemEvent;
import net.minestom.server.event.player.PlayerUseItemOnBlockEvent;
import net.minestom.server.item.ItemStack;
import net.minestom.server.utils.BlockPosition;
import net.minestom.server.utils.Position;
import org.jetbrains.annotations.NotNull;

/**
 * Calls event-related signals.
 */
public class EventSignal {

    public static final String MOVE_SIGNAL = "move";
    public static final String USE_ITEM_SIGNAL = "use_item";
    public static final String USE_ITEM_BLOCK_SIGNAL = "use_item_block";
    public static final String ENTITY_INTERACT_SIGNAL = "entity_interact";
    public static final String ENTITY_ATTACK_SIGNAL = "entity_attack";

    protected static void init(@NotNull GlobalEventHandler globalEventHandler) {

        final Executor executor = ScriptManager.API.getExecutor();

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

        // 'use_item_block'
        globalEventHandler.addEventCallback(PlayerUseItemOnBlockEvent.class, event -> {
            final Player player = event.getPlayer();
            final BlockPosition position = event.getPosition();

            Properties properties = new Properties();
            properties.putMember("player", new PlayerProperty(player));
            properties.putMember("position", new BlockPositionProperty(position));
            executor.signal(USE_ITEM_BLOCK_SIGNAL, properties);
        });

        // 'entity_interact'
        globalEventHandler.addEventCallback(PlayerEntityInteractEvent.class, event -> {

            // Prevent double execution
            if (event.getHand() != Player.Hand.MAIN) {
                return;
            }

            final Player player = event.getPlayer();
            final Entity target = event.getTarget();

            Properties properties = new Properties();
            properties.putMember("player", new PlayerProperty(player));
            properties.putMember("target", Properties.fromEntity(target));
            executor.signal(ENTITY_INTERACT_SIGNAL, properties);
        });

        // 'attack'
        globalEventHandler.addEventCallback(EntityAttackEvent.class, event -> {
            final Entity entity = event.getEntity();
            final Entity target = event.getTarget();

            Properties properties = new Properties();
            properties.putMember("entity", Properties.fromEntity(entity));
            properties.putMember("target", Properties.fromEntity(target));
            executor.signal(ENTITY_ATTACK_SIGNAL, properties);
        });
    }

}
