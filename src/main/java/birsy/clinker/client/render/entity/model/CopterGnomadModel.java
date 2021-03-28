package birsy.clinker.client.render.entity.model;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import birsy.clinker.common.entity.monster.AbstractGnomadEntity;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.entity.model.IHasArm;
import net.minecraft.client.renderer.entity.model.IHasHead;
import net.minecraft.client.renderer.model.ModelHelper;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.util.HandSide;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * PropellorGnomadModel - birsy
 * Created using Tabula 8.0.0
 */
@OnlyIn(Dist.CLIENT)
public class CopterGnomadModel<T extends AbstractGnomadEntity> extends EntityModel<T> implements IHasArm, IHasHead
{
    public ModelRenderer leftArm;
    public ModelRenderer rightArm;
    public ModelRenderer rightLeg;
    public ModelRenderer leftLeg;
    public ModelRenderer Neck;
    public ModelRenderer Torso;
    public ModelRenderer MainHead;
    public ModelRenderer Nose;
    public ModelRenderer FaceMain;
    public ModelRenderer FaceBottom;
    public ModelRenderer FaceTop;
    public ModelRenderer HatBottom;
    public ModelRenderer Beard;
    public ModelRenderer HatMiddle;
    public ModelRenderer HatFlapL;
    public ModelRenderer HatFlapR;
    public ModelRenderer HatTop;
    public ModelRenderer HatPropellor;
    public ModelRenderer Back;

