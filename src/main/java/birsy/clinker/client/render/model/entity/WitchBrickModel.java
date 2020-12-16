package birsy.clinker.client.render.model.entity;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import birsy.clinker.client.render.model.BirsyBaseModel;
import birsy.clinker.client.render.model.BirsyModelRenderer;
import birsy.clinker.common.entity.monster.WitchBrickEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * WitchBrickModel - doclg
 * Created using Tabula 8.0.0
 */
@OnlyIn(Dist.CLIENT)
public class WitchBrickModel<T extends WitchBrickEntity> extends BirsyBaseModel<T> {
    public float[] modelScale = new float[] { 1.1F, 1.1F, 1.1F };
    public BirsyModelRenderer movementBase;
    public BirsyModelRenderer brickRightLeg;
    public BirsyModelRenderer brickLeftLeg;
    public BirsyModelRenderer brickBody;
    public BirsyModelRenderer brickTeeth;
    public BirsyModelRenderer brickArmLeft;
    public BirsyModelRenderer brickArmRight;
    //public BirsyModelRenderer brickMask;
    public BirsyModelRenderer brickLoincloth;
    public BirsyModelRenderer brickHead;
    //public BirsyModelRenderer brickMaskNose;
    public BirsyModelRenderer brickHairRight;
    public BirsyModelRenderer brickHairLeft;

