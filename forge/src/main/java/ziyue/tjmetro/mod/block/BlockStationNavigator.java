package ziyue.tjmetro.mod.block;

import net.minecraft.nbt.CompoundTag;
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
import org.mtr.libraries.it.unimi.dsi.fastutil.longs.LongAVLTreeSet;
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
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A railway-sign-like block.
 *
 * @author ZiYueCommentary
 * @see BlockEntity
 * @see BlockRailwaySignBase
 * @since 1.0.0-beta-4
 */

public class BlockStationNavigator extends BlockRailwaySignBase implements IBlockTooltip
{
    public static final BooleanProperty ARROW_LEFT = BooleanProperty.create("arrow_left");

    public final int length;

    public BlockStationNavigator(int length) {
        this(length, BlockBehaviour.Properties.of().noCollission().lightLevel(state -> 15));
    }

    public BlockStationNavigator(int length, BlockBehaviour.Properties blockSettings) {
        super(blockSettings, length, true);
        this.length = length;
    }

    @Nonnull
    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor world, BlockPos pos, BlockPos neighborPos) {
        return IRailwaySign.getStateForNeighborUpdate(state, direction, neighborState, BlockList.STATION_NAVIGATOR_MIDDLE.get());
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        // This block should take three blocks, no matter how longs it is.
        // Navigator, which takes more blocks, is too long.
        final Direction facing = ctx.getHorizontalDirection();
        if (IBlock.isReplaceable(ctx, facing.getClockWise(), 2) && IBlock.isReplaceable(ctx, facing.getCounterClockWise(), 2)) {
            final Level world = ctx.getLevel();
            final BlockPos pos = ctx.getClickedPos();
            world.setBlock(pos.relative(facing.getCounterClockWise()), defaultBlockState().setValue(BlockStateProperties.HORIZONTAL_FACING, facing), 3);
            world.setBlock(pos.relative(facing.getClockWise()), defaultBlockState().setValue(BlockStateProperties.HORIZONTAL_FACING, facing.getOpposite()), 3);
            world.updateNeighborsAt(pos, net.minecraft.world.level.block.Blocks.AIR);
            world.updateNeighborsAt(pos, this);
            return BlockList.STATION_NAVIGATOR_MIDDLE.get().defaultBlockState().setValue(BlockStateProperties.HORIZONTAL_FACING, facing);
        }
        return null;
    }

    @Override
    public InteractionResult useWithoutItem(BlockState state, Level world, BlockPos pos, Player player, BlockHitResult hit) {
        return IBlock.checkHoldingItem(world, player, item -> {
            final BlockPos checkPos = findEndWithDirection(world, pos, hit.getDirection().getOpposite(), false);
            if (checkPos != null) {
                if (item == ItemList.WRENCH.get()) {
                    Registry.sendPacketToClient(((ServerPlayer) player), new PacketOpenBlockEntityScreen(checkPos));
                } else {
                    world.setBlock(checkPos, world.getBlockState(checkPos).cycle(ARROW_LEFT), 3);
                }
            }
        }, null, ItemList.WRENCH.get(), Items.BRUSH.get());
    }

    @Override
    public void setPlacedBy(Level world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {

    }

    @Override
    protected int getMiddleLength() {
        return 1;
    }

    public void addTooltips(ItemStack stack, @Nullable BlockGetter world, List<MutableComponent> tooltip, Item.TooltipContext options) {
        tooltip.add(Component.translatable("tooltip.mtr.railway_sign_length", length).withStyle(ChatFormatting.GRAY));
        IGuiExtension.addHoldShiftTooltip(tooltip, Component.translatable("tooltip.tjmetro.station_navigator"));
    }

    @Nonnull
    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        final Direction facing = IBlock.getStatePropertySafe(state, BlockStateProperties.HORIZONTAL_FACING);
        if (IBlockExtension.isBlock(state, BlockList.STATION_NAVIGATOR_MIDDLE.get())) {
            return IBlock.getVoxelShapeByDirection(0, 0, 7, 16, 9, 9, facing);
        } else {
            return IBlock.getVoxelShapeByDirection(getXStart() - 0.5, 0, 7, 16, 9, 9, facing);
        }
    }

    @Override
    protected BlockPos findEndWithDirection(Level world, BlockPos startPos, Direction direction, boolean allowOpposite) {
        return IRailwaySign.findEndWithDirection(world, startPos, direction, allowOpposite, BlockList.STATION_NAVIGATOR_MIDDLE.get());
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(ARROW_LEFT);
    }

    @Nonnull
    @Override
    public String getDescriptionId() {
        return "block.tjmetro.station_navigator";
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        if (length != 0)
            return new BlockEntity(length, blockPos, blockState);
        else
            return null;
    }

    /**
     * @author ZiYueCommentary
     * @see ziyue.tjmetro.mod.render.RenderStationNavigator
     * @since 1.0.0-beta-4
     */
    public static class BlockEntity extends BlockEntityExtension
    {
        public final int length;
        protected LongAVLTreeSet selectedRoutes;
        protected static final String KEY_SELECTED_ROUTES = "selected_routes";

        public BlockEntity(int length, BlockPos pos, BlockState state) {
            super(getType(length), pos, state);
            this.length = length;
            this.selectedRoutes = new LongAVLTreeSet();
        }

        public static BlockEntityType<?> getType(int length) {
            return switch (length) {
                case 3 -> BlockEntityTypes.STATION_NAVIGATOR_3.get();
                case 4 -> BlockEntityTypes.STATION_NAVIGATOR_4.get();
                case 5 -> BlockEntityTypes.STATION_NAVIGATOR_5.get();
                default -> throw new InvalidParameterException();
            };
        }

        @Override
        public void readNbt(CompoundTag compoundTag) {
            this.selectedRoutes = new LongAVLTreeSet();
            Arrays.stream(compoundTag.getLongArray(KEY_SELECTED_ROUTES)).forEach(selectedRoutes::add);
        }

        @Override
        public void writeNbt(CompoundTag compoundTag) {
            compoundTag.putLongArray(KEY_SELECTED_ROUTES, new ArrayList<>(selectedRoutes));
        }

        public void setData(LongAVLTreeSet selectedIds) {
            this.selectedRoutes = new LongAVLTreeSet();
            this.selectedRoutes.addAll(selectedIds);
            setChanged();
        }

        public LongAVLTreeSet getSelectedRoutes() {
            return selectedRoutes;
        }
    }
}
