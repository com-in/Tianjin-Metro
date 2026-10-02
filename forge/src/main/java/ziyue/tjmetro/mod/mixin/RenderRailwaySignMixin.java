package ziyue.tjmetro.mod.mixin;

import org.mtr.render.RenderRailwaySign;
import org.spongepowered.asm.mixin.Mixin;

/**
 * @author ZiYueCommentary
 * @see RenderRailwaySign
 * @since 1.0.0-beta-1
 *
 * UNKNOWN: MTR 4.1 completely rewrote the in-world railway sign rendering pipeline.
 * {@code RenderRailwaySign.drawSign}, {@code renderCustomText}, {@code getSign},
 * {@code BlockRailwaySign.SMALL_SIGN_PERCENTAGE} and the dynamic exit/line/platform
 * sign rendering that the original {@code @Overwrite} relied on no longer exist.
 * The remaining render methods are non-static instance methods that cannot be
 * overwritten with the previous static signature. This mixin is kept as a
 * compilation-safe placeholder until the 4.1 pipeline can be re-targeted.
 */

@Mixin(RenderRailwaySign.class)
public abstract class RenderRailwaySignMixin
{
}
