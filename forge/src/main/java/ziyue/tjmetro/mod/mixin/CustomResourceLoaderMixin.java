package ziyue.tjmetro.mod.mixin;

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
import org.mtr.libraries.com.google.gson.JsonArray;
import org.mtr.libraries.com.google.gson.JsonObject;
import org.mtr.libraries.it.unimi.dsi.fastutil.objects.Object2ObjectAVLTreeMap;
import org.mtr.libraries.it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.resources.ResourceLocation;
import org.mtr.resource.ResourceManagerHelper;
import org.mtr.client.CustomResourceLoader;
import org.mtr.config.Config;
import org.mtr.resource.SignResource;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ziyue.tjmetro.mod.TianjinMetro;
import ziyue.tjmetro.mod.block.BlockPIDSTianjin;
import ziyue.tjmetro.mod.block.base.IRailwaySign;

import java.awt.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author ZiYueCommentary
 * @see CustomResourceLoader
 * @since 1.0.0-beta-1
 */

@Mixin(CustomResourceLoader.class)
public abstract class CustomResourceLoaderMixin
{
    @Shadow(remap = false)
    @Final
    private static Object2ObjectAVLTreeMap<String, SignResource> SIGNS_CACHE;

    @Shadow(remap = false)
    @Final
    private static ObjectArrayList<SignResource> SIGNS;

    @Inject(at = @At("TAIL"), method = "reload", remap = false)
    private static void afterReload(CallbackInfo ci) {
        for (IRailwaySign.SignType value : IRailwaySign.SignType.values()) {
            SIGNS.add(value.sign);
            SIGNS_CACHE.put(value.signId, value.sign);
        }

        BlockPIDSTianjin.CATEGORIES.clear();

        ResourceManagerHelper.readAllResources(ResourceLocation.fromNamespaceAndPath("tjmetro", "pids_tianjin_ads.json"), (inputStream) -> {
            try {
                final JsonObject config = Config.readResource(inputStream).getAsJsonObject();
                config.entrySet().forEach(entry -> {
                    if (entry.getValue().isJsonObject()) {
                        long id = entry.getKey().hashCode();
                        if (BlockPIDSTianjin.CATEGORIES.containsKey(id)) {
                            TianjinMetro.LOGGER.warn("Duplicate category key: {}. Skipping!", entry.getKey());
                        } else {
                            final JsonObject adDefinition = entry.getValue().getAsJsonObject();
                            BlockPIDSTianjin.CATEGORIES.put(id, new BlockPIDSTianjin.Category(
                                    id,
                                    new Color(Integer.parseInt(adDefinition.get("color").getAsString(), 16)),
                                    Component.translatable(adDefinition.get("name").getAsString()),
                                    Component.translatable(adDefinition.get("description").getAsString()))
                            );

                            BlockPIDSTianjin.Category array = BlockPIDSTianjin.CATEGORIES.get(id);
                            if (!adDefinition.get("advertisements").isJsonArray()) {
                                TianjinMetro.LOGGER.warn("Bad advertisement config at {}: missing advertisement array. Skipping!", entry.getKey());
                            } else {
                                JsonArray advertisements = adDefinition.get("advertisements").getAsJsonArray();
                                AtomicInteger index = new AtomicInteger();
                                advertisements.forEach(ad -> {
                                    try {
                                        if (ad.isJsonObject()) {
                                            JsonObject slide = ad.getAsJsonObject();
                                            array.add(new BlockPIDSTianjin.Advertisement(
                                                    ResourceLocation.parse(slide.get("image").getAsString()),
                                                    Component.translatable(slide.get("text").getAsString()),
                                                    slide.has("url") && !slide.get("url").isJsonNull() && !slide.get("url").getAsString().isEmpty() ?
                                                            slide.get("url").getAsString() :
                                                            null));
                                        }
                                    } catch (Exception e) {
                                        TianjinMetro.LOGGER.warn("Bad advertisement config at {}, index {}. Skipping!", entry.getKey(), index.get());
                                    }
                                    index.getAndIncrement();
                                });
                            }
                        }
                    }
                });
            } catch (Exception e) {
                TianjinMetro.LOGGER.warn("Error when initializing advertisements!", e);
            }
        });
        TianjinMetro.LOGGER.info("Found {} categories for PIDS Tianjin", BlockPIDSTianjin.CATEGORIES.size());
    }
}
