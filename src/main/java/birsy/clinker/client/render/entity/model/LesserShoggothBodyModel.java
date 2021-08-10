package birsy.clinker.client.render.entity.model;

import birsy.clinker.client.render.util.BirsyBaseModel;
import birsy.clinker.client.render.util.BirsyModelRenderer;
import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class LesserShoggothBodyModel<T extends Entity> extends BirsyBaseModel<T> {
    public BirsyModelRenderer shoggothBase;
    public BirsyModelRenderer legsJoint;
    public BirsyModelRenderer shoggothRightLegHolder;
    public BirsyModelRenderer shoggothLeftLegHolder;
    public BirsyModelRenderer shoggothFrontLegHolder;
    public BirsyModelRenderer shoggothBackLegHolder;
    public BirsyModelRenderer shoggothRightLeg;
    public BirsyModelRenderer shoggothLeftLeg;
    public BirsyModelRenderer shoggothFrontLeg;
    public BirsyModelRenderer shoggothBackLeg;

    public LesserShoggothBodyModel() {
        this.textureWidth = 128;
        this.textureHeight = 128;
        this.shoggothBase = new BirsyModelRenderer(this, 64, 81);
        this.shoggothBase.setRotationPoint(0.0F, 11.0F, 0.0F);
        this.shoggothBase.addBox(-8.0F, -8.0F, -8.0F, 16.0F, 16.0F, 16.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(shoggothBase, 0.0F, 0.7853981633974483F, 0.0F);
        this.shoggothBackLeg = new BirsyModelRenderer(this, 94, 32);
        this.shoggothBackLeg.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.shoggothBackLeg.addBox(-4.0F, 0.0F, -4.0F, 8.0F, 19.0F, 8.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(shoggothBackLeg, 0.0F, 0.7853981633974483F, 0.0F);
        this.shoggothLeftLeg = new BirsyModelRenderer(this, 94, 32);
        this.shoggothLeftLeg.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.shoggothLeftLeg.addBox(-4.0F, 0.0F, -4.0F, 8.0F, 19.0F, 8.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(shoggothLeftLeg, 0.0F, 0.7853981633974483F, 0.0F);
        this.shoggothRightLegHolder = new BirsyModelRenderer(this, 0, 0);
        this.shoggothRightLegHolder.setRotationPoint(-11.31F, 0.0F, 0.0F);
        this.shoggothRightLegHolder.addBox(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(shoggothRightLegHolder, 1.7453292129831807E-4F, 0.0F, 0.0F);
        this.shoggothLeftLegHolder = new BirsyModelRenderer(this, 0, 0);
        this.shoggothLeftLegHolder.setRotationPoint(11.31F, 0.0F, 0.0F);
        this.shoggothLeftLegHolder.addBox(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
        this.shoggothBackLegHolder = new BirsyModelRenderer(this, 0, 0);
        this.shoggothBackLegHolder.setRotationPoint(0.0F, 0.0F, 11.31F);
        this.shoggothBackLegHolder.addBox(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
        this.shoggothFrontLegHolder = new BirsyModelRenderer(this, 0, 0);
        this.shoggothFrontLegHolder.setRotationPoint(0.0F, 0.0F, -11.31F);
        this.shoggothFrontLegHolder.addBox(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
        this.shoggothRightLeg = new BirsyModelRenderer(this, 94, 32);
        this.shoggothRightLeg.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.shoggothRightLeg.addBox(-4.0F, 0.0F, -4.0F, 8.0F, 19.0F, 8.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(shoggothRightLeg, 0.0F, 0.7853981633974483F, 0.0F);
        this.legsJoint = new BirsyModelRenderer(this, 0, 0);
        this.legsJoint.setRotationPoint(0.0F, -6.0F, 0.0F);
        this.legsJoint.addBox(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(legsJoint, 0.0F, -0.7853981633974483F, 0.0F);
        this.shoggothFrontLeg = new BirsyModelRenderer(this, 94, 32);
        this.shoggothFrontLeg.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.shoggothFrontLeg.addBox(-4.0F, 0.0F, -4.0F, 8.0F, 19.0F, 8.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(shoggothFrontLeg, 0.0F, 0.7853981633974483F, 0.0F);
        this.shoggothBackLegHolder.addChild(this.shoggothBackLeg);
        this.shoggothLeftLegHolder.addChild(this.shoggothLeftLeg);
        this.legsJoint.addChild(this.shoggothRightLegHolder);
        this.legsJoint.addChild(this.shoggothLeftLegHolder);
        this.legsJoint.addChild(this.shoggothBackLegHolder);
        this.legsJoint.addChild(this.shoggothFrontLegHolder);
        this.shoggothRightLegHolder.addChild(this.shoggothRightLeg);
        this.shoggothBase.addChild(this.legsJoint);
        this.shoggothFrontLegHolder.addChild(this.shoggothFrontLeg);
    }

    @Override
    public void render(MatrixStack matrixStackIn, IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) { 
        ImmutableList.of(this.shoggothBase).forEach((ModelRenderer) -> {
            ModelRenderer.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
        });
    }

    @Override
    public void setRotationAngles(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {}
}
