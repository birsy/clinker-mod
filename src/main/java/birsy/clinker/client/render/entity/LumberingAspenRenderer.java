package birsy.clinker.client.render.entity;

import birsy.clinker.common.world.entity.LumberingAspenEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraftforge.client.model.data.ModelData;

public class LumberingAspenRenderer extends EntityRenderer<LumberingAspenEntity> {
    public LumberingAspenRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(LumberingAspenEntity pEntity, float pEntityYaw, float pPartialTicks, PoseStack pMatrixStack, MultiBufferSource pBuffer, int pPackedLight) {
        BlockRenderDispatcher renderManager = Minecraft.getInstance().getBlockRenderer();
        Vec3i trunkSize = pEntity.getTrunkSize();
        pMatrixStack.pushPose();
        float ticks = pEntity.tickCount + pPartialTicks;
        pMatrixStack.mulPose(Axis.XP.rotation(Mth.cos(ticks * 0.1F) * 0.1F));
        pMatrixStack.mulPose(Axis.ZP.rotation(Mth.sin(ticks * 0.11F) * 0.1F));
        for (int x = 0; x < trunkSize.getX(); x++) {
            for (int y = 0; y < trunkSize.getY(); y++) {
                for (int z = 0; z < trunkSize.getZ(); z++) {
                    pMatrixStack.pushPose();
                    pMatrixStack.translate(x, y, z);
                    renderManager.renderSingleBlock(pEntity.getStateInTrunk(x, y, z), pMatrixStack, pBuffer, pPackedLight, LivingEntityRenderer.getOverlayCoords(pEntity, 0.0F), ModelData.EMPTY, RenderType.cutoutMipped());
                    pMatrixStack.popPose();
                }
            }
        }
        pMatrixStack.popPose();

        super.render(pEntity, pEntityYaw, pPartialTicks, pMatrixStack, pBuffer, pPackedLight);
    }


    public ResourceLocation getTextureLocation(LumberingAspenEntity entity) {
        return null;
    }
}
