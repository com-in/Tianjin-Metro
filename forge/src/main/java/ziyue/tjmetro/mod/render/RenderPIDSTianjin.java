package ziyue.tjmetro.mod.render;

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
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.AbstractWidget;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.math.Axis;
import net.minecraft.world.phys.Vec3;
import org.mtr.core.operation.ArrivalResponse;
import org.mtr.core.tool.Utilities;
import org.mtr.libraries.it.unimi.dsi.fastutil.longs.LongArrayList;
import org.mtr.libraries.it.unimi.dsi.fastutil.longs.LongCollection;
import org.mtr.libraries.it.unimi.dsi.fastutil.objects.ObjectArrayList;
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
import org.mtr.render.BlockEntityRendererExtension;
import org.mtr.client.IDrawing;
import com.mojang.blaze3d.vertex.PoseStack;
import org.mtr.MTRClient;
import org.mtr.block.*;
import org.mtr.data.ArrivalsCacheClient;
import org.mtr.data.IGui;
import org.mtr.generated.lang.TranslationProvider;
import org.mtr.render.MainRenderer;
import org.mtr.render.QueuedRenderLayer;
import ziyue.tjmetro.mod.Reference;
import ziyue.tjmetro.mod.TianjinMetro;
import ziyue.tjmetro.mod.block.BlockPIDSTianjin;
import ziyue.tjmetro.mod.block.BlockPIDSTianjinSingle;
import ziyue.tjmetro.mod.client.DynamicTextureCache;
import ziyue.tjmetro.mod.client.IDrawingExtension;

import static org.mtr.render.RenderPIDS.SWITCH_LANGUAGE_TICKS;

/**
 * @author ZiYueCommentary
 * @see org.mtr.render.RenderPIDS
 * @see BlockPIDSTianjin
 * @since 1.0.0
 */

public class RenderPIDSTianjin<T extends BlockPIDSTianjin.BlockEntity> extends BlockEntityRendererExtension<T> implements IGui, Utilities
{
    public static final float LEFT_TEXT_X_CENTER = 42.45F;
    public static final float LEFT_TEXT_MAX_WIDTH = 78.55F;

    protected final float maxHeight;
    protected final float maxWidth;
    protected final boolean rotate90;
    protected final float textPadding;

    public RenderPIDSTianjin(BlockEntityRendererProvider.Context dispatcher, float maxHeight, int maxWidth, boolean rotate90, float textPadding) {
        this.maxHeight = maxHeight;
        this.maxWidth = maxWidth;
        this.rotate90 = rotate90;
        this.textPadding = textPadding;
    }

    @Override
    public final void render(T entity, PoseStack graphicsHolder, MultiBufferSource bufferSource, ClientLevel level, LocalPlayer player, float tickDelta, int light, int overlay) {
        final Level world = entity.getLevel();
        if (world == null) return;

        final BlockPos blockPos = entity.getBlockPos();
        if (!(entity.getBlockState().getBlock() instanceof BlockPIDSTianjin block)) return;
        if (!block.canStoreData(world, blockPos)) return;

        final Direction facing = IBlock.getStatePropertySafe(world, blockPos, BlockStateProperties.HORIZONTAL_FACING);

        if (entity.getPlatformIds().isEmpty()) {
            final LongArrayList platformIds = new LongArrayList();
            MTRClient.findClosePlatform(entity.getBlockPos().below(4), 5, platform -> platformIds.add(platform.getId()));
            getArrivalsAndRender(entity, blockPos, facing, platformIds);
        } else {
            getArrivalsAndRender(entity, blockPos, facing, entity.getPlatformIds());
        }
    }

    private void getArrivalsAndRender(T entity, BlockPos blockPos, Direction facing, LongCollection platformIds) {
        final ObjectArrayList<ArrivalResponse> arrivalResponseList = ArrivalsCacheClient.INSTANCE.requestArrivals(platformIds);
        MainRenderer.scheduleRender(QueuedRenderLayer.LIGHT_TRANSLUCENT, (graphicsHolder, vertexConsumer, offset) -> {
            if (!entity.renderSingleFace || IBlock.getStatePropertySafe(entity.getBlockState(), BlockPIDSTianjinSingle.SHOULD_RENDER))
                render(entity, blockPos, facing, arrivalResponseList, graphicsHolder, offset);
            if (!entity.renderSingleFace)
                render(entity, blockPos.relative(facing), facing.getOpposite(), arrivalResponseList, graphicsHolder, offset);
        });
    }

