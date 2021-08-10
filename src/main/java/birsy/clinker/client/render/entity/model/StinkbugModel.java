package birsy.clinker.client.render.entity.model;

import birsy.clinker.client.render.util.BirsyBaseModel;
import birsy.clinker.client.render.util.BirsyModelRenderer;
import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Random;

@OnlyIn(Dist.CLIENT)
public class StinkbugModel<T extends Entity> extends BirsyBaseModel<T> {
    public BirsyModelRenderer rootJoint;
    public BirsyModelRenderer stinkbugBody;
    public BirsyModelRenderer headJoint;
    public BirsyModelRenderer armsJoint;
    public BirsyModelRenderer frontLegJoint;
    public BirsyModelRenderer backLegJoint;
    public BirsyModelRenderer stinkbugVentHolder;
    public BirsyModelRenderer stinkbugHead;
    public BirsyModelRenderer stinkbugLeftAntennae;
    public BirsyModelRenderer stinkbugRightAntennae;
    public BirsyModelRenderer stinkbugLeftPincer;
    public BirsyModelRenderer stinkbugRightPincer;
    public BirsyModelRenderer stinkbugUpperLeftArm;
    public BirsyModelRenderer stinkbugUpperRightArm;
    public BirsyModelRenderer stinkbugLowerLeftArm;
    public BirsyModelRenderer stinkbugLowerRightArm;
    public BirsyModelRenderer stinkbugFrontLeftLeg;
    public BirsyModelRenderer stinkbugFrontRightLeg;
    public BirsyModelRenderer stinkbugBackRightLeg;
    public BirsyModelRenderer stinkbugBackLeftLeg;


