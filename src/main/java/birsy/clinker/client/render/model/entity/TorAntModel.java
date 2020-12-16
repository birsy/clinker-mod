package birsy.clinker.client.render.model.entity;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import birsy.clinker.common.entity.passive.TorAntEntity;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * TorAntModel - doclg
 * Created using Tabula 8.0.0
 */
@OnlyIn(Dist.CLIENT)
public class TorAntModel<T extends TorAntEntity> extends EntityModel<T> {
    public ModelRenderer torAntThorax;
    public ModelRenderer torAntHead;
    public ModelRenderer torAntFrontLeftLeg;
    public ModelRenderer torAntFrontRightLeg;
    public ModelRenderer torAntBackLeftLeg;
    public ModelRenderer torAntBackRightLeg;
    public ModelRenderer torAntSaddle;
    public ModelRenderer torAntAbdomen;
    public ModelRenderer torAntHair4;
    public ModelRenderer torAntHair5;
    public ModelRenderer torAntSmallHair3;
    public ModelRenderer torAntHair2;
    public ModelRenderer torAntHair1;
    public ModelRenderer torAntHair3;
    public ModelRenderer torAntSmallHair1;
    public ModelRenderer torAntSmallHair2;
    public ModelRenderer torAntBeard;
    public ModelRenderer torAntJaw;
    public ModelRenderer torAntSmallHair4;
    public ModelRenderer torAntSmallHair5;
    public ModelRenderer torAntFrontLeftLegBottom;
    public ModelRenderer torAntFrontRightLegBottom;
    public ModelRenderer torAntBackLeftLegBottom;
    public ModelRenderer torAntBackRightLegBottom;

