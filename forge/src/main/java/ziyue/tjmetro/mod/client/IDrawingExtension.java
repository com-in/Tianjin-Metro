package ziyue.tjmetro.mod.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import org.mtr.client.IDrawing;
import org.mtr.data.IGui;
import org.mtr.libraries.it.unimi.dsi.fastutil.booleans.BooleanArrayList;
import org.mtr.libraries.it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.mtr.libraries.it.unimi.dsi.fastutil.objects.ObjectObjectImmutablePair;
import ziyue.tjmetro.mod.Reference;
import ziyue.tjmetro.mod.config.ConfigClient;

import javax.annotation.Nullable;

import static org.mtr.data.IGui.LINE_HEIGHT;
import static org.mtr.data.IGui.TEXT_HEIGHT;

/**
 * Some methods similar to methods in <b>IDrawing</b>.
 *
 * @see IDrawing
 * @since 1.0.0-beta-1
 */

public interface IDrawingExtension
{
    static void drawStringWithFont(PoseStack poseStack, MultiBufferSource bufferSource, String text, float x, float y, int light) {
        drawStringWithFont(poseStack, bufferSource, text, IGui.HorizontalAlignment.CENTER, IGui.VerticalAlignment.CENTER, x, y, -1, -1, 1, IGui.ARGB_WHITE, true, light, null);
    }

    static void drawStringWithFont(PoseStack poseStack, MultiBufferSource bufferSource, String text, IGui.HorizontalAlignment horizontalAlignment, IGui.VerticalAlignment verticalAlignment, float x, float y, float maxWidth, float maxHeight, float scale, int textColor, boolean shadow, int light, @Nullable IDrawing.DrawingCallback drawingCallback) {
        drawStringWithFont(poseStack, bufferSource, text, horizontalAlignment, verticalAlignment, horizontalAlignment, x, y, maxWidth, maxHeight, scale, textColor, shadow, light, drawingCallback);
    }

    static void drawStringWithFont(PoseStack poseStack, MultiBufferSource bufferSource, String text, IGui.HorizontalAlignment horizontalAlignment, IGui.VerticalAlignment verticalAlignment, IGui.HorizontalAlignment xAlignment, float x, float y, float maxWidth, float maxHeight, float scale, int textColor, boolean shadow, int light, @Nullable IDrawing.DrawingCallback drawingCallback) {
        drawStringWithFont(poseStack, bufferSource, text, horizontalAlignment, verticalAlignment, xAlignment, x, y, maxWidth, maxHeight, scale, textColor, textColor, 2, shadow, light, false, drawingCallback);
    }

    /**
     * @author ZiYueCommentary
     * @since 1.0.0
     */
    static void drawStringWithFont(PoseStack poseStack, MultiBufferSource bufferSource, String text, IGui.HorizontalAlignment horizontalAlignment, IGui.VerticalAlignment verticalAlignment, IGui.HorizontalAlignment xAlignment, float x, float y, float maxWidth, float maxHeight, float scale, int textColorCjk, int textColor, float fontSizeRatio, boolean shadow, int light, boolean forceMinecraftFont, @Nullable IDrawing.DrawingCallback drawingCallback) {
        final Style style;
        final int height;
        if (!forceMinecraftFont && ConfigClient.USE_TIANJIN_METRO_FONT.get()) {
            style = Style.EMPTY.withFont(ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, "tjmetro"));
            y += 0.05F;
            height = TEXT_HEIGHT;
        } else {
            style = Style.EMPTY;
            height = LINE_HEIGHT;
        }

        while (text.contains("||")) {
            text = text.replace("||", "|");
        }
        final String[] stringSplit = text.split("\\|");

        final BooleanArrayList isCJKList = new BooleanArrayList();
        final ObjectArrayList<FormattedCharSequence> orderedTexts = new ObjectArrayList<>();
        int totalHeight = 0, totalWidth = 0;
        for (final String stringSplitPart : stringSplit) {
            final boolean isCJK = IGui.isCjk(stringSplitPart);
            isCJKList.add(isCJK);

            final FormattedCharSequence orderedText = Component.literal(stringSplitPart).withStyle(style).getVisualOrderText();
            orderedTexts.add(orderedText);

            totalHeight += Math.round(height * (isCJK ? fontSizeRatio : 1));
            final int width = (int) Math.ceil(Minecraft.getInstance().font.width(orderedText) * (isCJK ? fontSizeRatio : 1));
            if (width > totalWidth) {
                totalWidth = width;
            }
        }

