package birsy.clinker.client.render.model.entity;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import birsy.clinker.common.entity.passive.SnailEntity;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * snail - birsy
 * Created using Tabula 8.0.0
 */
@OnlyIn(Dist.CLIENT)
public class SnailModel<T extends SnailEntity> extends EntityModel<T>
{
    public ModelRenderer snailShell;
    public ModelRenderer snailHead;
    public ModelRenderer snailRightEye;
    public ModelRenderer snailLeftEye;

    public SnailModel() {
        this.textureWidth = 64;
        this.textureHeight = 32;
        this.snailShell = new ModelRenderer(this, 0, 0);
        this.snailShell.setRotationPoint(0.0F, 24.4F, 2.0F);
        this.snailShell.addBox(-3.0F, -11.0F, -6.0F, 6.0F, 11.0F, 12.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(snailShell, -0.08464846705724931F, 0.0F, 0.0F);
        this.snailRightEye = new ModelRenderer(this, 0, 0);
        this.snailRightEye.setRotationPoint(1.5F, -2.0F, -3.0F);
        this.snailRightEye.addBox(-0.5F, -4.0F, -0.5F, 1.0F, 4.0F, 1.0F, 0.0F, 0.0F, 0.0F);
        this.snailLeftEye = new ModelRenderer(this, 4, 0);
        this.snailLeftEye.setRotationPoint(-1.5F, -2.0F, -3.0F);
        this.snailLeftEye.addBox(-0.5F, -4.0F, -0.5F, 1.0F, 4.0F, 1.0F, 0.0F, 0.0F, 0.0F);
        this.snailHead = new ModelRenderer(this, 24, 0);
        this.snailHead.setRotationPoint(0.0F, 24.0F, -5.0F);
        this.snailHead.addBox(-2.5F, -2.0F, -4.0F, 5.0F, 2.0F, 6.0F, 0.0F, 0.0F, 0.0F);
        this.snailHead.addChild(this.snailRightEye);
        this.snailHead.addChild(this.snailLeftEye);
    }

    @Override
    public void render(MatrixStack matrixStackIn, IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) { 
        ImmutableList.of(this.snailShell, this.snailHead).forEach((modelRenderer) -> { 
            modelRenderer.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
        });
    }

    @Override
    public void setRotationAngles(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch)
    {
    	float f = 0.01F * (float)(entityIn.getEntityId() % 10);
    	
    	this.snailHead.rotateAngleY = (netHeadYaw * ((float)Math.PI / 180F)) / 2;
    	
        this.snailLeftEye.rotateAngleY = (netHeadYaw * ((float)Math.PI / 180F)) / 2;
        this.snailLeftEye.rotateAngleX = headPitch * ((float)Math.PI / 180F);
        this.snailLeftEye.rotateAngleZ = MathHelper.cos((float)entityIn.ticksExisted * f) * 2.5F * ((float)Math.PI / 180F);
        
        this.snailRightEye.rotateAngleY = (netHeadYaw * ((float)Math.PI / 180F)) / 2;
        this.snailRightEye.rotateAngleX = headPitch * ((float)Math.PI / 180F);
        this.snailRightEye.rotateAngleZ = MathHelper.cos((float)entityIn.ticksExisted * f) * 2.5F * ((float)Math.PI / 180F);
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
