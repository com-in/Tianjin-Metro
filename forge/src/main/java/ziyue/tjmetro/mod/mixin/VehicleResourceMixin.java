package ziyue.tjmetro.mod.mixin;

import org.mtr.resource.VehicleResource;
import org.spongepowered.asm.mixin.Mixin;

/**
 * @author ZiYueCommentary
 * @since 1.0.0-beta-5
 *
 * UNKNOWN: MTR 4.1 removed {@code VehicleResource.queue(...)} and {@code OptimizedModelWrapper}.
 * The "disable train rendering" behaviour is now handled by {@link DynamicVehicleModelMixin}
 * (which cancels {@code org.mtr.render.RenderVehicles.render}). This mixin is kept as a
 * compilation-safe placeholder.
 */

@Mixin(VehicleResource.class)
public abstract class VehicleResourceMixin
{
}
