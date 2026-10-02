package ziyue.tjmetro.mod.screen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import org.apache.commons.lang3.StringUtils;
import org.mtr.client.IDrawing;
import org.mtr.data.IGui;
import ziyue.tjmetro.mod.RegistryClient;
import ziyue.tjmetro.mod.block.base.BlockCustomColorBase;
import ziyue.tjmetro.mod.packet.PacketUpdateCustomColor;

import java.awt.Color;
import java.util.Locale;

import static org.mtr.data.IGui.*;

/**
 * @author ZiYueCommentary
 * @see PacketUpdateCustomColor
 * @since 1.0.0-beta-1
 */

public class ColorPickerScreen extends Screen implements IGui
{
    protected float hue;
    protected float saturation;
    protected float brightness;
    protected DraggingState draggingState = DraggingState.NONE;

    protected final BlockPos pos;
    protected final int oldColor;
    protected final BlockCustomColorBase.BlockEntityBase entity;
    protected final EditBox textFieldColor;
    protected final EditBox textFieldRed;
    protected final EditBox textFieldGreen;
    protected final EditBox textFieldBlue;
    protected final Checkbox checkboxDefaultColor;
    protected final Button buttonReset;

    protected static final int RIGHT_WIDTH = 60;

    public ColorPickerScreen(BlockPos pos, BlockCustomColorBase.BlockEntityBase entity) {
        super(Component.empty());
        this.pos = pos;
        this.oldColor = entity.color;
        this.entity = entity;

        textFieldColor = new EditBox(Minecraft.getInstance().font, 0, 0, 0, SQUARE_SIZE, Component.empty());
        textFieldColor.setMaxLength(6);
        textFieldColor.setFilter(text -> text.matches("[0-9a-fA-F]*"));
        textFieldColor.setValue(Integer.toHexString(oldColor).toUpperCase(Locale.ENGLISH));

        textFieldRed = new EditBox(Minecraft.getInstance().font, 0, 0, 0, SQUARE_SIZE, Component.empty());
        textFieldGreen = new EditBox(Minecraft.getInstance().font, 0, 0, 0, SQUARE_SIZE, Component.empty());
        textFieldBlue = new EditBox(Minecraft.getInstance().font, 0, 0, 0, SQUARE_SIZE, Component.empty());
        for (final EditBox editBox : new EditBox[]{textFieldRed, textFieldGreen, textFieldBlue}) {
            editBox.setMaxLength(3);
            editBox.setFilter(text -> text.matches("\\d*"));
        }
        textFieldRed.setValue(String.valueOf((oldColor >> 16) & 0xFF));
        textFieldGreen.setValue(String.valueOf((oldColor >> 8) & 0xFF));
        textFieldBlue.setValue(String.valueOf(oldColor & 0xFF));

        checkboxDefaultColor = Checkbox.builder(Component.translatable("gui.tjmetro.default_color"), Minecraft.getInstance().font).pos(0, 0).selected(oldColor == -1).onValueChange((checkbox, checked) -> {
            if (checked) {
                setHsb(entity.getDefaultColor(pos), true);
            }
        }).build();

        buttonReset = Button.builder(Component.translatable("gui.mtr.reset_sign"), button -> {
            setHsb(oldColor, true);
            IGui.setChecked(checkboxDefaultColor, false);
            button.active = false;
        }).bounds(0, 0, 0, SQUARE_SIZE).build();
    }

