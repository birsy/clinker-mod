package birsy.clinker.client.render.entity;

import birsy.clinker.client.render.entity.model.SeaHagModel;
import birsy.clinker.client.render.entity.model.base.AnimFunctions;
import birsy.clinker.common.world.entity.SeaHagEntity;
import birsy.clinker.core.Clinker;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

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
        super.runShaders(pEntity, pEntityYaw, pPartialTicks, pMatrixStack, pBuffer, pPackedLight);
        //Clinker.LOGGER.info(deathTime);
        pMatrixStack.popPose();
        */
        for (int leg = 0; leg < 4; leg++) {
            //Vec3 legPosition = pEntity.getLegPosition(leg, pPartialTicks);
            Vec3 legPosition = AnimFunctions.getWorldPos(pEntity, this.getModel().legs[leg], pPartialTicks).multiply(1, 1, 1);
            //Clinker.LOGGER.info(legPosition);
            //Vec3 footPosition = pEntity.getPosition(pPartialTicks).subtract(pEntity.getFootPosition(leg, pPartialTicks)).scale(-1).add(pEntity.getPosition(pPartialTicks));
            Vec3 fp = pEntity.getFootPosition(leg, pPartialTicks);
            Vec3 footPosition = fp.subtract(pEntity.getPosition(pPartialTicks)).multiply(-1, -1, -1).add(pEntity.getPosition(pPartialTicks));

            Vec3 localLegPos = pEntity.getPosition(pPartialTicks).subtract(legPosition);
            Vec3 localFootPos = pEntity.getPosition(pPartialTicks).subtract(footPosition);
            Vec3 difference = localLegPos.subtract(localFootPos);


            float legLength = (float) difference.length();

            Vec3 legOrientation = difference.normalize();

            float pitch = (float) Math.asin(-legOrientation.y());
            float yaw = (float) Math.atan2(legOrientation.x(), legOrientation.z());
            float roll = 0.0F;
            float offset = 1.5708F;
            this.getModel().setLegRotation(leg, 0, 0, 0, legLength);


            float r = 0.0F, g = 0.0F, b = 0.0F;

            if (leg == 0) {
                r = 1.0F;
            } else if (leg == 1) {
                r = 1.0F;
                g = 1.0F;
                b = 1.0F;
            } else if (leg == 2) {
                b = 1.0F;
            } else {
                g = 1.0F;
            }

            if (Minecraft.getInstance().getEntityRenderDispatcher().shouldRenderHitBoxes()) {
                double radius = 0.25;
                Vec3 boxCenter1 = localLegPos;
                Vec3 min = boxCenter1.subtract(radius, radius, radius);
                Vec3 max = boxCenter1.add(radius, radius, radius);
                LevelRenderer.renderLineBox(pMatrixStack, pBuffer.getBuffer(RenderType.LINES), min.x, min.y, min.z, max.x, max.y, max.z, r, g, b, 1.0F);

                Vec3 boxCenter2 = localFootPos;
                min = boxCenter2.subtract(radius, radius, radius);
                max = boxCenter2.add(radius, radius, radius);
                LevelRenderer.renderLineBox(pMatrixStack, pBuffer.getBuffer(RenderType.LINES), min.x, min.y, min.z, max.x, max.y, max.z, r, g, b, 1.0F);
                renderLine(pMatrixStack, pBuffer.getBuffer(RenderType.LINES), boxCenter1.x(), boxCenter1.y(), boxCenter1.z(), boxCenter2.x(), boxCenter2.y(), boxCenter2.z(), r, g, b, 0.8F);
                Vec3 legPos = pEntity.getLegPosition(leg, pPartialTicks);
                Vec3 footPos = pEntity.getDesiredFootPosition(leg, pPartialTicks).subtract(0, 3, 0);

                radius = 0.4;
                float brightness = 0.1F;
                boxCenter1 = pEntity.getPosition(pPartialTicks).subtract(footPos).multiply(-1, -1, -1);
                min = boxCenter1.subtract(radius, radius, radius);
                max = boxCenter1.add(radius, radius, radius);
                LevelRenderer.renderLineBox(pMatrixStack, pBuffer.getBuffer(RenderType.LINES), min.x, min.y, min.z, max.x, max.y, max.z, r * brightness, g * brightness, b * brightness, 1.0F);

                brightness = 0.5F;
                boxCenter2 = pEntity.getPosition(pPartialTicks).subtract(legPos).multiply(-1, -1, -1);
                min = boxCenter2.subtract(radius, radius, radius);
                max = boxCenter2.add(radius, radius, radius);
                LevelRenderer.renderLineBox(pMatrixStack, pBuffer.getBuffer(RenderType.LINES), min.x, min.y, min.z, max.x, max.y, max.z, r * brightness, g * brightness, b * brightness, 1.0F);
                renderLine(pMatrixStack, pBuffer.getBuffer(RenderType.LINES), boxCenter1.x(), boxCenter1.y(), boxCenter1.z(), boxCenter2.x(), boxCenter2.y(), boxCenter2.z(), r * brightness, g * brightness, b * brightness, 0.8F);
            }
        }

        super.render(pEntity, pEntityYaw, pPartialTicks, pMatrixStack, pBuffer, pPackedLight);
    }

    public static void renderLine(PoseStack pPoseStack, VertexConsumer pConsumer, double pMinX, double pMinY, double pMinZ, double pMaxX, double pMaxY, double pMaxZ, float pRed, float pGreen, float pBlue, float pAlpha) {
        Matrix4f matrix4f = pPoseStack.last().pose();
        Matrix3f matrix3f = pPoseStack.last().normal();
        float minX = (float)pMinX;
        float minY = (float)pMinY;
        float minZ = (float)pMinZ;
        float maxX = (float)pMaxX;
        float maxY = (float)pMaxY;
        float maxZ = (float)pMaxZ;

        pConsumer.vertex(matrix4f, minX, minY, minZ).color(pRed, pGreen, pBlue, pAlpha).normal(matrix3f, 1.0F, 0.0F, 0.0F).endVertex();
        pConsumer.vertex(matrix4f, maxX, maxY, maxZ).color(pRed, pGreen, pBlue, pAlpha).normal(matrix3f, 1.0F, 0.0F, 0.0F).endVertex();
        pConsumer.vertex(matrix4f, minX, minY, minZ).color(pRed, pGreen, pBlue, pAlpha).normal(matrix3f, 0.0F, 1.0F, 0.0F).endVertex();
        pConsumer.vertex(matrix4f, maxX, maxY, maxZ).color(pRed, pGreen, pBlue, pAlpha).normal(matrix3f, 0.0F, 1.0F, 0.0F).endVertex();
    }

    public ResourceLocation getTextureLocation(SeaHagEntity entity) {
        return SEA_HAG_LOCATION;
    }
}
