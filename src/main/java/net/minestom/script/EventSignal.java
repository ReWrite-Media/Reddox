package net.minestom.script;

import net.minestom.script.utils.PropertySerializer;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.event.player.PlayerMoveEvent;
import net.minestom.server.utils.Position;
import org.jetbrains.annotations.NotNull;

/**
 * Calls event-related signals.
 */
public class EventSignal {

    protected static void init(@NotNull GlobalEventHandler globalEventHandler, Executor executor) {

        // 'move'
        globalEventHandler.addEventCallback(PlayerMoveEvent.class, event -> {
            final Position position = event.getNewPosition();

            ScriptProperties scriptProperties = new ScriptProperties();
            scriptProperties.putMember("position", PropertySerializer.fromPosition(position));
            executor.signal("move", scriptProperties);
        });
    }

}