    @Override
    protected void init() {
        super.init();

        final int startX = SQUARE_SIZE * 4 + getMainWidth();
        final int startY = SQUARE_SIZE + TEXT_HEIGHT + TEXT_PADDING + TEXT_FIELD_PADDING / 2;
        IDrawing.setPositionAndWidth(textFieldColor, startX + TEXT_FIELD_PADDING / 2, startY + 25, RIGHT_WIDTH - TEXT_FIELD_PADDING);
        IDrawing.setPositionAndWidth(textFieldRed, startX + TEXT_FIELD_PADDING / 2, startY + SQUARE_SIZE * 2 + TEXT_FIELD_PADDING + 25, RIGHT_WIDTH - TEXT_FIELD_PADDING);
        IDrawing.setPositionAndWidth(textFieldGreen, startX + TEXT_FIELD_PADDING / 2, startY + SQUARE_SIZE * 3 + TEXT_FIELD_PADDING * 2 + 25, RIGHT_WIDTH - TEXT_FIELD_PADDING);
        IDrawing.setPositionAndWidth(textFieldBlue, startX + TEXT_FIELD_PADDING / 2, startY + SQUARE_SIZE * 4 + TEXT_FIELD_PADDING * 3 + 25, RIGHT_WIDTH - TEXT_FIELD_PADDING);
        IDrawing.setPositionAndWidth(checkboxDefaultColor, SQUARE_SIZE * 4 + getMainWidth() + 3, SQUARE_SIZE, RIGHT_WIDTH - TEXT_FIELD_PADDING);
        IDrawing.setPositionAndWidth(buttonReset, startX, getMainHeight(), RIGHT_WIDTH);

        setHsb(oldColor == -1 ? entity.getDefaultColor(pos) : oldColor, true);

        textFieldColor.setResponder(text -> textCallback(text, -1));
        textFieldRed.setResponder(text -> textCallback(text, 16));
        textFieldGreen.setResponder(text -> textCallback(text, 8));
        textFieldBlue.setResponder(text -> textCallback(text, 0));

        addRenderableWidget(textFieldColor);
        addRenderableWidget(textFieldRed);
        addRenderableWidget(textFieldGreen);
        addRenderableWidget(textFieldBlue);
        addRenderableWidget(checkboxDefaultColor);
        addRenderableWidget(buttonReset);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        renderBackground(guiGraphics, mouseX, mouseY, delta);
        super.render(guiGraphics, mouseX, mouseY, delta);

        final int mainWidth = getMainWidth();
        final int mainHeight = getMainHeight();

        guiGraphics.drawCenteredString(Minecraft.getInstance().font, Component.translatable("gui.mtr.color"), SQUARE_SIZE * 4 + mainWidth + RIGHT_WIDTH / 2, SQUARE_SIZE + 25, ARGB_WHITE);
        guiGraphics.drawCenteredString(Minecraft.getInstance().font, "RGB", SQUARE_SIZE * 4 + mainWidth + RIGHT_WIDTH / 2, SQUARE_SIZE * 3 + TEXT_FIELD_PADDING + 25, ARGB_WHITE);

        final int selectedColor = Color.HSBtoRGB(hue, saturation, brightness);
        guiGraphics.fill(SQUARE_SIZE * 4 + mainWidth + 3, SQUARE_SIZE * 7 + TEXT_FIELD_PADDING * 4 + 26, SQUARE_SIZE * 4 + mainWidth + RIGHT_WIDTH - 1, mainHeight - 1, 0xFF000000 | selectedColor);

        for (int drawHue = 0; drawHue < mainHeight; drawHue++) {
            final int color = Color.HSBtoRGB((float) drawHue / (mainHeight - 1), 1, 1);
            guiGraphics.fill(SQUARE_SIZE * 2 + mainWidth, SQUARE_SIZE + drawHue, SQUARE_SIZE * 3 + mainWidth, SQUARE_SIZE + drawHue + 1, 0xFF000000 | color);
        }

        for (int drawSaturation = 0; drawSaturation < mainWidth; drawSaturation++) {
            for (int drawBrightness = 0; drawBrightness < mainHeight; drawBrightness++) {
                final int color = Color.HSBtoRGB(hue, (float) drawSaturation / (mainWidth - 1), (float) drawBrightness / (mainHeight - 1));
                guiGraphics.fill(SQUARE_SIZE + drawSaturation, SQUARE_SIZE + mainHeight - drawBrightness - 1, SQUARE_SIZE + drawSaturation + 1, SQUARE_SIZE + mainHeight - drawBrightness, 0xFF000000 | color);
            }
        }

        final int selectedHueInt = Math.round(hue * (mainHeight - 1));
        final int selectedSaturationInt = Math.round(saturation * (mainWidth - 1));
        final int selectedBrightnessInt = Math.round(brightness * (mainHeight - 1));
        guiGraphics.fill(SQUARE_SIZE * 2 + mainWidth, SQUARE_SIZE + selectedHueInt - 1, SQUARE_SIZE * 3 + mainWidth, SQUARE_SIZE + selectedHueInt + 2, ARGB_BLACK);
        guiGraphics.fill(SQUARE_SIZE * 2 + mainWidth, SQUARE_SIZE + selectedHueInt, SQUARE_SIZE * 3 + mainWidth, SQUARE_SIZE + selectedHueInt + 1, ARGB_WHITE);
        guiGraphics.fill(SQUARE_SIZE + selectedSaturationInt - 1, SQUARE_SIZE + mainHeight - selectedBrightnessInt - 1, SQUARE_SIZE + selectedSaturationInt + 2, SQUARE_SIZE + mainHeight - selectedBrightnessInt, ARGB_BLACK);
        guiGraphics.fill(SQUARE_SIZE + selectedSaturationInt, SQUARE_SIZE + mainHeight - selectedBrightnessInt - 2, SQUARE_SIZE + selectedSaturationInt + 1, SQUARE_SIZE + mainHeight - selectedBrightnessInt + 1, ARGB_BLACK);
        guiGraphics.fill(SQUARE_SIZE + selectedSaturationInt, SQUARE_SIZE + mainHeight - selectedBrightnessInt - 1, SQUARE_SIZE + selectedSaturationInt + 1, SQUARE_SIZE + mainHeight - selectedBrightnessInt, ARGB_WHITE);
    }

