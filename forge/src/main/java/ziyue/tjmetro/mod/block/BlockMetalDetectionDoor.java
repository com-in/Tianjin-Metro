package ziyue.tjmetro.mod.block;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundSource;
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
import org.mtr.registry.SoundEvents;
import org.mtr.block.IBlock;
import ziyue.tjmetro.mapping.DefaultedItemStackList;
import ziyue.tjmetro.mapping.MetalDetectionDoorEntity;
import ziyue.tjmetro.mapping.PlayerInventoryHelper;
import ziyue.tjmetro.mapping.RegistryHelper;
import ziyue.tjmetro.mod.BlockEntityTypes;
import ziyue.tjmetro.mod.BlockList;
import ziyue.tjmetro.mod.IGuiExtension;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/**
 * A device for clearing specify items from players' inventory.
 *
 * @author ZiYueCommentary
 * @see BlockEntity
 * @since 1.0.0-beta-1
 */

public class BlockMetalDetectionDoor extends Block implements IBlock, IBlockTooltip, EntityBlock
{
    public static final BooleanProperty OPEN = BooleanProperty.create("open");

    public BlockMetalDetectionDoor() {
        this(BlockBehaviour.Properties.of().noCollission());
    }

    public BlockMetalDetectionDoor(BlockBehaviour.Properties blockSettings) {
        super(blockSettings);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        final BlockState state = defaultBlockState().setValue(BlockStateProperties.HORIZONTAL_FACING, ctx.getHorizontalDirection());
        final BlockPos pos = ctx.getClickedPos();
        final Level world = ctx.getLevel();
        if (IBlock.isReplaceable(ctx, Direction.UP, 3)) {
            world.setBlock(pos.above(1), state.setValue(THIRD, EnumThird.MIDDLE), 3);
            world.setBlock(pos.above(2), state.setValue(THIRD, EnumThird.UPPER), 3);
            return state.setValue(THIRD, EnumThird.LOWER);
        }
        return null;
    }

    @Nonnull
    @Override
    public InteractionResult useWithoutItem(BlockState state, Level world, BlockPos pos, Player player, BlockHitResult hit) {
        return IBlockExtension.checkHoldingBrushOrWrench(world, player, () -> {
            final BlockPos blockPos = switch (IBlock.getStatePropertySafe(state, THIRD)) {
                case LOWER -> pos;
                case MIDDLE -> pos.below(1);
                default -> pos.below(2);
            };
            MetalDetectionDoorEntity entity = new MetalDetectionDoorEntity(world, blockPos, (BlockEntity) world.getBlockEntity(blockPos));
            world.addFreshEntity(entity);
            entity.interact(player, InteractionHand.MAIN_HAND);
            BlockEntity lower = (BlockEntity) world.getBlockEntity(blockPos);
            BlockEntity middle = (BlockEntity) world.getBlockEntity(blockPos.above(1));
            BlockEntity upper = (BlockEntity) world.getBlockEntity(blockPos.above(2));
            middle.setData(lower.inventory);
            upper.setData(lower.inventory);
        });
    }

