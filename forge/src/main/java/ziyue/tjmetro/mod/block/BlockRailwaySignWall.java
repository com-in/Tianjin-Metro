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
 * @see BlockRailwaySignBase
 * @see BlockEntity
 * @since 1.0.0-beta-1
 */

public class BlockRailwaySignWall extends BlockRailwaySignBase implements IRailwaySign, IBlockTooltip
{
    public static final BooleanProperty EOS = BooleanProperty.create("eos"); // end of sign
    // When Minecraft meets a new blockstate, the game will assign it with `true`.
    // For backward compatibility, we can't use `is_on_ground`, but `is_not_on_ground`,
    // to make sure the old signs are work properly.
    public static final BooleanProperty NOT_GROUND = BooleanProperty.create("not_ground");

    public BlockRailwaySignWall(int length) {
        super(net.minecraft.world.level.block.state.BlockBehaviour.Properties.of().noCollission().lightLevel(blockState -> 15).noCollission(), length, false);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        final Direction facing = ctx.getHorizontalDirection();
        return IBlock.isReplaceable(ctx, facing.getClockWise(), getMiddleLength() + 1) ?
                defaultBlockState().setValue(BlockStateProperties.HORIZONTAL_FACING, facing)
                        .setValue(EOS, false)
                        .setValue(NOT_GROUND, ctx.getNearestLookingDirection() != Direction.DOWN) :
                null;
    }

    @Nonnull
    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor world, BlockPos pos, BlockPos neighborPos) {
        final Direction facing = IBlock.getStatePropertySafe(state, BlockStateProperties.HORIZONTAL_FACING);
        final boolean isNext = ((!IBlock.getStatePropertySafe(state, EOS) && (direction == facing.getClockWise())) || IBlockExtension.isBlock(state, BlockList.RAILWAY_SIGN_WALL_MIDDLE.get()) && (direction == facing.getCounterClockWise()));
        if (isNext && !(neighborState.getBlock() instanceof BlockRailwaySignWall)) {
            return Blocks.AIR.defaultBlockState();
        } else {
            return state;
        }
    }

    @Nonnull
    @Override
    public InteractionResult useWithoutItem(BlockState state, Level world, BlockPos pos, Player player, BlockHitResult hit) {
        final Direction facing = IBlock.getStatePropertySafe(state, BlockStateProperties.HORIZONTAL_FACING);
        final BlockPos checkPos = findEndWithDirection(world, pos, facing, false);
        if (player.isHolding(ItemList.WRENCH.get())) {
            if (checkPos != null && world.getBlockEntity(checkPos) instanceof BlockEntityBase entity) {
                entity.setToggleStyle();
                return InteractionResult.SUCCESS;
            }
        }
        return IBlock.checkHoldingBrush(world, player, () -> {
            if (checkPos != null) {
                Registry.sendPacketToClient(((ServerPlayer) player), new PacketOpenBlockEntityScreen(checkPos));
            }
        });
    }

    @Override
    public void setPlacedBy(Level world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        if (world.isClientSide()) return;

        final Direction facing = IBlock.getStatePropertySafe(state, BlockStateProperties.HORIZONTAL_FACING);
        final boolean notGround = IBlock.getStatePropertySafe(state, NOT_GROUND);
        for (int i = 1; i < getMiddleLength(); i++) {
            world.setBlock(pos.relative(facing.getClockWise(), i), BlockList.RAILWAY_SIGN_WALL_MIDDLE.get().defaultBlockState()
                            .setValue(BlockStateProperties.HORIZONTAL_FACING, facing)
                            .setValue(EOS, false)
                            .setValue(NOT_GROUND, notGround), 3);
        }
        world.setBlock(pos.relative(facing.getClockWise(), getMiddleLength()), BlockList.RAILWAY_SIGN_WALL_MIDDLE.get().defaultBlockState()
                        .setValue(BlockStateProperties.HORIZONTAL_FACING, facing)
                        .setValue(EOS, true)
                        .setValue(NOT_GROUND, notGround), 3);
        world.updateNeighborsAt(pos, Blocks.AIR);
        world.updateNeighborsAt(pos, state.getBlock());
    }

    public void addTooltips(ItemStack stack, @Nullable BlockGetter world, List<MutableComponent> tooltip, Item.TooltipContext options) {
        tooltip.add(Component.translatable("tooltip.mtr.railway_sign_length", length).withStyle(ChatFormatting.GRAY));
        IGuiExtension.addHoldShiftTooltip(tooltip, Component.translatable("tooltip.tjmetro.railway_sign_wall"));
    }

    @Nonnull
    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        if (!IBlock.getStatePropertySafe(state, NOT_GROUND)) return Block.box(0, 0, 0, 16, 1, 16);
        return IBlock.getVoxelShapeByDirection(0, 0, 0, 16, 16, 1, IBlock.getStatePropertySafe(state, BlockStateProperties.HORIZONTAL_FACING));
    }

    @Nonnull
    @Override
    public String getDescriptionId() {
        return "block.tjmetro.railway_sign_wall";
    }

    @Override
    protected BlockPos findEndWithDirection(Level world, BlockPos startPos, Direction direction, boolean allowOpposite) {
        return IRailwaySign.findEndWithDirection(world, startPos, direction, allowOpposite, BlockList.RAILWAY_SIGN_WALL_MIDDLE.get());
    }

    @Override
    public int getXStart() {
        return 0;
    }

    @Override
    protected int getMiddleLength() {
        return length / 2 - 1;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(EOS);
        builder.add(NOT_GROUND);
    }

    @Override
    public net.minecraft.world.level.block.entity.BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        if (this == BlockList.RAILWAY_SIGN_WALL_MIDDLE.get())
            return null;
        else
            return new BlockEntity(length, blockPos, blockState);
    }

    /**
     * @author ZiYueCommentary
     * @see ziyue.tjmetro.mod.render.RenderRailwaySignWall
     * @since 1.0.0-beta-1
     */
    public static class BlockEntity extends BlockEntityBase
    {
        public BlockEntity(int length, BlockPos pos, BlockState state) {
            super(getType(length), length, pos, state);
        }

        public static BlockEntityType<?> getType(int length) {
            return switch (length) {
                case 4 -> BlockEntityTypes.RAILWAY_SIGN_WALL_4.get();
                case 6 -> BlockEntityTypes.RAILWAY_SIGN_WALL_6.get();
                case 8 -> BlockEntityTypes.RAILWAY_SIGN_WALL_8.get();
                case 10 -> BlockEntityTypes.RAILWAY_SIGN_WALL_10.get();
                default -> null;
            };
        }
    }
}
