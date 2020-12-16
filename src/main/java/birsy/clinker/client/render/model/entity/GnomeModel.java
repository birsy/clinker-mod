package birsy.clinker.client.render.model.entity;

import java.util.List;
import java.util.Random;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import birsy.clinker.client.render.model.BirsyBaseModel;
import birsy.clinker.client.render.model.BirsyModelRenderer;
import birsy.clinker.common.entity.merchant.GnomeEntity;
import birsy.clinker.common.entity.merchant.GnomeEntity.ArmPose;
import net.minecraft.client.renderer.entity.model.IHasArm;
import net.minecraft.client.renderer.entity.model.IHasHead;
import net.minecraft.client.renderer.model.ModelHelper;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.util.HandSide;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Gnome - birsy
 * Created using Tabula 8.0.0
 */
@OnlyIn(Dist.CLIENT)
public class GnomeModel<T extends GnomeEntity> extends BirsyBaseModel<T> implements IHasArm, IHasHead
{
	private List<ModelRenderer> modelRenderers = Lists.newArrayList();
	
	public BirsyModelRenderer controlJoint;
    public BirsyModelRenderer legControlJoint;
    public BirsyModelRenderer armControlJoint;
    public BirsyModelRenderer ArmL;
    public BirsyModelRenderer ArmR;
    public BirsyModelRenderer LegR;
    public BirsyModelRenderer LegL;
    public BirsyModelRenderer Neck;
    public BirsyModelRenderer Torso;
    public BirsyModelRenderer MainHead;
    public BirsyModelRenderer Nose;
    public BirsyModelRenderer FaceMain;
    public BirsyModelRenderer FaceBottom;
    public BirsyModelRenderer FaceTop;
    public BirsyModelRenderer HatBottom;
    public BirsyModelRenderer Beard;
    public BirsyModelRenderer HatMiddle;
    public BirsyModelRenderer HatTop;
    public BirsyModelRenderer Back;
    
