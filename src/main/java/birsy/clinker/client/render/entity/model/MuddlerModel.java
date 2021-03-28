package birsy.clinker.client.render.entity.model;

import birsy.clinker.client.render.util.BirsyBaseModel;
import birsy.clinker.client.render.util.BirsyModelRenderer;
import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * MuddlerModel - birse
 * Created using Tabula 8.0.0
 */
@OnlyIn(Dist.CLIENT)
public class MuddlerModel<T extends Entity> extends BirsyBaseModel<T> {
    public BirsyModelRenderer muddlerChest;
    public BirsyModelRenderer muddlerHood;
    public BirsyModelRenderer muddlerButtVines;
    public BirsyModelRenderer legsJoint;
    public BirsyModelRenderer armsJoint;
    public BirsyModelRenderer muddlerHead;
    public BirsyModelRenderer muddlerHat;
    public BirsyModelRenderer muddlerMudHat;
    public BirsyModelRenderer muddlerFace;
    public BirsyModelRenderer muddlerPlant;
    public BirsyModelRenderer muddlerRightLeg;
    public BirsyModelRenderer muddlerLeftLeg;
    public BirsyModelRenderer muddlerRightArm;
    public BirsyModelRenderer muddlerLeftArm;
    public BirsyModelRenderer muddlerShoulders;

