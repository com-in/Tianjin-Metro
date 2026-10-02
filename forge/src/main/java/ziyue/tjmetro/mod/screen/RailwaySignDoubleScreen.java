package ziyue.tjmetro.mod.screen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.mtr.MTRClient;
import org.mtr.client.CustomResourceLoader;
import org.mtr.client.IDrawing;
import org.mtr.client.MinecraftClientData;
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
import ziyue.tjmetro.mod.block.BlockRailwaySignWallDouble;
import ziyue.tjmetro.mod.block.base.BlockRailwaySignBase;
import ziyue.tjmetro.mod.block.base.IRailwaySign;
import ziyue.tjmetro.mod.packet.PacketUpdateRailwaySignDoubleConfig;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

import static org.mtr.data.IGui.*;

/**
 * @author ZiYueCommentary
 * @see PacketUpdateRailwaySignDoubleConfig
 * @since 1.0.0-beta-1
 */

public class RailwaySignDoubleScreen extends Screen implements IGui
{
    protected int line;
    protected int editingIndex;
    protected int page;
    protected int totalPages;
    protected int columns;
    protected int rows;

    protected final BlockPos signPos;
    protected final boolean isRailwaySign;
    protected final int length;
    protected final String[][] signIds;
    protected final List<LongAVLTreeSet> selectedIds;
    protected final ObjectImmutableList<DashboardListItem> exitsForList;
    protected final ObjectImmutableList<DashboardListItem> platformsForList;
    protected final ObjectArraySet<DashboardListItem> routesForList;
    protected final ObjectArraySet<DashboardListItem> stationsForList;
    protected final ObjectArrayList<String> allSignIds = new ObjectArrayList<>();

    protected final Button[][] buttonsEdit;
    protected final Button[] buttonsSelection;
    protected final Button buttonClear;
    protected final Button buttonPrevPage;
    protected final Button buttonNextPage;

    protected static final int SIGN_SIZE = 32;
    protected static final int SIGN_BUTTON_SIZE = 16;
    protected static final int BUTTON_Y_START = (SQUARE_SIZE + SIGN_SIZE) * 2 + SIGN_BUTTON_SIZE / 2;

