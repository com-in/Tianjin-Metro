package ziyue.tjmetro.mod.client;

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
import ziyue.filters.Filter;
import org.mtr.registry.ObjectHolder;
import ziyue.tjmetro.mapping.FilterBuilder;
import ziyue.tjmetro.mod.BlockList;
import ziyue.tjmetro.mod.BlockRegistryObject;
import ziyue.tjmetro.mod.CreativeModeTabs;
import ziyue.tjmetro.mod.ItemList;
import ziyue.tjmetro.mod.ItemRegistryObject;
import ziyue.tjmetro.mod.config.ConfigClient;

import static ziyue.tjmetro.mod.BlockList.*;
import static ziyue.tjmetro.mod.ItemList.*;

public interface Filters
{
    Button.OnPress OPTION_BUTTON_ACTION = button -> Minecraft.getInstance().setScreen(ConfigClient.getConfigScreen(Minecraft.getInstance().screen));

    Filter TIANJIN_MISCELLANEOUS = FilterBuilder.registerFilter(CreativeModeTabs.TIANJIN_METRO.getId(), Component.translatable("filter.tjmetro.tianjin_miscellaneous"), () -> new ItemStack((ItemList.WRENCH.get())));
    Filter TIANJIN_BUILDING = FilterBuilder.registerFilter(CreativeModeTabs.TIANJIN_METRO.getId(), Component.translatable("filter.tjmetro.tianjin_building"), () -> new ItemStack((BlockList.ROLLING.get())));
    Filter TIANJIN_SIGNS = FilterBuilder.registerFilter(CreativeModeTabs.TIANJIN_METRO.getId(), Component.translatable("filter.tjmetro.tianjin_signs"), () -> new ItemStack((BlockList.STATION_NAME_SIGN_1.get())));
    Filter TIANJIN_GATES = FilterBuilder.registerFilter(CreativeModeTabs.TIANJIN_METRO.getId(), Component.translatable("filter.tjmetro.tianjin_gates"), () -> new ItemStack((ItemList.PSD_DOOR_TIANJIN.get())));
    Filter TIANJIN_DECORATION = FilterBuilder.registerFilter(CreativeModeTabs.TIANJIN_METRO.getId(), Component.translatable("filter.tjmetro.tianjin_decoration"), () -> new ItemStack((BlockList.LOGO.get())));
    Filter TIANJIN_RAILWAY_SIGNS = FilterBuilder.registerFilter(CreativeModeTabs.TIANJIN_METRO.getId(), Component.translatable("filter.tjmetro.tianjin_railway_signs"), () -> new ItemStack((BlockList.RAILWAY_SIGN_TIANJIN_3_EVEN.get())));
    Filter TIANJIN_UNCATEGORIZED = FilterBuilder.registerUncategorizedItemsFilter(CreativeModeTabs.TIANJIN_METRO.getId());

    static void addBlocks(Filter filter, BlockRegistryObject... blocks) {
        for (BlockRegistryObject block : blocks) {
            FilterBuilder.addBlocks(filter, new ObjectHolder<Block>(block::get));
        }
    }

    static void addItems(Filter filter, ItemRegistryObject... items) {
        for (ItemRegistryObject item : items) {
            FilterBuilder.addItems(filter, new ObjectHolder<Item>(item::get));
        }
    }

