package ziyue.tjmetro.mod.block.flag;

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
import org.mtr.block.BlockPSDAPGGlassEndBase;
import org.mtr.block.IBlock;
import org.mtr.generated.lang.TranslationProvider;

import static org.mtr.block.BlockPSDAPGDoorBase.END;
import static org.mtr.block.BlockPSDAPGDoorBase.UNLOCKED;
import static org.mtr.block.IBlock.HALF;

/**
 * @since 1.1.1
 */
public interface BlockFlagDoorSingle
{
    boolean isLeft();

    static BlockState getStateForNeighborUpdate(BlockState state, LevelAccessor world, BlockPos pos) {
        final boolean end = world.getBlockState(pos.relative(IBlock.getSideDirection(state).getOpposite())).getBlock() instanceof BlockPSDAPGGlassEndBase;
        return state.setValue(END, end);
    }

    static InteractionResult onUse(BlockState state, Level world, BlockPos pos, Player player) {
        return IBlock.checkHoldingBrush(world, player, () -> {
            final boolean unlocked = IBlock.getStatePropertySafe(state, UNLOCKED);
            for (int y = -1; y <= 1; y++) {
                final BlockState scanState = world.getBlockState(pos.above(y));
                if (state.is(scanState.getBlock())) {
                    world.setBlock(pos.above(y), scanState.setValue(UNLOCKED, !unlocked), 3);
                }
            }
            player.displayClientMessage((unlocked ? TranslationProvider.GUI_MTR_PSD_APG_DOOR_LOCKED : TranslationProvider.GUI_MTR_PSD_APG_DOOR_UNLOCKED).getText(), true);
        });
    }

    static void onBreak(Level world, BlockPos pos, BlockState state, Player player) {
        BlockPos offsetPos = IBlock.getStatePropertySafe(state, HALF) == IBlock.DoubleBlockHalf.UPPER ? pos.below() : pos.above();
        IBlock.playerWillDestroyCreative(world, player, offsetPos);
        world.destroyBlock(pos, false, ((Entity) player));
    }
}
