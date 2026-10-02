package ziyue.tjmetro.mod.item;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.Block;
import ziyue.tjmetro.mod.block.IBlockTooltip;

import java.util.List;

/**
 * Block item that forwards vanilla tooltip handling to the block's {@link IBlockTooltip#addTooltips}.
 *
 * @author ZiYueCommentary
 * @since 1.0.0-beta-1
 */

public class BlockItemExtension extends BlockItem
{
    public BlockItemExtension(Block block, Item.Properties properties) {
        super(block, properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        if (getBlock() instanceof IBlockTooltip block) {
            //noinspection unchecked
            block.addTooltips(stack, context.level(), (List<MutableComponent>) (List<?>) tooltip, context);
        }
    }
}
