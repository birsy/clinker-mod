package birsy.clinker.client.render.entity.model;

import birsy.clinker.client.render.entity.model.base.BasicModelPart;
import birsy.clinker.client.render.entity.model.base.DynamicModel;
import birsy.clinker.client.render.entity.model.base.DynamicModelPart;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;

public class PlaceholderModel<T extends Entity> extends EntityModel<T> {
	public final DynamicModelPart part;
	public final DynamicModel skeleton;
	private float r, g, b, a = 1.0F;

	public PlaceholderModel () {
		this.skeleton = new DynamicModel(64, 32);
		//this.part = new DynamicModelPart(skeleton, BasicModelPart.toCubeList(CubeListBuilder.create().texOffs(0, 0).addBox(-8.0F, -8.0F, -8.0F, 16.0F, 16.0F, 16.0F), 64, 32), PartPose.offset(0.0F, 16.0F, 0.0F));
		this.part = new DynamicModelPart(skeleton, 0, 0);
		this.part.addCube("head", -8.0F, -8.0F,-8.0F, 16.0F, 16.0F, 16.0F,0.0F,0.0F, 0.0F);
		this.part.setInitialPosition(0.0F, 16.0F, 0.0F);
		this.part.setInitialRotation(0.0F, 0.0F, 0.0F);
		this.part.setInitialScale(1.0F, 1.0F, 1.0F);

		this.part.createDynamics(1, 0.5F, 1);
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
		skeleton.resetPose();

	}

	public void update() {
		this.part.update();
	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		this.part.update(1);
		this.part.render(poseStack, buffer, packedLight, packedOverlay, red * r, green * g, blue * b, alpha * a);
	}
}