    public RailwaySignDoubleScreen(BlockPos signPos) {
        super(Component.empty());
        editingIndex = -1;
        this.signPos = signPos;
        final ClientLevel world = Minecraft.getInstance().level;

        CustomResourceLoader.getSortedSigns().forEach(sign -> allSignIds.add(sign.signId));

        final Station station = MTRClient.findStation(signPos);
        if (station == null) {
            exitsForList = ObjectImmutableList.of();
            platformsForList = ObjectImmutableList.of();
            stationsForList = new ObjectArraySet<>();
            routesForList = new ObjectArraySet<>();
        } else {
            final ObjectArrayList<DashboardListItem> exitsForDashboardList = new ObjectArrayList<>();
            station.getExits().forEach(exit -> exitsForDashboardList.add(new DashboardListItem(org.mtr.screen.RailwaySignScreen.serializeExit(exit.getName()), exit.getName(), 0)));
            exitsForList = new ObjectImmutableList<>(exitsForDashboardList);
            platformsForList = PIDSTianjinConfigScreen.getPlatformsForList(new ObjectArrayList<>(station.savedRails));

            final ObjectArraySet<Station> connectingStationsIncludingThisOne = new ObjectArraySet<>(station.connectedStations);
            connectingStationsIncludingThisOne.add(station);
            stationsForList = new ObjectArraySet<>();
            connectingStationsIncludingThisOne.forEach(connectingStation -> stationsForList.add(new DashboardListItem(connectingStation)));

            final LongAVLTreeSet platformIds = new LongAVLTreeSet();
            connectingStationsIncludingThisOne.forEach(connectingStation -> connectingStation.savedRails.forEach(platform -> platformIds.add(platform.getId())));
            routesForList = new ObjectArraySet<>();
            final IntAVLTreeSet addedColors = new IntAVLTreeSet();
            MinecraftClientData.getInstance().simplifiedRoutes.forEach(simplifiedRoute -> {
                final int color = simplifiedRoute.getColor();
                if (!addedColors.contains(color) && simplifiedRoute.getPlatforms().stream().anyMatch(simplifiedRoutePlatform -> platformIds.contains(simplifiedRoutePlatform.getPlatformId()))) {
                    routesForList.add(new DashboardListItem(color, simplifiedRoute.getName().split("\\|\\|")[0], color));
                    addedColors.add(color);
                }
            });
        }

        if (world != null) {
            final BlockEntity entity = world.getBlockEntity(signPos);
            if (entity instanceof BlockRailwaySignWallDouble.BlockEntity entity1) {
                signIds = entity1.getSignIds();
                selectedIds = entity1.getSelectedIds();
                isRailwaySign = true;
            } else {
                signIds = new String[2][0];
                selectedIds = new ArrayList<>();
                selectedIds.add(new LongAVLTreeSet());
                selectedIds.add(new LongAVLTreeSet());
                isRailwaySign = false;
            }
            final Block block = world.getBlockState(signPos).getBlock();
            if (block instanceof BlockRailwaySignBase block1) {
                length = block1.length;
            } else {
                length = 0;
            }
        } else {
            length = 0;
            signIds = new String[2][0];
            selectedIds = new ArrayList<>();
            selectedIds.add(new LongAVLTreeSet());
            selectedIds.add(new LongAVLTreeSet());
            isRailwaySign = false;
        }

        buttonsEdit = new Button[2][length];
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < buttonsEdit[i].length; j++) {
                final int line = i;
                final int index = j;
                buttonsEdit[i][j] = Button.builder(Component.translatable("selectWorld.edit"), button -> edit(line, index)).bounds(0, 0, 0, SQUARE_SIZE).build();
            }
        }

        buttonsSelection = new Button[allSignIds.size()];
        for (int i = 0; i < allSignIds.size(); i++) {
            final int index = i;
            buttonsSelection[i] = Button.builder(Component.empty(), button -> setNewSignId(allSignIds.get(index))).bounds(0, 0, 0, SIGN_BUTTON_SIZE).build();
        }

        buttonClear = Button.builder(Component.translatable("gui.mtr.reset_sign"), button -> setNewSignId(null)).bounds(0, 0, 0, SQUARE_SIZE).build();
        buttonPrevPage = Button.builder(Component.literal("<"), button -> setPage(page - 1)).bounds(0, 0, 0, SQUARE_SIZE).build();
        buttonNextPage = Button.builder(Component.literal(">"), button -> setPage(page + 1)).bounds(0, 0, 0, SQUARE_SIZE).build();
    }

    @Override
    protected void init() {
        super.init();

        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < buttonsEdit[i].length; j++) {
                IDrawing.setPositionAndWidth(buttonsEdit[i][j], (width - SIGN_SIZE * length) / 2 + j * SIGN_SIZE, i * (SIGN_SIZE + SQUARE_SIZE) + SIGN_SIZE, SIGN_SIZE);
                addRenderableWidget(buttonsEdit[i][j]);
            }
        }

        columns = Math.max((width - SIGN_BUTTON_SIZE * 3) / (SIGN_BUTTON_SIZE * 8) * 2, 1);
        rows = Math.max((height - SIGN_SIZE * 2 - SQUARE_SIZE * 4 - SQUARE_SIZE / 2) / SIGN_BUTTON_SIZE, 1);

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

        if (!isRailwaySign) {
            Minecraft.getInstance().setScreen(new DashboardListSelectorScreen(this::onClose, platformsForList, selectedIds.get(line), true, false, null));
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        renderBackground(guiGraphics, mouseX, mouseY, delta);
        super.render(guiGraphics, mouseX, mouseY, delta);

        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < signIds[i].length; j++) {
                if (signIds[i][j] != null) {
                    SignRenderer.drawSign(guiGraphics, signPos, signIds[i][j], (width - SIGN_SIZE * length) / 2F + j * SIGN_SIZE, i * (SQUARE_SIZE + SIGN_SIZE), SIGN_SIZE, SignRenderer.getMaxWidth(signIds[i], j, false), SignRenderer.getMaxWidth(signIds[i], j, true), selectedIds.get(i), Direction.UP, 0);
                }
            }
        }

        if (editingIndex >= 0) {
            final int xOffsetSmall = (width - SIGN_BUTTON_SIZE * (columns * 4 + 3)) / 2 + SIGN_BUTTON_SIZE;
            final int xOffsetBig = xOffsetSmall + SIGN_BUTTON_SIZE * (columns + 1);

            loopSigns((index, x, y, isBig) -> {
                final String signId = allSignIds.get(index);
                final SignResource sign = SignRenderer.getSign(signId);
                if (sign != null) {
                    final boolean moveRight = sign.hasCustomText && sign.getFlipCustomText();
                    SignRenderer.drawSign(guiGraphics, signPos, signId, (isBig ? xOffsetBig : xOffsetSmall) + x + (moveRight ? SIGN_BUTTON_SIZE * 2 : 0), BUTTON_Y_START + y, SIGN_BUTTON_SIZE, 2, 2, selectedIds.get(line), Direction.UP, 0);
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
        RegistryClient.sendPacketToServer(new PacketUpdateRailwaySignDoubleConfig(signPos, selectedIds, signIds));
        super.onClose();
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void resize(Minecraft client, int width, int height) {
        super.resize(client, width, height);
        for (int i = 0; i < 2; i++) {
            for (Button button : buttonsEdit[i]) {
                button.active = true;
            }
        }
        for (Button button : buttonsSelection) {
            button.visible = false;
        }
        editingIndex = -1;
    }

    protected int loopSigns(RailwaySignScreen.LoopSignsCallback loopSignsCallback, boolean ignorePage) {
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

    protected void edit(int line, int editingIndex) {
        this.line = line;
        this.editingIndex = editingIndex;
        for (int i = 0; i < 2; i++) {
            for (Button button : buttonsEdit[i]) {
                button.active = true;
            }
        }
        buttonClear.visible = true;
        setPage(page);
        buttonsEdit[line][editingIndex].active = false;
    }

    protected void setNewSignId(@Nullable String newSignId) {
        if (editingIndex >= 0 && editingIndex < signIds[0].length) {
            signIds[line][editingIndex] = newSignId;
            final boolean isExitLetter = IRailwaySign.signIsExit(newSignId);
            final boolean isPlatform = IRailwaySign.signIsPlatform(newSignId);
            final boolean isLine = IRailwaySign.signIsLine(newSignId);
            final boolean isStation = IRailwaySign.signIsStation(newSignId);
            if ((isExitLetter || isPlatform || isLine || isStation)) {
                Minecraft.getInstance().setScreen(new DashboardListSelectorScreen(this::onClose, new ObjectImmutableList<>(isExitLetter ? exitsForList : (isPlatform ? platformsForList : (isLine ? routesForList : stationsForList))), selectedIds.get(line), false, false, null));
            }
        }
    }

    protected void setPage(int newPage) {
        page = Mth.clamp(newPage, 0, totalPages - 1);
        buttonPrevPage.visible = editingIndex >= 0 && page > 0;
        buttonNextPage.visible = editingIndex >= 0 && page < totalPages - 1;
    }
}
