package ziyue.tjmetro.mod.client;

import static org.mtr.data.IGui.*;
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
import net.minecraft.core.Direction;
import net.minecraft.client.renderer.RenderType;
import com.mojang.blaze3d.vertex.PoseStack;
import org.mtr.MTRClient;
import org.mtr.client.IDrawing;
import org.mtr.client.RouteMapGenerator;
import org.mtr.data.IGui;

import java.util.function.Supplier;

/**
 * @author ZiYueCommentary
 * @see org.mtr.client.ScrollingText
 * @since 1.0.0
 */

public class ScrollingText implements IGui
{
    protected float ticksOffset;
    public Supplier<DynamicTextureCache.DynamicResource> imageSupplier;

    protected final double availableWidth;
    protected final double availableHeight;
    protected final int scrollSpeed;
    protected final boolean isFullPixel;
    public static final float EPSILON = 0.03F;

    public ScrollingText(double availableWidth, double availableHeight, int scrollSpeed, boolean isFullPixel) {
        this.availableWidth = availableWidth;
        this.availableHeight = availableHeight;
        this.scrollSpeed = scrollSpeed;
        this.isFullPixel = isFullPixel;
    }

    public void changeImage(Supplier<DynamicTextureCache.DynamicResource> imageSupplier) {
        if (this.imageSupplier != imageSupplier) {
            this.imageSupplier = imageSupplier;
            ticksOffset = MTRClient.getGameTick();
        }
    }

    public ResourceLocation getTextureId() {
        return imageSupplier == null ? null : imageSupplier.get().identifier;
    }

    public boolean scrollText(PoseStack poseStack, VertexConsumer vertexConsumer, Direction facing) {
        if (imageSupplier != null) {
            poseStack.pushPose();
            final int pixelScale = isFullPixel ? 1 : RouteMapGenerator.PIXEL_SCALE;
            final double scale = availableHeight / imageSupplier.get().height;
            final int widthSteps = (int) Math.floor(availableWidth / scale / pixelScale);
            final int imageSteps = imageSupplier.get().width / pixelScale;
            final int totalSteps = widthSteps + imageSteps;
            final int step = Math.round((MTRClient.getGameTick() - ticksOffset) * scrollSpeed) % totalSteps;
            final double width = Math.min(Math.min(availableWidth, imageSupplier.get().width * scale), Math.min(step * pixelScale * scale, (totalSteps - step) * pixelScale * scale));
            final float u1 = Math.max((float) (step - widthSteps) / imageSteps, 0);
            final float u2 = Math.min((float) step / imageSteps, 1);
            IDrawing.drawTexture(poseStack, vertexConsumer, (float) (Math.max(widthSteps - step, 0) * scale * pixelScale), 0, (float) width, (float) availableHeight, u1, 0, u2, 1, facing, ARGB_WHITE, 0xF000F0);
            poseStack.popPose();
            return (1.0F - u1) <= EPSILON && (1.0F - u2) <= EPSILON;
        }
        return true;
    }
}