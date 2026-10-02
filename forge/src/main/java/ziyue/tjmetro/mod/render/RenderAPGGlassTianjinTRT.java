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
import com.mojang.blaze3d.vertex.PoseStack;
import org.mtr.MTRClient;
import org.mtr.block.BlockAPGGlass;
import org.mtr.block.IBlock;
import org.mtr.client.IDrawing;
import org.mtr.render.MainRenderer;
import org.mtr.render.QueuedRenderLayer;
import org.mtr.render.StoredMatrixTransformations;
import ziyue.tjmetro.mod.block.BlockAPGGlassTianjinTRT;
import ziyue.tjmetro.mod.client.DynamicTextureCache;

import static org.mtr.render.RenderRouteBase.getShadingColor;

/**
 * @author ZiYueCommentary
 * @see ziyue.tjmetro.mod.block.BlockAPGGlassTianjinTRT
 * @see RenderRouteBase
 * @since 1.0.0-beta-5
 */

public class RenderAPGGlassTianjinTRT extends RenderRouteBase<BlockAPGGlassTianjinTRT.BlockEntity>
{
    public RenderAPGGlassTianjinTRT(BlockEntityRendererProvider.Context dispatcher) {
        super(dispatcher, 4, 9, 3, 8, true, 2, BlockAPGGlass.ARROW_DIRECTION);
    }

    @Override
    public void render(BlockAPGGlassTianjinTRT.BlockEntity entity, PoseStack graphicsHolder, MultiBufferSource bufferSource, ClientLevel level, LocalPlayer player, float tickDelta, int light, int overlay) {
        final Level world = entity.getLevel();
        if (world == null) return;

        final BlockPos blockPos = entity.getBlockPos();
        final BlockState state = world.getBlockState(blockPos);
        final Direction facing = IBlock.getStatePropertySafe(state, BlockStateProperties.HORIZONTAL_FACING);

        final StoredMatrixTransformations storedMatrixTransformations = new StoredMatrixTransformations(0.5 + entity.getBlockPos().getX(), entity.getBlockPos().getY(), 0.5 + entity.getBlockPos().getZ());
        storedMatrixTransformations.add(graphicsHolderNew -> graphicsHolderNew.mulPose(Axis.YP.rotationDegrees(-facing.toYRot())));

        MTRClient.findClosePlatform(blockPos.below(platformSearchYOffset), 5, platform -> {
            final long platformId = platform.getId();

            storedMatrixTransformations.add(graphicsHolderNew -> {
                graphicsHolderNew.translate(0, 1, 0);
                graphicsHolderNew.mulPose(Axis.ZP.rotationDegrees(180));
                graphicsHolderNew.translate(-0.5, -getAdditionalOffset(state), z);
            });

            final int leftBlocks = getTextureNumber(world, blockPos, facing, true);
            final int rightBlocks = getTextureNumber(world, blockPos, facing, false);
            final int color = getShadingColor(facing, ARGB_WHITE);
            final RenderType renderType = getRenderType(world, blockPos.relative(facing.getCounterClockWise(), leftBlocks), state);

            if ((renderType != RenderType.NONE) && (IBlock.getStatePropertySafe(state, SIDE_EXTENDED) != EnumSide.SINGLE)) {
                final float width = leftBlocks + rightBlocks + 1 - sidePadding * 2;
                final float height = 1 - topPadding - bottomPadding;
                final int arrowDirection = IBlock.getStatePropertySafe(state, arrowDirectionProperty);

                final ResourceLocation identifier = DynamicTextureCache.instance.getRouteMapTRT(platformId, false, arrowDirection == 2, width / height, transparentWhite).identifier;

                MainRenderer.scheduleRender(identifier, false, QueuedRenderLayer.EXTERIOR, (graphicsHolderNew, vertexConsumer, offset) -> {
                    storedMatrixTransformations.transform(graphicsHolderNew, offset);
                    IDrawing.drawTexture(graphicsHolderNew, vertexConsumer, leftBlocks == 0 ? sidePadding : 0, topPadding, 0, 1 - (rightBlocks == 0 ? sidePadding : 0), 1 - bottomPadding, 0, (leftBlocks - (leftBlocks == 0 ? 0 : sidePadding)) / width, 0, (width - rightBlocks + (rightBlocks == 0 ? 0 : sidePadding)) / width, 1, facing.getOpposite(), color, light);
                    graphicsHolderNew.popPose();
                });
            }
        });
    }

    @Override
    protected RenderType getRenderType(Level world, BlockPos pos, BlockState state) {
        if (IBlock.getStatePropertySafe(state, HALF) == DoubleBlockHalf.LOWER) {
            return RenderType.NONE;
        } else {
            return RenderType.ROUTE;
        }
    }

    @Override
    protected void renderAdditional(StoredMatrixTransformations storedMatrixTransformations, long platformId, BlockState state, int leftBlocks, int rightBlocks, Direction facing, int color, int light) {
    }
}
