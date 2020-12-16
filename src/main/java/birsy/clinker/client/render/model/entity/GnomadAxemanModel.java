package birsy.clinker.client.render.model.entity;
import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import birsy.clinker.client.render.model.BirsyBaseModel;
import birsy.clinker.client.render.model.BirsyModelRenderer;
import birsy.clinker.common.entity.monster.AbstractGnomadEntity;
import net.minecraft.client.renderer.entity.model.IHasArm;
import net.minecraft.client.renderer.entity.model.IHasHead;
import net.minecraft.client.renderer.model.ModelHelper;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.util.HandSide;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Gnomad - birsy
 * Created using Tabula 8.0.0
 */
@OnlyIn(Dist.CLIENT)
public class GnomadAxemanModel<T extends AbstractGnomadEntity> extends BirsyBaseModel<T> implements IHasArm, IHasHead
{
    public BirsyModelRenderer leftArm;
    public BirsyModelRenderer rightArm;
    public BirsyModelRenderer rightLeg;
    public BirsyModelRenderer leftLeg;
    public BirsyModelRenderer Neck;
    public BirsyModelRenderer Torso;
    public BirsyModelRenderer MainHead;
    public BirsyModelRenderer Nose;
    public BirsyModelRenderer FaceMain;
    public BirsyModelRenderer FaceBottom;
    public BirsyModelRenderer FaceTop;
    public BirsyModelRenderer HatBottom;
    public BirsyModelRenderer HatTop;
    public BirsyModelRenderer Back;

