package birsy.clinker.client.render.util;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.phys.Vec3;

public class BirsyModelRenderer extends ModelPart
{
	BirsyModelRenderer parent = null;

	public float defaultRotationPointX;
	public float defaultRotationPointY;
	public float defaultRotationPointZ;
	
	public float defaultRotateAngleX;
	public float defaultRotateAngleY;
	public float defaultRotateAngleZ;

	public float defaultScaleX;
	public float defaultScaleY;
	public float defaultScaleZ;

	public float scaleX;
	public float scaleY;
	public float scaleZ;

	public Vec3 defaultModelRendererPosition = new Vec3(this.defaultRotationPointX, this.defaultRotationPointY, this.defaultRotationPointZ);
	public Vec3 modelRendererPosition = new Vec3(this.x, this.y, this.z);
	
	public BirsyModelRenderer(Model model, int texOffX, int texOffY) {
		super(model, texOffX, texOffY);

		this.scaleX = 1.0f;
		this.scaleY = 1.0f;
		this.scaleZ = 1.0f;

		this.defaultScaleX = 1.0f;
		this.defaultScaleY = 1.0f;
		this.defaultScaleZ = 1.0f;
	}

	@Override
	public void translateAndRotate(PoseStack matrixStackIn) {
		matrixStackIn.scale(scaleX, scaleY, scaleZ);
		super.translateAndRotate(matrixStackIn);
	}

	@Override
	public void setPos(float rotationPointXIn, float rotationPointYIn, float rotationPointZIn) {
		this.x = rotationPointXIn;
		this.y = rotationPointYIn;
		this.z = rotationPointZIn;
		
		this.defaultRotationPointX = this.x;
        this.defaultRotationPointY = this.y;
        this.defaultRotationPointZ = this.z;
	}

	public void setScale(float scaleXIn, float scaleYIn, float scaleZIn) {
		this.scaleX = scaleXIn;
		this.scaleY = scaleYIn;
		this.scaleZ = scaleZIn;

		this.defaultScaleX = this.scaleX;
		this.defaultScaleY = this.scaleY;
		this.defaultScaleZ = this.scaleZ;
	}
	
	public void copyModelAngles(BirsyModelRenderer modelRendererIn) {
		this.xRot = modelRendererIn.xRot;
		this.yRot = modelRendererIn.yRot;
		this.zRot = modelRendererIn.zRot;
		this.defaultRotateAngleX = modelRendererIn.xRot;
		this.defaultRotateAngleY = modelRendererIn.yRot;
		this.defaultRotateAngleZ = modelRendererIn.zRot;

		this.x = modelRendererIn.x;
		this.y = modelRendererIn.y;
		this.z = modelRendererIn.z;
	  	this.defaultRotationPointX = modelRendererIn.x;
	  	this.defaultRotationPointY = modelRendererIn.y;
	  	this.defaultRotationPointZ = modelRendererIn.z;

		this.scaleX = modelRendererIn.scaleX;
		this.scaleY = modelRendererIn.scaleY;
		this.scaleZ = modelRendererIn.scaleZ;
		this.defaultScaleX = modelRendererIn.scaleX;
		this.defaultScaleY = modelRendererIn.scaleY;
		this.defaultScaleZ = modelRendererIn.scaleZ;
	}

	public void addChild(BirsyModelRenderer renderer) {
		renderer.setParent(this);
		super.addChild(renderer);
	}

	public void setParent(BirsyModelRenderer parent) {
		this.parent = parent;
	}

	public BirsyModelRenderer getParent() {
		return parent;
	}

	//Thanks to BobMowzie for this clever bit of code.
	public void matrixStackFromModel(PoseStack matrixStack) {
		BirsyModelRenderer parent = this.getParent();
		if (parent != null) {
			parent.matrixStackFromModel(matrixStack);
		}
		this.translateAndRotate(matrixStack);
	}
}	
