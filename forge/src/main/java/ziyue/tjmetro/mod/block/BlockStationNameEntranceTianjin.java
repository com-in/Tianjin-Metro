package ziyue.tjmetro.mod.block;

import net.minecraft.nbt.CompoundTag;
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
import org.mtr.block.BlockStationNameBase;
import org.mtr.block.IBlock;
import ziyue.tjmetro.mod.BlockEntityTypes;
import ziyue.tjmetro.mod.ItemList;
import ziyue.tjmetro.mod.Registry;
import ziyue.tjmetro.mod.block.base.BlockEntityRenderable;
import ziyue.tjmetro.mod.IGuiExtension;
import ziyue.tjmetro.mod.packet.PacketOpenBlockEntityScreen;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Consumer;

/**
 * @author ZiYueCommentary
 * @see org.mtr.block.BlockStationNameEntrance
 * @see BlockEntity
 * @since 1.0.0-beta-1
 */

public class BlockStationNameEntranceTianjin extends BlockStationNameBase implements IBlock, IBlockTooltip
{
    /*
     * 0 - short
     * 1 - tall
     * 2 - short, no routes
     * 3 - tall, no routes
     * 4 - short, no background
     * 5 - tall, no background
     * 6 - short, no routes, no background
     * 7 - tall, no routes, no background
     */
    public static final IntegerProperty STYLE = IntegerProperty.create("style", 0, 7);

    public final boolean pinyin;
    public final Type type;

    public BlockStationNameEntranceTianjin(boolean pinyin, Type type) {
        this(BlockBehaviour.Properties.of().lightLevel(state -> 15).noCollission(), pinyin, type);
    }

    public BlockStationNameEntranceTianjin(BlockBehaviour.Properties blockSettings, boolean pinyin, Type type) {
        super(blockSettings);
        this.pinyin = pinyin;
        this.type = type;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        final BlockPos pos = ctx.getClickedPos();
        final Direction side = ctx.getClickedFace();
        final Direction facing = side.getOpposite();

        if (side != Direction.UP && side != Direction.DOWN) {
            final BlockState leftState = ctx.getLevel().getBlockState(pos.relative(facing.getCounterClockWise()));
            final BlockState rightState = ctx.getLevel().getBlockState(pos.relative(facing.getClockWise()));

            final int nearbyStyle;
            if (leftState.getBlock() instanceof BlockStationNameEntranceTianjin) {
                nearbyStyle = IBlock.getStatePropertySafe(leftState, STYLE);
            } else if (rightState.getBlock() instanceof BlockStationNameEntranceTianjin) {
                nearbyStyle = IBlock.getStatePropertySafe(rightState, STYLE);
            } else {
                nearbyStyle = 0;
            }

            return defaultBlockState()
                    .setValue(BlockStateProperties.HORIZONTAL_FACING, facing)
                    .setValue(STYLE, nearbyStyle);
        } else {
            return null;
        }
    }

    @Nonnull
    @Override
    public InteractionResult useWithoutItem(BlockState state, Level world, BlockPos pos, Player player, BlockHitResult hit) {
        return IBlock.checkHoldingItem(world, player, item -> {
            if (item == Items.BRUSH.get()) {
                world.setBlock(pos, state.cycle(STYLE), 3);
                propagate(world, pos, IBlock.getStatePropertySafe(state, BlockStateProperties.HORIZONTAL_FACING).getClockWise(), STYLE, 1);
                propagate(world, pos, IBlock.getStatePropertySafe(state, BlockStateProperties.HORIZONTAL_FACING).getCounterClockWise(), STYLE, 1);
            } else {
                Registry.sendPacketToClient(((ServerPlayer) player), new PacketOpenBlockEntityScreen(pos));
            }
        }, null, Items.BRUSH.get(), ItemList.WRENCH.get());
    }

    @Nonnull
    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        final boolean tall = IBlock.getStatePropertySafe(state, STYLE) % 2 == 1;
        return IBlock.getVoxelShapeByDirection(0, tall ? 0 : 4, 0, 16, tall ? 16 : 12, 1, IBlock.getStatePropertySafe(state, BlockStateProperties.HORIZONTAL_FACING));
    }

    public void addTooltips(ItemStack stack, @Nullable BlockGetter world, List<MutableComponent> tooltip, Item.TooltipContext options) {
        IGuiExtension.addHoldShiftTooltip(tooltip, Component.translatable("tooltip.tjmetro.station_name_entrance_tianjin"));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(BlockStateProperties.HORIZONTAL_FACING);
        builder.add(STYLE);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new BlockEntity(pinyin, type, blockPos, blockState);
    }

    /**
     * @author ZiYueCommentary
     * @see ziyue.tjmetro.mod.render.RenderStationNameEntranceTianjin
     * @see ziyue.tjmetro.mod.screen.RailwaySignScreen
     * @since 1.0.0-beta-1
     */
    public static class BlockEntity extends BlockEntityRenderable
    {
        protected long selectedId = -1;
        public static final String KEY_SELECTED_ID = "selected_id";

        public BlockEntity(boolean pinyin, Type type, BlockPos pos, BlockState state) {
            super(getType(pinyin, type), pos, state, 0, 0.00625F);
        }

        public static BlockEntityType<?> getType(boolean pinyin, Type type) {
            switch (type) {
                case TRT:
                    if (pinyin) return BlockEntityTypes.STATION_NAME_ENTRANCE_TIANJIN_PINYIN.get();
                    else return BlockEntityTypes.STATION_NAME_ENTRANCE_TIANJIN.get();
                case BMT:
                    if (pinyin) return BlockEntityTypes.STATION_NAME_ENTRANCE_TIANJIN_BMT_PINYIN.get();
                    else return BlockEntityTypes.STATION_NAME_ENTRANCE_TIANJIN_BMT.get();
                case JINJING:
                    if (pinyin) return BlockEntityTypes.STATION_NAME_ENTRANCE_TIANJIN_JINJING_PINYIN.get();
                    else return BlockEntityTypes.STATION_NAME_ENTRANCE_TIANJIN_JINJING.get();
                default:
                    return null;
            }
        }

        @Override
        public void readNbt(CompoundTag compoundTag) {
            selectedId = compoundTag.getLong(KEY_SELECTED_ID);
            super.readNbt(compoundTag);
        }

        @Override
        public void writeNbt(CompoundTag compoundTag) {
            compoundTag.putLong(KEY_SELECTED_ID, selectedId);
            super.writeNbt(compoundTag);
        }

        public void setData(long selectedId) {
            Consumer<Direction> setStyle = direction -> {
                BlockPos offsetPos = getBlockPos();
                net.minecraft.world.level.block.entity.BlockEntity blockEntity = getLevel().getBlockEntity(offsetPos);
                while ((blockEntity != null) && (blockEntity instanceof BlockEntity entity)) {
                    entity.selectedId = selectedId;
                    entity.setChanged();
                    offsetPos = offsetPos.relative(direction);
                    blockEntity = getLevel().getBlockEntity(offsetPos);
                }
            };
            setStyle.accept(IBlock.getStatePropertySafe(getBlockState(), BlockStateProperties.HORIZONTAL_FACING).getClockWise());
            setStyle.accept(IBlock.getStatePropertySafe(getBlockState(), BlockStateProperties.HORIZONTAL_FACING).getCounterClockWise());
            this.selectedId = selectedId;
            setChanged();
        }

        public long getSelectedId() {
            return selectedId;
        }
    }

    public enum Type
    {
        TRT,
        BMT,
        JINJING
    }
}
