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
import org.mtr.registry.Items;
import org.mtr.block.IBlock;
import ziyue.tjmetro.mod.ItemList;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Some methods similar to methods in IBlock.
 *
 * @see IBlock
 * @since 1.0.0-beta-1
 */

public interface IBlockExtension
{
    EnumProperty<BlockThirdProperty> THIRD = EnumProperty.create("third", BlockThirdProperty.class);

    /**
     * Replace block with air.
     *
     * @param pos block's position
     * @author ZiYueCommentary
     * @since 1.0.0-beta-1
     */
    static void breakBlock(Level world, BlockPos pos) {
        if (world.isClientSide()) return;

        world.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
    }

    /**
     * Specify a block, if a block in pos is a specified block, then replace it with air.
     *
     * @param pos   block's position
     * @param block specified block
     * @author ZiYueCommentary
     * @since 1.0.0-beta-1
     */
    static void breakBlock(Level world, BlockPos pos, Block block) {
        if (world.isClientSide()) return;

        if (isBlock(world.getBlockState(pos), block)) {
            world.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
        }
    }

    static boolean isBlock(BlockState state, Block block) {
        return state.getBlock() == block;
    }

    static InteractionResult checkHoldingWrench(Level world, Player player, Runnable callback) {
        return IBlock.checkHoldingItem(world, player, item -> callback.run(), null, ItemList.WRENCH.get());
    }

    static InteractionResult checkHoldingBrushOrWrench(Level world, Player player, Runnable callback) {
        return IBlock.checkHoldingItem(world, player, item -> callback.run(), null, ItemList.WRENCH.get(), Items.BRUSH.get());
    }

    static <T extends Enum<T> & StringRepresentable> BlockState cycleBlockState(BlockState state, EnumProperty<T> property, Predicate<T> includes) {
        return cycleBlockState(state, property, property.getPossibleValues().stream().filter(includes).collect(Collectors.toList()));
    }

    @SafeVarargs
    static <T extends Enum<T> & StringRepresentable> BlockState cycleBlockState(BlockState state, EnumProperty<T> property, T... includes) {
        return cycleBlockState(state, property, Arrays.asList(includes));
    }

    static <T extends Enum<T> & StringRepresentable> BlockState cycleBlockState(BlockState state, EnumProperty<T> property, List<T> includes) {
        int index = includes.indexOf(IBlock.getStatePropertySafe(state, property));
        if (index < 0 || (index == includes.size() - 1)) index = -1;
        return state.setValue(property, includes.get(index + 1));
    }

    /**
     * @author ZiYueCommentary
     * @since 1.0.0-beta-1
     */
    enum BlockThirdProperty implements StringRepresentable
    {
        LEFT("left"),
        RIGHT("right"),
        BOTH("both");

        final String name;

        BlockThirdProperty(String name) {
            this.name = name;
        }

        @Nonnull
        @Override
        public String getSerializedName() {
            return name;
        }
    }
}
