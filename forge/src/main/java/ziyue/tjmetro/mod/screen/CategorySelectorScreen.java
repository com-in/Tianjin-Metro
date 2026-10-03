package ziyue.tjmetro.mod.screen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.client.gui.Font;
import org.mtr.client.IDrawing;
import org.mtr.generated.lang.TranslationProvider;
import org.mtr.libraries.it.unimi.dsi.fastutil.longs.LongArrayList;
import org.mtr.libraries.it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.mtr.libraries.it.unimi.dsi.fastutil.objects.ObjectImmutableList;
import org.mtr.screen.DashboardListItem;
import ziyue.tjmetro.mapping.ConfirmLinkScreenHelper;
import ziyue.tjmetro.mod.Reference;
import ziyue.tjmetro.mod.RegistryClient;
import ziyue.tjmetro.mod.block.BlockPIDSTianjin;
import ziyue.tjmetro.mod.packet.PacketUpdatePIDSAdsConfig;

import java.util.ArrayList;
import java.util.List;

import static org.mtr.data.IGui.*;

public class CategorySelectorScreen extends DashboardListSelectorScreen
{
    private final Button buttonOpenTutorial;
    private final BlockPIDSTianjin.BlockEntity entity;
    protected final List<BlockPIDSTianjin.Category> categories;

    public CategorySelectorScreen(BlockPIDSTianjin.BlockEntity entity, Screen previousScreen) {
        super(CategoryForList.getCategoriesForList(), entity.getCategories(), false, true, previousScreen);
        this.buttonOpenTutorial = Button.builder(Component.translatable("button.tjmetro.open_tutorial"), button -> ConfirmLinkScreenHelper.open(this, Reference.PIDS_ADS, true)).bounds(0, 0, 0, SQUARE_SIZE).build();
        this.entity = entity;
        this.categories = new ArrayList<>(BlockPIDSTianjin.CATEGORIES.values());
    }

    @Override
    protected void init() {
        super.init();
        final int spareSpace = Math.max(0, width - SQUARE_SIZE * 4 - PANEL_WIDTH * 2);
        availableList.x = SQUARE_SIZE * 2 + spareSpace;
        selectedList.x = SQUARE_SIZE * 3 + spareSpace + PANEL_WIDTH;
        IDrawing.setPositionAndWidth(buttonDone, SQUARE_SIZE * 2 + spareSpace, height - SQUARE_SIZE * 2, PANEL_WIDTH);
        IDrawing.setPositionAndWidth(buttonOpenTutorial, SQUARE_SIZE * 3 + spareSpace + PANEL_WIDTH, height - SQUARE_SIZE * 2, PANEL_WIDTH);
        addRenderableWidget(buttonOpenTutorial);
    }

    @Override
    protected void renderAdditional(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        final int spareSpace = Math.max(0, width - SQUARE_SIZE * 4 - PANEL_WIDTH * 2);
        final Font font = Minecraft.getInstance().font;
        guiGraphics.drawCenteredString(font, TranslationProvider.GUI_MTR_AVAILABLE.getText(), SQUARE_SIZE * 2 + spareSpace + PANEL_WIDTH / 2, SQUARE_SIZE, ARGB_WHITE);
        guiGraphics.drawCenteredString(font, TranslationProvider.GUI_MTR_SELECTED.getText(), SQUARE_SIZE * 3 + spareSpace + PANEL_WIDTH * 3 / 2, SQUARE_SIZE, ARGB_WHITE);

        final int index = availableList.getHoverItemIndex();
        if (index < 0 || index >= categories.size()) return;
        final BlockPIDSTianjin.Category category = categories.get(index);
        int y = SQUARE_SIZE;
        y = drawWrappedText(guiGraphics, category.name, y, ARGB_WHITE);
        final String description = category.description.getString();
        if (!description.isEmpty()) {
            for (final String text : description.split("[|\n]")) {
                y = drawWrappedText(guiGraphics, Component.literal(text), y, ARGB_LIGHT_GRAY);
            }
        }
    }

    @Override
    public void onClose() {
        RegistryClient.sendPacketToServer(new PacketUpdatePIDSAdsConfig(entity.getBlockPos(), new LongArrayList(selectedIds)));
        super.onClose();
    }

    protected int drawWrappedText(GuiGraphics guiGraphics, MutableComponent component, int y, int color) {
        final Font font = Minecraft.getInstance().font;
        final List<FormattedCharSequence> splitText = font.split(component, Math.max(0, width - SQUARE_SIZE * 4 - PANEL_WIDTH * 2));
        int newY = y;
        for (final FormattedCharSequence formattedCharSequence : splitText) {
            final int nextY = newY + TEXT_HEIGHT + 2;
            if (nextY > height - SQUARE_SIZE - TEXT_HEIGHT) {
                guiGraphics.drawString(font, "...", SQUARE_SIZE, newY, color);
                return height;
            } else {
                guiGraphics.drawString(font, formattedCharSequence, SQUARE_SIZE, newY, color);
            }
            newY = nextY;
        }
        return newY + TEXT_PADDING;
    }

    public static class CategoryForList extends DashboardListItem
    {
        public CategoryForList(BlockPIDSTianjin.Category category) {
            super(category.id, category.name.getString(), ARGB_BLACK | category.color.getRGB());
        }

        public static ObjectImmutableList<DashboardListItem> getCategoriesForList() {
            final ObjectArrayList<CategoryForList> categories = new ObjectArrayList<>();
            BlockPIDSTianjin.CATEGORIES.values().forEach(category -> categories.add(new CategoryForList(category)));
            return new ObjectImmutableList<>(categories);
        }
    }
}
