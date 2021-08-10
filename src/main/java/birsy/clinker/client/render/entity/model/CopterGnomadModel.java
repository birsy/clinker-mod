package birsy.clinker.client.render.entity.model;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import birsy.clinker.common.entity.monster.gnomad.AbstractGnomadEntity;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.ArmedModel;
import net.minecraft.client.model.HeadedModel;
import net.minecraft.client.model.AnimationUtils;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * PropellorGnomadModel - birsy
 * Created using Tabula 8.0.0
 */
@OnlyIn(Dist.CLIENT)
public class CopterGnomadModel<T extends AbstractGnomadEntity> extends EntityModel<T> implements ArmedModel, HeadedModel
{
    public ModelPart leftArm;
    public ModelPart rightArm;
    public ModelPart rightLeg;
    public ModelPart leftLeg;
    public ModelPart Neck;
    public ModelPart Torso;
    public ModelPart MainHead;
    public ModelPart Nose;
    public ModelPart FaceMain;
    public ModelPart FaceBottom;
    public ModelPart FaceTop;
    public ModelPart HatBottom;
    public ModelPart Beard;
    public ModelPart HatMiddle;
    public ModelPart HatFlapL;
    public ModelPart HatFlapR;
    public ModelPart HatTop;
    public ModelPart HatPropellor;
    public ModelPart Back;