    private void render(T entity, BlockPos blockPos, Direction facing, ObjectArrayList<ArrivalResponse> arrivalResponseList, PoseStack graphicsHolder, Vec3 offset) {
        final float scale = 320 / maxHeight * textPadding;
        int arrivalIndex = entity.getDisplayPage() * 2;

        graphicsHolder.pushPose();
        graphicsHolder.translate(blockPos.getX() - offset.x + 0.5, blockPos.getY() - offset.y + 0.85, blockPos.getZ() - offset.z + 0.5);
        graphicsHolder.mulPose(Axis.YP.rotationDegrees((rotate90 ? 90 : 0) - facing.toYRot()));
        graphicsHolder.mulPose(Axis.ZP.rotationDegrees(180));
        graphicsHolder.translate(-0.48, 0, -0.48);
        graphicsHolder.scale(1 / scale, 1 / scale, 1 / scale);
        graphicsHolder.mulPose(Axis.XP.rotationDegrees(22.5F));
        graphicsHolder.translate(0, 0.05, 0);
        renderTexture(graphicsHolder, ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, "textures/block/pids_tianjin.png"), 251F, 115F, facing);
        graphicsHolder.translate(0, 0, -0.1);
        renderText(graphicsHolder, RenderTimeDisplay.getFormattedTime(entity.getLevel().getDayTime()), HorizontalAlignment.CENTER, VerticalAlignment.CENTER, LEFT_TEXT_X_CENTER, 106.5F, maxWidth * scale / 16, 0.5F, ARGB_WHITE);

        final int languageTicks = (int) Math.floor(MTRClient.getGameTick()) / SWITCH_LANGUAGE_TICKS;

        for (int i = 0; i < 2; i++) {
            final ArrivalResponse arrivalResponse = Utilities.getElement(arrivalResponseList, arrivalIndex + i);
            final String[] destinationSplit;
            final int languageIndex;
            if (arrivalResponse == null) {
                continue;
            }

            final String[] tempDestinationSplit = arrivalResponse.getDestination().split("\\|");
            if (arrivalResponse.getRouteNumber().isEmpty()) {
                destinationSplit = tempDestinationSplit;
            } else {
                final String[] tempNumberSplit = arrivalResponse.getRouteNumber().split("\\|");
                int destinationIndex = 0;
                int numberIndex = 0;
                final ObjectArrayList<String> newDestinations = new ObjectArrayList<>();
                while (true) {
                    final String newDestination = String.format("%s %s", tempNumberSplit[numberIndex % tempNumberSplit.length], tempDestinationSplit[destinationIndex % tempDestinationSplit.length]);
                    if (newDestinations.contains(newDestination)) {
                        break;
                    } else {
                        newDestinations.add(newDestination);
                    }
                    destinationIndex++;
                    numberIndex++;
                }
                destinationSplit = newDestinations.toArray(new String[0]);
            }
            final int messageCount = destinationSplit.length;
            languageIndex = languageTicks % messageCount;

            final long arrival = (arrivalResponse.getArrival() - ArrivalsCacheClient.INSTANCE.getMillisOffset() - System.currentTimeMillis()) / 1000;
            final String destination = destinationSplit[languageIndex];
            final boolean isCjk = IGui.isCjk(destination);
            final String destinationFormatted;
            final float yOffset = i * 45F;

            destinationFormatted = switch (arrivalResponse.getCircularState()) {
                case CLOCKWISE ->
                        (isCjk ? TranslationProvider.GUI_MTR_CLOCKWISE_VIA_CJK : TranslationProvider.GUI_MTR_CLOCKWISE_VIA).getString(destination);
                case ANTICLOCKWISE ->
                        (isCjk ? TranslationProvider.GUI_MTR_ANTICLOCKWISE_VIA_CJK : TranslationProvider.GUI_MTR_ANTICLOCKWISE_VIA).getString(destination);
                default ->
                        isCjk ? Component.translatable("gui.tjmetro.bound_for_pids_cjk", destination).getString() : Component.translatable("gui.tjmetro.bound_for_pids", destination).getString();
            };

            renderText(graphicsHolder, isCjk ? Component.translatable(i == 0 ? "gui.tjmetro.this_train_cjk" : "gui.tjmetro.next_train_cjk").getString() : Component.translatable(i == 0 ? "gui.tjmetro.this_train" : "gui.tjmetro.next_train").getString(), HorizontalAlignment.CENTER, VerticalAlignment.TOP, LEFT_TEXT_X_CENTER, 8F + yOffset, LEFT_TEXT_MAX_WIDTH, 1.5F, ARGB_WHITE);
            renderText(graphicsHolder, destinationFormatted, HorizontalAlignment.CENTER, VerticalAlignment.BOTTOM, LEFT_TEXT_X_CENTER, 45F + yOffset, LEFT_TEXT_MAX_WIDTH, 1.3F, ARGB_WHITE);
            if (arrival <= 15) {
                final String textKey = (arrival < 0 ? "gui.tjmetro.arrived" : "gui.tjmetro.arriving") + (isCjk ? "_cjk" : "");
                renderText(graphicsHolder, Component.translatable(textKey).getString(), HorizontalAlignment.CENTER, VerticalAlignment.TOP, LEFT_TEXT_X_CENTER, 21.5F + yOffset, LEFT_TEXT_MAX_WIDTH, 0.6F, 0xFFEFEF00);
            } else {
                final boolean isMinute = arrival > 60;
                String arrivalTime = String.valueOf(isMinute ? arrival / 60 : arrival);
                String arrivalUnit = Component.translatable((isMinute ? "gui.tjmetro.minute" : "gui.tjmetro.second") + (isCjk ? "_cjk" : "")).getString();
                float arrivalTimeWidth = IDrawingExtension.stringWidthWithFont(arrivalTime, 0.4F, 1, false).left();
                float arrivalUnitWidth = IDrawingExtension.stringWidthWithFont(arrivalUnit, 1.5F, 1, false).left();
                renderText(graphicsHolder, arrivalTime, HorizontalAlignment.LEFT, VerticalAlignment.CENTER, LEFT_TEXT_X_CENTER - (arrivalTimeWidth + arrivalUnitWidth) / 2, 30F + yOffset, LEFT_TEXT_MAX_WIDTH, 0.4F, 0xFFEFEF00);
                renderText(graphicsHolder, arrivalUnit, HorizontalAlignment.RIGHT, VerticalAlignment.CENTER, LEFT_TEXT_X_CENTER + (arrivalTimeWidth + arrivalUnitWidth) / 2, 30F + yOffset, LEFT_TEXT_MAX_WIDTH, 1.5F, ARGB_WHITE);
            }
        }