    public TorAntModel() {
        this.textureWidth = 128;
        this.textureHeight = 64;
        this.torAntHair1 = new ModelRenderer(this, 0, 41);
        this.torAntHair1.setRotationPoint(0.0F, -4.0F, 2.0F);
        this.torAntHair1.addBox(-4.5F, -4.0F, 0.0F, 9.0F, 4.0F, 1.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(torAntHair1, -1.2510520131558576F, -0.1563815016444822F, 0.0F);
        this.torAntBackRightLegBottom = new ModelRenderer(this, 44, 11);
        this.torAntBackRightLegBottom.setRotationPoint(-0.25F, 10.0F, 0.0F);
        this.torAntBackRightLegBottom.addBox(-0.5F, -1.0F, -0.5F, 1.0F, 16.0F, 1.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(torAntBackRightLegBottom, -0.20943951023931953F, 0.0F, -0.20943951023931953F);
        this.torAntBackRightLeg = new ModelRenderer(this, 36, 11);
        this.torAntBackRightLeg.mirror = true;
        this.torAntBackRightLeg.setRotationPoint(-8.0F, -0.5F, 8.0F);
        this.torAntBackRightLeg.addBox(-1.0F, -2.0F, -1.0F, 2.0F, 12.0F, 2.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(torAntBackRightLeg, 0.20943951023931953F, 0.0F, 0.10471975511965977F);
        this.torAntFrontRightLeg = new ModelRenderer(this, 36, 11);
        this.torAntFrontRightLeg.setRotationPoint(-5.4F, 1.5F, -7.0F);
        this.torAntFrontRightLeg.addBox(-1.0F, -2.0F, -1.0F, 2.0F, 15.0F, 2.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(torAntFrontRightLeg, 0.20943951023931953F, 0.0F, 0.06981317007977318F);
        this.torAntAbdomen = new ModelRenderer(this, 0, 34);
        this.torAntAbdomen.setRotationPoint(0.0F, 0.0F, 6.0F);
        this.torAntAbdomen.addBox(-7.0F, -4.0F, 0.0F, 14.0F, 7.0F, 20.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(torAntAbdomen, -0.3490658503988659F, 0.0F, 0.0F);
        this.torAntJaw = new ModelRenderer(this, 25, 0);
        this.torAntJaw.setRotationPoint(0.0F, 1.0F, -2.5F);
        this.torAntJaw.addBox(-2.5F, 0.0F, -7.0F, 5.0F, 3.0F, 7.0F, 0.0F, -0.3F, 0.0F);
        this.setRotateAngle(torAntJaw, 0.23457224414434488F, 0.0F, 0.0F);
        this.torAntSmallHair1 = new ModelRenderer(this, 48, 42);
        this.torAntSmallHair1.setRotationPoint(2.0F, -4.0F, 7.0F);
        this.torAntSmallHair1.addBox(-3.0F, -3.0F, 0.0F, 6.0F, 3.0F, 1.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(torAntSmallHair1, -0.8600982340775168F, 0.19547687289441354F, 0.0F);
        this.torAntHair3 = new ModelRenderer(this, 0, 50);
        this.torAntHair3.setRotationPoint(0.0F, -4.0F, 17.9F);
        this.torAntHair3.addBox(-4.5F, -3.0F, 0.5F, 9.0F, 3.0F, 1.0F, 0.0F, 0.0F, 0.5F);
        this.setRotateAngle(torAntHair3, -0.8600982340775168F, 0.0F, 0.0F);
        this.torAntBackLeftLegBottom = new ModelRenderer(this, 44, 11);
        this.torAntBackLeftLegBottom.setRotationPoint(0.25F, 10.0F, 0.0F);
        this.torAntBackLeftLegBottom.addBox(-0.5F, -1.0F, -0.5F, 1.0F, 16.0F, 1.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(torAntBackLeftLegBottom, -0.20943951023931953F, 0.0F, 0.20943951023931953F);
        this.torAntSmallHair3 = new ModelRenderer(this, 48, 50);
        this.torAntSmallHair3.setRotationPoint(-1.0F, -3.0F, -0.5F);
        this.torAntSmallHair3.addBox(-3.0F, -3.0F, 0.0F, 6.0F, 3.0F, 1.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(torAntSmallHair3, -1.0555751236166873F, -0.03909537541112055F, 0.0F);
        this.torAntHair4 = new ModelRenderer(this, 0, 50);
        this.torAntHair4.setRotationPoint(0.0F, -3.0F, 2.0F);
        this.torAntHair4.addBox(-4.5F, -3.0F, 0.5F, 9.0F, 3.0F, 1.0F, 0.0F, 0.0F, 0.5F);
        this.setRotateAngle(torAntHair4, -1.2119566751954398F, 0.1563815016444822F, 0.0F);
        this.torAntFrontRightLegBottom = new ModelRenderer(this, 44, 13);
        this.torAntFrontRightLegBottom.setRotationPoint(-0.25F, 13.0F, 0.0F);
        this.torAntFrontRightLegBottom.addBox(-0.5F, -4.0F, -0.5F, 1.0F, 14.0F, 1.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(torAntFrontRightLegBottom, -0.41887902047863906F, 0.0F, -0.13962634015954636F);
        this.torAntFrontLeftLeg = new ModelRenderer(this, 36, 11);
        this.torAntFrontLeftLeg.setRotationPoint(5.4F, 1.5F, -7.0F);
        this.torAntFrontLeftLeg.addBox(-1.0F, -2.0F, -1.0F, 2.0F, 15.0F, 2.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(torAntFrontLeftLeg, 0.20943951023931953F, 0.0F, -0.06981317007977318F);
        this.torAntSaddle = new ModelRenderer(this, 49, 0);
        this.torAntSaddle.setRotationPoint(0.0F, -4.4F, -1.0F);
        this.torAntSaddle.addBox(-7.0F, -1.0F, -6.0F, 14.0F, 5.0F, 12.0F, 0.2F, 0.2F, 0.2F);
        this.setRotateAngle(torAntSaddle, 0.11728612207217244F, 0.0F, 0.0F);
        this.torAntBeard = new ModelRenderer(this, 0, 34);
        this.torAntBeard.setRotationPoint(0.0F, 2.0F, -4.0F);
        this.torAntBeard.addBox(-4.0F, -0.5F, -0.5F, 8.0F, 6.0F, 1.0F, -0.5F, -0.5F, 0.0F);
        this.torAntHair5 = new ModelRenderer(this, 0, 46);
        this.torAntHair5.setRotationPoint(0.0F, -3.0F, -4.1F);
        this.torAntHair5.addBox(-4.5F, -3.0F, 0.5F, 9.0F, 3.0F, 1.0F, 0.0F, 0.0F, 0.1F);
        this.setRotateAngle(torAntHair5, -0.7819074915776542F, -0.0781907508222411F, 0.0F);
        this.torAntSmallHair2 = new ModelRenderer(this, 48, 46);
        this.torAntSmallHair2.setRotationPoint(-2.0F, -4.0F, 15.0F);
        this.torAntSmallHair2.addBox(-3.0F, -3.0F, 0.0F, 6.0F, 3.0F, 1.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(torAntSmallHair2, -0.5473352640780661F, -0.19547687289441354F, 0.0F);
        this.torAntHead = new ModelRenderer(this, 0, 0);
        this.torAntHead.setRotationPoint(0.0F, 2.5F, -12.5F);
        this.torAntHead.addBox(-4.0F, -3.0F, -10.0F, 8.0F, 5.0F, 10.0F, 0.0F, 0.0F, 0.0F);
        this.torAntFrontLeftLegBottom = new ModelRenderer(this, 44, 13);
        this.torAntFrontLeftLegBottom.setRotationPoint(0.25F, 13.0F, 0.0F);
        this.torAntFrontLeftLegBottom.addBox(-0.5F, -4.0F, -0.5F, 1.0F, 14.0F, 1.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(torAntFrontLeftLegBottom, -0.41887902047863906F, 0.0F, 0.13962634015954636F);
        this.torAntBackLeftLeg = new ModelRenderer(this, 36, 11);
        this.torAntBackLeftLeg.mirror = true;
        this.torAntBackLeftLeg.setRotationPoint(8.0F, -0.5F, 8.0F);
        this.torAntBackLeftLeg.addBox(-1.0F, -2.0F, -1.0F, 2.0F, 12.0F, 2.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(torAntBackLeftLeg, 0.20943951023931953F, 0.0F, -0.10471975511965977F);
        this.torAntSmallHair5 = new ModelRenderer(this, 48, 46);
        this.torAntSmallHair5.setRotationPoint(0.0F, -3.0F, -7.0F);
        this.torAntSmallHair5.addBox(-3.0F, -3.0F, 0.0F, 6.0F, 3.0F, 1.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(torAntSmallHair5, -1.1728612040769677F, 0.0F, 0.0F);
        this.torAntThorax = new ModelRenderer(this, 0, 15);
        this.torAntThorax.setRotationPoint(0.0F, 0.0F, -7.0F);
        this.torAntThorax.addBox(-5.5F, -3.0F, -6.5F, 11.0F, 6.0F, 13.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(torAntThorax, 0.2617993877991494F, 0.0F, 0.0F);
        this.torAntHair2 = new ModelRenderer(this, 0, 46);
        this.torAntHair2.setRotationPoint(0.0F, -4.0F, 10.5F);
        this.torAntHair2.addBox(-4.5F, -3.0F, 0.5F, 9.0F, 3.0F, 1.0F, 0.0F, 0.0F, 0.1F);
        this.setRotateAngle(torAntHair2, -0.6255260065779288F, 0.19547687289441354F, 0.0F);
        this.torAntSmallHair4 = new ModelRenderer(this, 48, 42);
        this.torAntSmallHair4.setRotationPoint(0.0F, -3.0F, -4.0F);
        this.torAntSmallHair4.addBox(-3.0F, -3.0F, 0.0F, 6.0F, 3.0F, 1.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(torAntSmallHair4, -0.9382889765773795F, -0.03909537541112055F, 0.0F);
        this.torAntAbdomen.addChild(this.torAntHair1);
        this.torAntBackRightLeg.addChild(this.torAntBackRightLegBottom);
        this.torAntThorax.addChild(this.torAntAbdomen);
        this.torAntHead.addChild(this.torAntJaw);
        this.torAntAbdomen.addChild(this.torAntSmallHair1);
        this.torAntAbdomen.addChild(this.torAntHair3);
        this.torAntBackLeftLeg.addChild(this.torAntBackLeftLegBottom);
        this.torAntThorax.addChild(this.torAntSmallHair3);
        this.torAntThorax.addChild(this.torAntHair4);
        this.torAntFrontRightLeg.addChild(this.torAntFrontRightLegBottom);
        this.torAntHead.addChild(this.torAntBeard);
        this.torAntThorax.addChild(this.torAntHair5);
        this.torAntAbdomen.addChild(this.torAntSmallHair2);
        this.torAntFrontLeftLeg.addChild(this.torAntFrontLeftLegBottom);
        this.torAntHead.addChild(this.torAntSmallHair5);
        this.torAntAbdomen.addChild(this.torAntHair2);
        this.torAntHead.addChild(this.torAntSmallHair4);
    }

    @Override
    public void render(MatrixStack matrixStackIn, IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) { 
        ImmutableList.of(this.torAntBackRightLeg, this.torAntFrontRightLeg, this.torAntFrontLeftLeg, this.torAntSaddle, this.torAntHead, this.torAntBackLeftLeg, this.torAntThorax).forEach((modelRenderer) -> { 
            modelRenderer.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
        });
    }

    @Override
    public void setRotationAngles(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch)
    {
    	float f = 0.01F * (float)(entityIn.getEntityId() % 10);
    	
        this.torAntHead.rotateAngleX = headPitch * ((float)Math.PI / 180F);
        this.torAntHead.rotateAngleY = netHeadYaw * ((float)Math.PI / 180F);
        
        this.torAntBackRightLeg.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
        this.torAntBackLeftLeg.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F + (float)Math.PI) * 1.4F * limbSwingAmount;
        this.torAntBackRightLegBottom.rotateAngleX = (-1) * (1 - this.torAntBackRightLeg.rotateAngleX) - 0.20943951023931953F;
        this.torAntBackLeftLegBottom.rotateAngleX = (-1) * (1 - this.torAntBackLeftLeg.rotateAngleX) - 0.20943951023931953F;
        
        this.torAntFrontRightLeg.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F + (float)Math.PI) * 1.4F * limbSwingAmount;
        this.torAntFrontLeftLeg.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
        this.torAntFrontRightLegBottom.rotateAngleX = (1 - this.torAntFrontRightLeg.rotateAngleX) - 0.41887902047863906F;
        this.torAntFrontLeftLegBottom.rotateAngleX = (1 - this.torAntFrontLeftLeg.rotateAngleX) - 0.41887902047863906F;
        
        this.torAntJaw.rotateAngleY = MathHelper.cos((float)entityIn.ticksExisted * f) * 2.5F * ((float)Math.PI / 180F);
        
        this.torAntAbdomen.rotateAngleY = MathHelper.cos((float)ageInTicks * f) * 2.5F * ((float)Math.PI / 180F);
    }

    /**
     * This is a helper function from Tabula to set the rotation of model parts
     */
    public void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }
}