    public CopterGnomadModel() {
        this.texWidth = 64;
        this.texHeight = 64;
        this.FaceMain = new ModelPart(this, 16, 9);
        this.FaceMain.setPos(0.0F, 0.0F, -7.0F);
        this.FaceMain.addBox(-4.0F, -2.5F, 0.0F, 8.0F, 6.0F, 1.0F, 0.0F, 0.0F, 0.0F);
        this.rightLeg = new ModelPart(this, 8, 0);
        this.rightLeg.setPos(-2.5F, 13.75F, 1.0F);
        this.rightLeg.addBox(-0.5F, 0.0F, -0.5F, 1.0F, 10.0F, 1.0F, 0.25F, 0.25F, 0.25F);
        this.FaceTop = new ModelPart(this, 34, 12);
        this.FaceTop.setPos(0.0F, -2.5F, -7.0F);
        this.FaceTop.addBox(-3.0F, -1.0F, 0.0F, 6.0F, 1.0F, 1.0F, 0.0F, 0.0F, 0.0F);
        this.Beard = new ModelPart(this, 40, 22);
        this.Beard.setPos(0.0F, 2.0F, -5.0F);
        this.Beard.addBox(-4.0F, 0.0F, -0.5F, 8.0F, 10.0F, 1.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(Beard, -0.1563815016444822F, 0.0F, 0.0F);
        this.Neck = new ModelPart(this, 16, 0);
        this.Neck.setPos(0.0F, -2.0F, -5.0F);
        this.Neck.addBox(-1.5F, -1.5F, -2.0F, 3.0F, 3.0F, 6.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(Neck, -0.5235987755982988F, 0.0F, 0.0F);
        this.HatPropellor = new ModelPart(this, 13, 18);
        this.HatPropellor.setPos(0.0F, -1.5F, 0.0F);
        this.HatPropellor.addBox(-8.0F, -0.5F, -1.5F, 16.0F, 1.0F, 3.0F, 0.0F, 0.0F, 0.0F);
        this.leftArm = new ModelPart(this, 0, 0);
        this.leftArm.setPos(4.25F, 1.0F, -1.0F);
        this.leftArm.addBox(0.0F, -0.5F, -0.5F, 1.0F, 10.0F, 1.0F, 0.25F, 0.25F, 0.25F);
        this.Nose = new ModelPart(this, 28, 0);
        this.Nose.setPos(0.0F, -0.2F, -6.7F);
        this.Nose.addBox(-1.0F, -1.0F, -1.0F, 2.0F, 2.0F, 2.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(Nose, -0.3441789165090569F, 0.0F, 0.0F);
        this.HatBottom = new ModelPart(this, 43, 12);
        this.HatBottom.setPos(0.0F, -3.0F, -3.5F);
        this.HatBottom.addBox(-2.5F, -1.0F, -2.5F, 5.0F, 1.0F, 5.0F, 0.0F, 0.0F, 0.0F);
        this.HatMiddle = new ModelPart(this, 0, 14);
        this.HatMiddle.setPos(0.0F, -1.0F, 0.0F);
        this.HatMiddle.addBox(-2.0F, -2.0F, -2.0F, 4.0F, 3.0F, 4.0F, 0.0F, 0.0F, 0.0F);
        this.Back = new ModelPart(this, 24, 32);
        this.Back.setPos(0.0F, -14.0F, -2.0F);
        this.Back.texOffs(24, 43).addBox(-4.0F, -3.0F, -4.0F, 8.0F, 8.0F, 8.0F, 0.0F, 0.0F, 0.0F);
        this.Torso = new ModelPart(this, 0, 28);
        this.Torso.setPos(0.0F, 15.0F, 1.0F);
        this.Torso.addBox(-4.0F, -15.0F, -4.0F, 8.0F, 15.0F, 8.0F, 0.0F, 0.0F, 0.0F);
        this.MainHead = new ModelPart(this, 34, 0);
        this.MainHead.setPos(0.0F, -0.9F, -2.0F);
        this.MainHead.addBox(-3.0F, -3.0F, -6.0F, 6.0F, 6.0F, 6.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(MainHead, 0.5235987755982988F, 0.0F, 0.0F);
        this.FaceBottom = new ModelPart(this, 0, 11);
        this.FaceBottom.setPos(0.0F, 3.5F, -7.0F);
        this.FaceBottom.addBox(-3.0F, 0.0F, 0.0F, 6.0F, 2.0F, 1.0F, 0.0F, 0.0F, 0.0F);
        this.HatFlapR = new ModelPart(this, 20, 22);
        this.HatFlapR.setPos(-2.8F, -0.5F, 0.0F);
        this.HatFlapR.addBox(0.0F, -0.5F, -2.5F, 5.0F, 1.0F, 5.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(HatFlapR, 0.0F, 0.0F, 2.0943951023931953F);
        this.rightArm = new ModelPart(this, 4, 0);
        this.rightArm.setPos(-4.25F, 1.0F, -1.0F);
        this.rightArm.addBox(-1.0F, -0.5F, -0.5F, 1.0F, 10.0F, 1.0F, 0.25F, 0.25F, 0.25F);
        this.leftLeg = new ModelPart(this, 12, 0);
        this.leftLeg.setPos(2.5F, 13.75F, 1.0F);
        this.leftLeg.addBox(-0.5F, 0.0F, -0.5F, 1.0F, 10.0F, 1.0F, 0.25F, 0.25F, 0.25F);
        this.HatTop = new ModelPart(this, 16, 0);
        this.HatTop.setPos(0.0F, -2.0F, 0.0F);
        this.HatTop.addBox(-0.5F, -2.5F, -0.5F, 1.0F, 3.0F, 1.0F, 0.0F, 0.0F, 0.0F);
        this.HatFlapL = new ModelPart(this, 0, 22);
        this.HatFlapL.setPos(2.8F, -0.5F, 0.0F);
        this.HatFlapL.addBox(0.0F, -0.5F, -2.5F, 5.0F, 1.0F, 5.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(HatFlapL, 0.0F, 0.0F, 1.0471975511965976F);
        this.MainHead.addChild(this.FaceMain);
        this.MainHead.addChild(this.FaceTop);
        this.MainHead.addChild(this.Beard);
        this.HatTop.addChild(this.HatPropellor);
        this.MainHead.addChild(this.Nose);
        this.MainHead.addChild(this.HatBottom);
        this.HatBottom.addChild(this.HatMiddle);
        this.Torso.addChild(this.Back);
        this.Neck.addChild(this.MainHead);
        this.MainHead.addChild(this.FaceBottom);
        this.HatBottom.addChild(this.HatFlapR);
        this.HatMiddle.addChild(this.HatTop);
        this.HatBottom.addChild(this.HatFlapL);
    }

    @Override
    public void renderToBuffer(PoseStack matrixStackIn, VertexConsumer bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) { 
        ImmutableList.of(this.rightLeg, this.Neck, this.leftArm, this.Torso, this.rightArm, this.leftLeg).forEach((modelRenderer) -> { 
            modelRenderer.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
        });
    }

    @Override
    public void setupAnim(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
    	float f = 0.01F * (float)(entityIn.getId() % 10);
    	
    	this.HatPropellor.yRot = ageInTicks*2;
    	
    	this.MainHead.yRot = netHeadYaw * ((float)Math.PI / 180F);
        //this.MainHead.rotateAngleX = headPitch * ((float)Math.PI / 180F) - 0.5235987755982988F;
        
        this.HatBottom.xRot = Mth.sin((float)entityIn.tickCount * f) * 4.5F * ((float)Math.PI / 180F);
        this.HatBottom.yRot = 0.0F;
        this.HatBottom.zRot = Mth.cos((float)entityIn.tickCount * f) * 2.5F * ((float)Math.PI / 180F);
        
        
        this.rightLeg.xRot = Mth.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount * 0.5F;
        this.leftLeg.xRot = Mth.cos(limbSwing * 0.6662F + (float)Math.PI) * 1.4F * limbSwingAmount * 0.5F;
        this.rightLeg.yRot = 0.0F;
        this.leftLeg.yRot = 0.0F;
        
    	this.leftArm.xRot = Mth.cos(limbSwing * 0.6662F + (float)Math.PI) * 1.4F * limbSwingAmount;
    	this.rightArm.xRot = Mth.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
    	
        this.rightArm.xRot += Mth.sin(ageInTicks * 0.05F) * 1.005F;
        this.leftArm.xRot -= Mth.sin(ageInTicks * 0.05F) * 1.005F;
        
        AbstractGnomadEntity.ArmPose AbstractGnomadEntity$armpose = entityIn.getArmPose();
        if (AbstractGnomadEntity$armpose == AbstractGnomadEntity.ArmPose.ATTACKING) {
           AnimationUtils.swingWeaponDown(this.rightArm, this.leftArm, entityIn, this.attackTime, ageInTicks);
        } else if (AbstractGnomadEntity$armpose == AbstractGnomadEntity.ArmPose.SPELLCASTING) {
           this.rightArm.z = 0.0F;
           this.rightArm.x = -5.0F;
           this.leftArm.z = 0.0F;
           this.leftArm.x = 5.0F;
           this.rightArm.xRot = Mth.cos(ageInTicks * 0.6662F) * 0.25F;
           this.leftArm.xRot = Mth.cos(ageInTicks * 0.6662F) * 0.25F;
           this.rightArm.zRot = 2.3561945F;
           this.leftArm.zRot = -2.3561945F;
           this.rightArm.yRot = 0.0F;
           this.leftArm.yRot = 0.0F;
        } else if (AbstractGnomadEntity$armpose == AbstractGnomadEntity.ArmPose.BOW_AND_ARROW) {
           this.rightArm.yRot = -0.1F + this.MainHead.yRot;
           this.rightArm.xRot = (-(float)Math.PI / 2F) + this.MainHead.xRot;
           this.leftArm.xRot = -0.9424779F + this.MainHead.xRot;
           this.leftArm.yRot = this.MainHead.yRot - 0.4F;
           this.leftArm.zRot = ((float)Math.PI / 2F);
        } else if (AbstractGnomadEntity$armpose == AbstractGnomadEntity.ArmPose.CROSSBOW_HOLD) {
           AnimationUtils.animateCrossbowHold(this.rightArm, this.leftArm, this.MainHead, true);
        } else if (AbstractGnomadEntity$armpose == AbstractGnomadEntity.ArmPose.CROSSBOW_CHARGE) {
           AnimationUtils.animateCrossbowCharge(this.rightArm, this.leftArm, entityIn, true);
        } else if (AbstractGnomadEntity$armpose == AbstractGnomadEntity.ArmPose.CELEBRATING) {
           this.rightArm.z = 0.0F;
           this.rightArm.x = -5.0F;
           this.rightArm.xRot = Mth.cos(ageInTicks * 0.6662F) * 0.05F;
           this.rightArm.zRot = 2.670354F;
           this.rightArm.yRot = 0.0F;
           this.leftArm.z = 0.0F;
           this.leftArm.x = 5.0F;
           this.leftArm.xRot = Mth.cos(ageInTicks * 0.6662F) * 0.05F;
           this.leftArm.zRot = -2.3561945F;
           this.leftArm.yRot = 0.0F;
        } else if (AbstractGnomadEntity$armpose == AbstractGnomadEntity.ArmPose.SHAKING) {
           this.MainHead.zRot = Mth.cos(ageInTicks * 0.6662F) * 0.05F;
           this.FaceMain.zRot = Mth.cos(ageInTicks * 0.6662F) * 0.05F;   
        }
    }

    public void setRotateAngle(ModelPart modelRenderer, float x, float y, float z) {
        modelRenderer.xRot = x;
        modelRenderer.yRot = y;
        modelRenderer.zRot = z;
    }
    
    @Override
	public ModelPart getHead() {
		return this.MainHead;
	}

	@Override
	public void translateToHand(HumanoidArm sideIn, PoseStack matrixStackIn) {
		this.getArmForSide(sideIn).translateAndRotate(matrixStackIn);
	}
	
	protected ModelPart getArmForSide(HumanoidArm side)
	{
		return side == HumanoidArm.LEFT ? this.leftArm : this.rightArm;
	}
}
