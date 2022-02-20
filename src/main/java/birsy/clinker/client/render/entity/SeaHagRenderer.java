package birsy.clinker.client.render.entity;

import birsy.clinker.client.render.entity.model.SeaHagModel;
import birsy.clinker.common.entity.SeaHagEntity;
import birsy.clinker.core.Clinker;
import birsy.clinker.core.util.MathUtils;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.debug.DebugRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.phys.Vec3;

public class SeaHagRenderer extends ClinkerEntityRenderer<SeaHagEntity, SeaHagModel<SeaHagEntity>> {
    private static final ResourceLocation SEA_HAG_LOCATION = new ResourceLocation(Clinker.MOD_ID, "textures/entity/sea_hag.png");

    public SeaHagRenderer(EntityRendererProvider.Context context) {
        super(context, new SeaHagModel<>(context.bakeLayer(SeaHagModel.LAYER_LOCATION)), 1.7F);
    }

    protected float getFlipDegrees(SeaHagEntity pLivingEntity) {
        return 0.0F;
    }

    @Override
    public void render(SeaHagEntity pEntity, float pEntityYaw, float pPartialTicks, PoseStack pMatrixStack, MultiBufferSource pBuffer, int pPackedLight) {
        /*
        float deathTime = (float) Math.sqrt(MathUtils.ease((pEntity.deathTime + pPartialTicks - 1.0F) / 20.0F - 1.6F, MathUtils.EasingType.easeOutBack));
        deathTime = Float.isNaN(deathTime) ? 0.0F : deathTime;
        if (pEntity.deathTime == 0) {
            deathTime = 0;
        }
        if (deathTime > 1.0F) {
            deathTime = 1.0F;
        }
        deathTime -= deathTime;

        float scaleFactor = 0.7F;
        float horizontalScale = MathUtils.mapRange(0, 1, 1, 1 / scaleFactor, deathTime);
        pMatrixStack.pushPose();
        pMatrixStack.scale(horizontalScale, MathUtils.mapRange(0, 1, scaleFactor, 1, MathUtils.invert(deathTime) - -1), horizontalScale);
        super.render(pEntity, pEntityYaw, pPartialTicks, pMatrixStack, pBuffer, pPackedLight);
        //Clinker.LOGGER.info(deathTime);
        pMatrixStack.popPose();
        */
        for (int leg = 0; leg < 4; leg++) {
            Vec3 legPosition = pEntity.getLegPosition(leg, 1);
            Vec3 footPosition = pEntity.getFootPosition(leg, 1);
            Vec3 difference = legPosition.subtract(footPosition);

            float legLength = (float) difference.length();
            Vec3 legOrientation = difference.normalize();

            float pitch = (float) Math.asin(-legOrientation.y());
            float yaw = (float) Math.atan2(legOrientation.x(), legOrientation.z());
            float roll = 0.0F;
            this.getModel().setLegRotation(leg, pitch, yaw, roll, legLength);
        }

        super.render(pEntity, pEntityYaw, pPartialTicks, pMatrixStack, pBuffer, pPackedLight);
    }

