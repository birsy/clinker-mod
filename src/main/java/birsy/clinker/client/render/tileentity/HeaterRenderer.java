package birsy.clinker.client.render.tileentity;

import birsy.clinker.common.tileentity.HeaterTileEntity;
import birsy.clinker.core.Clinker;
import birsy.clinker.core.util.MathUtils;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import com.mojang.math.Vector3f;
import net.minecraft.world.level.LightLayer;

public class HeaterRenderer<T extends HeaterTileEntity> extends BlockEntityRenderer<T> {
    private static final ResourceLocation HEATER = new ResourceLocation(Clinker.MOD_ID, "textures/blocks/heater/heater.png");
    private final ModelPart heatModel;

    public HeaterRenderer(BlockEntityRenderDispatcher rendererDispatcherIn) {
        super(rendererDispatcherIn);
        this.heatModel = new ModelPart(64, 64, 0, 32);
        this.heatModel.setPos(0.0F, 0.0F, 0.0F);
        this.heatModel.addBox(-8.0F, -8.0F, -8.0F, 16.0F, 16.0F, 16.0F, 0.0F, 0.0F, 0.0F);
    }

    @Override
    public void render(T tileEntityIn, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn) {
        matrixStackIn.pushPose();

        matrixStackIn.translate(0.5, 0.5, 0.5);
        matrixStackIn.mulPose(Vector3f.ZP.rotation((float) Math.PI));
        matrixStackIn.scale(1.002F, 1.002F, 1.002F);

        final float intensity = 0.02f;
        float fade = MathUtils.mapRange(0, 100, 0, 1, tileEntityIn.getHeatOverlayStrength(partialTicks)) * ((Mth.sin(tileEntityIn.ageInTicks * 0.125f) * intensity) + (1 - intensity));
        heatModel.render(matrixStackIn, bufferIn.getBuffer(RenderType.entityTranslucent(HEATER)), LightTexture.pack(15, tileEntityIn.getLevel().getBrightness(LightLayer.SKY, tileEntityIn.getBlockPos())), OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, fade);

        matrixStackIn.popPose();
    }
}
