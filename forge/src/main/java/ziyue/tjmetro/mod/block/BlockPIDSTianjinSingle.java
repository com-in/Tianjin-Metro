package ziyue.tjmetro.mod.block;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.Blocks;
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
import org.mtr.block.BlockEntityExtension;
import org.mtr.block.IBlock;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/**
 * @author ZiYueCommentary
 * @see BlockEntity
 * @since 1.1.0
 */

public class BlockPIDSTianjinSingle extends BlockPIDSTianjin
{
    public static final BooleanProperty SHOULD_RENDER = BooleanProperty.create("should_render"); // it just works...

    @Override
    public @Nonnull BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor world, BlockPos pos, BlockPos neighborPos) {
        return state;
    }

    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        final Direction direction = ctx.getHorizontalDirection();
        return IBlock.isReplaceable(ctx, direction.getClockWise(), 2) ? this.defaultBlockState().setValue(BlockStateProperties.HORIZONTAL_FACING, direction).setValue(SHOULD_RENDER, true) : null;
    }

    @Override
    public boolean canStoreData(Level world, BlockPos blockPos) {
        return IBlock.getStatePropertySafe(world, blockPos, SHOULD_RENDER);
    }

    public void setPlacedBy(Level world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        if (!world.isClientSide()) {
            Direction direction = IBlock.getStatePropertySafe(state, BlockStateProperties.HORIZONTAL_FACING);
            world.setBlock(pos.relative(direction.getClockWise()), this.defaultBlockState().setValue(BlockStateProperties.HORIZONTAL_FACING, direction).setValue(SHOULD_RENDER, false), 3);
            world.updateNeighborsAt(pos, Blocks.AIR);
            world.updateNeighborsAt(pos, state.getBlock());
        }
    }

    @Override
    public BlockPos getBlockPosWithData(Level world, BlockPos blockPos) {
        if (canStoreData(world, blockPos)) {
            return blockPos;
        } else {
            final Direction direction = IBlock.getStatePropertySafe(world, blockPos, BlockStateProperties.HORIZONTAL_FACING);
            return blockPos.relative(IBlock.getStatePropertySafe(world, blockPos, SHOULD_RENDER) ? direction.getClockWise() : direction.getCounterClockWise());
        }
    }

    @Override
    public BlockState playerWillDestroy(Level world, BlockPos pos, BlockState state, Player player) {
        final Direction direction = IBlock.getStatePropertySafe(state, BlockStateProperties.HORIZONTAL_FACING);
        IBlockExtension.breakBlock(world, pos.relative(IBlock.getStatePropertySafe(state, SHOULD_RENDER) ? direction.getClockWise() : direction.getCounterClockWise()), this);
        world.destroyBlock(pos, false, ((Entity) player));
        return super.playerWillDestroy(world, pos, state, player);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(SHOULD_RENDER);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new BlockEntity(blockPos, blockState, true);
    }
}
