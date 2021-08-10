package birsy.clinker.client.render.tileentity.model;

import birsy.clinker.core.util.MathUtils;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import com.mojang.math.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class MitesoilDiffuserModel extends Model {
    public ModelPart diffuserBulb;

    public MitesoilDiffuserModel() {
        super(RenderType::entityCutout);

        this.texWidth = 64;
        this.texHeight = 32;
        this.diffuserBulb = new ModelPart(this, 0, 0);
        this.diffuserBulb.setPos(0.0F, 24.0F, 0.0F);
        this.diffuserBulb.addBox(-7.0F, -16.0F, -7.0F, 14.0F, 16.0F, 14.0F, 0.0F, 0.0F, 0.0F);
    }

    public void render(PoseStack matrixStackIn, VertexConsumer bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha, float burstTicks, int burstLength) {
        matrixStackIn.pushPose();
        //Renders offset by 1/2 a block.
        matrixStackIn.translate(0.5, 0.5, 0.5);

        final float minVerticalScale = 0.8f;
        float verticalScaleFactor = MathUtils.mapRange(0, 1, minVerticalScale, 1, -((burstTicks/ burstLength) - 1));

        matrixStackIn.scale(1.0f, verticalScaleFactor, 1.0f);
        matrixStackIn.translate(0.0f, (verticalScaleFactor * 0.5f) - 0.5f, 0.0f);

        //It's also upside down for some reason?
        matrixStackIn.mulPose(Vector3f.ZP.rotation((float) Math.PI));
        //Fixes it going a block down from the previous translation.
        matrixStackIn.translate(0, -1, 0);

        final float maxHorizontalScale = 1.2f;
        float horizontalScaleFactor = MathUtils.mapRange(0, 1, 1, maxHorizontalScale, burstTicks / burstLength);

        matrixStackIn.scale(horizontalScaleFactor, 1.0f, horizontalScaleFactor);

        diffuserBulb.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);

        matrixStackIn.popPose();
    }

    @Override
    public void renderToBuffer(PoseStack matrixStackIn, VertexConsumer bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
        matrixStackIn.pushPose();
        //Renders offset by 1/2 a block.
        matrixStackIn.translate(0.5, 0.5, 0.5);
        //It's also upside down for some reason?
        matrixStackIn.mulPose(Vector3f.ZP.rotation((float) Math.PI));
        //Fixes it going a block down from the previous translation.
        matrixStackIn.translate(0, -1, 0);
        diffuserBulb.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
        matrixStackIn.popPose();
    }
}
