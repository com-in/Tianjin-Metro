package ziyue.tjmetro.mod.screen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.mtr.generated.lang.TranslationProvider;
import org.mtr.libraries.it.unimi.dsi.fastutil.longs.LongCollection;
import org.mtr.libraries.it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.mtr.libraries.it.unimi.dsi.fastutil.objects.ObjectImmutableList;
import org.mtr.screen.DashboardListItem;

import static org.mtr.data.IGui.*;

/**
 * Native replacement for MTR's removed {@code DashboardListSelectorScreen}.
 * Shows an "available" list on the left and the "selected" list on the right; clicking an entry
 * moves it between the two lists. Layout and drawing follow MTR 4.0's original screen so that the
 * two versions look the same.
 *
 * @author ZiYueCommentary
 * @since 1.0.0-beta-1
 */
public class DashboardListSelectorScreen extends Screen
{
    protected final Runnable onCloseRunnable;
    protected final ObjectImmutableList<DashboardListItem> availableData;
    protected final LongCollection selectedIds;
    protected final boolean canSelectMultiple;
    protected final boolean canSelectDuplicate;
    protected final Screen previousScreen;

    protected final ItemList availableList = new ItemList();
    protected final ItemList selectedList = new ItemList();
    protected Button buttonDone;

    /** Y position of both lists, matching MTR 4.0. */
    protected static final int LIST_Y = 34;
    /** Top padding inside a list before the first row, matching MTR 4.0's {@code 6 + 24}. */
    private static final int ROW_TOP_PADDING = 6 + 24;
    /** Horizontal centre offset of the two list headers, matching MTR 4.0. */
    private static final int HEADER_OFFSET = 112;
    private String emptyMessageKey = "gui.tjmetro.no_available_data";

    public DashboardListSelectorScreen(Runnable onCloseRunnable, ObjectImmutableList<DashboardListItem> availableData, LongCollection selectedIds, boolean canSelectMultiple, boolean canSelectDuplicate, Screen previousScreen) {
        super(Component.empty());
        this.onCloseRunnable = onCloseRunnable;
        this.availableData = availableData;
        this.selectedIds = selectedIds;
        this.canSelectMultiple = canSelectMultiple;
        this.canSelectDuplicate = canSelectDuplicate;
        this.previousScreen = previousScreen;
    }

    public DashboardListSelectorScreen(ObjectImmutableList<DashboardListItem> availableData, LongCollection selectedIds, boolean canSelectMultiple, boolean canSelectDuplicate, Screen previousScreen) {
        this(null, availableData, selectedIds, canSelectMultiple, canSelectDuplicate, previousScreen);
    }

    /**
     * 列表为空时显示哪条提示。不同来源（出口/站台/线路/车站）的空态原因不同，
     * 默认沿用通用文案，调用方可改成更准确的说明。
     */
    public DashboardListSelectorScreen withEmptyMessage(String translationKey) {
        this.emptyMessageKey = translationKey;
        return this;
    }

    @Override
    protected void init() {
        super.init();

        // MTR 4.0 geometry: two PANEL_WIDTH wide lists side by side, centred on the screen.
        final int listHeight = Math.max(SQUARE_SIZE + ROW_TOP_PADDING, height - SQUARE_SIZE * 5 + TEXT_PADDING);

        availableList.x = width / 2 - PANEL_WIDTH - SQUARE_SIZE;
        availableList.y = LIST_Y;
        availableList.width = PANEL_WIDTH;
        availableList.height = listHeight;

        selectedList.x = width / 2 + SQUARE_SIZE;
        selectedList.y = LIST_Y;
        selectedList.width = PANEL_WIDTH;
        selectedList.height = listHeight;

        buttonDone = Button.builder(Component.translatable("gui.done"), button -> onDone()).bounds((width - PANEL_WIDTH) / 2, height - SQUARE_SIZE * 2, PANEL_WIDTH, SQUARE_SIZE).build();
        addRenderableWidget(buttonDone);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        availableList.mouseX = mouseX;
        availableList.mouseY = mouseY;
        selectedList.mouseX = mouseX;
        selectedList.mouseY = mouseY;

        // Screen.render() already draws the background, so everything below must be drawn afterwards or it gets covered.
        super.render(guiGraphics, mouseX, mouseY, delta);

        final ObjectArrayList<DashboardListItem> selected = getSelectedItems();
        availableList.setSize(availableData.size());
        selectedList.setSize(selected.size());
        renderListFrame(guiGraphics, availableList);
        renderListFrame(guiGraphics, selectedList);
        renderList(guiGraphics, availableList, availableData, true);
        renderList(guiGraphics, selectedList, selected, false);

        final Font font = Minecraft.getInstance().font;
        if (availableData.isEmpty() && selected.isEmpty()) {
            guiGraphics.drawCenteredString(font, Component.translatable(emptyMessageKey), width / 2, height - SQUARE_SIZE * 3 - TEXT_HEIGHT, ARGB_WHITE);
        }

        renderAdditional(guiGraphics, mouseX, mouseY, delta);
    }

    private void renderListFrame(GuiGraphics guiGraphics, ItemList list) {
        guiGraphics.fill(list.x - 2, list.y - 2, list.x + list.width + 2, list.y + list.height + 8, ARGB_GRAY);
        guiGraphics.fill(list.x, list.y, list.x + list.width, list.y + list.height + 6, ARGB_BLACK);
    }

