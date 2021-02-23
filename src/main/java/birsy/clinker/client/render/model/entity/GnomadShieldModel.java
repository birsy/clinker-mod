package birsy.clinker.client.render.model.entity;

import birsy.clinker.client.render.model.BirsyModelRenderer;
import birsy.clinker.common.entity.monster.GnomadAxemanEntity;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.util.math.MathHelper;
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
        super(RenderType::getEntityTranslucent);
        
        this.textureWidth = 32;
        this.textureHeight = 64;

        this.rootPart = new BirsyModelRenderer(this, 0, 0);
        this.rootPart.setRotationPoint(0.0F, 12.0F, 0.0F);
        this.rootPart.addBox(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);

        this.shieldFront = new BirsyModelRenderer(this, 0, 0);
        this.shieldFront.setRotationPoint(0.0F, 0.0F, -1 * distance);
        this.shieldFront.addBox(-6.0F, -11.0F, -1.0F, 12.0F, 22.0F, 1.0F, 0.0F, 0.0F, 0.0F);

        this.shieldBack = new BirsyModelRenderer(this, 0, 0);
        this.shieldBack.setRotationPoint(0.0F, 0.0F, distance);
        this.shieldBack.addBox(-6.0F, -11.0F, 0.0F, 12.0F, 22.0F, 1.0F, 0.0F, 0.0F, 0.0F);
        this.shieldBack.mirror = true;

        this.shieldLeft = new BirsyModelRenderer(this, 0, 23);
        this.shieldLeft.setRotationPoint(-1 * distance, 0.0F, 0.0F);
        this.shieldLeft.addBox(-1.0F, -11.0F, -6.0F, 1.0F, 22.0F, 12.0F, 0.0F, 0.0F, 0.0F);

        this.shieldRight = new BirsyModelRenderer(this, 0, 23);
        this.shieldRight.setRotationPoint(distance, 0.0F, 0.0F);
        this.shieldRight.addBox(0.0F, -11.0F, -6.0F, 1.0F, 22.0F, 12.0F, 0.0F, 0.0F, 0.0F);
        this.shieldRight.mirror = true;

        this.rootPart.addChild(this.shieldFront);
        this.rootPart.addChild(this.shieldBack);
        this.rootPart.addChild(this.shieldLeft);
        this.rootPart.addChild(this.shieldRight);
    }

    @Override
    public void render(MatrixStack matrixStackIn, IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
        this.rootPart.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
    }

    public void setRotation(float rotation, float distanceFromCenter, float ticksExisted, int shieldNumber) {
        this.rootPart.rotateAngleY = rotation;
        this.rootPart.rotationPointY =+ (MathHelper.sin(ticksExisted * 0.13F) * 2) + 5;

        this.shieldFront.rotationPointZ = -1 * distanceFromCenter;
        this.shieldBack.rotationPointZ  = distanceFromCenter;
        this.shieldLeft.rotationPointX  = -1 * distanceFromCenter;
        this.shieldRight.rotationPointX = distanceFromCenter;

        shieldFront.showModel = false;
        shieldBack.showModel  = false;
        shieldLeft.showModel  = false;
        shieldRight.showModel = false;

        if (shieldNumber > 0) {
            shieldRight.showModel = true;
        }

        if (shieldNumber > 1) {
            shieldLeft.showModel  = true;
        }

        if (shieldNumber > 2) {
            shieldBack.showModel  = true;
        }

        if (shieldNumber > 3) {
            shieldFront.showModel = true;
        }
    }
    
    public void resetRotation() {
        this.rootPart.rotateAngleX = this.rootPart.defaultRotateAngleX;
        this.rootPart.rotateAngleY = this.rootPart.defaultRotateAngleY;
        this.rootPart.rotateAngleZ = this.rootPart.defaultRotateAngleZ;

        this.rootPart.rotationPointX = this.rootPart.defaultRotationPointX;
        this.rootPart.rotationPointY = this.rootPart.defaultRotationPointY;
        this.rootPart.rotationPointZ = this.rootPart.defaultRotationPointZ;

        this.shieldFront.rotateAngleX = this.shieldFront.defaultRotateAngleX;
        this.shieldFront.rotateAngleY = this.shieldFront.defaultRotateAngleY;
        this.shieldFront.rotateAngleZ = this.shieldFront.defaultRotateAngleZ;

        this.shieldFront.rotationPointX = this.shieldFront.defaultRotationPointX;
        this.shieldFront.rotationPointY = this.shieldFront.defaultRotationPointY;
        this.shieldFront.rotationPointZ = this.shieldFront.defaultRotationPointZ;

        this.shieldBack.rotateAngleX = this.shieldBack.defaultRotateAngleX;
        this.shieldBack.rotateAngleY = this.shieldBack.defaultRotateAngleY;
        this.shieldBack.rotateAngleZ = this.shieldBack.defaultRotateAngleZ;

        this.shieldBack.rotationPointX = this.shieldBack.defaultRotationPointX;
        this.shieldBack.rotationPointY = this.shieldBack.defaultRotationPointY;
        this.shieldBack.rotationPointZ = this.shieldBack.defaultRotationPointZ;

        this.shieldLeft.rotateAngleX = this.shieldLeft.defaultRotateAngleX;
        this.shieldLeft.rotateAngleY = this.shieldLeft.defaultRotateAngleY;
        this.shieldLeft.rotateAngleZ = this.shieldLeft.defaultRotateAngleZ;

        this.shieldLeft.rotationPointX = this.shieldLeft.defaultRotationPointX;
        this.shieldLeft.rotationPointY = this.shieldLeft.defaultRotationPointY;
        this.shieldLeft.rotationPointZ = this.shieldLeft.defaultRotationPointZ;

        this.shieldRight.rotateAngleX = this.shieldRight.defaultRotateAngleX;
        this.shieldRight.rotateAngleY = this.shieldRight.defaultRotateAngleY;
        this.shieldRight.rotateAngleZ = this.shieldRight.defaultRotateAngleZ;

        this.shieldRight.rotationPointX = this.shieldRight.defaultRotationPointX;
        this.shieldRight.rotationPointY = this.shieldRight.defaultRotationPointY;
        this.shieldRight.rotationPointZ = this.shieldRight.defaultRotationPointZ;
    }
}
