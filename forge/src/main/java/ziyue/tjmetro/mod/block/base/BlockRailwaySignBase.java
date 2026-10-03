package ziyue.tjmetro.mod.block.base;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
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
import net.minecraft.world.level.block.EntityBlock;
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
import net.minecraft.world.level.block.EntityBlock;
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
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.entity.Entity;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.sounds.AbstractSoundInstance;
import net.minecraft.client.resources.sounds.TickableSoundInstance;
import org.mtr.block.BlockEntityExtension;
import org.mtr.render.BlockEntityRendererExtension;
import org.mtr.block.IBlock;
import ziyue.tjmetro.mod.block.IBlockExtension;
import ziyue.tjmetro.mod.block.IBlockTooltip;
import ziyue.tjmetro.mod.ItemList;
import ziyue.tjmetro.mod.Registry;
import ziyue.tjmetro.mod.packet.PacketOpenBlockEntityScreen;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * @author ZiYueCommentary
 * @see org.mtr.block.BlockRailwaySign
 * @see BlockEntityBase
 * @since 1.0.0-beta-1
 */

public abstract class BlockRailwaySignBase extends Block implements IBlock, IBlockTooltip, EntityBlock
{
    public final int length;
    public final boolean isOdd;

    public BlockRailwaySignBase(BlockBehaviour.Properties settings, int length, boolean isOdd) {
        super(settings);
        this.length = length;
        this.isOdd = isOdd;
    }

    @Nonnull
    @Override
    public InteractionResult useWithoutItem(BlockState state, Level world, BlockPos pos, Player player, BlockHitResult hit) {
        final Direction facing = IBlock.getStatePropertySafe(state, BlockStateProperties.HORIZONTAL_FACING);
        // 沿 facing 方向回溯即可定位到持有方块实体的主块，任意一块、任意一面均可命中
        final BlockPos checkPos = findEndWithDirection(world, pos, facing, false);
        if (player.isHolding(ItemList.WRENCH.get()) && player.isShiftKeyDown()) {
            if (checkPos != null && world.getBlockEntity(checkPos) instanceof BlockEntityBase entity) {
                entity.setToggleStyle();
                return InteractionResult.SUCCESS;
            }
        }
        return IBlockExtension.checkHoldingBrushOrWrench(world, player, () -> {
            if (checkPos != null) {
                Registry.sendPacketToClient(((ServerPlayer) player), new PacketOpenBlockEntityScreen(checkPos));
            }
        });
    }

    @Nonnull
    @Override
    public abstract BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor world, BlockPos pos, BlockPos neighborPos);

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        final Direction facing = ctx.getHorizontalDirection();
        return IBlock.isReplaceable(ctx, facing.getClockWise(), getMiddleLength() + 2) ? defaultBlockState().setValue(BlockStateProperties.HORIZONTAL_FACING, facing) : null;
    }

    @Override
    public BlockState playerWillDestroy(Level world, BlockPos pos, BlockState state, Player player) {
        final Direction facing = IBlock.getStatePropertySafe(state, BlockStateProperties.HORIZONTAL_FACING);

        final BlockPos checkPos = findEndWithDirection(world, pos, facing, true);
        if (checkPos != null) {
            IBlock.playerWillDestroyCreative(world, player, checkPos);
        }

        return super.playerWillDestroy(world, pos, state, player);
    }

    @Override
    public abstract void setPlacedBy(Level world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack);

    @Nonnull
    @Override
    public abstract VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context);

    @Nonnull
    @Override
    public abstract String getDescriptionId();

    public void addTooltips(ItemStack stack, @Nullable BlockGetter world, List<MutableComponent> tooltip, Item.TooltipContext options) {
        tooltip.add(Component.translatable("tooltip.mtr.railway_sign_length", length).withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable(isOdd ? "tooltip.mtr.railway_sign_odd" : "tooltip.mtr.railway_sign_even").withStyle(ChatFormatting.GRAY));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(BlockStateProperties.HORIZONTAL_FACING);
    }

    public int getXStart() {
        switch (length % 4) {
            default:
                return isOdd ? 8 : 16;
            case 1:
                return isOdd ? 4 : 12;
            case 2:
                return isOdd ? 16 : 8;
            case 3:
                return isOdd ? 12 : 4;
        }
    }

    protected int getMiddleLength() {
        return (length - (4 - getXStart() / 4)) / 2;
    }

    protected abstract BlockPos findEndWithDirection(Level world, BlockPos startPos, Direction direction, boolean allowOpposite);

    public abstract static class BlockEntityBase extends BlockEntityExtension
    {
        protected final LongAVLTreeSet selectedIds;
        protected final String[] signIds;
        protected boolean toggleStyle = false; // This is a very dumb way to implement this, but it is convenient
        protected static final String KEY_SELECTED_IDS = "selected_ids";
        protected static final String KEY_SIGN_LENGTH = "sign_length";
        protected static final String KEY_TOGGLE_STYLE = "toggle_style";

        public BlockEntityBase(BlockEntityType<?> type, int length, BlockPos pos, BlockState state) {
            super(type, pos, state);
            signIds = new String[length];
            selectedIds = new LongAVLTreeSet();
        }

        @Override
        public void readNbt(CompoundTag compoundTag) {
            selectedIds.clear();
            Arrays.stream(compoundTag.getLongArray(KEY_SELECTED_IDS)).forEach(selectedIds::add);
            for (int i = 0; i < signIds.length; i++) {
                final String signId = compoundTag.getString(KEY_SIGN_LENGTH + i);
                signIds[i] = signId.isEmpty() ? null : signId.toLowerCase(Locale.ENGLISH);
            }
            toggleStyle = compoundTag.getBoolean(KEY_TOGGLE_STYLE);
        }

        @Override
        public void writeNbt(CompoundTag compoundTag) {
            compoundTag.putLongArray(KEY_SELECTED_IDS, new ArrayList<>(selectedIds));
            for (int i = 0; i < signIds.length; i++) {
                compoundTag.putString(KEY_SIGN_LENGTH + i, signIds[i] == null ? "" : signIds[i]);
            }
            compoundTag.putBoolean(KEY_TOGGLE_STYLE, toggleStyle);
        }

        public void setData(LongAVLTreeSet selectedIds, String[] signTypes) {
            this.selectedIds.clear();
            this.selectedIds.addAll(selectedIds);
            if (signIds.length == signTypes.length) {
                System.arraycopy(signTypes, 0, signIds, 0, signTypes.length);
            }
            setChanged();
        }

        public void setToggleStyle() {
            toggleStyle = !toggleStyle;
            setChanged();
        }

        public LongAVLTreeSet getSelectedIds() {
            return selectedIds;
        }

        public String[] getSignIds() {
            return signIds;
        }

        public boolean getToggleStyle() {
            return toggleStyle;
        }
    }
}
