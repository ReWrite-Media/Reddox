package net.minestom.script.component;

import net.minestom.server.MinecraftServer;
import net.minestom.server.event.handler.EventHandler;
import org.jetbrains.annotations.NotNull;

/**
 * Represents an independent component of the Minecraft server.
 * <p>
 * Commands and subcommands should be divided based on these.
 */
public class ScriptComponent {

    protected ScriptComponent() {

    }

    @NotNull
    public EventHandler getGlobalEventHandler() {
        return MinecraftServer.getGlobalEventHandler();
    }

}
