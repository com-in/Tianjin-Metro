package ziyue.tjmetro.mod.block;

import net.minecraft.util.StringRepresentable;
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
import org.mtr.block.BlockPSDAPGBase;
import org.mtr.block.BlockPSDTop;
import org.mtr.block.IBlock;
import ziyue.tjmetro.mod.BlockEntityTypes;
import ziyue.tjmetro.mod.BlockList;
import ziyue.tjmetro.mod.ItemList;
import ziyue.tjmetro.mod.block.flag.BlockFlagPSDTianjinBMT;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * @author ZiYueCommentary
 * @see BlockEntity
 * @see BlockPSDTop
 * @since 1.0.0-beta-2
 */

public class BlockPSDTopTianjinBMT extends BlockPSDTop implements BlockFlagPSDTianjinBMT
{
    public BlockPSDTopTianjinBMT() {
        super(BlockBehaviour.Properties.of());
    }

    public static final IntegerProperty ARROW_DIRECTION = IntegerProperty.create("arrow_direction", 0, 1);
    public static final EnumProperty<EnumPSDType> STYLE = EnumProperty.create("style", EnumPSDType.class);

    @Nonnull
    @Override
    public InteractionResult useWithoutItem(BlockState state, Level world, BlockPos pos, Player player, BlockHitResult hit) {
        return IBlock.checkHoldingItem(world, player, item -> {
            if (item == ItemList.WRENCH.get()) {
                world.setBlock(pos, state.cycle(STYLE), 3);
                propagate(world, pos, IBlock.getStatePropertySafe(state, BlockStateProperties.HORIZONTAL_FACING).getClockWise(), STYLE, 1);
                propagate(world, pos, IBlock.getStatePropertySafe(state, BlockStateProperties.HORIZONTAL_FACING).getCounterClockWise(), STYLE, 1);
            } else {
                world.setBlock(pos, state.cycle(ARROW_DIRECTION), 3);
                propagate(world, pos, IBlock.getStatePropertySafe(state, BlockStateProperties.HORIZONTAL_FACING).getClockWise(), ARROW_DIRECTION, 1);
                propagate(world, pos, IBlock.getStatePropertySafe(state, BlockStateProperties.HORIZONTAL_FACING).getCounterClockWise(), ARROW_DIRECTION, 1);
            }
        }, null, org.mtr.registry.Items.BRUSH.get());
    }

    @Nonnull
    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor world, BlockPos pos, BlockPos neighborPos) {
        if (direction == Direction.DOWN && !(neighborState.getBlock() instanceof BlockPSDAPGBase)) {
            return Blocks.AIR.defaultBlockState();
        } else {
            return getActualState(world, pos);
        }
    }

    @Nonnull
    @Override
    public Item asItem() {
        return ItemList.PSD_GLASS_TIANJIN_BMT.get();
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(STYLE);
        builder.add(BlockStateProperties.HORIZONTAL_FACING);
        builder.add(SIDE_EXTENDED);
        builder.add(AIR_LEFT);
        builder.add(AIR_RIGHT);
        builder.add(ARROW_DIRECTION);
    }

    @Nonnull
    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new BlockEntity(blockPos, blockState);
    }

    public static BlockState getActualState(LevelAccessor world, BlockPos pos) {
        Direction facing = null;
        EnumSide side = null;
        boolean airLeft = false, airRight = false;

        final BlockState stateBelow = world.getBlockState(pos.below());
        final Block blockBelow = stateBelow.getBlock();
        if (blockBelow instanceof BlockPSDGlassTianjinBMT || blockBelow instanceof BlockPSDDoorTianjinBMT || blockBelow instanceof BlockPSDGlassEndTianjinBMT) {
            if (blockBelow instanceof BlockPSDDoorTianjinBMT) {
                side = IBlock.getStatePropertySafe(stateBelow, SIDE);
            } else {
                side = IBlock.getStatePropertySafe(stateBelow, SIDE_EXTENDED);
            }

            if (blockBelow instanceof BlockPSDGlassEndTianjinBMT) {
                if (IBlock.getStatePropertySafe(stateBelow, BlockPSDGlassEndTianjinBMT.TOUCHING_LEFT) == BlockPSDGlassEndTianjinBMT.EnumPSDAPGGlassEndSide.AIR) {
                    airLeft = true;
                }
                if (IBlock.getStatePropertySafe(stateBelow, BlockPSDGlassEndTianjinBMT.TOUCHING_RIGHT) == BlockPSDGlassEndTianjinBMT.EnumPSDAPGGlassEndSide.AIR) {
                    airRight = true;
                }
            }

            facing = IBlock.getStatePropertySafe(stateBelow, BlockStateProperties.HORIZONTAL_FACING);
        }

        final BlockState oldState = world.getBlockState(pos);
        BlockState neighborState = (oldState.getBlock() instanceof BlockPSDTop ? oldState : BlockList.PSD_TOP_TIANJIN_BMT.get().defaultBlockState()).setValue(AIR_LEFT, airLeft).setValue(AIR_RIGHT, airRight);
        if (facing != null) {
            neighborState = neighborState.setValue(BlockStateProperties.HORIZONTAL_FACING, facing);
        }
        if (side != null) {
            neighborState = neighborState.setValue(SIDE_EXTENDED, side);
        }
        return neighborState;
    }

    /**
     * @author ZiYueCommentary
     * @see ziyue.tjmetro.mod.render.RenderPSDTopTianjin
     * @since 1.0.0-beta-1
     */
    public static class BlockEntity extends BlockEntityBase
    {
        public BlockEntity(BlockPos pos, BlockState state) {
            super(BlockEntityTypes.PSD_TOP_TIANJIN_BMT.get(), pos, state);
        }
    }

    public enum EnumPSDType implements StringRepresentable
    {
        BMT("bmt"),
        TRT("trt");

        final String name;

        EnumPSDType(String name) {
            this.name = name;
        }

        @Nonnull
        @Override
        public String getSerializedName() {
            return this.name;
        }
    }
}
