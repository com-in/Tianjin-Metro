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
import org.mtr.registry.Items;
import org.mtr.block.BlockRouteSignBase;
import org.mtr.block.IBlock;
import ziyue.tjmetro.mod.BlockEntityTypes;
import ziyue.tjmetro.mod.BlockList;
import ziyue.tjmetro.mod.ItemList;
import ziyue.tjmetro.mod.Registry;
import ziyue.tjmetro.mod.block.base.BlockRailwaySignBase;
import ziyue.tjmetro.mod.block.base.IRailwaySign;
import ziyue.tjmetro.mod.IGuiExtension;
import ziyue.tjmetro.mod.packet.PacketOpenBlockEntityScreen;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/**
 * @author ZiYueCommentary
 * @see BlockEntity
 * @since 1.0.0
 */

public class BlockRouteMapBMT extends BlockRailwaySignBase implements IBlockTooltip
{
    public BlockRouteMapBMT() {
        this(BlockBehaviour.Properties.of().noCollission().lightLevel(state -> 10));
    }

    public BlockRouteMapBMT(BlockBehaviour.Properties blockSettings) {
        super(blockSettings, 6, false);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        final Direction facing = ctx.getHorizontalDirection();
        if (!IBlock.isReplaceable(ctx, facing.getClockWise(), getMiddleLength() + 2)) {
            return null;
        }
        for (int i = 0; i < getMiddleLength() + 2; i++) {
            if (!ctx.getLevel().getBlockState(ctx.getClickedPos().below().relative(facing.getClockWise(), i)).canBeReplaced(ctx)) {
                return null;
            }
        }
        return defaultBlockState().setValue(BlockStateProperties.HORIZONTAL_FACING, facing);
    }

    @Nonnull
    @Override
    public InteractionResult useWithoutItem(BlockState state, Level world, BlockPos pos, Player player, BlockHitResult hit) {
        return IBlock.checkHoldingItem(world, player, item -> {
            final BlockPos blockPos = IBlock.getStatePropertySafe(world, pos, HALF) == DoubleBlockHalf.LOWER ? pos.above() : pos;
            final Direction facing = IBlock.getStatePropertySafe(state, BlockStateProperties.HORIZONTAL_FACING);
            // 沿 facing 方向回溯定位主块，任意一块/一面都能命中
            final BlockPos checkPos = findEndWithDirection(world, blockPos, facing, false);
            if (checkPos != null) {
                if (item == Items.BRUSH.get()) {
                    world.setBlock(checkPos, world.getBlockState(checkPos).cycle(SIDE), 3);
                } else {
                    Registry.sendPacketToClient(((ServerPlayer) player), new PacketOpenBlockEntityScreen(checkPos));
                }
            }
        }, null, Items.BRUSH.get(), ItemList.WRENCH.get());
    }