    public GnomadAxemanModel() {
        this.textureWidth = 64;
        this.textureHeight = 64;
        this.MainHead = new BirsyModelRenderer(this, 36, 0);
        this.MainHead.setRotationPoint(0.0F, -0.9F, -2.0F);
        this.MainHead.addBox(-3.0F, -3.0F, -6.0F, 6.0F, 6.0F, 6.0F, 0.0F, 0.0F, 0.0F);
        this.FaceTop = new BirsyModelRenderer(this, 28, 4);
        this.FaceTop.setRotationPoint(0.0F, -2.5F, -7.0F);
        this.FaceTop.addBox(-3.0F, -1.0F, 0.0F, 6.0F, 1.0F, 1.0F, 0.0F, 0.0F, 0.0F);
        this.leftLeg = new BirsyModelRenderer(this, 12, 0);
        this.leftLeg.setRotationPoint(2.5F, 13.75F, 2.0F);
        this.leftLeg.addBox(-0.5F, 0.0F, -0.5F, 1.0F, 10.0F, 1.0F, 0.25F, 0.25F, 0.25F);
        this.FaceBottom = new BirsyModelRenderer(this, 0, 11);
        this.FaceBottom.setRotationPoint(0.0F, 3.5F, -7.0F);
        this.FaceBottom.addBox(-3.0F, 0.0F, 0.0F, 6.0F, 2.0F, 1.0F, 0.0F, 0.0F, 0.0F);
        this.Back = new BirsyModelRenderer(this, 0, 39);
        this.Back.setRotationPoint(0.0F, -18.0F, -2.0F);
        this.Back.addBox(-5.0F, -3.0F, -4.0F, 10.0F, 3.0F, 8.0F, 0.0F, 0.0F, 0.0F);
        this.Neck = new BirsyModelRenderer(this, 16, 0);
        this.Neck.setRotationPoint(0.0F, 1.0F, -5.0F);
        this.Neck.addBox(-1.5F, -1.5F, -2.0F, 3.0F, 3.0F, 6.0F, 0.0F, 0.0F, 0.0F);
        this.HatBottom = new BirsyModelRenderer(this, 0, 14);
        this.HatBottom.setRotationPoint(0.0F, -3.0F, -3.5F);
        this.HatBottom.addBox(-2.0F, -2.0F, -2.0F, 4.0F, 2.0F, 4.0F, 0.0F, 0.0F, 0.0F);
        this.Torso = new BirsyModelRenderer(this, 4, 9);
        this.Torso.setRotationPoint(0.0F, 20.0F, 1.0F);
        this.Torso.addBox(-5.0F, -18.0F, -6.0F, 10.0F, 18.0F, 12.0F, 0.0F, 0.0F, 0.0F);
        this.rightArm = new BirsyModelRenderer(this, 4, 0);
        this.rightArm.setRotationPoint(-5.25F, 4.0F, -1.0F);
        this.rightArm.addBox(-1.0F, -0.5F, -0.5F, 1.0F, 10.0F, 1.0F, 0.25F, 0.25F, 0.25F);
        this.Nose = new BirsyModelRenderer(this, 28, 0);
        this.Nose.setRotationPoint(0.0F, -0.2F, -6.7F);
        this.Nose.addBox(-1.0F, -1.0F, -1.0F, 2.0F, 2.0F, 2.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(Nose, -0.3441789165090569F, 0.0F, 0.0F);
        this.leftArm = new BirsyModelRenderer(this, 0, 0);
        this.leftArm.setRotationPoint(5.25F, 4.0F, -1.0F);
        this.leftArm.addBox(0.0F, -0.5F, -0.5F, 1.0F, 10.0F, 1.0F, 0.25F, 0.25F, 0.25F);
        this.HatTop = new BirsyModelRenderer(this, 51, 16);
        this.HatTop.setRotationPoint(0.0F, -2.0F, 0.0F);
        this.HatTop.addBox(-1.5F, -2.0F, -1.5F, 3.0F, 3.0F, 3.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(HatTop, 0.0F, 0.0F, -0.17941984244448517F);
        this.FaceMain = new BirsyModelRenderer(this, 36, 12);
        this.FaceMain.setRotationPoint(0.0F, 0.0F, -7.0F);
        this.FaceMain.addBox(-4.0F, -2.5F, 0.0F, 8.0F, 6.0F, 1.0F, 0.0F, 0.0F, 0.0F);
        this.rightLeg = new BirsyModelRenderer(this, 8, 0);
        this.rightLeg.setRotationPoint(-2.5F, 13.75F, 2.0F);
        this.rightLeg.addBox(-0.5F, 0.0F, -0.5F, 1.0F, 10.0F, 1.0F, 0.25F, 0.25F, 0.25F);
        this.Neck.addChild(this.MainHead);
        this.MainHead.addChild(this.FaceTop);
        this.MainHead.addChild(this.FaceBottom);
        this.Torso.addChild(this.Back);
        this.MainHead.addChild(this.HatBottom);
        this.MainHead.addChild(this.Nose);
        this.HatBottom.addChild(this.HatTop);
        this.MainHead.addChild(this.FaceMain);
    }

    @Override
    public void render(MatrixStack matrixStackIn, IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) { 
        ImmutableList.of(this.leftLeg, this.Neck, this.Torso, this.rightArm, this.leftArm, this.rightLeg).forEach((modelRenderer) -> { 
            modelRenderer.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
        });
    }

    @Override
    public void setRotationAngles(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch)
    {
    	resetParts(leftArm, rightArm, rightLeg, leftLeg, Neck, Torso, MainHead, Nose, FaceMain, FaceBottom, FaceTop, HatBottom, HatTop, Back);
    	
    	float f = limbSwing;
    	float f1 = limbSwingAmount * 2F;
    	
    	float globalSpeed = 1.25F;
    	float globalHeight = 1;
    	float globalDegree = 1.25F;
    	
    	float walkSpeed = 0.5F * globalSpeed;
        
    	bob(this.MainHead, 0.5F * walkSpeed, 1 * globalHeight, false, f, f1, false);
    	bob(this.MainHead, 0.125F * globalSpeed, 0.5F * globalHeight, false, ageInTicks, 0.5F, false);
    	swing(this.MainHead, 0.5F * walkSpeed, 0.05F * globalDegree, false, 1F, 0, f, f1, Axis.X);
    	
    	swing(this.Neck, 0.5F * walkSpeed, 0.05F * globalDegree, false, 0.25F, 0, f, f1, Axis.X);
    	swing(this.Back, 0.5F * walkSpeed, 0.05F * globalDegree, false, 0.5F, 0, f, f1, Axis.X);
    	
    	swing(this.Torso, 0.5F * walkSpeed, 0.05F * globalDegree, false, 1F, 0, f, f1, Axis.X);
    	
    	swingLimbs(this.leftLeg, this.rightLeg, walkSpeed, 1.0f * globalDegree, 0.0F, 0.0F, f, f1);
    	swingLimbs(this.rightArm, this.leftArm, walkSpeed, 1.0f * globalDegree, 0.1F, 0.5F, f, f1);
    	
    	swing(this.rightArm, 0.5F * globalSpeed, 0.25f * globalDegree, false, 1.0F, 0.25F, f, f1, Axis.Z);
    	swing(this.leftArm, 0.5F * globalSpeed, 0.25f * globalDegree, false, 1.0F, -0.25F, f, f1, Axis.Z);
    	
    	swing(this.rightArm, 0.125F * globalSpeed, 0.125f * globalDegree, false, 0.5F, 0.25F, ageInTicks, 0.5F, Axis.Z);
    	swing(this.leftArm, 0.125F * globalSpeed, 0.125f * globalDegree, false, 1.5F, -0.25F, ageInTicks, 0.5F, Axis.Z);
    	
    	swing(this.rightArm, 0.125F * globalSpeed, 0.125f * globalDegree, false, 0.5F, 0.0F, ageInTicks, 0.5F, Axis.X);
    	swing(this.leftArm, 0.125F * globalSpeed, 0.125f * globalDegree, false, 1.5F, 0.0F, ageInTicks, 0.5F, Axis.X);
    	
    	rotVar(this.Nose, entityIn, -0.5F, 0.1F, Axis.X);
    	
    	rotVar(this.HatBottom, entityIn, -0.01F, 0.01F, Axis.X);
    	rotVar(this.HatBottom, entityIn, -0.01F, 0.01F, Axis.Y);
    	rotVar(this.HatBottom, entityIn, -0.05F, 0.05F, Axis.Z);
    	
    	swing(this.HatBottom, 0.5F * walkSpeed, 0.05F * globalDegree, false, 1F, 0, f, f1, Axis.X);
    	swing(this.HatBottom, 0.5F * walkSpeed, 0.05F * globalDegree, false, 1F, 0, f, f1, Axis.Z);
    	
    	rotVar(this.HatTop, entityIn, -0.01F, 0.01F, Axis.X);
    	rotVar(this.HatTop, entityIn, -0.01F, 0.01F, Axis.Z);
    	
    	look(this.Neck, netHeadYaw, headPitch, 2.0F, 2.0F);
    	look(this.MainHead, netHeadYaw, headPitch, 2.0F, 2.0F);
        
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
