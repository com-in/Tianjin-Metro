package ziyue.tjmetro.mod.screen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import org.mtr.MTRClient;
import org.mtr.client.IDrawing;
import org.mtr.client.MinecraftClientData;
import org.mtr.core.data.Platform;
import org.mtr.core.data.Station;
import org.mtr.core.tool.Utilities;
import org.mtr.data.IGui;
import org.mtr.generated.lang.TranslationProvider;
import org.mtr.libraries.it.unimi.dsi.fastutil.longs.LongAVLTreeSet;
import org.mtr.libraries.it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.mtr.libraries.it.unimi.dsi.fastutil.objects.ObjectImmutableList;
import org.mtr.screen.DashboardListItem;
import ziyue.tjmetro.mod.RegistryClient;
import ziyue.tjmetro.mod.TianjinMetro;
import ziyue.tjmetro.mod.block.BlockPIDSTianjin;
import ziyue.tjmetro.mod.packet.PacketUpdatePIDSTianjinConfig;

import java.util.Collections;
import java.util.stream.Collectors;

import static org.mtr.data.IGui.*;

public class PIDSTianjinConfigScreen extends Screen implements IGui
{
    private final BlockPos blockPos;
    private final EditBox displayPageInput;
    private final Checkbox selectAllCheckbox;
    private final Button filterButton;
    private final LongAVLTreeSet filterPlatformIds;
    private final int displayPage;
    private final Button categoryButton;
    private final int filteredAdsCount;

    public PIDSTianjinConfigScreen(BlockPos blockPos) {
        super(Component.empty());
        this.blockPos = blockPos;

        selectAllCheckbox = Checkbox.builder(Component.empty(), Minecraft.getInstance().font).pos(0, 0).selected(true).build();
        selectAllCheckbox.setMessage(TranslationProvider.GUI_MTR_AUTOMATICALLY_DETECT_NEARBY_PLATFORM.getText());

        final ClientLevel clientWorld = Minecraft.getInstance().level;
        final BlockPIDSTianjin.BlockEntity blockEntity = (BlockPIDSTianjin.BlockEntity) clientWorld.getBlockEntity(blockPos);
        filterPlatformIds = blockEntity.getPlatformIds();
        displayPage = blockEntity.getDisplayPage();
        filteredAdsCount = blockEntity.getCategories().size();

        filterButton = getPlatformFilterButton(blockPos, selectAllCheckbox, filterPlatformIds, this);
        categoryButton = Button.builder(Component.empty(), button -> Minecraft.getInstance().setScreen(new CategorySelectorScreen(blockEntity, this))).bounds(0, 0, 0, SQUARE_SIZE).build();
        displayPageInput = new EditBox(Minecraft.getInstance().font, 0, 0, 0, SQUARE_SIZE, Component.empty());
        displayPageInput.setMaxLength(3);
        displayPageInput.setFilter(text -> text.matches("\\d*"));
        displayPageInput.setValue("1");
    }

    @Override
    protected void init() {
        super.init();
        IDrawing.setPositionAndWidth(selectAllCheckbox, SQUARE_SIZE, SQUARE_SIZE, PANEL_WIDTH);
        IGui.setChecked(selectAllCheckbox, filterPlatformIds.isEmpty());
        addRenderableWidget(selectAllCheckbox);

        IDrawing.setPositionAndWidth(filterButton, SQUARE_SIZE, SQUARE_SIZE * 3, PANEL_WIDTH / 2);
        filterButton.setMessage(Component.translatable("selectWorld.edit"));
        addRenderableWidget(filterButton);

        IDrawing.setPositionAndWidth(displayPageInput, SQUARE_SIZE + TEXT_FIELD_PADDING / 2, SQUARE_SIZE * 5 + TEXT_FIELD_PADDING / 2, PANEL_WIDTH / 2 - TEXT_FIELD_PADDING);
        displayPageInput.setValue(String.valueOf(displayPage + 1));
        addRenderableWidget(displayPageInput);

        IDrawing.setPositionAndWidth(categoryButton, SQUARE_SIZE + TEXT_FIELD_PADDING / 2, SQUARE_SIZE * 7 + TEXT_FIELD_PADDING, PANEL_WIDTH / 2);
        categoryButton.setMessage(Component.translatable("selectWorld.edit"));
        addRenderableWidget(categoryButton);
    }

