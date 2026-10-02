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
import org.mtr.block.IBlock;
import ziyue.tjmetro.mod.BlockEntityTypes;
import ziyue.tjmetro.mod.BlockList;
import ziyue.tjmetro.mod.ItemList;
import ziyue.tjmetro.mod.Registry;
import ziyue.tjmetro.mod.block.base.IRailwaySign;
import ziyue.tjmetro.mod.packet.PacketOpenBlockEntityScreen;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author ZiYueCommentary
 * @see BlockRailwaySignWall
 * @see BlockEntity
 * @since 1.0.0-beta-1
 */

public class BlockRailwaySignWallDouble extends BlockRailwaySignWall
{
    public BlockRailwaySignWallDouble(int length) {
        super(length);
    }

    @Nonnull
    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor world, BlockPos pos, BlockPos neighborPos) {
        final Direction facing = IBlock.getStatePropertySafe(state, BlockStateProperties.HORIZONTAL_FACING);
        final boolean isNext = ((!IBlock.getStatePropertySafe(state, EOS) && (direction == facing.getClockWise())) || IBlockExtension.isBlock(state, BlockList.RAILWAY_SIGN_WALL_DOUBLE_MIDDLE.get()) && (direction == facing.getCounterClockWise()));
        if (isNext && !(neighborState.getBlock() instanceof BlockRailwaySignWallDouble)) {
            return Blocks.AIR.defaultBlockState();
        } else {
            return state;
        }
    }

    @Override
    public void setPlacedBy(Level world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        if (world.isClientSide()) return;

        final Direction facing = IBlock.getStatePropertySafe(state, BlockStateProperties.HORIZONTAL_FACING);
        final boolean ground = IBlock.getStatePropertySafe(state, NOT_GROUND);
        for (int i = 1; i < getMiddleLength(); i++) {
            world.setBlock(pos.relative(facing.getClockWise(), i), BlockList.RAILWAY_SIGN_WALL_DOUBLE_MIDDLE.get().defaultBlockState()
                            .setValue(BlockStateProperties.HORIZONTAL_FACING, facing)
                            .setValue(EOS, false)
                            .setValue(NOT_GROUND, ground), 3);
        }
        world.setBlock(pos.relative(facing.getClockWise(), getMiddleLength()), BlockList.RAILWAY_SIGN_WALL_DOUBLE_MIDDLE.get().defaultBlockState()
                        .setValue(BlockStateProperties.HORIZONTAL_FACING, facing)
                        .setValue(EOS, true)
                        .setValue(NOT_GROUND, ground), 3);
        world.updateNeighborsAt(pos, Blocks.AIR);
        world.updateNeighborsAt(pos, state.getBlock());
    }

    @Nonnull
    @Override
    public InteractionResult useWithoutItem(BlockState state, Level world, BlockPos pos, Player player, BlockHitResult hit) {
        final Direction facing = IBlock.getStatePropertySafe(state, BlockStateProperties.HORIZONTAL_FACING);
        final BlockPos checkPos = findEndWithDirection(world, pos, facing, false);
        if (player.isHolding(ItemList.WRENCH.get())) {
            if (checkPos != null && world.getBlockEntity(checkPos) instanceof BlockEntity entity) {
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
    protected BlockPos findEndWithDirection(Level world, BlockPos startPos, Direction direction, boolean allowOpposite) {
        return IRailwaySign.findEndWithDirection(world, startPos, direction, allowOpposite, BlockList.RAILWAY_SIGN_WALL_DOUBLE_MIDDLE.get());
    }

    @Nonnull
    @Override
    public String getDescriptionId() {
        return "block.tjmetro.railway_sign_wall_double";
    }

    @Override
    public net.minecraft.world.level.block.entity.BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        if (this == BlockList.RAILWAY_SIGN_WALL_DOUBLE_MIDDLE.get())
            return null;
        else
            return new BlockEntity(length, blockPos, blockState);
    }

    /**
     * @author ZiYueCommentary
     * @see ziyue.tjmetro.mod.render.RenderRailwaySignWallDouble
     * @since 1.0.0-beta-1
     */
    public static class BlockEntity extends BlockEntityExtension
    {
        protected final List<LongAVLTreeSet> selectedIds;
        protected final String[][] signIds;
        protected boolean toggleStyle = false;
        protected static final String KEY_SELECTED_IDS = "selected_ids";
        protected static final String KEY_SIGN_LENGTH = "sign_length";
        protected static final String KEY_TOGGLE_STYLE = "toggle_style";

        public BlockEntity(int length, BlockPos pos, BlockState state) {
            super(getType(length), pos, state);
            signIds = new String[2][length];
            selectedIds = new ArrayList<>();
            selectedIds.add(new LongAVLTreeSet());
            selectedIds.add(new LongAVLTreeSet());
        }

        @Override
        public void readNbt(CompoundTag compoundTag) {
            selectedIds.forEach(LongAVLTreeSet::clear);
            for (int i = 0; i < 2; i++) {
                Arrays.stream(compoundTag.getLongArray(KEY_SELECTED_IDS + i)).forEach(selectedIds.get(i)::add);
                for (int j = 0; j < signIds[i].length; j++) {
                    final String signId = compoundTag.getString(KEY_SIGN_LENGTH + i + j);
                    signIds[i][j] = signId.isEmpty() ? null : signId;
                }
            }
            toggleStyle = compoundTag.getBoolean(KEY_TOGGLE_STYLE);
        }

        @Override
        public void writeNbt(CompoundTag compoundTag) {
            for (int i = 0; i < 2; i++) {
                compoundTag.putLongArray(KEY_SELECTED_IDS + i, new ArrayList<>(selectedIds.get(i)));
                for (int j = 0; j < signIds[i].length; j++) {
                    compoundTag.putString(KEY_SIGN_LENGTH + i + j, signIds[i][j] == null ? "" : signIds[i][j]);
                }
            }
            compoundTag.putBoolean(KEY_TOGGLE_STYLE, toggleStyle);
        }

        public void setData(List<LongAVLTreeSet> selectedIds, String[][] signTypes) {
            this.selectedIds.clear();
            this.selectedIds.addAll(selectedIds);
            if (signIds[0].length == signTypes[0].length) { // Both lines have the same length
                System.arraycopy(signTypes, 0, signIds, 0, signTypes.length);
            }
            setChanged();
        }

        public void setToggleStyle() {
            toggleStyle = !toggleStyle;
            setChanged();
        }

        public List<LongAVLTreeSet> getSelectedIds() {
            return selectedIds;
        }

        public String[][] getSignIds() {
            return signIds;
        }

        public boolean getToggleStyle() {
            return toggleStyle;
        }

        protected static BlockEntityType<?> getType(int length) {
            return switch (length) {
                case 4 -> BlockEntityTypes.RAILWAY_SIGN_WALL_DOUBLE_4.get();
                case 6 -> BlockEntityTypes.RAILWAY_SIGN_WALL_DOUBLE_6.get();
                case 8 -> BlockEntityTypes.RAILWAY_SIGN_WALL_DOUBLE_8.get();
                case 10 -> BlockEntityTypes.RAILWAY_SIGN_WALL_DOUBLE_10.get();
                default -> null;
            };
        }
    }
}
