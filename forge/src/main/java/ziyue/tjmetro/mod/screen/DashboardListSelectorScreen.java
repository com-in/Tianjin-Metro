package ziyue.tjmetro.mod.screen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.mtr.libraries.it.unimi.dsi.fastutil.longs.LongCollection;
import org.mtr.libraries.it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.mtr.libraries.it.unimi.dsi.fastutil.objects.ObjectImmutableList;
import org.mtr.screen.DashboardListItem;

import static org.mtr.data.IGui.*;

/**
 * Local, native replacement for MTR's removed {@code DashboardListSelectorScreen}.
 * Shows an "available" list on the left and the "selected" list on the right; clicking an entry
 * moves it between the two lists.
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

    @Override
    protected void init() {
        super.init();
        final int listHeight = Math.max(LINE_HEIGHT, height - SQUARE_SIZE * 4);

        availableList.x = SQUARE_SIZE;
        availableList.y = SQUARE_SIZE * 2;
        availableList.width = PANEL_WIDTH;
        availableList.height = listHeight;

        selectedList.x = SQUARE_SIZE * 4 + PANEL_WIDTH;
        selectedList.y = SQUARE_SIZE * 2;
        selectedList.width = PANEL_WIDTH;
        selectedList.height = listHeight;

        buttonDone = Button.builder(Component.translatable("gui.done"), button -> onDone()).bounds(SQUARE_SIZE * 2 + PANEL_WIDTH, height - SQUARE_SIZE * 2, PANEL_WIDTH, SQUARE_SIZE).build();
        addRenderableWidget(buttonDone);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        availableList.mouseX = mouseX;
        availableList.mouseY = mouseY;
        selectedList.mouseX = mouseX;
        selectedList.mouseY = mouseY;

        renderBackground(guiGraphics, mouseX, mouseY, delta);

        final ObjectArrayList<DashboardListItem> selected = getSelectedItems();
        availableList.setSize(availableData.size());
        selectedList.setSize(selected.size());
        renderList(guiGraphics, availableList, availableData);
        renderList(guiGraphics, selectedList, selected);

        super.render(guiGraphics, mouseX, mouseY, delta);
        renderAdditional(guiGraphics, mouseX, mouseY, delta);
    }

    @Override
    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        guiGraphics.fill(0, 0, width, height, ARGB_BACKGROUND);
    }

    protected void renderAdditional(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        availableList.mouseX = (int) mouseX;
        availableList.mouseY = (int) mouseY;
        selectedList.mouseX = (int) mouseX;
        selectedList.mouseY = (int) mouseY;
        availableList.setSize(availableData.size());

        final int availableIndex = availableList.getHoverItemIndex();
        if (availableIndex >= 0) {
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

    private void renderList(GuiGraphics guiGraphics, ItemList list, Iterable<DashboardListItem> items) {
        int index = 0;
        final boolean hovered = list.getHoverItemIndex() >= 0;
        for (final DashboardListItem item : items) {
            final int itemY = list.y + index * LINE_HEIGHT;
            if (itemY + LINE_HEIGHT > list.y + list.height) {
                break;
            }
            final int backgroundColor = hovered && index == list.getHoverItemIndex() ? ARGB_GRAY : ARGB_BLACK;
            guiGraphics.fill(list.x, itemY, list.x + list.width, itemY + LINE_HEIGHT - 1, backgroundColor);
            guiGraphics.drawString(Minecraft.getInstance().font, item.getName(false), list.x + TEXT_PADDING, itemY + TEXT_PADDING, ARGB_WHITE);
            index++;
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

        public void setSize(int size) {
            this.size = size;
        }

        public int getHoverItemIndex() {
            if (mouseX < x || mouseX >= x + width || mouseY < y || mouseY >= y + height) {
                return -1;
            }
            final int index = (mouseY - y) / LINE_HEIGHT;
            return index >= 0 && index < size ? index : -1;
        }
    }
}
