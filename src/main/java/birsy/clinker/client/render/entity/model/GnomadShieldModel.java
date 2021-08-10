package birsy.clinker.client.render.entity.model;

import birsy.clinker.client.render.util.BirsyModelRenderer;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.model.Model;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GnomadShieldModel extends Model {

    public BirsyModelRenderer rootPart;
	public BirsyModelRenderer shieldFront;
    public BirsyModelRenderer shieldBack;
    public BirsyModelRenderer shieldLeft;
    public BirsyModelRenderer shieldRight;

    public GnomadShieldModel(float distance) {
        super(RenderType::entityTranslucent);
        
        this.texWidth = 32;
        this.texHeight = 64;

        this.rootPart = new BirsyModelRenderer(this, 0, 0);
        this.rootPart.setPos(0.0F, 12.0F, 0.0F);
        this.rootPart.addBox(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);

        this.shieldFront = new BirsyModelRenderer(this, 0, 0);
        this.shieldFront.setPos(0.0F, 0.0F, -1 * distance);
        this.shieldFront.addBox(-6.0F, -11.0F, -1.0F, 12.0F, 22.0F, 1.0F, 0.0F, 0.0F, 0.0F);

        this.shieldBack = new BirsyModelRenderer(this, 0, 0);
        this.shieldBack.setPos(0.0F, 0.0F, distance);
        this.shieldBack.addBox(-6.0F, -11.0F, 0.0F, 12.0F, 22.0F, 1.0F, 0.0F, 0.0F, 0.0F);
        this.shieldBack.mirror = true;

        this.shieldLeft = new BirsyModelRenderer(this, 0, 23);
        this.shieldLeft.setPos(-1 * distance, 0.0F, 0.0F);
        this.shieldLeft.addBox(-1.0F, -11.0F, -6.0F, 1.0F, 22.0F, 12.0F, 0.0F, 0.0F, 0.0F);

        this.shieldRight = new BirsyModelRenderer(this, 0, 23);
        this.shieldRight.setPos(distance, 0.0F, 0.0F);
        this.shieldRight.addBox(0.0F, -11.0F, -6.0F, 1.0F, 22.0F, 12.0F, 0.0F, 0.0F, 0.0F);
        this.shieldRight.mirror = true;

        this.rootPart.addChild(this.shieldFront);
        this.rootPart.addChild(this.shieldBack);
        this.rootPart.addChild(this.shieldLeft);
        this.rootPart.addChild(this.shieldRight);
    }

    @Override
    public void renderToBuffer(PoseStack matrixStackIn, VertexConsumer bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
        this.rootPart.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
    }

    public void setRotation(float rotation, float distanceFromCenter, float ticksExisted, int shieldNumber) {
        this.rootPart.yRot = rotation;
        this.rootPart.y =+ (Mth.sin(ticksExisted * 0.13F) * 2) + 5;

        this.shieldFront.z = -1 * distanceFromCenter;
        this.shieldBack.z  = distanceFromCenter;
        this.shieldLeft.x  = -1 * distanceFromCenter;
        this.shieldRight.x = distanceFromCenter;

        shieldFront.visible = false;
        shieldBack.visible  = false;
        shieldLeft.visible  = false;
        shieldRight.visible = false;

        if (shieldNumber > 0) {
            shieldRight.visible = true;
        }

        if (shieldNumber > 1) {
            shieldLeft.visible  = true;
        }

        if (shieldNumber > 2) {
            shieldBack.visible  = true;
        }

        if (shieldNumber > 3) {
            shieldFront.visible = true;
        }
    }
    
    public void resetRotation() {
        this.rootPart.xRot = this.rootPart.defaultRotateAngleX;
        this.rootPart.yRot = this.rootPart.defaultRotateAngleY;
        this.rootPart.zRot = this.rootPart.defaultRotateAngleZ;

        this.rootPart.x = this.rootPart.defaultRotationPointX;
        this.rootPart.y = this.rootPart.defaultRotationPointY;
        this.rootPart.z = this.rootPart.defaultRotationPointZ;

        this.shieldFront.xRot = this.shieldFront.defaultRotateAngleX;
        this.shieldFront.yRot = this.shieldFront.defaultRotateAngleY;
        this.shieldFront.zRot = this.shieldFront.defaultRotateAngleZ;

        this.shieldFront.x = this.shieldFront.defaultRotationPointX;
        this.shieldFront.y = this.shieldFront.defaultRotationPointY;
        this.shieldFront.z = this.shieldFront.defaultRotationPointZ;

        this.shieldBack.xRot = this.shieldBack.defaultRotateAngleX;
        this.shieldBack.yRot = this.shieldBack.defaultRotateAngleY;
        this.shieldBack.zRot = this.shieldBack.defaultRotateAngleZ;

        this.shieldBack.x = this.shieldBack.defaultRotationPointX;
        this.shieldBack.y = this.shieldBack.defaultRotationPointY;
        this.shieldBack.z = this.shieldBack.defaultRotationPointZ;

        this.shieldLeft.xRot = this.shieldLeft.defaultRotateAngleX;
        this.shieldLeft.yRot = this.shieldLeft.defaultRotateAngleY;
        this.shieldLeft.zRot = this.shieldLeft.defaultRotateAngleZ;

        this.shieldLeft.x = this.shieldLeft.defaultRotationPointX;
        this.shieldLeft.y = this.shieldLeft.defaultRotationPointY;
        this.shieldLeft.z = this.shieldLeft.defaultRotationPointZ;

        this.shieldRight.xRot = this.shieldRight.defaultRotateAngleX;
        this.shieldRight.yRot = this.shieldRight.defaultRotateAngleY;
        this.shieldRight.zRot = this.shieldRight.defaultRotateAngleZ;

        this.shieldRight.x = this.shieldRight.defaultRotationPointX;
        this.shieldRight.y = this.shieldRight.defaultRotationPointY;
        this.shieldRight.z = this.shieldRight.defaultRotationPointZ;
    }
}
