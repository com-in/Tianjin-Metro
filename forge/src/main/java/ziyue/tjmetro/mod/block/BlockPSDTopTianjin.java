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
import ziyue.tjmetro.mod.block.flag.BlockFlagPSDTianjin;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.function.Consumer;

/**
 * @author ZiYueCommentary
 * @see BlockEntity
 * @see BlockPSDTop
 * @since 1.0.0-beta-1
 */

public class BlockPSDTopTianjin extends BlockPSDTop implements BlockFlagPSDTianjin
{
    public final boolean jinjing;
    public static final EnumProperty<EnumPSDType> STYLE = EnumProperty.create("style", EnumPSDType.class);

    public BlockPSDTopTianjin(boolean jinjing) {
        super(BlockBehaviour.Properties.of());
        this.jinjing = jinjing;
    }

    @Nonnull
    @Override
    public InteractionResult useWithoutItem(BlockState state, Level world, BlockPos pos, Player player, BlockHitResult hit) {
        return IBlock.checkHoldingItem(world, player, item -> {
            if (item == ItemList.WRENCH.get()) {
                if ((IBlock.getStatePropertySafe(state, PERSISTENT) == EnumPersistent.ARROW) || (world.getBlockState(pos.below()).getBlock() instanceof BlockPSDDoorTianjin)) {
                    world.setBlock(pos, IBlockExtension.cycleBlockState(state, STYLE, value -> value != EnumPSDType.NEXT_STATION), 3);
                    BlockPos pos1 = (IBlock.getStatePropertySafe(state, SIDE_EXTENDED) == EnumSide.LEFT) ? pos.relative(IBlock.getStatePropertySafe(state, BlockStateProperties.HORIZONTAL_FACING).getClockWise()) : pos.relative(IBlock.getStatePropertySafe(state, BlockStateProperties.HORIZONTAL_FACING).getCounterClockWise());
                    world.setBlock(pos1, IBlockExtension.cycleBlockState(world.getBlockState(pos1), STYLE, value -> value != EnumPSDType.NEXT_STATION), 3);
                } else {
                    world.setBlock(pos, IBlockExtension.cycleBlockState(state, STYLE, EnumPSDType.DEFAULT, EnumPSDType.NEXT_STATION), 3);
                    Consumer<Direction> setStyle = direction -> {
                        EnumPSDType style = IBlock.getStatePropertySafe(world, pos, STYLE);
                        BlockPos offsetPos = pos;
                        for (; ; ) {
                            if ((IBlock.getStatePropertySafe(world.getBlockState(offsetPos), PERSISTENT) == EnumPersistent.ARROW) || (world.getBlockState(offsetPos.below()).getBlock() instanceof BlockPSDDoorTianjin)) {
                                offsetPos = offsetPos.relative(direction);
                                style = (style == EnumPSDType.DEFAULT) ? EnumPSDType.NEXT_STATION : EnumPSDType.DEFAULT;
                            } else if (this == world.getBlockState(offsetPos).getBlock()) {
                                world.setBlock(offsetPos, world.getBlockState(offsetPos).setValue(STYLE, style), 3);
                            } else {
                                break;
                            }
                            offsetPos = offsetPos.relative(direction);
                        }
                    };
                    setStyle.accept(IBlock.getStatePropertySafe(state, BlockStateProperties.HORIZONTAL_FACING).getClockWise());
                    setStyle.accept(IBlock.getStatePropertySafe(state, BlockStateProperties.HORIZONTAL_FACING).getCounterClockWise());
                }
            } else if (item == org.mtr.registry.Items.BRUSH.get()) {
                world.setBlock(pos, state.cycle(ARROW_DIRECTION), 3);
                propagate(world, pos, IBlock.getStatePropertySafe(state, BlockStateProperties.HORIZONTAL_FACING).getClockWise(), ARROW_DIRECTION, 1);
                propagate(world, pos, IBlock.getStatePropertySafe(state, BlockStateProperties.HORIZONTAL_FACING).getCounterClockWise(), ARROW_DIRECTION, 1);
            } else {
                final boolean shouldBePersistent = IBlock.getStatePropertySafe(state, PERSISTENT) == EnumPersistent.NONE;
                setState(world, pos, shouldBePersistent);
                propagate(world, pos, IBlock.getStatePropertySafe(state, BlockStateProperties.HORIZONTAL_FACING).getClockWise(), offsetPos -> setState(world, offsetPos, shouldBePersistent), 1);
                propagate(world, pos, IBlock.getStatePropertySafe(state, BlockStateProperties.HORIZONTAL_FACING).getCounterClockWise(), offsetPos -> setState(world, offsetPos, shouldBePersistent), 1);
            }
        }, null, org.mtr.registry.Items.BRUSH.get(), ItemList.WRENCH.get(), net.minecraft.world.item.Items.SHEARS);
    }

