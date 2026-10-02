package ziyue.tjmetro.mod.mixin;

import org.mtr.screen.RouteScreen;
import org.spongepowered.asm.mixin.Mixin;

/**
 * @author ZiYueCommentary
 * @since 1.1.1
 *
 * UNKNOWN: MTR 4.1 replaced {@code EditRouteScreen} with {@link RouteScreen} (final, Elementa based),
 * and removed the shadowed fields ({@code textFieldLightRailRouteNumber}, the checkbox widgets, ...).
 * This mixin is kept as a compilation-safe placeholder until the 4.1 route screen can be re-targeted.
 */

@Mixin(RouteScreen.class)
public abstract class EditRouteScreenMixin
{
}
