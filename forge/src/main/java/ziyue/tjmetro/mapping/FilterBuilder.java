package ziyue.tjmetro.mapping;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.CreativeModeTabRegistry;
import org.mtr.registry.ObjectHolder;
import ziyue.filters.Filter;

import java.util.function.Supplier;

/**
 * @since 1.0.0-beta-1
 */
public interface FilterBuilder
{
    static Filter registerFilter(String creativeModeTab, Component filterName, Supplier<ItemStack> filterIcon) {
        return ziyue.filters.FilterBuilder.registerFilter(getTab(creativeModeTab), filterName, filterIcon);
    }

    static Filter registerUncategorizedItemsFilter(String creativeModeTab) {
        return ziyue.filters.FilterBuilder.registerUncategorizedItemsFilter(getTab(creativeModeTab));
    }

    static void filtersVisibility(String creativeModeTab, boolean visible) {
        if (!ModLoaderHelper.hasFilters()) return;
        ziyue.filters.FilterBuilder.filtersVisibility(getTab(creativeModeTab), visible);
    }

    static void setReservedButton(String creativeModeTab, Component tooltip, Button.OnPress onPress) {
        ziyue.filters.FilterBuilder.setReservedButton(getTab(creativeModeTab), tooltip, onPress);
    }

    static void addBlocks(Filter filter, ObjectHolder<Block>... blocks) {
        for (ObjectHolder<Block> block : blocks) {
            filter.addItems(block.get().asItem());
        }
    }

    static void addItems(Filter filter, ObjectHolder<Item>... items) {
        for (ObjectHolder<Item> item : items) {
            filter.addItems(item.get());
        }
    }

    private static CreativeModeTab getTab(String tabId) {
        return CreativeModeTabRegistry.getTab(ResourceLocation.fromNamespaceAndPath(ziyue.tjmetro.mod.Reference.MOD_ID, tabId));
    }
}