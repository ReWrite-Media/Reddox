package net.minestom.script;

import net.minestom.script.object.PlayerProperty;
import net.minestom.script.object.PositionProperty;
import net.minestom.script.object.Properties;
import net.minestom.server.entity.Player;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.event.player.PlayerMoveEvent;
import net.minestom.server.utils.Position;
import org.graalvm.polyglot.Value;
import org.jetbrains.annotations.NotNull;

/**
 * Calls event-related signals.
 */
public class EventSignal {

    protected static void init(@NotNull GlobalEventHandler globalEventHandler, Executor executor) {

        // 'move'
        globalEventHandler.addEventCallback(PlayerMoveEvent.class, event -> {
            final Player player = event.getPlayer();
            final Position position = event.getNewPosition();

            Properties properties = new Properties();
            properties.putMember("player", Value.asValue(new PlayerProperty(player)));
            properties.putMember("position", Value.asValue(new PositionProperty(position)));
            executor.signal("move", properties);
        });
    }

}
