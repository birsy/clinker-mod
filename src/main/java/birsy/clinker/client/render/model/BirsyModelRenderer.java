package birsy.clinker.client.render.model;

import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.util.math.vector.Vector3d;

public class BirsyModelRenderer extends ModelRenderer
{
	public float defaultRotationPointX;
	public float defaultRotationPointY;
	public float defaultRotationPointZ;
	
	public float defaultRotateAngleX;
	public float defaultRotateAngleY;
	public float defaultRotateAngleZ;
	
	public Vector3d defaultModelRendererPosition = new Vector3d(this.defaultRotationPointX, this.defaultRotationPointY, this.defaultRotationPointZ);
	public Vector3d modelRendererPosition = new Vector3d(this.rotationPointX, this.rotationPointY, this.rotationPointZ);
	
	public BirsyModelRenderer(Model model, int texOffX, int texOffY) {
		super(model, texOffX, texOffY);
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
	
	public void copyModelAngles(BirsyModelRenderer modelRendererIn) {
		this.rotateAngleX = modelRendererIn.rotateAngleX;
		this.rotateAngleY = modelRendererIn.rotateAngleY;
		this.rotateAngleZ = modelRendererIn.rotateAngleZ;
		
		this.rotationPointX = modelRendererIn.rotationPointX;
		this.rotationPointY = modelRendererIn.rotationPointY;
		this.rotationPointZ = modelRendererIn.rotationPointZ;
	  	
	  	this.defaultRotateAngleX = modelRendererIn.rotateAngleX;
	  	this.defaultRotateAngleY = modelRendererIn.rotateAngleY;
	  	this.defaultRotateAngleZ = modelRendererIn.rotateAngleZ;
	  	
	  	this.defaultRotationPointX = modelRendererIn.rotationPointX;
	  	this.defaultRotationPointY = modelRendererIn.rotationPointY;
	  	this.defaultRotationPointZ = modelRendererIn.rotationPointZ;
	}
}	
