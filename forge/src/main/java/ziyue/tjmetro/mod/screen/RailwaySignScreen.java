package ziyue.tjmetro.mod.screen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.mtr.MTRClient;
import org.mtr.client.CustomResourceLoader;
import org.mtr.client.IDrawing;
import org.mtr.client.MinecraftClientData;
import org.mtr.core.data.Platform;
import org.mtr.core.data.Station;
import org.mtr.data.IGui;
import org.mtr.libraries.it.unimi.dsi.fastutil.ints.IntAVLTreeSet;
import org.mtr.libraries.it.unimi.dsi.fastutil.longs.LongAVLTreeSet;
import org.mtr.libraries.it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.mtr.libraries.it.unimi.dsi.fastutil.objects.ObjectArraySet;
import org.mtr.libraries.it.unimi.dsi.fastutil.objects.ObjectImmutableList;
import org.mtr.resource.SignResource;
import org.mtr.screen.DashboardListItem;
import ziyue.tjmetro.mod.RegistryClient;
import ziyue.tjmetro.mod.TianjinMetro;
import ziyue.tjmetro.mod.block.BlockRouteMapBMT;
import ziyue.tjmetro.mod.block.BlockStationNameEntranceTianjin;
import ziyue.tjmetro.mod.block.BlockStationNamePlate;
import ziyue.tjmetro.mod.block.BlockStationNameSignTianjin;
import ziyue.tjmetro.mod.block.base.BlockRailwaySignBase;
import ziyue.tjmetro.mod.block.base.IRailwaySign;
import ziyue.tjmetro.mod.packet.PacketUpdateRailwaySignConfig;

import javax.annotation.Nullable;

import static org.mtr.data.IGui.*;

/**
 * @author ZiYueCommentary
 * @see PacketUpdateRailwaySignConfig
 * @since 1.0.0-beta-1
 */

public class RailwaySignScreen extends Screen implements IGui
{
    protected int editingIndex;
    protected int page;
    protected int totalPages;
    protected int columns;
    protected int rows;

    protected final BlockPos signPos;
    protected final Type type;
    protected final int length;
    protected final String[] signIds;
    protected final LongAVLTreeSet selectedIds;
    protected final ObjectImmutableList<DashboardListItem> exitsForList;
    protected final ObjectImmutableList<DashboardListItem> platformsForList;
    protected final ObjectArraySet<DashboardListItem> routesForList;
    protected final ObjectArraySet<DashboardListItem> stationsForList;
    protected final ObjectArrayList<String> allSignIds = new ObjectArrayList<>();

    protected final Button[] buttonsEdit;
    protected final Button[] buttonsSelection;
    protected final Button buttonClear;
    protected final Button buttonPrevPage;
    protected final Button buttonNextPage;

    protected static final int SIGN_SIZE = 32;
    protected static final int SIGN_BUTTON_SIZE = 16;
    protected static final int BUTTON_Y_START = SIGN_SIZE + SQUARE_SIZE + SQUARE_SIZE / 2;

