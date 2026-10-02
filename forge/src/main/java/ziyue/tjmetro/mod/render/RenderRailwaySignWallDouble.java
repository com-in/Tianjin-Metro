package ziyue.tjmetro.mod.render;

import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import static org.mtr.data.IGui.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.ChatFormatting;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.AbstractWidget;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.math.Axis;
import org.mtr.core.data.NameColorDataBase;
import org.mtr.core.data.Station;
import org.mtr.core.data.StationExit;
import org.mtr.libraries.it.unimi.dsi.fastutil.ints.IntAVLTreeSet;
import org.mtr.libraries.it.unimi.dsi.fastutil.ints.IntObjectImmutablePair;
import org.mtr.libraries.it.unimi.dsi.fastutil.longs.LongAVLTreeSet;
import org.mtr.libraries.it.unimi.dsi.fastutil.longs.LongArrayList;
import org.mtr.libraries.it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.ChatFormatting;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.phys.AABB;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.components.AbstractWidget;
import com.mojang.blaze3d.platform.NativeImage;
import org.mtr.render.BlockEntityRendererExtension;
import com.mojang.blaze3d.vertex.PoseStack;
import org.mtr.MTR;
import org.mtr.MTRClient;
import org.mtr.block.BlockRailwaySign;
import org.mtr.block.IBlock;
import org.mtr.client.IDrawing;
import org.mtr.client.MinecraftClientData;
import org.mtr.data.IGui;
import org.mtr.render.MainRenderer;
import org.mtr.render.QueuedRenderLayer;
import org.mtr.render.RenderRailwaySign;
import org.mtr.render.StoredMatrixTransformations;
import org.mtr.resource.SignResource;
import org.mtr.screen.RailwaySignScreen;
import ziyue.tjmetro.mod.block.BlockRailwaySignWall;
import ziyue.tjmetro.mod.block.BlockRailwaySignWallDouble;
import ziyue.tjmetro.mod.block.base.IRailwaySign;
import ziyue.tjmetro.mod.client.DynamicTextureCache;
import ziyue.tjmetro.mod.IGuiExtension;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static ziyue.tjmetro.mod.render.RenderRailwaySignHelper.SMALL_SIGN_PERCENTAGE;
import static ziyue.tjmetro.mod.render.RenderRailwaySignHelper.getMaxWidth;
import static ziyue.tjmetro.mod.render.RenderRailwaySignHelper.getSign;

/**
 * @author ZiYueCommentary
 * @see BlockRailwaySignWallDouble
 * @since 1.0.0-beta-1
 */

public class RenderRailwaySignWallDouble<T extends BlockRailwaySignWallDouble.BlockEntity> extends BlockEntityRendererExtension<T> implements IBlock, IGui, IDrawing
{
    public RenderRailwaySignWallDouble(BlockEntityRendererProvider.Context dispatcher) {
    }

