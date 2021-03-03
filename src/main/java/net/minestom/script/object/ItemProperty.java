package net.minestom.script.object;

import net.minestom.server.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class ItemProperty extends Properties {

    public ItemProperty(@NotNull ItemStack itemStack) {
        putMember("material", itemStack.getMaterial().toString());
        putMember("amount", itemStack.getAmount());
    }

}
