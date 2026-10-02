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
import com.mojang.blaze3d.vertex.PoseStack;
import org.mtr.MTR;
import org.mtr.block.BlockAPGGlass;
import org.mtr.block.BlockAPGGlassEnd;
import org.mtr.block.BlockPSDAPGDoorBase;
import org.mtr.block.IBlock;
import org.mtr.data.IGui;
import org.mtr.render.MainRenderer;
import org.mtr.render.QueuedRenderLayer;
import org.mtr.render.StoredMatrixTransformations;
import ziyue.tjmetro.mod.Reference;
import ziyue.tjmetro.mod.render.RenderAPGDoorTianjinTRT.ModelAPGDoorBottom;
import ziyue.tjmetro.mod.render.RenderAPGDoorTianjinTRT.ModelSingleCube;
import ziyue.tjmetro.mod.render.RenderAPGDoorTianjin.ModelAPGDoorLight;

/**
 * @author ZiYueCommentary
 * @see ziyue.tjmetro.mod.block.BlockAPGDoorTianjinJinjing
 * @since 1.0.0-prerelease-1
 */

public class RenderAPGDoorTianjinJinjing<T extends BlockPSDAPGDoorBase.BlockEntityBase> extends BlockEntityRendererExtension<T> implements IGui, IBlock
{
    private static final ModelSingleCube MODEL_APG_TOP = new ModelSingleCube(34, 9, 0, 8, 1, 16, 8, 1);
    private static final ModelAPGDoorBottom MODEL_APG_BOTTOM = new ModelAPGDoorBottom();
    private static final ModelAPGDoorLight MODEL_APG_LIGHT = new ModelAPGDoorLight();
    private static final ModelSingleCube MODEL_APG_DOOR_LOCKED = new ModelSingleCube(6, 6, 5, 10, 1, 6, 6, 0);

    public RenderAPGDoorTianjinJinjing(BlockEntityRendererProvider.Context dispatcher) {
    }

    @Override
    public void render(T entity, PoseStack graphicsHolder, MultiBufferSource bufferSource, ClientLevel level, LocalPlayer player, float tickDelta, int light, int overlay) {
        final Level world = entity.getLevel();
        if (world == null) return;

        entity.tick(tickDelta);

        final BlockPos blockPos = entity.getBlockPos();
        final Direction facing = IBlock.getStatePropertySafe(world, blockPos, BlockStateProperties.HORIZONTAL_FACING);
        final boolean side = IBlock.getStatePropertySafe(world, blockPos, BlockPSDAPGDoorBase.SIDE) == EnumSide.RIGHT;
        final boolean half = IBlock.getStatePropertySafe(world, blockPos, BlockPSDAPGDoorBase.HALF) == DoubleBlockHalf.UPPER;
        final boolean unlocked = IBlock.getStatePropertySafe(world, blockPos, BlockPSDAPGDoorBase.UNLOCKED);
        final double open = Math.min(entity.getDoorValue(), 1F);

        final StoredMatrixTransformations storedMatrixTransformations = new StoredMatrixTransformations(0.5 + entity.getBlockPos().getX(), entity.getBlockPos().getY(), 0.5 + entity.getBlockPos().getZ());
        storedMatrixTransformations.add(graphicsHolderNew -> {
            graphicsHolderNew.mulPose(Axis.YP.rotationDegrees(-facing.toYRot()));
            graphicsHolderNew.mulPose(Axis.XP.rotationDegrees(180));
        });
        final StoredMatrixTransformations storedMatrixTransformationsLight = storedMatrixTransformations.copy();

        if (half) {
            final Block block = world.getBlockState(blockPos.relative(side ? facing.getClockWise() : facing.getCounterClockWise())).getBlock();
            if (block instanceof BlockAPGGlass || block instanceof BlockAPGGlassEnd) {
                MainRenderer.scheduleRender(ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, String.format("textures/block/apg_door_tianjin_jinjing_light_%s.png", open > 0 ? "on" : "off")), false, open > 0 ? QueuedRenderLayer.LIGHT_TRANSLUCENT : QueuedRenderLayer.EXTERIOR, (graphicsHolderNew, vertexConsumer, offset) -> {
                    storedMatrixTransformationsLight.transform(graphicsHolderNew, offset);
                    graphicsHolderNew.translate(side ? -0.515625 : 0.515625, 0, 0);
                    graphicsHolderNew.scale(0.5F, 1, 1);
                    MODEL_APG_LIGHT.render(graphicsHolderNew, vertexConsumer, light, overlay);
                    graphicsHolderNew.popPose();
                });
            }
        }

        storedMatrixTransformations.add(matricesNew -> matricesNew.translate(open * (side ? -1 : 1), 0, 0));

        MainRenderer.scheduleRender(ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, String.format("textures/block/apg_door_tianjin_%s_%s.png", half ? "top" : "bottom", side ? "right" : "left")), false, QueuedRenderLayer.EXTERIOR, (graphicsHolderNew, vertexConsumer, offset) -> {
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
}
