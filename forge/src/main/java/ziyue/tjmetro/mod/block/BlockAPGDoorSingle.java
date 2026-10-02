package ziyue.tjmetro.mod.block;

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
import org.mtr.block.BlockPSDAPGDoorBase;
import org.mtr.block.BlockPSDAPGGlassEndBase;
import org.mtr.block.IBlock;
import org.mtr.generated.lang.TranslationProvider;
import ziyue.tjmetro.mod.BlockEntityTypes;
import ziyue.tjmetro.mod.ItemList;
import ziyue.tjmetro.mod.block.flag.BlockFlagAPGTianjin;
import ziyue.tjmetro.mod.block.flag.BlockFlagDoorSingle;

import javax.annotation.Nonnull;

/**
 * @author ZiYueCommentary
 * @see BlockEntity
 * @see org.mtr.block.BlockAPGDoor
 * @see ziyue.tjmetro.mod.item.ItemPSDAPGTianjinBase
 * @since 1.1.1
 */

public class BlockAPGDoorSingle extends BlockPSDAPGDoorBase implements BlockFlagAPGTianjin, BlockFlagDoorSingle
{
    public final boolean isLeft;

    public BlockAPGDoorSingle(boolean isLeft) {
        super(BlockBehaviour.Properties.of());
        this.isLeft = isLeft;
    }

    @Override
    protected boolean isAPG() {
        return true;
    }

    @Override
    public boolean isLeft() {
        return isLeft;
    }

    @Nonnull
    @Override
    public Item asItem() {
        return isLeft ? ItemList.APG_DOOR_SINGLE_LEFT.get() : ItemList.APG_DOOR_SINGLE_RIGHT.get();
    }

    @Nonnull
    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor world, BlockPos pos, BlockPos neighborPos) {
        return BlockFlagDoorSingle.getStateForNeighborUpdate(state, world, pos);
    }

    @Override
    public BlockState playerWillDestroy(Level world, BlockPos pos, BlockState state, Player player) {
        BlockFlagDoorSingle.onBreak(world, pos, state, player);
        return super.playerWillDestroy(world, pos, state, player);
    }

    @Nonnull
    @Override
    public InteractionResult useWithoutItem(BlockState state, Level world, BlockPos pos, Player player, BlockHitResult hit) {
        return BlockFlagDoorSingle.onUse(state, world, pos, player);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new BlockEntity(blockPos, blockState, isLeft);
    }

    /**
     * @author ZiYueCommentary
     * @see org.mtr.render.RenderPSDAPGDoor
     * @since 1.1.1
     */
    public static class BlockEntity extends BlockEntityBase
    {
        public final boolean isLeft;

        public BlockEntity(BlockPos pos, BlockState state, boolean isLeft) {
            super(isLeft ? BlockEntityTypes.APG_DOOR_SINGLE_LEFT.get() : BlockEntityTypes.APG_DOOR_SINGLE_RIGHT.get(), pos, state);
            this.isLeft = isLeft;
        }
    }
}
