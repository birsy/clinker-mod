package birsy.clinker.client.render.entity.model;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import birsy.clinker.common.entity.passive.SnailEntity;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * snail - birsy
 * Created using Tabula 8.0.0
 */
@OnlyIn(Dist.CLIENT)
public class SnailModel<T extends SnailEntity> extends EntityModel<T>
{
    public ModelPart snailShell;
    public ModelPart snailHead;
    public ModelPart snailRightEye;
    public ModelPart snailLeftEye;

    public SnailModel() {
        this.texWidth = 64;
        this.texHeight = 32;
        this.snailShell = new ModelPart(this, 0, 0);
        this.snailShell.setPos(0.0F, 24.4F, 2.0F);
        this.snailShell.addBox(-3.0F, -11.0F, -6.0F, 6.0F, 11.0F, 12.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(snailShell, -0.08464846705724931F, 0.0F, 0.0F);
        this.snailRightEye = new ModelPart(this, 0, 0);
        this.snailRightEye.setPos(1.5F, -2.0F, -3.0F);
        this.snailRightEye.addBox(-0.5F, -4.0F, -0.5F, 1.0F, 4.0F, 1.0F, 0.0F, 0.0F, 0.0F);
        this.snailLeftEye = new ModelPart(this, 4, 0);
        this.snailLeftEye.setPos(-1.5F, -2.0F, -3.0F);
        this.snailLeftEye.addBox(-0.5F, -4.0F, -0.5F, 1.0F, 4.0F, 1.0F, 0.0F, 0.0F, 0.0F);
        this.snailHead = new ModelPart(this, 24, 0);
        this.snailHead.setPos(0.0F, 24.0F, -5.0F);
        this.snailHead.addBox(-2.5F, -2.0F, -4.0F, 5.0F, 2.0F, 6.0F, 0.0F, 0.0F, 0.0F);
        this.snailHead.addChild(this.snailRightEye);
        this.snailHead.addChild(this.snailLeftEye);
    }

    @Override
    public void renderToBuffer(PoseStack matrixStackIn, VertexConsumer bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) { 
        ImmutableList.of(this.snailShell, this.snailHead).forEach((modelRenderer) -> { 
            modelRenderer.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
        });
    }

    @Override
    public void setupAnim(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch)
    {
    	float f = 0.01F * (float)(entityIn.getId() % 10);
    	
    	this.snailHead.yRot = (netHeadYaw * ((float)Math.PI / 180F)) / 2;
    	
        this.snailLeftEye.yRot = (netHeadYaw * ((float)Math.PI / 180F)) / 2;
        this.snailLeftEye.xRot = headPitch * ((float)Math.PI / 180F);
        this.snailLeftEye.zRot = Mth.cos((float)entityIn.tickCount * f) * 2.5F * ((float)Math.PI / 180F);
        
        this.snailRightEye.yRot = (netHeadYaw * ((float)Math.PI / 180F)) / 2;
        this.snailRightEye.xRot = headPitch * ((float)Math.PI / 180F);
        this.snailRightEye.zRot = Mth.cos((float)entityIn.tickCount * f) * 2.5F * ((float)Math.PI / 180F);
    }

    /**
     * This is a helper function from Tabula to set the rotation of model parts
     */
    public void setRotateAngle(ModelPart modelRenderer, float x, float y, float z) {
        modelRenderer.xRot = x;
        modelRenderer.yRot = y;
        modelRenderer.zRot = z;
    }
}
