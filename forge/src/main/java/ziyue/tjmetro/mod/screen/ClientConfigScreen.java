package ziyue.tjmetro.mod.screen;

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
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.gui.entries.BooleanListEntry;
import me.shedaniel.clothconfig2.gui.entries.IntegerSliderEntry;
import me.shedaniel.clothconfig2.gui.entries.TextListEntry;
import me.shedaniel.clothconfig2.impl.builders.SubCategoryBuilder;
import net.minecraft.client.gui.screens.Screen;
import ziyue.tjmetro.centralconfig.CentralConfig;
import ziyue.tjmetro.centralconfig.MasterCategory;
import ziyue.tjmetro.mapping.TextFormatter;
import ziyue.tjmetro.mod.Reference;
import ziyue.tjmetro.mod.client.DynamicTextureCache;
import ziyue.tjmetro.mod.config.ConfigClient;

import java.util.Random;

/**
 * @author ZiYueCommentary
 * @since 1.0.0-beta-5
 */

public class ClientConfigScreen
{
    public static final MasterCategory TIANJIN_METRO_CATEGORY = new MasterCategory(Reference.MOD_ID, () -> Component.translatable("config.category.tjmetro"), (entryBuilder, categoryTianjinMetro) -> {
        BooleanListEntry booleanUseTianjinMetroFont = entryBuilder
                .startBooleanToggle(Component.translatable("config.tjmetro.use_tianjin_metro_font"), ConfigClient.USE_TIANJIN_METRO_FONT.get())
                .setTooltip(Component.translatable("tooltip.tjmetro.use_tianjin_metro_font"))
                .setDefaultValue(ConfigClient.USE_TIANJIN_METRO_FONT.getDefault())
                .setSaveConsumer(ConfigClient.USE_TIANJIN_METRO_FONT::set)
                .build();
        BooleanListEntry booleanRotatedStationName = entryBuilder
                .startBooleanToggle(Component.translatable("config.tjmetro.rotated_station_name"), ConfigClient.ROTATED_STATION_NAME.get())
                .setDefaultValue(ConfigClient.ROTATED_STATION_NAME.getDefault())
                .setSaveConsumer(ConfigClient.ROTATED_STATION_NAME::set)
                .build();
        BooleanListEntry booleanDisableFilters = entryBuilder
                .startBooleanToggle(Component.translatable("config.tjmetro.disable_filters"), ConfigClient.DISABLE_FILTERS.get())
                .setDefaultValue(ConfigClient.DISABLE_FILTERS.getDefault())
                .setTooltip(Component.translatable("tooltip.tjmetro.disable_filters"))
                .setSaveConsumer(ConfigClient.DISABLE_FILTERS::set)
                .build();
        SubCategoryBuilder subCategoryDebugging = entryBuilder.startSubCategory(Component.translatable("config.tjmetro.debugging"));
        BooleanListEntry booleanDisableDynamicTextures = entryBuilder
                .startBooleanToggle(Component.translatable("config.tjmetro.disable_dynamic_textures"), ConfigClient.DISABLE_DYNAMIC_TEXTURES.get())
                .setDefaultValue(ConfigClient.DISABLE_DYNAMIC_TEXTURES.getDefault())
                .setTooltip(Component.translatable("tooltip.tjmetro.disable_dynamic_textures"))
                .setSaveConsumer(ConfigClient.DISABLE_DYNAMIC_TEXTURES::set)
                .build();
        BooleanListEntry booleanDisableTrainRendering = entryBuilder
                .startBooleanToggle(Component.translatable("config.tjmetro.disable_train_rendering"), ConfigClient.DISABLE_TRAIN_RENDERING.get())
                .setDefaultValue(ConfigClient.DISABLE_TRAIN_RENDERING.getDefault())
                .setSaveConsumer(ConfigClient.DISABLE_TRAIN_RENDERING::set)
                .build();
        IntegerSliderEntry integerDynamicTextureMaxSize = entryBuilder
                .startIntSlider(Component.translatable("config.tjmetro.dynamic_texture_max_size"), ConfigClient.DYNAMIC_TEXTURE_MAX_SIZE.get(), 1024, 4096)
                .setDefaultValue(ConfigClient.DYNAMIC_TEXTURE_MAX_SIZE.getDefault())
                .setTooltip(Component.translatable("tooltip.tjmetro.dynamic_texture_max_size"))
                .setSaveConsumer(ConfigClient.DYNAMIC_TEXTURE_MAX_SIZE::set)
                .build();
        subCategoryDebugging.add(booleanDisableDynamicTextures);
        subCategoryDebugging.add(booleanDisableTrainRendering);
        subCategoryDebugging.add(integerDynamicTextureMaxSize);
        TextListEntry textFooter = entryBuilder.startTextDescription(TextFormatter.FOOTER_LINK.apply(ConfigClient.FOOTERS.get(new Random().nextInt(ConfigClient.FOOTERS.size())))).build();
        categoryTianjinMetro.addEntry(booleanUseTianjinMetroFont).addEntry(booleanRotatedStationName).addEntry(booleanDisableFilters).addEntry(subCategoryDebugging.build()).addEntry(textFooter);
    }, () -> ConfigBuilder.create()
            .setTitle(Component.translatable("gui.tjmetro.options"))
            .setSavingRunnable(() -> {
                if (DynamicTextureCache.instance != null) {
                    DynamicTextureCache.instance.reload();
                }
                ConfigClient.writeToFile();
            }));

    public static final CentralConfig TIANJIN_METRO_CENTRAL_CONFIG = new CentralConfig(TIANJIN_METRO_CATEGORY);

    public static Screen getClothConfigScreen(Screen parent) {
        return TIANJIN_METRO_CENTRAL_CONFIG.getConfigScreen(parent);
    }
}
