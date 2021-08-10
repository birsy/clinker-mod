package birsy.clinker.client.render.entity.model;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import birsy.clinker.common.entity.monster.HyenaEntity;
import net.minecraft.client.renderer.entity.model.SegmentedModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * HyenaModel - doclg
 * Created using Tabula 8.0.0
 */
@OnlyIn(Dist.CLIENT)
public class HyenaModel<T extends Entity> extends SegmentedModel<HyenaEntity> {
    public ModelRenderer hyenaHead;
    public ModelRenderer hyenaBack;
    public ModelRenderer hyenaLegFrontRight;
    public ModelRenderer hyenaLegFrontLeft;
    public ModelRenderer hyenaWaist;
    public ModelRenderer hyenaLegBackRightTop;
    public ModelRenderer hyenaLegBackLeftTop;
    public ModelRenderer hyenaSnout;
    public ModelRenderer hyenaJaw;
    public ModelRenderer hyenaEars;
    public ModelRenderer hyenaScruff;
    public ModelRenderer hyenaTail;
    public ModelRenderer hyenaLegBackRightBottom;
    public ModelRenderer hyenaLegBackLeftBottom;

    public HyenaModel() {
        this.textureWidth = 128;
        this.textureHeight = 64;
        this.hyenaEars = new ModelRenderer(this, 45, 0);
        this.hyenaEars.setRotationPoint(0.0F, -2.5F, -4.0F);
        this.hyenaEars.addBox(-5.0F, -3.0F, -0.5F, 10.0F, 3.0F, 1.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(hyenaEars, 0.17453292519943295F, 0.0F, 0.0F);
        this.hyenaLegBackRightTop = new ModelRenderer(this, 64, 18);
        this.hyenaLegBackRightTop.mirror = true;
        this.hyenaLegBackRightTop.setRotationPoint(4.0F, 10.0F, 10.0F);
        this.hyenaLegBackRightTop.setTextureOffset(1, 20).addBox(-2.0F, -2.5F, -3.5F, 3.0F, 8.0F, 7.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(hyenaLegBackRightTop, 0.2617993877991494F, 0.0F, 0.0F);
        this.hyenaLegBackRightBottom = new ModelRenderer(this, 0, 13);
        this.hyenaLegBackRightBottom.mirror = true;
        this.hyenaLegBackRightBottom.setRotationPoint(0.5F, 4.0F, 2.5F);
        this.hyenaLegBackRightBottom.addBox(-1.0F, 0.0F, -1.0F, 2.0F, 11.0F, 2.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(hyenaLegBackRightBottom, -0.3490658503988659F, 0.0F, 0.0F);
        this.hyenaJaw = new ModelRenderer(this, 27, 0);
        this.hyenaJaw.setRotationPoint(0.0F, 2.5F, -3.5F);
        this.hyenaJaw.addBox(-3.0F, 0.0F, -4.5F, 6.0F, 1.0F, 6.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(hyenaJaw, 0.4363323129985824F, 0.0F, 0.0F);
        this.hyenaSnout = new ModelRenderer(this, 21, 0);
        this.hyenaSnout.setRotationPoint(0.0F, 1.0F, -7.0F);
        this.hyenaSnout.addBox(-2.0F, -1.5F, -2.0F, 4.0F, 3.0F, 2.0F, 0.0F, 0.0F, 0.0F);
        this.hyenaBack = new ModelRenderer(this, 55, 0);
        this.hyenaBack.setRotationPoint(0.0F, 10.0F, -2.0F);
        this.hyenaBack.addBox(-4.5F, -5.0F, -6.0F, 9.0F, 10.0F, 12.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(hyenaBack, 0.23457224414434488F, 0.0F, 0.0F);
        this.hyenaScruff = new ModelRenderer(this, 64, 0);
        this.hyenaScruff.setRotationPoint(0.0F, -5.0F, 0.0F);
        this.hyenaScruff.setTextureOffset(86, 0).addBox(-6.0F, -4.0F, -0.5F, 12.0F, 4.0F, 1.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(hyenaScruff, 0.0F, 1.5707963267948966F, 0.0F);
        this.hyenaTail = new ModelRenderer(this, 64, 5);
        this.hyenaTail.setRotationPoint(0.0F, -1.0F, 5.0F);
        this.hyenaTail.setTextureOffset(97, 5).addBox(-1.5F, -1.5F, 0.0F, 3.0F, 10.0F, 3.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(hyenaTail, 0.6981317007977318F, 0.0F, 0.0F);
        this.hyenaHead = new ModelRenderer(this, 0, 0);
        this.hyenaHead.setRotationPoint(0.0F, 13.0F, -8.0F);
        this.hyenaHead.addBox(-3.5F, -3.5F, -7.0F, 7.0F, 6.0F, 7.0F, 0.0F, 0.0F, 0.0F);
        this.hyenaLegFrontRight = new ModelRenderer(this, 64, 0);
        this.hyenaLegFrontRight.mirror = true;
        this.hyenaLegFrontRight.setRotationPoint(4.5F, 12.0F, -1.0F);
        this.hyenaLegFrontRight.setTextureOffset(111, 0).addBox(-1.0F, -4.0F, -1.0F, 2.0F, 16.0F, 2.0F, 0.0F, 0.0F, 0.0F);
        this.hyenaWaist = new ModelRenderer(this, 17, 7);
        this.hyenaWaist.setRotationPoint(0.0F, 10.5F, 7.0F);
        this.hyenaWaist.addBox(-4.0F, -5.0F, -6.0F, 8.0F, 8.0F, 11.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(hyenaWaist, -0.13962634015954636F, 0.0F, 0.0F);
        this.hyenaLegFrontLeft = new ModelRenderer(this, 64, 0);
        this.hyenaLegFrontLeft.setRotationPoint(-4.5F, 12.0F, -1.0F);
        this.hyenaLegFrontLeft.setTextureOffset(119, 0).addBox(-1.0F, -4.0F, -1.0F, 2.0F, 16.0F, 2.0F, 0.0F, 0.0F, 0.0F);
        this.hyenaLegBackLeftBottom = new ModelRenderer(this, 64, 18);
        this.hyenaLegBackLeftBottom.setRotationPoint(-0.5F, 4.0F, 2.5F);
        this.hyenaLegBackLeftBottom.setTextureOffset(46, 0).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 11.0F, 2.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(hyenaLegBackLeftBottom, -0.3490658503988659F, 0.0F, 0.0F);
        this.hyenaLegBackLeftTop = new ModelRenderer(this, 1, 19);
        this.hyenaLegBackLeftTop.setRotationPoint(-4.0F, 10.0F, 10.0F);
        this.hyenaLegBackLeftTop.addBox(-1.0F, -2.5F, -3.5F, 3.0F, 8.0F, 7.0F, 0.0F, 0.0F, 0.0F);
        this.hyenaLegBackLeftTop.setTextureOffset(90, 19);
        this.setRotateAngle(hyenaLegBackLeftTop, 0.2617993877991494F, 0.0F, 0.0F);
        this.hyenaHead.addChild(this.hyenaEars);
        this.hyenaLegBackRightTop.addChild(this.hyenaLegBackRightBottom);
        this.hyenaHead.addChild(this.hyenaJaw);
        this.hyenaHead.addChild(this.hyenaSnout);
        this.hyenaBack.addChild(this.hyenaScruff);
        this.hyenaWaist.addChild(this.hyenaTail);
        this.hyenaLegBackLeftTop.addChild(this.hyenaLegBackLeftBottom);
    }

    @Override
    public void render(MatrixStack matrixStackIn, IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) { 
        ImmutableList.of(this.hyenaLegBackRightTop, this.hyenaBack, this.hyenaHead, this.hyenaLegFrontRight, this.hyenaWaist, this.hyenaLegFrontLeft, this.hyenaLegBackLeftTop).forEach((modelRenderer) -> { 
            modelRenderer.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
        });
    }
    
	@Override
	public void setRotationAngles(HyenaEntity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch)
	{
		float f = 0.01F * (float)(entityIn.getEntityId());
    	
        this.hyenaHead.rotateAngleX = headPitch * ((float)Math.PI / 180F);
        this.hyenaHead.rotateAngleY = netHeadYaw * ((float)Math.PI / 180F);
        
        this.hyenaLegBackRightTop.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
        this.hyenaLegBackLeftTop.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F + (float)Math.PI) * 1.4F * limbSwingAmount;
        this.hyenaLegFrontRight.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F + (float)Math.PI) * 1.4F * limbSwingAmount;
        this.hyenaLegFrontLeft.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
        
        this.hyenaJaw.rotateAngleX += MathHelper.sin((float)ageInTicks * f) * 4.5F * ((float)Math.PI / 180F);
        this.hyenaJaw.rotateAngleZ = MathHelper.cos((float)ageInTicks * f) * 2.5F * ((float)Math.PI / 180F);
        
        this.hyenaTail.rotateAngleZ = MathHelper.cos((float)ageInTicks * f) * 2.5F * ((float)Math.PI / 180F);
        
        this.hyenaScruff.rotateAngleZ = MathHelper.cos((float)ageInTicks * f) * 2.5F * ((float)Math.PI / 180F);	
	}

    /**
     * This is a helper function from Tabula to set the rotation of model parts
     */
    public void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }

	protected Iterable<ModelRenderer> getHeadParts() {
		return ImmutableList.of(this.hyenaHead, this.hyenaEars, this.hyenaSnout, this.hyenaJaw);
	}

	protected Iterable<ModelRenderer> getBodyParts() {
		return ImmutableList.of(this.hyenaLegBackRightTop, this.hyenaLegBackRightBottom, this.hyenaLegBackLeftTop, this.hyenaLegBackLeftBottom, this.hyenaLegFrontRight, this.hyenaLegFrontLeft, this.hyenaBack, this.hyenaScruff, this.hyenaTail, this.hyenaWaist);
	}

	@Override
	public Iterable<ModelRenderer> getParts() {
		// TODO Auto-generated method stub
		return null;
	}
}
