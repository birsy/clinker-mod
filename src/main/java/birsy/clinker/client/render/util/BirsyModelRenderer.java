package birsy.clinker.client.render.util;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.util.math.vector.Vector3d;

public class BirsyModelRenderer extends ModelRenderer implements IPosablePart
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

	public Vector3d defaultModelRendererPosition = new Vector3d(this.defaultRotationPointX, this.defaultRotationPointY, this.defaultRotationPointZ);
	public Vector3d modelRendererPosition = new Vector3d(this.rotationPointX, this.rotationPointY, this.rotationPointZ);
	
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
	public void translateRotate(MatrixStack matrixStackIn) {
		matrixStackIn.scale(scaleX, scaleY, scaleZ);
		super.translateRotate(matrixStackIn);
	}

	@Override
	public void setRotationPoint(float rotationPointXIn, float rotationPointYIn, float rotationPointZIn) {
		this.rotationPointX = rotationPointXIn;
		this.rotationPointY = rotationPointYIn;
		this.rotationPointZ = rotationPointZIn;
		
		this.defaultRotationPointX = this.rotationPointX;
        this.defaultRotationPointY = this.rotationPointY;
        this.defaultRotationPointZ = this.rotationPointZ;
	}

	public void setScale(float scaleXIn, float scaleYIn, float scaleZIn) {
		this.scaleX = scaleXIn;
		this.scaleY = scaleYIn;
		this.scaleZ = scaleZIn;

		this.defaultScaleX = this.scaleX;
		this.defaultScaleY = this.scaleY;
		this.defaultScaleZ = this.scaleZ;
	}

	public void setScale(float scaleIn) {
		this.setScale(scaleIn, scaleIn, scaleIn);
	}
	
	public void copyModelAngles(BirsyModelRenderer modelRendererIn) {
		this.rotateAngleX = modelRendererIn.rotateAngleX;
		this.rotateAngleY = modelRendererIn.rotateAngleY;
		this.rotateAngleZ = modelRendererIn.rotateAngleZ;
		this.defaultRotateAngleX = modelRendererIn.rotateAngleX;
		this.defaultRotateAngleY = modelRendererIn.rotateAngleY;
		this.defaultRotateAngleZ = modelRendererIn.rotateAngleZ;

		this.rotationPointX = modelRendererIn.rotationPointX;
		this.rotationPointY = modelRendererIn.rotationPointY;
		this.rotationPointZ = modelRendererIn.rotationPointZ;
	  	this.defaultRotationPointX = modelRendererIn.rotationPointX;
	  	this.defaultRotationPointY = modelRendererIn.rotationPointY;
	  	this.defaultRotationPointZ = modelRendererIn.rotationPointZ;

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
	public void matrixStackFromModel(MatrixStack matrixStack) {
		BirsyModelRenderer parent = this.getParent();
		if (parent != null) {
			parent.matrixStackFromModel(matrixStack);
		}
		this.translateRotate(matrixStack);
	}


	public float getRotationPointX() {
		return this.rotationPointX;
	}
	public float getRotationPointY() {
		return this.rotationPointY;
	}
	public float getRotationPointZ() {
		return this.rotationPointZ;
	}

	public float getRotateAngleX() {
		return this.rotateAngleX;
	}
	public float getRotateAngleY() {
		return this.rotationPointY;
	}
	public float getRotateAngleZ() {
		return this.rotateAngleZ;
	}

	public float getScaleX() {
		return this.scaleX;
	}
	public float getScaleY() {
		return this.scaleY;
	}
	public float getScaleZ() {
		return this.scaleZ;
	}


	public void setRotationPointX(float value) {
		this.rotationPointX = value;
	}
	public void setRotationPointY(float value) {
		this.rotationPointY = value;
	}
	public void setRotationPointZ(float value) {
		this.rotationPointZ = value;
	}

	public void setRotateAngleX(float value) {
		this.rotateAngleX = value;
	}
	public void setRotateAngleY(float value) {
		this.rotateAngleY = value;
	}
	public void setRotateAngleZ(float value) {
		this.rotateAngleZ = value;
	}

	public void setScaleX(float value) {
		this.scaleX = value;
	}
	public void setScaleY(float value) {
		this.scaleY = value;
	}
	public void setScaleZ(float value) {
		this.scaleZ = value;
	}
}	
