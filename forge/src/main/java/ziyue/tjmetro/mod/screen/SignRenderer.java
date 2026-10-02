package ziyue.tjmetro.mod.screen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import org.mtr.client.CustomResourceLoader;
import org.mtr.libraries.it.unimi.dsi.fastutil.longs.LongAVLTreeSet;
import org.mtr.resource.SignResource;

import static org.mtr.data.IGui.*;

/**
 * Native replacement for the removed {@code RenderRailwaySign.drawSign}/{@code getMaxWidth} GUI helpers.
 * Only used to preview signs inside the railway sign configuration screens.
 *
 * @author ZiYueCommentary
 * @since 1.0.0-beta-1
 */
public final class SignRenderer
{
    private static final float SMALL_SIGN_PERCENTAGE = 1 - SignResource.SMALL_SIGN_PADDING * 2;

    private SignRenderer() {
    }

    public static SignResource getSign(String signId) {
        return signId == null ? null : CustomResourceLoader.getSignById(signId);
    }

    public static float getMaxWidth(String[] signIds, int index, boolean right) {
        float width = 1;
        if (signIds == null) {
            return width;
        }
        if (right) {
            for (int i = index + 1; i < signIds.length; i++) {
                if (signIds[i] == null) {
                    break;
                }
                width++;
            }
        } else {
            for (int i = index - 1; i >= 0; i--) {
                if (signIds[i] == null) {
                    break;
                }
                width++;
            }
        }
        return width;
    }

    public static void drawSign(GuiGraphics guiGraphics, BlockPos signPos, String signId, float x, float y, float size, float maxWidthLeft, float maxWidthRight, LongAVLTreeSet selectedIds, Direction facing, int backgroundColor) {
        final SignResource sign = getSign(signId);
        if (sign == null) {
            return;
        }

        final float signSize = (sign.getSmall() ? SMALL_SIGN_PERCENTAGE : 1) * size;
        final float margin = (size - signSize) / 2;

        if (sign.hasCustomText) {
            final String text = sign.getCustomText();
            if (text != null && !text.isEmpty()) {
                final Font font = Minecraft.getInstance().font;
                guiGraphics.drawCenteredString(font, text, (int) (x + size / 2), (int) (y + margin + (signSize - font.lineHeight) / 2), ARGB_WHITE);
            }
        } else {
            guiGraphics.blit(sign.textureId, (int) (x + margin), (int) (y + margin), 0, 0, (int) signSize, (int) signSize, (int) signSize, (int) signSize);
        }
    }
}
