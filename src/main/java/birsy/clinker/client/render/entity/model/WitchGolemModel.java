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
 * witchGolem - birsy
 * Created using Tabula 8.0.0
 */
@OnlyIn(Dist.CLIENT)
public class WitchGolemModel<T extends Entity> extends EntityModel<T> {
    public ModelPart witchGolemWaist;
    public ModelPart witchGolemLeftLeg;
    public ModelPart witchGolemRightLeg;
    public ModelPart witchGolemNeck;
    public ModelPart witchGolemRightArm;
    public ModelPart witchGolemLeftArm;
    public ModelPart witchGolemSpine;
    public ModelPart witchGolemTorso;
    public ModelPart witchGolemHead;
    public ModelPart witchGolemMask;
    public ModelPart witchGolemHair1;
    public ModelPart witchGolemHair2;
    public ModelPart witchGolemNose1;
    public ModelPart witchGolemNose2;

    public WitchGolemModel() {
        this.texWidth = 128;
        this.texHeight = 64;
        this.witchGolemNose1 = new ModelPart(this, 10, 0);
        this.witchGolemNose1.setPos(0.0F, -1.0F, -1.0F);
        this.witchGolemNose1.addBox(-1.0F, -1.0F, -1.0F, 2.0F, 2.0F, 1.0F, 0.0F, 0.0F, 0.0F);
        this.witchGolemRightArm = new ModelPart(this, 54, 0);
        this.witchGolemRightArm.setPos(-7.0F, -5.0F, 0.0F);
        this.witchGolemRightArm.addBox(-3.0F, -1.5F, -1.5F, 3.0F, 16.0F, 3.0F, 0.0F, 0.0F, 0.0F);
        this.witchGolemLeftArm = new ModelPart(this, 66, 0);
        this.witchGolemLeftArm.setPos(7.0F, -5.0F, 0.0F);
        this.witchGolemLeftArm.addBox(0.0F, -1.5F, -1.5F, 3.0F, 16.0F, 3.0F, 0.0F, 0.0F, 0.0F);
        this.witchGolemRightLeg = new ModelPart(this, 14, 0);
        this.witchGolemRightLeg.setPos(-5.0F, 12.0F, 0.0F);
        this.witchGolemRightLeg.addBox(-3.0F, -2.0F, -2.0F, 3.0F, 14.0F, 4.0F, 0.0F, 0.0F, 0.0F);
        this.witchGolemNeck = new ModelPart(this, 28, 0);
        this.witchGolemNeck.setPos(0.0F, -7.0F, 0.0F);
        this.witchGolemNeck.addBox(-4.0F, -7.0F, -2.5F, 8.0F, 7.0F, 5.0F, 0.0F, 0.0F, 0.0F);
        this.witchGolemWaist = new ModelPart(this, 0, 23);
        this.witchGolemWaist.setPos(0.0F, 9.0F, 0.0F);
        this.witchGolemWaist.addBox(-5.0F, 0.0F, -3.0F, 10.0F, 5.0F, 6.0F, 0.0F, 0.0F, 0.0F);
        this.witchGolemHair2 = new ModelPart(this, 68, 14);
        this.witchGolemHair2.setPos(-3.5F, -3.5F, -1.5F);
        this.witchGolemHair2.addBox(-5.0F, -0.5F, -3.0F, 10.0F, 1.0F, 10.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(witchGolemHair2, -0.18203784630933073F, -1.3203416057653212F, -0.8651597048872669F);
        this.witchGolemMask = new ModelPart(this, 106, 0);
        this.witchGolemMask.setPos(0.0F, -3.0F, -4.5F);
        this.witchGolemMask.addBox(-4.0F, -4.5F, -1.0F, 8.0F, 9.0F, 2.0F, 0.0F, 0.0F, 0.0F);
        this.witchGolemHead = new ModelPart(this, 78, 0);
        this.witchGolemHead.setPos(0.0F, -6.0F, 0.0F);
        this.witchGolemHead.addBox(-3.5F, -7.0F, -5.0F, 7.0F, 7.0F, 7.0F, 0.0F, 0.0F, 0.0F);
        this.witchGolemSpine = new ModelPart(this, 32, 23);
        this.witchGolemSpine.setPos(0.0F, 0.0F, 0.0F);
        this.witchGolemSpine.addBox(-4.0F, -9.0F, -2.5F, 8.0F, 9.0F, 5.0F, 0.0F, 0.0F, 0.0F);
        this.witchGolemNose2 = new ModelPart(this, 75, 0);
        this.witchGolemNose2.setPos(0.0F, 0.0F, 0.0F);
        this.witchGolemNose2.addBox(-2.0F, 1.0F, -1.0F, 4.0F, 1.0F, 1.0F, 0.0F, 0.0F, 0.0F);
        this.witchGolemTorso = new ModelPart(this, 58, 25);
        this.witchGolemTorso.setPos(0.0F, -9.0F, 0.0F);
        this.witchGolemTorso.addBox(-7.0F, -7.0F, -3.5F, 14.0F, 7.0F, 7.0F, 0.0F, 0.0F, 0.0F);
        this.witchGolemHair1 = new ModelPart(this, 18, 12);
        this.witchGolemHair1.setPos(3.5F, -3.5F, -1.5F);
        this.witchGolemHair1.addBox(-5.0F, -0.5F, -3.0F, 10.0F, 1.0F, 10.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(witchGolemHair1, 0.0F, 1.48352986419518F, 1.0471975511965976F);
        this.witchGolemLeftLeg = new ModelPart(this, 0, 0);
        this.witchGolemLeftLeg.setPos(5.0F, 12.0F, 0.0F);
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
    public void renderToBuffer(PoseStack matrixStackIn, VertexConsumer bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) { 
        ImmutableList.of(this.witchGolemRightArm, this.witchGolemLeftArm, this.witchGolemRightLeg, this.witchGolemNeck, this.witchGolemWaist, this.witchGolemLeftLeg).forEach((modelRenderer) -> { 
            modelRenderer.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
        });
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
