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
import net.minecraft.resources.ResourceLocation;
import org.mtr.resource.ResourceManagerHelper;
import org.mtr.MTR;
import org.mtr.client.DynamicTextureCache;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ziyue.tjmetro.mod.Reference;
import ziyue.tjmetro.mod.TianjinMetro;
import ziyue.tjmetro.mod.config.ConfigClient;

import java.awt.*;

/**
 * @author ZiYueCommentary
 * @see DynamicTextureCache
 * @since 1.0.0-beta-1
 */

@Mixin(DynamicTextureCache.class)
public abstract class DynamicTextureCacheMixin
{
    @Shadow(remap = false)
    private Font font;

    @Shadow(remap = false)
    private Font fontCjk;

    @Inject(at = @At("TAIL"), method = "<init>", remap = false)
    private void afterConstruct(CallbackInfo ci) {
        ziyue.tjmetro.mod.client.DynamicTextureCache.instance = new ziyue.tjmetro.mod.client.DynamicTextureCache();
    }

    @Inject(at = @At("TAIL"), method = "reload", remap = false)
    private void afterReload(CallbackInfo ci) {
        ziyue.tjmetro.mod.client.DynamicTextureCache.instance.reload();
        // If a Tianjin Metro sign gets text with MTR font and the MTR fonts are not initialized, the game will throw a NullPointerException.
        // This will happen in case of Tianjin Metro sign is rendered before the MTR signs. To fix this, here we load MTR fonts manually.
        if (font == null) {
            ResourceManagerHelper.readResource(ResourceLocation.fromNamespaceAndPath(MTR.MOD_ID, "font/noto-sans-semibold.ttf"), inputStream -> {
                try {
                    font = Font.createFont(Font.TRUETYPE_FONT, inputStream);
                } catch (Exception e) {
                    TianjinMetro.LOGGER.error(e.getMessage(), e);
                }
            });
        }

        if (fontCjk == null) {
            ResourceManagerHelper.readResource(ResourceLocation.fromNamespaceAndPath(MTR.MOD_ID, "font/noto-serif-cjk-tc-semibold.ttf"), inputStream -> {
                try {
                    fontCjk = Font.createFont(Font.TRUETYPE_FONT, inputStream);
                } catch (Exception e) {
                    TianjinMetro.LOGGER.error(e.getMessage(), e);
                }
            });
        }
    }

    @Inject(at = @At("TAIL"), method = "tick", remap = false)
    private void afterTick(CallbackInfo ci) {
        ziyue.tjmetro.mod.client.DynamicTextureCache.instance.tick();
    }
}
