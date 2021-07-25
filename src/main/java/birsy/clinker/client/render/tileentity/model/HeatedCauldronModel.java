package birsy.clinker.client.render.tileentity.model;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;

@OnlyIn(Dist.CLIENT)
public class HeatedCauldronModel extends Model {
    public ModelRenderer cauldronBase;
    public ModelRenderer cauldronSouthWall;
    public ModelRenderer cauldronNorthWall;
    public ModelRenderer cauldronWestWall;
    public ModelRenderer cauldronEastWall;
    public ModelRenderer cauldronFloor;
    public ModelRenderer cauldronSouthWestLegA;
    public ModelRenderer cauldronSouthEastLegA;
    public ModelRenderer cauldronSouthWestLeg;
    public ModelRenderer cauldronSouthEastLegB;
    public ModelRenderer cauldronNorthEastLegA;
    public ModelRenderer cauldronNorthWestLegA;
    public ModelRenderer cauldronNorthEastLegB;
    public ModelRenderer cauldronNorthWestLeg;

    public ModelRenderer cauldronSpoonHandle;
    public ModelRenderer cauldronSpoonBowl;

    public HeatedCauldronModel() {
        super(RenderType::getEntityCutout);

        this.textureWidth = 64;
        this.textureHeight = 64;
        this.cauldronNorthEastLegA = new ModelRenderer(this, 13, 43);
        this.cauldronNorthEastLegA.setRotationPoint(8.0F, 0.0F, 8.0F);
        this.cauldronNorthEastLegA.addBox(-4.0F, 0.0F, -2.0F, 4.0F, 3.0F, 2.0F, 0.0F, 0.0F, 0.0F);
        this.cauldronEastWall = new ModelRenderer(this, 36, 39);
        this.cauldronEastWall.setRotationPoint(0.0F, -3.0F, 0.0F);
        this.cauldronEastWall.addBox(6.0F, -13.0F, -6.0F, 2.0F, 13.0F, 12.0F, 0.0F, 0.0F, 0.0F);
        this.cauldronNorthWestLeg = new ModelRenderer(this, 0, 48);
        this.cauldronNorthWestLeg.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.cauldronNorthWestLeg.addBox(0.0F, 0.0F, -4.0F, 2.0F, 3.0F, 2.0F, 0.0F, 0.0F, 0.0F);
        this.cauldronNorthWestLegA = new ModelRenderer(this, 0, 43);
        this.cauldronNorthWestLegA.setRotationPoint(-8.0F, 0.0F, 8.0F);
        this.cauldronNorthWestLegA.addBox(0.0F, 0.0F, -2.0F, 4.0F, 3.0F, 2.0F, 0.0F, 0.0F, 0.0F);
        this.cauldronNorthWall = new ModelRenderer(this, 0, 0);
        this.cauldronNorthWall.setRotationPoint(0.0F, -3.0F, 0.0F);
        this.cauldronNorthWall.addBox(-8.0F, -13.0F, 6.0F, 16.0F, 13.0F, 2.0F, 0.0F, 0.0F, 0.0F);
        this.cauldronSouthWestLegA = new ModelRenderer(this, 0, 54);
        this.cauldronSouthWestLegA.setRotationPoint(-8.0F, 0.0F, -8.0F);
        this.cauldronSouthWestLegA.addBox(0.0F, 0.0F, 0.0F, 4.0F, 3.0F, 2.0F, 0.0F, 0.0F, 0.0F);
        this.cauldronSouthEastLegA = new ModelRenderer(this, 13, 54);
        this.cauldronSouthEastLegA.setRotationPoint(8.0F, 0.0F, -8.0F);
        this.cauldronSouthEastLegA.addBox(-4.0F, 0.0F, 0.0F, 4.0F, 3.0F, 2.0F, 0.0F, 0.0F, 0.0F);
        this.cauldronWestWall = new ModelRenderer(this, 36, 0);
        this.cauldronWestWall.setRotationPoint(0.0F, -3.0F, 0.0F);
        this.cauldronWestWall.addBox(-8.0F, -13.0F, -6.0F, 2.0F, 13.0F, 12.0F, 0.0F, 0.0F, 0.0F);
        this.cauldronSouthWestLeg = new ModelRenderer(this, 0, 59);
        this.cauldronSouthWestLeg.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.cauldronSouthWestLeg.addBox(0.0F, 0.0F, 2.0F, 2.0F, 3.0F, 2.0F, 0.0F, 0.0F, 0.0F);
        this.cauldronFloor = new ModelRenderer(this, 0, 30);
        this.cauldronFloor.setRotationPoint(0.0F, -3.0F, 0.0F);
        this.cauldronFloor.addBox(-6.0F, -1.0F, -6.0F, 12.0F, 1.0F, 12.0F, 0.0F, 0.0F, 0.0F);
        this.cauldronNorthEastLegB = new ModelRenderer(this, 13, 48);
        this.cauldronNorthEastLegB.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.cauldronNorthEastLegB.addBox(-2.0F, 0.0F, -4.0F, 2.0F, 3.0F, 2.0F, 0.0F, 0.0F, 0.0F);
        this.cauldronSouthEastLegB = new ModelRenderer(this, 13, 59);
        this.cauldronSouthEastLegB.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.cauldronSouthEastLegB.addBox(-2.0F, 0.0F, 2.0F, 2.0F, 3.0F, 2.0F, 0.0F, 0.0F, 0.0F);
        this.cauldronBase = new ModelRenderer(this, 0, 0);
        this.cauldronBase.setRotationPoint(0.0F, 24.0F, 0.0F);
        this.cauldronBase.addBox(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
        this.cauldronSouthWall = new ModelRenderer(this, 0, 15);
        this.cauldronSouthWall.setRotationPoint(0.0F, -3.0F, 0.0F);
        this.cauldronSouthWall.addBox(-8.0F, -13.0F, -8.0F, 16.0F, 13.0F, 2.0F, 0.0F, 0.0F, 0.0F);

        this.cauldronSpoonHandle = new ModelRenderer(this, 52, 25);
        this.cauldronSpoonHandle.setRotationPoint(0.0F, -6.0F, 0.0F);
        this.cauldronSpoonHandle.addBox(-1.0F, -22.0F, 2.0F, 2.0F, 17.0F, 2.0F, 0.0F, 0.0F, 0.0F);
        this.cauldronSpoonHandle.rotateAngleX = -0.19198621771937624F;

        this.cauldronSpoonBowl = new ModelRenderer(this, 52, 44);
        this.cauldronSpoonBowl.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.cauldronSpoonBowl.addBox(-2.0F, -5.0F, 2.0F, 4.0F, 5.0F, 2.0F, 0.0F, 0.0F, 0.0F);

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

    public void setCauldronShake (float shakeAmount, float ageInTicks) {
        this.cauldronBase.rotateAngleX = (MathHelper.sin(ageInTicks) * 0.02F) * shakeAmount;
        this.cauldronBase.rotateAngleZ = (MathHelper.cos(ageInTicks) * 0.02F) * shakeAmount;
    }
}
