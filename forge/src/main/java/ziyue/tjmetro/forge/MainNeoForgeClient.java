package ziyue.tjmetro.forge;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import ziyue.tjmetro.mod.RegistryClient;
import ziyue.tjmetro.mod.config.ConfigClient;

public class MainNeoForgeClient
{
    public static void registerConfigMenu() {
        ModLoadingContext.get().registerExtensionPoint(IConfigScreenFactory.class,
                () -> (container, parent) -> ConfigClient.getConfigScreen(parent));
    }

    public static void registerClientEvents(IEventBus modEventBus) {
        modEventBus.addListener(RegistryClient::flushItemColors);
        modEventBus.addListener(RegistryClient::flushEntityRenderers);
    }
}