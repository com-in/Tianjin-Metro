package ziyue.tjmetro.mod.block;

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
import org.mtr.block.BlockEntityExtension;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.Block;
import org.mtr.block.BlockRouteSignBase;
import org.mtr.block.IBlock;
import ziyue.tjmetro.mod.BlockEntityTypes;
import ziyue.tjmetro.mod.Registry;
import ziyue.tjmetro.mod.TianjinMetro;
import ziyue.tjmetro.mod.packet.PacketOpenBlockEntityScreen;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

import static org.mtr.block.IBlock.SIDE;

/**
 * @author ZiYueCommentary
 * @see BlockEntity
 * @since 1.0.0
 */

public class BlockStationNameSignTianjin extends Block implements EntityBlock 
{
    public BlockStationNameSignTianjin() {
        this(BlockBehaviour.Properties.of().noCollission().lightLevel(state -> 10));
    }

    public BlockStationNameSignTianjin(BlockBehaviour.Properties blockSettings) {
        super(blockSettings);
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext ctx) {
        final Direction direction = ctx.getHorizontalDirection();
        final BlockState state = defaultBlockState().setValue(BlockStateProperties.HORIZONTAL_FACING, direction);
        if (IBlock.isReplaceable(ctx, direction.getClockWise(), 2)) {
            ctx.getLevel().setBlock(ctx.getClickedPos().relative(direction.getClockWise(), 1), state.setValue(SIDE, IBlock.EnumSide.RIGHT), 3);
            return state.setValue(SIDE, IBlock.EnumSide.LEFT);
        }
        return null;
    }

    @Override
    public @Nonnull InteractionResult useWithoutItem(BlockState state, Level world, BlockPos pos, Player player, BlockHitResult hit) {
        return IBlockExtension.checkHoldingBrushOrWrench(world, player, () -> Registry.sendPacketToClient(((ServerPlayer) player), new PacketOpenBlockEntityScreen(pos)));
    }

    @Override
    public BlockState playerWillDestroy(Level world, BlockPos pos, BlockState state, Player player) {
        final Direction direction = IBlock.getStatePropertySafe(state, BlockStateProperties.HORIZONTAL_FACING);
        final IBlock.EnumSide side = IBlock.getStatePropertySafe(state, SIDE);
        IBlockExtension.breakBlock(world, pos.relative(side == IBlock.EnumSide.LEFT ? direction.getClockWise() : direction.getCounterClockWise(), 1));
        return super.playerWillDestroy(world, pos, state, player);
    }

    @Override
    public @Nonnull VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        final Direction direction = IBlock.getStatePropertySafe(state, BlockStateProperties.HORIZONTAL_FACING);
        if (IBlock.getStatePropertySafe(state, SIDE) == IBlock.EnumSide.LEFT) {
            return IBlock.getVoxelShapeByDirection(4, 0, 0, 16, 16, 1, direction);
        } else {
            return IBlock.getVoxelShapeByDirection(0, 0, 0, 12, 16, 1, direction);
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(BlockStateProperties.HORIZONTAL_FACING);
        builder.add(SIDE);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new BlockEntity(blockPos, blockState);
    }

    /**
     * @author ZiYueCommentary
     * @see ziyue.tjmetro.mod.render.RenderStationNameSignTianjin
     * @since 1.0.0
     */
    public static class BlockEntity extends BlockRouteSignBase.BlockEntityBase
    {
        public BlockEntity(BlockPos pos, BlockState state) {
            super(BlockEntityTypes.STATION_NAME_SIGN_TIANJIN.get(), pos, state);
        }

        public void setData(long platformId) {
            this.setPlatformId(platformId);
            final BlockPos pos;
            if (IBlock.getStatePropertySafe(this.getBlockState(), SIDE) == IBlock.EnumSide.LEFT) {
                pos = this.getBlockPos().relative(IBlock.getStatePropertySafe(this.getBlockState(), BlockStateProperties.HORIZONTAL_FACING).getClockWise());
            } else {
                pos = this.getBlockPos().relative(IBlock.getStatePropertySafe(this.getBlockState(), BlockStateProperties.HORIZONTAL_FACING).getCounterClockWise());
            }

            net.minecraft.world.level.block.entity.BlockEntity blockEntity = this.getLevel().getBlockEntity(pos);
            if (blockEntity instanceof BlockEntity entity) {
                entity.setPlatformId(platformId);
                entity.setChanged();
            } else {
                TianjinMetro.LOGGER.error("BlockStationNameSignTianjin.BlockEntity: Unable to set data for block entity at {}", pos.toShortString());
            }
            setChanged();
        }
    }
}
