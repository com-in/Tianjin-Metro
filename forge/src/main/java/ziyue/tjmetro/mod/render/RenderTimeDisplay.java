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
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import org.mtr.render.BlockEntityRendererExtension;
import com.mojang.blaze3d.vertex.PoseStack;
import org.mtr.block.IBlock;
import org.mtr.client.IDrawing;
import org.mtr.data.IGui;
import org.mtr.render.MainRenderer;
import org.mtr.render.QueuedRenderLayer;
import org.mtr.render.StoredMatrixTransformations;
import ziyue.tjmetro.mod.block.BlockTimeDisplay;
import ziyue.tjmetro.mod.block.BlockTimeDisplayEven;
import ziyue.tjmetro.mod.client.IDrawingExtension;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.HORIZONTAL_FACING;

/**
 * @author ZiYueCommentary
 * @see BlockTimeDisplay
 * @see BlockTimeDisplayEven
 * @since 1.0.0-beta-1
 */

public class RenderTimeDisplay<T extends BlockTimeDisplay.BlockEntity> extends BlockEntityRendererExtension<T> implements IGui, IDrawing
{
    public RenderTimeDisplay(BlockEntityRendererProvider.Context argument) {
    }

    @Override
    public void render(T entity, PoseStack graphicsHolder, MultiBufferSource bufferSource, ClientLevel level, LocalPlayer player, float tickDelta, int light, int overlay) {
        final Level world = entity.getLevel();
        if (world == null) return;

        final BlockPos pos = entity.getBlockPos();
        final BlockState state = world.getBlockState(pos);
        final Direction facing = IBlock.getStatePropertySafe(state, BlockStateProperties.HORIZONTAL_FACING);

        final StoredMatrixTransformations storedMatrixTransformations = new StoredMatrixTransformations(0.5 + pos.getX(), 0.5 + entity.yOffset + pos.getY(), 0.5 + pos.getZ());
        storedMatrixTransformations.add(graphicsHolderNew -> {
            graphicsHolderNew.mulPose(Axis.YP.rotationDegrees(-facing.toYRot()));
            graphicsHolderNew.mulPose(Axis.ZP.rotationDegrees(180));
        });

        if (entity instanceof BlockTimeDisplayEven.BlockEntity) {
            final StoredMatrixTransformations storedMatrixTransformations2 = storedMatrixTransformations.copy();
            storedMatrixTransformations2.add(graphicsHolderNew -> graphicsHolderNew.translate(0.516, -0.04, 0.5 - entity.zOffset - SMALL_OFFSET));
            MainRenderer.scheduleRender(QueuedRenderLayer.LIGHT_TRANSLUCENT, (newGraphicsHolder, vertexConsumer, offset) -> {
                storedMatrixTransformations2.transform(newGraphicsHolder, offset);
                IDrawingExtension.drawStringWithFont(newGraphicsHolder, bufferSource, getFormattedTime(entity.getLevel().getDayTime()), HorizontalAlignment.CENTER, VerticalAlignment.CENTER, HorizontalAlignment.CENTER, 0, 0.03F, -1, -1, 30, -3276781, -3276781, 2, false, light, true, null);
                newGraphicsHolder.popPose();
            });
        } else {
            for (int i = 0; i < 2; i++) {
                final StoredMatrixTransformations storedMatrixTransformations2 = storedMatrixTransformations.copy();
                final boolean shouldFlip = i == 1;
                storedMatrixTransformations2.add(graphicsHolderNew -> {
                    if (shouldFlip) {
                        graphicsHolderNew.mulPose(Axis.YP.rotationDegrees(180));
                    }
                    graphicsHolderNew.translate(0, -0.04, 0.5 - entity.zOffset - SMALL_OFFSET);
                });
                MainRenderer.scheduleRender(QueuedRenderLayer.LIGHT_TRANSLUCENT, (newGraphicsHolder, vertexConsumer, offset) -> {
                    storedMatrixTransformations2.transform(newGraphicsHolder, offset);
                    IDrawingExtension.drawStringWithFont(newGraphicsHolder, bufferSource, getFormattedTime(entity.getLevel().getDayTime()), HorizontalAlignment.CENTER, VerticalAlignment.CENTER, HorizontalAlignment.CENTER, 0, 0.03F, -1, -1, 30, -3276781, -3276781, 2, false, light, true, null);
                    newGraphicsHolder.popPose();
                });
            }
        }
    }

    public static String getFormattedTime(long ticks) {
        int hours = (int) ((Math.floor(ticks / 1000.0) + 6) % 24);
        int minutes = (int) Math.floor((ticks % 1000) / 1000.0 * 60);
        return String.format("%02d:%02d", hours, minutes);
    }
}
