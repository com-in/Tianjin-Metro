package ziyue.tjmetro.mod;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import org.mtr.MTRClient;
import org.mtr.packet.PacketHandler;
import org.mtr.registry.ObjectHolder;
import ziyue.tjmetro.mod.block.base.BlockCustomColorBase;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Thin facade over the MTR 4.1 native client registry ({@link org.mtr.registry.RegistryClient}).
 *
 * <p>MTR 4.1 has no item colour or entity renderer registry, so those two are buffered here and
 * flushed from the matching NeoForge client events in {@code MainNeoForgeClient}.
 *
 * @since 1.0.0-beta-1
 */

public final class RegistryClient
{
    public static final RegistryClientHolder REGISTRY_CLIENT = new RegistryClientHolder();

    private static final List<Consumer<EntityRenderersEvent.RegisterRenderers>> ENTITY_RENDERER_REGISTRATIONS = new ArrayList<>();
    private static final List<ItemColorRegistration> ITEM_COLOR_REGISTRATIONS = new ArrayList<>();

    public static void registerRenderShape(RenderType renderLayer, BlockRegistryObject block) {
        org.mtr.registry.RegistryClient.registerBlockRenderType(renderLayer, new ObjectHolder<Block>(block.holder));
    }

    public static void registerBlockStationColor(BlockRegistryObject... blocks) {
        for (final BlockRegistryObject block : blocks) {
            org.mtr.registry.RegistryClient.registerBlockColors((state, world, pos, tintIndex) -> MTRClient.getStationColor(pos), new ObjectHolder<Block>(block.holder));
        }
    }

    public static void registerItemCustomColor(int color, BlockRegistryObject block, String blockId) {
        ITEM_COLOR_REGISTRATIONS.add(new ItemColorRegistration(color, block));
    }

    public static void registerBlockCustomColor(BlockRegistryObject... blocks) {
        for (final BlockRegistryObject block : blocks) {
            org.mtr.registry.RegistryClient.registerBlockColors((state, world, pos, tintIndex) -> {
                try {
                    if (world.getBlockEntity(pos) instanceof BlockCustomColorBase.BlockEntityBase entity) {
                        return entity.color == -1 ? entity.getDefaultColor(pos) : entity.color;
                    }
                } catch (Exception ignored) {
                }
                return 8355711;
            }, new ObjectHolder<Block>(block.holder));
        }
    }

    public static <T extends BlockEntity> void registerBlockEntityRenderer(BlockEntityTypeRegistryObject<T> blockEntityType, BlockEntityRendererProvider<T> rendererInstance) {
        org.mtr.registry.RegistryClient.registerBlockEntityRenderer(new ObjectHolder<BlockEntityType<T>>(blockEntityType.holder), rendererInstance);
    }

    public static <T extends Entity> void registerEntityRenderer(EntityTypeRegistryObject<T> entityType, Function<EntityRendererProvider.Context, EntityRenderer<T>> rendererInstance) {
        ENTITY_RENDERER_REGISTRATIONS.add(event -> event.registerEntityRenderer(entityType.get(), rendererInstance::apply));
    }

    public static <T extends PacketHandler> void sendPacketToServer(T data) {
        org.mtr.registry.RegistryClient.sendPacketToServer(data);
    }

    public static void flushItemColors(RegisterColorHandlersEvent.Item event) {
        for (final ItemColorRegistration registration : ITEM_COLOR_REGISTRATIONS) {
            final Item item = registration.block().get().asItem();
            event.register((stack, index) -> registration.color(), item);
        }
    }

    public static void flushEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        ENTITY_RENDERER_REGISTRATIONS.forEach(registration -> registration.accept(event));
    }

    public static final class RegistryClientHolder
    {
        public void init() {
            // Render types, block colours and block entity renderers are handed to MTR 4.1 directly.
        }
    }

    private record ItemColorRegistration(int color, BlockRegistryObject block) { }
}