    public RailwaySignScreen(BlockPos signPos) {
        super(Component.empty());
        editingIndex = -1;
        this.signPos = signPos;
        final ClientLevel world = Minecraft.getInstance().level;

        CustomResourceLoader.getSortedSigns().forEach(sign -> allSignIds.add(sign.signId));

        final Station station = MTRClient.findStation(signPos);
        final ObjectArrayList<Platform> platformsForBlock = PIDSTianjinConfigScreen.getPlatformsForBlock(signPos);
        if (station == null) {
            exitsForList = ObjectImmutableList.of();
            platformsForList = PIDSTianjinConfigScreen.getPlatformsForList(platformsForBlock);
            stationsForList = new ObjectArraySet<>();
            final LongAVLTreeSet platformIds = new LongAVLTreeSet();
            platformsForBlock.forEach(platform -> platformIds.add(platform.getId()));
            routesForList = getRoutesForList(platformIds);
        } else {
            final ObjectArrayList<DashboardListItem> exitsForDashboardList = new ObjectArrayList<>();
            station.getExits().forEach(exit -> exitsForDashboardList.add(new DashboardListItem(org.mtr.screen.RailwaySignScreen.serializeExit(exit.getName()), exit.getName(), 0)));
            exitsForList = new ObjectImmutableList<>(exitsForDashboardList);
            platformsForList = PIDSTianjinConfigScreen.getPlatformsForList(platformsForBlock);

            final ObjectArraySet<Station> connectingStationsIncludingThisOne = new ObjectArraySet<>(station.connectedStations);
            connectingStationsIncludingThisOne.add(station);
            stationsForList = new ObjectArraySet<>();
            connectingStationsIncludingThisOne.forEach(connectingStation -> stationsForList.add(new DashboardListItem(connectingStation)));

            final LongAVLTreeSet platformIds = new LongAVLTreeSet();
            connectingStationsIncludingThisOne.forEach(connectingStation -> connectingStation.savedRails.forEach(platform -> platformIds.add(platform.getId())));
            routesForList = getRoutesForList(platformIds);
        }

        if (world != null) {
            final BlockEntity entity = world.getBlockEntity(signPos);
            if (entity instanceof BlockRailwaySignBase.BlockEntityBase entity1) {
                signIds = entity1.getSignIds();
                selectedIds = entity1.getSelectedIds();
                type = Type.RAILWAY_SIGN;
            } else {
                signIds = new String[0];
                selectedIds = new LongAVLTreeSet();
                if (entity != null) {
                    if (entity instanceof BlockStationNameEntranceTianjin.BlockEntity sign) {
                        selectedIds.add(sign.getSelectedId());
                        type = Type.SINGLE_EXIT;
                    } else if (entity instanceof BlockStationNamePlate.BlockEntity plate) {
                        selectedIds.add(plate.getPlatformId());
                        type = Type.SINGLE_PLATFORM;
                    } else if (entity instanceof BlockRouteMapBMT.BlockEntity routeMap) {
                        selectedIds.add(routeMap.getPlatformId());
                        type = Type.SINGLE_PLATFORM;
                    } else if (entity instanceof BlockStationNameSignTianjin.BlockEntity sign) {
                        selectedIds.add(sign.getPlatformId());
                        type = Type.SINGLE_PLATFORM;
                    } else {
                        type = null;
                    }
                } else {
                    type = null;
                }
            }
            final Block block = world.getBlockState(signPos).getBlock();
            if (block instanceof BlockRailwaySignBase block1) {
                length = block1.length;
            } else {
                length = 0;
            }
        } else {
            throw new NullPointerException("Level is null");
        }

        buttonsEdit = new Button[length];
        for (int i = 0; i < buttonsEdit.length; i++) {
            final int index = i;
            buttonsEdit[i] = Button.builder(Component.translatable("selectWorld.edit"), button -> edit(index)).bounds(0, 0, 0, SQUARE_SIZE).build();
        }

        buttonsSelection = new Button[allSignIds.size()];
        for (int i = 0; i < allSignIds.size(); i++) {
            final int index = i;
            buttonsSelection[i] = Button.builder(Component.empty(), button -> setNewSignId(allSignIds.get(index))).bounds(0, 0, 0, SIGN_BUTTON_SIZE).build();
        }

        buttonClear = Button.builder(Component.translatable("gui.mtr.reset"), button -> setNewSignId(null)).bounds(0, 0, 0, SQUARE_SIZE).build();
        buttonPrevPage = Button.builder(Component.literal("<"), button -> setPage(page - 1)).bounds(0, 0, 0, SQUARE_SIZE).build();
        buttonNextPage = Button.builder(Component.literal(">"), button -> setPage(page + 1)).bounds(0, 0, 0, SQUARE_SIZE).build();
    }

    private static ObjectArraySet<DashboardListItem> getRoutesForList(LongAVLTreeSet platformIds) {
        final ObjectArraySet<DashboardListItem> routesForList = new ObjectArraySet<>();
        final IntAVLTreeSet addedColors = new IntAVLTreeSet();
        MinecraftClientData.getInstance().simplifiedRoutes.forEach(simplifiedRoute -> {
            final int color = simplifiedRoute.getColor();
            if (!addedColors.contains(color) && simplifiedRoute.getPlatforms().stream().anyMatch(simplifiedRoutePlatform -> platformIds.contains(simplifiedRoutePlatform.getPlatformId()))) {
                routesForList.add(new DashboardListItem(color, simplifiedRoute.getName().split("\\|\\|")[0], color));
                addedColors.add(color);
            }
        });
        return routesForList;
    }