    @Override
    public void onClose() {
        RegistryClient.sendPacketToServer(new PacketUpdateCustomColor(pos, checkboxDefaultColor.selected() ? -1 : Color.HSBtoRGB(hue, saturation, brightness) & RGB_WHITE));
        super.onClose();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        final int mainWidth = getMainWidth();
        final int mainHeight = getMainHeight();
        draggingState = DraggingState.NONE;
        if (mouseY >= SQUARE_SIZE && mouseY < SQUARE_SIZE + mainHeight) {
            if (mouseX >= SQUARE_SIZE && mouseX < SQUARE_SIZE + mainWidth) {
                draggingState = DraggingState.SATURATION_BRIGHTNESS;
            } else if (mouseX >= SQUARE_SIZE * 2 + mainWidth && mouseX < SQUARE_SIZE * 3 + mainWidth) {
                draggingState = DraggingState.HUE;
            }
        }
        selectColor(mouseX, mouseY);
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        selectColor(mouseX, mouseY);
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    protected void selectColor(double mouseX, double mouseY) {
        if (checkboxDefaultColor.selected()) return;

        final int mainWidth = getMainWidth();
        final int mainHeight = getMainHeight();
        switch (draggingState) {
            case SATURATION_BRIGHTNESS:
                saturation = (float) Mth.clamp((mouseX - SQUARE_SIZE) / mainWidth, 0, 1);
                brightness = 1 - (float) Mth.clamp((mouseY - SQUARE_SIZE) / mainHeight, 0, 1);
                setColorText(Color.HSBtoRGB(hue, saturation, brightness), true);
                break;
            case HUE:
                hue = (float) Mth.clamp((mouseY - SQUARE_SIZE) / mainHeight, 0, 1);
                setColorText(Color.HSBtoRGB(hue, saturation, brightness), true);
                break;
        }
    }

    protected void setHsb(int color, boolean padZero) {
        final float[] hsb = Color.RGBtoHSB((color >> 16) & 0xFF, (color >> 8) & 0xFF, color & 0xFF, null);
        hue = hsb[0];
        saturation = hsb[1];
        brightness = hsb[2];
        setColorText(color, padZero);
    }

    protected void setColorText(int color, boolean padZero) {
        final String colorString = Integer.toHexString(color & RGB_WHITE).toUpperCase(Locale.ENGLISH);
        textFieldColor.setValue(padZero ? StringUtils.leftPad(colorString, 6, "0") : colorString);
        textFieldRed.setValue(String.valueOf((color >> 16) & 0xFF));
        textFieldGreen.setValue(String.valueOf((color >> 8) & 0xFF));
        textFieldBlue.setValue(String.valueOf(color & 0xFF));
        buttonReset.active = (color & RGB_WHITE) != oldColor;
    }

    protected void textCallback(String text, int shift) {
        try {
            final boolean isHex = shift < 0;
            final int compare = Integer.parseInt(text, isHex ? 16 : 10);
            final int currentColor = Color.HSBtoRGB(hue, saturation, brightness) & RGB_WHITE;
            if ((isHex ? currentColor : ((currentColor >> shift) & 0xFF)) != compare) {
                setHsb(isHex ? compare : (currentColor & ~(0xFF << shift)) + (compare << shift), !isHex);
            }
        } catch (Exception ignored) {
        }
    }

    protected int getMainWidth() {
        return width - SQUARE_SIZE * 5 - RIGHT_WIDTH;
    }

    protected int getMainHeight() {
        return height - SQUARE_SIZE * 2;
    }

    protected enum DraggingState
    {
        NONE, SATURATION_BRIGHTNESS, HUE
    }
}
