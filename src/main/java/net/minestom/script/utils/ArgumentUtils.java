package net.minestom.script.utils;

import net.minestom.server.command.CommandSender;
import net.minestom.server.utils.BlockPosition;
import net.minestom.server.utils.Vector;
import net.minestom.server.utils.location.RelativeBlockPosition;
import net.minestom.server.utils.location.RelativeVec;
import org.jetbrains.annotations.NotNull;

public class ArgumentUtils {

    public static @NotNull Vector from(@NotNull CommandSender sender, @NotNull RelativeVec relativeVec) {
        return relativeVec.from(sender.isPlayer() ? sender.asPlayer() : null);
    }

    public static @NotNull BlockPosition from(@NotNull CommandSender sender, @NotNull RelativeBlockPosition relativeBlockPosition) {
        return relativeBlockPosition.from(sender.isPlayer() ? sender.asPlayer() : null);
    }
}
