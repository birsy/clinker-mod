package birsy.clinker.client.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.Blocks;

public class DebugEntityRenderer extends EntityRenderer<Entity> {
    private final BlockRenderDispatcher dispatcher;
    public DebugEntityRenderer(EntityRendererProvider.Context pContext) {
        super(pContext);
        this.dispatcher = pContext.getBlockRenderDispatcher();
        this.shadowRadius = 0.0001F;
        this.shadowStrength = 0.0F;
    }

    @Override
    public void render(Entity pEntity, float pEntityYaw, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight) {
        super.render(pEntity, pEntityYaw, pPartialTick, pPoseStack, pBuffer, pPackedLight);
        pPoseStack.pushPose();
        pPoseStack.mulPose(Axis.YN.rotationDegrees(180 + pEntity.getViewYRot(pPartialTick)));
        pPoseStack.mulPose(Axis.XP.rotationDegrees(pEntity.getViewXRot(pPartialTick)));
        pPoseStack.scale(pEntity.getBbWidth(), pEntity.getBbHeight(), pEntity.getBbWidth());
        pPoseStack.translate(pEntity.getBbWidth() * -0.5F, 0, pEntity.getBbWidth() * -0.5F);


        this.dispatcher.renderSingleBlock(Blocks.DISPENSER.defaultBlockState(), pPoseStack, pBuffer, pPackedLight, OverlayTexture.NO_OVERLAY);
        pPoseStack.popPose();
    }

    @Override
    public ResourceLocation getTextureLocation(Entity pEntity) {
        return null;
    }
}

