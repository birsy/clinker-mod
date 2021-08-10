package birsy.clinker.client.render.entity.model;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import birsy.clinker.client.render.util.BirsyBaseModel;
import birsy.clinker.client.render.util.BirsyModelRenderer;
import birsy.clinker.common.entity.monster.WitchBrickEntity;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import birsy.clinker.client.render.util.BirsyBaseModel.Axis;

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
        this.texWidth = 128;
        this.texHeight = 64;
        this.brickLeftLeg = new BirsyModelRenderer(this, 64, 17);
        this.brickLeftLeg.setPos(6.0F, -6.0F, 0.0F);
        this.brickLeftLeg.addBox(-2.0F, -2.0F, -2.0F, 4.0F, 8.0F, 4.0F, 0.0F, 0.0F, 0.0F);
        this.brickBody = new BirsyModelRenderer(this, 45, 11);
        this.brickBody.setPos(0.0F, -13.0F, 2.0F);
        this.brickBody.addBox(-9.0F, -11.0F, -7.0F, 18.0F, 18.0F, 9.0F, 0.0F, 0.0F, 0.0F);
        this.brickHairRight = new BirsyModelRenderer(this, 54, 0);
        this.brickHairRight.setPos(-6.0F, -5.0F, -3.0F);
        this.brickHairRight.addBox(-5.0F, -8.0F, -0.5F, 10.0F, 10.0F, 1.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(brickHairRight, -0.41887902047863906F, 0.0F, -0.4363323129985824F);
        this.brickArmLeft = new BirsyModelRenderer(this, 64, 0);
        this.brickArmLeft.setPos(9.0F, -8.0F, -2.0F);
        this.brickArmLeft.texOffs(98, 0).addBox(0.0F, -2.0F, -2.0F, 2.0F, 13.0F, 4.0F, 0.0F, 0.0F, 0.0F);
        this.brickRightLeg = new BirsyModelRenderer(this, 18, 16);
        this.brickRightLeg.setPos(-6.0F, -6.0F, 0.0F);
        this.brickRightLeg.addBox(-2.0F, -2.0F, -2.0F, 4.0F, 8.0F, 4.0F, 0.0F, 0.0F, 0.0F);
        this.movementBase = new BirsyModelRenderer(this, 0, 0);
        this.movementBase.setPos(0.0F, 24.0F, 0.0F);
        this.movementBase.addBox(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
        //this.brickMaskNose = new BirsyModelRenderer(this, 0, 0);
        //this.brickMaskNose.setRotationPoint(0.0F, 0.0F, -2.0F);
        //this.brickMaskNose.addBox(-0.5F, 0.0F, -2.0F, 1.0F, 1.0F, 2.0F, 0.0F, 0.0F, 0.0F);
        //this.brickMask = new BirsyModelRenderer(this, 0, 16);
        //this.brickMask.setRotationPoint(0.0F, -12.0F, -7.0F);
        //this.brickMask.addBox(-3.5F, -4.0F, -2.0F, 7.0F, 8.0F, 2.0F, 0.0F, 0.0F, 0.0F);
        //this.setRotateAngle(brickMask, -0.10471975511965977F, 0.0F, 0.0F);
        this.brickTeeth = new BirsyModelRenderer(this, 0, 29);
        this.brickTeeth.setPos(0.0F, -10.0F, 0.25F);
        this.brickTeeth.addBox(-9.0F, -4.5F, -7.0F, 18.0F, 7.0F, 9.0F, -1.0F, -1.0F, -1.0F);
        this.setRotateAngle(brickTeeth, 0.2617993877991494F, 0.0F, 0.0F);
        this.brickArmRight = new BirsyModelRenderer(this, 64, 0);
        this.brickArmRight.setPos(-9.0F, -8.0F, -2.0F);
        this.brickArmRight.texOffs(110, 0).addBox(-2.0F, -2.0F, -2.0F, 2.0F, 13.0F, 4.0F, 0.0F, 0.0F, 0.0F);
        this.brickHead = new BirsyModelRenderer(this, 0, 0);
        this.brickHead.setPos(0.0F, -11.0F, 0.25F);
        this.brickHead.addBox(-9.0F, -7.0F, -7.0F, 18.0F, 7.0F, 9.0F, 0.0F, 0.0F, -0.25F);
        this.brickHairLeft = new BirsyModelRenderer(this, 64, 0);
        this.brickHairLeft.setPos(6.0F, -5.0F, -3.0F);
        this.brickHairLeft.texOffs(77, 0).addBox(-5.0F, -8.0F, -0.5F, 10.0F, 10.0F, 1.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(brickHairLeft, -0.41887902047863906F, 0.0F, 0.4363323129985824F);
        this.brickLoincloth = new BirsyModelRenderer(this, 45, 32);
        this.brickLoincloth.setPos(0.0F, 7.0F, -2.0F);
        this.brickLoincloth.texOffs(45, 38).addBox(-9.0F, -4.5F, -5.0F, 18.0F, 7.0F, 9.0F, 0.5F, 0.5F, 0.5F);
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
    public void renderToBuffer(PoseStack matrixStackIn, VertexConsumer bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) { 
        matrixStackIn.pushPose();
        matrixStackIn.scale(modelScale[0], modelScale[1], modelScale[2]);
        ImmutableList.of(this.movementBase).forEach((modelRenderer) -> modelRenderer.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha));
        matrixStackIn.popPose();
    }

	@Override
    public void setupAnim(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch)
    {
    	resetParts(movementBase, brickRightLeg, brickLeftLeg, brickBody, brickTeeth, brickArmLeft, brickArmRight, brickLoincloth, brickHead, brickHairRight, brickHairLeft);
    	
    	float f = limbSwing;
    	float f1 = limbSwingAmount;
    	
    	float globalSpeed = 1.0F;
    	float globalHeight = 1.0F;
    	float globalDegree = 1.6F;

    	//IDLE

        swing(this.brickHead, globalSpeed - 0.25F, 0.15f * globalDegree, false, 0.65F, 0.0F, f, f1, Axis.Z);
        swing(this.brickHead, globalSpeed - 0.15F, 0.15f * globalDegree, false, 0.35F, -0.2F, f, f1, Axis.X);

        swing(this.brickTeeth, globalSpeed - 0.15F, 0.15f * globalDegree, false, 0.95F, 0.2F, f, f1, Axis.X);

        bob(this.movementBase, 0.12F * globalSpeed, 0.2f * globalDegree, false, ageInTicks, 0.5F, true);

        bob(this.brickArmRight, 0.125F * globalSpeed, 0.2f * globalDegree, false, ageInTicks, 0.5F, true);
        bob(this.brickArmLeft, 0.125F * globalSpeed, 0.2f * globalDegree, false, ageInTicks, 0.5F, true);

        //WALK
    	float walkSpeed = 0.5F * globalSpeed;

        bob(this.brickHead, globalSpeed, 1.25F * globalHeight, true, f, f1, true);

        swing(this.movementBase, globalSpeed, 0.15f * globalDegree, false, 0.5F, 0.0F, f, f1, Axis.Z);
        swing(this.movementBase, walkSpeed, 0.25f * globalDegree, false, 0.5F, 0.0F, f, f1, Axis.Y);
        bob(this.movementBase, globalSpeed, 1.5F * globalHeight, true, f, f1, true);

        swingLimbs(this.brickLeftLeg, this.brickRightLeg, walkSpeed, 0.6f * globalDegree, 0.0F, 0.0F, f, f1);

        swingLimbs(this.brickArmRight, this.brickArmLeft, walkSpeed, 0.25f * globalDegree, 0.0F, 0.0F, f, f1);
        swing(this.brickArmRight, walkSpeed, 0.2f * globalDegree, false, 0.0F, 0.2F, f, f1, Axis.Z);
        swing(this.brickArmLeft, walkSpeed, 0.2f * globalDegree, false, 0.0F, -0.2F, f, f1, Axis.Z);
        bob(this.brickArmLeft, walkSpeed, globalHeight, false, f, f1, true);
        bob(this.brickArmRight, walkSpeed, globalHeight, false, f, f1, true);

        look(this.brickBody, netHeadYaw, headPitch, 9999.0F, 6.0F);
        look(this.brickHead, netHeadYaw, headPitch, 9999.0F, 2.0F);

        if(entityIn.isCharging || entityIn.isWindingUp) {
            this.brickBody.xRot += 10.0F;
        }
    }

    public void prepareMobModel(T entityIn, float limbSwing, float limbSwingAmount, float partialTick) {
    	int i = entityIn.getAttackTimer();
    	if (i > 0) {
    		this.brickBody.xRot = (-2.0F + 1.5F * Mth.triangleWave((float)i - partialTick, 10.0F))*-1;
    		this.brickHead.xRot = -2.0F + 1.5F * Mth.triangleWave((float)i - partialTick, 10.0F);
    	}
    }
}
