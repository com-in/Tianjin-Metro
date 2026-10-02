package ziyue.tjmetro.forge.mixin;

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
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ziyue.tjmetro.mod.BlockList;
import ziyue.tjmetro.mod.GameRules;
import ziyue.tjmetro.mod.block.BlockSmokeAlarm;
import ziyue.tjmetro.mod.block.IBlockExtension;

#if MC_VERSION > "11605"
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunk;

#if MC_VERSION > "11701"
import net.minecraft.core.Registry;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.blending.BlendingData;
import org.jetbrains.annotations.Nullable;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.UpgradeData;
#endif

/**
 * @author ZiYueCommentary
 * @since 1.1.0
 */

@Mixin(LevelChunk.class)
#if MC_VERSION > "11701"
public abstract class LevelChunkMixin extends ChunkAccess
#else
public abstract class LevelChunkMixin implements ChunkAccess
#endif
{
    @Shadow
    public abstract Level getLevel();

#if MC_VERSION > "11701"
    public LevelChunkMixin(ChunkPos p_187621_, UpgradeData p_187622_, LevelHeightAccessor p_187623_, Registry<Biome> p_187624_, long p_187625_, @Nullable LevelChunkSection[] p_187626_, @Nullable BlendingData p_187627_) {
        super(p_187621_, p_187622_, p_187623_, p_187624_, p_187625_, p_187626_, p_187627_);
    }
#endif

#else

import net.minecraft.world.chunk.Chunk;
import net.minecraft.block.BlockState;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Level;

@Mixin(Chunk.class)
public abstract class LevelChunkMixin
{

    @Shadow
    public abstract Level getLevel();

    @Shadow
    public abstract BlockState getBlockState(BlockPos par1);

    @Shadow
    public abstract BlockState setBlockState(BlockPos pos, BlockState state, boolean moved);

#endif

    /**
     * Listening block changed and activate nearby smoke alarms.
     *
     * @see BlockSmokeAlarm
     */
    @Inject(at = @At("TAIL"), method = "setBlockState")
    private void afterSetBlockState(BlockPos pos, BlockState state, boolean p_62867_, CallbackInfoReturnable<BlockState> cir) {
        if (!state.is(BlockTags.FIRE) && !state.is(BlockTags.CAMPFIRES)) return;
        int radius = this.getLevel().getGameRules().getInt(GameRules.SMOKE_ALARM_RANGE.data);
        for (int x = pos.getX() - radius; x <= pos.getX() + radius; x++) {
            for (int y = pos.getY(); y <= pos.getY() + radius; y++) {
                for (int z = pos.getZ() - radius; z <= pos.getZ() + radius; z++) {
                    BlockPos currentPos = new BlockPos(x, y, z);
                    BlockState blockState = this.getBlockState(currentPos);
                    if (IBlockExtension.isBlock(blockState, BlockList.SMOKE_ALARM.get())) {
                        // This is pretty dangerous...
                        this.setBlockState(currentPos, blockState.setValue(BlockSmokeAlarm.ACTIVATED, true), true);
                        this.getLevel().neighborChanged(currentPos.above(2), blockState.getBlock(), currentPos.below());
                    }
                }
            }
        }
    }
}