    @Override
    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        guiGraphics.fill(0, 0, width, height, ARGB_BACKGROUND);
    }

    protected void renderAdditional(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        final Font font = Minecraft.getInstance().font;
        guiGraphics.drawCenteredString(font, TranslationProvider.GUI_MTR_AVAILABLE.getText(), width / 2 - HEADER_OFFSET, SQUARE_SIZE, ARGB_WHITE);
        guiGraphics.drawCenteredString(font, TranslationProvider.GUI_MTR_SELECTED.getText(), width / 2 + HEADER_OFFSET, SQUARE_SIZE, ARGB_WHITE);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        availableList.mouseX = (int) mouseX;
        availableList.mouseY = (int) mouseY;
        selectedList.mouseX = (int) mouseX;
        selectedList.mouseY = (int) mouseY;
        availableList.setSize(availableData.size());

        final int availableIndex = availableList.getHoverItemIndex();
        if (availableIndex >= 0 && availableIndex < availableData.size()) {
            addSelection(availableData.get(availableIndex));
            return true;
        }

        final ObjectArrayList<DashboardListItem> selected = getSelectedItems();
        selectedList.setSize(selected.size());
        final int selectedIndex = selectedList.getHoverItemIndex();
        if (selectedIndex >= 0 && selectedIndex < selected.size()) {
            selectedIds.remove(selected.get(selectedIndex).id);
            return true;
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        final int pageChange = (int) Math.signum(-scrollY);
        if (availableList.isHovered(mouseX, mouseY)) {
            availableList.setPage(availableList.getPage() + pageChange);
            return true;
        }
        if (selectedList.isHovered(mouseX, mouseY)) {
            selectedList.setPage(selectedList.getPage() + pageChange);
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

    @Override
    public void onClose() {
        if (onCloseRunnable != null) {
            onCloseRunnable.run();
        }
        Minecraft.getInstance().setScreen(previousScreen);
    }

    protected void onDone() {
        onClose();
    }

    protected ObjectArrayList<DashboardListItem> getSelectedItems() {
        final ObjectArrayList<DashboardListItem> selected = new ObjectArrayList<>();
        availableData.forEach(item -> {
            if (selectedIds.contains(item.id)) {
                selected.add(item);
            }
        });
        return selected;
    }

    private void addSelection(DashboardListItem item) {
        if (!canSelectMultiple && !canSelectDuplicate) {
            selectedIds.clear();
        }
        if (canSelectDuplicate || !selectedIds.contains(item.id)) {
            selectedIds.add(item.id);
        }
    }

    private void renderList(GuiGraphics guiGraphics, ItemList list, Iterable<DashboardListItem> items, boolean isAvailable) {
        final Font font = Minecraft.getInstance().font;
        final int itemsToShow = list.itemsToShow();
        guiGraphics.drawCenteredString(font, String.format("%s/%s", list.getPage() + 1, list.getTotalPages()), list.x + 40, list.y + TEXT_HEIGHT, ARGB_WHITE);

        int index = 0;
        for (final DashboardListItem item : items) {
            final int row = index - list.getPage() * itemsToShow;
            final int itemIndex = index;
            index++;
            if (row < 0 || row >= itemsToShow || itemIndex >= list.getSize()) {
                continue;
            }

            final int itemY = list.y + row * SQUARE_SIZE + ROW_TOP_PADDING;
            if (itemIndex == list.getHoverItemIndex()) {
                guiGraphics.fill(list.x, itemY - 2, list.x + list.width, itemY + SQUARE_SIZE - 2, ARGB_GRAY);
            }
            guiGraphics.fill(list.x + 6, itemY, list.x + 14, itemY + 8, ARGB_BLACK | item.getColor(isAvailable));

            final String name = formatStationName(item.getName(isAvailable));
            final int maxWidth = list.width - 20;
            final int textWidth = font.width(name);
            guiGraphics.pose().pushPose();
            guiGraphics.pose().translate(list.x + 20, 0, 0);
            if (textWidth > maxWidth) {
                guiGraphics.pose().scale(maxWidth / (float) textWidth, 1, 1);
            }
            guiGraphics.drawString(font, name, 0, itemY, ARGB_WHITE);
            guiGraphics.pose().popPose();
        }
    }

    public static class ItemList
    {
        public int x;
        public int y;
        public int width;
        public int height;
        public int mouseX = -1;
        public int mouseY = -1;
        private int size;
        private int page;
        private int totalPages = 1;

        /** Same as MTR 4.0: rows are {@code SQUARE_SIZE} tall and the first row starts 24 pixels down. */
        public int itemsToShow() {
            return Math.max(1, (height - 24) / SQUARE_SIZE);
        }

        public void setSize(int size) {
            this.size = size;
            this.totalPages = Math.max(1, (int) Math.ceil(size / (double) itemsToShow()));
            this.page = Math.max(0, Math.min(page, totalPages - 1));
        }

        public int getSize() {
            return size;
        }

        public int getPage() {
            return page;
        }

        public int getTotalPages() {
            return totalPages;
        }

        public void setPage(int page) {
            this.page = Math.max(0, Math.min(page, totalPages - 1));
        }

        public boolean isHovered(double mouseX, double mouseY) {
            return mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height;
        }

        public int getHoverItemIndex() {
            if (!isHovered(mouseX, mouseY) || mouseY < y + 24) {
                return -1;
            }
            final int index = (mouseY - y - 24) / SQUARE_SIZE + page * itemsToShow();
            return index >= 0 && index < size ? index : -1;
        }
    }
}