    @Override
    protected void init() {
        super.init();

        for (int i = 0; i < buttonsEdit.length; i++) {
            IDrawing.setPositionAndWidth(buttonsEdit[i], (width - SIGN_SIZE * length) / 2 + i * SIGN_SIZE, SIGN_SIZE, SIGN_SIZE);
            addRenderableWidget(buttonsEdit[i]);
        }

        columns = Math.max((width - SIGN_BUTTON_SIZE * 3) / (SIGN_BUTTON_SIZE * 8) * 2, 1);
        rows = Math.max((height - SIGN_SIZE - SQUARE_SIZE * 4) / SIGN_BUTTON_SIZE, 1);

        final int xOffsetSmall = (width - SIGN_BUTTON_SIZE * (columns * 4 + 3)) / 2 + SIGN_BUTTON_SIZE;
        final int xOffsetBig = xOffsetSmall + SIGN_BUTTON_SIZE * (columns + 1);

        totalPages = loopSigns((index, x, y, isBig) -> {
            IDrawing.setPositionAndWidth(buttonsSelection[index], (isBig ? xOffsetBig : xOffsetSmall) + x, BUTTON_Y_START + y, isBig ? SIGN_BUTTON_SIZE * 3 : SIGN_BUTTON_SIZE);
            buttonsSelection[index].visible = false;
            addRenderableWidget(buttonsSelection[index]);
        }, true);

        final int buttonClearX = (width - PANEL_WIDTH - SQUARE_SIZE * 4) / 2;
        final int buttonY = height - SQUARE_SIZE * 2;

        IDrawing.setPositionAndWidth(buttonClear, buttonClearX, buttonY, PANEL_WIDTH);
        buttonClear.visible = false;
        addRenderableWidget(buttonClear);

        IDrawing.setPositionAndWidth(buttonPrevPage, buttonClearX + PANEL_WIDTH, buttonY, SQUARE_SIZE);
        buttonPrevPage.visible = false;
        addRenderableWidget(buttonPrevPage);
        IDrawing.setPositionAndWidth(buttonNextPage, buttonClearX + PANEL_WIDTH + SQUARE_SIZE * 3, buttonY, SQUARE_SIZE);
        buttonNextPage.visible = false;
        addRenderableWidget(buttonNextPage);

        if (type != null && type != Type.RAILWAY_SIGN) {
            TianjinMetro.LOGGER.info("[TJSIGN] pos={} type={} exits={} platforms={} routes={} stations={} selected={}", signPos, type, exitsForList.size(), platformsForList.size(), routesForList.size(), stationsForList.size(), selectedIds.size());
            final DashboardListSelectorScreen screen = switch (type) {
                case SINGLE_EXIT -> new DashboardListSelectorScreen(this::onClose, exitsForList, selectedIds, true, false, null).withEmptyMessage("gui.tjmetro.no_available_exits");
                case SINGLE_PLATFORM -> new DashboardListSelectorScreen(this::onClose, platformsForList, selectedIds, true, false, null).withEmptyMessage("gui.tjmetro.no_available_platforms");
                case MULTIPLE_ROUTE -> new DashboardListSelectorScreen(this::onClose, new ObjectImmutableList<>(routesForList), selectedIds, false, false, null).withEmptyMessage("gui.tjmetro.no_available_routes");
                default -> throw new IllegalStateException("Unknown enum type: " + type);
            };
            Minecraft.getInstance().setScreen(screen);
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        super.render(guiGraphics, mouseX, mouseY, delta);

        SignRenderer.drawSign(guiGraphics, signPos, signIds, selectedIds, (width - SIGN_SIZE * length) / 2F, 0, SIGN_SIZE);

        if (editingIndex >= 0) {
            final int xOffsetSmall = (width - SIGN_BUTTON_SIZE * (columns * 4 + 3)) / 2 + SIGN_BUTTON_SIZE;
            final int xOffsetBig = xOffsetSmall + SIGN_BUTTON_SIZE * (columns + 1);

            loopSigns((index, x, y, isBig) -> {
                final String signId = allSignIds.get(index);
                if (SignRenderer.getSign(signId) != null) {
                    SignRenderer.drawSign(guiGraphics, signPos, signId, isBig ? 3 : 1, selectedIds, (isBig ? xOffsetBig : xOffsetSmall) + x, BUTTON_Y_START + y, SIGN_BUTTON_SIZE);
                }
            }, false);

            guiGraphics.drawCenteredString(Minecraft.getInstance().font, String.format("%s/%s", page + 1, totalPages), (width - PANEL_WIDTH - SQUARE_SIZE * 4) / 2 + PANEL_WIDTH + SQUARE_SIZE * 2, height - SQUARE_SIZE * 2 + TEXT_PADDING, ARGB_WHITE);
        }
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        setPage(page + (int) Math.signum(-scrollY));
        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

    @Override
    public void onClose() {
        RegistryClient.sendPacketToServer(new PacketUpdateRailwaySignConfig(signPos, selectedIds, signIds));
        super.onClose();
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void resize(Minecraft client, int width, int height) {
        super.resize(client, width, height);
        for (Button button : buttonsEdit) {
            button.active = true;
        }
        for (Button button : buttonsSelection) {
            button.visible = false;
        }
        editingIndex = -1;
    }

    protected int loopSigns(LoopSignsCallback loopSignsCallback, boolean ignorePage) {
        int pageCount = rows * columns;
        int indexSmall = 0;
        int indexBig = 0;
        int columnSmall = 0;
        int columnBig = 0;
        int rowSmall = 0;
        int rowBig = 0;
        int totalPagesSmallCount = 1;
        int totalPagesBigCount = 1;
        for (int i = 0; i < allSignIds.size(); i++) {
            final SignResource sign = SignRenderer.getSign(allSignIds.get(i));
            final boolean isBig = sign != null && sign.hasCustomText;

            final boolean onPage = (isBig ? indexBig : indexSmall) / pageCount == page;
            buttonsSelection[i].visible = onPage;
            if (ignorePage || onPage) {
                loopSignsCallback.loopSignsCallback(i, (isBig ? columnBig * 3 : columnSmall) * SIGN_BUTTON_SIZE, (isBig ? rowBig : rowSmall) * SIGN_BUTTON_SIZE, isBig);
            }

            if (isBig) {
                columnBig++;
                if (totalPagesBigCount < 0) {
                    totalPagesBigCount = -totalPagesBigCount + 1;
                }
                if (columnBig >= columns) {
                    columnBig = 0;
                    rowBig++;
                    if (rowBig >= rows) {
                        rowBig = 0;
                        totalPagesBigCount = -totalPagesBigCount;
                    }
                }
                indexBig++;
            } else {
                columnSmall++;
                if (totalPagesSmallCount < 0) {
                    totalPagesSmallCount = -totalPagesSmallCount + 1;
                }
                if (columnSmall >= columns) {
                    columnSmall = 0;
                    rowSmall++;
                    if (rowSmall >= rows) {
                        rowSmall = 0;
                        totalPagesSmallCount = -totalPagesSmallCount;
                    }
                }
                indexSmall++;
            }
        }
        return Math.max(Math.abs(totalPagesBigCount), Math.abs(totalPagesSmallCount));
    }

    protected void edit(int editingIndex) {
        this.editingIndex = editingIndex;
        for (Button button : buttonsEdit) {
            button.active = true;
        }
        buttonClear.visible = true;
        setPage(page);
        buttonsEdit[editingIndex].active = false;
    }

    protected void setNewSignId(@Nullable String newSignId) {
        if (editingIndex >= 0 && editingIndex < signIds.length) {
            signIds[editingIndex] = newSignId;
            final boolean isExitLetter = IRailwaySign.signIsExit(newSignId);
            final boolean isPlatform = IRailwaySign.signIsPlatform(newSignId);
            final boolean isLine = IRailwaySign.signIsLine(newSignId);
            final boolean isStation = IRailwaySign.signIsStation(newSignId);
            if ((isExitLetter || isPlatform || isLine || isStation)) {
                Minecraft.getInstance().setScreen(new DashboardListSelectorScreen(this::onClose, new ObjectImmutableList<>(isExitLetter ? exitsForList : (isPlatform ? platformsForList : (isLine ? routesForList : stationsForList))), selectedIds, false, false, null)
                        .withEmptyMessage(isExitLetter ? "gui.tjmetro.no_available_exits" : (isPlatform ? "gui.tjmetro.no_available_platforms" : (isLine ? "gui.tjmetro.no_available_routes" : "gui.tjmetro.no_available_stations"))));
            }
        }
    }

    protected void setPage(int newPage) {
        page = Mth.clamp(newPage, 0, totalPages - 1);
        buttonPrevPage.visible = editingIndex >= 0 && page > 0;
        buttonNextPage.visible = editingIndex >= 0 && page < totalPages - 1;
    }

    @FunctionalInterface
    protected interface LoopSignsCallback
    {
        void loopSignsCallback(int index, int x, int y, boolean isBig);
    }

    public enum Type
    {
        RAILWAY_SIGN,
        SINGLE_EXIT,
        SINGLE_PLATFORM,
        MULTIPLE_ROUTE
    }
}
