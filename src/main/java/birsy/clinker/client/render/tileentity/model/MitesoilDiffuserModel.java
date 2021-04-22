package birsy.clinker.client.render.tileentity.model;

import birsy.clinker.core.util.MathUtils;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class MitesoilDiffuserModel extends Model {
    public ModelRenderer diffuserBulb;

    public MitesoilDiffuserModel() {
        super(RenderType::getEntityCutout);

        this.textureWidth = 64;
        this.textureHeight = 32;
        this.diffuserBulb = new ModelRenderer(this, 0, 0);
        this.diffuserBulb.setRotationPoint(0.0F, 24.0F, 0.0F);
        this.diffuserBulb.addBox(-7.0F, -16.0F, -7.0F, 14.0F, 16.0F, 14.0F, 0.0F, 0.0F, 0.0F);
    }

    public void render(MatrixStack matrixStackIn, IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha, float burstTicks, int burstLength) {
        matrixStackIn.push();
        //Renders offset by 1/2 a block.
        matrixStackIn.translate(0.5, 0.5, 0.5);

        final float minVerticalScale = 0.8f;
        float verticalScaleFactor = MathUtils.mapRange(0, 1, minVerticalScale, 1, -((burstTicks/ burstLength) - 1));

        matrixStackIn.scale(1.0f, verticalScaleFactor, 1.0f);
        matrixStackIn.translate(0.0f, (verticalScaleFactor * 0.5f) - 0.5f, 0.0f);

        //It's also upside down for some reason?
        matrixStackIn.rotate(Vector3f.ZP.rotation((float) Math.PI));
        //Fixes it going a block down from the previous translation.
        matrixStackIn.translate(0, -1, 0);

        final float maxHorizontalScale = 1.2f;
        float horizontalScaleFactor = MathUtils.mapRange(0, 1, 1, maxHorizontalScale, burstTicks / burstLength);

        matrixStackIn.scale(horizontalScaleFactor, 1.0f, horizontalScaleFactor);

        diffuserBulb.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);

        matrixStackIn.pop();
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
        diffuserBulb.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
        matrixStackIn.pop();
    }
}
