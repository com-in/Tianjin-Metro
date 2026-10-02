package ziyue.tjmetro.mod.render;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

import java.util.function.Consumer;

/**
 * Builds the simple cuboid {@link ModelPart}s used by the PSD/APG door renderers.
 *
 * <p>MTR 4.1 removed {@code org.mtr.mapping.mapper.EntityModelExtension} and
 * {@code org.mtr.mapping.mapper.ModelPartExtension}, so the models are now built directly
 * with the vanilla model part API, mirroring how MTR 4.1 builds {@code RenderPSDAPGDoor}'s models.</p>
 *
 * @author ZiYueCommentary
 * @since 1.0.0-beta-1
 */

public final class RenderDoorModelHelper
{
    private static int counter = 0;

    private RenderDoorModelHelper() {
    }

    /**
     * Equivalent of the removed {@code ModelPartExtension.setTextureUVOffset(u, v).addCuboid(x - 8, y - 16, z - 8, length, height, depth, 0, false)}.
     */
    public static ModelPart createSingleCube(float textureWidth, float textureHeight, float x, float y, float z, float length, float height, float depth) {
        return createModelPart(root -> createModelPartData(root, 0, 0, x - 8, y - 16, z - 8, length, height, depth, 0, 0, 0, 0, 0, 0, 0), (int) textureWidth, (int) textureHeight);
    }

    public static ModelPart createModelPart(Consumer<PartDefinition> consumer, int textureWidth, int textureHeight) {
        final MeshDefinition meshDefinition = new MeshDefinition();
        consumer.accept(meshDefinition.getRoot());
        return LayerDefinition.create(meshDefinition, textureWidth, textureHeight).bakeRoot();
    }

    public static void createModelPartData(PartDefinition root, int textureU, int textureV, float x, float y, float z, float sizeX, float sizeY, float sizeZ, float deformation, float pivotX, float pivotY, float pivotZ, float rotationX, float rotationY, float rotationZ) {
        root.addOrReplaceChild("cube_" + counter++, CubeListBuilder.create().texOffs(textureU, textureV).addBox(x, y, z, sizeX, sizeY, sizeZ, new CubeDeformation(deformation)), PartPose.offsetAndRotation(pivotX, pivotY, pivotZ, rotationX, rotationY, rotationZ));
    }
}