    public MuddlerModel() {
        this.textureWidth = 128;
        this.textureHeight = 128;
        this.muddlerLeftLeg = new BirsyModelRenderer(this, 84, 17);
        this.muddlerLeftLeg.setRotationPoint(4.5F, 0.0F, 0.0F);
        this.muddlerLeftLeg.addBox(-2.0F, 0.0F, -1.5F, 4.0F, 12.0F, 3.0F, 0.0F, 0.0F, 0.0F);
        this.legsJoint = new BirsyModelRenderer(this, 0, 0);
        this.legsJoint.setRotationPoint(0.0F, 4.0F, 0.0F);
        this.legsJoint.addBox(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(legsJoint, -0.17453292519943295F, 0.0F, 0.0F);
        this.muddlerRightLeg = new BirsyModelRenderer(this, 70, 17);
        this.muddlerRightLeg.setRotationPoint(-4.5F, 0.0F, 0.0F);
        this.muddlerRightLeg.addBox(-2.0F, 0.0F, -1.5F, 4.0F, 12.0F, 3.0F, 0.0F, 0.0F, 0.0F);
        this.muddlerHood = new BirsyModelRenderer(this, 0, 32);
        this.muddlerHood.setRotationPoint(0.0F, -15.0F, 5.5F);
        this.muddlerHood.addBox(-7.0F, -24.0F, -14.0F, 14.0F, 24.0F, 14.0F, 0.02F, 0.0F, 0.0F);
        this.setRotateAngle(muddlerHood, 0.2617993877991494F, 0.0F, 0.0F);
        this.muddlerRightArm = new BirsyModelRenderer(this, 98, 17);
        this.muddlerRightArm.mirror = true;
        this.muddlerRightArm.setRotationPoint(-7.5F, 0.0F, 0.0F);
        this.muddlerRightArm.addBox(-1.5F, 0.0F, -2.0F, 3.0F, 24.0F, 4.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(muddlerRightArm, 0.0F, 0.20943951023931953F, 0.0F);
        this.armsJoint = new BirsyModelRenderer(this, 0, 0);
        this.armsJoint.setRotationPoint(0.0F, -9.0F, -4.0F);
        this.armsJoint.addBox(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(armsJoint, -0.17453292519943295F, 0.0F, 0.0F);
        this.muddlerPlant = new BirsyModelRenderer(this, 80, 92);
        this.muddlerPlant.setRotationPoint(0.0F, -31.5F, 0.0F);
        this.muddlerPlant.addBox(-4.0F, -15.0F, 0.0F, 8.0F, 25.0F, 0.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(muddlerPlant, 0.23457224414434488F, 0.0F, 0.11728612207217244F);
        this.muddlerLeftArm = new BirsyModelRenderer(this, 98, 17);
        this.muddlerLeftArm.setRotationPoint(7.5F, 0.0F, 0.0F);
        this.muddlerLeftArm.addBox(-1.5F, 0.0F, -2.0F, 3.0F, 24.0F, 4.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(muddlerLeftArm, 0.0F, -0.20943951023931953F, 0.0F);
        this.muddlerChest = new BirsyModelRenderer(this, 56, 35);
        this.muddlerChest.setRotationPoint(0.0F, 8.0F, 0.0F);
        this.muddlerChest.addBox(-7.0F, -15.0F, -5.5F, 14.0F, 21.0F, 11.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(muddlerChest, 0.17453292519943295F, 0.0F, 0.0F);
        this.muddlerButtVines = new BirsyModelRenderer(this, 70, 0);
        this.muddlerButtVines.setRotationPoint(0.0F, 6.0F, 5.5F);
        this.muddlerButtVines.addBox(-7.0F, 0.0F, -11.0F, 14.0F, 6.0F, 11.0F, -0.02F, 0.0F, 0.0F);
        this.setRotateAngle(muddlerButtVines, -0.17453292519943295F, 0.0F, 0.0F);
        this.muddlerHead = new BirsyModelRenderer(this, 26, 0);
        this.muddlerHead.setRotationPoint(0.0F, 0.0F, -5.5F);
        this.muddlerHead.addBox(-6.0F, -22.0F, -5.0F, 12.0F, 22.0F, 10.0F, 0.0F, 0.0F, 0.0F);
        this.muddlerHat = new BirsyModelRenderer(this, 80, 67);
        this.muddlerHat.setRotationPoint(0.0F, -10.0F, 0.0F);
        this.muddlerHat.addBox(-4.0F, -17.0F, -8.0F, 8.0F, 17.0F, 8.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(muddlerHat, -0.17453292519943295F, 0.0F, 0.0F);
        this.muddlerMudHat = new BirsyModelRenderer(this, 0, 70);
        this.muddlerMudHat.setRotationPoint(0.0F, 2.0F, -6.0F);
        this.muddlerMudHat.addBox(-10.0F, -31.0F, -10.0F, 20.0F, 31.0F, 20.0F, 0.0F, 0.0F, 0.0F);
        this.muddlerFace = new BirsyModelRenderer(this, 0, 0);
        this.muddlerFace.setRotationPoint(0.0F, -19.0F, -5.0F);
        this.muddlerFace.addBox(-3.0F, 0.0F, 0.0F, 6.0F, 16.0F, 7.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(muddlerFace, -0.2617993877991494F, 0.0F, 0.0F);
        this.muddlerShoulders = new BirsyModelRenderer(this, 0, 121);
        this.muddlerShoulders.setRotationPoint(0.0F, 0.0F, 1.5F);
        this.muddlerShoulders.addBox(-10.0F, -1.5F, -2.0F, 20.0F, 3.0F, 4.0F, -1.0F, 0.2F, 0.0F);
        this.setRotateAngle(muddlerShoulders, -0.46914448828868976F, 0.0F, 0.0F);
        this.legsJoint.addChild(this.muddlerLeftLeg);
        this.muddlerChest.addChild(this.legsJoint);
        this.legsJoint.addChild(this.muddlerRightLeg);
        this.muddlerChest.addChild(this.muddlerHood);
        this.armsJoint.addChild(this.muddlerRightArm);
        this.muddlerChest.addChild(this.armsJoint);
        this.muddlerMudHat.addChild(this.muddlerPlant);
        this.armsJoint.addChild(this.muddlerLeftArm);
        this.muddlerChest.addChild(this.muddlerButtVines);
        this.muddlerHood.addChild(this.muddlerHead);
        this.muddlerHood.addChild(this.muddlerHat);
        this.muddlerHood.addChild(this.muddlerMudHat);
        this.muddlerHead.addChild(this.muddlerFace);
        this.armsJoint.addChild(this.muddlerShoulders);
    }

    @Override
    public void render(MatrixStack matrixStackIn, IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) { 
        ImmutableList.of(this.muddlerChest).forEach((ModelRenderer) -> {
            ModelRenderer.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
        });
    }

    @Override
    public void setRotationAngles(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {}
}
