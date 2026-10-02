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
import org.mtr.block.IBlock;
import org.mtr.client.IDrawing;
import org.mtr.data.IGui;
import org.mtr.render.MainRenderer;
import org.mtr.render.QueuedRenderLayer;
import org.mtr.render.StoredMatrixTransformations;
import org.mtr.resource.SignResource;
import ziyue.tjmetro.mod.block.BlockRailwaySignWall;
import ziyue.tjmetro.mod.block.BlockRailwaySignWallBig;
import ziyue.tjmetro.mod.block.base.BlockRailwaySignBase;

import static ziyue.tjmetro.mod.render.RenderRailwaySignHelper.getMaxWidth;
import static ziyue.tjmetro.mod.render.RenderRailwaySignHelper.getSign;

/**
 * @author ZiYueCommentary
 * @see BlockRailwaySignWall
 * @see BlockRailwaySignWallBig
 * @since 1.0.0-beta-1
 */

public class RenderRailwaySignWall<T extends BlockRailwaySignBase.BlockEntityBase> extends BlockEntityRendererExtension<T> implements IBlock, IGui, IDrawing
{
    public RenderRailwaySignWall(BlockEntityRendererProvider.Context dispatcher) {
    }

    @Override
    public void render(T entity, PoseStack graphicsHolder, MultiBufferSource bufferSource, ClientLevel level, LocalPlayer player, float tickDelta, int light, int overlay) {
        final Level world = entity.getLevel();
        if (world == null) return;

        final BlockPos pos = entity.getBlockPos();
        final BlockState state = world.getBlockState(pos);
        if (!(state.getBlock() instanceof BlockRailwaySignBase block)) return;

        if (entity.getSignIds().length != block.length) return;

        final Direction facing = IBlock.getStatePropertySafe(state, BlockStateProperties.HORIZONTAL_FACING);
        final String[] signIds = entity.getSignIds();

        int backgroundColor = 0;
        for (final String signId : signIds) {
            if (signId != null) {
                final SignResource sign = getSign(signId);
                if (sign != null) {
                    if (sign.getBackgroundColor() != 0) {
                        backgroundColor = sign.getBackgroundColor();
                        break;
                    }
                }
            }
        }

        final StoredMatrixTransformations storedMatrixTransformations = new StoredMatrixTransformations(0.5 + entity.getBlockPos().getX(), 0.53125 + entity.getBlockPos().getY(), 0.5 + entity.getBlockPos().getZ());
        storedMatrixTransformations.add(graphicsHolderNew -> {
            graphicsHolderNew.mulPose(Axis.YP.rotationDegrees(-facing.toYRot()));
            graphicsHolderNew.mulPose(Axis.ZP.rotationDegrees(180));
            if (!IBlock.getStatePropertySafe(state, BlockRailwaySignWall.NOT_GROUND)) {
                graphicsHolderNew.mulPose(Axis.XP.rotationDegrees(-90));
                graphicsHolderNew.translate(0, -0.03, 0.02);
            }
            graphicsHolderNew.translate(block.getXStart() / 16F - 0.5, -0.25, 0.493);
            if (entity instanceof BlockRailwaySignWallBig.BlockEntity) {
                graphicsHolderNew.translate(0, -0.218, -0.003);
                graphicsHolderNew.scale(2, 2, 2);
            }
        });

        graphicsHolder.pushPose();
        graphicsHolder.translate(0.5, 0.53125, 0.5);
        graphicsHolder.mulPose(Axis.YP.rotationDegrees(-facing.toYRot()));
        graphicsHolder.mulPose(Axis.ZP.rotationDegrees(180));
        graphicsHolder.translate(block.getXStart() / 16F - 0.5, 0, -0.0625 - SMALL_OFFSET * 2);

        final int newBackgroundColor = backgroundColor | ARGB_BLACK;
        MainRenderer.scheduleRender(ResourceLocation.fromNamespaceAndPath(MTR.MOD_ID, "textures/block/white.png"), false, QueuedRenderLayer.LIGHT, (poseStack, vertexConsumer, offset) -> {
            storedMatrixTransformations.transform(poseStack, offset);
            IDrawing.drawTexture(poseStack, vertexConsumer, 0, 0, SMALL_OFFSET, 0.5F * (signIds.length), 0.5F, SMALL_OFFSET, facing, newBackgroundColor, LightTexture.FULL_BRIGHT);
            poseStack.popPose();
        });
        for (int i = 0; i < signIds.length; i++) {
            if (signIds[i] != null) {
                final RenderRailwaySignHelper.DrawTexture drawTexture = (textureId, x, y, size, flipTexture) -> MainRenderer.scheduleRender(textureId, true, QueuedRenderLayer.LIGHT_TRANSLUCENT, (poseStack, vertexConsumer, offset) -> {
                    storedMatrixTransformations.transform(poseStack, offset);
                    IDrawing.drawTexture(poseStack, vertexConsumer, x, y, size, size, flipTexture ? 1 : 0, 0, flipTexture ? 0 : 1, 1, facing, -1, LightTexture.FULL_BRIGHT);
                    poseStack.popPose();
                });
                RenderRailwaySignWallDouble.drawSign(
                        graphicsHolder,
                        bufferSource,
                        storedMatrixTransformations,
                        pos,
                        signIds[i],
                        0.5F * i,
                        0,
                        0.5F,
                        getMaxWidth(signIds, i, false),
                        getMaxWidth(signIds, i, true),
                        entity.getSelectedIds(),
                        facing,
                        backgroundColor | ARGB_BLACK,
                        drawTexture,
                        entity.getToggleStyle()
                );
            }
        }

        graphicsHolder.popPose();
    }
}
