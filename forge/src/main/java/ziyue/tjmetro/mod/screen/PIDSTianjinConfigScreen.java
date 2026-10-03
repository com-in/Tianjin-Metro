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
import org.mtr.libraries.it.unimi.dsi.fastutil.objects.ObjectArraySet;
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
        // Screen.render() already draws the background, so everything below must be drawn afterwards or it gets covered.
        super.render(guiGraphics, mouseX, mouseY, delta);
        guiGraphics.drawString(Minecraft.getInstance().font, TranslationProvider.GUI_MTR_DISPLAY_PAGE.getText(), SQUARE_SIZE, SQUARE_SIZE * 4 + TEXT_PADDING, ARGB_WHITE);
        guiGraphics.drawString(Minecraft.getInstance().font, TranslationProvider.GUI_MTR_FILTERED_PLATFORMS.getText(selectAllCheckbox.selected() ? 0 : filterPlatformIds.size()), SQUARE_SIZE, SQUARE_SIZE * 2 + TEXT_PADDING, ARGB_WHITE);
        guiGraphics.drawString(Minecraft.getInstance().font, Component.translatable("gui.tjmetro.filtered_ads", filteredAdsCount), SQUARE_SIZE, SQUARE_SIZE * 6 + TEXT_PADDING * 2, ARGB_WHITE);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    public static Button getPlatformFilterButton(BlockPos blockPos, Checkbox selectAllCheckbox, LongAVLTreeSet filterPlatformIds, Screen thisScreen) {
        return Button.builder(Component.empty(), button -> {
            final ObjectImmutableList<DashboardListItem> platformsForList = getPlatformsForList(getPlatformsForBlock(blockPos));

            if (selectAllCheckbox.selected()) {
                filterPlatformIds.clear();
            }

            Minecraft.getInstance().setScreen(new DashboardListSelectorScreen(() -> IGui.setChecked(selectAllCheckbox, filterPlatformIds.isEmpty()), platformsForList, filterPlatformIds, false, false, thisScreen).withEmptyMessage("gui.tjmetro.no_available_platforms"));
        }).bounds(0, 0, 0, SQUARE_SIZE).build();
    }

    /**
     * Resolves the platforms to list for the given position. Blocks are often placed just outside the
     * station area, so this falls back progressively: the platforms MTR associated with the station,
     * then a manual sweep of the station area, then an expanding radius around the block, and finally
     * every platform the client knows about.
     */
    public static ObjectArrayList<Platform> getPlatformsForBlock(BlockPos blockPos) {
        final Station station = MTRClient.findStation(blockPos);
        final ObjectArraySet<Platform> allPlatforms = MinecraftClientData.getInstance().platforms;

        ObjectArrayList<Platform> platforms = null;
        if (station != null && !station.savedRails.isEmpty()) {
            platforms = new ObjectArrayList<>(station.savedRails);
        } else if (station != null) {
            final ObjectArrayList<Platform> inArea = new ObjectArrayList<>();
            allPlatforms.forEach(platform -> {
                if (station.inArea(platform.getMidPosition())) {
                    inArea.add(platform);
                }
            });
            if (!inArea.isEmpty()) {
                platforms = inArea;
            }
        }

        if (platforms == null) {
            for (final int radius : new int[]{5, 16, 32, 64}) {
                final ObjectArrayList<Platform> nearby = new ObjectArrayList<>();
                MTRClient.findClosePlatform(blockPos.below(4), radius, nearby::add);
                if (!nearby.isEmpty()) {
                    platforms = nearby;
                    break;
                }
            }
        }

        if (platforms == null) {
            platforms = new ObjectArrayList<>(allPlatforms);
        }

        TianjinMetro.LOGGER.info("[TJDATA] pos={} station={} savedRails={} clientStations={} clientPlatforms={} clientRails={} resolved={}",
                blockPos.toShortString(),
                station == null ? "null" : station.getName(),
                station == null ? -1 : station.savedRails.size(),
                MinecraftClientData.getInstance().stations.size(),
                allPlatforms.size(),
                MinecraftClientData.getInstance().rails.size(),
                platforms.size()
        );

        return platforms;
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
