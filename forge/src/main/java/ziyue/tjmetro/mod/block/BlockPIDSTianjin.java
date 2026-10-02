package ziyue.tjmetro.mod.block;

import net.minecraft.nbt.CompoundTag;
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
import org.mtr.libraries.it.unimi.dsi.fastutil.longs.LongAVLTreeSet;
import org.mtr.libraries.it.unimi.dsi.fastutil.longs.LongArrayList;
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
import org.mtr.block.BlockPIDSHorizontalBase;
import org.mtr.block.IBlock;
import ziyue.tjmetro.mapping.ConfirmLinkScreenHelper;
import ziyue.tjmetro.mod.BlockEntityTypes;
import ziyue.tjmetro.mod.ItemList;
import ziyue.tjmetro.mod.Registry;
import ziyue.tjmetro.mod.client.ScrollingText;
import ziyue.tjmetro.mod.packet.PacketOpenBlockEntityScreen;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * @author ZiYueCommentary
 * @see BlockEntity
 * @since 1.0.0
 */

public class BlockPIDSTianjin extends BlockPIDSHorizontalBase
{
    public static final Map<Long, Category> CATEGORIES = new HashMap<>();

    public BlockPIDSTianjin() {
        super(BlockBehaviour.Properties.of(), 2);
    }

    @Override
    public @Nonnull InteractionResult useWithoutItem(BlockState state, Level world, BlockPos pos, Player player, BlockHitResult hit) {
        final BlockPos newBlockPos = getBlockPosWithData(world, pos);
        if (!(world.getBlockEntity(newBlockPos) instanceof BlockEntity entity)) return InteractionResult.PASS;
        if (player.isHolding(Items.BRUSH.get()) || player.isHolding(ItemList.WRENCH.get())) {
            return IBlockExtension.checkHoldingBrushOrWrench(world, player, () -> {
                entity.setChanged();
                Registry.sendPacketToClient(((ServerPlayer) player), new PacketOpenBlockEntityScreen(newBlockPos));
            });
        }
        if (entity.advertisement == null) return InteractionResult.PASS;
        final @Nullable String url = entity.advertisement.url;
        if (url != null) {
            ConfirmLinkScreenHelper.open(Minecraft.getInstance().screen, url, false);
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new BlockEntity(blockPos, blockState, false);
    }

    public boolean canStoreData(Level world, BlockPos blockPos) {
        final Direction facing = IBlock.getStatePropertySafe(world, blockPos, BlockStateProperties.HORIZONTAL_FACING);
        return facing == Direction.NORTH || facing == Direction.EAST;
    }

    public BlockPos getBlockPosWithData(Level world, BlockPos blockPos) {
        if (canStoreData(world, blockPos)) {
            return blockPos;
        } else {
            return blockPos.relative(IBlock.getStatePropertySafe(world, blockPos, BlockStateProperties.HORIZONTAL_FACING));
        }
    }

    public static class BlockEntity extends BlockEntityExtension
    {
        protected final LongAVLTreeSet platformIds = new LongAVLTreeSet();
        protected final LongArrayList categories = new LongArrayList();
        protected int displayPage;
        public final ScrollingText scrollingText = new ScrollingText(158F, 47, 4, true);
        public BlockPIDSTianjin.Advertisement advertisement = null;
        public int categoryIndex = 0;
        public int advertisementIndex = 0;
        public final boolean renderSingleFace;

        public static final String PLATFORM_IDS_ID = "platform_ids";
        public static final String DISPLAY_PAGE_ID = "display_page";
        public static final String CATEGORIES_ID = "categories";

        public BlockEntity(BlockPos pos, BlockState state, boolean renderSingleFace) {
            super(renderSingleFace ? BlockEntityTypes.PIDS_TIANJIN_SINGLE.get() : BlockEntityTypes.PIDS_TIANJIN.get(), pos, state);
            this.renderSingleFace = renderSingleFace;
            categories.add("tjmetro".hashCode());
        }

        @Override
        public void readNbt(CompoundTag compoundTag) {
            platformIds.clear();
            final long[] platformIdsArray = compoundTag.getLongArray(PLATFORM_IDS_ID);
            for (final long platformId : platformIdsArray) {
                platformIds.add(platformId);
            }

            categories.clear();
            final long[] categoriesArray = compoundTag.getLongArray(CATEGORIES_ID);
            for (final long category : categoriesArray) {
                categories.add(category);
            }

            displayPage = compoundTag.getInt(DISPLAY_PAGE_ID);
        }

        @Override
        public void writeNbt(CompoundTag compoundTag) {
            compoundTag.putLongArray(PLATFORM_IDS_ID, new ArrayList<>(platformIds));
            compoundTag.putLongArray(CATEGORIES_ID, new ArrayList<>(categories));
            compoundTag.putInt(DISPLAY_PAGE_ID, displayPage);
        }

        public LongArrayList getCategories() {
            return categories;
        }

        public LongAVLTreeSet getPlatformIds() {
            return platformIds;
        }

        public int getDisplayPage() {
            return displayPage;
        }

        public void setData(LongArrayList categories) {
            this.categories.clear();
            this.categories.addAll(categories);
            setChanged();
        }

        public void setData(LongAVLTreeSet platformIds, int displayPage) {
            this.platformIds.clear();
            this.platformIds.addAll(platformIds);
            this.displayPage = displayPage;
            setChanged();
        }
    }

    public record Advertisement(ResourceLocation image, MutableComponent text, @Nullable String url)
    {
    }

    public static class Category extends ArrayList<Advertisement>
    {
        public final long id;
        public final Color color;
        public final MutableComponent name;
        public final MutableComponent description;

        public Category(long id, Color color, MutableComponent name, MutableComponent description) {
            this.id = id;
            this.color = color;
            this.name = name;
            this.description = description;
        }
    }
}
