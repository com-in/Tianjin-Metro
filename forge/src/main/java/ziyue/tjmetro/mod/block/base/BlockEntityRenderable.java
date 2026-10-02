package ziyue.tjmetro.mod.block.base;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import org.mtr.block.BlockEntityExtension;

/**
 * @author ZiYueCommentary
 * @since 1.0.0-beta-1
 */

public class BlockEntityRenderable extends BlockEntityExtension
{
    public final float yOffset;
    public final float zOffset;

    public BlockEntityRenderable(BlockEntityType<?> type, BlockPos blockPos, BlockState state, float yOffset, float zOffset) {
        super(type, blockPos, state);
        this.yOffset = yOffset;
        this.zOffset = zOffset;
    }
}
