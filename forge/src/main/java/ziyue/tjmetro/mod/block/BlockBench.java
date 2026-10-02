package ziyue.tjmetro.mod.block;

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
import net.minecraft.world.level.block.Block;
import org.mtr.block.IBlock;
import ziyue.tjmetro.mod.BlockList;
import ziyue.tjmetro.mod.entity.EntitySeat;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/**
 * Bench is a wooden chair on which players can sit down.
 * Note that the bench in Minecraft 1.16.5 does not support sitting down.
 *
 * @author ZiYueCommentary
 * @see EntitySeat
 * @see <a href="https://github.com/ZiYueCommentary/Tianjin-Metro/wiki/Bench">Wiki page</a>
 * @since 1.0.0-beta-1
 */

public class BlockBench extends Block implements IBlock
{
    public BlockBench() {
        this(BlockBehaviour.Properties.of());
    }

    public BlockBench(BlockBehaviour.Properties blockSettings) {
        super(blockSettings);
    }

    // Not working in 1.16.5 due to fabric's bug.
#if MC_VERSION > "11605"
    @Nonnull
    @Override
    public InteractionResult useWithoutItem(BlockState state, Level world, BlockPos pos, Player player, BlockHitResult hit) {
        Entity entity = new EntitySeat(world, pos.getX() + 0.5, pos.getY() + 0.55, pos.getZ() + 0.5);
        world.addFreshEntity(entity);
        player.startRiding(entity);
        return InteractionResult.SUCCESS;
    }
#endif

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        final Direction direction = ctx.getHorizontalDirection();
        final BlockPos pos = ctx.getClickedPos();
        return defaultBlockState().setValue(BlockStateProperties.HORIZONTAL_FACING, direction).setValue(SIDE_EXTENDED, getPos(direction, pos, ctx.getLevel()));
    }

    @Override
    public void neighborChanged(BlockState state, Level world, BlockPos pos, Block block, BlockPos fromPos, boolean notify) {
        world.setBlock(pos, state.setValue(SIDE_EXTENDED, getPos(IBlock.getStatePropertySafe(state, BlockStateProperties.HORIZONTAL_FACING), pos, world)), 3);
    }

    @Nonnull
    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        final Direction direction = IBlock.getStatePropertySafe(state, BlockStateProperties.HORIZONTAL_FACING);
        final VoxelShape top = IBlock.getVoxelShapeByDirection(0.0, 8.0, 1.0, 16.0, 9.5, 15.0, direction);
        final VoxelShape left = IBlock.getVoxelShapeByDirection(12.0, 0.0, 2.0, 14.0, 8.0, 14.0, direction);
        final VoxelShape right = IBlock.getVoxelShapeByDirection(2.0, 0.0, 2.0, 4.0, 8.0, 14.0, direction);
        switch (IBlock.getStatePropertySafe(state, SIDE_EXTENDED)) {
            case SINGLE:
                return Shapes.or(Shapes.or(top, left), right);
            case LEFT:
                return Shapes.or(top, left);
            case RIGHT:
                return Shapes.or(top, right);
            default:
                return top;
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(BlockStateProperties.HORIZONTAL_FACING);
        builder.add(SIDE_EXTENDED);
    }

    public IBlock.EnumSide getPos(Direction direction, BlockPos blockPos, Level world) {
        final BlockState counterClockWiseState = world.getBlockState(blockPos.relative(direction.getCounterClockWise()));
        final BlockState clockWiseState = world.getBlockState(blockPos.relative(direction.getClockWise()));
        final boolean[] blockSame = {
                (IBlockExtension.isBlock(counterClockWiseState, BlockList.BENCH.get())) && (IBlock.getStatePropertySafe(counterClockWiseState, BlockStateProperties.HORIZONTAL_FACING) == direction),
                (IBlockExtension.isBlock(clockWiseState, BlockList.BENCH.get())) && (IBlock.getStatePropertySafe(clockWiseState, BlockStateProperties.HORIZONTAL_FACING) == direction)
        };
        return blockSame[0] && blockSame[1] ? IBlock.EnumSide.MIDDLE : blockSame[0] ? IBlock.EnumSide.LEFT : blockSame[1] ? IBlock.EnumSide.RIGHT : IBlock.EnumSide.SINGLE;
    }
}
