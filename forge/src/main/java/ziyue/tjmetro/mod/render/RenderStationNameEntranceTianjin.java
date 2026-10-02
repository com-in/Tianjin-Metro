package ziyue.tjmetro.mod.render;

import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
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
import org.mtr.core.data.Station;
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
import org.mtr.MTRClient;
import org.mtr.block.BlockStationNameBase;
import org.mtr.block.IBlock;
import org.mtr.client.IDrawing;
import org.mtr.data.IGui;
import org.mtr.render.MainRenderer;
import org.mtr.render.QueuedRenderLayer;
import org.mtr.render.StoredMatrixTransformations;
import ziyue.tjmetro.mod.block.BlockStationNameEntranceTianjin;
import ziyue.tjmetro.mod.client.DynamicTextureCache;
import ziyue.tjmetro.mod.IGuiExtension;

import javax.annotation.Nullable;

/**
 * @author ZiYueCommentary
 * @see BlockStationNameEntranceTianjin
 * @since 1.0.0-beta-1
 */

public class RenderStationNameEntranceTianjin<T extends BlockStationNameEntranceTianjin.BlockEntity> extends BlockEntityRendererExtension<T> implements IGui, IDrawing
{
    public RenderStationNameEntranceTianjin(BlockEntityRendererProvider.Context argument) {
    }

    @Override
    public void render(T entity, PoseStack graphicsHolder, MultiBufferSource bufferSource, ClientLevel level, LocalPlayer player, float tickDelta, int light, int overlay) {
        final Level world = entity.getLevel();
        if (world == null) return;

        final BlockPos pos = entity.getBlockPos();
        final BlockState state = world.getBlockState(pos);
        if (!(state.getBlock() instanceof BlockStationNameEntranceTianjin block)) return;
        final Direction facing = IBlock.getStatePropertySafe(state, BlockStateProperties.HORIZONTAL_FACING);

        final StoredMatrixTransformations storedMatrixTransformations = new StoredMatrixTransformations(0.5 + pos.getX(), 0.5 + entity.yOffset + pos.getY(), 0.5 + pos.getZ());
        storedMatrixTransformations.add(graphicsHolderNew -> {
            graphicsHolderNew.mulPose(Axis.YP.rotationDegrees(-facing.toYRot()));
            graphicsHolderNew.mulPose(Axis.ZP.rotationDegrees(180));
            graphicsHolderNew.translate(0, 0, 0.5 - entity.zOffset - SMALL_OFFSET);
        });

        final Station station = MTRClient.findStation(pos);

        final int lengthLeft = getLength(world, pos, false);
        final int lengthRight = getLength(world, pos, true);

        final int totalLength = lengthLeft + lengthRight - 1;
        final int propagateProperty = IBlock.getStatePropertySafe(world, pos, BlockStationNameEntranceTianjin.STYLE);
        final float logoSize = propagateProperty % 2 == 0 ? 0.5F : 1;

        final DynamicTextureCache.DynamicResource resource;
        if (station == null) {
            final int style = switch (propagateProperty) {
                case 0, 1, 4, 5 -> propagateProperty + 2;
                default -> propagateProperty;
            };
            resource = DynamicTextureCache.instance.getStationNameEntrance(-1, -1, style, IGuiExtension.insertTranslation("gui.mtr.station_cjk", "gui.mtr.station", 1, Component.translatable("gui.mtr.untitled").getString()), block.type, totalLength / logoSize);
        } else {
            resource = DynamicTextureCache.instance.getStationNameEntrance(station.getId(), entity.getSelectedId(), propagateProperty, IGuiExtension.insertTranslation("gui.mtr.station_cjk", block.pinyin ? "gui.tjmetro.station_pinyin" : "gui.mtr.station", 1, station.getName()), block.type, totalLength / logoSize);
        }
        MainRenderer.scheduleRender(resource.identifier, false, QueuedRenderLayer.EXTERIOR, (graphicsHolderNew, vertexConsumer, offset) -> {
            storedMatrixTransformations.transform(graphicsHolderNew, offset);
            IDrawing.drawTexture(graphicsHolderNew, vertexConsumer, -0.5F, -logoSize / 2, 1, logoSize, (float) (lengthLeft - 1) / totalLength, 0, (float) lengthLeft / totalLength, 1, facing, ARGB_WHITE, light);
            graphicsHolderNew.popPose();
        });
    }

    protected int getLength(@Nullable Level world, BlockPos pos, boolean lookRight) {
        if (world == null) return 1;
        final Direction facing = IBlock.getStatePropertySafe(world, pos, BlockStateProperties.HORIZONTAL_FACING);
        final Block thisBlock = world.getBlockState(pos).getBlock();

        int length = 1;
        while (true) {
            final Block checkBlock = world.getBlockState(pos.relative(lookRight ? facing.getClockWise() : facing.getCounterClockWise(), length)).getBlock();
            if (checkBlock instanceof BlockStationNameEntranceTianjin && checkBlock == thisBlock) {
                length++;
            } else {
                break;
            }
        }

        return length;
    }
}
