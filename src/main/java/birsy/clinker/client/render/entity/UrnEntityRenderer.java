package birsy.clinker.client.render.entity;

import birsy.clinker.client.BezierCurve;
import birsy.clinker.client.model.entity.PresentSkeletonFactory;
import birsy.clinker.client.render.DebugRenderUtil;
import birsy.clinker.client.render.RenderUtils;
import birsy.clinker.client.render.entity.base.InterpolatedEntityRenderer;
import birsy.clinker.common.world.entity.UrnEntity;
import birsy.clinker.core.Clinker;
import birsy.clinker.core.util.VectorUtils;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.joml.AxisAngle4f;
import org.joml.Quaternionf;
import org.joml.Vector3d;
import org.joml.Vector3f;

public class UrnEntityRenderer extends InterpolatedEntityRenderer<UrnEntity, PresentSkeletonFactory.PresentSkeleton> {
    private static final ResourceLocation TEXTURE_LOCATION = new ResourceLocation(Clinker.MOD_ID, "textures/entity/present.png");

    public UrnEntityRenderer(EntityRendererProvider.Context context) {
        super(context, new PresentSkeletonFactory(), 0.5F);
    }

    @Override
    public void render(UrnEntity pEntity, float pEntityYaw, float pPartialTicks, PoseStack poseStack, MultiBufferSource pBuffer, int pPackedLight) {

        poseStack.pushPose();
        //poseStack.translate(0, 0.5, 0);

        Vector3f vector = pEntity.getAnimationVelocity(pPartialTicks).toVector3f();
        float velocityMagnitude = vector.length();
        vector = vector.cross(0, 1, 0).normalize();
        Quaternionf orientation = velocityMagnitude > 0.01 ? new Quaternionf(new AxisAngle4f(velocityMagnitude * 0.1F, vector)) : new Quaternionf();
        orientation.mul(new Quaternionf().rotateY(pEntityYaw));
        if (velocityMagnitude > 0.01) poseStack.mulPose(orientation);

        super.render(pEntity, pEntityYaw, pPartialTicks, poseStack, pBuffer, pPackedLight);

        poseStack.popPose();

        VertexConsumer consumer = pBuffer.getBuffer(this.getRenderType(pEntity));


        // render legs
        poseStack.pushPose();
        Vec3 cameraPos = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
        Vec3 position = pEntity.getPosition(pPartialTicks);
        float u1 = 57.0F / 64.0F;
        float u2 = 1.0F;
        float resolution = 12;
        float uvScale = 2.5F;
        float vScale = 2;
        for (UrnEntity.UrnFoot leg : pEntity.legs) {
            Vector3d startPos = VectorUtils.toJOML(leg.getStartPos(1.0F));
            Vector3d endPos = VectorUtils.toJOML(leg.getCurrentPosition(pPartialTicks)).sub(position.x(), position.y(), position.z());
            double verticalDistance = Math.abs(startPos.y() - endPos.y());
            Vector3d startControlPoint = new Vector3d(0, verticalDistance * -0.6, 0).add(startPos);
            Vector3d endControlPoint = VectorUtils.toJOML(leg.getCurrentNormal(pPartialTicks)).mul(verticalDistance * 0.6).add(endPos);

            BezierCurve curve = new BezierCurve(startPos, startControlPoint, endControlPoint, endPos);

            float distance = 0;
            for (float i = resolution - 1; i > -1; i--) {
                float previousT = (i + 1) / resolution;
                Vector3d previousPoint = curve.evaluate(previousT);
                float t = i / resolution;
                Vector3d point = curve.evaluate(t);
                float nextT = (i - 1) / resolution;
                Vector3d nextPoint = curve.evaluate(nextT);

                Vector3f tangent1 = previousPoint.sub(point, new Vector3d()).normalize().get(new Vector3f());
                Vector3f normal1 = previousPoint.add(position.x(), position.y(), position.z(), new Vector3d()).sub(cameraPos.x(), cameraPos.y(), cameraPos.z()).normalize().get(new Vector3f());
                Vector3f biTangent1 = normal1.cross(tangent1, new Vector3f()).normalize();

                Vector3f tangent2 = point.sub(nextPoint, new Vector3d()).normalize().get(new Vector3f());
                Vector3f normal2 = point.add(position.x(), position.y(), position.z(), new Vector3d()).sub(cameraPos.x(), cameraPos.y(), cameraPos.z()).normalize().get(new Vector3f());
                Vector3f biTangent2 = normal2.cross(tangent2, new Vector3f()).normalize();

                float distanceBetweenPoints = (float) previousPoint.distance(point);

                float v1 = 1 - ((distance / 16.0F) * (uvScale * vScale));
                float v2 = 1 - (((distance + distanceBetweenPoints) / 16.0F) * (uvScale * vScale));

                Vector3f normal = new Vector3f(0, 1, 0);

                RenderUtils.drawFaceBetweenPoints(consumer, poseStack, (6.0F / 16.0F) / uvScale, previousPoint.get(new Vector3f()), tangent1, normal, biTangent1, pPackedLight, OverlayTexture.NO_OVERLAY, u1, v1,
                        point.get(new Vector3f()), tangent2, normal, biTangent2, pPackedLight, OverlayTexture.NO_OVERLAY, u2, v2);
                distance += distanceBetweenPoints;
//                DebugRenderUtil.renderLine(poseStack, consumer, (float)previousPoint.x(), (float)previousPoint.y(), (float)previousPoint.z(),
//                                                                (float)point.x(), (float)point.y(), (float)point.z(),
//                        brightness, brightness, brightness, 1.0F, brightness2, brightness2, brightness2, 1.0F);
            }
        }

        poseStack.popPose();
    }

    @Override
    public RenderType getRenderType(UrnEntity entity) {
        return RenderType.entityCutoutNoCull(this.getTextureLocation(entity));
    }

    @Override
    public ResourceLocation getTextureLocation(UrnEntity entity) {
        return TEXTURE_LOCATION;
    }
}
