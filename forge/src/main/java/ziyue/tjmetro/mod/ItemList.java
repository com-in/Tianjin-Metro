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
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Item;
import org.mtr.registry.ObjectHolder;
import ziyue.tjmetro.mod.item.ItemPSDAPGTianjinBase;

/**
 * @since 1.0.0-beta-1
 */

public interface ItemList
{
    ItemRegistryObject WRENCH = Registry.registerItem("wrench", itemSettings -> new Item(itemSettings.stacksTo(1)), CreativeModeTabs.TIANJIN_METRO);
    ItemRegistryObject PSD_DOOR_TIANJIN = Registry.registerItem("psd_door_tianjin", itemSettings -> new ItemPSDAPGTianjinBase(BlockList.PSD_DOOR_TIANJIN_BLOCK, itemSettings), CreativeModeTabs.TIANJIN_METRO);
    ItemRegistryObject PSD_GLASS_TIANJIN = Registry.registerItem("psd_glass_tianjin", itemSettings -> new ItemPSDAPGTianjinBase(BlockList.PSD_GLASS_TIANJIN_BLOCK, itemSettings), CreativeModeTabs.TIANJIN_METRO);
    ItemRegistryObject PSD_GLASS_END_TIANJIN = Registry.registerItem("psd_glass_end_tianjin", itemSettings -> new ItemPSDAPGTianjinBase(BlockList.PSD_GLASS_END_TIANJIN_BLOCK, itemSettings), CreativeModeTabs.TIANJIN_METRO);
    ItemRegistryObject APG_DOOR_TIANJIN = Registry.registerItem("apg_door_tianjin", itemSettings -> new ItemPSDAPGTianjinBase(BlockList.APG_DOOR_TIANJIN_BLOCK, itemSettings), CreativeModeTabs.TIANJIN_METRO);
    ItemRegistryObject APG_GLASS_TIANJIN = Registry.registerItem("apg_glass_tianjin", itemSettings -> new ItemPSDAPGTianjinBase(BlockList.APG_GLASS_TIANJIN_BLOCK, itemSettings), CreativeModeTabs.TIANJIN_METRO);
    ItemRegistryObject APG_GLASS_END_TIANJIN = Registry.registerItem("apg_glass_end_tianjin", itemSettings -> new ItemPSDAPGTianjinBase(BlockList.APG_GLASS_END_TIANJIN_BLOCK, itemSettings), CreativeModeTabs.TIANJIN_METRO);
    ItemRegistryObject APG_DOOR_TIANJIN_BMT = Registry.registerItem("apg_door_tianjin_bmt", itemSettings -> new ItemPSDAPGTianjinBase(BlockList.APG_DOOR_TIANJIN_BMT_BLOCK, itemSettings), CreativeModeTabs.TIANJIN_METRO);
    ItemRegistryObject APG_GLASS_TIANJIN_BMT = Registry.registerItem("apg_glass_tianjin_bmt", itemSettings -> new ItemPSDAPGTianjinBase(BlockList.APG_GLASS_TIANJIN_BMT_BLOCK, itemSettings), CreativeModeTabs.TIANJIN_METRO);
    ItemRegistryObject APG_GLASS_END_TIANJIN_BMT = Registry.registerItem("apg_glass_end_tianjin_bmt", itemSettings -> new ItemPSDAPGTianjinBase(BlockList.APG_GLASS_END_TIANJIN_BMT_BLOCK, itemSettings), CreativeModeTabs.TIANJIN_METRO);
    ItemRegistryObject PSD_DOOR_TIANJIN_BMT = Registry.registerItem("psd_door_tianjin_bmt", itemSettings -> new ItemPSDAPGTianjinBase(BlockList.PSD_DOOR_TIANJIN_BMT_BLOCK, itemSettings), CreativeModeTabs.TIANJIN_METRO);
    ItemRegistryObject PSD_GLASS_TIANJIN_BMT = Registry.registerItem("psd_glass_tianjin_bmt", itemSettings -> new ItemPSDAPGTianjinBase(BlockList.PSD_GLASS_TIANJIN_BMT_BLOCK, itemSettings), CreativeModeTabs.TIANJIN_METRO);
    ItemRegistryObject PSD_GLASS_END_TIANJIN_BMT = Registry.registerItem("psd_glass_end_tianjin_bmt", itemSettings -> new ItemPSDAPGTianjinBase(BlockList.PSD_GLASS_END_TIANJIN_BMT_BLOCK, itemSettings), CreativeModeTabs.TIANJIN_METRO);
    ItemRegistryObject APG_DOOR_TIANJIN_TRT = Registry.registerItem("apg_door_tianjin_trt", itemSettings -> new ItemPSDAPGTianjinBase(BlockList.APG_DOOR_TIANJIN_TRT_BLOCK, itemSettings), CreativeModeTabs.TIANJIN_METRO);
    ItemRegistryObject APG_GLASS_TIANJIN_TRT = Registry.registerItem("apg_glass_tianjin_trt", itemSettings -> new ItemPSDAPGTianjinBase(BlockList.APG_GLASS_TIANJIN_TRT_BLOCK, itemSettings), CreativeModeTabs.TIANJIN_METRO);
    ItemRegistryObject APG_GLASS_END_TIANJIN_TRT = Registry.registerItem("apg_glass_end_tianjin_trt", itemSettings -> new ItemPSDAPGTianjinBase(BlockList.APG_GLASS_END_TIANJIN_TRT_BLOCK, itemSettings), CreativeModeTabs.TIANJIN_METRO);
    ItemRegistryObject PSD_DOOR_TIANJIN_JINJING = Registry.registerItem("psd_door_tianjin_jinjing", itemSettings -> new ItemPSDAPGTianjinBase(BlockList.PSD_DOOR_TIANJIN_JINJING_BLOCK, itemSettings), CreativeModeTabs.TIANJIN_METRO);
    ItemRegistryObject PSD_GLASS_TIANJIN_JINJING = Registry.registerItem("psd_glass_tianjin_jinjing", itemSettings -> new ItemPSDAPGTianjinBase(BlockList.PSD_GLASS_TIANJIN_JINJING_BLOCK, itemSettings), CreativeModeTabs.TIANJIN_METRO);
    ItemRegistryObject PSD_GLASS_END_TIANJIN_JINJING = Registry.registerItem("psd_glass_end_tianjin_jinjing", itemSettings -> new ItemPSDAPGTianjinBase(BlockList.PSD_GLASS_END_TIANJIN_JINJING_BLOCK, itemSettings), CreativeModeTabs.TIANJIN_METRO);
    ItemRegistryObject APG_DOOR_TIANJIN_JINJING = Registry.registerItem("apg_door_tianjin_jinjing", itemSettings -> new ItemPSDAPGTianjinBase(BlockList.APG_DOOR_TIANJIN_JINJING_BLOCK, itemSettings), CreativeModeTabs.TIANJIN_METRO);
    ItemRegistryObject APG_GLASS_TIANJIN_JINJING = Registry.registerItem("apg_glass_tianjin_jinjing", itemSettings -> new ItemPSDAPGTianjinBase(BlockList.APG_GLASS_TIANJIN_JINJING_BLOCK, itemSettings), CreativeModeTabs.TIANJIN_METRO);
    ItemRegistryObject APG_GLASS_END_TIANJIN_JINJING = Registry.registerItem("apg_glass_end_tianjin_jinjing", itemSettings -> new ItemPSDAPGTianjinBase(BlockList.APG_GLASS_END_TIANJIN_JINJING_BLOCK, itemSettings), CreativeModeTabs.TIANJIN_METRO);
    ItemRegistryObject APG_DOOR_SINGLE_LEFT = Registry.registerItem("apg_door_single_left", itemSettings -> new ItemPSDAPGTianjinBase(BlockList.APG_DOOR_SINGLE_LEFT, itemSettings), CreativeModeTabs.TIANJIN_METRO);
    ItemRegistryObject APG_DOOR_SINGLE_RIGHT = Registry.registerItem("apg_door_single_right", itemSettings -> new ItemPSDAPGTianjinBase(BlockList.APG_DOOR_SINGLE_RIGHT, itemSettings), CreativeModeTabs.TIANJIN_METRO);
    ItemRegistryObject PSD_DOOR_SINGLE_LEFT = Registry.registerItem("psd_door_single_left", itemSettings -> new ItemPSDAPGTianjinBase(BlockList.PSD_DOOR_SINGLE_LEFT, itemSettings), CreativeModeTabs.TIANJIN_METRO);
    ItemRegistryObject PSD_DOOR_SINGLE_RIGHT = Registry.registerItem("psd_door_single_right", itemSettings -> new ItemPSDAPGTianjinBase(BlockList.PSD_DOOR_SINGLE_RIGHT, itemSettings), CreativeModeTabs.TIANJIN_METRO);

    static void registerItems() {
        // Calling this class to initialize constants
        TianjinMetro.LOGGER.info("Registering items");
    }
}