    @Nonnull
    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor world, BlockPos pos, BlockPos neighborPos) {
        return IRailwaySign.getStateForNeighborUpdate(state, direction, neighborState, BlockList.ROUTE_MAP_BMT_MIDDLE.get());
    }

    @Override
    public void setPlacedBy(Level world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        if (world.isClientSide()) return;

        final Direction facing = IBlock.getStatePropertySafe(state, BlockStateProperties.HORIZONTAL_FACING);
        world.setBlock(pos.below(), state.getBlock().defaultBlockState().setValue(BlockStateProperties.HORIZONTAL_FACING, facing).setValue(HALF, DoubleBlockHalf.LOWER), 3);
        final BlockState middleState = BlockList.ROUTE_MAP_BMT_MIDDLE.get().defaultBlockState().setValue(BlockStateProperties.HORIZONTAL_FACING, facing);
        for (int i = 1; i <= getMiddleLength(); i++) {
            final BlockPos blockPos = pos.relative(facing.getClockWise(), i);
            world.setBlock(blockPos, middleState.setValue(HALF, DoubleBlockHalf.UPPER), 3);
            world.setBlock(blockPos.below(), middleState.setValue(HALF, DoubleBlockHalf.LOWER), 3);
        }
        final BlockPos blockPos = pos.relative(facing.getClockWise(), getMiddleLength() + 1);
        world.setBlock(blockPos, state.getBlock().defaultBlockState().setValue(BlockStateProperties.HORIZONTAL_FACING, facing.getOpposite()).setValue(HALF, DoubleBlockHalf.UPPER), 3);
        world.setBlock(blockPos.below(), state.getBlock().defaultBlockState().setValue(BlockStateProperties.HORIZONTAL_FACING, facing.getOpposite()).setValue(HALF, DoubleBlockHalf.LOWER), 3);
        world.updateNeighborsAt(pos, net.minecraft.world.level.block.Blocks.AIR);
        world.updateNeighborsAt(pos, state.getBlock());
    }

    @Override
    public BlockState playerWillDestroy(Level world, BlockPos pos, BlockState state, Player player) {
        final Direction facing = IBlock.getStatePropertySafe(state, BlockStateProperties.HORIZONTAL_FACING);
        final BlockPos blockPos = IBlock.getStatePropertySafe(world, pos, HALF) == DoubleBlockHalf.LOWER ? pos.above() : pos;

        final BlockPos checkPos = findEndWithDirection(world, blockPos, facing, true);
        if (checkPos != null) {
            IBlockExtension.breakBlock(world, checkPos);
            IBlockExtension.breakBlock(world, checkPos.below());
        }

        IBlockExtension.breakBlock(world, pos.below());
        return super.playerWillDestroy(world, pos, state, player);
    }

    @Nonnull
    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        final Direction facing = IBlock.getStatePropertySafe(state, BlockStateProperties.HORIZONTAL_FACING);
        final DoubleBlockHalf half = IBlock.getStatePropertySafe(state, HALF);
        if (this == BlockList.ROUTE_MAP_BMT_MIDDLE.get()) {
            if (half == DoubleBlockHalf.UPPER) {
                return IBlock.getVoxelShapeByDirection(0, 0, 7, 16, 12, 9, facing);
            } else {
                return IBlock.getVoxelShapeByDirection(0, 0, 7, 16, 16, 9, facing);
            }
        } else {
            if (half == DoubleBlockHalf.UPPER) {
                final VoxelShape pole = IBlock.getVoxelShapeByDirection(3, 0, 7, 4.25, 16, 9, facing);
                final VoxelShape plate = IBlock.getVoxelShapeByDirection(4.25, 0, 7, 16, 12, 9, facing);
                return Shapes.or(pole, plate);
            } else {
                return IBlock.getVoxelShapeByDirection(3, 0, 7, 16, 16, 9, facing);
            }
        }
    }

    public void addTooltips(ItemStack stack, @Nullable BlockGetter world, List<MutableComponent> tooltip, Item.TooltipContext options) {
        IGuiExtension.addHoldShiftTooltip(tooltip, Component.translatable("tooltip.tjmetro.station_name_plate"));
    }

    @Override
    protected int getMiddleLength() {
        return 1;
    }

    @Nonnull
    @Override
    public String getDescriptionId() {
        return "block.tjmetro.route_map_bmt";
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(SIDE);
        builder.add(HALF);
    }

    @Override
    protected BlockPos findEndWithDirection(Level world, BlockPos startPos, Direction direction, boolean allowOpposite) {
        return IRailwaySign.findEndWithDirection(world, startPos, direction, allowOpposite, BlockList.ROUTE_MAP_BMT_MIDDLE.get());
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        if (this == BlockList.ROUTE_MAP_BMT_MIDDLE.get() || IBlock.getStatePropertySafe(blockState, HALF) == DoubleBlockHalf.LOWER)
            return null;
        else return new BlockEntity(blockPos, blockState);
    }

    /**
     * @author ZiYueCommentary
     * @see ziyue.tjmetro.mod.render.RenderRouteMapBMT
     * @see ziyue.tjmetro.mod.screen.RailwaySignScreen
     * @since 1.0.0
     */
    public static class BlockEntity extends BlockRouteSignBase.BlockEntityBase
    {
        public BlockEntity(BlockPos pos, BlockState state) {
            super(BlockEntityTypes.ROUTE_MAP_BMT.get(), pos, state);
        }
    }
}