    static void init() {
        addItems(Filters.TIANJIN_MISCELLANEOUS, WRENCH);
        addBlocks(Filters.TIANJIN_MISCELLANEOUS, PLAYER_DETECTOR, HIGH_SPEED_REPEATER);
        addBlocks(Filters.TIANJIN_BUILDING,
                ROLLING,
                PLATFORM_TJ_1, PLATFORM_TJ_2, PLATFORM_TJ_1_INDENTED, PLATFORM_TJ_2_INDENTED, PLATFORM_TJ_1_SLAB, PLATFORM_TJ_2_SLAB, PLATFORM_TJ_LINE_11_1, PLATFORM_TJ_LINE_11_1_INDENTED, PLATFORM_TJ_LINE_11_1_SLAB, PLATFORM_TJ_LINE_11_2, PLATFORM_TJ_LINE_11_2_INDENTED, PLATFORM_TJ_LINE_11_2_SLAB,
                MARBLE_GRAY, MARBLE_GRAY_SLAB, MARBLE_GRAY_STAIRS, MARBLE_YELLOW, MARBLE_YELLOW_SLAB, MARBLE_YELLOW_STAIRS,
                ROADBLOCK, ROADBLOCK_SIGN, TICKET_BARRIER_TIANJIN_ENTRANCE, TICKET_BARRIER_TIANJIN_EXIT,
                CUSTOM_COLOR_CONCRETE, CUSTOM_COLOR_CONCRETE_SLAB, CUSTOM_COLOR_CONCRETE_STAIRS,
                CUSTOM_COLOR_MOSAIC_TILE, CUSTOM_COLOR_MOSAIC_TILE_SLAB, CUSTOM_COLOR_MOSAIC_TILE_STAIRS);
        addBlocks(Filters.TIANJIN_SIGNS,
                STATION_NAME_SIGN_1, STATION_NAME_SIGN_2, STATION_NAME_SIGN_TIANJIN,
                STATION_NAME_ENTRANCE_TIANJIN, STATION_NAME_ENTRANCE_TIANJIN_PINYIN, STATION_NAME_ENTRANCE_TIANJIN_BMT, STATION_NAME_ENTRANCE_TIANJIN_BMT_PINYIN, STATION_NAME_ENTRANCE_TIANJIN_JINJING, STATION_NAME_ENTRANCE_TIANJIN_JINJING_PINYIN,
                STATION_NAME_WALL_LEGACY, STATION_NAME_PROJECTOR, ROUTE_MAP_BMT,
                STATION_NAME_PLATE);
        addItems(Filters.TIANJIN_GATES,
                PSD_DOOR_TIANJIN, PSD_GLASS_TIANJIN, PSD_GLASS_END_TIANJIN,
                APG_DOOR_TIANJIN, APG_GLASS_TIANJIN, APG_GLASS_END_TIANJIN,
                APG_DOOR_TIANJIN_BMT, APG_GLASS_TIANJIN_BMT, APG_GLASS_END_TIANJIN_BMT,
                PSD_DOOR_TIANJIN_BMT, PSD_GLASS_TIANJIN_BMT, PSD_GLASS_END_TIANJIN_BMT,
                APG_DOOR_TIANJIN_JINJING, APG_GLASS_TIANJIN_JINJING, APG_GLASS_END_TIANJIN_JINJING,
                PSD_DOOR_TIANJIN_JINJING, PSD_GLASS_TIANJIN_JINJING, PSD_GLASS_END_TIANJIN_JINJING,
                APG_DOOR_TIANJIN_TRT, APG_GLASS_TIANJIN_TRT, APG_GLASS_END_TIANJIN_TRT,
                ItemList.APG_DOOR_SINGLE_LEFT, ItemList.APG_DOOR_SINGLE_RIGHT, ItemList.PSD_DOOR_SINGLE_LEFT, ItemList.PSD_DOOR_SINGLE_RIGHT
        );
        addBlocks(Filters.TIANJIN_DECORATION,
                CEILING_NOT_LIT, STATION_COLOR_CEILING, STATION_COLOR_CEILING_LIGHT, STATION_COLOR_CEILING_NO_LIGHT, STATION_COLOR_CEILING_NOT_LIT,
                CEILING_TIANJIN, CEILING_TIANJIN_NO_LIGHT, CEILING_TIANJIN_LIGHT, CEILING_TIANJIN_NOT_LIT,
                LOGO, APG_CORNER, TIME_DISPLAY, TIME_DISPLAY_EVEN, EMERGENCY_EXIT_SIGN, SERVICE_CORRIDOR_SIGN, BENCH, METAL_DETECTION_DOOR, METAL_POLE_BMT, SECURITY_CHECK_SIGN, NO_ENTERING_SIGN,
                STATION_NAVIGATOR_POLE, STATION_NAVIGATOR_3, STATION_NAVIGATOR_4, STATION_NAVIGATOR_5,
                STATION_SIGN, STATION_SIGN_BMT, TRASH_CAN, SMOKE_ALARM
                );
        addBlocks(Filters.TIANJIN_RAILWAY_SIGNS,
                PIDS_TIANJIN, PIDS_TIANJIN_SINGLE,
                RAILWAY_SIGN_WALL_4, RAILWAY_SIGN_WALL_6, RAILWAY_SIGN_WALL_8, RAILWAY_SIGN_WALL_10,
                RAILWAY_SIGN_WALL_DOUBLE_4, RAILWAY_SIGN_WALL_DOUBLE_6, RAILWAY_SIGN_WALL_DOUBLE_8, RAILWAY_SIGN_WALL_DOUBLE_10,
                RAILWAY_SIGN_WALL_BIG_2, RAILWAY_SIGN_WALL_BIG_3, RAILWAY_SIGN_WALL_BIG_4, RAILWAY_SIGN_WALL_BIG_5, RAILWAY_SIGN_WALL_BIG_6, RAILWAY_SIGN_WALL_BIG_7, RAILWAY_SIGN_WALL_BIG_8, RAILWAY_SIGN_WALL_BIG_9, RAILWAY_SIGN_WALL_BIG_10,
                RAILWAY_SIGN_TIANJIN_3_ODD, RAILWAY_SIGN_TIANJIN_4_ODD, RAILWAY_SIGN_TIANJIN_5_ODD, RAILWAY_SIGN_TIANJIN_6_ODD, RAILWAY_SIGN_TIANJIN_7_ODD, RAILWAY_SIGN_TIANJIN_2_EVEN, RAILWAY_SIGN_TIANJIN_3_EVEN, RAILWAY_SIGN_TIANJIN_4_EVEN, RAILWAY_SIGN_TIANJIN_5_EVEN, RAILWAY_SIGN_TIANJIN_6_EVEN, RAILWAY_SIGN_TIANJIN_7_EVEN, RAILWAY_SIGN_TIANJIN_POLE,
                RAILWAY_SIGN_TIANJIN_BMT_2_ODD, RAILWAY_SIGN_TIANJIN_BMT_3_ODD, RAILWAY_SIGN_TIANJIN_BMT_4_ODD, RAILWAY_SIGN_TIANJIN_BMT_5_ODD, RAILWAY_SIGN_TIANJIN_BMT_6_ODD, RAILWAY_SIGN_TIANJIN_BMT_7_ODD, RAILWAY_SIGN_TIANJIN_BMT_2_EVEN, RAILWAY_SIGN_TIANJIN_BMT_3_EVEN, RAILWAY_SIGN_TIANJIN_BMT_4_EVEN, RAILWAY_SIGN_TIANJIN_BMT_5_EVEN, RAILWAY_SIGN_TIANJIN_BMT_6_EVEN, RAILWAY_SIGN_TIANJIN_BMT_7_EVEN);

        FilterBuilder.setReservedButton(CreativeModeTabs.TIANJIN_METRO.getId(), Component.translatable("button.tjmetro.tianjin_metro_options"), OPTION_BUTTON_ACTION);
    }
}
