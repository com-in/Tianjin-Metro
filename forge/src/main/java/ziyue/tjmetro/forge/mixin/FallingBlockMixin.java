package ziyue.tjmetro.forge.mixin;

import net.minecraft.util.RandomSource;
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
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ziyue.tjmetro.mapping.BooleanGameRule;
import ziyue.tjmetro.mod.GameRules;

#if MC_VERSION <= "11605"
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.FallingBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerLevel;
import java.util.Random;

@Mixin(FallingBlock.class)
public abstract class FallingBlockMixin extends Block
{
    public FallingBlockMixin(Properties p_i48440_1_) {
        super(p_i48440_1_);
    }

    @Inject(at = @At("HEAD"), method = "tick", cancellable = true)
    private void beforeTick(BlockState p_225534_1_, ServerLevel world, BlockPos p_225534_3_, Random p_225534_4_, CallbackInfo ci) {
        if (BooleanGameRule.getValue(world, GameRules.NO_FALLING_BLOCK)) {
            ci.cancel();
        }
    }
}
#else
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Fallable;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Random;

/**
 * @author ZiYueCommentary
 * @since 1.0.0-beta-2
 */

@Mixin(FallingBlock.class)
public abstract class FallingBlockMixin extends Block implements Fallable
{
    public FallingBlockMixin(Properties p_49795_) {
        super(p_49795_);
    }

    @Inject(at = @At("HEAD"), method = "tick", cancellable = true)
#if MC_VERSION >= "11902"
    private void beforeTick(BlockState p_221124_, ServerLevel world, BlockPos p_221126_, net.minecraft.util.RandomSource p_221127_, CallbackInfo ci)
#else
    private void beforeTick(BlockState p_53216_, ServerLevel world, BlockPos p_53218_, Random p_53219_, CallbackInfo ci)
#endif {
        if (BooleanGameRule.getValue(world, GameRules.NO_FALLING_BLOCK)) {
            ci.cancel();
        }
    }
}
#endif
