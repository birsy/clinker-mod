package birsy.clinker.client.render.entity.model;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * witchGolem - birsy
 * Created using Tabula 8.0.0
 */
@OnlyIn(Dist.CLIENT)
public class WitchGolemModel<T extends Entity> extends EntityModel<T> {
    public ModelRenderer witchGolemWaist;
    public ModelRenderer witchGolemLeftLeg;
    public ModelRenderer witchGolemRightLeg;
    public ModelRenderer witchGolemNeck;
    public ModelRenderer witchGolemRightArm;
    public ModelRenderer witchGolemLeftArm;
    public ModelRenderer witchGolemSpine;
    public ModelRenderer witchGolemTorso;
    public ModelRenderer witchGolemHead;
    public ModelRenderer witchGolemMask;
    public ModelRenderer witchGolemHair1;
    public ModelRenderer witchGolemHair2;
    public ModelRenderer witchGolemNose1;
    public ModelRenderer witchGolemNose2;

    public WitchGolemModel() {
        this.textureWidth = 128;
        this.textureHeight = 64;
        this.witchGolemNose1 = new ModelRenderer(this, 10, 0);
        this.witchGolemNose1.setRotationPoint(0.0F, -1.0F, -1.0F);
        this.witchGolemNose1.addBox(-1.0F, -1.0F, -1.0F, 2.0F, 2.0F, 1.0F, 0.0F, 0.0F, 0.0F);
        this.witchGolemRightArm = new ModelRenderer(this, 54, 0);
        this.witchGolemRightArm.setRotationPoint(-7.0F, -5.0F, 0.0F);
        this.witchGolemRightArm.addBox(-3.0F, -1.5F, -1.5F, 3.0F, 16.0F, 3.0F, 0.0F, 0.0F, 0.0F);
        this.witchGolemLeftArm = new ModelRenderer(this, 66, 0);
        this.witchGolemLeftArm.setRotationPoint(7.0F, -5.0F, 0.0F);
        this.witchGolemLeftArm.addBox(0.0F, -1.5F, -1.5F, 3.0F, 16.0F, 3.0F, 0.0F, 0.0F, 0.0F);
        this.witchGolemRightLeg = new ModelRenderer(this, 14, 0);
        this.witchGolemRightLeg.setRotationPoint(-5.0F, 12.0F, 0.0F);
        this.witchGolemRightLeg.addBox(-3.0F, -2.0F, -2.0F, 3.0F, 14.0F, 4.0F, 0.0F, 0.0F, 0.0F);
        this.witchGolemNeck = new ModelRenderer(this, 28, 0);
        this.witchGolemNeck.setRotationPoint(0.0F, -7.0F, 0.0F);
        this.witchGolemNeck.addBox(-4.0F, -7.0F, -2.5F, 8.0F, 7.0F, 5.0F, 0.0F, 0.0F, 0.0F);
        this.witchGolemWaist = new ModelRenderer(this, 0, 23);
        this.witchGolemWaist.setRotationPoint(0.0F, 9.0F, 0.0F);
        this.witchGolemWaist.addBox(-5.0F, 0.0F, -3.0F, 10.0F, 5.0F, 6.0F, 0.0F, 0.0F, 0.0F);
        this.witchGolemHair2 = new ModelRenderer(this, 68, 14);
        this.witchGolemHair2.setRotationPoint(-3.5F, -3.5F, -1.5F);
        this.witchGolemHair2.addBox(-5.0F, -0.5F, -3.0F, 10.0F, 1.0F, 10.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(witchGolemHair2, -0.18203784630933073F, -1.3203416057653212F, -0.8651597048872669F);
        this.witchGolemMask = new ModelRenderer(this, 106, 0);
        this.witchGolemMask.setRotationPoint(0.0F, -3.0F, -4.5F);
        this.witchGolemMask.addBox(-4.0F, -4.5F, -1.0F, 8.0F, 9.0F, 2.0F, 0.0F, 0.0F, 0.0F);
        this.witchGolemHead = new ModelRenderer(this, 78, 0);
        this.witchGolemHead.setRotationPoint(0.0F, -6.0F, 0.0F);
        this.witchGolemHead.addBox(-3.5F, -7.0F, -5.0F, 7.0F, 7.0F, 7.0F, 0.0F, 0.0F, 0.0F);
        this.witchGolemSpine = new ModelRenderer(this, 32, 23);
        this.witchGolemSpine.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.witchGolemSpine.addBox(-4.0F, -9.0F, -2.5F, 8.0F, 9.0F, 5.0F, 0.0F, 0.0F, 0.0F);
        this.witchGolemNose2 = new ModelRenderer(this, 75, 0);
        this.witchGolemNose2.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.witchGolemNose2.addBox(-2.0F, 1.0F, -1.0F, 4.0F, 1.0F, 1.0F, 0.0F, 0.0F, 0.0F);
        this.witchGolemTorso = new ModelRenderer(this, 58, 25);
        this.witchGolemTorso.setRotationPoint(0.0F, -9.0F, 0.0F);
        this.witchGolemTorso.addBox(-7.0F, -7.0F, -3.5F, 14.0F, 7.0F, 7.0F, 0.0F, 0.0F, 0.0F);
        this.witchGolemHair1 = new ModelRenderer(this, 18, 12);
        this.witchGolemHair1.setRotationPoint(3.5F, -3.5F, -1.5F);
        this.witchGolemHair1.addBox(-5.0F, -0.5F, -3.0F, 10.0F, 1.0F, 10.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(witchGolemHair1, 0.0F, 1.48352986419518F, 1.0471975511965976F);
        this.witchGolemLeftLeg = new ModelRenderer(this, 0, 0);
        this.witchGolemLeftLeg.setRotationPoint(5.0F, 12.0F, 0.0F);
        this.witchGolemLeftLeg.addBox(0.0F, -2.0F, -2.0F, 3.0F, 14.0F, 4.0F, 0.0F, 0.0F, 0.0F);
        this.witchGolemMask.addChild(this.witchGolemNose1);
        this.witchGolemHead.addChild(this.witchGolemHair2);
        this.witchGolemHead.addChild(this.witchGolemMask);
        this.witchGolemNeck.addChild(this.witchGolemHead);
        this.witchGolemWaist.addChild(this.witchGolemSpine);
        this.witchGolemNose1.addChild(this.witchGolemNose2);
        this.witchGolemSpine.addChild(this.witchGolemTorso);
        this.witchGolemHead.addChild(this.witchGolemHair1);
    }

    @Override
    public void render(MatrixStack matrixStackIn, IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) { 
        ImmutableList.of(this.witchGolemRightArm, this.witchGolemLeftArm, this.witchGolemRightLeg, this.witchGolemNeck, this.witchGolemWaist, this.witchGolemLeftLeg).forEach((modelRenderer) -> { 
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
