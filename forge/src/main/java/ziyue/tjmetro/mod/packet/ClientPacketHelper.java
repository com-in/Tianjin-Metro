package ziyue.tjmetro.mod.packet;

import net.minecraft.client.multiplayer.ClientLevel;
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
import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import org.mtr.libraries.it.unimi.dsi.fastutil.longs.LongAVLTreeSet;
import org.mtr.libraries.it.unimi.dsi.fastutil.objects.ObjectImmutableList;
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
import net.minecraft.client.gui.screens.Screen;
import org.mtr.client.MinecraftClientData;
import org.mtr.screen.DashboardListItem;
import ziyue.tjmetro.mod.RegistryClient;
import ziyue.tjmetro.mod.TianjinMetro;
import ziyue.tjmetro.mod.block.*;
import ziyue.tjmetro.mod.block.base.BlockCustomColorBase;
import ziyue.tjmetro.mod.block.base.BlockRailwaySignBase;
import ziyue.tjmetro.mod.screen.*;

import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * @since 1.0.0-beta-1
 */

public final class ClientPacketHelper
{
    public static void openBlockEntityScreen(BlockPos blockPos) {
        getBlockEntity(blockPos, blockEntity -> {
            if (blockEntity instanceof BlockRoadblockSign.BlockEntity entity) {
                openScreen(new RoadblockContentScreen(blockPos, entity.content), screen -> screen instanceof RoadblockContentScreen);
            } else if (blockEntity instanceof BlockCustomColorBase.BlockEntityBase entity) {
                openScreen(new ColorPickerScreen(blockPos, entity), screen -> screen instanceof ColorPickerScreen);
            } else if (blockEntity instanceof BlockRailwaySignWallDouble.BlockEntity) {
                openScreen(new RailwaySignDoubleScreen(blockPos), screen -> screen instanceof RailwaySignDoubleScreen);
            } else if (blockEntity instanceof BlockRailwaySignBase.BlockEntityBase ||
                    blockEntity instanceof BlockStationNameEntranceTianjin.BlockEntity ||
                    blockEntity instanceof BlockStationNamePlate.BlockEntity ||
                    blockEntity instanceof BlockRouteMapBMT.BlockEntity ||
                    blockEntity instanceof BlockStationNameSignTianjin.BlockEntity
            ) {
                openScreen(new RailwaySignScreen(blockPos), screen -> screen instanceof RailwaySignScreen);
            } else if (blockEntity instanceof BlockPIDSTianjin.BlockEntity) {
                openScreen(new PIDSTianjinConfigScreen(blockPos), screen -> screen instanceof PIDSTianjinConfigScreen);
            } else if (blockEntity instanceof BlockStationNavigator.BlockEntity) {
                final ObjectArraySet<DashboardListItem> routes = new ObjectArraySet<>();
                MinecraftClientData.getInstance().simplifiedRoutes.forEach(route ->
                        routes.add(new DashboardListItem(route.getId(), route.getName().split("\\|\\|")[0], route.getColor())));

                final LongAVLTreeSet selectedRoutes = new LongAVLTreeSet(((BlockStationNavigator.BlockEntity) blockEntity).getSelectedRoutes());
                openScreen(new DashboardListSelectorScreen(
                        () -> RegistryClient.sendPacketToServer(new PacketUpdateStationNavigatorConfig(blockPos, selectedRoutes)),
                        new ObjectImmutableList<>(routes),
                        selectedRoutes,
                        false,
                        false,
                        null), screen -> screen instanceof DashboardListSelectorScreen);
            } else {
                TianjinMetro.LOGGER.warn("Unknown block entity data at {}: {}", blockPos.toShortString(), blockEntity);
            }
        });
    }

    public static void openScreen(Screen screenExtension, Predicate<Screen> isInstance) {
        final Minecraft minecraftClient = Minecraft.getInstance();
        if (minecraftClient.screen == null || !isInstance.test(minecraftClient.screen)) {
            minecraftClient.setScreen(screenExtension);
        }
    }

    public static void getBlockEntity(BlockPos blockPos, Consumer<BlockEntity> consumer) {
        final ClientLevel clientWorld = Minecraft.getInstance().level;
        if (clientWorld != null) {
            final BlockEntity blockEntity = clientWorld.getBlockEntity(blockPos);
            if (blockEntity != null) {
                consumer.accept(blockEntity);
            }
        }
    }
}
