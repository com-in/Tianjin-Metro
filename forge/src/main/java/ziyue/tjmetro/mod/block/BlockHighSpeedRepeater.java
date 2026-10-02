package ziyue.tjmetro.mod.block;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.ChatFormatting;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
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
import ziyue.tjmetro.mapping.DustParticleOptions;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * @author ZiYueCommentary
 * @since 1.0.0-beta-1
 */

public class BlockHighSpeedRepeater extends Block 
{
    public static final BooleanProperty LOCKED = BooleanProperty.create("locked");
    public static final BooleanProperty POWERED = BooleanProperty.create("powered");
    public static final IntegerProperty POWER = IntegerProperty.create("power", 0, 15);

    public BlockHighSpeedRepeater() {
        this(net.minecraft.world.level.block.state.BlockBehaviour.Properties.of());
    }

    public BlockHighSpeedRepeater(BlockBehaviour.Properties blockSettings) {
        super(blockSettings);
    }

    @Nonnull
    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return Block.box(0.0, 0.0, 0.0, 16.0, 2.0, 16.0);
    }

    @Override
    public int getDirectSignal(BlockState state, BlockGetter world, BlockPos pos, Direction direction) {
        return direction == IBlock.getStatePropertySafe(state, BlockStateProperties.HORIZONTAL_FACING) && IBlock.getStatePropertySafe(state, POWERED) ? 15 : 0;
    }

    @Override
    public int getSignal(BlockState state, BlockGetter world, BlockPos pos, Direction direction) {
        return getDirectSignal(state, world, pos, direction);
    }

    @Override
    public void neighborChanged(BlockState state, Level world, BlockPos pos, Block block, BlockPos fromPos, boolean notify) {
        if (IBlock.getStatePropertySafe(state, LOCKED)) {
            world.setBlock(pos, state.setValue(LOCKED, this.isLocked(world, pos, state)), 3);
            return;
        }
        final Direction direction = IBlock.getStatePropertySafe(state, BlockStateProperties.HORIZONTAL_FACING);
        final BlockPos blockPos1 = pos.relative(direction);
        final boolean powered = world.getBlockState(blockPos1).getDirectSignal(world, blockPos1, direction) > 0;
        final boolean isLocked = this.isLocked(world, pos, state);
        world.setBlock(pos, state.setValue(POWERED, powered).setValue(LOCKED, isLocked), 3);
    }

    protected int getInputLevel(Level world, BlockPos pos, Direction dir) {
        BlockState blockState = world.getBlockState(pos);
        if (IBlockExtension.isBlock(blockState, Blocks.REDSTONE_BLOCK)) return 15;
        if (IBlockExtension.isBlock(blockState, Blocks.REDSTONE_WIRE)) {
            return IBlock.getStatePropertySafe(blockState, POWER);
        }
        return world.getBlockState(pos).getDirectSignal(world, pos, dir);
    }

    @Override
    public boolean isSignalSource(BlockState state) {
        return true;
    }

    @Override
    public void onPlace(BlockState state, Level world, BlockPos pos, BlockState oldState, boolean notify) {
        this.updateTarget(world, pos, state);
    }

    @Override
    public void onRemove(BlockState state, Level world, BlockPos pos, BlockState newState, boolean moved) {
        if (moved || state.is(newState.getBlock())) return;
        super.onRemove(state, world, pos, newState, moved);
        this.updateTarget(world, pos, state);
    }

    protected void updateTarget(Level world, BlockPos pos, BlockState state) {
        Direction direction = IBlock.getStatePropertySafe(state, BlockStateProperties.HORIZONTAL_FACING);
        BlockPos blockPos = pos.relative(direction.getOpposite());
        world.neighborChanged(blockPos, this, pos);
        world.updateNeighborsAtExceptFromFacing(blockPos, this, direction);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        final Direction direction = ctx.getHorizontalDirection().getOpposite();
        final BlockPos blockPos = ctx.getClickedPos().relative(direction);
        final boolean powered = ctx.getLevel().getBlockState(blockPos).getDirectSignal(ctx.getLevel(), blockPos, direction) > 0;
        final BlockState state = defaultBlockState().setValue(BlockStateProperties.HORIZONTAL_FACING, direction).setValue(POWERED, powered);
        return state.setValue(LOCKED, this.isLocked(ctx.getLevel(), ctx.getClickedPos(), state));
    }

    public boolean isLocked(Level world, BlockPos pos, BlockState state) {
        Direction direction = IBlock.getStatePropertySafe(state, BlockStateProperties.HORIZONTAL_FACING);
        Direction direction2 = direction.getClockWise();
        Direction direction3 = direction.getCounterClockWise();
        return Math.max(this.getInputLevel(world, pos.relative(direction2), direction2), this.getInputLevel(world, pos.relative(direction3), direction3)) > 0;
    }

    @Override
    public void animateTick(BlockState state, Level world, BlockPos pos, RandomSource random) {
        if (!IBlock.getStatePropertySafe(state, POWERED)) return;
        final Direction direction = IBlock.getStatePropertySafe(state, BlockStateProperties.HORIZONTAL_FACING);
        final double d = (double) pos.getX() + 0.5 + (random.nextDouble() - 0.5) * 0.2;
        final double e = (double) pos.getY() + 0.4 + (random.nextDouble() - 0.5) * 0.2;
        final double f = (double) pos.getZ() + 0.5 + (random.nextDouble() - 0.5) * 0.2;
        float g = random.nextBoolean() ? 7 : -5.0f;
        final double h = (g /= 16.0f) * (float) direction.getStepX();
        final double i = g * (float) direction.getStepZ();
        world.addParticle(DustParticleOptions.BLUE, d + h, e, f + i, 0.0, 0.0, 0.0);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(BlockStateProperties.HORIZONTAL_FACING);
        builder.add(POWERED);
        builder.add(LOCKED);
    }
}
