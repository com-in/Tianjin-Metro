package ziyue.tjmetro.mod;

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
import org.apache.commons.lang3.tuple.ImmutablePair;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.ChatFormatting;
import com.mojang.blaze3d.vertex.PoseStack;
import org.mtr.config.Config;
import org.mtr.data.IGui;
import org.mtr.generated.lang.TranslationProvider;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Some methods similar to methods in IGui.
 *
 * @see org.mtr.data.IGui
 * @since 1.0.0-beta-1
 */

public interface IGuiExtension
{
    int CHECKBOX_WIDTH = 160;

    /**
     * Remains the language string that the language option specified.
     *
     * @return filtered string
     * @author ZiYueCommentary
     * @see org.mtr.config.LanguageDisplay
     * @since 1.0.0-beta-1
     */
    static String filterLanguage(String text) {
        final StringBuilder noCommentString = new StringBuilder(text);
        final int commentIndex = noCommentString.indexOf("||");
        final int separatorIndex = noCommentString.indexOf("|");
        if (commentIndex != -1)
            noCommentString.delete(commentIndex, noCommentString.length());
        if (separatorIndex != -1) {
            switch (Config.getClient().getLanguageDisplay()) {
                case CJK_ONLY:
                    noCommentString.delete(separatorIndex, noCommentString.length());
                    break;
                case NON_CJK_ONLY:
                    noCommentString.delete(0, separatorIndex);
            }
        }
        return noCommentString.toString();
    }

    /**
     * Returns the language string that the language option specified.
     *
     * @param keyCJK key of CJK string
     * @param key    key of English string
     * @return filtered string
     * @author ZiYueCommentary
     * @see org.mtr.config.LanguageDisplay
     * @since 1.0.0-beta-1
     */
    static String mergeTranslation(String keyCJK, String key) {
        return switch (Config.getClient().getLanguageDisplay()) {
            case CJK_ONLY -> Component.translatable(keyCJK).getString();
            case NON_CJK_ONLY -> Component.translatable(key).getString();
            default -> Component.translatable(keyCJK).getString() + "|" + Component.translatable(key).getString();
        };
    }

    /**
     * Splits the translation string into English and CJK. This function requires there is only a "|" separator.
     *
     * @param text the translation string
     * @return a tuple of CJK (left) and English (right)
     * @author ZiYueCommentary
     * @since 1.0.0-beta-1
     */
    static ImmutablePair<String, String> splitTranslation(String text) {
        final int separatorIndex = text.indexOf("|");
        if (separatorIndex == -1) {
            if (IGui.isCjk(text)) return ImmutablePair.of(text, "");
            else return ImmutablePair.of("", text);
        }
        return ImmutablePair.of(text.substring(0, separatorIndex), text.substring(separatorIndex + 1));
    }

    /**
     * Formatting the string.
     *
     * @author ZiYueCommentary
     * @see IGui#insertTranslation(TranslationProvider.TranslationHolder, TranslationProvider.TranslationHolder, String, int, String...)
     * @since 1.0.0-beta-2
     */
    static String insertTranslation(String keyCJK, String key, @Nullable String overrideFirst, int expectedArguments, String... arguments) {
        if (arguments.length < expectedArguments) {
            return "";
        }

        final List<String[]> dataCJK = new ArrayList<>();
        final List<String[]> data = new ArrayList<>();
        for (int i = 0; i < arguments.length; i++) {
            final String[] argumentSplit = arguments[i].split("\\|");

            int indexCJK = 0;
            int index = 0;
            for (final String text : argumentSplit) {
                if (IGui.isCjk(text)) {
                    if (indexCJK == dataCJK.size()) {
                        dataCJK.add(new String[expectedArguments]);
                    }
                    dataCJK.get(indexCJK)[i] = text;
                    indexCJK++;
                } else {
                    if (index == data.size()) {
                        data.add(new String[expectedArguments]);
                    }
                    data.get(index)[i] = text;
                    index++;
                }
            }
        }

        final StringBuilder result = new StringBuilder();
        dataCJK.forEach(combinedArguments -> {
            if (Arrays.stream(combinedArguments).allMatch(Objects::nonNull)) {
                result.append("|");
                if (overrideFirst == null) {
                    result.append(Component.translatable(keyCJK, (Object[]) combinedArguments).getString());
                } else {
                    final String[] newCombinedArguments = new String[expectedArguments + 1];
                    System.arraycopy(combinedArguments, 0, newCombinedArguments, 1, expectedArguments);
                    newCombinedArguments[0] = overrideFirst;
                    result.append((Component.translatable(keyCJK, (Object[]) newCombinedArguments).getString()));
                }
            }
        });
        data.forEach(combinedArguments -> {
            if (Arrays.stream(combinedArguments).allMatch(Objects::nonNull)) {
                result.append("|");
                if (overrideFirst == null) {
                    result.append((Component.translatable(key, (Object[]) combinedArguments).getString()));
                } else {
                    final String[] newCombinedArguments = new String[expectedArguments + 1];
                    System.arraycopy(combinedArguments, 0, newCombinedArguments, 1, expectedArguments);
                    newCombinedArguments[0] = overrideFirst;
                    result.append((Component.translatable(key, (Object[]) newCombinedArguments).getString()));
                }
            }
        });

        if (result.isEmpty()) return "";
        return result.substring(1);
    }

