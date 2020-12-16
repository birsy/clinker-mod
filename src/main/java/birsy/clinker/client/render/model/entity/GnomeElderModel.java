package birsy.clinker.client.render.model.entity;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * GnomeElderModel - birsy
 * Created using Tabula 8.0.0
 */
@OnlyIn(Dist.CLIENT)
public class GnomeElderModel<T extends Entity> extends EntityModel<T> {
    public float[] modelScale = new float[] { 1.62F, 1.62F, 1.62F };
    public ModelRenderer elderArmRight;
    public ModelRenderer elderTorso;
    public ModelRenderer elderArmLeft;
    public ModelRenderer elderNeck;
    public ModelRenderer elderBack;
    public ModelRenderer elderLegRight;
    public ModelRenderer elderLegLeft;
    public ModelRenderer elderStaff;
    public ModelRenderer elderBell;
    public ModelRenderer elderHead;
    public ModelRenderer elderFaceMain;
    public ModelRenderer elderHelm;
    public ModelRenderer elderFaceBottom;
    public ModelRenderer elderFaceTop;
    public ModelRenderer elderNose;
    public ModelRenderer elderFaceBottom_1;
    public ModelRenderer elderFlapRight;
    public ModelRenderer elderFlapLeft;

    public GnomeElderModel() {
        this.textureWidth = 128;
        this.textureHeight = 64;
        this.elderLegLeft = new ModelRenderer(this, 55, 18);
        this.elderLegLeft.mirror = true;
        this.elderLegLeft.setRotationPoint(6.35F, 14.2F, 5.5F);
        this.elderLegLeft.addBox(-1.5F, -1.0F, -1.5F, 3.0F, 11.0F, 3.0F, -0.25F, -0.25F, -0.25F);
        this.elderNeck = new ModelRenderer(this, 26, 21);
        this.elderNeck.setRotationPoint(0.0F, 6.0F, -5.0F);
        this.elderNeck.addBox(-1.5F, -2.0F, -5.0F, 3.0F, 4.0F, 5.0F, 0.0F, 0.0F, 0.0F);
        this.elderArmRight = new ModelRenderer(this, 47, 18);
        this.elderArmRight.setRotationPoint(-7.75F, 7.5F, -2.0F);
        this.elderArmRight.addBox(-1.0F, -1.0F, -1.0F, 2.0F, 14.0F, 2.0F, -0.25F, -0.25F, -0.25F);
        this.elderFaceMain = new ModelRenderer(this, 0, 32);
        this.elderFaceMain.setRotationPoint(0.0F, -0.5F, -6.5F);
        this.elderFaceMain.addBox(-5.0F, -4.0F, -2.0F, 10.0F, 8.0F, 2.0F, 0.5F, 0.5F, 0.0F);
        this.setRotateAngle(elderFaceMain, -0.10471975511965977F, 0.0F, 0.0F);
        this.elderNose = new ModelRenderer(this, 37, 21);
        this.elderNose.setRotationPoint(0.0F, 0.0F, -1.25F);
        this.elderNose.addBox(-1.0F, -1.0F, -1.5F, 2.0F, 3.0F, 2.0F, 0.3F, 0.1F, 0.0F);
        this.setRotateAngle(elderNose, -0.1563815016444822F, 0.0F, 0.0F);
        this.elderFlapRight = new ModelRenderer(this, 0, 21);
        this.elderFlapRight.setRotationPoint(-4.8F, 0.4F, -0.5F);
        this.elderFlapRight.addBox(0.0F, -0.5F, -3.5F, 6.0F, 1.0F, 7.0F, 0.5F, 0.0F, 0.5F);
        this.setRotateAngle(elderFlapRight, 0.0F, 0.0F, 1.7453292519943295F);
        this.elderLegRight = new ModelRenderer(this, 55, 18);
        this.elderLegRight.setRotationPoint(-6.35F, 14.2F, 5.5F);
        this.elderLegRight.addBox(-1.5F, -1.0F, -1.5F, 3.0F, 11.0F, 3.0F, -0.25F, -0.25F, -0.25F);
        this.elderHelm = new ModelRenderer(this, 0, 49);
        this.elderHelm.setRotationPoint(0.0F, -4.0F, -1.7F);
        this.elderHelm.addBox(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F, 0.0F, 0.0F, 0.0F);
        this.elderArmLeft = new ModelRenderer(this, 47, 18);
        this.elderArmLeft.mirror = true;
        this.elderArmLeft.setRotationPoint(7.75F, 7.5F, -2.0F);
        this.elderArmLeft.addBox(-1.0F, -1.0F, -1.0F, 2.0F, 14.0F, 2.0F, -0.25F, -0.25F, -0.25F);
        this.setRotateAngle(elderArmLeft, -0.8726646259971648F, 0.0F, 0.0F);
        this.elderFaceTop = new ModelRenderer(this, 0, 29);
        this.elderFaceTop.setRotationPoint(0.0F, -4.6F, 0.0F);
        this.elderFaceTop.addBox(-3.5F, -1.0F, -2.0F, 7.0F, 1.0F, 2.0F, 0.5F, 0.1F, 0.0F);
        this.elderFlapLeft = new ModelRenderer(this, 0, 21);
        this.elderFlapLeft.setRotationPoint(4.8F, 0.4F, -0.5F);
        this.elderFlapLeft.addBox(0.0F, -0.5F, -3.5F, 6.0F, 1.0F, 7.0F, 0.5F, 0.0F, 0.5F);
        this.setRotateAngle(elderFlapLeft, 0.0F, 0.0F, 1.3962634015954636F);
        this.elderFaceBottom_1 = new ModelRenderer(this, 23, 18);
        this.elderFaceBottom_1.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.elderFaceBottom_1.setTextureOffset(44, 0).addBox(-4.0F, -0.3F, -1.0F, 8.0F, 8.0F, 1.0F, 0.2F, 0.2F, -0.3F);
        this.setRotateAngle(elderFaceBottom_1, 0.10471975511965977F, 0.0F, 0.0F);
        this.elderBack = new ModelRenderer(this, 0, 0);
        this.elderBack.setRotationPoint(0.0F, 4.0F, -3.8F);
        this.elderBack.addBox(-7.0F, -3.0F, -4.0F, 14.0F, 12.0F, 9.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(elderBack, 0.8991936386169619F, 0.0F, 0.0F);
        this.elderFaceBottom = new ModelRenderer(this, 0, 42);
        this.elderFaceBottom.setRotationPoint(0.0F, 5.0F, 0.0F);
        this.elderFaceBottom.addBox(-4.0F, -0.3F, -2.0F, 8.0F, 2.0F, 2.0F, 0.4F, 0.2F, 0.0F);
        this.elderBell = new ModelRenderer(this, 79, 0);
        this.elderBell.setRotationPoint(0.0F, -22.0F, -0.5F);
        this.elderBell.addBox(-2.0F, -0.5F, -2.0F, 4.0F, 5.0F, 4.0F, -0.5F, -0.5F, -0.5F);
        this.setRotateAngle(elderBell, -0.33929201590876146F, -0.5082398928281348F, -0.11728612207217244F);
        this.elderStaff = new ModelRenderer(this, 67, 31);
        this.elderStaff.setRotationPoint(0.0F, 12.0F, 0.0F);
        this.elderStaff.addBox(-0.5F, -23.25F, -0.4F, 1.0F, 32.0F, 1.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(elderStaff, 0.9773843811168246F, 0.0F, 0.0F);
        this.elderHead = new ModelRenderer(this, 10, 34);
        this.elderHead.setRotationPoint(0.0F, 1.0F, -6.0F);
        this.elderHead.setTextureOffset(14, 0).addBox(-4.0F, -4.0F, -6.0F, 8.0F, 8.0F, 7.0F, 0.5F, 0.5F, 0.5F);
        this.elderTorso = new ModelRenderer(this, 46, 0);
        this.elderTorso.setRotationPoint(0.0F, 11.0F, 3.9F);
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
    public void render(MatrixStack matrixStackIn, IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) { 
        matrixStackIn.push();
        matrixStackIn.scale(modelScale[0], modelScale[1], modelScale[2]);
        ImmutableList.of(this.elderLegLeft, this.elderNeck, this.elderArmRight, this.elderLegRight, this.elderArmLeft, this.elderBack, this.elderTorso).forEach((modelRenderer) -> { 
            modelRenderer.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
        });
        matrixStackIn.pop();
    }

    @Override
    public void setRotationAngles(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {}

    /**
     * This is a helper function from Tabula to set the rotation of model parts
     */
    public void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }
}