    public WitchBrickModel() {
        this.textureWidth = 128;
        this.textureHeight = 64;
        this.brickLeftLeg = new BirsyModelRenderer(this, 64, 17);
        this.brickLeftLeg.setRotationPoint(6.0F, -6.0F, 0.0F);
        this.brickLeftLeg.addBox(-2.0F, -2.0F, -2.0F, 4.0F, 8.0F, 4.0F, 0.0F, 0.0F, 0.0F);
        this.brickBody = new BirsyModelRenderer(this, 45, 11);
        this.brickBody.setRotationPoint(0.0F, -13.0F, 2.0F);
        this.brickBody.addBox(-9.0F, -11.0F, -7.0F, 18.0F, 18.0F, 9.0F, 0.0F, 0.0F, 0.0F);
        this.brickHairRight = new BirsyModelRenderer(this, 54, 0);
        this.brickHairRight.setRotationPoint(-6.0F, -5.0F, -3.0F);
        this.brickHairRight.addBox(-5.0F, -8.0F, -0.5F, 10.0F, 10.0F, 1.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(brickHairRight, -0.41887902047863906F, 0.0F, -0.4363323129985824F);
        this.brickArmLeft = new BirsyModelRenderer(this, 64, 0);
        this.brickArmLeft.setRotationPoint(9.0F, -8.0F, -2.0F);
        this.brickArmLeft.setTextureOffset(98, 0).addBox(0.0F, -2.0F, -2.0F, 2.0F, 13.0F, 4.0F, 0.0F, 0.0F, 0.0F);
        this.brickRightLeg = new BirsyModelRenderer(this, 18, 16);
        this.brickRightLeg.setRotationPoint(-6.0F, -6.0F, 0.0F);
        this.brickRightLeg.addBox(-2.0F, -2.0F, -2.0F, 4.0F, 8.0F, 4.0F, 0.0F, 0.0F, 0.0F);
        this.movementBase = new BirsyModelRenderer(this, 0, 0);
        this.movementBase.setRotationPoint(0.0F, 24.0F, 0.0F);
        this.movementBase.addBox(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
        //this.brickMaskNose = new BirsyModelRenderer(this, 0, 0);
        //this.brickMaskNose.setRotationPoint(0.0F, 0.0F, -2.0F);
        //this.brickMaskNose.addBox(-0.5F, 0.0F, -2.0F, 1.0F, 1.0F, 2.0F, 0.0F, 0.0F, 0.0F);
        //this.brickMask = new BirsyModelRenderer(this, 0, 16);
        //this.brickMask.setRotationPoint(0.0F, -12.0F, -7.0F);
        //this.brickMask.addBox(-3.5F, -4.0F, -2.0F, 7.0F, 8.0F, 2.0F, 0.0F, 0.0F, 0.0F);
        //this.setRotateAngle(brickMask, -0.10471975511965977F, 0.0F, 0.0F);
        this.brickTeeth = new BirsyModelRenderer(this, 0, 29);
        this.brickTeeth.setRotationPoint(0.0F, -10.0F, 0.25F);
        this.brickTeeth.addBox(-9.0F, -4.5F, -7.0F, 18.0F, 7.0F, 9.0F, -1.0F, -1.0F, -1.0F);
        this.setRotateAngle(brickTeeth, 0.2617993877991494F, 0.0F, 0.0F);
        this.brickArmRight = new BirsyModelRenderer(this, 64, 0);
        this.brickArmRight.setRotationPoint(-9.0F, -8.0F, -2.0F);
        this.brickArmRight.setTextureOffset(110, 0).addBox(-2.0F, -2.0F, -2.0F, 2.0F, 13.0F, 4.0F, 0.0F, 0.0F, 0.0F);
        this.brickHead = new BirsyModelRenderer(this, 0, 0);
        this.brickHead.setRotationPoint(0.0F, -11.0F, 0.25F);
        this.brickHead.addBox(-9.0F, -7.0F, -7.0F, 18.0F, 7.0F, 9.0F, 0.0F, 0.0F, -0.25F);
        this.brickHairLeft = new BirsyModelRenderer(this, 64, 0);
        this.brickHairLeft.setRotationPoint(6.0F, -5.0F, -3.0F);
        this.brickHairLeft.setTextureOffset(77, 0).addBox(-5.0F, -8.0F, -0.5F, 10.0F, 10.0F, 1.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(brickHairLeft, -0.41887902047863906F, 0.0F, 0.4363323129985824F);
        this.brickLoincloth = new BirsyModelRenderer(this, 45, 32);
        this.brickLoincloth.setRotationPoint(0.0F, 7.0F, -2.0F);
        this.brickLoincloth.setTextureOffset(45, 38).addBox(-9.0F, -4.5F, -5.0F, 18.0F, 7.0F, 9.0F, 0.5F, 0.5F, 0.5F);
        this.movementBase.addChild(this.brickLeftLeg);
        this.movementBase.addChild(this.brickBody);
        this.brickHead.addChild(this.brickHairRight);
        this.brickBody.addChild(this.brickArmLeft);
        this.movementBase.addChild(this.brickRightLeg);
        //this.brickMask.addChild(this.brickMaskNose);
        //this.brickBody.addChild(this.brickMask);
        this.brickBody.addChild(this.brickTeeth);
        this.brickBody.addChild(this.brickArmRight);
        this.brickBody.addChild(this.brickHead);
        this.brickHead.addChild(this.brickHairLeft);
        this.brickBody.addChild(this.brickLoincloth);
    }

    @Override
    public void render(MatrixStack matrixStackIn, IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) { 
        matrixStackIn.push();
        matrixStackIn.scale(modelScale[0], modelScale[1], modelScale[2]);
        ImmutableList.of(this.movementBase).forEach((modelRenderer) -> { 
            modelRenderer.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
        });
        matrixStackIn.pop();
    }

	@Override
    public void setRotationAngles(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch)
    {
    	resetParts(movementBase, brickRightLeg, brickLeftLeg, brickBody, brickTeeth, brickArmLeft, brickArmRight, brickLoincloth, brickHead, brickHairRight, brickHairLeft);
    	
    	float f = limbSwing;
    	float f1 = limbSwingAmount * 1.5F;
    	
    	float globalSpeed = 1.0F;
    	float globalHeight = 1.0F;
    	float globalDegree = 1.2F;
    	
    	float walkSpeed = 0.5F * globalSpeed;
    	
    	this.brickBody.rotationPointY = (float) (-13.0F + (Math.sin(ageInTicks * (0.125F * globalSpeed)) * 0.5F * (0.5F * globalHeight) - 0.5F * (0.5F * globalHeight)));
    	this.brickHead.rotationPointY = (float) (-11.0F + (Math.sin(ageInTicks * (0.125F * globalSpeed)) * 0.5F * (0.5F * globalHeight) - 0.5F * (0.5F * globalHeight)));
    	
    	swing(this.brickHead, 0.125F * globalSpeed, 0.05F * globalDegree, false, 1F, 0, ageInTicks, 0.5F, Axis.Z);
    	swing(this.brickHead, 0.125F * globalSpeed, 0.07F * globalDegree, false, 0.5F, -0.6F, ageInTicks, 0.5F, Axis.X);
    	
    	swing(this.brickTeeth, 0.125F * globalSpeed, 0.05F * globalDegree, false, 1F, 0, ageInTicks, 0.5F, Axis.Z);
    	swing(this.brickTeeth, 0.5F * walkSpeed, 0.07F * globalDegree, false, 0.25F, 0.25F, f, f1, Axis.X);
    	
    	swing(this.brickArmRight, 0.125F * globalSpeed, 0.125f * globalDegree, false, 0.5F, 0.125F, ageInTicks, 0.5F, Axis.Z);
    	swing(this.brickArmLeft, 0.125F * globalSpeed, 0.125f * globalDegree, false, 1.5F, -0.125F, ageInTicks, 0.5F, Axis.Z);
    	
    	swing(this.brickArmRight, 0.15F * globalSpeed, 0.125f * globalDegree, false, 0.5F, 0.0F, ageInTicks, 0.5F, Axis.X);
    	swing(this.brickArmLeft, 0.15F * globalSpeed, 0.125f * globalDegree, false, 1.5F, 0.0F, ageInTicks, 0.5F, Axis.X);
    	
    	swingLimbs(this.brickLeftLeg, this.brickRightLeg, walkSpeed, 1.0f * globalDegree, 0.0F, 0.0F, f, f1);
    	swingLimbs(this.brickArmRight, this.brickArmLeft, walkSpeed, 1.0f * globalDegree, 0.1F, 0.5F, f, f1);
    	
    	look(this.brickHead, netHeadYaw, headPitch, 7.5F, 5.0F);
    	look(this.brickTeeth, netHeadYaw, headPitch, 10F, 10F);
    	look(this.brickBody, netHeadYaw, headPitch, 5.0F, 7.5F);
    	
    	swing(this.movementBase, 0.125F * globalSpeed, 0.015F * globalDegree, false, 1F, 0, ageInTicks, 0.5F, Axis.Z);
    	swing(this.movementBase, 0.125F * globalSpeed, 0.015F * globalDegree, false, 1F, 0, ageInTicks, 0.5F, Axis.X);
    	
    	swing(this.movementBase, 0.51F * walkSpeed, 0.1F * globalDegree, false, 0.25F, 0.25F, f, f1, Axis.X);
    	swing(this.movementBase, 0.5F * walkSpeed, 0.1F * globalDegree, false, 1F, 0, f, f1, Axis.Z);
    }

    public void setLivingAnimations(T entityIn, float limbSwing, float limbSwingAmount, float partialTick) {
    	int i = entityIn.getAttackTimer();
    	if (i > 0) {
    		this.brickBody.rotateAngleX = (-2.0F + 1.5F * MathHelper.func_233021_e_((float)i - partialTick, 10.0F))*-1;
    		this.brickHead.rotateAngleX = -2.0F + 1.5F * MathHelper.func_233021_e_((float)i - partialTick, 10.0F);
    	}
    }
}
