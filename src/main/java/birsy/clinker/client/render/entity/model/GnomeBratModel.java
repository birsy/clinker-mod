package birsy.clinker.client.render.entity.model;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.ArmedModel;
import net.minecraft.client.model.HeadedModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import Entity;

/**
 * GnomeBrat - birsy
 * Created using Tabula 8.0.0
 */
@OnlyIn(Dist.CLIENT)
public class GnomeBratModel<T extends Entity> extends EntityModel<T> implements ArmedModel, HeadedModel
{
    public ModelPart bratLeftLeg;
    public ModelPart bratRightLeg;
    public ModelPart bratBelly;
    public ModelPart bratChest;
    public ModelPart bratHead;
    public ModelPart bratRightArm;
    public ModelPart bratLeftArm;
    public ModelPart bratFace;
    public ModelPart bratHatBottom;
    public ModelPart bratNose;
    public ModelPart bratHatTop;

    public GnomeBratModel() {
        this.texWidth = 64;
        this.texHeight = 32;
        this.bratNose = new ModelPart(this, 8, 0);
        this.bratNose.setPos(0.0F, -1.0F, -0.5F);
        this.bratNose.addBox(-0.5F, 0.0F, -1.0F, 1.0F, 2.0F, 1.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(bratNose, -0.19547687289441354F, 0.0F, 0.0F);
        this.bratLeftLeg = new ModelPart(this, 0, 0);
        this.bratLeftLeg.setPos(3.0F, 17.0F, 0.0F);
        this.bratLeftLeg.addBox(0.0F, 0.0F, -0.5F, 1.0F, 7.0F, 1.0F, 0.0F, 0.0F, 0.0F);
        this.bratChest = new ModelPart(this, 32, 0);
        this.bratChest.setPos(0.0F, 14.5F, 0.0F);
        this.bratChest.addBox(-3.0F, -4.0F, -2.0F, 6.0F, 5.0F, 4.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(bratChest, 0.15358897750445236F, 0.0F, 0.0F);
        this.bratFace = new ModelPart(this, 22, 9);
        this.bratFace.setPos(0.0F, -2.5F, -2.5F);
        this.bratFace.addBox(-3.0F, -2.5F, -1.0F, 6.0F, 7.0F, 1.0F, -0.5F, -0.7F, 0.0F);
        this.bratBelly = new ModelPart(this, 8, 0);
        this.bratBelly.setPos(0.0F, 14.5F, 0.0F);
        this.bratBelly.addBox(-3.5F, 0.0F, -2.5F, 7.0F, 4.0F, 5.0F, 0.0F, 0.0F, 0.0F);
        this.bratHead = new ModelPart(this, 0, 9);
        this.bratHead.setPos(0.0F, 10.4F, -1.3F);
        this.bratHead.addBox(-3.0F, -5.0F, -3.0F, 6.0F, 6.0F, 5.0F, -0.75F, -0.75F, -0.5F);
        this.bratRightArm = new ModelPart(this, 52, 0);
        this.bratRightArm.setPos(-3.5F, -3.0F, 0.0F);
        this.bratRightArm.addBox(-0.5F, -0.5F, -0.5F, 1.0F, 6.0F, 1.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(bratRightArm, -0.3490658503988659F, 0.0F, 0.4363323129985824F);
        this.bratLeftArm = new ModelPart(this, 56, 0);
        this.bratLeftArm.setPos(3.5F, -3.0F, 0.0F);
        this.bratLeftArm.addBox(-0.5F, -0.5F, -0.5F, 1.0F, 6.0F, 1.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(bratLeftArm, -0.3490658503988659F, 0.0F, -0.4363323129985824F);
        this.bratRightLeg = new ModelPart(this, 4, 0);
        this.bratRightLeg.setPos(-3.0F, 17.0F, 0.0F);
        this.bratRightLeg.addBox(-1.0F, 0.0F, -0.5F, 1.0F, 7.0F, 1.0F, 0.0F, 0.0F, 0.0F);
        this.bratHatBottom = new ModelPart(this, 36, 9);
        this.bratHatBottom.setPos(0.0F, -4.2F, -0.7F);
        this.bratHatBottom.addBox(-2.0F, -1.0F, -2.0F, 4.0F, 1.0F, 4.0F, 0.0F, 0.0F, 0.0F);
        this.bratHatTop = new ModelPart(this, 49, 7);
        this.bratHatTop.setPos(0.0F, -0.5F, 0.0F);
        this.bratHatTop.addBox(-1.5F, -2.0F, -1.5F, 3.0F, 2.0F, 3.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(bratHatTop, -0.27366763203903305F, 0.0F, 0.1563815016444822F);
        this.bratFace.addChild(this.bratNose);
        this.bratHead.addChild(this.bratFace);
        this.bratChest.addChild(this.bratRightArm);
        this.bratChest.addChild(this.bratLeftArm);
        this.bratHead.addChild(this.bratHatBottom);
        this.bratHatBottom.addChild(this.bratHatTop);
    }
    
    protected Iterable<ModelPart> getHeadParts() {
    	return ImmutableList.of(this.bratHead, this.bratFace);
    }

    @Override
    public void renderToBuffer(PoseStack matrixStackIn, VertexConsumer bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) { 
        ImmutableList.of(this.bratLeftLeg, this.bratChest, this.bratBelly, this.bratHead, this.bratRightLeg).forEach((modelRenderer) -> { 
            modelRenderer.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
        });
    }

    @Override
    public void setupAnim(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch)
    {
    	float f = 0.01F * (float)(entityIn.getId() % 10);
    	
    	this.bratHead.yRot = netHeadYaw * ((float)Math.PI / 180F);
        this.bratHead.xRot = headPitch * ((float)Math.PI / 180F);
        
        this.bratHatBottom.xRot = Mth.sin((float)entityIn.tickCount * f) * 4.5F * ((float)Math.PI / 180F);
        this.bratHatBottom.yRot = 0.0F;
        this.bratHatBottom.zRot = Mth.cos((float)entityIn.tickCount * f) * 2.5F * ((float)Math.PI / 180F);
        
        this.bratRightLeg.xRot = Mth.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount * 0.5F;
        this.bratLeftLeg.xRot = Mth.cos(limbSwing * 0.6662F + (float)Math.PI) * 1.4F * limbSwingAmount * 0.5F;
        this.bratRightLeg.yRot = 0.0F;
        this.bratLeftLeg.yRot = 0.0F;
        
    	this.bratRightArm.xRot = Mth.cos(limbSwing * 0.5662F + (float)Math.PI) * 1.4F * limbSwingAmount;
    	this.bratLeftArm.xRot = Mth.cos(limbSwing * 0.5662F) * 1.4F * limbSwingAmount;
    	
    	bratRightArm.zRot += Mth.cos(ageInTicks * 0.09F) * 0.05F + 0.05F;
    	bratLeftArm.zRot -= Mth.cos(ageInTicks * 0.09F) * 0.05F + 0.05F;
        bratRightArm.xRot += Mth.sin(ageInTicks * 0.067F) * 0.05F;
        bratLeftArm.xRot -= Mth.sin(ageInTicks * 0.067F) * 0.05F;
    }

    /**
     * This is a helper function from Tabula to set the rotation of model parts
     */
    public void setRotateAngle(ModelPart modelRenderer, float x, float y, float z) {
        modelRenderer.xRot = x;
        modelRenderer.yRot = y;
        modelRenderer.zRot = z;
    }

	@Override
	public ModelPart getHead() {
		return this.bratHead;
	}

	@Override
	public void translateToHand(HumanoidArm sideIn, PoseStack matrixStackIn) {
		this.getArmForSide(sideIn).translateAndRotate(matrixStackIn);
	}
	
	protected ModelPart getArmForSide(HumanoidArm side)
	{
		return side == HumanoidArm.LEFT ? this.bratLeftArm : this.bratRightArm;
	}
}