        if (maxHeight >= 0 && totalHeight / scale > maxHeight) {
            scale = totalHeight / maxHeight;
        }

        poseStack.pushPose();

        final float totalWidthScaled;
        final float scaleX;
        if (maxWidth >= 0 && totalWidth > maxWidth * scale) {
            totalWidthScaled = maxWidth * scale;
            scaleX = totalWidth / maxWidth;
        } else {
            totalWidthScaled = totalWidth;
            scaleX = scale;
        }
        poseStack.scale(1 / scaleX, 1 / scale, 1 / scale);

        float offset = verticalAlignment.getOffset(y * scale, totalHeight);
        for (int i = 0; i < orderedTexts.size(); i++) {
            final boolean isCJK = isCJKList.getBoolean(i);
            final float extraScale = isCJK ? fontSizeRatio : 1;
            if (isCJK) {
                poseStack.pushPose();
                poseStack.scale(extraScale, extraScale, 1);
            }

            final float xOffset = horizontalAlignment.getOffset(xAlignment.getOffset(x * scaleX, totalWidth), Minecraft.getInstance().font.width(orderedTexts.get(i)) * extraScale - totalWidth);

            final int color = isCJK ? textColorCjk : textColor;
            final float shade = light == LightTexture.FULL_BRIGHT ? 1 : Math.min(LightTexture.block(light) / 16F * 0.1F + 0.7F, 1);
            final int a = (color >> 24) & 0xFF;
            final int r = (int) (((color >> 16) & 0xFF) * shade);
            final int g = (int) (((color >> 8) & 0xFF) * shade);
            final int b = (int) ((color & 0xFF) * shade);

            Minecraft.getInstance().font.drawInBatch(orderedTexts.get(i), Math.round(xOffset / extraScale), Math.round(offset / extraScale), (a << 24) + (r << 16) + (g << 8) + b, shadow, poseStack.last().pose(), bufferSource, Font.DisplayMode.NORMAL, 0, LightTexture.FULL_BRIGHT);

            if (isCJK) {
                poseStack.popPose();
            }

            offset += height * extraScale;
        }

        poseStack.popPose();

        if (drawingCallback != null) {
            final float x1 = xAlignment.getOffset(x, totalWidthScaled / scale);
            final float y1 = verticalAlignment.getOffset(y, totalHeight / scale);
            drawingCallback.drawingCallback(x1, y1, x1 + totalWidthScaled / scale, y1 + totalHeight / scale);
        }
    }

    static ObjectObjectImmutablePair<Float, Float> stringWidthWithFont(String text, float scale, float fontSizeRatio, boolean forceMinecraftFont) {
        final int height;
        if (!forceMinecraftFont && ConfigClient.USE_TIANJIN_METRO_FONT.get()) {
            height = TEXT_HEIGHT;
        } else {
            height = LINE_HEIGHT;
        }

        while (text.contains("||")) {
            text = text.replace("||", "|");
        }
        final String[] stringSplit = text.split("\\|");

        int totalHeight = 0, totalWidth = 0;
        for (final String stringSplitPart : stringSplit) {
            final boolean isCJK = IGui.isCjk(stringSplitPart);

            final FormattedCharSequence orderedText = Component.literal(stringSplitPart).getVisualOrderText();

            totalHeight += Math.round(height * (isCJK ? fontSizeRatio : 1));
            final int width = (int) Math.ceil(Minecraft.getInstance().font.width(orderedText) * (isCJK ? fontSizeRatio : 1));
            if (width > totalWidth) {
                totalWidth = width;
            }
        }

        return ObjectObjectImmutablePair.of(totalWidth / scale, totalHeight / scale);
    }
}