    @Override
    public void onClose() {
        if (selectAllCheckbox.selected()) {
            filterPlatformIds.clear();
        }
        int displayPage = 0;
        try {
            displayPage = Math.max(0, Integer.parseInt(displayPageInput.getValue()) - 1);
        } catch (Exception e) {
            TianjinMetro.LOGGER.error("", e);
        }
        RegistryClient.sendPacketToServer(new PacketUpdatePIDSTianjinConfig(blockPos, filterPlatformIds, displayPage));
        super.onClose();
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        renderBackground(guiGraphics, mouseX, mouseY, delta);
        guiGraphics.drawString(Minecraft.getInstance().font, TranslationProvider.GUI_MTR_DISPLAY_PAGE.getText(), SQUARE_SIZE, SQUARE_SIZE * 4 + TEXT_PADDING, ARGB_WHITE);
        guiGraphics.drawString(Minecraft.getInstance().font, TranslationProvider.GUI_MTR_FILTERED_PLATFORMS.getText(selectAllCheckbox.selected() ? 0 : filterPlatformIds.size()), SQUARE_SIZE, SQUARE_SIZE * 2 + TEXT_PADDING, ARGB_WHITE);
        guiGraphics.drawString(Minecraft.getInstance().font, Component.translatable("gui.tjmetro.filtered_ads", filteredAdsCount), SQUARE_SIZE, SQUARE_SIZE * 6 + TEXT_PADDING * 2, ARGB_WHITE);
        super.render(guiGraphics, mouseX, mouseY, delta);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    public static Button getPlatformFilterButton(BlockPos blockPos, Checkbox selectAllCheckbox, LongAVLTreeSet filterPlatformIds, Screen thisScreen) {
        return Button.builder(Component.empty(), button -> {
            final Station station = MTRClient.findStation(blockPos);

            final ObjectImmutableList<DashboardListItem> platformsForList;
            if (station != null) {
                platformsForList = getPlatformsForList(new ObjectArrayList<>(station.savedRails));
            } else {
                final ObjectArrayList<Platform> nearbyPlatforms = new ObjectArrayList<>();
                MTRClient.findClosePlatform(blockPos.below(4), 5, nearbyPlatforms::add);
                platformsForList = getPlatformsForList(nearbyPlatforms);
            }

            if (selectAllCheckbox.selected()) {
                filterPlatformIds.clear();
            }

            Minecraft.getInstance().setScreen(new DashboardListSelectorScreen(() -> IGui.setChecked(selectAllCheckbox, filterPlatformIds.isEmpty()), platformsForList, filterPlatformIds, false, false, thisScreen));
        }).bounds(0, 0, 0, SQUARE_SIZE).build();
    }

    public static ObjectImmutableList<DashboardListItem> getPlatformsForList(ObjectArrayList<Platform> platforms) {
        final ObjectArrayList<DashboardListItem> platformsForList = new ObjectArrayList<>();
        Collections.sort(platforms);
        platforms.forEach(platform -> platformsForList.add(new DashboardListItem(platform.getId(), platform.getName() + " " + IGui.mergeStations(MinecraftClientData.getInstance().simplifiedRoutes
                .stream()
                .filter(simplifiedRoute -> simplifiedRoute.getPlatformIndex(platform.getId()) >= 0)
                .map(simplifiedRoute -> Utilities.getElement(simplifiedRoute.getPlatforms(), -1).getStationName())
                .collect(Collectors.toList())
        ), 0)));
        return new ObjectImmutableList<>(platformsForList);
    }
}
