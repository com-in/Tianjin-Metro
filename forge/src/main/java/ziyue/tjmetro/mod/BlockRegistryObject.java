package ziyue.tjmetro.mod;

import net.minecraft.world.level.block.Block;

import java.util.function.Supplier;

/**
 * Handle for a block registered in the {@code tjmetro} namespace.
 */

public class BlockRegistryObject
{
    final Supplier<Block> holder;

    BlockRegistryObject(Supplier<Block> holder) {
        this.holder = holder;
    }

    public Block get() {
        return holder.get();
    }
}
