package birsy.clinker.client.render.entity.model;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import birsy.clinker.client.render.util.BirsyBaseModel;
import birsy.clinker.client.render.util.BirsyModelRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import Entity;

@OnlyIn(Dist.CLIENT)
public class BombardierBeetleModel<T extends Entity> extends BirsyBaseModel<T> {
    public BirsyModelRenderer bombBeetleBody;
    public BirsyModelRenderer bombBeetleBlaster;
    public BirsyModelRenderer headJoint;
    public BirsyModelRenderer legsJoint;
    public BirsyModelRenderer bombBeetleHead;
    public BirsyModelRenderer bombBeetleRightAntennae;
    public BirsyModelRenderer bombBeetleLeftAntennae;
    public BirsyModelRenderer bombBeetleMiddleRightLeg;
    public BirsyModelRenderer bombBeetleFrontRightLeg;
    public BirsyModelRenderer bombBeetleBackRightLeg;
    public BirsyModelRenderer bombBeetleMiddleLeftLeg;
    public BirsyModelRenderer bombBeetleFrontLeftLeg;
    public BirsyModelRenderer bombBeetleBackLeftLeg;

    public BombardierBeetleModel() {
        this.texWidth = 64;
        this.texHeight = 64;
        this.bombBeetleRightAntennae = new BirsyModelRenderer(this, 24, 34);
        this.bombBeetleRightAntennae.setPos(-3.0F, -2.0F, -3.0F);
        this.bombBeetleRightAntennae.addBox(-0.5F, -4.0F, -1.5F, 1.0F, 4.0F, 2.0F, -0.0F, 0.0F, 0.0F);
        this.setRotateAngle(bombBeetleRightAntennae, 0.5585053606381855F, 0.0F, -0.41887902047863906F);
        this.bombBeetleBlaster = new BirsyModelRenderer(this, 0, 43);
        this.bombBeetleBlaster.setPos(0.0F, -24.0F, 0.0F);
        this.bombBeetleBlaster.addBox(-4.0F, -2.5F, -4.0F, 8.0F, 3.0F, 8.0F, 0.0F, 0.25F, 0.0F);
        this.setRotateAngle(bombBeetleBlaster, 0.20943951023931953F, 0.0F, 0.0F);
        this.bombBeetleFrontRightLeg = new BirsyModelRenderer(this, 0, 0);
        this.bombBeetleFrontRightLeg.setPos(-7.0F, -1.5F, -2.0F);
        this.bombBeetleFrontRightLeg.addBox(-4.0F, -0.5F, -0.5F, 4.0F, 5.0F, 1.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(bombBeetleFrontRightLeg, 0.0F, 0.0F, -0.3490658503988659F);
        this.headJoint = new BirsyModelRenderer(this, 0, 0);
        this.headJoint.setPos(0.0F, -1.0F, -4.0F);
        this.headJoint.addBox(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(headJoint, 0.20943951023931953F, 0.0F, 0.0F);
        this.legsJoint = new BirsyModelRenderer(this, 0, 0);
        this.legsJoint.setPos(0.0F, 0.0F, 0.0F);
        this.legsJoint.addBox(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(legsJoint, 0.20943951023931953F, 0.0F, 0.0F);
        this.bombBeetleHead = new BirsyModelRenderer(this, 0, 34);
        this.bombBeetleHead.setPos(0.0F, 0.0F, 0.0F);
        this.bombBeetleHead.addBox(-4.0F, -2.5F, -4.0F, 8.0F, 5.0F, 4.0F, 0.0F, 0.0F, 0.0F);
        this.bombBeetleFrontLeftLeg = new BirsyModelRenderer(this, 0, 0);
        this.bombBeetleFrontLeftLeg.mirror = true;
        this.bombBeetleFrontLeftLeg.setPos(7.0F, -1.5F, -2.0F);
        this.bombBeetleFrontLeftLeg.addBox(0.0F, -0.5F, -0.5F, 4.0F, 5.0F, 1.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(bombBeetleFrontLeftLeg, 0.0F, 0.0F, 0.3490658503988659F);
        this.bombBeetleBackRightLeg = new BirsyModelRenderer(this, 38, 0);
        this.bombBeetleBackRightLeg.setPos(-4.0F, 0.5F, 5.0F);
        this.bombBeetleBackRightLeg.addBox(-0.5F, -0.5F, -0.5F, 1.0F, 4.0F, 3.0F, 0.0F, 0.0F, 0.0F);
        this.bombBeetleMiddleRightLeg = new BirsyModelRenderer(this, 0, 0);
        this.bombBeetleMiddleRightLeg.setPos(-7.0F, -0.5F, 2.0F);
        this.bombBeetleMiddleRightLeg.addBox(-4.0F, -0.5F, -0.5F, 4.0F, 5.0F, 1.0F, 0.0F, 0.0F, 0.0F);
        this.bombBeetleMiddleLeftLeg = new BirsyModelRenderer(this, 0, 0);
        this.bombBeetleMiddleLeftLeg.mirror = true;
        this.bombBeetleMiddleLeftLeg.setPos(7.0F, -0.5F, 2.0F);
        this.bombBeetleMiddleLeftLeg.addBox(0.0F, -0.5F, -0.5F, 4.0F, 5.0F, 1.0F, 0.0F, 0.0F, 0.0F);
        this.bombBeetleBackLeftLeg = new BirsyModelRenderer(this, 38, 0);
        this.bombBeetleBackLeftLeg.setPos(4.0F, 0.5F, 5.0F);
        this.bombBeetleBackLeftLeg.addBox(-0.5F, -0.5F, -0.5F, 1.0F, 4.0F, 3.0F, 0.0F, 0.0F, 0.0F);
        this.bombBeetleLeftAntennae = new BirsyModelRenderer(this, 24, 34);
        this.bombBeetleLeftAntennae.setPos(3.0F, -2.0F, -3.0F);
        this.bombBeetleLeftAntennae.addBox(-0.5F, -4.0F, -1.5F, 1.0F, 4.0F, 2.0F, -0.0F, 0.0F, 0.0F);
        this.setRotateAngle(bombBeetleLeftAntennae, 0.5585053606381855F, 0.0F, 0.41887902047863906F);
        this.bombBeetleBody = new BirsyModelRenderer(this, 0, 0);
        this.bombBeetleBody.setPos(0.0F, 20.0F, 0.0F);
        this.bombBeetleBody.addBox(-7.0F, -24.0F, -5.0F, 14.0F, 24.0F, 10.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(bombBeetleBody, -0.20943951023931953F, 0.0F, 0.0F);
        this.bombBeetleHead.addChild(this.bombBeetleRightAntennae);
        this.bombBeetleBody.addChild(this.bombBeetleBlaster);
        this.legsJoint.addChild(this.bombBeetleFrontRightLeg);
        this.bombBeetleBody.addChild(this.headJoint);
        this.bombBeetleBody.addChild(this.legsJoint);
        this.headJoint.addChild(this.bombBeetleHead);
        this.legsJoint.addChild(this.bombBeetleFrontLeftLeg);
        this.legsJoint.addChild(this.bombBeetleBackRightLeg);
        this.legsJoint.addChild(this.bombBeetleMiddleRightLeg);
        this.legsJoint.addChild(this.bombBeetleMiddleLeftLeg);
        this.legsJoint.addChild(this.bombBeetleBackLeftLeg);
        this.bombBeetleHead.addChild(this.bombBeetleLeftAntennae);
    }

    @Override
    public void renderToBuffer(PoseStack matrixStackIn, VertexConsumer bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) { 
        ImmutableList.of(this.bombBeetleBody).forEach((modelRenderer) -> { 
            modelRenderer.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
        });
    }

    @Override
    public void setupAnim(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {}
}
