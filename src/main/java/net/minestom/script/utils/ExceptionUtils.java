package net.minestom.script.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.script.ScriptManager;
import net.minestom.server.MinecraftServer;
import net.minestom.server.adventure.audience.Audiences;
import org.graalvm.polyglot.PolyglotException;
import org.jetbrains.annotations.NotNull;

public class ExceptionUtils {
    public static void handleException(@NotNull Throwable throwable) {
        throwable.printStackTrace();
        var audiences = Audiences.players(player -> ScriptManager.getCommandPermission().apply(player));
        audiences.sendMessage(Component.text(throwable.getMessage(), NamedTextColor.RED));

        if (throwable instanceof PolyglotException) {
            PolyglotException polyglotException = (PolyglotException) throwable;
            var sourceLocation = polyglotException.getSourceLocation();
            audiences.sendMessage(Component.text("Line " + sourceLocation.getStartLine() + ":" + sourceLocation.getEndLine()));
            audiences.sendMessage(Component.text("Characters: " + sourceLocation.getCharacters(), NamedTextColor.RED));
        }
    }

    public static void sendMessage(Component component) {
        var audiences = Audiences.players(player -> ScriptManager.getCommandPermission().apply(player));
        audiences.sendMessage(component);
        MinecraftServer.getCommandManager().getConsoleSender().sendMessage(component);
    }

}