    public static void drawDebugCube(VertexConsumer bufferbuilder, float x, float y, float z, float size, float r, float g, float b, float lineWidth) {
        float cubeRadius = size / 2.0F;

        RenderSystem.enableDepthTest();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        RenderSystem.disableTexture();
        RenderSystem.disableBlend();
        RenderSystem.lineWidth(lineWidth);

        bufferbuilder.vertex(x + cubeRadius, y + cubeRadius, z + cubeRadius).color(r, g, b, 0.0F).normal(1, 0, 0).endVertex();
        bufferbuilder.vertex(x + cubeRadius, y - cubeRadius, z + cubeRadius).color(r, g, b, 0.0F).normal(1, 0, 0).endVertex();
        bufferbuilder.vertex(x + cubeRadius, y - cubeRadius, z - cubeRadius).color(r, g, b, 0.0F).normal(1, 0, 0).endVertex();
        bufferbuilder.vertex(x + cubeRadius, y + cubeRadius, z - cubeRadius).color(r, g, b, 0.0F).normal(1, 0, 0).endVertex();

        bufferbuilder.vertex(x + cubeRadius, y + cubeRadius, z + cubeRadius).color(r, g, b, 0.0F).normal(0, 1, 0).endVertex();
        bufferbuilder.vertex(x - cubeRadius, y + cubeRadius, z + cubeRadius).color(r, g, b, 0.0F).normal(0, 1, 0).endVertex();
        bufferbuilder.vertex(x - cubeRadius, y + cubeRadius, z - cubeRadius).color(r, g, b, 0.0F).normal(0, 1, 0).endVertex();
        bufferbuilder.vertex(x + cubeRadius, y + cubeRadius, z - cubeRadius).color(r, g, b, 0.0F).normal(0, 1, 0).endVertex();

        bufferbuilder.vertex(x + cubeRadius, y + cubeRadius, z + cubeRadius).color(r, g, b, 0.0F).normal(0, 0, 1).endVertex();
        bufferbuilder.vertex(x - cubeRadius, y + cubeRadius, z + cubeRadius).color(r, g, b, 0.0F).normal(0, 0, 1).endVertex();
        bufferbuilder.vertex(x - cubeRadius, y - cubeRadius, z + cubeRadius).color(r, g, b, 0.0F).normal(0, 0, 1).endVertex();
        bufferbuilder.vertex(x + cubeRadius, y - cubeRadius, z + cubeRadius).color(r, g, b, 0.0F).normal(0, 0, 1).endVertex();

        Clinker.LOGGER.info("drew cube at " + x + ", " + y + ", " + z);
        bufferbuilder.vertex(x - cubeRadius, y - cubeRadius, z - cubeRadius).color(r, g, b, 0.0F).normal(-1, 0, 0).endVertex();
        bufferbuilder.vertex(x - cubeRadius, y + cubeRadius, z - cubeRadius).color(r, g, b, 0.0F).normal(-1, 0, 0).endVertex();
        bufferbuilder.vertex(x - cubeRadius, y + cubeRadius, z + cubeRadius).color(r, g, b, 0.0F).normal(-1, 0, 0).endVertex();
        bufferbuilder.vertex(x - cubeRadius, y - cubeRadius, z + cubeRadius).color(r, g, b, 0.0F).normal(-1, 0, 0).endVertex();

        bufferbuilder.vertex(x - cubeRadius, y - cubeRadius, z - cubeRadius).color(r, g, b, 0.0F).normal(0, 0, -1).endVertex();
        bufferbuilder.vertex(x + cubeRadius, y - cubeRadius, z - cubeRadius).color(r, g, b, 0.0F).normal(0, 0, -1).endVertex();
        bufferbuilder.vertex(x + cubeRadius, y + cubeRadius, z - cubeRadius).color(r, g, b, 0.0F).normal(0, 0, -1).endVertex();
        bufferbuilder.vertex(x - cubeRadius, y + cubeRadius, z - cubeRadius).color(r, g, b, 0.0F).normal(0, 0, -1).endVertex();

        bufferbuilder.vertex(x - cubeRadius, y - cubeRadius, z - cubeRadius).color(r, g, b, 0.0F).normal(0, -1, 0).endVertex();
        bufferbuilder.vertex(x + cubeRadius, y - cubeRadius, z - cubeRadius).color(r, g, b, 0.0F).normal(0, -1, 0).endVertex();
        bufferbuilder.vertex(x + cubeRadius, y - cubeRadius, z + cubeRadius).color(r, g, b, 0.0F).normal(0, -1, 0).endVertex();
        bufferbuilder.vertex(x - cubeRadius, y - cubeRadius, z + cubeRadius).color(r, g, b, 0.0F).normal(0, -1, 0).endVertex();

        RenderSystem.lineWidth(1.0F);
        RenderSystem.enableBlend();
        RenderSystem.enableTexture();
    }

    public ResourceLocation getTextureLocation(SeaHagEntity entity) {
        return SEA_HAG_LOCATION;
    }
}
