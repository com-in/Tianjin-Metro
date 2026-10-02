package ziyue.tjmetro.mod;

import net.minecraft.world.item.Item;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * Handle for the {@code tjmetro} creative mode tab. Items registered against it are collected here and
 * served to the tab's display generator.
 */

public class CreativeModeTabHolder
{
    final String id;
    private final List<Supplier<Item>> entries = new ArrayList<>();

    CreativeModeTabHolder(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    void addEntry(Supplier<Item> item) {
        entries.add(item);
    }

    List<Supplier<Item>> getEntries() {
        return entries;
    }
}
