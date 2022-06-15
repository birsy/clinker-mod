package birsy.clinker.client.render.entity.model;

import birsy.clinker.core.Clinker;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

public class PlaceholderModel<T extends Entity> extends EntityModel<T> {
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation(Clinker.MOD_ID, "placeholder"), "main");
	public final CappinModelPart part;
	private float r, g, b, a = 1.0F;

	public PlaceholderModel (ModelPart root) {
		this.part = CappinModelPart.fromModelPart(root.getChild("part"));
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition part = partdefinition.addOrReplaceChild("part", CubeListBuilder.create().texOffs(0, 0).addBox(-8.0F, -8.0F, -8.0F, 16.0F, 16.0F, 16.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 16.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 64, 32);
	}
	public void setColors(float red, float green, float blue, float alpha) {
		this.r = red;
		this.g = green;
		this.b = blue;
		this.a = alpha;
	}

	public void setColors(float red, float green, float blue) {
		this.setColors(red, green, blue, 1.0F);
	}

	@Override
	public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		this.part.setPos(0.0F, 16.0F, 0.0F);
		this.part.setRotation(0.0F, 0.0F, 0.0F);
		this.part.setScale(1.0F, 1.0F, 1.0F);
		AnimFunctions.look(this.part, netHeadYaw, headPitch, 1.0F, 1.0F);
	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		this.part.render(poseStack, buffer, packedLight, packedOverlay, red * r, green * g, blue * b, alpha * a);
	}
}