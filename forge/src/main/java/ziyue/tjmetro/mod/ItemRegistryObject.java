package ziyue.tjmetro.mod;

import net.minecraft.world.item.Item;

import java.util.function.Supplier;

/**
 * Handle for an item registered in the {@code tjmetro} namespace.
 */

public class ItemRegistryObject
{
    final Supplier<Item> holder;

    ItemRegistryObject(Supplier<Item> holder) {
        this.holder = holder;
    }

    public Item get() {
        return holder.get();
    }
}
