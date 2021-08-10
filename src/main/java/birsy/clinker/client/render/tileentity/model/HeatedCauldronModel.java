package birsy.clinker.client.render.tileentity.model;

import birsy.clinker.client.render.util.BirsyModelRenderer;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class HeatedCauldronModel extends Model {
    public BirsyModelRenderer cauldronBase;
    public BirsyModelRenderer cauldronSouthWall;
    public BirsyModelRenderer cauldronNorthWall;
    public BirsyModelRenderer cauldronWestWall;
    public BirsyModelRenderer cauldronEastWall;
    public BirsyModelRenderer cauldronFloor;
    public BirsyModelRenderer cauldronSouthWestLegA;
    public BirsyModelRenderer cauldronSouthEastLegA;
    public BirsyModelRenderer cauldronSouthWestLeg;
    public BirsyModelRenderer cauldronSouthEastLegB;
    public BirsyModelRenderer cauldronNorthEastLegA;
    public BirsyModelRenderer cauldronNorthWestLegA;
    public BirsyModelRenderer cauldronNorthEastLegB;
    public BirsyModelRenderer cauldronNorthWestLeg;

    public BirsyModelRenderer cauldronSpoonHandle;
    public BirsyModelRenderer cauldronSpoonBowl;

    public BirsyModelRenderer itemBase;
    public BirsyModelRenderer item0Base;
    public BirsyModelRenderer item1Base;
    public BirsyModelRenderer item2Base;
    public BirsyModelRenderer item3Base;
    public BirsyModelRenderer item0;
    public BirsyModelRenderer item1;
    public BirsyModelRenderer item2;
    public BirsyModelRenderer item3;

    public HeatedCauldronModel() {
        super(RenderType::getEntityCutout);

        this.textureWidth = 64;
        this.textureHeight = 64;
        this.cauldronNorthEastLegA = new BirsyModelRenderer(this, 13, 43);
        this.cauldronNorthEastLegA.setRotationPoint(8.0F, 0.0F, 8.0F);
        this.cauldronNorthEastLegA.addBox(-4.0F, 0.0F, -2.0F, 4.0F, 3.0F, 2.0F, 0.0F, 0.0F, 0.0F);
        this.cauldronEastWall = new BirsyModelRenderer(this, 36, 39);
        this.cauldronEastWall.setRotationPoint(0.0F, -3.0F, 0.0F);
        this.cauldronEastWall.addBox(6.0F, -13.0F, -6.0F, 2.0F, 13.0F, 12.0F, 0.0F, 0.0F, 0.0F);
        this.cauldronNorthWestLeg = new BirsyModelRenderer(this, 0, 48);
        this.cauldronNorthWestLeg.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.cauldronNorthWestLeg.addBox(0.0F, 0.0F, -4.0F, 2.0F, 3.0F, 2.0F, 0.0F, 0.0F, 0.0F);
        this.cauldronNorthWestLegA = new BirsyModelRenderer(this, 0, 43);
        this.cauldronNorthWestLegA.setRotationPoint(-8.0F, 0.0F, 8.0F);
        this.cauldronNorthWestLegA.addBox(0.0F, 0.0F, -2.0F, 4.0F, 3.0F, 2.0F, 0.0F, 0.0F, 0.0F);
        this.cauldronNorthWall = new BirsyModelRenderer(this, 0, 0);
        this.cauldronNorthWall.setRotationPoint(0.0F, -3.0F, 0.0F);
        this.cauldronNorthWall.addBox(-8.0F, -13.0F, 6.0F, 16.0F, 13.0F, 2.0F, 0.0F, 0.0F, 0.0F);
        this.cauldronSouthWestLegA = new BirsyModelRenderer(this, 0, 54);
        this.cauldronSouthWestLegA.setRotationPoint(-8.0F, 0.0F, -8.0F);
        this.cauldronSouthWestLegA.addBox(0.0F, 0.0F, 0.0F, 4.0F, 3.0F, 2.0F, 0.0F, 0.0F, 0.0F);
        this.cauldronSouthEastLegA = new BirsyModelRenderer(this, 13, 54);
        this.cauldronSouthEastLegA.setRotationPoint(8.0F, 0.0F, -8.0F);
        this.cauldronSouthEastLegA.addBox(-4.0F, 0.0F, 0.0F, 4.0F, 3.0F, 2.0F, 0.0F, 0.0F, 0.0F);
        this.cauldronWestWall = new BirsyModelRenderer(this, 36, 0);
        this.cauldronWestWall.setRotationPoint(0.0F, -3.0F, 0.0F);
        this.cauldronWestWall.addBox(-8.0F, -13.0F, -6.0F, 2.0F, 13.0F, 12.0F, 0.0F, 0.0F, 0.0F);
        this.cauldronSouthWestLeg = new BirsyModelRenderer(this, 0, 59);
        this.cauldronSouthWestLeg.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.cauldronSouthWestLeg.addBox(0.0F, 0.0F, 2.0F, 2.0F, 3.0F, 2.0F, 0.0F, 0.0F, 0.0F);
        this.cauldronFloor = new BirsyModelRenderer(this, 0, 30);
        this.cauldronFloor.setRotationPoint(0.0F, -3.0F, 0.0F);
        this.cauldronFloor.addBox(-6.0F, -1.0F, -6.0F, 12.0F, 1.0F, 12.0F, 0.0F, 0.0F, 0.0F);
        this.cauldronNorthEastLegB = new BirsyModelRenderer(this, 13, 48);
        this.cauldronNorthEastLegB.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.cauldronNorthEastLegB.addBox(-2.0F, 0.0F, -4.0F, 2.0F, 3.0F, 2.0F, 0.0F, 0.0F, 0.0F);
        this.cauldronSouthEastLegB = new BirsyModelRenderer(this, 13, 59);
        this.cauldronSouthEastLegB.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.cauldronSouthEastLegB.addBox(-2.0F, 0.0F, 2.0F, 2.0F, 3.0F, 2.0F, 0.0F, 0.0F, 0.0F);
        this.cauldronBase = new BirsyModelRenderer(this, 0, 0);
        this.cauldronBase.setRotationPoint(0.0F, 24.0F, 0.0F);
        this.cauldronBase.addBox(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
        this.cauldronSouthWall = new BirsyModelRenderer(this, 0, 15);
        this.cauldronSouthWall.setRotationPoint(0.0F, -3.0F, 0.0F);
        this.cauldronSouthWall.addBox(-8.0F, -13.0F, -8.0F, 16.0F, 13.0F, 2.0F, 0.0F, 0.0F, 0.0F);

        this.cauldronSpoonHandle = new BirsyModelRenderer(this, 52, 25);
        this.cauldronSpoonHandle.setRotationPoint(0.0F, -6.0F, 0.0F);
        this.cauldronSpoonHandle.addBox(-1.0F, -22.0F, 2.0F, 2.0F, 17.0F, 2.0F, 0.0F, 0.0F, 0.0F);
        this.cauldronSpoonHandle.rotateAngleX = -0.19198621771937624F;

        this.cauldronSpoonBowl = new BirsyModelRenderer(this, 52, 44);
        this.cauldronSpoonBowl.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.cauldronSpoonBowl.addBox(-2.0F, -5.0F, 2.0F, 4.0F, 5.0F, 2.0F, 0.0F, 0.0F, 0.0F);

        this.itemBase = new BirsyModelRenderer(this, 0, 0);
        this.itemBase.setRotationPoint(0.0F, -5.0F, 0.0F);
        this.itemBase.addBox(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
        this.item0Base = new BirsyModelRenderer(this, 0, 0);
        this.item0Base.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.item0Base.addBox(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
        this.item0 = new BirsyModelRenderer(this, 0, 0);
        this.item0.setRotationPoint(0.0F, 0.0F, 3.0F);
        this.item0.addBox(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
        this.item1Base = new BirsyModelRenderer(this, 0, 0);
        this.item1Base.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.item1Base.addBox(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
        this.item1 = new BirsyModelRenderer(this, 0, 0);
        this.item1.setRotationPoint(0.0F, 0.0F, 3.0F);
        this.item1.addBox(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
        this.item2Base = new BirsyModelRenderer(this, 0, 0);
        this.item2Base.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.item2Base.addBox(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
        this.item2 = new BirsyModelRenderer(this, 0, 0);
        this.item2.setRotationPoint(0.0F, 0.0F, 3.0F);
        this.item2.addBox(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
        this.item3Base = new BirsyModelRenderer(this, 0, 0);
        this.item3Base.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.item3Base.addBox(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
        this.item3 = new BirsyModelRenderer(this, 0, 0);
        this.item3.setRotationPoint(0.0F, 0.0F, 3.0F);
        this.item3.addBox(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);

        this.cauldronNorthWall.addChild(this.cauldronNorthEastLegA);
        this.cauldronBase.addChild(this.cauldronEastWall);
        this.cauldronBase.addChild(this.cauldronSpoonHandle);
        this.cauldronNorthWestLegA.addChild(this.cauldronNorthWestLeg);
        this.cauldronNorthWall.addChild(this.cauldronNorthWestLegA);
        this.cauldronBase.addChild(this.cauldronNorthWall);
        this.cauldronSouthWall.addChild(this.cauldronSouthWestLegA);
        this.cauldronSouthWall.addChild(this.cauldronSouthEastLegA);
        this.cauldronBase.addChild(this.cauldronWestWall);
        this.cauldronSouthWestLegA.addChild(this.cauldronSouthWestLeg);
        this.cauldronBase.addChild(this.cauldronFloor);
        this.cauldronNorthEastLegA.addChild(this.cauldronNorthEastLegB);
        this.cauldronSouthEastLegA.addChild(this.cauldronSouthEastLegB);
        this.cauldronBase.addChild(this.cauldronSouthWall);

        this.cauldronSpoonHandle.addChild(this.cauldronSpoonBowl);

        this.cauldronBase.addChild(this.itemBase);
        this.itemBase.addChild(this.item0Base);
        this.item0Base.addChild(this.item0);
        this.itemBase.addChild(this.item1Base);
        this.item1Base.addChild(this.item1);
        this.itemBase.addChild(this.item2Base);
        this.item2Base.addChild(this.item2);
        this.itemBase.addChild(this.item3Base);
        this.item3Base.addChild(this.item3);
    }

    @Override
    public void render(MatrixStack matrixStackIn, IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
        matrixStackIn.push();
        //Renders offset by 1/2 a block.
        matrixStackIn.translate(0.5, 0.5, 0.5);
        //It's also upside down for some reason?
        matrixStackIn.rotate(Vector3f.ZP.rotation((float) Math.PI));
        //Fixes it going a block down from the previous translation.
        matrixStackIn.translate(0, -1, 0);
        this.cauldronBase.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
        matrixStackIn.pop();
    }

    public void setSpoonRotation(float rotationInDegrees) {
        this.cauldronSpoonHandle.rotateAngleY = (float) Math.toRadians(rotationInDegrees);
    }

    public void setItemRotations(float rotationInDegrees0, float rotationInDegrees1, float rotationInDegrees2, float rotationInDegrees3) {
        this.item0Base.rotateAngleY = (float) Math.toRadians(rotationInDegrees0);
        this.item1Base.rotateAngleY = (float) Math.toRadians(rotationInDegrees1);
        this.item2Base.rotateAngleY = (float) Math.toRadians(rotationInDegrees2);
        this.item3Base.rotateAngleY = (float) Math.toRadians(rotationInDegrees3);
    }
    
    public BirsyModelRenderer getRendererFromIndex(int index) {
        if (index == 0) {
            return this.item0;
        } else if (index == 1) {
            return this.item1;
        } else if (index == 2) {
            return this.item2;
        } else if (index == 3) {
            return this.item3;
        } else {
            throw new IndexOutOfBoundsException();
        }
    }

    public void setCauldronShake (float shakeAmount, float ageInTicks) {
        this.cauldronBase.rotateAngleX = (MathHelper.sin(ageInTicks) * 0.02F) * shakeAmount;
        this.cauldronBase.rotateAngleZ = (MathHelper.cos(ageInTicks) * 0.02F) * shakeAmount;
    }
}
