package ziyue.tjmetro.mod.mixin;

import org.mtr.screen.SidingScreen;
import org.spongepowered.asm.mixin.Mixin;

/**
 * @author ZiYueCommentary
 * @since 1.1.0
 *
 * UNKNOWN: MTR 4.1 rewrote {@link SidingScreen} with Elementa and removed the fields
 * ({@code textFieldMaxTrains}, the {@code WidgetShorterSlider}s, the checkbox widgets, ...) that the
 * original "duplicate siding settings" injection relied on. This mixin is kept as a compilation-safe
 * placeholder until the 4.1 siding screen can be re-targeted.
 */

@Mixin(SidingScreen.class)
public abstract class SidingScreenMixin
{
}