        if (entity.getCategories().isEmpty()) {
            graphicsHolder.popPose();
            return;
        }

        graphicsHolder.translate(86.7, 3.38, 0);
        if (entity.categoryIndex >= entity.getCategories().size() || !BlockPIDSTianjin.CATEGORIES.containsKey(entity.getCategories().getLong(entity.categoryIndex))) {
            TianjinMetro.LOGGER.warn("Invalid advertisement category id: {} at {}. Skipping!", entity.categoryIndex, blockPos.toShortString());
            entity.categoryIndex = (entity.categoryIndex + 1) % entity.getCategories().size();
            entity.advertisementIndex = 0;
            if (!BlockPIDSTianjin.CATEGORIES.containsKey(entity.getCategories().getLong(entity.categoryIndex))) {
                TianjinMetro.LOGGER.error("Invalid advertisement after reset. Clearing!");
                entity.getCategories().clear();
                graphicsHolder.popPose();
                return;
            }
        }

        BlockPIDSTianjin.Advertisement newAd = BlockPIDSTianjin.CATEGORIES.get(entity.getCategories().getLong(entity.categoryIndex)).get(entity.advertisementIndex);
        if (entity.advertisement != newAd) {
            entity.advertisement = newAd;
            // The space down below is a hacky way to deal with the error of float.
            entity.scrollingText.changeImage(() -> DynamicTextureCache.instance.getPlainText("   " + entity.advertisement.text().getString() + "   ", 0xFF1A1D46, ARGB_WHITE));
        }
        renderTexture(graphicsHolder, entity.advertisement.image(), 161F, 88.3F, facing);
        if (entity.advertisement.url() != null) {
            graphicsHolder.translate(0, 0, -0.1F);
            renderTexture(graphicsHolder, ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, "textures/sign/click.png"), 15F, 15F, facing);
            graphicsHolder.translate(0, 0, 0.1F);
        }
        graphicsHolder.translate(1.5F, 75.3F, 0);
        final ResourceLocation scrollTextureId = entity.scrollingText.getTextureId();
        boolean shouldSwitch = true;
        if (scrollTextureId != null) {
            shouldSwitch = entity.scrollingText.scrollText(graphicsHolder, Minecraft.getInstance().renderBuffers().bufferSource().getBuffer(RenderType.text(scrollTextureId)), facing);
        }
        if (shouldSwitch) nextSlide(entity);

        graphicsHolder.popPose();
    }

    protected void nextSlide(T entity) {
        if (entity.advertisementIndex + 1 >= BlockPIDSTianjin.CATEGORIES.get(entity.getCategories().getLong(entity.categoryIndex)).size()) {
            entity.categoryIndex = (entity.categoryIndex + 1) % entity.getCategories().size();
            entity.advertisementIndex = 0;
            return;
        }
        entity.advertisementIndex = (entity.advertisementIndex + 1) % BlockPIDSTianjin.CATEGORIES.get(entity.getCategories().getLong(entity.categoryIndex)).size();
    }

    protected void renderText(PoseStack graphicsHolder, String text, HorizontalAlignment horizontalAlignment, VerticalAlignment verticalAlignment, float x, float y, float availableWidth, float scale, int color) {
        graphicsHolder.pushPose();
        final MultiBufferSource bufferSource = Minecraft.getInstance().renderBuffers().bufferSource();
        IDrawingExtension.drawStringWithFont(graphicsHolder, bufferSource, text, horizontalAlignment, verticalAlignment, horizontalAlignment, x, y, availableWidth, -1, scale, color, color, 1, false, LightTexture.FULL_BRIGHT, false, null);
        graphicsHolder.popPose();
    }

    protected void renderTexture(PoseStack graphicsHolder, ResourceLocation identifier, float width, float height, Direction facing) {
        graphicsHolder.pushPose();
        final VertexConsumer vertexConsumer = Minecraft.getInstance().renderBuffers().bufferSource().getBuffer(RenderType.text(identifier));
        IDrawing.drawTexture(graphicsHolder, vertexConsumer, 0, height, 0, width, height, 0, width, 0, 0, 0, 0, 0, 0, 0, 1, 1, facing, ARGB_WHITE, 0xF000F0);
        graphicsHolder.popPose();
    }
}
