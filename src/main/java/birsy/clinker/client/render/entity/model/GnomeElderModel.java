package birsy.clinker.client.render.entity.model;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import Entity;

/**
 * GnomeElderModel - birsy
 * Created using Tabula 8.0.0
 */
@OnlyIn(Dist.CLIENT)
public class GnomeElderModel<T extends Entity> extends EntityModel<T> {
    public float[] modelScale = new float[] { 1.62F, 1.62F, 1.62F };
    public ModelPart elderArmRight;
    public ModelPart elderTorso;
    public ModelPart elderArmLeft;
    public ModelPart elderNeck;
    public ModelPart elderBack;
    public ModelPart elderLegRight;
    public ModelPart elderLegLeft;
    public ModelPart elderStaff;
    public ModelPart elderBell;
    public ModelPart elderHead;
    public ModelPart elderFaceMain;
    public ModelPart elderHelm;
    public ModelPart elderFaceBottom;
    public ModelPart elderFaceTop;
    public ModelPart elderNose;
    public ModelPart elderFaceBottom_1;
    public ModelPart elderFlapRight;
    public ModelPart elderFlapLeft;

    public GnomeElderModel() {
        this.texWidth = 128;
        this.texHeight = 64;
        this.elderLegLeft = new ModelPart(this, 55, 18);
        this.elderLegLeft.mirror = true;
        this.elderLegLeft.setPos(6.35F, 14.2F, 5.5F);
        this.elderLegLeft.addBox(-1.5F, -1.0F, -1.5F, 3.0F, 11.0F, 3.0F, -0.25F, -0.25F, -0.25F);
        this.elderNeck = new ModelPart(this, 26, 21);
        this.elderNeck.setPos(0.0F, 6.0F, -5.0F);
        this.elderNeck.addBox(-1.5F, -2.0F, -5.0F, 3.0F, 4.0F, 5.0F, 0.0F, 0.0F, 0.0F);
        this.elderArmRight = new ModelPart(this, 47, 18);
        this.elderArmRight.setPos(-7.75F, 7.5F, -2.0F);
        this.elderArmRight.addBox(-1.0F, -1.0F, -1.0F, 2.0F, 14.0F, 2.0F, -0.25F, -0.25F, -0.25F);
        this.elderFaceMain = new ModelPart(this, 0, 32);
        this.elderFaceMain.setPos(0.0F, -0.5F, -6.5F);
        this.elderFaceMain.addBox(-5.0F, -4.0F, -2.0F, 10.0F, 8.0F, 2.0F, 0.5F, 0.5F, 0.0F);
        this.setRotateAngle(elderFaceMain, -0.10471975511965977F, 0.0F, 0.0F);
        this.elderNose = new ModelPart(this, 37, 21);
        this.elderNose.setPos(0.0F, 0.0F, -1.25F);
        this.elderNose.addBox(-1.0F, -1.0F, -1.5F, 2.0F, 3.0F, 2.0F, 0.3F, 0.1F, 0.0F);
        this.setRotateAngle(elderNose, -0.1563815016444822F, 0.0F, 0.0F);
        this.elderFlapRight = new ModelPart(this, 0, 21);
        this.elderFlapRight.setPos(-4.8F, 0.4F, -0.5F);
        this.elderFlapRight.addBox(0.0F, -0.5F, -3.5F, 6.0F, 1.0F, 7.0F, 0.5F, 0.0F, 0.5F);
        this.setRotateAngle(elderFlapRight, 0.0F, 0.0F, 1.7453292519943295F);
        this.elderLegRight = new ModelPart(this, 55, 18);
        this.elderLegRight.setPos(-6.35F, 14.2F, 5.5F);
        this.elderLegRight.addBox(-1.5F, -1.0F, -1.5F, 3.0F, 11.0F, 3.0F, -0.25F, -0.25F, -0.25F);
        this.elderHelm = new ModelPart(this, 0, 49);
        this.elderHelm.setPos(0.0F, -4.0F, -1.7F);
        this.elderHelm.addBox(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F, 0.0F, 0.0F, 0.0F);
        this.elderArmLeft = new ModelPart(this, 47, 18);
        this.elderArmLeft.mirror = true;
        this.elderArmLeft.setPos(7.75F, 7.5F, -2.0F);
        this.elderArmLeft.addBox(-1.0F, -1.0F, -1.0F, 2.0F, 14.0F, 2.0F, -0.25F, -0.25F, -0.25F);
        this.setRotateAngle(elderArmLeft, -0.8726646259971648F, 0.0F, 0.0F);
        this.elderFaceTop = new ModelPart(this, 0, 29);
        this.elderFaceTop.setPos(0.0F, -4.6F, 0.0F);
        this.elderFaceTop.addBox(-3.5F, -1.0F, -2.0F, 7.0F, 1.0F, 2.0F, 0.5F, 0.1F, 0.0F);
        this.elderFlapLeft = new ModelPart(this, 0, 21);
        this.elderFlapLeft.setPos(4.8F, 0.4F, -0.5F);
        this.elderFlapLeft.addBox(0.0F, -0.5F, -3.5F, 6.0F, 1.0F, 7.0F, 0.5F, 0.0F, 0.5F);
        this.setRotateAngle(elderFlapLeft, 0.0F, 0.0F, 1.3962634015954636F);
        this.elderFaceBottom_1 = new ModelPart(this, 23, 18);
        this.elderFaceBottom_1.setPos(0.0F, 0.0F, 0.0F);
        this.elderFaceBottom_1.texOffs(44, 0).addBox(-4.0F, -0.3F, -1.0F, 8.0F, 8.0F, 1.0F, 0.2F, 0.2F, -0.3F);
        this.setRotateAngle(elderFaceBottom_1, 0.10471975511965977F, 0.0F, 0.0F);
        this.elderBack = new ModelPart(this, 0, 0);
        this.elderBack.setPos(0.0F, 4.0F, -3.8F);
        this.elderBack.addBox(-7.0F, -3.0F, -4.0F, 14.0F, 12.0F, 9.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(elderBack, 0.8991936386169619F, 0.0F, 0.0F);
        this.elderFaceBottom = new ModelPart(this, 0, 42);
        this.elderFaceBottom.setPos(0.0F, 5.0F, 0.0F);
        this.elderFaceBottom.addBox(-4.0F, -0.3F, -2.0F, 8.0F, 2.0F, 2.0F, 0.4F, 0.2F, 0.0F);
        this.elderBell = new ModelPart(this, 79, 0);
        this.elderBell.setPos(0.0F, -22.0F, -0.5F);
        this.elderBell.addBox(-2.0F, -0.5F, -2.0F, 4.0F, 5.0F, 4.0F, -0.5F, -0.5F, -0.5F);
        this.setRotateAngle(elderBell, -0.33929201590876146F, -0.5082398928281348F, -0.11728612207217244F);
        this.elderStaff = new ModelPart(this, 67, 31);
        this.elderStaff.setPos(0.0F, 12.0F, 0.0F);
        this.elderStaff.addBox(-0.5F, -23.25F, -0.4F, 1.0F, 32.0F, 1.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(elderStaff, 0.9773843811168246F, 0.0F, 0.0F);
        this.elderHead = new ModelPart(this, 10, 34);
        this.elderHead.setPos(0.0F, 1.0F, -6.0F);
        this.elderHead.texOffs(14, 0).addBox(-4.0F, -4.0F, -6.0F, 8.0F, 8.0F, 7.0F, 0.5F, 0.5F, 0.5F);
        this.elderTorso = new ModelPart(this, 46, 0);
        this.elderTorso.setPos(0.0F, 11.0F, 3.9F);
        this.elderTorso.addBox(-6.0F, -4.0F, -4.5F, 12.0F, 9.0F, 9.0F, 0.0F, 0.0F, -0.2F);
        this.setRotateAngle(elderTorso, 0.3909537457888271F, 0.0F, 0.0F);
        this.elderHead.addChild(this.elderFaceMain);
        this.elderFaceMain.addChild(this.elderNose);
        this.elderHelm.addChild(this.elderFlapRight);
        this.elderHead.addChild(this.elderHelm);
        this.elderFaceMain.addChild(this.elderFaceTop);
        this.elderHelm.addChild(this.elderFlapLeft);
        this.elderFaceBottom.addChild(this.elderFaceBottom_1);
        this.elderFaceMain.addChild(this.elderFaceBottom);
        this.elderStaff.addChild(this.elderBell);
        this.elderArmLeft.addChild(this.elderStaff);
        this.elderNeck.addChild(this.elderHead);
    }

    @Override
    public void renderToBuffer(PoseStack matrixStackIn, VertexConsumer bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) { 
        matrixStackIn.pushPose();
        matrixStackIn.scale(modelScale[0], modelScale[1], modelScale[2]);
        ImmutableList.of(this.elderLegLeft, this.elderNeck, this.elderArmRight, this.elderLegRight, this.elderArmLeft, this.elderBack, this.elderTorso).forEach((modelRenderer) -> { 
            modelRenderer.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
        });
        matrixStackIn.popPose();
    }

    @Override
    public void setupAnim(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {}

    /**
     * This is a helper function from Tabula to set the rotation of model parts
     */
    public void setRotateAngle(ModelPart modelRenderer, float x, float y, float z) {
        modelRenderer.xRot = x;
        modelRenderer.yRot = y;
        modelRenderer.zRot = z;
    }
}