    public CopterGnomadModel() {
        this.textureWidth = 64;
        this.textureHeight = 64;
        this.FaceMain = new ModelRenderer(this, 16, 9);
        this.FaceMain.setRotationPoint(0.0F, 0.0F, -7.0F);
        this.FaceMain.addBox(-4.0F, -2.5F, 0.0F, 8.0F, 6.0F, 1.0F, 0.0F, 0.0F, 0.0F);
        this.rightLeg = new ModelRenderer(this, 8, 0);
        this.rightLeg.setRotationPoint(-2.5F, 13.75F, 1.0F);
        this.rightLeg.addBox(-0.5F, 0.0F, -0.5F, 1.0F, 10.0F, 1.0F, 0.25F, 0.25F, 0.25F);
        this.FaceTop = new ModelRenderer(this, 34, 12);
        this.FaceTop.setRotationPoint(0.0F, -2.5F, -7.0F);
        this.FaceTop.addBox(-3.0F, -1.0F, 0.0F, 6.0F, 1.0F, 1.0F, 0.0F, 0.0F, 0.0F);
        this.Beard = new ModelRenderer(this, 40, 22);
        this.Beard.setRotationPoint(0.0F, 2.0F, -5.0F);
        this.Beard.addBox(-4.0F, 0.0F, -0.5F, 8.0F, 10.0F, 1.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(Beard, -0.1563815016444822F, 0.0F, 0.0F);
        this.Neck = new ModelRenderer(this, 16, 0);
        this.Neck.setRotationPoint(0.0F, -2.0F, -5.0F);
        this.Neck.addBox(-1.5F, -1.5F, -2.0F, 3.0F, 3.0F, 6.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(Neck, -0.5235987755982988F, 0.0F, 0.0F);
        this.HatPropellor = new ModelRenderer(this, 13, 18);
        this.HatPropellor.setRotationPoint(0.0F, -1.5F, 0.0F);
        this.HatPropellor.addBox(-8.0F, -0.5F, -1.5F, 16.0F, 1.0F, 3.0F, 0.0F, 0.0F, 0.0F);
        this.leftArm = new ModelRenderer(this, 0, 0);
        this.leftArm.setRotationPoint(4.25F, 1.0F, -1.0F);
        this.leftArm.addBox(0.0F, -0.5F, -0.5F, 1.0F, 10.0F, 1.0F, 0.25F, 0.25F, 0.25F);
        this.Nose = new ModelRenderer(this, 28, 0);
        this.Nose.setRotationPoint(0.0F, -0.2F, -6.7F);
        this.Nose.addBox(-1.0F, -1.0F, -1.0F, 2.0F, 2.0F, 2.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(Nose, -0.3441789165090569F, 0.0F, 0.0F);
        this.HatBottom = new ModelRenderer(this, 43, 12);
        this.HatBottom.setRotationPoint(0.0F, -3.0F, -3.5F);
        this.HatBottom.addBox(-2.5F, -1.0F, -2.5F, 5.0F, 1.0F, 5.0F, 0.0F, 0.0F, 0.0F);
        this.HatMiddle = new ModelRenderer(this, 0, 14);
        this.HatMiddle.setRotationPoint(0.0F, -1.0F, 0.0F);
        this.HatMiddle.addBox(-2.0F, -2.0F, -2.0F, 4.0F, 3.0F, 4.0F, 0.0F, 0.0F, 0.0F);
        this.Back = new ModelRenderer(this, 24, 32);
        this.Back.setRotationPoint(0.0F, -14.0F, -2.0F);
        this.Back.setTextureOffset(24, 43).addBox(-4.0F, -3.0F, -4.0F, 8.0F, 8.0F, 8.0F, 0.0F, 0.0F, 0.0F);
        this.Torso = new ModelRenderer(this, 0, 28);
        this.Torso.setRotationPoint(0.0F, 15.0F, 1.0F);
        this.Torso.addBox(-4.0F, -15.0F, -4.0F, 8.0F, 15.0F, 8.0F, 0.0F, 0.0F, 0.0F);
        this.MainHead = new ModelRenderer(this, 34, 0);
        this.MainHead.setRotationPoint(0.0F, -0.9F, -2.0F);
        this.MainHead.addBox(-3.0F, -3.0F, -6.0F, 6.0F, 6.0F, 6.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(MainHead, 0.5235987755982988F, 0.0F, 0.0F);
        this.FaceBottom = new ModelRenderer(this, 0, 11);
        this.FaceBottom.setRotationPoint(0.0F, 3.5F, -7.0F);
        this.FaceBottom.addBox(-3.0F, 0.0F, 0.0F, 6.0F, 2.0F, 1.0F, 0.0F, 0.0F, 0.0F);
        this.HatFlapR = new ModelRenderer(this, 20, 22);
        this.HatFlapR.setRotationPoint(-2.8F, -0.5F, 0.0F);
        this.HatFlapR.addBox(0.0F, -0.5F, -2.5F, 5.0F, 1.0F, 5.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(HatFlapR, 0.0F, 0.0F, 2.0943951023931953F);
        this.rightArm = new ModelRenderer(this, 4, 0);
        this.rightArm.setRotationPoint(-4.25F, 1.0F, -1.0F);
        this.rightArm.addBox(-1.0F, -0.5F, -0.5F, 1.0F, 10.0F, 1.0F, 0.25F, 0.25F, 0.25F);
        this.leftLeg = new ModelRenderer(this, 12, 0);
        this.leftLeg.setRotationPoint(2.5F, 13.75F, 1.0F);
        this.leftLeg.addBox(-0.5F, 0.0F, -0.5F, 1.0F, 10.0F, 1.0F, 0.25F, 0.25F, 0.25F);
        this.HatTop = new ModelRenderer(this, 16, 0);
        this.HatTop.setRotationPoint(0.0F, -2.0F, 0.0F);
        this.HatTop.addBox(-0.5F, -2.5F, -0.5F, 1.0F, 3.0F, 1.0F, 0.0F, 0.0F, 0.0F);
        this.HatFlapL = new ModelRenderer(this, 0, 22);
        this.HatFlapL.setRotationPoint(2.8F, -0.5F, 0.0F);
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
    public void render(MatrixStack matrixStackIn, IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) { 
        ImmutableList.of(this.rightLeg, this.Neck, this.leftArm, this.Torso, this.rightArm, this.leftLeg).forEach((modelRenderer) -> { 
            modelRenderer.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
        });
    }

    @Override
    public void setRotationAngles(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
    	float f = 0.01F * (float)(entityIn.getEntityId() % 10);
    	
    	this.HatPropellor.rotateAngleY = ageInTicks*2;
    	
    	this.MainHead.rotateAngleY = netHeadYaw * ((float)Math.PI / 180F);
        //this.MainHead.rotateAngleX = headPitch * ((float)Math.PI / 180F) - 0.5235987755982988F;
        
        this.HatBottom.rotateAngleX = MathHelper.sin((float)entityIn.ticksExisted * f) * 4.5F * ((float)Math.PI / 180F);
        this.HatBottom.rotateAngleY = 0.0F;
        this.HatBottom.rotateAngleZ = MathHelper.cos((float)entityIn.ticksExisted * f) * 2.5F * ((float)Math.PI / 180F);
        
        
        this.rightLeg.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount * 0.5F;
        this.leftLeg.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F + (float)Math.PI) * 1.4F * limbSwingAmount * 0.5F;
        this.rightLeg.rotateAngleY = 0.0F;
        this.leftLeg.rotateAngleY = 0.0F;
        
    	this.leftArm.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F + (float)Math.PI) * 1.4F * limbSwingAmount;
    	this.rightArm.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
    	
        this.rightArm.rotateAngleX += MathHelper.sin(ageInTicks * 0.05F) * 1.005F;
        this.leftArm.rotateAngleX -= MathHelper.sin(ageInTicks * 0.05F) * 1.005F;
        
        AbstractGnomadEntity.ArmPose AbstractGnomadEntity$armpose = entityIn.getArmPose();
        if (AbstractGnomadEntity$armpose == AbstractGnomadEntity.ArmPose.ATTACKING) {
           ModelHelper.func_239103_a_(this.rightArm, this.leftArm, entityIn, this.swingProgress, ageInTicks);
        } else if (AbstractGnomadEntity$armpose == AbstractGnomadEntity.ArmPose.SPELLCASTING) {
           this.rightArm.rotationPointZ = 0.0F;
           this.rightArm.rotationPointX = -5.0F;
           this.leftArm.rotationPointZ = 0.0F;
           this.leftArm.rotationPointX = 5.0F;
           this.rightArm.rotateAngleX = MathHelper.cos(ageInTicks * 0.6662F) * 0.25F;
           this.leftArm.rotateAngleX = MathHelper.cos(ageInTicks * 0.6662F) * 0.25F;
           this.rightArm.rotateAngleZ = 2.3561945F;
           this.leftArm.rotateAngleZ = -2.3561945F;
           this.rightArm.rotateAngleY = 0.0F;
           this.leftArm.rotateAngleY = 0.0F;
        } else if (AbstractGnomadEntity$armpose == AbstractGnomadEntity.ArmPose.BOW_AND_ARROW) {
           this.rightArm.rotateAngleY = -0.1F + this.MainHead.rotateAngleY;
           this.rightArm.rotateAngleX = (-(float)Math.PI / 2F) + this.MainHead.rotateAngleX;
           this.leftArm.rotateAngleX = -0.9424779F + this.MainHead.rotateAngleX;
           this.leftArm.rotateAngleY = this.MainHead.rotateAngleY - 0.4F;
           this.leftArm.rotateAngleZ = ((float)Math.PI / 2F);
        } else if (AbstractGnomadEntity$armpose == AbstractGnomadEntity.ArmPose.CROSSBOW_HOLD) {
           ModelHelper.func_239104_a_(this.rightArm, this.leftArm, this.MainHead, true);
        } else if (AbstractGnomadEntity$armpose == AbstractGnomadEntity.ArmPose.CROSSBOW_CHARGE) {
           ModelHelper.func_239102_a_(this.rightArm, this.leftArm, entityIn, true);
        } else if (AbstractGnomadEntity$armpose == AbstractGnomadEntity.ArmPose.CELEBRATING) {
           this.rightArm.rotationPointZ = 0.0F;
           this.rightArm.rotationPointX = -5.0F;
           this.rightArm.rotateAngleX = MathHelper.cos(ageInTicks * 0.6662F) * 0.05F;
           this.rightArm.rotateAngleZ = 2.670354F;
           this.rightArm.rotateAngleY = 0.0F;
           this.leftArm.rotationPointZ = 0.0F;
           this.leftArm.rotationPointX = 5.0F;
           this.leftArm.rotateAngleX = MathHelper.cos(ageInTicks * 0.6662F) * 0.05F;
           this.leftArm.rotateAngleZ = -2.3561945F;
           this.leftArm.rotateAngleY = 0.0F;
        } else if (AbstractGnomadEntity$armpose == AbstractGnomadEntity.ArmPose.SHAKING) {
           this.MainHead.rotateAngleZ = MathHelper.cos(ageInTicks * 0.6662F) * 0.05F;
           this.FaceMain.rotateAngleZ = MathHelper.cos(ageInTicks * 0.6662F) * 0.05F;   
        }
    }

    public void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }
    
    @Override
	public ModelRenderer getModelHead() {
		return this.MainHead;
	}

	@Override
	public void translateHand(HandSide sideIn, MatrixStack matrixStackIn) {
		this.getArmForSide(sideIn).translateRotate(matrixStackIn);
	}
	
	protected ModelRenderer getArmForSide(HandSide side)
	{
		return side == HandSide.LEFT ? this.leftArm : this.rightArm;
	}
}
