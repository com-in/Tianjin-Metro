package ziyue.tjmetro.mapping;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import ziyue.tjmetro.mod.config.ConfigClient;

import java.util.function.Function;

public interface TextFormatter
{
    /**
     * Stylize a config screen footer as a link.
     *
     * @since 1.0.0-beta-5
     */
    Function<ConfigClient.Footer, Component> FOOTER_LINK = footer -> footer.text().get().copy().withStyle(Style.EMPTY.withColor(ChatFormatting.BLUE).withUnderlined(true).withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, footer.link())));
}
