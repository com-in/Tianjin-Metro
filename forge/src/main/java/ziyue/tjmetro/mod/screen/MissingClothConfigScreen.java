package ziyue.tjmetro.mod.screen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.mtr.client.IDrawing;
import ziyue.tjmetro.mapping.ConfirmLinkScreenHelper;
import ziyue.tjmetro.mod.TianjinMetro;

import static org.mtr.data.IGui.*;

/**
 * @author ZiYueCommentary
 * @since 1.0.0-beta-1
 */

public class MissingClothConfigScreen extends Screen
{
    protected final Screen parent;
    protected final Button buttonDownload;

    public MissingClothConfigScreen(Screen parent) {
        super(Component.empty());
        this.parent = parent;
        buttonDownload = Button.builder(Component.empty(), button -> {
            ConfirmLinkScreenHelper.open(this, "https://modrinth.com/mod/cloth-config", true);
            this.onClose();
        }).bounds(0, 0, 0, 20).build();
    }

    @Override
    protected void init() {
        super.init();
        addRenderableWidget(buttonDownload);
        IDrawing.setPositionAndWidth(buttonDownload, (width - 150) / 2, height / 2 + TEXT_PADDING, 150);

        buttonDownload.setMessage(Component.translatable("config.tjmetro.download_cloth_config"));
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        try {
            renderBackground(guiGraphics, mouseX, mouseY, delta);
            guiGraphics.drawCenteredString(Minecraft.getInstance().font, Component.translatable("config.tjmetro.cloth_config_not_found"), width / 2, height / 2 - TEXT_PADDING * 3, ARGB_WHITE);
            guiGraphics.drawCenteredString(Minecraft.getInstance().font, Component.translatable("config.tjmetro.cloth_config_required"), width / 2, height / 2 - TEXT_PADDING, ARGB_WHITE);
            super.render(guiGraphics, mouseX, mouseY, delta);
        } catch (Exception e) {
            TianjinMetro.LOGGER.error(e.getMessage(), e);
        }
    }

    @Override
    public void onClose() {
        Minecraft.getInstance().setScreen(parent);
    }
}
