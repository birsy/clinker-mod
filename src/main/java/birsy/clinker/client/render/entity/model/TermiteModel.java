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
 * termite - birsy
 * Created using Tabula 8.0.0
 */
@OnlyIn(Dist.CLIENT)
public class TermiteModel<T extends Entity> extends EntityModel<T> {
    public ModelPart termiteHead;
    public ModelPart termiteNeck;
    public ModelPart termiteBody;
    public ModelPart termiteLeg1;
    public ModelPart termiteLeg1_1;
    public ModelPart termiteLeg1_2;
    public ModelPart termiteLeg1_3;
    public ModelPart termiteLeg1_4;
    public ModelPart termiteLeg1_5;
    public ModelPart termiteAntennae;
    public ModelPart termiteRightJaw;
    public ModelPart termiteLeftJaw;

    public TermiteModel() {
        this.texWidth = 32;
        this.texHeight = 32;
        this.termiteRightJaw = new ModelPart(this, 0, 7);
        this.termiteRightJaw.setPos(-1.0F, 0.9F, -4.0F);
        this.termiteRightJaw.addBox(-0.5F, -0.5F, -2.0F, 1.0F, 1.0F, 3.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(termiteRightJaw, 0.0F, 0.2617993877991494F, 0.0F);
        this.termiteLeg1 = new ModelPart(this, 12, 0);
        this.termiteLeg1.setPos(2.0F, 21.0F, -2.0F);
        this.termiteLeg1.addBox(0.0F, -0.5F, -0.5F, 6.0F, 1.0F, 1.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(termiteLeg1, 0.0F, 0.7853981633974483F, 0.7853981633974483F);
        this.termiteLeg1_3 = new ModelPart(this, 16, 6);
        this.termiteLeg1_3.setPos(-2.0F, 21.0F, 0.0F);
        this.termiteLeg1_3.addBox(-6.0F, -0.5F, -0.5F, 6.0F, 1.0F, 1.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(termiteLeg1_3, 0.0F, 0.0F, -0.5811946275983063F);
        this.termiteNeck = new ModelPart(this, 0, 7);
        this.termiteNeck.setPos(0.0F, 22.0F, 1.0F);
        this.termiteNeck.addBox(-2.0F, -2.0F, -4.0F, 4.0F, 3.0F, 8.0F, 0.0F, 0.0F, 0.0F);
        this.termiteLeg1_1 = new ModelPart(this, 12, 2);
        this.termiteLeg1_1.setPos(2.0F, 21.0F, 2.0F);
        this.termiteLeg1_1.addBox(0.0F, -0.5F, -0.5F, 6.0F, 1.0F, 1.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(termiteLeg1_1, 0.0F, -0.7853981633974483F, 0.7853981633974483F);
        this.termiteLeg1_2 = new ModelPart(this, 16, 4);
        this.termiteLeg1_2.setPos(-2.0F, 21.0F, -2.0F);
        this.termiteLeg1_2.addBox(-6.0F, -0.5F, -0.5F, 6.0F, 1.0F, 1.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(termiteLeg1_2, 0.0F, -0.7853981633974483F, -0.7853981633974483F);
        this.termiteLeg1_5 = new ModelPart(this, 16, 10);
        this.termiteLeg1_5.setPos(2.0F, 21.0F, 0.0F);
        this.termiteLeg1_5.addBox(0.0F, -0.5F, -0.5F, 6.0F, 1.0F, 1.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(termiteLeg1_5, 0.0F, 0.0F, 0.5811946275983063F);
        this.termiteBody = new ModelPart(this, 0, 18);
        this.termiteBody.setPos(0.0F, 21.5F, 3.5F);
        this.termiteBody.addBox(-2.5F, -2.5F, 0.0F, 5.0F, 5.0F, 5.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(termiteBody, 0.7853981633974483F, 0.0F, 0.0F);
        this.termiteAntennae = new ModelPart(this, 15, 18);
        this.termiteAntennae.setPos(0.0F, -1.0F, -2.0F);
        this.termiteAntennae.addBox(-2.0F, -3.0F, 0.0F, 4.0F, 3.0F, 1.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(termiteAntennae, 0.39269908169872414F, 0.0F, 0.0F);
        this.termiteHead = new ModelPart(this, 0, 0);
        this.termiteHead.setPos(0.0F, 22.0F, -3.0F);
        this.termiteHead.addBox(-2.0F, -1.5F, -4.0F, 4.0F, 3.0F, 4.0F, 0.0F, 0.0F, 0.0F);
        this.termiteLeftJaw = new ModelPart(this, 0, 11);
        this.termiteLeftJaw.setPos(1.0F, 0.9F, -4.0F);
        this.termiteLeftJaw.addBox(-0.5F, -0.5F, -2.0F, 1.0F, 1.0F, 3.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(termiteLeftJaw, 0.0F, -0.2617993877991494F, 0.0F);
        this.termiteLeg1_4 = new ModelPart(this, 16, 8);
        this.termiteLeg1_4.setPos(-2.0F, 21.0F, 2.0F);
        this.termiteLeg1_4.addBox(-6.0F, -0.5F, -0.5F, 6.0F, 1.0F, 1.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(termiteLeg1_4, 0.0F, 0.7853981633974483F, -0.7853981633974483F);
        this.termiteHead.addChild(this.termiteRightJaw);
        this.termiteHead.addChild(this.termiteAntennae);
        this.termiteHead.addChild(this.termiteLeftJaw);
    }

    @Override
    public void renderToBuffer(PoseStack matrixStackIn, VertexConsumer bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) { 
        ImmutableList.of(this.termiteLeg1, this.termiteLeg1_3, this.termiteNeck, this.termiteLeg1_1, this.termiteLeg1_2, this.termiteLeg1_5, this.termiteBody, this.termiteHead, this.termiteLeg1_4).forEach((modelRenderer) -> { 
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
