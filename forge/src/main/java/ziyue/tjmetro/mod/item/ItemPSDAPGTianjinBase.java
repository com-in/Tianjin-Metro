package ziyue.tjmetro.mod.item;

import net.minecraft.world.item.context.UseOnContext;
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
import net.minecraft.world.item.Item;
import net.minecraft.world.item.TooltipFlag;
import org.mtr.registry.ObjectHolder;
import org.mtr.block.*;
import ziyue.tjmetro.mod.block.*;
import ziyue.tjmetro.mod.block.flag.*;
import ziyue.tjmetro.mod.BlockRegistryObject;
import ziyue.tjmetro.mod.IGuiExtension;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

import static org.mtr.item.ItemPSDAPGBase.blocksNotReplaceable;

/**
 * @author ZiYueCommentary
 * @see BlockAPGDoorTianjin
 * @see BlockAPGGlassEndTianjin
 * @see BlockAPGGlassTianjin
 * @see BlockAPGDoorTianjinBMT
 * @see BlockAPGGlassEndTianjinBMT
 * @see BlockAPGGlassTianjinBMT
 * @see BlockPSDDoorTianjin
 * @see BlockPSDGlassEndTianjin
 * @see BlockPSDGlassTianjin
 * @see BlockPSDDoorTianjinBMT
 * @see BlockPSDGlassEndTianjinBMT
 * @see BlockPSDGlassTianjinBMT
 * @since 1.0.0-beta-1
 */

public class ItemPSDAPGTianjinBase extends Item implements IBlock
{
    public final Block block;

    public ItemPSDAPGTianjinBase(BlockRegistryObject block, Item.Properties settings) {
        super(settings);
        this.block = block.get();
    }

    @Nonnull
    @Override
    public InteractionResult useOn(UseOnContext context) {
        final int horizontalBlocks = block instanceof BlockPSDAPGDoorBase && !(block instanceof BlockFlagDoorSingle) ? 2 : 1;
        if (blocksNotReplaceable(context, horizontalBlocks, isAPG() ? 2 : 3, this.block)) return InteractionResult.FAIL;

        final Level world = context.getLevel();
        final Direction playerFacing = context.getHorizontalDirection();
        final BlockPos pos = context.getClickedPos().relative(context.getClickedFace());

        for (int x = 0; x < horizontalBlocks; x++) {
            final BlockPos newPos = pos.relative(playerFacing.getClockWise(), x);

            for (int y = 0; y < 2; y++) {
                final BlockState state = this.block.defaultBlockState().setValue(BlockStateProperties.HORIZONTAL_FACING, playerFacing).setValue(HALF, y == 1 ? DoubleBlockHalf.UPPER : DoubleBlockHalf.LOWER);
                if (block instanceof BlockFlagDoorSingle doorSingle) {
                    BlockState neighborState = state.setValue(SIDE, doorSingle.isLeft() ? EnumSide.LEFT : EnumSide.RIGHT);
                    world.setBlock(newPos.above(y), neighborState, 3);
                } else if (block instanceof BlockPSDAPGDoorBase) {
                    BlockState neighborState = state.setValue(SIDE, x == 0 ? EnumSide.LEFT : EnumSide.RIGHT);
                    world.setBlock(newPos.above(y), neighborState, 3);
                } else {
                    world.setBlock(newPos.above(y), state.setValue(SIDE_EXTENDED, EnumSide.SINGLE), 3);
                }
            }
            if (this.block instanceof BlockFlagPSDTianjin) {
                world.setBlock(newPos.above(2), BlockPSDTopTianjin.getActualState((world), newPos.above(2), this.block instanceof BlockFlagPSDTianjinJinjing).setValue(BlockPSDTopTianjin.STYLE, BlockPSDTopTianjin.EnumPSDType.STATION_NAME), 3);
            } else if (this.block instanceof BlockFlagPSDTianjinBMT) {
                world.setBlock(newPos.above(2), BlockPSDTopTianjinBMT.getActualState((world), newPos.above(2)).setValue(BlockPSDTopTianjinBMT.STYLE, BlockPSDTopTianjinBMT.EnumPSDType.BMT), 3);
            }
        }

        context.getItemInHand().shrink(1);
        return InteractionResult.SUCCESS;
    }

    public boolean isAPG() {
        return this.block instanceof BlockFlagAPGTianjin || this.block instanceof BlockFlagAPGTianjinBMT || this.block instanceof BlockFlagAPGTianjinTRT;
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        if (this.block instanceof BlockPSDAPGDoorBase) {
            tooltip.add(Component.translatable("tooltip.mtr.psd_apg_door").withStyle(ChatFormatting.GRAY));
        } else if (this.block instanceof BlockPSDAPGGlassEndBase) {
            tooltip.add(Component.translatable("tooltip.mtr.psd_apg_glass_end").withStyle(ChatFormatting.GRAY));
        } else if (this.block instanceof BlockPSDAPGGlassBase) {
            tooltip.add(Component.translatable("tooltip.mtr.psd_apg_glass").withStyle(ChatFormatting.GRAY));
        }
        if (this.block instanceof BlockFlagPSDTianjin || this.block instanceof BlockFlagAPGTianjinBMT) {
            //noinspection unchecked
            IGuiExtension.addHoldShiftTooltip((List<MutableComponent>) (List<?>) tooltip, Component.translatable("tooltip.tjmetro.psd_apg_tianjin"));
        }
    }

    @Nonnull
    @Override
    public String getDescriptionId() {
        if (this.block instanceof BlockFlagDoorSingle) {
            return this.block.getDescriptionId();
        } else if (this.block instanceof BlockFlagPSDTianjinJinjing) {
            return "block.tjmetro.psd_tianjin_jinjing";
        } else if (this.block instanceof BlockFlagPSDTianjin) {
            return "block.tjmetro.psd_tianjin";
        } else if (this.block instanceof BlockFlagPSDTianjinBMT) {
            return "block.tjmetro.psd_tianjin_bmt";
        } else if (this.block instanceof BlockFlagAPGTianjinJinjing) {
            return "block.tjmetro.apg_tianjin_jinjing";
        } else if (this.block instanceof BlockFlagAPGTianjin) {
            return "block.tjmetro.apg_tianjin";
        } else if (this.block instanceof BlockFlagAPGTianjinBMT) {
            return "block.tjmetro.apg_tianjin_bmt";
        } else {
            return "block.tjmetro.apg_tianjin_trt";
        }
    }
}
