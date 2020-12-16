package birsy.clinker.client.render.model.entity;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.entity.model.IHasArm;
import net.minecraft.client.renderer.entity.model.IHasHead;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.HandSide;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * GnomeBrat - birsy
 * Created using Tabula 8.0.0
 */
@OnlyIn(Dist.CLIENT)
public class GnomeBratModel<T extends Entity> extends EntityModel<T> implements IHasArm, IHasHead
{
    public ModelRenderer bratLeftLeg;
    public ModelRenderer bratRightLeg;
    public ModelRenderer bratBelly;
    public ModelRenderer bratChest;
    public ModelRenderer bratHead;
    public ModelRenderer bratRightArm;
    public ModelRenderer bratLeftArm;
    public ModelRenderer bratFace;
    public ModelRenderer bratHatBottom;
    public ModelRenderer bratNose;
    public ModelRenderer bratHatTop;

    public GnomeBratModel() {
        this.textureWidth = 64;
        this.textureHeight = 32;
        this.bratNose = new ModelRenderer(this, 8, 0);
        this.bratNose.setRotationPoint(0.0F, -1.0F, -0.5F);
        this.bratNose.addBox(-0.5F, 0.0F, -1.0F, 1.0F, 2.0F, 1.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(bratNose, -0.19547687289441354F, 0.0F, 0.0F);
        this.bratLeftLeg = new ModelRenderer(this, 0, 0);
        this.bratLeftLeg.setRotationPoint(3.0F, 17.0F, 0.0F);
        this.bratLeftLeg.addBox(0.0F, 0.0F, -0.5F, 1.0F, 7.0F, 1.0F, 0.0F, 0.0F, 0.0F);
        this.bratChest = new ModelRenderer(this, 32, 0);
        this.bratChest.setRotationPoint(0.0F, 14.5F, 0.0F);
        this.bratChest.addBox(-3.0F, -4.0F, -2.0F, 6.0F, 5.0F, 4.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(bratChest, 0.15358897750445236F, 0.0F, 0.0F);
        this.bratFace = new ModelRenderer(this, 22, 9);
        this.bratFace.setRotationPoint(0.0F, -2.5F, -2.5F);
        this.bratFace.addBox(-3.0F, -2.5F, -1.0F, 6.0F, 7.0F, 1.0F, -0.5F, -0.7F, 0.0F);
        this.bratBelly = new ModelRenderer(this, 8, 0);
        this.bratBelly.setRotationPoint(0.0F, 14.5F, 0.0F);
        this.bratBelly.addBox(-3.5F, 0.0F, -2.5F, 7.0F, 4.0F, 5.0F, 0.0F, 0.0F, 0.0F);
        this.bratHead = new ModelRenderer(this, 0, 9);
        this.bratHead.setRotationPoint(0.0F, 10.4F, -1.3F);
        this.bratHead.addBox(-3.0F, -5.0F, -3.0F, 6.0F, 6.0F, 5.0F, -0.75F, -0.75F, -0.5F);
        this.bratRightArm = new ModelRenderer(this, 52, 0);
        this.bratRightArm.setRotationPoint(-3.5F, -3.0F, 0.0F);
        this.bratRightArm.addBox(-0.5F, -0.5F, -0.5F, 1.0F, 6.0F, 1.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(bratRightArm, -0.3490658503988659F, 0.0F, 0.4363323129985824F);
        this.bratLeftArm = new ModelRenderer(this, 56, 0);
        this.bratLeftArm.setRotationPoint(3.5F, -3.0F, 0.0F);
        this.bratLeftArm.addBox(-0.5F, -0.5F, -0.5F, 1.0F, 6.0F, 1.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(bratLeftArm, -0.3490658503988659F, 0.0F, -0.4363323129985824F);
        this.bratRightLeg = new ModelRenderer(this, 4, 0);
        this.bratRightLeg.setRotationPoint(-3.0F, 17.0F, 0.0F);
        this.bratRightLeg.addBox(-1.0F, 0.0F, -0.5F, 1.0F, 7.0F, 1.0F, 0.0F, 0.0F, 0.0F);
        this.bratHatBottom = new ModelRenderer(this, 36, 9);
        this.bratHatBottom.setRotationPoint(0.0F, -4.2F, -0.7F);
        this.bratHatBottom.addBox(-2.0F, -1.0F, -2.0F, 4.0F, 1.0F, 4.0F, 0.0F, 0.0F, 0.0F);
        this.bratHatTop = new ModelRenderer(this, 49, 7);
        this.bratHatTop.setRotationPoint(0.0F, -0.5F, 0.0F);
        this.bratHatTop.addBox(-1.5F, -2.0F, -1.5F, 3.0F, 2.0F, 3.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(bratHatTop, -0.27366763203903305F, 0.0F, 0.1563815016444822F);
        this.bratFace.addChild(this.bratNose);
        this.bratHead.addChild(this.bratFace);
        this.bratChest.addChild(this.bratRightArm);
        this.bratChest.addChild(this.bratLeftArm);
        this.bratHead.addChild(this.bratHatBottom);
        this.bratHatBottom.addChild(this.bratHatTop);
    }
    
    protected Iterable<ModelRenderer> getHeadParts() {
    	return ImmutableList.of(this.bratHead, this.bratFace);
    }

    @Override
    public void render(MatrixStack matrixStackIn, IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) { 
        ImmutableList.of(this.bratLeftLeg, this.bratChest, this.bratBelly, this.bratHead, this.bratRightLeg).forEach((modelRenderer) -> { 
            modelRenderer.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
        });
    }

    @Override
    public void setRotationAngles(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch)
    {
    	float f = 0.01F * (float)(entityIn.getEntityId() % 10);
    	
    	this.bratHead.rotateAngleY = netHeadYaw * ((float)Math.PI / 180F);
        this.bratHead.rotateAngleX = headPitch * ((float)Math.PI / 180F);
        
        this.bratHatBottom.rotateAngleX = MathHelper.sin((float)entityIn.ticksExisted * f) * 4.5F * ((float)Math.PI / 180F);
        this.bratHatBottom.rotateAngleY = 0.0F;
        this.bratHatBottom.rotateAngleZ = MathHelper.cos((float)entityIn.ticksExisted * f) * 2.5F * ((float)Math.PI / 180F);
        
        this.bratRightLeg.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount * 0.5F;
        this.bratLeftLeg.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F + (float)Math.PI) * 1.4F * limbSwingAmount * 0.5F;
        this.bratRightLeg.rotateAngleY = 0.0F;
        this.bratLeftLeg.rotateAngleY = 0.0F;
        
    	this.bratRightArm.rotateAngleX = MathHelper.cos(limbSwing * 0.5662F + (float)Math.PI) * 1.4F * limbSwingAmount;
    	this.bratLeftArm.rotateAngleX = MathHelper.cos(limbSwing * 0.5662F) * 1.4F * limbSwingAmount;
    	
    	bratRightArm.rotateAngleZ += MathHelper.cos(ageInTicks * 0.09F) * 0.05F + 0.05F;
    	bratLeftArm.rotateAngleZ -= MathHelper.cos(ageInTicks * 0.09F) * 0.05F + 0.05F;
        bratRightArm.rotateAngleX += MathHelper.sin(ageInTicks * 0.067F) * 0.05F;
        bratLeftArm.rotateAngleX -= MathHelper.sin(ageInTicks * 0.067F) * 0.05F;
    }

    /**
     * This is a helper function from Tabula to set the rotation of model parts
     */
    public void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }

	@Override
	public ModelRenderer getModelHead() {
		return this.bratHead;
	}

	@Override
	public void translateHand(HandSide sideIn, MatrixStack matrixStackIn) {
		this.getArmForSide(sideIn).translateRotate(matrixStackIn);
	}
	
	protected ModelRenderer getArmForSide(HandSide side)
	{
		return side == HandSide.LEFT ? this.bratLeftArm : this.bratRightArm;
	}
}
