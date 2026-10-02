package ziyue.tjmetro.mod.render;

import net.minecraft.resources.ResourceLocation;
import org.mtr.client.CustomResourceLoader;
import org.mtr.resource.SignResource;

/**
 * Shared helpers for the Tianjin Metro railway sign renderers.
 *
 * <p>MTR 4.1 removed {@code org.mtr.mapping.*} and with it {@code RenderRailwaySign.getSign},
 * {@code RenderRailwaySign.getMaxWidth} and {@code BlockRailwaySign.SMALL_SIGN_PERCENTAGE}.
 * This class provides the equivalents used by the sign renderers in this package.</p>
 *
 * @author ZiYueCommentary
 * @since 1.0.0-beta-1
 */

public final class RenderRailwaySignHelper
{
    private RenderRailwaySignHelper() {
    }

    /**
     * Equivalent of {@code BlockRailwaySign.SMALL_SIGN_PERCENTAGE} (removed in MTR 4.1).
     * Derived from the remaining padding constant used by {@link SignResource}.
     */
    public static final float SMALL_SIGN_PERCENTAGE = 1 - 2 * SignResource.SMALL_SIGN_PADDING;

    public static SignResource getSign(String signId) {
        return CustomResourceLoader.getSignById(signId);
    }

    /**
     * Returns the number of contiguous empty sign slots next to {@code index} in the given direction.
     * MTR 4.1 inlined this logic into {@code SignResource.getTextSpace}, so it is re-implemented here.
     */
    public static float getMaxWidth(String[] signIds, int index, boolean right) {
        int spaces = 0;
        final int step = right ? 1 : -1;
        for (int i = index + step; i >= 0 && i < signIds.length; i += step) {
            if (signIds[i] == null) {
                spaces++;
            } else {
                break;
            }
        }
        return spaces;
    }

    @FunctionalInterface
    public interface DrawTexture
    {
        void drawTexture(ResourceLocation textureId, float x, float y, float size, boolean flipTexture);
    }
}
