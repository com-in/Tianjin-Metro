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
import org.mtr.block.BlockPoleCheckBase;
import org.mtr.block.BlockRailwaySignPole;
import org.mtr.block.IBlock;
import org.mtr.generated.lang.TranslationProvider;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * @author ZiYueCommentary
 * @see BlockRailwaySignPole
 * @since 1.0.0-beta-1
 */

public class BlockRailwaySignTianjinPole extends BlockPoleCheckBase
{
    public static final IntegerProperty TYPE = IntegerProperty.create("type", 0, 4);

    public BlockRailwaySignTianjinPole() {
        this(BlockBehaviour.Properties.of());
    }

    public BlockRailwaySignTianjinPole(BlockBehaviour.Properties blockSettings) {
        super(blockSettings);
    }

    @Override
    protected BlockState placeWithState(BlockState stateBelow) {
        final int type;
        final Block block = stateBelow.getBlock();
        if (block instanceof BlockRailwaySignTianjin sign) {
            type = (sign.length + (sign.isOdd ? 2 : 0)) % 4;
        } else if (block instanceof BlockPIDSTianjin) {
            type = 4;
        }else {
            type = IBlock.getStatePropertySafe(stateBelow, TYPE);
        }
        return super.placeWithState(stateBelow).setValue(TYPE, type);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(TYPE);
        builder.add(BlockStateProperties.HORIZONTAL_FACING);
    }

    @Nonnull
    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        final Direction facing = IBlock.getStatePropertySafe(state, BlockStateProperties.HORIZONTAL_FACING);
        return switch (IBlock.getStatePropertySafe(state, TYPE)) {
            case 0 -> IBlock.getVoxelShapeByDirection(3, 0, 7.5, 4, 16, 8.5, facing);
            case 1 -> IBlock.getVoxelShapeByDirection(15, 0, 7.5, 16, 16, 8.5, facing);
            case 2 -> IBlock.getVoxelShapeByDirection(11, 0, 7.5, 12, 16, 8.5, facing);
            case 3 -> IBlock.getVoxelShapeByDirection(7, 0, 7.5, 8, 16, 8.5, facing);
            case 4 -> IBlock.getVoxelShapeByDirection(7.5, 0, 7.5, 8.5, 16, 8.5, facing);
            default -> Shapes.block();
        };
    }

    @Override
    protected boolean isBlock(BlockBehaviour block) {
        return block instanceof BlockRailwaySignTianjin && (((BlockRailwaySignTianjin) block).length > 0) || block instanceof BlockRailwaySignTianjinPole || block instanceof BlockPIDSTianjin;
    }

    @Override
    protected Component getTooltipBlockText() {
        return TranslationProvider.BLOCK_MTR_RAILWAY_SIGN.getText();
    }
}
