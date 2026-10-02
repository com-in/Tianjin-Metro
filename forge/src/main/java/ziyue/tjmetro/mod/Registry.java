package ziyue.tjmetro.mod;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.mtr.packet.PacketBufferReceiver;
import org.mtr.packet.PacketHandler;
import org.mtr.registry.RegistryServer;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Owns the {@code tjmetro} registries. MTR 4.1's {@link RegistryServer} is hard-wired to the {@code mtr}
 * namespace (it registers into MTR's own {@code DeferredRegister}s), so blocks, items, block entities and
 * the creative tab are declared here instead. Only the generic packet framework and item-group delivery
 * come from MTR, since those are the parts MTR explicitly exposes to add-ons.
 *
 * @since 1.0.0-beta-1
 */

public final class Registry
{
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(Registries.BLOCK, Reference.MOD_ID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(Registries.ITEM, Reference.MOD_ID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, Reference.MOD_ID);
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Reference.MOD_ID);
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(Registries.ENTITY_TYPE, Reference.MOD_ID);

    /**
     * Kept for the legacy {@code REGISTRY_TABS.init()} call site; the tab is registered eagerly above.
     */
    public static final TabsRegistryHolder REGISTRY_TABS = new TabsRegistryHolder();

    public static final class TabsRegistryHolder
    {
        public void init() {
        }
    }

    public static void register(IEventBus modEventBus) {
        BLOCKS.register(modEventBus);
        ITEMS.register(modEventBus);
        BLOCK_ENTITY_TYPES.register(modEventBus);
        CREATIVE_MODE_TABS.register(modEventBus);
        ENTITY_TYPES.register(modEventBus);
    }

    public static CreativeModeTabHolder createCreativeModeTabHolder(String id, Supplier<ItemStack> icon) {
        final CreativeModeTabHolder holder = new CreativeModeTabHolder(id);
        CREATIVE_MODE_TABS.register(id, () -> CreativeModeTab.builder()
                .title(Component.translatable("itemGroup." + Reference.MOD_ID + "." + id))
                .icon(icon)
                .displayItems((parameters, output) -> holder.getEntries().forEach(item -> output.accept(item.get())))
                .build());
        return holder;
    }

    public static BlockRegistryObject registerBlock(String id, Supplier<Block> supplier) {
        return new BlockRegistryObject(BLOCKS.register(id, supplier));
    }

    public static ItemRegistryObject registerItem(String id, Function<Item.Properties, Item> function, CreativeModeTabHolder creativeModeTab) {
        return new ItemRegistryObject(registerItemInternal(id, function, creativeModeTab));
    }

    public static BlockRegistryObject registerBlockWithBlockItem(String id, Supplier<Block> block, CreativeModeTabHolder creativeModeTab) {
        return registerBlockWithBlockItem(id, block, ziyue.tjmetro.mod.item.BlockItemExtension::new, creativeModeTab);
    }

    public static BlockRegistryObject registerBlockWithBlockItem(String id, Supplier<Block> block, BiFunction<Block, Item.Properties, BlockItem> function, CreativeModeTabHolder creativeModeTab) {
        final DeferredHolder<Block, Block> blockHolder = BLOCKS.register(id, block);
        registerItemInternal(id, properties -> function.apply(blockHolder.get(), properties), creativeModeTab);
        return new BlockRegistryObject(blockHolder);
    }

    private static DeferredHolder<Item, Item> registerItemInternal(String id, Function<Item.Properties, Item> function, CreativeModeTabHolder creativeModeTab) {
        final DeferredHolder<Item, Item> itemHolder = ITEMS.register(id, () -> function.apply(new Item.Properties()));
        if (creativeModeTab != null) creativeModeTab.addEntry(itemHolder::get);
        return itemHolder;
    }

    public static <T extends BlockEntity> BlockEntityTypeRegistryObject<T> registerBlockEntityType(String id, BiFunction<BlockPos, BlockState, T> function, Supplier<Block> blockSupplier) {
        final DeferredHolder<BlockEntityType<?>, BlockEntityType<?>> holder = BLOCK_ENTITY_TYPES.register(id, () -> BlockEntityType.Builder.of((pos, state) -> function.apply(pos, state), blockSupplier.get()).build(null));
        return new BlockEntityTypeRegistryObject<>(() -> {
            //noinspection unchecked
            return (BlockEntityType<T>) holder.get();
        });
    }

    public static <T extends Entity> EntityTypeRegistryObject<T> registerEntityType(String id, BiFunction<EntityType<?>, Level, T> function, float width, float height) {
        final DeferredHolder<EntityType<?>, EntityType<?>> holder = ENTITY_TYPES.register(id, () -> EntityType.Builder.of((entityType, level) -> function.apply(entityType, level), MobCategory.MISC).sized(width, height).build(id));
        return new EntityTypeRegistryObject<>(() -> {
            //noinspection unchecked
            return (EntityType<T>) holder.get();
        });
    }

    public static <T extends PacketHandler> void registerPacket(Class<T> classObject, Function<PacketBufferReceiver, T> getInstance) {
        RegistryServer.registerPacket(classObject, getInstance);
    }

    public static <T extends PacketHandler> void sendPacketToClient(ServerPlayer serverPlayerEntity, T data) {
        RegistryServer.sendPacketToClient(serverPlayerEntity, data);
    }

    public static void init() {
        RegistryServer.init();
    }
}