    protected void setState(Level world, BlockPos pos, boolean shouldBePersistent) {
        final Block blockBelow = world.getBlockState(pos.below()).getBlock();
        if (blockBelow instanceof BlockFlagPSDTianjin) {
            if (shouldBePersistent) {
                world.setBlock(pos, world.getBlockState(pos).setValue(PERSISTENT, blockBelow instanceof BlockPSDDoorTianjin ? EnumPersistent.ARROW : blockBelow instanceof BlockPSDGlassTianjin ? EnumPersistent.ROUTE : EnumPersistent.BLANK), 3);
            } else {
                world.setBlock(pos, world.getBlockState(pos).setValue(PERSISTENT, EnumPersistent.NONE), 3);
            }
        }
    }

    @Nonnull
    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor world, BlockPos pos, BlockPos neighborPos) {
        if (direction == Direction.DOWN && IBlock.getStatePropertySafe(state, PERSISTENT) == EnumPersistent.NONE && !(neighborState.getBlock() instanceof BlockPSDAPGBase)) {
            return Blocks.AIR.defaultBlockState();
        } else {
            return getActualState(world, pos, jinjing);
        }
    }

    @Nonnull
    @Override
    public Item asItem() {
        return jinjing ? ItemList.PSD_GLASS_TIANJIN_JINJING.get() : ItemList.PSD_GLASS_TIANJIN.get();
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(STYLE);
        super.createBlockStateDefinition(builder);
    }

    @Nonnull
    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new BlockEntity(blockPos, blockState, jinjing);
    }

    public static BlockState getActualState(LevelAccessor world, BlockPos pos, boolean jinjing) {
        Direction facing = null;
        EnumSide side = null;
        boolean airLeft = false, airRight = false;

        final BlockState stateBelow = world.getBlockState(pos.below());
        final Block blockBelow = stateBelow.getBlock();
        if (blockBelow instanceof BlockFlagPSDTianjin) {
            if (blockBelow instanceof BlockPSDDoorTianjin) {
                side = IBlock.getStatePropertySafe(stateBelow, SIDE);
            } else {
                side = IBlock.getStatePropertySafe(stateBelow, SIDE_EXTENDED);
            }

            if (blockBelow instanceof BlockPSDGlassEndTianjin) {
                if (IBlock.getStatePropertySafe(stateBelow, BlockPSDGlassEndTianjin.TOUCHING_LEFT) == BlockPSDGlassEndTianjin.EnumPSDAPGGlassEndSide.AIR) {
                    airLeft = true;
                }
                if (IBlock.getStatePropertySafe(stateBelow, BlockPSDGlassEndTianjin.TOUCHING_RIGHT) == BlockPSDGlassEndTianjin.EnumPSDAPGGlassEndSide.AIR) {
                    airRight = true;
                }
            }

            facing = IBlock.getStatePropertySafe(stateBelow, BlockStateProperties.HORIZONTAL_FACING);
        }

        final BlockState oldState = world.getBlockState(pos);
        BlockState neighborState = (oldState.getBlock() instanceof BlockPSDTop ? oldState : (jinjing ? BlockList.PSD_TOP_TIANJIN_JINJING.get() : BlockList.PSD_TOP_TIANJIN.get()).defaultBlockState()).setValue(AIR_LEFT, airLeft).setValue(AIR_RIGHT, airRight);
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
        public final boolean jinjing;

        public BlockEntity(BlockPos pos, BlockState state, boolean jinjing) {
            super(jinjing ? BlockEntityTypes.PSD_TOP_TIANJIN_JINJING.get() : BlockEntityTypes.PSD_TOP_TIANJIN.get(), pos, state);
            this.jinjing = jinjing;
        }
    }

    public enum EnumPSDType implements StringRepresentable
    {
        DEFAULT("default"),
        STATION_NAME("station_name"),
        //NAME_AND_ROUTES_LEFT("name_and_routes_left"),
        //NAME_AND_ROUTES_RIGHT("name_and_routes_right"),
        //NAME_AND_ROUTES_BOTH("name_and_routes_both"),
        NEXT_STATION("next_station");

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
