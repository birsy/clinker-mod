package birsy.clinker.client.render.tileentity;

import birsy.clinker.common.tileentity.HeaterTileEntity;
import birsy.clinker.core.Clinker;
import birsy.clinker.core.util.MathUtils;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.world.LightType;

public class HeaterRenderer<T extends HeaterTileEntity> extends TileEntityRenderer<T> {
    private static final ResourceLocation HEATER = new ResourceLocation(Clinker.MOD_ID, "textures/blocks/heater/heater.png");
    private final ModelRenderer heatModel;

    public HeaterRenderer(TileEntityRendererDispatcher rendererDispatcherIn) {
        super(rendererDispatcherIn);
        this.heatModel = new ModelRenderer(64, 64, 0, 32);
        this.heatModel.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.heatModel.addBox(-8.0F, -8.0F, -8.0F, 16.0F, 16.0F, 16.0F, 0.0F, 0.0F, 0.0F);
    }

    @Override
    public void render(T tileEntityIn, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn) {
        matrixStackIn.push();

        matrixStackIn.translate(0.5, 0.5, 0.5);
        matrixStackIn.rotate(Vector3f.ZP.rotation((float) Math.PI));
        matrixStackIn.scale(1.002F, 1.002F, 1.002F);

        final float intensity = 0.02f;
        float fade = MathUtils.mapRange(0, 100, 0, 1, tileEntityIn.getHeatOverlayStrength(partialTicks)) * ((MathHelper.sin(tileEntityIn.ageInTicks * 0.125f) * intensity) + (1 - intensity));
        heatModel.render(matrixStackIn, bufferIn.getBuffer(RenderType.getEntityTranslucent(HEATER)), LightTexture.packLight(15, tileEntityIn.getWorld().getLightFor(LightType.SKY, tileEntityIn.getPos())), OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, fade);

        matrixStackIn.pop();
    }
}
