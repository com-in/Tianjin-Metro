package ziyue.tjmetro.mod;

import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.function.Supplier;

/**
 * Handle for a block entity type registered in the {@code tjmetro} namespace.
 */

public class BlockEntityTypeRegistryObject<T extends BlockEntity>
{
    final Supplier<BlockEntityType<T>> holder;

    BlockEntityTypeRegistryObject(Supplier<BlockEntityType<T>> holder) {
        this.holder = holder;
    }

    public BlockEntityType<T> get() {
        return holder.get();
    }
}
