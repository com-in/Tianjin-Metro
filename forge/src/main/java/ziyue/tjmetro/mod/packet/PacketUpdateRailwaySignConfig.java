package ziyue.tjmetro.mod.packet;

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
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import org.mtr.packet.PacketHandler;
import org.mtr.packet.PacketBufferReceiver;
import org.mtr.packet.PacketBufferSender;
import ziyue.tjmetro.mod.block.BlockRouteMapBMT;
import ziyue.tjmetro.mod.block.BlockStationNameEntranceTianjin;
import ziyue.tjmetro.mod.block.BlockStationNamePlate;
import ziyue.tjmetro.mod.block.BlockStationNameSignTianjin;
import ziyue.tjmetro.mod.block.base.BlockRailwaySignBase;

/**
 * @author ZiYueCommentary
 * @see ziyue.tjmetro.mod.block.BlockRailwaySignTianjinBMT.BlockEntity
 * @see ziyue.tjmetro.mod.block.BlockRailwaySignTianjin.BlockEntity
 * @see ziyue.tjmetro.mod.block.BlockRailwaySignWall.BlockEntity
 * @see ziyue.tjmetro.mod.block.BlockRailwaySignWallBig.BlockEntity
 * @since 1.0.0-beta-1
 */

public final class PacketUpdateRailwaySignConfig extends PacketHandler
{
    private final BlockPos blockPos;
    private final LongAVLTreeSet selectedIds;
    private final String[] signIds;

    public PacketUpdateRailwaySignConfig(PacketBufferReceiver packetBufferReceiver) {
        blockPos = BlockPos.of(packetBufferReceiver.readLong());
        final int selectedIdsLength = packetBufferReceiver.readInt();
        selectedIds = new LongAVLTreeSet();
        for (int i = 0; i < selectedIdsLength; i++) {
            selectedIds.add(packetBufferReceiver.readLong());
        }
        final int signLength = packetBufferReceiver.readInt();
        signIds = new String[signLength];
        for (int i = 0; i < signLength; i++) {
            final String signId = packetBufferReceiver.readString();
            signIds[i] = signId.isEmpty() ? null : signId;
        }
    }

    public PacketUpdateRailwaySignConfig(BlockPos blockPos, LongAVLTreeSet selectedIds, String[] signIds) {
        this.blockPos = blockPos;
        this.selectedIds = selectedIds;
        this.signIds = signIds;
    }

    @Override
    public void write(PacketBufferSender packetBufferSender) {
        packetBufferSender.writeLong(blockPos.asLong());
        packetBufferSender.writeInt(selectedIds.size());
        selectedIds.forEach(packetBufferSender::writeLong);
        packetBufferSender.writeInt(signIds.length);
        for (final String signType : signIds) {
            packetBufferSender.writeString(signType == null ? "" : signType);
        }
    }

    @Override
    public void runServer(MinecraftServer minecraftServer, ServerPlayer serverPlayerEntity) {
        final BlockEntity entity = serverPlayerEntity.level().getBlockEntity(blockPos);
        if (entity != null) {
            if (entity instanceof BlockRailwaySignBase.BlockEntityBase entity1) {
                entity1.setData(selectedIds, signIds);
            } else if (entity instanceof BlockStationNameEntranceTianjin.BlockEntity entity1) {
                final long platformId = selectedIds.isEmpty() ? -1 : (long) selectedIds.toArray()[0];
                entity1.setData(platformId);
            } else if (entity instanceof BlockStationNamePlate.BlockEntity entity1) {
                final long platformId = selectedIds.isEmpty() ? 0 : (long) selectedIds.toArray()[0];
                entity1.setPlatformId(platformId);
            } else if (entity instanceof BlockRouteMapBMT.BlockEntity entity1) {
                final long platformId = selectedIds.isEmpty() ? 0 : (long) selectedIds.toArray()[0];
                entity1.setPlatformId(platformId);
            } else if (entity instanceof BlockStationNameSignTianjin.BlockEntity entity1) {
                final long platformId = selectedIds.isEmpty() ? 0 : (long) selectedIds.toArray()[0];
                entity1.setData(platformId);
            }
        }
    }
}
