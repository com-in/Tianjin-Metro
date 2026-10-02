package ziyue.tjmetro.mod.mixin;

import org.mtr.render.RenderVehicles;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ziyue.tjmetro.mod.config.ConfigClient;

/**
 * @author ZiYueCommentary
 * @since 1.0.0-prerelease-1
 *
 * UNKNOWN: MTR 4.1 removed {@code org.mtr.render.DynamicVehicleModel}. The "disable train rendering"
 * behaviour is now applied at the top level by cancelling {@link RenderVehicles#render(long, net.minecraft.world.phys.Vec3)}.
 */
@Mixin(RenderVehicles.class)
public abstract class DynamicVehicleModelMixin
{
    @Inject(at = @At("HEAD"), method = "render", cancellable = true, remap = false)
    private static void beforeRender(long vehicleId, net.minecraft.world.phys.Vec3 cameraPosition, CallbackInfo ci) {
        if (ConfigClient.DISABLE_TRAIN_RENDERING.get()) ci.cancel();
    }
}
