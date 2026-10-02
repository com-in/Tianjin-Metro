package ziyue.tjmetro.mod;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;

import java.util.function.Supplier;

/**
 * Handle for a registered entity type. MTR 4.1 has no entity registry, so this backs a NeoForge DeferredHolder.
 */

public class EntityTypeRegistryObject<T extends Entity>
{
    final Supplier<EntityType<T>> supplier;

    EntityTypeRegistryObject(Supplier<EntityType<T>> supplier) {
        this.supplier = supplier;
    }

    public EntityType<T> get() {
        return supplier.get();
    }
}
