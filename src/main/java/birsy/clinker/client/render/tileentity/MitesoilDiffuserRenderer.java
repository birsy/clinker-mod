package birsy.clinker.client.render.tileentity;

import birsy.clinker.client.render.tileentity.model.MitesoilDiffuserModel;
import birsy.clinker.common.tileentity.MitesoilDiffuserTileEntity;
import birsy.clinker.core.Clinker;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.resources.ResourceLocation;

public class MitesoilDiffuserRenderer<T extends MitesoilDiffuserTileEntity> extends BlockEntityRenderer<T> {
    protected static final ResourceLocation TEXTURE = new ResourceLocation(Clinker.MOD_ID, "textures/blocks/mitesoil/mitesoil_diffuser.png");
    private final MitesoilDiffuserModel mitesoilDiffuserModel;

    public MitesoilDiffuserRenderer(BlockEntityRenderDispatcher rendererDispatcherIn) {
        super(rendererDispatcherIn);
        mitesoilDiffuserModel = new MitesoilDiffuserModel();
    }

    @Override
    public void render(T tileEntityIn, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn) {
        if (tileEntityIn.getBurstTicks(partialTicks) >= tileEntityIn.burstLength || tileEntityIn.getBurstTicks(partialTicks) <= 0 || tileEntityIn.burstLength <= 0) {
            mitesoilDiffuserModel.renderToBuffer(matrixStackIn, bufferIn.getBuffer(RenderType.entityCutout(TEXTURE)), combinedLightIn, combinedOverlayIn, 1.0f, 1.0f, 1.0f, 1.0f);
        } else {
            mitesoilDiffuserModel.render(matrixStackIn, bufferIn.getBuffer(RenderType.entityCutout(TEXTURE)), combinedLightIn, combinedOverlayIn, 1.0f, 1.0f, 1.0f, 1.0f,
                    tileEntityIn.getBurstTicks(partialTicks), tileEntityIn.burstLength);
        }
    }
}
