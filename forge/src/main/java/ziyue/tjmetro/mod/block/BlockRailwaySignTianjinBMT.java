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
 * @see BlockRailwaySignBase
 * @since 1.0.0-beta-1
 */

public class BlockRailwaySignTianjinBMT extends BlockRailwaySignBase implements IBlockTooltip
{
    public BlockRailwaySignTianjinBMT(int length, boolean isOdd) {
        super(BlockBehaviour.Properties.of().lightLevel(state -> 0), length, isOdd);
    }

    @Nonnull
    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor world, BlockPos pos, BlockPos neighborPos) {
        return IRailwaySign.getStateForNeighborUpdate(state, direction, neighborState, BlockList.RAILWAY_SIGN_TIANJIN_BMT_MIDDLE.get());
    }

    @Override
    public void setPlacedBy(Level world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        IRailwaySign.onPlaced(world, pos, state, BlockList.RAILWAY_SIGN_TIANJIN_BMT_MIDDLE.get(), getMiddleLength());
    }

    @Nonnull
    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return IRailwaySign.getOutlineShape(state, getXStart(), BlockList.RAILWAY_SIGN_TIANJIN_BMT_MIDDLE.get());
    }

    @Nonnull
    @Override
    public String getDescriptionId() {
        return "block.tjmetro.railway_sign_tianjin_bmt";
    }

    public void addTooltips(ItemStack stack, @Nullable BlockGetter world, List<MutableComponent> tooltip, Item.TooltipContext options) {
        super.addTooltips(stack, world, tooltip, options);
        IGuiExtension.addHoldShiftTooltip(tooltip, Component.translatable("tooltip.tjmetro.railway_sign_bmt"));
    }

    @Override
    protected BlockPos findEndWithDirection(Level world, BlockPos startPos, Direction direction, boolean allowOpposite) {
        return IRailwaySign.findEndWithDirection(world, startPos, direction, allowOpposite, BlockList.RAILWAY_SIGN_TIANJIN_BMT_MIDDLE.get());
    }

    @Override
    public @Nonnull InteractionResult useWithoutItem(BlockState state, Level world, BlockPos pos, Player player, BlockHitResult hit) {
        final Direction facing = IBlock.getStatePropertySafe(state, BlockStateProperties.HORIZONTAL_FACING);
        // 沿 facing 方向回溯定位主块（持方块实体），不再依赖点击面，任意一块/一面都能打开界面
        final BlockPos checkPos = findEndWithDirection(world, pos, facing, false);
        return IBlockExtension.checkHoldingBrushOrWrench(world, player, () -> {
            if (checkPos != null) {
                Registry.sendPacketToClient(((ServerPlayer) player), new PacketOpenBlockEntityScreen(checkPos));
            }
        });
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        if (this == BlockList.RAILWAY_SIGN_TIANJIN_BMT_MIDDLE.get())
            return null;
        else
            return new BlockEntity(length, isOdd, blockPos, blockState);
    }

    /**
     * @author ZiYueCommentary
     * @see ziyue.tjmetro.mod.render.RenderRailwaySignTianjinBMT
     * @since 1.0.0-beta-1
     */
    public static class BlockEntity extends BlockEntityBase
    {
        public BlockEntity(int length, boolean isOdd, BlockPos pos, BlockState state) {
            super(getType(length, isOdd), length, pos, state);
        }

        public static BlockEntityType<?> getType(int length, boolean isOdd) {
            return switch (length) {
                case 2 ->
                        isOdd ? BlockEntityTypes.RAILWAY_SIGN_TIANJIN_BMT_2_ODD.get() : BlockEntityTypes.RAILWAY_SIGN_TIANJIN_BMT_2_EVEN.get();
                case 3 ->
                        isOdd ? BlockEntityTypes.RAILWAY_SIGN_TIANJIN_BMT_3_ODD.get() : BlockEntityTypes.RAILWAY_SIGN_TIANJIN_BMT_3_EVEN.get();
                case 4 ->
                        isOdd ? BlockEntityTypes.RAILWAY_SIGN_TIANJIN_BMT_4_ODD.get() : BlockEntityTypes.RAILWAY_SIGN_TIANJIN_BMT_4_EVEN.get();
                case 5 ->
                        isOdd ? BlockEntityTypes.RAILWAY_SIGN_TIANJIN_BMT_5_ODD.get() : BlockEntityTypes.RAILWAY_SIGN_TIANJIN_BMT_5_EVEN.get();
                case 6 ->
                        isOdd ? BlockEntityTypes.RAILWAY_SIGN_TIANJIN_BMT_6_ODD.get() : BlockEntityTypes.RAILWAY_SIGN_TIANJIN_BMT_6_EVEN.get();
                case 7 ->
                        isOdd ? BlockEntityTypes.RAILWAY_SIGN_TIANJIN_BMT_7_ODD.get() : BlockEntityTypes.RAILWAY_SIGN_TIANJIN_BMT_7_EVEN.get();
                default -> null;
            };
        }
    }
}