    public GnomeModel(float modelSize) {
        this.textureWidth = 64;
        this.textureHeight = 64;
        this.HatBottom = new BirsyModelRenderer(this, 43, 12);
        this.HatBottom.setRotationPoint(0.0F, -3.0F, -3.5F);
        this.HatBottom.addBox(-2.5F, -1.0F, -2.5F, 5.0F, 1.0F, 5.0F, 0.0F, 0.0F, 0.0F);
        this.ArmL = new BirsyModelRenderer(this, 0, 0);
        this.ArmL.setRotationPoint(4.25F, 4.0F, -1.0F);
        this.ArmL.addBox(0.0F, -0.5F, -0.5F, 1.0F, 10.0F, 1.0F, modelSize+0.25F, modelSize+0.25F, modelSize+0.25F);
        this.Torso = new BirsyModelRenderer(this, 8, 20);
        this.Torso.setRotationPoint(0.0F, 15.0F, 1.0F);
        this.Torso.addBox(-4.0F, -15.0F, -4.0F, 8.0F, 15.0F, 8.0F, modelSize, modelSize, modelSize);
        this.FaceTop = new BirsyModelRenderer(this, 34, 12);
        this.FaceTop.setRotationPoint(0.0F, -2.5F, -7.0F);
        this.FaceTop.addBox(-3.0F, -1.0F, 0.0F, 6.0F, 1.0F, 1.0F, 0.0F, 0.0F, 0.0F);
        this.FaceMain = new BirsyModelRenderer(this, 16, 9);
        this.FaceMain.setRotationPoint(0.0F, 0.0F, -7.0F);
        this.FaceMain.addBox(-4.0F, -2.5F, 0.0F, 8.0F, 6.0F, 1.0F, 0.0F, 0.0F, 0.0F);
        this.Neck = new BirsyModelRenderer(this, 16, 0);
        this.Neck.setRotationPoint(0.0F, 1.0F, -5.0F);
        this.Neck.addBox(-1.5F, -1.5F, -2.0F, 3.0F, 3.0F, 6.0F, 0.0F, 0.0F, 0.0F);
        this.MainHead = new BirsyModelRenderer(this, 34, 0);
        this.MainHead.setRotationPoint(0.0F, -0.9F, -2.0F);
        this.MainHead.addBox(-3.0F, -3.0F, -6.0F, 6.0F, 6.0F, 6.0F, 0.0F, 0.0F, 0.0F);
        this.LegR = new BirsyModelRenderer(this, 8, 0);
        this.LegR.setRotationPoint(-2.5F, 13.75F, 1.0F);
        this.LegR.addBox(-0.5F, 0.0F, -0.5F, 1.0F, 10.0F, 1.0F, modelSize+0.25F, modelSize+0.25F, modelSize+0.25F);
        this.HatTop = new BirsyModelRenderer(this, 31, 14);
        this.HatTop.setRotationPoint(0.0F, -2.0F, 0.0F);
        this.HatTop.addBox(-1.5F, -2.0F, -1.5F, 3.0F, 3.0F, 3.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(HatTop, -0.2617993877991494F, 0.0F, -0.17941984244448517F);
        this.LegL = new BirsyModelRenderer(this, 12, 0);
        this.LegL.setRotationPoint(2.5F, 13.75F, 1.0F);
        this.LegL.addBox(-0.5F, 0.0F, -0.5F, 1.0F, 10.0F, 1.0F, modelSize+0.25F, modelSize+0.25F, modelSize+0.25F);
        this.Beard = new BirsyModelRenderer(this, 43, 18);
        this.Beard.setRotationPoint(0.0F, 2.0F, -5.0F);
        this.Beard.addBox(-4.0F, 0.0F, -0.5F, 8.0F, 10.0F, 1.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(Beard, -0.1563815016444822F, 0.0F, 0.0F);
        this.Nose = new BirsyModelRenderer(this, 28, 0);
        this.Nose.setRotationPoint(0.0F, -0.2F, -6.7F);
        this.Nose.addBox(-1.0F, -1.0F, -1.0F, 2.0F, 2.0F, 2.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(Nose, -0.3441789165090569F, 0.0F, 0.0F);
        this.Back = new BirsyModelRenderer(this, 0, 32);
        this.Back.setRotationPoint(0.0F, -14.0F, -2.0F);
        this.Back.setTextureOffset(0, 43).addBox(-4.0F, -3.0F, -4.0F, 8.0F, 8.0F, 8.0F, modelSize + 0.25F, modelSize, modelSize);
        this.ArmR = new BirsyModelRenderer(this, 4, 0);
        this.ArmR.setRotationPoint(-4.25F, 4.0F, -1.0F);
        this.ArmR.addBox(-1.0F, -0.5F, -0.5F, 1.0F, 10.0F, 1.0F, modelSize+0.25F, modelSize+0.25F, modelSize+0.25F);
        this.FaceBottom = new BirsyModelRenderer(this, 0, 11);
        this.FaceBottom.setRotationPoint(0.0F, 3.5F, -7.0F);
        this.FaceBottom.addBox(-3.0F, 0.0F, 0.0F, 6.0F, 2.0F, 1.0F, 0.0F, 0.0F, 0.0F);
        this.HatMiddle = new BirsyModelRenderer(this, 0, 14);
        this.HatMiddle.setRotationPoint(0.0F, -1.0F, 0.0F);
        this.HatMiddle.addBox(-2.0F, -2.0F, -2.0F, 4.0F, 3.0F, 4.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(HatMiddle, -0.2617993877991494F, 0.0F, -0.17941984244448517F);
        this.MainHead.addChild(this.HatBottom);
        this.MainHead.addChild(this.FaceTop);
        this.MainHead.addChild(this.FaceMain);
        this.Neck.addChild(this.MainHead);
        this.HatMiddle.addChild(this.HatTop);
        this.MainHead.addChild(this.Beard);
        this.MainHead.addChild(this.Nose);
        this.Torso.addChild(this.Back);
        this.MainHead.addChild(this.FaceBottom);
        this.HatBottom.addChild(this.HatMiddle);
    }

    @Override
    public void render(MatrixStack matrixStackIn, IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) { 
        ImmutableList.of(this.ArmL, this.Torso, this.Neck, this.LegR, this.LegL, this.ArmR).forEach((modelRenderer) -> { 
            modelRenderer.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
        });
    }

    @Override
    public void setRotationAngles(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch)
    {   
    	resetParts(ArmL, ArmR, LegR, LegL, Neck, Torso, MainHead, Nose, FaceMain, FaceBottom, FaceTop, HatBottom, Beard, HatMiddle, HatTop, Back);
    	
    	float f = limbSwing;
    	float f1 = limbSwingAmount * 2F;
    	
    	float globalSpeed = 1.25F;
    	float globalHeight = 1;
    	float globalDegree = 1.25F;
    	
    	float walkSpeed = 0.5F * globalSpeed;
        
    	bob(this.MainHead, 0.5F * walkSpeed, 1 * globalHeight, false, f, f1, false);
    	bob(this.MainHead, 0.125F * globalSpeed, 0.5F * globalHeight, false, ageInTicks, 0.5F, false);
    	swing(this.MainHead, 0.5F * walkSpeed, 0.05F * globalDegree, false, 1F, 0, f, f1, Axis.X);
    	
    	swing(this.Beard, 0.5F * walkSpeed, 0.05F * globalDegree, false, 1F, 0, f, f1, Axis.Z);
    	locVar(this.Beard, entityIn, -0.125F, 0.0F, Axis.Y);
    	
    	swing(this.Neck, 0.5F * walkSpeed, 0.05F * globalDegree, false, 0.25F, 0, f, f1, Axis.X);
    	swing(this.Back, 0.5F * walkSpeed, 0.05F * globalDegree, false, 0.5F, 0, f, f1, Axis.X);
    	
    	swing(this.Back, 0.5F * walkSpeed, 0.05F * globalDegree, false, 1F, 0, f, f1, Axis.X);
    	swing(this.Torso, 0.5F * walkSpeed, 0.05F * globalDegree, false, 1F, 0, f, f1, Axis.X);
    	
    	swingLimbs(this.LegL, this.LegR, walkSpeed, 1.0f * globalDegree, 0.0F, 0.0F, f, f1);
    	swingLimbs(this.ArmR, this.ArmL, walkSpeed, 1.0f * globalDegree, 0.1F, 0.5F, f, f1);
    	
    	swing(this.ArmR, 0.5F * globalSpeed, 0.25f * globalDegree, false, 1.0F, 0.25F, f, f1, Axis.Z);
    	swing(this.ArmL, 0.5F * globalSpeed, 0.25f * globalDegree, false, 1.0F, -0.25F, f, f1, Axis.Z);
    	
    	swing(this.ArmR, 0.125F * globalSpeed, 0.125f * globalDegree, false, 0.5F, 0.25F, ageInTicks, 0.5F, Axis.Z);
    	swing(this.ArmL, 0.125F * globalSpeed, 0.125f * globalDegree, false, 1.5F, -0.25F, ageInTicks, 0.5F, Axis.Z);
    	
    	swing(this.ArmR, 0.125F * globalSpeed, 0.125f * globalDegree, false, 0.5F, 0.0F, ageInTicks, 0.5F, Axis.X);
    	swing(this.ArmL, 0.125F * globalSpeed, 0.125f * globalDegree, false, 1.5F, 0.0F, ageInTicks, 0.5F, Axis.X);
    	
    	rotVar(this.Nose, entityIn, -0.5F, 0.1F, Axis.X);
    	
    	rotVar(this.HatBottom, entityIn, -0.01F, 0.01F, Axis.X);
    	rotVar(this.HatBottom, entityIn, -0.01F, 0.01F, Axis.Y);
    	rotVar(this.HatBottom, entityIn, -0.05F, 0.05F, Axis.Z);
    	
    	swing(this.HatBottom, 0.5F * walkSpeed, 0.05F * globalDegree, false, 1F, 0, f, f1, Axis.X);
    	swing(this.HatBottom, 0.5F * walkSpeed, 0.05F * globalDegree, false, 1F, 0, f, f1, Axis.Z);
    	
    	rotVar(this.HatMiddle, entityIn, -0.2F, 0.2F, Axis.X);
    	rotVar(this.HatMiddle, entityIn, -0.2F, 0.2F, Axis.Z);
    	
    	rotVar(this.HatTop, entityIn, -0.2F, 0.2F, Axis.X);
    	rotVar(this.HatTop, entityIn, -0.2F, 0.2F, Axis.Z);
    	
    	look(this.Neck, netHeadYaw, headPitch, 2.0F, 2.0F);
    	look(this.MainHead, netHeadYaw, headPitch, 2.0F, 2.0F);
    	
        GnomeEntity.ArmPose GnomeEntity$armpose = entityIn.getArmPose();
        if (GnomeEntity$armpose == GnomeEntity.ArmPose.ATTACKING) {
        	ModelHelper.func_239103_a_(this.ArmR, this.ArmL, entityIn, this.swingProgress, ageInTicks);
        } else if (GnomeEntity$armpose == GnomeEntity.ArmPose.SPELLCASTING) {
        	this.ArmR.rotationPointZ = 0.0F;
        	this.ArmR.rotationPointX = -5.0F;
        	this.ArmL.rotationPointZ = 0.0F;
        	this.ArmL.rotationPointX = 5.0F;
        	this.ArmR.rotateAngleX = MathHelper.cos(ageInTicks * 0.6662F) * 0.25F;
        	this.ArmL.rotateAngleX = MathHelper.cos(ageInTicks * 0.6662F) * 0.25F;
        	this.ArmR.rotateAngleZ = 2.3561945F;
        	this.ArmL.rotateAngleZ = -2.3561945F;
        	this.ArmR.rotateAngleY = 0.0F;
        	this.ArmL.rotateAngleY = 0.0F;
        } else if (GnomeEntity$armpose == GnomeEntity.ArmPose.BOW_AND_ARROW) {
        	this.ArmR.rotateAngleY = -0.1F + this.MainHead.rotateAngleY;
        	this.ArmR.rotateAngleX = (-(float)Math.PI / 2F) + this.MainHead.rotateAngleX;
        	this.ArmL.rotateAngleX = -0.9424779F + this.MainHead.rotateAngleX;
        	this.ArmL.rotateAngleY = this.MainHead.rotateAngleY - 0.4F;
        	this.ArmL.rotateAngleZ = ((float)Math.PI / 2F);
        } else if (GnomeEntity$armpose == GnomeEntity.ArmPose.CROSSBOW_HOLD) {
        	ModelHelper.func_239104_a_(this.ArmR, this.ArmL, this.MainHead, true);
        } else if (GnomeEntity$armpose == GnomeEntity.ArmPose.CROSSBOW_CHARGE) {
        	ModelHelper.func_239102_a_(this.ArmR, this.ArmL, entityIn, true);
        } else if (GnomeEntity$armpose == GnomeEntity.ArmPose.CELEBRATING) {
        	this.ArmR.rotationPointZ = 0.0F;
        	this.ArmR.rotationPointX = -5.0F;
        	this.ArmR.rotateAngleX = MathHelper.cos(ageInTicks * 0.6662F) * 0.05F;
        	this.ArmR.rotateAngleZ = 2.670354F;
        	this.ArmR.rotateAngleY = 0.0F;
        	this.ArmL.rotationPointZ = 0.0F;
        	this.ArmL.rotationPointX = 5.0F;
        	this.ArmL.rotateAngleX = MathHelper.cos(ageInTicks * 0.6662F) * 0.05F;
        	this.ArmL.rotateAngleZ = -2.3561945F;
        	this.ArmL.rotateAngleY = 0.0F;
        } else if (GnomeEntity$armpose == ArmPose.HOLDING_BLOCK) {}
    }

    /**
     * This is a helper function from Tabula to set the rotation of model parts
     */
    public void setRotateAngle(BirsyModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
        
        modelRenderer.defaultRotateAngleX = x;
        modelRenderer.defaultRotateAngleY = y;
        modelRenderer.defaultRotateAngleZ = z;
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
		return side == HandSide.LEFT ? this.ArmL : this.ArmR;
	}
	
	public void setModelAttributes(GnomeModel<T> Model) {
		super.copyModelAttributesTo(Model);
		this.ArmL.copyModelAngles(this.ArmL);
		this.ArmR.copyModelAngles(this.ArmR);
		this.LegR.copyModelAngles(this.LegR);
		this.LegL.copyModelAngles(this.LegL);
		this.Neck.copyModelAngles(this.Neck);
		this.Torso.copyModelAngles(this.Torso);
		this.MainHead.copyModelAngles(this.MainHead);
		this.Nose.copyModelAngles(this.Nose);
		this.FaceMain.copyModelAngles(this.FaceMain);
		this.FaceBottom.copyModelAngles(this.FaceBottom);
		this.FaceTop.copyModelAngles(this.FaceTop);
		this.HatBottom.copyModelAngles(this.HatBottom);
		this.Beard.copyModelAngles(this.Beard);
		this.HatMiddle.copyModelAngles(this.HatMiddle);
		this.HatTop.copyModelAngles(this.HatTop);
		this.Back.copyModelAngles(this.Back);
	}
	
	public void setVisible(boolean visible) {	
	    this.ArmL.showModel = visible;
	    this.ArmR.showModel = visible;
	    this.LegR.showModel = visible;
	    this.LegL.showModel = visible;
	    this.Neck.showModel = visible;
	    this.Torso.showModel = visible;
	    this.MainHead.showModel = visible;
	    this.Nose.showModel = visible;
	    this.FaceMain.showModel = visible;
	    this.FaceBottom.showModel = visible;
	    this.FaceTop.showModel = visible;
	    this.HatBottom.showModel = visible;
	    this.Beard.showModel = visible;
	    this.HatMiddle.showModel = visible;
	    this.HatTop.showModel = visible;
	    this.Back.showModel = visible;
	}
	
	public ModelRenderer getRandomModelRenderer(Random randomIn) {
		return this.modelRenderers.get(randomIn.nextInt(this.modelRenderers.size()));
	}

	public void accept(ModelRenderer p_accept_1_) {
		if (this.modelRenderers == null) {
			this.modelRenderers = Lists.newArrayList();
		}
		this.modelRenderers.add(p_accept_1_);
	}
}
