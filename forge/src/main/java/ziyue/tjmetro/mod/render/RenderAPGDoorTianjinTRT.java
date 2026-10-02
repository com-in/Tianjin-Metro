package ziyue.tjmetro.mod.render;

import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
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
import org.mtr.render.BlockEntityRendererExtension;
import net.minecraft.client.model.geom.ModelPart;
import com.mojang.blaze3d.vertex.PoseStack;
import org.mtr.MTR;
import org.mtr.block.*;
import org.mtr.data.IGui;
import org.mtr.render.MainRenderer;
import org.mtr.render.QueuedRenderLayer;
import org.mtr.render.StoredMatrixTransformations;
import ziyue.tjmetro.mod.Reference;
import ziyue.tjmetro.mod.block.BlockAPGDoorTianjinTRT;
import ziyue.tjmetro.mod.block.BlockAPGGlassTianjinTRT;

/**
 * @author ZiYueCommentary
 * @see ziyue.tjmetro.mod.block.BlockAPGDoorTianjinTRT
 * @see RenderRouteBase
 * @since 1.0.0-beta-2
 */

public class RenderAPGDoorTianjinTRT<T extends BlockPSDAPGDoorBase.BlockEntityBase> extends BlockEntityRendererExtension<T> implements IGui, IBlock
{
    protected static final ModelSingleCube MODEL_APG_TOP = new ModelSingleCube(34, 9, 0, 8, 1, 16, 8, 1);
    protected static final ModelAPGDoorBottom MODEL_APG_BOTTOM = new ModelAPGDoorBottom();
    protected static final ModelSingleCube MODEL_APG_DOOR_LOCKED = new ModelSingleCube(6, 6, 5, 10, 1, 6, 6, 0);

    public RenderAPGDoorTianjinTRT(BlockEntityRendererProvider.Context dispatcher) {
    }

    @Override
    public void render(T entity, PoseStack graphicsHolder, MultiBufferSource bufferSource, ClientLevel level, LocalPlayer player, float tickDelta, int light, int overlay) {
        final Level world = entity.getLevel();
        if (world == null) return;

        entity.tick(tickDelta);

        final BlockPos blockPos = entity.getBlockPos();
        final Direction facing = IBlock.getStatePropertySafe(world, blockPos, BlockStateProperties.HORIZONTAL_FACING);
        final boolean side = IBlock.getStatePropertySafe(world, blockPos, BlockAPGDoorTianjinTRT.SIDE) == EnumSide.RIGHT;
        final boolean half = IBlock.getStatePropertySafe(world, blockPos, BlockAPGDoorTianjinTRT.HALF) == DoubleBlockHalf.UPPER;
        final boolean unlocked = IBlock.getStatePropertySafe(world, blockPos, BlockAPGDoorTianjinTRT.UNLOCKED);
        final double open = Math.min(entity.getDoorValue(), 1);

        final StoredMatrixTransformations storedMatrixTransformations = new StoredMatrixTransformations(0.5 + entity.getBlockPos().getX(), entity.getBlockPos().getY(), 0.5 + entity.getBlockPos().getZ());
        storedMatrixTransformations.add(graphicsHolderNew -> {
            graphicsHolderNew.mulPose(Axis.YP.rotationDegrees(-facing.toYRot()));
            graphicsHolderNew.mulPose(Axis.XP.rotationDegrees(180));
        });

        // Traditional lights.
        if (half) {
            final Block sideBlock = world.getBlockState(blockPos.relative(side ? facing.getClockWise() : facing.getCounterClockWise())).getBlock();
            if (sideBlock instanceof BlockAPGGlassTianjinTRT) {
                if (open > 0) {
                    tryUpdateLightState(world, blockPos, BlockAPGDoorTianjinTRT.LightProperty.LIGHT_ON);
                } else {
                    tryUpdateLightState(world, blockPos, BlockAPGDoorTianjinTRT.LightProperty.LIGHT_OFF);
                }
            } else {
                tryUpdateLightState(world, blockPos, BlockAPGDoorTianjinTRT.LightProperty.NO_LIGHT);
            }
        }

        storedMatrixTransformations.add(matricesNew -> matricesNew.translate(open * (side ? -1 : 1), 0, 0));

        MainRenderer.scheduleRender(ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, String.format("textures/block/apg_door_tianjin_trt_%s_%s.png", half ? "top" : "bottom", side ? "right" : "left")), false, QueuedRenderLayer.EXTERIOR, (graphicsHolderNew, vertexConsumer, offset) -> {
            storedMatrixTransformations.transform(graphicsHolderNew, offset);
            if (half) {
                MODEL_APG_TOP.render(graphicsHolderNew, vertexConsumer, light, overlay);
            } else {
                MODEL_APG_BOTTOM.render(graphicsHolderNew, vertexConsumer, light, overlay);
            }
            graphicsHolderNew.popPose();
        });
        if (half && !unlocked) {
            MainRenderer.scheduleRender(ResourceLocation.fromNamespaceAndPath(MTR.MOD_ID, "textures/block/sign/door_not_in_use.png"), false, QueuedRenderLayer.EXTERIOR, (graphicsHolderNew, vertexConsumer, offset) -> {
                storedMatrixTransformations.transform(graphicsHolderNew, offset);
                MODEL_APG_DOOR_LOCKED.render(graphicsHolderNew, vertexConsumer, light, overlay);
                graphicsHolderNew.popPose();
            });
        }
    }

    @Override
    public boolean shouldRenderOffScreen(T blockEntity) {
        return true;
    }

    protected void tryUpdateLightState(Level world, BlockPos blockPos, BlockAPGDoorTianjinTRT.LightProperty lightProperty) {
        if (IBlock.getStatePropertySafe(world, blockPos, BlockAPGDoorTianjinTRT.LIGHT) != lightProperty) {
            world.setBlock(blockPos, world.getBlockState(blockPos).setValue(BlockAPGDoorTianjinTRT.LIGHT, lightProperty), 3);
        }
    }

    protected static class ModelSingleCube
    {
        protected final ModelPart cube;

        protected ModelSingleCube(int textureWidth, int textureHeight, int x, int y, int z, int length, int height, int depth) {
            cube = RenderDoorModelHelper.createSingleCube(textureWidth, textureHeight, x, y, z, length, height, depth);
        }

        public void render(PoseStack graphicsHolder, VertexConsumer vertexConsumer, int packedLight, int packedOverlay) {
            cube.render(graphicsHolder, vertexConsumer, packedLight, packedOverlay);
        }
    }

    protected static class ModelAPGDoorBottom
    {
        protected final ModelPart bone;

        protected ModelAPGDoorBottom() {
            bone = RenderDoorModelHelper.createModelPart(root -> {
                RenderDoorModelHelper.createModelPartData(root, 0, 0, -8, -16, -7, 16, 16, 1, 0, 0, 0, 0, 0, 0, 0);
                RenderDoorModelHelper.createModelPartData(root, 0, 17, -8, -6, -8, 16, 6, 1, 0, 0, 0, 0, 0, 0, 0);
                RenderDoorModelHelper.createModelPartData(root, 0, 24, -8, -2, 0, 16, 2, 1, 0, 0, -6, -8, -0.7854F, 0, 0);
            }, 34, 27);
        }

        public void render(PoseStack graphicsHolder, VertexConsumer vertexConsumer, int packedLight, int packedOverlay) {
            bone.render(graphicsHolder, vertexConsumer, packedLight, packedOverlay);
        }
    }
}
