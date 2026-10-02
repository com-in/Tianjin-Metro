package ziyue.tjmetro.mod.mixin;

import org.mtr.screen.RailwaySignScreen;
import org.spongepowered.asm.mixin.Mixin;

/**
 * @author ZiYueCommentary
 * @since 1.0.0-beta-1
 *
 * UNKNOWN: MTR 4.1 rewrote {@link RailwaySignScreen} with Elementa and removed {@code setNewSignId}
 * and the fields it relied on. Dynamic Tianjin signs are now handled by the mod's own
 * {@code ziyue.tjmetro.mod.screen.RailwaySignScreen}. This mixin is kept as a compilation-safe placeholder.
 */

@Mixin(RailwaySignScreen.class)
public abstract class RailwaySignScreenMixin
{
}
