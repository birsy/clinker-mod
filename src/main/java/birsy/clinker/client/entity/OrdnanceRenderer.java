package birsy.clinker.client.entity;

import birsy.clinker.common.world.entity.projectile.OrdnanceEntity;
import birsy.clinker.core.Clinker;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.phys.Vec3;

public class OrdnanceRenderer extends EntityRenderer<OrdnanceEntity> {
    private static final ResourceLocation ORDNANCE_LOCATION = Clinker.resource("textures/entity/ordnance.png");

    public OrdnanceRenderer(EntityRendererProvider.Context pContext) {
        super(pContext);
    }

    @Override
    public void render(OrdnanceEntity pEntity, float pEntityYaw, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight) {
        VertexConsumer consumer = pBuffer.getBuffer(RenderType.entityCutout(this.getTextureLocation(pEntity)));

        // todo: redo all of this it sucks ASS!!!
        float bombFlash = 0;
        if (pEntity.hasFuse() && pEntity.canDetonate()) {
            float fuseTime = (pEntity.getFuseTime() + pPartialTick) / (pEntity.getMaxFuseTime() + 1.0F);
            float fuseFactor = fuseTime * 120;
            bombFlash = Mth.clamp(Mth.sin(((fuseFactor * fuseFactor * Mth.PI) / 20.0F) * 0.02F), 0, 1) * fuseTime;
            float bigPuffTime = 110;
            if (fuseFactor > bigPuffTime) bombFlash = Math.max((float) (1 - Math.pow((fuseFactor - 120) / (120 - bigPuffTime), 4)) * fuseTime, bombFlash);
        }

        BlockPos lightPos = BlockPos.containing(pEntity.getX(), pEntity.getY() + pEntity.getBbHeight() * 0.5, pEntity.getZ());
        int overlay = OverlayTexture.pack(bombFlash, pEntity.hurtMarked);
        int blockLight = pEntity.level().getBrightness(LightLayer.BLOCK, lightPos),
            skyLight = pEntity.level().getBrightness(LightLayer.SKY, lightPos);
        int light = LightTexture.pack(Math.max((int) (bombFlash * 16), blockLight), skyLight);

        Vec3 directionTowardsCamera = this.entityRenderDispatcher.camera.getPosition().subtract(pEntity.getPosition(pPartialTick)).normalize();

        pPoseStack.pushPose();
        pPoseStack.translate(0, pEntity.getBbHeight() * 0.5F, 0);
        float size = 1 / 16.0F;
        size *= 1 + Mth.sqrt(bombFlash) * 0.2F;
        size *= 0.6F;
        pPoseStack.scale(size, size, size);

        drawBomb(pPoseStack, consumer, light, overlay, pEntity, pPartialTick, directionTowardsCamera);
        pPoseStack.popPose();

        super.render(pEntity, pEntityYaw, pPartialTick, pPoseStack, pBuffer, pPackedLight);
    }
    
    public void drawBomb(PoseStack stack, VertexConsumer consumer, int pPackedLight, int overlayTexture, OrdnanceEntity pEntity, float pPartialTick, Vec3 directionTowardsCamera) {
        stack.pushPose();
        Vec3 dir = directionTowardsCamera.scale(8 / 16.0F);
        stack.translate(dir.x(), dir.y() - 1, dir.z());
        stack.mulPose(this.entityRenderDispatcher.cameraOrientation());
        stack.mulPose(Axis.ZP.rotation(-pEntity.getSpin(pPartialTick)));
        stack.translate(0, 1.5, 0);

        float u0 = 0, u1 = 12.0F / 32.0F;
        float v0 = 0, v1 = 1.0F;
        int color = FastColor.ARGB32.colorFromFloat(1, 1, 1, 1);
        PoseStack.Pose pose = stack.last();
        consumer.addVertex(pose, 6.0F, -9.0F, 0)
                .setColor(color)
                .setUv(u0, v1)
                .setOverlay(overlayTexture)
                .setLight(pPackedLight)
                .setNormal(pose, 0, 0, 1);
        consumer.addVertex(pose, 6.0F, 9.0F, 0)
                .setColor(color)
                .setUv(u0, v0)
                .setOverlay(overlayTexture)
                .setLight(pPackedLight)
                .setNormal(pose, 0, 0, 1);
        consumer.addVertex(pose, -6.0F, 9.0F, 0)
                .setColor(color)
                .setUv(u1, v0)
                .setOverlay(overlayTexture)
                .setLight(pPackedLight)
                .setNormal(pose, 0, 0, 1);
        consumer.addVertex(pose, -6.0F, -9.0F, 0)
                .setColor(color)
                .setUv(u1, v1)
                .setOverlay(overlayTexture)
                .setLight(pPackedLight)
                .setNormal(pose, 0, 0, 1);
        stack.popPose();
    }

    @Override
    public ResourceLocation getTextureLocation(OrdnanceEntity pEntity) {
        return ORDNANCE_LOCATION;
    }
}
