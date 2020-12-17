package birsy.clinker.client.render.entity;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import birsy.clinker.client.render.model.BirsyBaseModel;
import birsy.clinker.client.render.model.BirsyModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BoxBeetleModel<T extends Entity> extends BirsyBaseModel<T> {
    public BirsyModelRenderer boxBeetleBody;
    public BirsyModelRenderer boxBeelteLeftElytra;
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

    public BoxBeetleModel() {
        this.textureWidth = 128;
        this.textureHeight = 128;
        this.boxBeelteLeftElytra = new BirsyModelRenderer(this, 0, 75);
        this.boxBeelteLeftElytra.mirror = true;
        this.boxBeelteLeftElytra.setRotationPoint(4.25F, -12.0F, -12.0F);
        this.boxBeelteLeftElytra.addBox(-4.0F, -4.0F, 0.0F, 8.0F, 11.0F, 24.0F, 0.25F, 0.5F, 0.5F);
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
        this.boxBeetleBody.addChild(this.boxBeelteLeftElytra);
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
    public void setRotationAngles(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {}
}
