package net.minestom.script;

import net.minestom.script.property.*;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.Player;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.event.entity.EntityAttackEvent;
import net.minestom.server.event.player.*;
import net.minestom.server.item.ItemStack;
import net.minestom.server.utils.BlockPosition;
import net.minestom.server.utils.Position;
import org.graalvm.polyglot.Value;
import org.graalvm.polyglot.proxy.ProxyObject;
import org.jetbrains.annotations.NotNull;

/**
 * Calls event-related signals.
 */
public enum EventSignal {
    PLAYER_MOVE,
    PLAYER_USE_ITEM,
    PLAYER_USE_ITEM_ON_BLOCK,
    PLAYER_BLOCK_PLACE,
    PLAYER_ENTITY_INTERACT,
    ENTITY_ATTACK;

    private static final String CANCEL_MEMBER = "cancel";

    protected static void init(@NotNull GlobalEventHandler globalEventHandler) {

        final Executor executor = ScriptManager.API.getExecutor();

        // 'move'
        globalEventHandler.addEventCallback(PlayerMoveEvent.class, event -> {
            final Player player = event.getPlayer();
            final Position position = event.getNewPosition();

            Properties properties = new Properties();
            properties.putMember("player", new PlayerProperty(player));
            properties.putMember("position", new PositionProperty(position));
            ProxyObject output = executor.signal(PLAYER_MOVE.name(), properties);
            event.setCancelled(isCancelled(output));
        });

        // 'use_item'
        globalEventHandler.addEventCallback(PlayerUseItemEvent.class, event -> {
            final Player player = event.getPlayer();
            final ItemStack itemStack = event.getItemStack();

            Properties properties = new Properties();
            properties.putMember("player", new PlayerProperty(player));
            properties.putMember("item", new ItemProperty(itemStack));
            executor.signal(PLAYER_USE_ITEM.name(), properties);
        });

        // 'use_item_block'
        globalEventHandler.addEventCallback(PlayerUseItemOnBlockEvent.class, event -> {
            final Player player = event.getPlayer();
            final BlockPosition position = event.getPosition();
            final short blockStateId = player.getInstance().getBlockStateId(position);

            Properties properties = new Properties();
            properties.putMember("player", new PlayerProperty(player));
            properties.putMember("block", new BlockProperty(blockStateId, position));
            executor.signal(PLAYER_USE_ITEM_ON_BLOCK.name(), properties);
        });

        // 'place_block'
        globalEventHandler.addEventCallback(PlayerBlockPlaceEvent.class, event -> {
            final Player player = event.getPlayer();
            final BlockPosition position = event.getBlockPosition();
            final short blockStateId = event.getBlockStateId();

            Properties properties = new Properties();
            properties.putMember("player", new PlayerProperty(player));
            properties.putMember("block", new BlockProperty(blockStateId, position));
            executor.signal(PLAYER_BLOCK_PLACE.name(), properties);
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
            executor.signal(PLAYER_ENTITY_INTERACT.name(), properties);
        });

        // 'attack'
        globalEventHandler.addEventCallback(EntityAttackEvent.class, event -> {
            final Entity entity = event.getEntity();
            final Entity target = event.getTarget();

            Properties properties = new Properties();
            properties.putMember("entity", Properties.fromEntity(entity));
            properties.putMember("target", Properties.fromEntity(target));
            executor.signal(ENTITY_ATTACK.name(), properties);
        });
    }

    private static boolean isCancelled(ProxyObject output) {
        if (!output.hasMember(CANCEL_MEMBER))
            return false;
        final Object member = output.getMember(CANCEL_MEMBER);
        if (!(member instanceof Value))
            return false;

        final Value cancelled = (Value) member;
        if (cancelled.isBoolean()) {
            return cancelled.asBoolean();
        }
        return false;
    }

}
