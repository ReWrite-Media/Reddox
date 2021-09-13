package net.minestom.script;

import net.minestom.script.property.*;
import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.Player;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.event.entity.EntityAttackEvent;
import net.minestom.server.event.player.*;
import net.minestom.server.instance.block.Block;
import net.minestom.server.item.ItemStack;
import org.graalvm.polyglot.Value;
import org.graalvm.polyglot.proxy.ProxyObject;
import org.jetbrains.annotations.NotNull;

/**
 * Calls event-related signals.
 */
public enum EventSignal {
    /**
     * player: PlayerProperty
     */
    PLAYER_JOIN,
    /**
     * player: PlayerProperty<br>
     * position: PositionProperty
     */
    PLAYER_MOVE,
    /**
     * player: PlayerProperty<br>
     * item: ItemProperty
     */
    PLAYER_USE_ITEM,
    /**
     * player: PlayerProperty<br>
     * block: BlockProperty
     */
    PLAYER_USE_ITEM_ON_BLOCK,
    /**
     * player: PlayerProperty<br>
     * block: BlockProperty
     */
    PLAYER_BLOCK_PLACE,
    /**
     * player: PlayerProperty<br>
     * target: EntityProperty
     */
    PLAYER_ENTITY_INTERACT,
    /**
     * entity: EntityProperty<br>
     * target: EntityProperty
     */
    ENTITY_ATTACK;

    private static final String CANCEL_MEMBER = "cancel";

    static void init(@NotNull GlobalEventHandler globalEventHandler) {
        final GlobalExecutor globalExecutor = ScriptManager.API.getExecutor();

        // 'player_join'
        globalEventHandler.addListener(PlayerSpawnEvent.class, event -> {
            if (event.isFirstSpawn()) {
                final Player player = event.getPlayer();

                Properties properties = new Properties();
                properties.putMember("player", new PlayerProperty(player));
                globalExecutor.signal(PLAYER_JOIN.name(), properties);
            }
        });

        // 'move'
        globalEventHandler.addListener(PlayerMoveEvent.class, event -> {
            final Player player = event.getPlayer();
            final Pos position = event.getNewPosition();

            Properties properties = new Properties();
            properties.putMember("player", new PlayerProperty(player));
            properties.putMember("position", new PositionProperty(position));
            ProxyObject output = globalExecutor.signal(PLAYER_MOVE.name(), properties);
            event.setCancelled(isCancelled(output));
        });

        // 'use_item'
        globalEventHandler.addListener(PlayerUseItemEvent.class, event -> {
            final Player player = event.getPlayer();
            final ItemStack itemStack = event.getItemStack();

            Properties properties = new Properties();
            properties.putMember("player", new PlayerProperty(player));
            properties.putMember("item", new ItemProperty(itemStack));
            ProxyObject output = globalExecutor.signal(PLAYER_USE_ITEM.name(), properties);
            event.setCancelled(isCancelled(output));
        });

        // 'use_item_block'
        globalEventHandler.addListener(PlayerUseItemOnBlockEvent.class, event -> {
            final Player player = event.getPlayer();
            final Point position = event.getPosition();
            final Block block = player.getInstance().getBlock(position);

            Properties properties = new Properties();
            properties.putMember("player", new PlayerProperty(player));
            properties.putMember("block", new BlockProperty(block, position));
            globalExecutor.signal(PLAYER_USE_ITEM_ON_BLOCK.name(), properties);
        });

        // 'place_block'
        globalEventHandler.addListener(PlayerBlockPlaceEvent.class, event -> {
            final Player player = event.getPlayer();
            final Point position = event.getBlockPosition();
            final Block block = event.getBlock();

            Properties properties = new Properties();
            properties.putMember("player", new PlayerProperty(player));
            properties.putMember("block", new BlockProperty(block, position));
            ProxyObject output = globalExecutor.signal(PLAYER_BLOCK_PLACE.name(), properties);
            event.setCancelled(isCancelled(output));
        });

        // 'entity_interact'
        globalEventHandler.addListener(PlayerEntityInteractEvent.class, event -> {

            // Prevent double execution
            if (event.getHand() != Player.Hand.MAIN) {
                return;
            }

            final Player player = event.getPlayer();
            final Entity target = event.getTarget();

            Properties properties = new Properties();
            properties.putMember("player", new PlayerProperty(player));
            properties.putMember("target", Properties.fromEntity(target));
            globalExecutor.signal(PLAYER_ENTITY_INTERACT.name(), properties);
        });

        // 'attack'
        globalEventHandler.addListener(EntityAttackEvent.class, event -> {
            final Entity entity = event.getEntity();
            final Entity target = event.getTarget();

            Properties properties = new Properties();
            properties.putMember("entity", Properties.fromEntity(entity));
            properties.putMember("target", Properties.fromEntity(target));
            globalExecutor.signal(ENTITY_ATTACK.name(), properties);
        });
    }

    private static boolean isCancelled(ProxyObject output) {
        if (!output.hasMember(CANCEL_MEMBER)) return false;
        final Object member = output.getMember(CANCEL_MEMBER);
        if (!(member instanceof Value)) return false;

        final Value cancelled = (Value) member;
        if (cancelled.isBoolean()) {
            return cancelled.asBoolean();
        }
        return false;
    }
}
