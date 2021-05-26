package net.minestom.script.property;

import net.minestom.server.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class ItemProperty extends Properties {

    private final ItemStack itemStack;

    public ItemProperty(@NotNull ItemStack itemStack) {
        this.itemStack = itemStack;
        Properties.applyExtensions(ItemProperty.class, itemStack, this);
        putMember("material", itemStack.getMaterial().toString());
        putMember("amount", itemStack.getAmount());
    }

    @Override
    public String toString() {
        final String namespace = itemStack.getMaterial().getName();
        final String nbt = itemStack.getMeta().toSNBT();
        return namespace + nbt;
    }
}