    /**
     * @author ZiYueCommentary
     * @see #insertTranslation(String, String, String, int, String...)
     * @since 1.0.0-beta-2
     */
    static String insertTranslation(String keyCJK, String key, int expectedArguments, String... arguments) {
        return insertTranslation(keyCJK, key, null, expectedArguments, arguments);
    }

    /**
     * Adding a hold-shift-tooltip to the tooltips.
     *
     * @param list      hover text List, just like pointer in C/C++
     * @param component a component that waits for split
     * @param regex     the delimiting regular expression
     * @param limit     the result threshold, as described above
     * @return hover text lists
     * @author ZiYueCommentary
     * @since 1.0.0-beta-2
     */
    static List<MutableComponent> addHoldShiftTooltip(List<MutableComponent> list, MutableComponent component, boolean wordWarp, String regex, int limit) {
        if (Screen.hasShiftDown()) {
            // Adding a space in the end is the simplest way to fix the bug. Do not ask me why.
            String[] texts = (component.getString() + " ").split(regex, limit);
            if (wordWarp) {
                for (String text : texts) {
                    int start = 0;
                    while (start < text.length()) {
                        int end = start;
                        int currentWidth = 0;
                        int lastSpace = -1;

                        while (end < text.length() && currentWidth + Minecraft.getInstance().font.width(text.substring(start, end + 1)) <= 380) { // 380 is the maximum width
                            if (text.charAt(end) == ' ') {
                                lastSpace = end;
                            }
                            currentWidth += Minecraft.getInstance().font.width(text.substring(end, end + 1));
                            end++;
                        }

                        if (lastSpace != -1 && lastSpace > start) {
                            list.add(Component.literal(text.substring(start, lastSpace)));
                            start = lastSpace + 1;
                        } else {
                            list.add(Component.literal(text.substring(start, end)));
                            start = end;
                        }
                    }
                }
            } else {
                for (String text : texts) {
                    list.add(Component.literal(text));
                }
            }
        } else {
            list.add(Component.translatable("tooltip.tjmetro.shift").withStyle(ChatFormatting.YELLOW));
        }
        return list;
    }

    /**
     * @author ZiYueCommentary
     * @see #addHoldShiftTooltip(List, MutableComponent, boolean, String, int)
     * @since 1.0.0-beta-2
     */
    static List<MutableComponent> addHoldShiftTooltip(List<MutableComponent> list, MutableComponent component, boolean wordWarp, String regex) {
        return addHoldShiftTooltip(list, component, wordWarp, regex, 0);
    }

    /**
     * @author ZiYueCommentary
     * @see #addHoldShiftTooltip(List, MutableComponent, boolean, String, int)
     * @since 1.0.0-beta-2
     */
    static List<MutableComponent> addHoldShiftTooltip(List<MutableComponent> list, MutableComponent component, boolean wordWarp) {
        return addHoldShiftTooltip(list, component, wordWarp, "\n", 0);
    }

    /**
     * @author ZiYueCommentary
     * @see #addHoldShiftTooltip(List, MutableComponent, boolean, String, int)
     * @since 1.0.0-beta-2
     */
    static List<MutableComponent> addHoldShiftTooltip(List<MutableComponent> list, MutableComponent component) {
        return addHoldShiftTooltip(list, component, true, "\n", 0);
    }
}
