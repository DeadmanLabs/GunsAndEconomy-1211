package sheridan.gcaa.client.model.entities;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import sheridan.gcaa.GCAA;
import sheridan.gcaa.client.model.modelPart.*;
import sheridan.gcaa.lib.ArsenalLib;

public class GrenadeModel<T extends Entity> extends EntityModel<T> {
    public static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(
            GCAA.MODID, "model_assets/entities/grenade/grenade.png");
    private final ModelPart root;

    public GrenadeModel(ResourceLocation modelPath) {
        this.root = ArsenalLib.loadBedRockGunModel(modelPath).bakeRoot().getChild("root").meshing();
    }
    @Override
    public void setupAnim(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {}

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, int color) {
        float r = ((color >> 16) & 0xFF) / 255f;
        float g = ((color >> 8) & 0xFF) / 255f;
        float b = (color & 0xFF) / 255f;
        float a = ((color >> 24) & 0xFF) / 255f;
        root.render(poseStack, vertexConsumer, packedLight, packedOverlay, r, g, b, a);
    }
}
