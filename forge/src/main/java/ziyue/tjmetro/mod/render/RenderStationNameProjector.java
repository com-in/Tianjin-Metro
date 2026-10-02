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
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import org.mtr.render.BlockEntityRendererExtension;
import com.mojang.blaze3d.vertex.PoseStack;
import org.mtr.MTRClient;
import org.mtr.block.BlockStationNameBase;
import org.mtr.block.IBlock;
import org.mtr.client.IDrawing;
import org.mtr.data.IGui;
import org.mtr.generated.lang.TranslationProvider;
import org.mtr.render.MainRenderer;
import org.mtr.render.QueuedRenderLayer;
import org.mtr.render.StoredMatrixTransformations;
import ziyue.tjmetro.mod.block.BlockStationNameProjector;
import ziyue.tjmetro.mod.client.DynamicTextureCache;

/**
 * @author ZiYueCommentary
 * @see BlockStationNameProjector
 * @since 1.0.0-beta-4
 */

public class RenderStationNameProjector<T extends BlockStationNameProjector.BlockEntity> extends BlockEntityRendererExtension<T> implements IGui, IDrawing
{
    public RenderStationNameProjector(BlockEntityRendererProvider.Context dispatcher) {
    }

    @Override
    public void render(T entity, PoseStack graphicsHolder, MultiBufferSource bufferSource, ClientLevel level, LocalPlayer player, float tickDelta, int light, int overlay) {
        final Level world = entity.getLevel();
        if (world == null) return;

        final BlockPos pos = entity.getBlockPos();
        final BlockState state = world.getBlockState(pos);
        final Direction facing = IBlock.getStatePropertySafe(state, BlockStateProperties.HORIZONTAL_FACING);
        final int scale = IBlock.getStatePropertySafe(state, BlockStationNameProjector.SCALE);

        final StoredMatrixTransformations storedMatrixTransformations = new StoredMatrixTransformations(0.5 + pos.getX(), 0.5 + pos.getY(), 0.5 + pos.getZ());
        storedMatrixTransformations.add(graphicsHolderNew -> {
            graphicsHolderNew.mulPose(Axis.YP.rotationDegrees(-facing.toYRot()));
            graphicsHolderNew.mulPose(Axis.ZP.rotationDegrees(180));
            graphicsHolderNew.translate(0, 0, 0.5 - SMALL_OFFSET);
            graphicsHolderNew.scale(scale, scale, scale);
        });

        final Station station = MTRClient.findStation(pos);
        MainRenderer.scheduleRender(DynamicTextureCache.instance.getStationNameProjector(station == null ? TranslationProvider.GUI_MTR_UNTITLED.getString() : station.getName(), 10).identifier, false, QueuedRenderLayer.EXTERIOR, (graphicsHolderNew, vertexConsumer, offset) -> {
            storedMatrixTransformations.transform(graphicsHolderNew, offset);
            IDrawing.drawTexture(graphicsHolderNew, vertexConsumer, -5F, -0.5F, 10, 1, 0, 0, 1, 1, facing, entity.color == -1 ? entity.getDefaultColor(pos) : ARGB_BLACK | entity.color, light);
            graphicsHolderNew.popPose();
        });
    }
}
