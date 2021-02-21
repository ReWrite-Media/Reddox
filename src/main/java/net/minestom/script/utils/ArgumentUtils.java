package net.minestom.script.utils;

import net.minestom.server.command.CommandSender;
import net.minestom.server.utils.Vector;
import net.minestom.server.utils.location.RelativeVec;
import org.jetbrains.annotations.NotNull;

public class ArgumentUtils {

    @NotNull
    public static Vector from(@NotNull CommandSender sender, @NotNull RelativeVec relativeVec) {
        return relativeVec.from(sender.isPlayer() ? sender.asPlayer() : null);
    }

}
