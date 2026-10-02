package ziyue.tjmetro.mapping;

import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ConfirmLinkScreen;
import net.minecraft.client.gui.screens.Screen;

/**
 * @author ZiYueCommentary
 * @since 1.1.1
 */

public interface ConfirmLinkScreenHelper
{
    static void open(Screen parent, String url, boolean trusted) {
        Minecraft minecraftClient = Minecraft.getInstance();
        minecraftClient.setScreen(new ConfirmLinkScreen(confirmed -> {
            if (confirmed) {
                Util.getPlatform().openUri(url);
            }
            minecraftClient.setScreen(parent);
        }, url, trusted));
    }
}