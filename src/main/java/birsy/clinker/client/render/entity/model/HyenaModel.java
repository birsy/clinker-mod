package birsy.clinker.client.render.entity.model;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import birsy.clinker.common.entity.monster.HyenaEntity;
import net.minecraft.client.model.ListModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.Entity;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import Entity;

/**
 * HyenaModel - doclg
 * Created using Tabula 8.0.0
 */
@OnlyIn(Dist.CLIENT)
public class HyenaModel<T extends Entity> extends ListModel<HyenaEntity> {
    public ModelPart hyenaHead;
    public ModelPart hyenaBack;
    public ModelPart hyenaLegFrontRight;
    public ModelPart hyenaLegFrontLeft;
    public ModelPart hyenaWaist;
    public ModelPart hyenaLegBackRightTop;
    public ModelPart hyenaLegBackLeftTop;
    public ModelPart hyenaSnout;
    public ModelPart hyenaJaw;
    public ModelPart hyenaEars;
    public ModelPart hyenaScruff;
    public ModelPart hyenaTail;
    public ModelPart hyenaLegBackRightBottom;
    public ModelPart hyenaLegBackLeftBottom;

    public HyenaModel() {
        this.texWidth = 128;
        this.texHeight = 64;
        this.hyenaEars = new ModelPart(this, 45, 0);
        this.hyenaEars.setPos(0.0F, -2.5F, -4.0F);
        this.hyenaEars.addBox(-5.0F, -3.0F, -0.5F, 10.0F, 3.0F, 1.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(hyenaEars, 0.17453292519943295F, 0.0F, 0.0F);
        this.hyenaLegBackRightTop = new ModelPart(this, 64, 18);
        this.hyenaLegBackRightTop.mirror = true;
        this.hyenaLegBackRightTop.setPos(4.0F, 10.0F, 10.0F);
        this.hyenaLegBackRightTop.texOffs(1, 20).addBox(-2.0F, -2.5F, -3.5F, 3.0F, 8.0F, 7.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(hyenaLegBackRightTop, 0.2617993877991494F, 0.0F, 0.0F);
        this.hyenaLegBackRightBottom = new ModelPart(this, 0, 13);
        this.hyenaLegBackRightBottom.mirror = true;
        this.hyenaLegBackRightBottom.setPos(0.5F, 4.0F, 2.5F);
        this.hyenaLegBackRightBottom.addBox(-1.0F, 0.0F, -1.0F, 2.0F, 11.0F, 2.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(hyenaLegBackRightBottom, -0.3490658503988659F, 0.0F, 0.0F);
        this.hyenaJaw = new ModelPart(this, 27, 0);
        this.hyenaJaw.setPos(0.0F, 2.5F, -3.5F);
        this.hyenaJaw.addBox(-3.0F, 0.0F, -4.5F, 6.0F, 1.0F, 6.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(hyenaJaw, 0.4363323129985824F, 0.0F, 0.0F);
        this.hyenaSnout = new ModelPart(this, 21, 0);
        this.hyenaSnout.setPos(0.0F, 1.0F, -7.0F);
        this.hyenaSnout.addBox(-2.0F, -1.5F, -2.0F, 4.0F, 3.0F, 2.0F, 0.0F, 0.0F, 0.0F);
        this.hyenaBack = new ModelPart(this, 55, 0);
        this.hyenaBack.setPos(0.0F, 10.0F, -2.0F);
        this.hyenaBack.addBox(-4.5F, -5.0F, -6.0F, 9.0F, 10.0F, 12.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(hyenaBack, 0.23457224414434488F, 0.0F, 0.0F);
        this.hyenaScruff = new ModelPart(this, 64, 0);
        this.hyenaScruff.setPos(0.0F, -5.0F, 0.0F);
        this.hyenaScruff.texOffs(86, 0).addBox(-6.0F, -4.0F, -0.5F, 12.0F, 4.0F, 1.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(hyenaScruff, 0.0F, 1.5707963267948966F, 0.0F);
        this.hyenaTail = new ModelPart(this, 64, 5);
        this.hyenaTail.setPos(0.0F, -1.0F, 5.0F);
        this.hyenaTail.texOffs(97, 5).addBox(-1.5F, -1.5F, 0.0F, 3.0F, 10.0F, 3.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(hyenaTail, 0.6981317007977318F, 0.0F, 0.0F);
        this.hyenaHead = new ModelPart(this, 0, 0);
        this.hyenaHead.setPos(0.0F, 13.0F, -8.0F);
        this.hyenaHead.addBox(-3.5F, -3.5F, -7.0F, 7.0F, 6.0F, 7.0F, 0.0F, 0.0F, 0.0F);
        this.hyenaLegFrontRight = new ModelPart(this, 64, 0);
        this.hyenaLegFrontRight.mirror = true;
        this.hyenaLegFrontRight.setPos(4.5F, 12.0F, -1.0F);
        this.hyenaLegFrontRight.texOffs(111, 0).addBox(-1.0F, -4.0F, -1.0F, 2.0F, 16.0F, 2.0F, 0.0F, 0.0F, 0.0F);
        this.hyenaWaist = new ModelPart(this, 17, 7);
        this.hyenaWaist.setPos(0.0F, 10.5F, 7.0F);
        this.hyenaWaist.addBox(-4.0F, -5.0F, -6.0F, 8.0F, 8.0F, 11.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(hyenaWaist, -0.13962634015954636F, 0.0F, 0.0F);
        this.hyenaLegFrontLeft = new ModelPart(this, 64, 0);
        this.hyenaLegFrontLeft.setPos(-4.5F, 12.0F, -1.0F);
        this.hyenaLegFrontLeft.texOffs(119, 0).addBox(-1.0F, -4.0F, -1.0F, 2.0F, 16.0F, 2.0F, 0.0F, 0.0F, 0.0F);
        this.hyenaLegBackLeftBottom = new ModelPart(this, 64, 18);
        this.hyenaLegBackLeftBottom.setPos(-0.5F, 4.0F, 2.5F);
        this.hyenaLegBackLeftBottom.texOffs(46, 0).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 11.0F, 2.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(hyenaLegBackLeftBottom, -0.3490658503988659F, 0.0F, 0.0F);
        this.hyenaLegBackLeftTop = new ModelPart(this, 1, 19);
        this.hyenaLegBackLeftTop.setPos(-4.0F, 10.0F, 10.0F);
        this.hyenaLegBackLeftTop.addBox(-1.0F, -2.5F, -3.5F, 3.0F, 8.0F, 7.0F, 0.0F, 0.0F, 0.0F);
        this.hyenaLegBackLeftTop.texOffs(90, 19);
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
    public void renderToBuffer(PoseStack matrixStackIn, VertexConsumer bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) { 
        ImmutableList.of(this.hyenaLegBackRightTop, this.hyenaBack, this.hyenaHead, this.hyenaLegFrontRight, this.hyenaWaist, this.hyenaLegFrontLeft, this.hyenaLegBackLeftTop).forEach((modelRenderer) -> { 
            modelRenderer.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
        });
    }
    
	@Override
	public void setupAnim(HyenaEntity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch)
	{
		float f = 0.01F * (float)(entityIn.getId());
    	
        this.hyenaHead.xRot = headPitch * ((float)Math.PI / 180F);
        this.hyenaHead.yRot = netHeadYaw * ((float)Math.PI / 180F);
        
        this.hyenaLegBackRightTop.xRot = Mth.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
        this.hyenaLegBackLeftTop.xRot = Mth.cos(limbSwing * 0.6662F + (float)Math.PI) * 1.4F * limbSwingAmount;
        this.hyenaLegFrontRight.xRot = Mth.cos(limbSwing * 0.6662F + (float)Math.PI) * 1.4F * limbSwingAmount;
        this.hyenaLegFrontLeft.xRot = Mth.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
        
        this.hyenaJaw.xRot += Mth.sin((float)ageInTicks * f) * 4.5F * ((float)Math.PI / 180F);
        this.hyenaJaw.zRot = Mth.cos((float)ageInTicks * f) * 2.5F * ((float)Math.PI / 180F);
        
        this.hyenaTail.zRot = Mth.cos((float)ageInTicks * f) * 2.5F * ((float)Math.PI / 180F);
        
        this.hyenaScruff.zRot = Mth.cos((float)ageInTicks * f) * 2.5F * ((float)Math.PI / 180F);	
	}

    /**
     * This is a helper function from Tabula to set the rotation of model parts
     */
    public void setRotateAngle(ModelPart modelRenderer, float x, float y, float z) {
        modelRenderer.xRot = x;
        modelRenderer.yRot = y;
        modelRenderer.zRot = z;
    }

	protected Iterable<ModelPart> getHeadParts() {
		return ImmutableList.of(this.hyenaHead, this.hyenaEars, this.hyenaSnout, this.hyenaJaw);
	}

	protected Iterable<ModelPart> getBodyParts() {
		return ImmutableList.of(this.hyenaLegBackRightTop, this.hyenaLegBackRightBottom, this.hyenaLegBackLeftTop, this.hyenaLegBackLeftBottom, this.hyenaLegFrontRight, this.hyenaLegFrontLeft, this.hyenaBack, this.hyenaScruff, this.hyenaTail, this.hyenaWaist);
	}

	@Override
	public Iterable<ModelPart> parts() {
		// TODO Auto-generated method stub
		return null;
	}
}
