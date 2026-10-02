package ziyue.tjmetro.mapping;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;

/**
 * @author ZiYueCommentary
 * @since 1.0.0-beta-2
 */

public interface RegistryHelper
{
    static net.minecraft.world.item.ItemStack cloneSingleItemStack(net.minecraft.world.item.ItemStack itemStack) {
        return new net.minecraft.world.item.ItemStack(itemStack.getItem());
    }

    static ResourceLocation getIdentifierByItem(net.minecraft.world.item.Item item) {
        return BuiltInRegistries.ITEM.getKey(item);
    }

    static net.minecraft.world.item.ItemStack getItemStackByIdentifier(ResourceLocation identifier) {
        return BuiltInRegistries.ITEM.get(identifier).getDefaultInstance();
    }
}