    public StinkbugModel() {
        this.textureWidth = 128;
        this.textureHeight = 64;

        this.stinkbugLowerRightArm = new BirsyModelRenderer(this, 105, 0);
        this.stinkbugLowerRightArm.mirror = true;
        this.stinkbugLowerRightArm.setRotationPoint(-1.0F, 6.0F, 1.0F);
        this.stinkbugLowerRightArm.addBox(-1.0F, -1.0F, -9.0F, 2.0F, 5.0F, 9.0F, 0.0F, 0.0F, 0.0F);
        this.stinkbugRightPincer = new BirsyModelRenderer(this, 79, 0);
        this.stinkbugRightPincer.mirror = true;
        this.stinkbugRightPincer.setRotationPoint(-2.0F, -1.0F, -16.0F);
        this.stinkbugRightPincer.addBox(-1.0F, -1.0F, -2.0F, 2.0F, 4.0F, 4.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(stinkbugRightPincer, -0.4363323129985824F, 0.08726646259971647F, -0.2617993877991494F);
        this.rootJoint = new BirsyModelRenderer(this, 0, 0);
        this.rootJoint.setRotationPoint(0.0F, 24.0F, 0.0F);
        this.rootJoint.addBox(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
        this.stinkbugBackLeftLeg = new BirsyModelRenderer(this, 67, 0);
        this.stinkbugBackLeftLeg.setRotationPoint(6.5F, 0.5F, 0.5F);
        this.stinkbugBackLeftLeg.addBox(-1.5F, 0.0F, -1.5F, 3.0F, 12.0F, 3.0F, -0.25F, 0.0F, -0.25F);
        this.setRotateAngle(stinkbugBackLeftLeg, 0.0F, 0.0F, -0.08726646259971647F);
        this.stinkbugBody = new BirsyModelRenderer(this, 62, 26);
        this.stinkbugBody.setRotationPoint(0.0F, -13.0F, 0.0F);
        this.stinkbugBody.addBox(-8.0F, -18.0F, -8.5F, 16.0F, 21.0F, 17.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(stinkbugBody, 0.20943951023931953F, 0.0F, 0.0F);
        this.backLegJoint = new BirsyModelRenderer(this, 0, 0);
        this.backLegJoint.setRotationPoint(0.0F, 2.0F, 7.5F);
        this.backLegJoint.addBox(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(backLegJoint, -0.20943951023931953F, 0.0F, 0.0F);
        this.stinkbugHead = new BirsyModelRenderer(this, 0, 0);
        this.stinkbugHead.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.stinkbugHead.addBox(-8.5F, -8.0F, -16.0F, 17.0F, 8.0F, 21.0F, 0.0F, 0.0F, 0.0F);
        this.stinkbugVentHolder = new BirsyModelRenderer(this, 0, 50);
        this.stinkbugVentHolder.setRotationPoint(0.0F, -13.0F, 4.0F);
        this.stinkbugVentHolder.addBox(-8.0F, -4.5F, -1.0F, 16.0F, 9.0F, 5.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(stinkbugVentHolder, 0.11728612207217244F, 0.0F, 0.0F);
        this.stinkbugUpperLeftArm = new BirsyModelRenderer(this, 105, 0);
        this.stinkbugUpperLeftArm.setRotationPoint(8.0F, 0.0F, 0.0F);
        this.stinkbugUpperLeftArm.addBox(0.0F, -1.0F, -1.0F, 2.0F, 6.0F, 2.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(stinkbugUpperLeftArm, 0.0F, 0.0F, -0.10471975511965977F);
        this.stinkbugBackRightLeg = new BirsyModelRenderer(this, 67, 0);
        this.stinkbugBackRightLeg.mirror = true;
        this.stinkbugBackRightLeg.setRotationPoint(-6.5F, 0.5F, 0.5F);
        this.stinkbugBackRightLeg.addBox(-1.5F, 0.0F, -1.5F, 3.0F, 12.0F, 3.0F, -0.25F, 0.0F, -0.25F);
        this.setRotateAngle(stinkbugBackRightLeg, 0.0F, 0.0F, 0.08726646259971647F);
        this.frontLegJoint = new BirsyModelRenderer(this, 0, 0);
        this.frontLegJoint.setRotationPoint(0.0F, 2.0F, -7.5F);
        this.frontLegJoint.addBox(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(frontLegJoint, -0.5061454830783556F, 0.0F, 0.0F);
        this.stinkbugFrontRightLeg = new BirsyModelRenderer(this, 55, 0);
        this.stinkbugFrontRightLeg.mirror = true;
        this.stinkbugFrontRightLeg.setRotationPoint(-6.5F, 0.5F, -0.5F);
        this.stinkbugFrontRightLeg.addBox(-1.5F, 0.0F, -1.5F, 3.0F, 10.0F, 3.0F, -0.25F, 0.0F, -0.25F);
        this.setRotateAngle(stinkbugFrontRightLeg, 0.0F, 0.0F, 0.08726646259971647F);
        this.stinkbugUpperRightArm = new BirsyModelRenderer(this, 105, 0);
        this.stinkbugUpperRightArm.mirror = true;
        this.stinkbugUpperRightArm.setRotationPoint(-8.0F, 0.0F, 0.0F);
        this.stinkbugUpperRightArm.addBox(-2.0F, -1.0F, -1.0F, 2.0F, 6.0F, 2.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(stinkbugUpperRightArm, 0.0F, 0.0F, 0.10471975511965977F);
        this.stinkbugLowerLeftArm = new BirsyModelRenderer(this, 105, 0);
        this.stinkbugLowerLeftArm.setRotationPoint(1.0F, 6.0F, 1.0F);
        this.stinkbugLowerLeftArm.addBox(-1.0F, -1.0F, -9.0F, 2.0F, 5.0F, 9.0F, 0.0F, 0.0F, 0.0F);
        this.stinkbugLeftAntennae = new BirsyModelRenderer(this, 79, 0);
        this.stinkbugLeftAntennae.setRotationPoint(4.5F, -8.0F, -10.0F);
        this.stinkbugLeftAntennae.addBox(-0.5F, -7.0F, -11.5F, 1.0F, 8.0F, 12.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(stinkbugLeftAntennae, 0.2617993877991494F, -0.4363323129985824F, 0.0F);
        this.headJoint = new BirsyModelRenderer(this, 0, 0);
        this.headJoint.setRotationPoint(0.0F, -16.0F, -3.0F);
        this.headJoint.addBox(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(headJoint, -0.20943951023931953F, 0.0F, 0.0F);
        this.stinkbugFrontLeftLeg = new BirsyModelRenderer(this, 55, 0);
        this.stinkbugFrontLeftLeg.setRotationPoint(6.5F, 0.5F, -0.5F);
        this.stinkbugFrontLeftLeg.addBox(-1.5F, 0.0F, -1.5F, 3.0F, 10.0F, 3.0F, -0.25F, 0.0F, -0.25F);
        this.setRotateAngle(stinkbugFrontLeftLeg, 0.0F, 0.0F, -0.08726646259971647F);
        this.armsJoint = new BirsyModelRenderer(this, 0, 0);
        this.armsJoint.setRotationPoint(0.0F, -7.0F, -4.0F);
        this.armsJoint.addBox(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(armsJoint, -0.3490658503988659F, 0.0F, 0.0F);
        this.stinkbugLeftPincer = new BirsyModelRenderer(this, 79, 0);
        this.stinkbugLeftPincer.setRotationPoint(2.0F, -1.0F, -16.0F);
        this.stinkbugLeftPincer.addBox(-1.0F, -1.0F, -2.0F, 2.0F, 4.0F, 4.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(stinkbugLeftPincer, -0.4363323129985824F, -0.08726646259971647F, 0.2617993877991494F);
        this.stinkbugRightAntennae = new BirsyModelRenderer(this, 79, 0);
        this.stinkbugRightAntennae.mirror = true;
        this.stinkbugRightAntennae.setRotationPoint(-4.5F, -8.0F, -10.0F);
        this.stinkbugRightAntennae.addBox(-0.5F, -7.0F, -11.5F, 1.0F, 8.0F, 12.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(stinkbugRightAntennae, 0.2617993877991494F, 0.4363323129985824F, 0.0F);

        this.stinkbugUpperRightArm.addChild(this.stinkbugLowerRightArm);
        this.stinkbugHead.addChild(this.stinkbugRightPincer);
        this.backLegJoint.addChild(this.stinkbugBackLeftLeg);
        this.rootJoint.addChild(this.stinkbugBody);
        this.stinkbugBody.addChild(this.backLegJoint);
        this.headJoint.addChild(this.stinkbugHead);
        this.stinkbugBody.addChild(this.stinkbugVentHolder);
        this.armsJoint.addChild(this.stinkbugUpperLeftArm);
        this.backLegJoint.addChild(this.stinkbugBackRightLeg);
        this.stinkbugBody.addChild(this.frontLegJoint);
        this.frontLegJoint.addChild(this.stinkbugFrontRightLeg);
        this.armsJoint.addChild(this.stinkbugUpperRightArm);
        this.stinkbugUpperLeftArm.addChild(this.stinkbugLowerLeftArm);
        this.stinkbugHead.addChild(this.stinkbugLeftAntennae);
        this.stinkbugBody.addChild(this.headJoint);
        this.frontLegJoint.addChild(this.stinkbugFrontLeftLeg);
        this.stinkbugBody.addChild(this.armsJoint);
        this.stinkbugHead.addChild(this.stinkbugLeftPincer);
        this.stinkbugHead.addChild(this.stinkbugRightAntennae);
    }

    @Override
    public void render(MatrixStack matrixStackIn, IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) { 
        ImmutableList.of(this.rootJoint).forEach((BirsyModelRenderer) -> { 
            BirsyModelRenderer.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
        });
    }

    @Override
    public void setRotationAngles(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
    }
}
