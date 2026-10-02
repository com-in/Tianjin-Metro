package ziyue.tjmetro.mod.block;

import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Implemented by blocks that contribute tooltip lines. Vanilla places tooltips on items, so
 * {@link ziyue.tjmetro.mod.item.BlockItemExtension} forwards {@code appendHoverText} to this method.
 *
 * @author ZiYueCommentary
 * @since 1.0.0-beta-1
 */

public interface IBlockTooltip
{
    void addTooltips(ItemStack stack, @Nullable BlockGetter world, List<MutableComponent> tooltip, Item.TooltipContext options);
}
