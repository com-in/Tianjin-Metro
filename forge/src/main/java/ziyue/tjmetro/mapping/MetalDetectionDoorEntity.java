package ziyue.tjmetro.mapping;

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
 * An entity for GUI of Metal Detection Door. This entity is a minecart-with-chest.
 *
 * @author ZiYueCommentary
 * @see BlockMetalDetectionDoor
 * @since 1.0.0-beta-2
 */

#if MC_VERSION >= "11902"

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.MinecartChest;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.Level;
import ziyue.tjmetro.mod.block.BlockMetalDetectionDoor;

import javax.annotation.Nullable;

public class MetalDetectionDoorEntity extends MinecartChest
{
    public final BlockMetalDetectionDoor.BlockEntity blockEntity;

    public MetalDetectionDoorEntity(Level world, BlockPos blockPos, BlockMetalDetectionDoor.BlockEntity blockEntity) {
        super(world, blockPos.getX(), -1, blockPos.getZ());
        this.blockEntity = blockEntity;
        for (int i = 0; i < blockEntity.inventory.size(); i++) {
            this.getItemStacks().set(i, blockEntity.inventory.get(i));
        }
    }

    @Override
    public AbstractContainerMenu createMenu(int syncId, Inventory playerInventory) {
        return new ChestMenu(MenuType.GENERIC_9x1, syncId, playerInventory, this, 1) {
            @Override
            public void removed(Player player) {
                super.removed(player);
                final MetalDetectionDoorEntity entity = (MetalDetectionDoorEntity) this.getContainer();
                entity.blockEntity.setData(new DefaultedItemStackList(entity.getItemStacks()));
                entity.getItemStacks().clear();
                entity.kill();
            }
        };
    }

    @Override
    public boolean isChestVehicleStillValid(Player p_219955_) {
        return true;
    }

    @Override
    public boolean isNoGravity() {
        return true;
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }

    @Nullable
    @Override
    public Component getCustomName() {
        return Component.translatable("gui.tjmetro.metal_detection_door");
    }
}

#elif MC_VERSION >= "11701"

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.MinecartChest;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.Level;
import ziyue.tjmetro.mod.block.BlockMetalDetectionDoor;

import javax.annotation.Nullable;

public class MetalDetectionDoorEntity extends MinecartChest
{
    public final BlockMetalDetectionDoor.BlockEntity blockEntity;

    public MetalDetectionDoorEntity(Level world, BlockPos blockPos, BlockMetalDetectionDoor.BlockEntity blockEntity) {
        super(world, blockPos.getX(), -1, blockPos.getZ());
        this.blockEntity = blockEntity;
        for (int i = 0; i < blockEntity.inventory.size(); i++) {
            ((ContainerAccessor) this).tianjin_Metro$getItemStacks().set(i, blockEntity.inventory.get(i));
        }
    }

    @Override
    public AbstractContainerMenu createMenu(int syncId, Inventory playerInventory) {
        return new ChestMenu(MenuType.GENERIC_9x1, syncId, playerInventory, this, 1)
        {
            @Override
            public void removed(Player player) {
                super.removed(player);
                final MetalDetectionDoorEntity entity = (MetalDetectionDoorEntity) this.getContainer();
                entity.blockEntity.setData(new DefaultedItemStackList(((ContainerAccessor) entity).tianjin_Metro$getItemStacks()));
                ((ContainerAccessor) entity).tianjin_Metro$getItemStacks().clear();
                entity.kill();
            }
        };
    }

    @Override
    public boolean stillValid(Player p_38230_) {
        return true;
    }

    @Override
    public boolean isNoGravity() {
        return true;
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }

    @Nullable
    @Override
    public Component getCustomName() {
        return new TranslatableComponent("gui.tjmetro.metal_detection_door");
    }
}

#else

import net.minecraft.entity.item.minecart.ChestMinecartEntity;
import net.minecraft.entity.player.Player;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ChestContainer;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import ziyue.tjmetro.mod.block.BlockMetalDetectionDoor;

import javax.annotation.Nullable;

public class MetalDetectionDoorEntity extends ChestMinecartEntity
{
    public final BlockMetalDetectionDoor.BlockEntity blockEntity;

    public MetalDetectionDoorEntity(Level world, BlockPos blockPos, BlockMetalDetectionDoor.BlockEntity blockEntity) {
        super(world, blockPos.getX(), -1, blockPos.getZ());
        this.blockEntity = blockEntity;
        for (int i = 0; i < blockEntity.inventory.size(); i++) {
            ((ContainerAccessor) this).tianjin_Metro$getItemStacks().set(i, blockEntity.inventory.get(i));
        }
    }

    @Override
    public Container createMenu(int syncId, PlayerInventory playerInventory) {
        return new ChestContainer(ContainerType.GENERIC_9x1, syncId, playerInventory, this, 1)
        {
            @Override
            public void removed(Player player) {
                super.removed(player);
                final MetalDetectionDoorEntity entity = (MetalDetectionDoorEntity) this.getContainer();
                entity.blockEntity.setData(new DefaultedItemStackList(((ContainerAccessor) entity).tianjin_Metro$getItemStacks()));
                ((ContainerAccessor) entity).tianjin_Metro$getItemStacks().clear();
                entity.kill();
            }
        };
    }

    @Override
    public boolean stillValid(Player p_38230_) {
        return true;
    }

    @Override
    public boolean isNoGravity() {
        return true;
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }

    @Nullable
    @Override
    public ITextComponent getCustomName() {
        return new TranslationTextComponent("gui.tjmetro.metal_detection_door");
    }
}

#endif