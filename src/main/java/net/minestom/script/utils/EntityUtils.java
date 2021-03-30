package net.minestom.script.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.Player;
import org.apache.commons.text.WordUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class EntityUtils {

    @NotNull
    public static Component getDisplayComponent(@NotNull Entity entity) {
        Component component;
        if (entity instanceof Player) {
            final Player player = (Player) entity;
            component = Objects.requireNonNullElseGet(player.getDisplayName(),
                    () -> Component.text(player.getUsername(), NamedTextColor.GRAY));
        } else {
            final EntityType entityType = entity.getEntityType();
            final String displayableEntityType = getDisplayableEntityType(entityType);
            component = Component.text(displayableEntityType, NamedTextColor.GRAY)
                    .append(Component.text("[" + entity.getUuid() + "]", NamedTextColor.DARK_GRAY));
        }
        return component.hoverEvent(entity).insertion(entity.getUuid().toString());
    }

    @NotNull
    public static String getDisplayableEntityType(@NotNull EntityType entityType) {
        final String name = entityType.toString().replace("_", "");
        return WordUtils.capitalizeFully(name);
    }

}
