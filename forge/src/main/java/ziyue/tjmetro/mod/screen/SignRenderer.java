package ziyue.tjmetro.mod.screen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import org.mtr.client.CustomResourceLoader;
import org.mtr.libraries.it.unimi.dsi.fastutil.longs.LongAVLTreeSet;
import org.mtr.resource.SignResource;

import javax.annotation.Nullable;
import java.util.Arrays;

/**
 * GUI 里预览指示牌的小工具。
 * MTR 4.1 把指示牌的绘制收敛到了 {@code SignResource.render(...)}（它自己的按钮/预览组件就是调它画的），
 * 所以这里直接复用 MTR 的实现，而不是自己拼贴图和文字。
 * <p>
 * 关键点：MTR 的消息牌是"一格一格"拼的，带自定义文字的大牌会占多格，
 * 只有 {@code signs} 数组里它的后面（或前面，取决于 flipCustomText）留有 null 空格时，
 * MTR 才会把文字画出来。所以这里必须按整条链/整块按钮的格数传数组，不能一格一格传。
 *
 * @author ZiYueCommentary
 * @since 1.0.0-beta-1
 */
public final class SignRenderer
{
    private SignRenderer() {
    }

    public static SignResource getSign(String signId) {
        return signId == null ? null : CustomResourceLoader.getSignById(signId);
    }

    /**
     * 画一整条指示牌链（顶部预览用）。
     *
     * @param signIds 与链条格数等长的 id 数组，null 表示该格没有牌子
     */
    public static void drawSign(GuiGraphics guiGraphics, BlockPos signPos, String[] signIds, LongAVLTreeSet selectedIds, float x, float y, float size) {
        final SignResource[] signs = new SignResource[signIds.length];
        for (int i = 0; i < signIds.length; i++) {
            signs[i] = getSign(signIds[i]);
        }
        drawSign(guiGraphics, signPos, signs, selectedIds, x, y, size);
    }

    /**
     * 画单个牌子，按 {@code unitCount} 格宽绘制（按钮网格用）。
     * 带自定义文字的大牌固定占 3 格，其余格子留空给文字。
     */
    public static void drawSign(GuiGraphics guiGraphics, BlockPos signPos, @Nullable String signId, int unitCount, LongAVLTreeSet selectedIds, float x, float y, float size) {
        final SignResource sign = getSign(signId);
        if (sign == null) {
            return;
        }

        final SignResource[] signs = new SignResource[unitCount];
        signs[sign.hasCustomText && sign.getFlipCustomText() ? unitCount - 1 : 0] = sign;
        drawSign(guiGraphics, signPos, signs, selectedIds, x, y, size);
    }

    private static void drawSign(GuiGraphics guiGraphics, BlockPos signPos, SignResource[] signs, LongAVLTreeSet selectedIds, float x, float y, float size) {
        final LongAVLTreeSet[] selectedIdsArray = new LongAVLTreeSet[signs.length];
        Arrays.fill(selectedIdsArray, selectedIds);

        final MultiBufferSource.BufferSource bufferSource = Minecraft.getInstance().renderBuffers().bufferSource();
        SignResource.render(guiGraphics.pose(), bufferSource, signPos, x, y, selectedIdsArray, signs, size, 0, true);
        bufferSource.endBatch();
    }
}
