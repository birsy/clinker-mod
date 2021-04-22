package birsy.clinker.client.render.tileentity.model;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;

@OnlyIn(Dist.CLIENT)
public class HeatedCauldronModel extends Model {
    public ModelRenderer cauldronBottom;
    public ModelRenderer cauldronSouthWall;
    public ModelRenderer cauldronNorthWall;
    public ModelRenderer cauldronWestWall;
    public ModelRenderer cauldronEastWall;
    public ModelRenderer cauldronNorthWestLeg;
    public ModelRenderer cauldronNorthEastLeg;
    public ModelRenderer cauldronSouthWestLeg;
    public ModelRenderer cauldronSouthEastLeg;
    public ModelRenderer cauldronSpoon;

    public HeatedCauldronModel() {
        super(RenderType::getEntityCutout);

        this.textureWidth = 128;
        this.textureHeight = 64;

        //Cauldron Bowl
        this.cauldronNorthWall = new ModelRenderer(this, 0, 17);
        this.cauldronNorthWall.setRotationPoint(0.0F, 19.0F, 0.0F);
        this.cauldronNorthWall.addBox(-8.0F, -11.0F, 5.0F, 16.0F, 14.0F, 3.0F, 0.0F, 0.0F, 0.0F);

        this.cauldronSouthWall = new ModelRenderer(this, 0, 0);
        this.cauldronSouthWall.setRotationPoint(0.0F, 19.0F, 0.0F);
        this.cauldronSouthWall.addBox(-8.0F, -11.0F, -8.0F, 16.0F, 14.0F, 3.0F, 0.0F, 0.0F, 0.0F);

        this.cauldronEastWall = new ModelRenderer(this, 64, 0);
        this.cauldronEastWall.setRotationPoint(0.0F, 19.0F, 0.0F);
        this.cauldronEastWall.addBox(5.0F, -11.0F, -5.0F, 3.0F, 14.0F, 10.0F, 0.0F, 0.0F, 0.0F);

        this.cauldronWestWall = new ModelRenderer(this, 38, 0);
        this.cauldronWestWall.setRotationPoint(0.0F, 19.0F, 0.0F);
        this.cauldronWestWall.addBox(-8.0F, -11.0F, -5.0F, 3.0F, 14.0F, 10.0F, 0.0F, 0.0F, 0.0F);

        this.cauldronBottom = new ModelRenderer(this, 0, 34);
        this.cauldronBottom.setRotationPoint(0.0F, 19.0F, 0.0F);
        this.cauldronBottom.addBox(-5.0F, 0.0F, -5.0F, 10.0F, 3.0F, 10.0F, 0.0F, 0.0F, 0.0F);

        //Cauldron Legs
        this.cauldronNorthEastLeg = new ModelRenderer(this, 54, 24);
        this.cauldronNorthEastLeg.setRotationPoint(0.0F, 24.0F, 0.0F);
        this.cauldronNorthEastLeg.addBox(3.0F, -2.0F, 5.0F, 5.0F, 2.0F, 3.0F, 0.0F, 0.0F, 0.0F);
        this.cauldronNorthEastLeg.addBox(5.0F, -2.0F, 3.0F, 3.0F, 2.0F, 2.0F, 0.0F, 0.0F, 0.0F);
        this.cauldronNorthEastLeg.setTextureOffset(0, 5);

        this.cauldronNorthWestLeg = new ModelRenderer(this, 38, 24);
        this.cauldronNorthWestLeg.setRotationPoint(0.0F, 24.0F, 0.0F);
        this.cauldronNorthWestLeg.addBox(-8.0F, -2.0F, 5.0F, 5.0F, 2.0F, 3.0F, 0.0F, 0.0F, 0.0F);
        this.cauldronNorthWestLeg.addBox(-8.0F, -2.0F, 3.0F, 3.0F, 2.0F, 2.0F, 0.0F, 0.0F, 0.0F);
        this.cauldronNorthWestLeg.setTextureOffset(0, 5);

        this.cauldronSouthEastLeg = new ModelRenderer(this, 54, 33);
        this.cauldronSouthEastLeg.setRotationPoint(0.0F, 24.0F, 0.0F);
        this.cauldronSouthEastLeg.addBox(3.0F, -2.0F, -8.0F, 5.0F, 2.0F, 3.0F, 0.0F, 0.0F, 0.0F);
        this.cauldronSouthEastLeg.addBox(5.0F, -2.0F, -5.0F, 3.0F, 2.0F, 2.0F, 0.0F, 0.0F, 0.0F);
        this.cauldronSouthEastLeg.setTextureOffset(0, 5);

        this.cauldronSouthWestLeg = new ModelRenderer(this, 38, 33);
        this.cauldronSouthWestLeg.setRotationPoint(0.0F, 24.0F, 0.0F);
        this.cauldronSouthWestLeg.addBox(-8.0F, -2.0F, -8.0F, 5.0F, 2.0F, 3.0F, 0.0F, 0.0F, 0.0F);
        this.cauldronSouthWestLeg.addBox(-8.0F, -2.0F, -5.0F, 3.0F, 2.0F, 2.0F, 0.0F, 0.0F, 0.0F);
        this.cauldronSouthWestLeg.setTextureOffset(0, 5);

        //Spoon
        this.cauldronSpoon = new ModelRenderer(this, 90, 0);
        this.cauldronSpoon.setRotationPoint(0.0F, 19.0F, 0.0F);
        this.cauldronSpoon.addBox(-1.0F, -22.0F, 1.75F, 2.0F, 17.0F, 2.0F, 0.0F, 0.0F, 0.0F);
        this.cauldronSpoon.addBox(-2.0F, -5.0F, 1.75F, 4.0F, 5.0F, 2.0F, 0.0F, 0.0F, 0.0F);
        this.cauldronSpoon.setTextureOffset(0, 19);
        this.cauldronSpoon.rotateAngleX = -0.12217304763960307F;
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
        ImmutableList.of(this.cauldronSouthWestLeg, this.cauldronSouthWall, this.cauldronEastWall, this.cauldronNorthWall, this.cauldronBottom, this.cauldronWestWall, this.cauldronNorthWestLeg, this.cauldronSpoon, this.cauldronNorthEastLeg, this.cauldronSouthEastLeg).forEach((modelRenderer) -> {
            modelRenderer.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
        });
        matrixStackIn.pop();
    }

    public void setSpoonRotation(float rotationInDegrees) {
        this.cauldronSpoon.rotateAngleY = (float) Math.toRadians(rotationInDegrees);
    }
}
