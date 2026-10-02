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
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import org.mtr.block.BlockEntityExtension;
import org.mtr.block.IBlock;
import ziyue.tjmetro.mod.BlockEntityTypes;
import ziyue.tjmetro.mod.BlockList;
import ziyue.tjmetro.mod.block.base.BlockCustomColorBase;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/**
 * @author ZiYueCommentary
 * @see BlockEntity
 * @see BlockCustomColorBase
 * @since 1.0.0-beta-2
 */

public class BlockMetalPoleBMT extends BlockCustomColorBase 
{
    public static final BooleanProperty NORTH = BooleanProperty.create("north");
    public static final BooleanProperty EAST = BooleanProperty.create("east");
    public static final BooleanProperty SOUTH = BooleanProperty.create("south");
    public static final BooleanProperty WEST = BooleanProperty.create("west");
    public static final DirectionProperty FACING_NORMAL = DirectionProperty.create("facing_normal");

    public BlockMetalPoleBMT() {
        this(BlockBehaviour.Properties.of().noOcclusion());
    }

    public BlockMetalPoleBMT(BlockBehaviour.Properties blockSettings) {
        super(blockSettings);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        final Direction direction = ctx.getClickedFace();
        return defaultBlockState().setValue(NORTH, false).setValue(EAST, false).setValue(SOUTH, false).setValue(WEST, false)
                .setValue(FACING_NORMAL, direction == Direction.UP ? Direction.DOWN : direction);
    }

    @Nonnull
    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor world, BlockPos pos, BlockPos neighborPos) {
        if (IBlock.getStatePropertySafe(state, FACING_NORMAL) != Direction.DOWN) return state;

        boolean shouldConnect = false;
        if (IBlockExtension.isBlock(neighborState, BlockList.METAL_POLE_BMT.get())) {
            if (IBlock.getStatePropertySafe(neighborState, FACING_NORMAL) != Direction.DOWN) {
                shouldConnect = true;
            }
        }
        return switch (direction) {
            case NORTH -> state.setValue(NORTH, shouldConnect);
            case EAST -> state.setValue(EAST, shouldConnect);
            case SOUTH -> state.setValue(SOUTH, shouldConnect);
            case WEST -> state.setValue(WEST, shouldConnect);
            default -> state;
        };
    }

    @Nonnull
    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        final Direction direction = IBlock.getStatePropertySafe(state, FACING_NORMAL);
        if (direction == Direction.DOWN) {
            final VoxelShape pole = Block.box(6.0, 0.0, 6.0, 10.0, 16.0, 10.0);
            VoxelShape connect = Block.box(0, 0, 0, 0, 0, 0);
            if (IBlock.getStatePropertySafe(state, NORTH)) {
                connect = Shapes.or(connect, IBlock.getVoxelShapeByDirection(6, 0, 0, 10, 4, 6, Direction.NORTH));
            }
            if (IBlock.getStatePropertySafe(state, EAST)) {
                connect = Shapes.or(connect, IBlock.getVoxelShapeByDirection(6, 0, 0, 10, 4, 6, Direction.EAST));
            }
            if (IBlock.getStatePropertySafe(state, SOUTH)) {
                connect = Shapes.or(connect, IBlock.getVoxelShapeByDirection(6, 0, 0, 10, 4, 6, Direction.SOUTH));
            }
            if (IBlock.getStatePropertySafe(state, WEST)) {
                connect = Shapes.or(connect, IBlock.getVoxelShapeByDirection(6, 0, 0, 10, 4, 6, Direction.WEST));
            }
            return Shapes.or(pole, connect);
        }
        return IBlock.getVoxelShapeByDirection(6, 0, 0, 10, 4, 16, direction);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new BlockEntity(blockPos, blockState);
    }

    @Nonnull
    @Override
    public String getDescriptionId() {
        return "block.tjmetro.metal_pole_bmt";
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(FACING_NORMAL);
        builder.add(NORTH);
        builder.add(EAST);
        builder.add(SOUTH);
        builder.add(WEST);
    }

    /**
     * @author ZiYueCommentary
     * @see ziyue.tjmetro.mod.screen.ColorPickerScreen
     * @since 1.0.0-beta-2
     */
    public static class BlockEntity extends BlockEntityBase
    {
        public BlockEntity(BlockPos blockPos, BlockState blockState) {
            super(BlockEntityTypes.METAL_POLE_BMT.get(), blockPos, blockState);
        }

        @Override
        public int getDefaultColor(BlockPos pos) {
            return 0xfff100;
        }
    }
}
