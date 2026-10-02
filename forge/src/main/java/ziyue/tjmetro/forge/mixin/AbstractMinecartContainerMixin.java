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
/**
 * @author ZiYueCommentary
 * @see ContainerAccessor
 * @since 1.0.0-beta-2
 */

#if MC_VERSION >= "11902"

import net.minecraft.core.NonNullList;
import net.minecraft.world.Container;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.entity.vehicle.AbstractMinecartContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import ziyue.tjmetro.mapping.ContainerAccessor;

@Mixin(AbstractMinecartContainer.class)
public abstract class AbstractMinecartContainerMixin extends AbstractMinecart implements Container, MenuProvider, ContainerAccessor
{
    @Shadow
    private NonNullList<ItemStack> itemStacks;

    protected AbstractMinecartContainerMixin(EntityType<?> p_38213_, Level p_38214_) {
        super(p_38213_, p_38214_);
    }

    @Override
    public NonNullList<ItemStack> tianjin_Metro$getItemStacks() {
        return this.itemStacks;
    }
}

#elif MC_VERSION >= "11701"

import net.minecraft.core.NonNullList;
import net.minecraft.world.Container;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.entity.vehicle.AbstractMinecartContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import ziyue.tjmetro.mapping.ContainerAccessor;

@Mixin(AbstractMinecartContainer.class)
public abstract class AbstractMinecartContainerMixin extends AbstractMinecart implements Container, MenuProvider, ContainerAccessor
{
    @Shadow
    private NonNullList<ItemStack> itemStacks;

    protected AbstractMinecartContainerMixin(EntityType<?> p_38213_, Level p_38214_) {
        super(p_38213_, p_38214_);
    }

    @Override
    public NonNullList<ItemStack> tianjin_Metro$getItemStacks() {
        return this.itemStacks;
    }
}

#else

import net.minecraft.entity.EntityType;
import net.minecraft.entity.item.minecart.AbstractMinecartEntity;
import net.minecraft.entity.item.minecart.ContainerMinecartEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.world.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import ziyue.tjmetro.mapping.ContainerAccessor;

@Mixin(ContainerMinecartEntity.class)
public abstract class AbstractMinecartContainerMixin extends AbstractMinecartEntity implements IInventory, INamedContainerProvider, ContainerAccessor
{
    @Shadow
    private NonNullList<ItemStack> itemStacks;

    protected AbstractMinecartContainerMixin(EntityType<?> p_i48538_1_, Level p_i48538_2_) {
        super(p_i48538_1_, p_i48538_2_);
    }

    @Override
    public NonNullList<ItemStack> tianjin_Metro$getItemStacks() {
        return this.itemStacks;
    }
}

#endif