    @Nonnull
    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        final Direction direction = IBlock.getStatePropertySafe(state, BlockStateProperties.HORIZONTAL_FACING);
        if (IBlock.getStatePropertySafe(state, THIRD) == EnumThird.UPPER) {
            return IBlock.getVoxelShapeByDirection(0, 0, 1, 16, 6, 15, direction);
        } else {
            final VoxelShape left = IBlock.getVoxelShapeByDirection(0, 0, 1, 1, 16, 15, direction);
            final VoxelShape right = IBlock.getVoxelShapeByDirection(15, 0, 1, 16, 16, 15, direction);
            return Shapes.or(left, right);
        }
    }

    @Nonnull
    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        final VoxelShape shape = this.getShape(state, world, pos, context);
        if ((!IBlock.getStatePropertySafe(state, OPEN)) && (IBlock.getStatePropertySafe(state, THIRD) != EnumThird.UPPER)) {
            final VoxelShape barrier = IBlock.getVoxelShapeByDirection(0, 0, 1, 16, 16, 2, IBlock.getStatePropertySafe(state, BlockStateProperties.HORIZONTAL_FACING));
            return Shapes.or(shape, barrier);
        }
        return shape;
    }

    @Override
    public BlockState playerWillDestroy(Level world, BlockPos pos, BlockState state, Player player) {
        switch (IBlock.getStatePropertySafe(state, THIRD)) {
            case UPPER:
                IBlockExtension.breakBlock(world, pos.below(1), BlockList.METAL_DETECTION_DOOR.get());
                IBlockExtension.breakBlock(world, pos.below(2), BlockList.METAL_DETECTION_DOOR.get());
                break;
            case MIDDLE:
                IBlockExtension.breakBlock(world, pos.above(1), BlockList.METAL_DETECTION_DOOR.get());
                IBlockExtension.breakBlock(world, pos.below(1), BlockList.METAL_DETECTION_DOOR.get());
                break;
            case LOWER:
                IBlockExtension.breakBlock(world, pos.above(1), BlockList.METAL_DETECTION_DOOR.get());
                IBlockExtension.breakBlock(world, pos.above(2), BlockList.METAL_DETECTION_DOOR.get());
                break;
        }
        return super.playerWillDestroy(world, pos, state, player);
    }

    public void addTooltips(ItemStack stack, @Nullable BlockGetter world, List<MutableComponent> tooltip, Item.TooltipContext options) {
        IGuiExtension.addHoldShiftTooltip(tooltip, Component.translatable("tooltip.tjmetro.metal_detection_door"));
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new BlockEntity(blockPos, blockState);
    }

    @Nullable
    @Override
    public <T extends net.minecraft.world.level.block.entity.BlockEntity> net.minecraft.world.level.block.entity.BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (level.isClientSide) {
            return null;
        }
        return (tickLevel, tickPos, tickState, blockEntity) -> {
            if (blockEntity instanceof BlockEntity metalDetectionDoor) {
                metalDetectionDoor.blockEntityTick();
            }
        };
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(BlockStateProperties.HORIZONTAL_FACING);
        builder.add(THIRD);
        builder.add(OPEN);
    }

    /**
     * @author ZiYueCommentary
     * @since 1.0.0-beta-2
     */
    public static class BlockEntity extends BlockEntityExtension
    {
        public final DefaultedItemStackList inventory;

        public BlockEntity(BlockPos blockPos, BlockState blockState) {
            super(BlockEntityTypes.METAL_DETECTION_DOOR.get(), blockPos, blockState);
            this.inventory = DefaultedItemStackList.ofSize(9);
        }

        public void blockEntityTick() {
            if (IBlock.getStatePropertySafe(getBlockState(), THIRD) != EnumThird.LOWER) return;

            final Player player = getLevel().getNearestPlayer(getBlockPos().getX(), getBlockPos().getY(), getBlockPos().getZ(), 1, false);
            if (player != null) {
                if (getBlockPos().getX() == Math.floor(player.getX()) && getBlockPos().getY() == Math.round(player.getY()) && getBlockPos().getZ() == Math.floor(player.getZ())) {
                    List<?> items = this.inventory.stream().map(itemStack -> itemStack.getItem()).toList(); // Do not use method reference.
                    PlayerInventoryHelper.clearItems(player, items::contains);
                    if (IBlock.getStatePropertySafe(getBlockState(), OPEN)) return;
                    getLevel().playSound(null, getBlockPos(), SoundEvents.TICKET_BARRIER.get(), SoundSource.BLOCKS, 1, 1);
                    getLevel().setBlock(getBlockPos(), getBlockState().setValue(OPEN, true), 3);
                }
            } else {
                getLevel().setBlock(getBlockPos(), getBlockState().setValue(OPEN, false), 3);
            }
        }

        @Override
        public void writeNbt(CompoundTag compoundTag) {
            for (int i = 0; i < this.inventory.size(); i++) {
                compoundTag.putString(Integer.toString(i), RegistryHelper.getIdentifierByItem(this.inventory.get(i).getItem()).toString());
            }
            super.writeNbt(compoundTag);
        }

        @Override
        public void readNbt(CompoundTag compoundTag) {
            for (int i = 0; i < this.inventory.size(); i++) {
                this.inventory.set(i, RegistryHelper.getItemStackByIdentifier(ResourceLocation.parse(compoundTag.getString(Integer.toString(i)))));
            }
            super.readNbt(compoundTag);
        }

        public void setData(DefaultedItemStackList list) {
            for (int i = 0; i < this.inventory.size(); i++) {
                this.inventory.set(i, RegistryHelper.cloneSingleItemStack(list.get(i)));
            }
            setChanged();
        }
    }
}
