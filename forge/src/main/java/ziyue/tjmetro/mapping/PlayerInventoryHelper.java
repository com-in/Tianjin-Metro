package ziyue.tjmetro.mapping;

import net.minecraft.world.entity.Entity;
import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.function.Function;

/**
 * @author ZiYueCommentary
 * @since 1.0.0-beta-2
 */

public class PlayerInventoryHelper
{
    public static void clearItems(Player player, Function<Item, Boolean> filter) {
        final NonNullList<ItemStack> items = player.getInventory().items;
        for (int i = 0; i < items.size(); i++) {
            if (filter.apply(items.get(i).getItem())) items.set(i, ItemStack.EMPTY);
        }

        final NonNullList<ItemStack> armor = player.getInventory().armor;
        for (int i = 0; i < armor.size(); i++) {
            if (filter.apply(armor.get(i).getItem())) armor.set(i, ItemStack.EMPTY);
        }

        final NonNullList<ItemStack> offhand = player.getInventory().offhand;
        for (int i = 0; i < offhand.size(); i++) {
            if (filter.apply(offhand.get(i).getItem())) offhand.set(i, ItemStack.EMPTY);
        }
    }
}