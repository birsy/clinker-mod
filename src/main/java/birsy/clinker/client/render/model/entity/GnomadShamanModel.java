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
 * GnomadShaman - birsy
 * Created using Tabula 8.0.0
 */
@OnlyIn(Dist.CLIENT)
public class GnomadShamanModel<T extends Entity> extends EntityModel<T> {
    public ModelRenderer ArmL;
    public ModelRenderer ArmR;
    public ModelRenderer Neck;
    public ModelRenderer Torso;
    public ModelRenderer MainHead;
    public ModelRenderer Nose;
    public ModelRenderer FaceMain;
    public ModelRenderer FaceBottom;
    public ModelRenderer FaceTop;
    public ModelRenderer HatBottom;
    public ModelRenderer Beard;
    public ModelRenderer HatTop;
    public ModelRenderer HatRim;
    public ModelRenderer Back;

    public GnomadShamanModel() {
        this.textureWidth = 64;
        this.textureHeight = 64;
        this.Beard = new ModelRenderer(this, 41, 40);
        this.Beard.setRotationPoint(0.0F, -0.5F, 1.5F);
        this.Beard.addBox(-4.0F, 0.0F, 0.0F, 8.0F, 5.0F, 1.0F, 0.0F, 0.0F, 0.0F);
        this.ArmL = new ModelRenderer(this, 0, 0);
        this.ArmL.setRotationPoint(5.25F, 4.0F, -1.0F);
        this.ArmL.addBox(0.0F, -0.5F, -0.5F, 1.0F, 10.0F, 1.0F, 0.25F, 0.25F, 0.25F);
        this.HatTop = new ModelRenderer(this, 0, 11);
        this.HatTop.setRotationPoint(0.0F, -2.0F, 0.0F);
        this.HatTop.addBox(-1.5F, -2.0F, -1.5F, 3.0F, 3.0F, 3.0F, 0.0F, 0.0F, 0.0F);
        this.MainHead = new ModelRenderer(this, 0, 34);
        this.MainHead.setRotationPoint(0.0F, -0.9F, -2.0F);
        this.MainHead.addBox(-3.0F, -3.0F, -6.0F, 6.0F, 6.0F, 6.0F, 0.0F, 0.0F, 0.0F);
        this.Nose = new ModelRenderer(this, 46, 0);
        this.Nose.setRotationPoint(0.0F, -0.2F, -6.7F);
        this.Nose.addBox(-1.0F, -1.0F, -1.0F, 2.0F, 2.0F, 2.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(Nose, -0.3441789165090569F, 0.0F, 0.0F);
        this.FaceBottom = new ModelRenderer(this, 46, 4);
        this.FaceBottom.setRotationPoint(0.0F, 3.5F, -7.0F);
        this.FaceBottom.addBox(-3.0F, 0.0F, 0.0F, 6.0F, 2.0F, 1.0F, 0.0F, 0.0F, 0.0F);
        this.HatRim = new ModelRenderer(this, 0, 46);
        this.HatRim.setRotationPoint(0.0F, -1.0F, 0.0F);
        this.HatRim.addBox(-4.5F, 0.0F, -3.5F, 9.0F, 1.0F, 7.0F, 0.0F, 0.0F, 0.0F);
        this.Neck = new ModelRenderer(this, 8, 0);
        this.Neck.setRotationPoint(0.0F, 1.0F, -5.0F);
        this.Neck.addBox(-1.5F, -1.5F, -2.1F, 3.0F, 3.0F, 6.0F, 0.0F, 0.0F, 0.0F);
        this.FaceTop = new ModelRenderer(this, 46, 7);
        this.FaceTop.setRotationPoint(0.0F, -2.5F, -7.0F);
        this.FaceTop.addBox(-3.0F, -1.0F, 0.0F, 6.0F, 1.0F, 1.0F, 0.0F, 0.0F, 0.0F);
        this.Torso = new ModelRenderer(this, 14, 0);
        this.Torso.setRotationPoint(0.0F, 20.0F, 1.0F);
        this.Torso.addBox(-5.0F, -18.0F, -6.0F, 10.0F, 22.0F, 12.0F, 0.0F, 0.0F, 0.0F);
        this.Back = new ModelRenderer(this, 24, 46);
        this.Back.setRotationPoint(0.0F, -18.0F, -2.0F);
        this.Back.addBox(-5.0F, -3.0F, -4.0F, 10.0F, 3.0F, 8.0F, 0.0F, 0.0F, 0.0F);
        this.FaceMain = new ModelRenderer(this, 24, 34);
        this.FaceMain.setRotationPoint(0.0F, 0.0F, -7.0F);
        this.FaceMain.addBox(-4.0F, -2.5F, 0.0F, 8.0F, 6.0F, 1.0F, 0.0F, 0.0F, 0.0F);
        this.ArmR = new ModelRenderer(this, 4, 0);
        this.ArmR.setRotationPoint(-5.25F, 4.0F, -1.0F);
        this.ArmR.addBox(-1.0F, -0.5F, -0.5F, 1.0F, 10.0F, 1.0F, 0.25F, 0.25F, 0.25F);
        this.HatBottom = new ModelRenderer(this, 42, 34);
        this.HatBottom.setRotationPoint(0.0F, -3.0F, -3.0F);
        this.HatBottom.addBox(-2.0F, -2.0F, -2.0F, 4.0F, 2.0F, 4.0F, 0.0F, 0.0F, 0.0F);
        this.FaceBottom.addChild(this.Beard);
        this.HatBottom.addChild(this.HatTop);
        this.Neck.addChild(this.MainHead);
        this.MainHead.addChild(this.Nose);
        this.MainHead.addChild(this.FaceBottom);
        this.HatBottom.addChild(this.HatRim);
        this.MainHead.addChild(this.FaceTop);
        this.Torso.addChild(this.Back);
        this.MainHead.addChild(this.FaceMain);
        this.MainHead.addChild(this.HatBottom);
    }

    @Override
    public void render(MatrixStack matrixStackIn, IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) { 
        ImmutableList.of(this.ArmL, this.Neck, this.Torso, this.ArmR).forEach((modelRenderer) -> { 
            modelRenderer.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
        });
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
