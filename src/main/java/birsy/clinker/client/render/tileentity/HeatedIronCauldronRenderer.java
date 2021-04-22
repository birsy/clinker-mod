package birsy.clinker.client.render.tileentity;

import birsy.clinker.client.render.tileentity.model.HeatedCauldronModel;
import birsy.clinker.common.tileentity.HeatedIronCauldronTileEntity;
import birsy.clinker.core.Clinker;
import birsy.clinker.core.util.MathUtils;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.LightType;

public class HeatedIronCauldronRenderer<T extends HeatedIronCauldronTileEntity> extends TileEntityRenderer<T> {
    protected static final ResourceLocation TEXTURE = new ResourceLocation(Clinker.MOD_ID, "textures/blocks/heated_iron_cauldron/cauldron.png");
    protected static final ResourceLocation OVERLAY = new ResourceLocation(Clinker.MOD_ID, "textures/blocks/heated_iron_cauldron/cauldron_glow.png");
    private final HeatedCauldronModel cauldronModel;

    public HeatedIronCauldronRenderer(TileEntityRendererDispatcher rendererDispatcherIn) {
        super(rendererDispatcherIn);
        cauldronModel = new HeatedCauldronModel();
    }

    @Override
    public void render(T tileEntityIn, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn) {
        cauldronModel.setSpoonRotation(tileEntityIn.getSpoonRotation(partialTicks));
        cauldronModel.render(matrixStackIn, bufferIn.getBuffer(RenderType.getEntityCutout(TEXTURE)), combinedLightIn, combinedOverlayIn, 1.0f, 1.0f, 1.0f, 1.0f);

        final float intensity = 0.1f;
        float fade = MathUtils.mapRange(0, 100, 0, 1, tileEntityIn.getHeatOverlayStrength(partialTicks)) * ((MathHelper.sin(tileEntityIn.ageInTicks * 0.125f) * intensity) + (1 - intensity));
        cauldronModel.render(matrixStackIn, bufferIn.getBuffer(RenderType.getEntityTranslucent(OVERLAY)), LightTexture.packLight(15, tileEntityIn.getWorld().getLightFor(LightType.SKY, tileEntityIn.getPos())), OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, fade);
    }
}
