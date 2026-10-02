package ziyue.tjmetro.mod.block;

import net.minecraft.util.StringRepresentable;
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
import org.mtr.block.BlockAPGGlass;
import org.mtr.block.BlockPSDTop;
import org.mtr.block.IBlock;
import ziyue.tjmetro.mod.BlockEntityTypes;
import ziyue.tjmetro.mod.BlockList;
import ziyue.tjmetro.mod.ItemList;
import ziyue.tjmetro.mod.block.flag.BlockFlagAPGTianjinBMT;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.function.BiConsumer;

/**
 * @author ZiYueCommentary
 * @see org.mtr.block.BlockAPGGlass
 * @see ziyue.tjmetro.mod.item.ItemPSDAPGTianjinBase
 * @see BlockEntity
 * @since 1.0.0-beta-2
 */

public class BlockAPGGlassTianjinBMT extends BlockAPGGlass implements BlockFlagAPGTianjinBMT
{
    public BlockAPGGlassTianjinBMT() {
        super(BlockBehaviour.Properties.of());
    }

    public static final IntegerProperty ARROW_DIRECTION = IntegerProperty.create("propagate_property", 0, 2);
    public static final EnumProperty<EnumDoorType> STYLE = EnumProperty.create("style", EnumDoorType.class);

    @Nonnull
    @Override
    public InteractionResult useWithoutItem(BlockState state, Level world, BlockPos pos, Player player, BlockHitResult hit) {
        if (IBlock.getStatePropertySafe(state, HALF) == DoubleBlockHalf.UPPER) {
            return IBlock.checkHoldingItem(world, player, item -> {
                if (item == ItemList.WRENCH.get()) {
                    world.setBlock(pos, state.cycle(STYLE), 3);
                    BiConsumer<Boolean, Direction> setStyle = (bool, direction) -> {
                        EnumDoorType style = IBlock.getStatePropertySafe(world, pos, STYLE);
                        BlockPos offsetPos = pos;
                        for (; ; ) {
                            if (IBlockExtension.isBlock(world.getBlockState(offsetPos), BlockList.APG_DOOR_TIANJIN_BMT_BLOCK.get())) {
                                offsetPos = offsetPos.relative(direction);
                                if (bool) {
                                    final int id = (style.asId() - 1);
                                    style = EnumDoorType.byId(id < 0 ? 2 : id % 3);
                                } else {
                                    style = EnumDoorType.byId((style.asId() + 1) % 3);
                                }
                            } else if (IBlockExtension.isBlock(world.getBlockState(offsetPos), BlockList.APG_GLASS_TIANJIN_BMT_BLOCK.get())) {
                                world.setBlock(offsetPos, world.getBlockState(offsetPos).setValue(STYLE, style), 3);
                            } else {
                                break;
                            }
                            offsetPos = offsetPos.relative(direction);
                        }
                    };
                    setStyle.accept(true, IBlock.getStatePropertySafe(state, BlockStateProperties.HORIZONTAL_FACING).getClockWise());
                    setStyle.accept(false, IBlock.getStatePropertySafe(state, BlockStateProperties.HORIZONTAL_FACING).getCounterClockWise());
                } else {
                    world.setBlock(pos, state.cycle(ARROW_DIRECTION), 3);
                    propagate(world, pos, IBlock.getStatePropertySafe(state, BlockStateProperties.HORIZONTAL_FACING).getClockWise(), ARROW_DIRECTION, 3);
                    propagate(world, pos, IBlock.getStatePropertySafe(state, BlockStateProperties.HORIZONTAL_FACING).getCounterClockWise(), ARROW_DIRECTION, 3);
                }
            }, null, org.mtr.registry.Items.BRUSH.get(), ItemList.WRENCH.get());
        } else {
            return super.useWithoutItem(state, world, pos, player, hit);
        }
    }

    @Override
    protected boolean isAPG() {
        return true;
    }

    @Nonnull
    @Override
    public Item asItem() {
        return ItemList.APG_GLASS_TIANJIN_BMT.get();
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new BlockEntity(blockPos, blockState);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(BlockStateProperties.HORIZONTAL_FACING);
        builder.add(HALF);
        builder.add(SIDE_EXTENDED);
        builder.add(ARROW_DIRECTION);
        builder.add(STYLE);
    }

    /**
     * @author ZiYueCommentary
     * @see ziyue.tjmetro.mod.render.RenderAPGGlassTianjinBMT
     * @since 1.0.0-beta-2
     */
    public static class BlockEntity extends BlockPSDTop.BlockEntityBase
    {
        public BlockEntity(BlockPos pos, BlockState state) {
            super(BlockEntityTypes.APG_GLASS_TIANJIN_BMT.get(), pos, state);
        }
    }

    public enum EnumDoorType implements StringRepresentable
    {
        ROUTE(0, "route"),
        STATION_NAME(1, "station_name"),
        NEXT_STATION(2, "next_station");

        final int id;
        final String name;

        EnumDoorType(int id, String name) {
            this.id = id;
            this.name = name;
        }

        public int asId() {
            return this.id;
        }

        public static EnumDoorType byId(int id) {
            return switch (id) {
                case 0 -> ROUTE;
                case 1 -> STATION_NAME;
                case 2 -> NEXT_STATION;
                default -> throw new IllegalStateException();
            };
        }

        @Nonnull
        @Override
        public String getSerializedName() {
            return this.name;
        }
    }
}
