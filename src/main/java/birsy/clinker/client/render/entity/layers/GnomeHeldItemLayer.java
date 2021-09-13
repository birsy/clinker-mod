package birsy.clinker.client.render.entity.layers;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.entity.model.IHasArm;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.HandSide;
import net.minecraft.util.math.vector.Vector3f;

public class GnomeHeldItemLayer<T extends LivingEntity, M extends EntityModel<T> & IHasArm> extends LayerRenderer<T, M> {
	public GnomeHeldItemLayer(IEntityRenderer<T, M> p_i50934_1_) {
		super(p_i50934_1_);
	}

	public void render(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, T entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
		boolean rightHanded = entitylivingbaseIn.getPrimaryHand() == HandSide.RIGHT;
		ItemStack leftHandItem = rightHanded ? entitylivingbaseIn.getHeldItemOffhand() : entitylivingbaseIn.getHeldItemMainhand();
		ItemStack rightHandItem = rightHanded ? entitylivingbaseIn.getHeldItemMainhand() : entitylivingbaseIn.getHeldItemOffhand();
		if (!leftHandItem.isEmpty() || !rightHandItem.isEmpty()) {
			matrixStackIn.push();
			this.renderItemInHand(entitylivingbaseIn, rightHandItem, ItemCameraTransforms.TransformType.THIRD_PERSON_RIGHT_HAND, HandSide.RIGHT, matrixStackIn, bufferIn, packedLightIn);
			this.renderItemInHand(entitylivingbaseIn, leftHandItem, ItemCameraTransforms.TransformType.THIRD_PERSON_LEFT_HAND, HandSide.LEFT, matrixStackIn, bufferIn, packedLightIn);
			matrixStackIn.pop();
		}
	}

	private void renderItemInHand(LivingEntity entity, ItemStack itemStack, ItemCameraTransforms.TransformType transformType, HandSide handSide, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn) {
		if (!itemStack.isEmpty()) {
			matrixStackIn.push();
			this.getEntityModel().translateHand(handSide, matrixStackIn);
			matrixStackIn.rotate(Vector3f.XP.rotationDegrees(-90.0F));
			matrixStackIn.rotate(Vector3f.YP.rotationDegrees(180.0F));

			boolean leftHanded = handSide == HandSide.LEFT;
			matrixStackIn.translate((float)(leftHanded ? -1 : 1) / 16.0F, 0, -0.625D);
			Minecraft.getInstance().getFirstPersonRenderer().renderItemSide(entity, itemStack, transformType, leftHanded, matrixStackIn, bufferIn, packedLightIn);
			matrixStackIn.pop();
		}
	}
}