    @Override
    public void render(T entity, PoseStack graphicsHolder, MultiBufferSource bufferSource, ClientLevel level, LocalPlayer player, float tickDelta, int light, int overlay) {
        final Level world = entity.getLevel();
        if (world == null) return;

        final BlockPos pos = entity.getBlockPos();
        final BlockState state = world.getBlockState(pos);
        if (!(state.getBlock() instanceof BlockRailwaySignWallDouble block)) return;

        if (entity.getSignIds()[0].length != block.length) return;

        final Direction facing = IBlock.getStatePropertySafe(state, BlockStateProperties.HORIZONTAL_FACING);
        final String[][] signIds = entity.getSignIds();

        int[] backgroundColor = new int[2];
        for (int i = 0; i < 2; i++) {
            for (final String signId : signIds[i]) {
                if (signId != null) {
                    final SignResource sign = getSign(signId);
                    if (sign != null) {
                        if (sign.getBackgroundColor() != 0) {
                            backgroundColor[i] = sign.getBackgroundColor();
                            break;
                        }
                    }
                }
            }
        }

        final StoredMatrixTransformations storedMatrixTransformations = new StoredMatrixTransformations(0.5 + entity.getBlockPos().getX(), entity.getBlockPos().getY() + 1, 0.5 + entity.getBlockPos().getZ());
        storedMatrixTransformations.add(graphicsHolderNew -> {
            graphicsHolderNew.mulPose(Axis.YP.rotationDegrees(-facing.toYRot()));
            graphicsHolderNew.mulPose(Axis.ZP.rotationDegrees(180));
            if (!IBlock.getStatePropertySafe(state, BlockRailwaySignWall.NOT_GROUND)) {
                graphicsHolderNew.mulPose(Axis.XP.rotationDegrees(-90));
                graphicsHolderNew.translate(0, -0.5, 0.5);
            }
            graphicsHolderNew.translate(block.getXStart() / 16F - 0.5, 0, 0.493);
        });

        graphicsHolder.pushPose();
        graphicsHolder.translate(0.5, 0.53125, 0.5);
        graphicsHolder.mulPose(Axis.YP.rotationDegrees(-facing.toYRot()));
        graphicsHolder.mulPose(Axis.ZP.rotationDegrees(180));
        graphicsHolder.translate(block.getXStart() / 16F - 0.5, 0, -0.0625 - SMALL_OFFSET * 2);

        final int[] newBackgroundColor = {backgroundColor[0] | ARGB_BLACK, backgroundColor[1] | ARGB_BLACK};
        MainRenderer.scheduleRender(ResourceLocation.fromNamespaceAndPath(MTR.MOD_ID, "textures/block/white.png"), false, QueuedRenderLayer.LIGHT, (poseStack, vertexConsumer, offset) -> {
            storedMatrixTransformations.transform(poseStack, offset);
            IDrawing.drawTexture(poseStack, vertexConsumer, 0, 0, SMALL_OFFSET, 0.5F * (signIds[0].length), 0.5F, SMALL_OFFSET, facing, newBackgroundColor[0], LightTexture.FULL_BRIGHT);
            IDrawing.drawTexture(poseStack, vertexConsumer, 0, 0.5F, SMALL_OFFSET, 0.5F * (signIds[1].length), 1F, SMALL_OFFSET, facing, newBackgroundColor[1], LightTexture.FULL_BRIGHT);
            poseStack.popPose();
        });
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < signIds[i].length; j++) {
                if (signIds[i][j] != null) {
                    drawSign(
                            graphicsHolder,
                            bufferSource,
                            storedMatrixTransformations,
                            pos,
                            signIds[i][j],
                            0.5F * j,
                            0.5F * i,
                            0.5F,
                            getMaxWidth(signIds[i], j, false),
                            getMaxWidth(signIds[i], j, true),
                            entity.getSelectedIds().get(i),
                            facing,
                            backgroundColor[i] | ARGB_BLACK,
                            (textureId, x, y, size, flipTexture) -> MainRenderer.scheduleRender(textureId, true, QueuedRenderLayer.LIGHT_TRANSLUCENT, (poseStack, vertexConsumer, offset) -> {
                                storedMatrixTransformations.transform(poseStack, offset);
                                IDrawing.drawTexture(poseStack, vertexConsumer, x, y, size, size, flipTexture ? 1 : 0, 0, flipTexture ? 0 : 1, 1, facing, -1, LightTexture.FULL_BRIGHT);
                                poseStack.popPose();
                            }),
                            entity.getToggleStyle()
                    );
                }
            }
        }

        graphicsHolder.popPose();
    }

    public static void drawSign(PoseStack graphicsHolder, MultiBufferSource bufferSource, @Nullable StoredMatrixTransformations storedMatrixTransformations, BlockPos pos, String signId, float x, float y, float size, float maxWidthLeft, float maxWidthRight, LongAVLTreeSet selectedIds, Direction facing, int backgroundColor, RenderRailwaySignHelper.DrawTexture drawTexture, boolean toggleStyle) {
        final SignResource sign = getSign(signId);
        if (sign == null) return;

        final float signSize = (sign.getSmall() ? SMALL_SIGN_PERCENTAGE : 1) * size;
        final float margin = (size - signSize) / 2;

        final boolean hasCustomText = sign.hasCustomText;
        final boolean flipCustomText = sign.getFlipCustomText();
        final boolean flipTexture = sign.getFlipTexture();
        final boolean isExit = IRailwaySign.signIsExit(signId);
        final boolean isLine = IRailwaySign.signIsLine(signId);
        final boolean isPlatform = IRailwaySign.signIsPlatform(signId);
        final boolean isStation = IRailwaySign.signIsStation(signId);

        if (storedMatrixTransformations != null && isExit) {
            final Station station = MTRClient.findStation(pos);
            if (station == null) return;

            final ObjectArrayList<StationExit> selectedExitsSorted = new ObjectArrayList<>();
            SignResource.getStationExits(pos).forEach(exit -> {
                if (selectedIds.contains(RailwaySignScreen.serializeExit(exit.getName()))) {
                    selectedExitsSorted.add(exit);
                }
            });

            graphicsHolder.pushPose();
            graphicsHolder.translate(x + margin + (flipCustomText ? signSize : 0), y + margin, 0);
            final float maxWidth = ((flipCustomText ? maxWidthLeft : maxWidthRight) + 1) * size - margin * 2;
            final float exitWidth = signSize * selectedExitsSorted.size();
            graphicsHolder.scale(Math.min(1, maxWidth / exitWidth), 1, 1);

            for (int i = 0; i < selectedExitsSorted.size(); i++) {
                final StationExit stationExit = selectedExitsSorted.get(flipCustomText ? selectedExitsSorted.size() - i - 1 : i);
                final float signOffset = (flipCustomText ? -1 : 1) * signSize * i - (flipCustomText ? signSize : 0);

                MainRenderer.scheduleRender(IRailwaySign.getExitSignResource(signId, stationExit.getName().substring(0, 1), stationExit.getName().substring(1), backgroundColor, ARGB_WHITE, !toggleStyle), true, QueuedRenderLayer.LIGHT_TRANSLUCENT, (poseStack, vertexConsumer, offset) -> {
                    storedMatrixTransformations.transform(poseStack, offset);
                    poseStack.translate(x + margin + (flipCustomText ? signSize : 0), margin, 0);
                    poseStack.scale(Math.min(1, maxWidth / exitWidth), 1, 1);
                    IDrawing.drawTexture(poseStack, vertexConsumer, signOffset, y, signSize, signSize, facing, LightTexture.FULL_BRIGHT);
                    poseStack.popPose();
                });

                if (maxWidth > exitWidth && selectedExitsSorted.size() == 1 && !stationExit.getDestinations().isEmpty()) {
                    renderCustomText(stationExit.getDestinations().get(0), storedMatrixTransformations, facing, size, flipCustomText ? x : x + size, y, flipCustomText, maxWidth - exitWidth - margin * 2, backgroundColor, toggleStyle);
                }
            }

            graphicsHolder.popPose();
        } else if (storedMatrixTransformations != null && isLine) {
            final Station station = MTRClient.findStation(pos);
            if (station == null) return;

            final LongAVLTreeSet platformIds = new LongAVLTreeSet();
            station.savedRails.forEach(platform -> platformIds.add(platform.getId()));
            station.connectedStations.forEach(connectingStation -> connectingStation.savedRails.forEach(platform -> platformIds.add(platform.getId())));

            final ObjectArrayList<IntObjectImmutablePair<String>> selectedRoutesSorted = new ObjectArrayList<>();
            final IntAVLTreeSet addedColors = new IntAVLTreeSet();
            MinecraftClientData.getInstance().simplifiedRoutes.forEach(simplifiedRoute -> {
                final int color = simplifiedRoute.getColor();
                if (!addedColors.contains(color) && selectedIds.contains(color) && simplifiedRoute.getPlatforms().stream().anyMatch(simplifiedRoutePlatform -> platformIds.contains(simplifiedRoutePlatform.getPlatformId()))) {
                    selectedRoutesSorted.add(new IntObjectImmutablePair<>(color, simplifiedRoute.getName().split("\\|\\|")[0]));
                    addedColors.add(color);
                }
            });

            selectedRoutesSorted.sort(Comparator.comparingInt(IntObjectImmutablePair::leftInt));
            final float maxWidth = Math.max(0, ((flipCustomText ? maxWidthLeft : maxWidthRight) + 1) * size - margin * 2);
            final float height = size - margin * 2;
            final List<DynamicTextureCache.DynamicResource> resourceLocationDataList = new ArrayList<>();
            float totalTextWidth = 0;
            for (final IntObjectImmutablePair<String> route : selectedRoutesSorted) {
                if (toggleStyle) {
                    final DynamicTextureCache.DynamicResource resourceLocationData = DynamicTextureCache.instance.getRouteSquare(route.leftInt(), route.right(), flipCustomText ? HorizontalAlignment.RIGHT : HorizontalAlignment.LEFT);
                    resourceLocationDataList.add(resourceLocationData);
                    totalTextWidth += height * resourceLocationData.width / resourceLocationData.height + margin / 2F;
                } else {
                    final org.mtr.client.DynamicTextureCache.DynamicResource resourceLocationData = org.mtr.client.DynamicTextureCache.instance.getRouteSquare(route.leftInt(), route.right(), flipCustomText ? HorizontalAlignment.RIGHT : HorizontalAlignment.LEFT);
                    resourceLocationDataList.add(new DynamicTextureCache.DynamicResource(resourceLocationData));
                    totalTextWidth += height * resourceLocationData.width / resourceLocationData.height + margin / 2F;
                }
            }

            final StoredMatrixTransformations storedMatrixTransformations2 = storedMatrixTransformations.copy();
            storedMatrixTransformations2.add(graphicsHolderNew -> graphicsHolderNew.translate(flipCustomText ? x + size - margin : x + margin, 0, 0));

            if (totalTextWidth > margin / 2F) {
                totalTextWidth -= margin / 2F;
            }
            if (totalTextWidth > maxWidth) {
                final float finalTotalTextWidth = totalTextWidth;
                storedMatrixTransformations2.add(graphicsHolderNew -> graphicsHolderNew.scale(maxWidth / finalTotalTextWidth, 1, 1));
            }

            float xOffset = 0;
            for (final DynamicTextureCache.DynamicResource resourceLocationData : resourceLocationDataList) {
                final float width = height * resourceLocationData.width / resourceLocationData.height;
                final float finalXOffset = xOffset;
                MainRenderer.scheduleRender(resourceLocationData.identifier, true, QueuedRenderLayer.LIGHT, (poseStack, vertexConsumer, offset) -> {
                    storedMatrixTransformations2.transform(poseStack, offset);
                    IDrawing.drawTexture(poseStack, vertexConsumer, flipCustomText ? -finalXOffset - width : finalXOffset, margin + y, width, height, Direction.UP, LightTexture.FULL_BRIGHT);
                    poseStack.popPose();
                });
                xOffset += width + margin / 2F;
            }
        } else if (storedMatrixTransformations != null && isPlatform) {
            final Station station = MTRClient.findStation(pos);
            if (station == null) return;

            final LongArrayList selectedIdsSorted = station.savedRails.stream().sorted().mapToLong(NameColorDataBase::getId).filter(selectedIds::contains).boxed().collect(Collectors.toCollection(LongArrayList::new));
            final int selectedCount = selectedIdsSorted.size();

            final float extraMargin = margin - margin / selectedCount;
            final float height = (size - extraMargin * 2) / selectedCount;
            for (int i = 0; i < selectedIdsSorted.size(); i++) {
                final float topOffset = i * height + extraMargin + y;
                final float bottomOffset = (i + 1) * height + extraMargin + y;
                final float left = flipCustomText ? x - maxWidthLeft * size : x + margin;
                final float right = flipCustomText ? x + size - margin : x + (maxWidthRight + 1) * size;
                MainRenderer.scheduleRender(IRailwaySign.getPlatformSignResource(signId, selectedIdsSorted.getLong(i), flipCustomText ? HorizontalAlignment.RIGHT : HorizontalAlignment.LEFT, margin / size, (right - left) / (bottomOffset - topOffset), backgroundColor, ARGB_WHITE, backgroundColor, !toggleStyle), true, QueuedRenderLayer.LIGHT_TRANSLUCENT, (poseStack, vertexConsumer, offset) -> {
                    storedMatrixTransformations.transform(poseStack, offset);
                    IDrawing.drawTexture(poseStack, vertexConsumer, left, topOffset, 0, right, bottomOffset, 0, 0, 0, 1, 1, facing, -1, LightTexture.FULL_BRIGHT);
                    poseStack.popPose();
                });
            }
        } else {
            drawTexture.drawTexture(sign.textureId, x + margin, y + margin, signSize, flipTexture);

            if (hasCustomText) {
                final float fixedMargin = size * (1 - SMALL_SIGN_PERCENTAGE) / 2;
                final boolean isSmall = sign.getSmall();
                final float maxWidth = Math.max(0, (flipCustomText ? maxWidthLeft : maxWidthRight) * size - fixedMargin * (isSmall ? 1 : 2));
                final float start = flipCustomText ? x - (isSmall ? 0 : fixedMargin) : x + size + (isSmall ? 0 : fixedMargin);
                if (storedMatrixTransformations == null) {
                    IDrawing.drawStringWithFont(graphicsHolder, bufferSource, isExit || isLine ? "..." : sign.getCustomText(), flipCustomText ? HorizontalAlignment.RIGHT : HorizontalAlignment.LEFT, VerticalAlignment.TOP, start, y + fixedMargin, maxWidth, size - fixedMargin * 2, 0.01F, ARGB_WHITE, false, LightTexture.FULL_BRIGHT, null);
                } else {
                    final String signText;
                    if (isStation) {
                        signText = IGui.mergeStations(selectedIds.longStream()
                                .filter(MinecraftClientData.getInstance().stationIdMap::containsKey)
                                .sorted()
                                .mapToObj(stationId -> IGuiExtension.insertTranslation("gui.mtr.station_cjk", "gui.mtr.station", 1, MinecraftClientData.getInstance().stationIdMap.get(stationId).getName()))
                                .collect(Collectors.toList())
                        );
                    } else {
                        signText = sign.getCustomText();
                    }
                    renderCustomText(signText, storedMatrixTransformations, facing, size, start, y, flipCustomText, maxWidth, backgroundColor, toggleStyle);
                }
            }
        }
    }

    protected static void renderCustomText(String signText, StoredMatrixTransformations storedMatrixTransformations, Direction facing, float size, float start, float offset, boolean flipCustomText, float maxWidth, int backgroundColor, boolean toggleStyle) {
        final DynamicTextureCache.DynamicResource dynamicResource;
        if (toggleStyle) {
            dynamicResource = DynamicTextureCache.instance.getSignText(signText, flipCustomText ? HorizontalAlignment.RIGHT : HorizontalAlignment.LEFT, (1 - SMALL_SIGN_PERCENTAGE) / 2, backgroundColor, ARGB_WHITE);
        } else {
            dynamicResource = new DynamicTextureCache.DynamicResource(org.mtr.client.DynamicTextureCache.instance.getSignText(signText, flipCustomText ? HorizontalAlignment.RIGHT : HorizontalAlignment.LEFT, (1 - SMALL_SIGN_PERCENTAGE) / 2, backgroundColor, ARGB_WHITE));
        }
        final float width = Math.min(size * dynamicResource.width / dynamicResource.height, maxWidth);
        MainRenderer.scheduleRender(dynamicResource.identifier, true, QueuedRenderLayer.LIGHT_TRANSLUCENT, (poseStack, vertexConsumer, offset1) -> {
            storedMatrixTransformations.transform(poseStack, offset1);
            IDrawing.drawTexture(poseStack, vertexConsumer, start - (flipCustomText ? width : 0), offset, 0, start + (flipCustomText ? 0 : width), size + offset, 0, 0, 0, 1, 1, facing, -1, LightTexture.FULL_BRIGHT);
            poseStack.popPose();
        });
    }
}
