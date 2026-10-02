package ziyue.tjmetro.mod.screen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import org.mtr.client.IDrawing;
import org.mtr.data.IGui;
import ziyue.tjmetro.mod.RegistryClient;
import ziyue.tjmetro.mod.TianjinMetro;
import ziyue.tjmetro.mod.packet.PacketUpdateRoadblockContent;

import static org.mtr.data.IGui.*;

/**
 * Custom content to display for Custom Content Block.
 *
 * @author ZiYueCommentary
 * @see PacketUpdateRoadblockContent
 * @since 1.0.0-beta-1
 */

public class RoadblockContentScreen extends Screen implements IGui
{
    protected final BlockPos pos;
    protected final EditBox textField;
    protected String content;
    private static final int MAX_MESSAGE_LENGTH = 256;

    public RoadblockContentScreen(BlockPos pos, String content) {
        super(Component.empty());
        this.pos = pos;
        this.content = content;
        textField = new EditBox(Minecraft.getInstance().font, 0, 0, 0, SQUARE_SIZE, Component.empty());
        textField.setMaxLength(MAX_MESSAGE_LENGTH);
    }

    @Override
    protected void init() {
        super.init();
        addRenderableWidget(textField);
        IDrawing.setPositionAndWidth(textField, SQUARE_SIZE, SQUARE_SIZE, width);
        textField.setValue(content);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        try {
            renderBackground(guiGraphics, mouseX, mouseY, delta);
            guiGraphics.drawString(Minecraft.getInstance().font, Component.translatable("gui.tjmetro.custom_content"), SQUARE_SIZE, TEXT_PADDING, ARGB_WHITE);
            super.render(guiGraphics, mouseX, mouseY, delta);
        } catch (Exception e) {
            TianjinMetro.LOGGER.error(e.getMessage(), e);
        }
    }

    @Override
    public void onClose() {
        RegistryClient.sendPacketToServer(new PacketUpdateRoadblockContent(pos, textField.getValue()));
        super.onClose();
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
