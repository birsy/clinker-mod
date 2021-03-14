package birsy.clinker.client.render.model.entity;

import birsy.clinker.common.entity.monster.beetle.BoxBeetleEntity;
import birsy.clinker.core.util.MathUtils;
import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import birsy.clinker.client.render.model.BirsyBaseModel;
import birsy.clinker.client.render.model.BirsyModelRenderer;
import net.minecraft.client.renderer.entity.model.ModelUtils;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BoxBeetleModel<T extends BoxBeetleEntity> extends BirsyBaseModel<T> {
    public BirsyModelRenderer boxBeetleBody;
    public BirsyModelRenderer boxBeetleLeftElytra;
    public BirsyModelRenderer boxBeetleRightElytra;
    public BirsyModelRenderer boxBeetleLeftWing;
    public BirsyModelRenderer boxBeetleRightWing;
    public BirsyModelRenderer boxBeetleHead;
    public BirsyModelRenderer boxBeetleRightArm;
    public BirsyModelRenderer boxBeetleLeftArm;
    public BirsyModelRenderer boxBeetleFrontLeftLeg;
    public BirsyModelRenderer boxBeetleBackLeftLeg;
    public BirsyModelRenderer boxBeetleFrontRightLeg;
    public BirsyModelRenderer boxBeetleBackRightLeg;
    public BirsyModelRenderer boxBeetleRightAntennae;
    public BirsyModelRenderer boxBeetleLeftAntennae;
    private float bodyPitch;
    private float currentBodyPitch = 0;

    public BoxBeetleModel() {
        this.textureWidth = 128;
        this.textureHeight = 128;
        this.boxBeetleLeftElytra = new BirsyModelRenderer(this, 0, 75);
        this.boxBeetleLeftElytra.mirror = true;
        this.boxBeetleLeftElytra.setRotationPoint(4.25F, -12.0F, -12.0F);
        this.boxBeetleLeftElytra.addBox(-4.0F, -4.0F, 0.0F, 8.0F, 11.0F, 24.0F, 0.25F, 0.5F, 0.5F);
        this.boxBeetleFrontLeftLeg = new BirsyModelRenderer(this, 12, 12);
        this.boxBeetleFrontLeftLeg.setRotationPoint(7.0F, -1.0F, -4.0F);
        this.boxBeetleFrontLeftLeg.addBox(-0.5F, 0.0F, -4.0F, 1.0F, 4.0F, 4.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(boxBeetleFrontLeftLeg, 0.0F, -1.1344640137963142F, 0.0F);
        this.boxBeetleLeftArm = new BirsyModelRenderer(this, 12, 12);
        this.boxBeetleLeftArm.setRotationPoint(5.0F, 0.0F, -12.0F);
        this.boxBeetleLeftArm.addBox(-0.5F, -0.5F, -4.0F, 1.0F, 4.0F, 4.0F, 0.0F, 0.0F, 0.0F);
        this.boxBeetleHead = new BirsyModelRenderer(this, 0, 0);
        this.boxBeetleHead.setRotationPoint(0.0F, -2.75F, -9.75F);
        this.boxBeetleHead.addBox(-3.0F, -3.0F, -6.0F, 6.0F, 6.0F, 6.0F, 0.0F, 0.0F, 0.0F);
        this.boxBeetleBody = new BirsyModelRenderer(this, 0, 0);
        this.boxBeetleBody.setRotationPoint(0.0F, 21.0F, 0.0F);
        this.boxBeetleBody.addBox(-8.0F, -16.0F, -12.0F, 16.0F, 16.0F, 24.0F, 0.0F, 0.0F, 0.0F);
        this.boxBeetleBackRightLeg = new BirsyModelRenderer(this, 12, 12);
        this.boxBeetleBackRightLeg.setRotationPoint(-7.0F, 0.0F, 6.0F);
        this.boxBeetleBackRightLeg.addBox(-0.5F, -0.5F, -4.0F, 1.0F, 4.0F, 4.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(boxBeetleBackRightLeg, 0.0F, 2.007128639793479F, 0.0F);
        this.boxBeetleLeftAntennae = new BirsyModelRenderer(this, 0, 12);
        this.boxBeetleLeftAntennae.setRotationPoint(1.5F, -0.65F, -6.0F);
        this.boxBeetleLeftAntennae.addBox(-0.5F, -0.5F, -4.0F, 1.0F, 5.0F, 5.0F, -0.25F, -0.25F, -0.25F);
        this.setRotateAngle(boxBeetleLeftAntennae, -0.6108652381980153F, -0.2617993877991494F, 0.0F);
        this.boxBeetleLeftWing = new BirsyModelRenderer(this, -24, 40);
        this.boxBeetleLeftWing.setRotationPoint(6.25F, -16.0F, -12.0F);
        this.boxBeetleLeftWing.addBox(-6.5F, 0.0F, 0.0F, 8.0F, 0.0F, 24.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(boxBeetleLeftWing, 0.017453292519943295F, 0.0F, 0.0F);
        this.boxBeetleRightWing = new BirsyModelRenderer(this, -24, 75);
        this.boxBeetleRightWing.setRotationPoint(-6.25F, -16.0F, -12.0F);
        this.boxBeetleRightWing.addBox(-1.5F, 0.0F, 0.0F, 8.0F, 0.0F, 24.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(boxBeetleRightWing, 0.017453292519943295F, 0.0F, 0.0F);
        this.boxBeetleRightArm = new BirsyModelRenderer(this, 12, 12);
        this.boxBeetleRightArm.setRotationPoint(-5.0F, 0.0F, -12.0F);
        this.boxBeetleRightArm.addBox(-0.5F, -0.5F, -4.0F, 1.0F, 4.0F, 4.0F, 0.0F, 0.0F, 0.0F);
        this.boxBeetleRightAntennae = new BirsyModelRenderer(this, 0, 12);
        this.boxBeetleRightAntennae.setRotationPoint(-1.5F, -0.65F, -6.0F);
        this.boxBeetleRightAntennae.addBox(-0.5F, -0.5F, -4.0F, 1.0F, 5.0F, 5.0F, -0.25F, -0.25F, -0.25F);
        this.setRotateAngle(boxBeetleRightAntennae, -0.6108652381980153F, 0.2617993877991494F, 0.0F);
        this.boxBeetleRightElytra = new BirsyModelRenderer(this, 0, 40);
        this.boxBeetleRightElytra.mirror = true;
        this.boxBeetleRightElytra.setRotationPoint(-4.25F, -12.0F, -12.0F);
        this.boxBeetleRightElytra.addBox(-4.0F, -4.0F, 0.0F, 8.0F, 11.0F, 24.0F, 0.25F, 0.5F, 0.5F);
        this.boxBeetleBackLeftLeg = new BirsyModelRenderer(this, 12, 12);
        this.boxBeetleBackLeftLeg.setRotationPoint(7.0F, -1.0F, 6.0F);
        this.boxBeetleBackLeftLeg.addBox(-0.5F, 0.0F, -4.0F, 1.0F, 4.0F, 4.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(boxBeetleBackLeftLeg, 0.0F, -2.007128639793479F, 0.0F);
        this.boxBeetleFrontRightLeg = new BirsyModelRenderer(this, 12, 12);
        this.boxBeetleFrontRightLeg.setRotationPoint(-7.0F, 0.0F, -4.0F);
        this.boxBeetleFrontRightLeg.addBox(-0.5F, -0.5F, -4.0F, 1.0F, 4.0F, 4.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(boxBeetleFrontRightLeg, 0.0F, 1.1344640137963142F, 0.0F);
        this.boxBeetleBody.addChild(this.boxBeetleLeftElytra);
        this.boxBeetleBody.addChild(this.boxBeetleFrontLeftLeg);
        this.boxBeetleBody.addChild(this.boxBeetleLeftArm);
        this.boxBeetleBody.addChild(this.boxBeetleHead);
        this.boxBeetleBody.addChild(this.boxBeetleBackRightLeg);
        this.boxBeetleHead.addChild(this.boxBeetleLeftAntennae);
        this.boxBeetleBody.addChild(this.boxBeetleLeftWing);
        this.boxBeetleBody.addChild(this.boxBeetleRightWing);
        this.boxBeetleBody.addChild(this.boxBeetleRightArm);
        this.boxBeetleHead.addChild(this.boxBeetleRightAntennae);
        this.boxBeetleBody.addChild(this.boxBeetleRightElytra);
        this.boxBeetleBody.addChild(this.boxBeetleBackLeftLeg);
        this.boxBeetleBody.addChild(this.boxBeetleFrontRightLeg);
    }

    @Override
    public void render(MatrixStack matrixStackIn, IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) { 
        ImmutableList.of(this.boxBeetleBody).forEach((modelRenderer) -> { 
            modelRenderer.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
        });
    }

    @Override
    public void setRotationAngles(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        resetParts(this.boxBeetleBody, this.boxBeetleLeftElytra, this.boxBeetleRightElytra, this.boxBeetleLeftWing, this.boxBeetleRightWing, this.boxBeetleHead, this.boxBeetleRightArm, this.boxBeetleLeftArm, this.boxBeetleFrontLeftLeg, this.boxBeetleBackLeftLeg, this.boxBeetleFrontRightLeg, this.boxBeetleBackRightLeg, this.boxBeetleRightAntennae, this.boxBeetleLeftAntennae);
        float f = limbSwing;
        float f1 = limbSwingAmount * 2F;

        float globalSpeed = 1.25F;
        float globalHeight = 0.75F;
        float globalDegree = 1.25F;

        float walkSpeed = 0.5F * globalSpeed;

        swing(this.boxBeetleLeftAntennae, 0.1F * globalSpeed, 0.05F * globalDegree, false, 1F, 0, ageInTicks, 0.5F, Axis.X);
        swing(this.boxBeetleLeftAntennae, 0.1F * globalSpeed, 0.05F * globalDegree, false, 1F, 0, ageInTicks, 0.5F, Axis.Z);

        swing(this.boxBeetleRightAntennae, 0.1F * globalSpeed, 0.05F * globalDegree, true, 1F, 0, ageInTicks, 0.5F, Axis.X);
        swing(this.boxBeetleRightAntennae, 0.1F * globalSpeed, 0.05F * globalDegree, true, 1F, 0, ageInTicks, 0.5F, Axis.Z);

        swing(this.boxBeetleRightElytra, 0.125F * globalSpeed, 0.03f * globalDegree, false, 0.5F, -0.07F, ageInTicks, 0.5F, Axis.Z);
        swing(this.boxBeetleLeftElytra, 0.125F * globalSpeed, 0.03f * globalDegree, false, 1.5F, -0.07F, ageInTicks, 0.5F, Axis.Z);
        swing(this.boxBeetleRightElytra, 0.12F * globalSpeed, 0.03f * globalDegree, false, 0.5F, 0.07F, ageInTicks, 0.5F, Axis.X);
        swing(this.boxBeetleLeftElytra, 0.12F * globalSpeed, 0.03f * globalDegree, false, 1.5F, 0.07F, ageInTicks, 0.5F, Axis.X);

        if (entityIn.getFlying() || entityIn.isAirBorne) {
            float flightTransition = MathUtils.mapRange(0, 10, 1, 0, entityIn.flightOpenTransitionTicks);

            this.bodyPitch = entityIn.getBodyPitch(0.5F);
            flyAnimation(entityIn, flightTransition, globalSpeed, globalDegree, ageInTicks, netHeadYaw, headPitch);
        } else {
            float flightTransition = MathUtils.mapRange(0, 10, 0, 1, entityIn.flightCloseTransitionTicks);

            flyAnimation(entityIn, flightTransition, globalSpeed, globalDegree, ageInTicks, netHeadYaw, headPitch);

            bob(this.boxBeetleBody, 2.0F * walkSpeed, 2 * globalHeight, true, f, f1, true);
            swing(this.boxBeetleBody, walkSpeed, 0.07f * globalDegree, true, 0.0F, 0.0F, f, f1, Axis.Z);
            swing(this.boxBeetleBody, 2.0F * walkSpeed, 0.07f * globalDegree, true, 0.0F, 0.0F, f, f1, Axis.X);

            swing(this.boxBeetleFrontLeftLeg, walkSpeed, 0.6f * globalDegree, false, 0.0F, 0.0F, f, f1, Axis.Z);
            swing(this.boxBeetleBackRightLeg, walkSpeed, 0.6f * globalDegree, false, 0.0F, 0.0F, f, f1, Axis.Z);
            swing(this.boxBeetleFrontRightLeg, walkSpeed, 0.6f * globalDegree, true, 0.0F, 0.0F, f, f1, Axis.Z);
            swing(this.boxBeetleBackLeftLeg, walkSpeed, 0.6f * globalDegree, true, 0.0F, 0.0F, f, f1, Axis.Z);

            swing(this.boxBeetleLeftArm, walkSpeed, 0.2f * globalDegree, true, 0.0F, 0.0F, f, f1, Axis.Y);
            swing(this.boxBeetleRightArm, walkSpeed, 0.2f * globalDegree, false, 0.0F, 0.0F, f, f1, Axis.Y);

            bob(this.boxBeetleHead, 2.0F * walkSpeed, 2 * globalHeight, true, f + 5, f1, true);
        }

        if (entityIn.getBashing()) {
            this.boxBeetleBody.rotateAngleX = 0.69F;
        }
    }

    public void flyAnimation(T entityIn, float flightTransition, float globalSpeed, float globalDegree, float ageInTicks, float netHeadYaw, float headPitch) {
        this.boxBeetleLeftElytra.rotateAngleX =+ MathHelper.lerp(flightTransition, this.boxBeetleLeftElytra.defaultRotateAngleX, 0.87F);
        this.boxBeetleLeftElytra.rotateAngleY =+ MathHelper.lerp(flightTransition, this.boxBeetleLeftElytra.defaultRotateAngleY, 0.52F);
        this.boxBeetleLeftElytra.rotateAngleZ =+ MathHelper.lerp(flightTransition, this.boxBeetleLeftElytra.defaultRotateAngleZ, 0.69F);

        this.boxBeetleRightElytra.rotateAngleX =+ MathHelper.lerp(flightTransition, this.boxBeetleLeftElytra.defaultRotateAngleX, 0.87F);
        this.boxBeetleRightElytra.rotateAngleY =+ MathHelper.lerp(flightTransition, this.boxBeetleLeftElytra.defaultRotateAngleY, -0.52F);
        this.boxBeetleRightElytra.rotateAngleZ =+ MathHelper.lerp(flightTransition, this.boxBeetleLeftElytra.defaultRotateAngleZ, -0.69F);

        this.boxBeetleLeftWing.rotateAngleY = MathHelper.lerp(flightTransition, this.boxBeetleLeftElytra.defaultRotateAngleX, 0.26F);
        this.boxBeetleLeftWing.rotateAngleX = MathHelper.lerp(flightTransition, this.boxBeetleLeftWing.defaultRotateAngleX, swing(null, 1.75F * globalSpeed, globalDegree, false, 0.0F, 0.5F, ageInTicks, flightTransition * 0.5F, Axis.X));

        this.boxBeetleRightWing.rotateAngleX = MathHelper.lerp(flightTransition, this.boxBeetleRightWing.defaultRotateAngleX, swing(null, 1.75F * globalSpeed, globalDegree, false, 0.0F, 0.5F, ageInTicks, flightTransition * 0.5F, Axis.X));
        this.boxBeetleRightWing.rotateAngleY = MathHelper.lerp(flightTransition, this.boxBeetleLeftElytra.defaultRotateAngleX, -0.26F);

        boxBeetleBody.rotateAngleY =+ MathHelper.lerp(flightTransition, (netHeadYaw * ((float)Math.PI / 180F))/2, 0);
        boxBeetleBody.rotateAngleX =+ MathHelper.lerp(flightTransition, (headPitch * ((float)Math.PI / 180F))/2, 0);

        look(this.boxBeetleHead, netHeadYaw, headPitch, flightTransition + 1.0F, flightTransition + 1.0F);
        bob(this.boxBeetleBody, 0.5F * globalSpeed, 3 * 0.75F, false, ageInTicks, 0.5F, true);

        boxBeetleBody.rotationPointY =+ MathHelper.lerp(flightTransition, boxBeetleBody.defaultRotationPointY, (float)(boxBeetleBody.defaultRotationPointY + (Math.sin(ageInTicks * 0.5F * globalSpeed) * 0.5F * 3 * 0.75F - 0.5F * 3 * 0.75F)));

        if (this.bodyPitch > 0.0F) {
            this.bodyPitch = entityIn.getBodyPitch(0.5F);
            currentBodyPitch = ModelUtils.func_228283_a_(currentBodyPitch, 3.0915928F, this.bodyPitch);

            MathHelper.lerp(flightTransition, this.boxBeetleBody.defaultRotateAngleX, currentBodyPitch);
        }
    }
    
    public void setLivingAnimations(T entityIn, float limbSwing, float limbSwingAmount, float partialTick) {
        super.setLivingAnimations(entityIn, limbSwing, limbSwingAmount, partialTick);
        this.bodyPitch = entityIn.getBodyPitch(partialTick);
    }
}
