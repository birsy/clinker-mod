package birsy.clinker.client.render.tileentity;

import birsy.clinker.common.tileentity.SoulWellTileEntity;
import birsy.clinker.core.Clinker;
import birsy.clinker.core.util.MathUtils;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3f;

public class SoulWellRenderer<T extends SoulWellTileEntity> extends BlockEntityRenderer<T> {
    protected static final ResourceLocation LIGHT = new ResourceLocation(Clinker.MOD_ID, "textures/blocks/soul_well/soul_well_light.png");
    private final ModelPart lightModel;

    public SoulWellRenderer(BlockEntityRenderDispatcher rendererDispatcherIn) {
        super(rendererDispatcherIn);
        this.lightModel = new ModelPart(64, 32, 0, 0);
        this.lightModel.setPos(0.0F, 0.0F, 0.0F);
        this.lightModel.addBox(-8.0F, -8.0F, -8.0F, 16.0F, 16.0F, 16.0F, 0.0F, 0.0F, 0.0F);
    }

    @Override
    public void render(T tileEntityIn, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn) {
        /**
        matrixStackIn.push();

        matrixStackIn.translate(0.5, 0.5, 0.5);
        matrixStackIn.rotate(Vector3f.ZP.rotation((float) Math.PI));

        float glowRadius = 8;
        float resolution = 24;

        for (int i = 1; i < resolution; i++) {
            matrixStackIn.push();

            float distanceBetweenLayers = glowRadius / resolution;

            float size = 1.0F + ((i * distanceBetweenLayers) / (glowRadius * 2));
            size = size * MathUtils.minMaxSin(tileEntityIn.getAgeInTicks(partialTicks) * 0.02F, 1, 1.125F);
            matrixStackIn.scale(size, size, size);

            float wiggleX = MathHelper.sin(tileEntityIn.getAgeInTicks(partialTicks) * 0.06F) * 0.025F;
            float wiggleY = MathHelper.cos(tileEntityIn.getAgeInTicks(partialTicks) * 0.07F) * 0.025F;
            float wiggleZ = MathHelper.sin(tileEntityIn.getAgeInTicks(partialTicks) * 0.08F) * 0.025F;
            matrixStackIn.translate(wiggleX * (size - 1.0F), wiggleY * (size - 1.0F), wiggleZ * (size - 1.0F));

            //matrixStackIn.scale(-1, -1, -1);
            float opacity = 1 / (resolution * 2.0F);
            this.lightModel.render(matrixStackIn, bufferIn.getBuffer(RenderType.getEnergySwirl(LIGHT, 0, 0)), combinedLightIn, combinedOverlayIn, opacity, opacity, opacity, 1.0F);

            matrixStackIn.pop();
        }

        matrixStackIn.pop();
         */
    }

    @Override
    public boolean shouldRenderOffScreen(T te) {
        return true;
    }
}
