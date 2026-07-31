package birsy.clinker.client.entity.layer;

import birsy.clinker.client.AnimationUtilities;
import birsy.clinker.client.render.utilities.DebugRenderUtil;
import birsy.clinker.common.entity.GroundLocomotionEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import foundry.veil.api.client.necromancer.Skeleton;
import foundry.veil.api.client.necromancer.SkeletonParent;
import foundry.veil.api.client.necromancer.render.NecromancerEntityRenderLayer;
import foundry.veil.api.client.necromancer.render.NecromancerEntityRenderer;
import foundry.veil.api.client.necromancer.render.NecromancerRenderer;
import foundry.veil.api.client.render.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.Mth;
import org.joml.Vector3fc;

import java.util.function.Function;

public class DebugSurveyorWheelRenderer<E extends GroundLocomotionEntity & SkeletonParent<E, S>, S extends Skeleton> extends NecromancerEntityRenderLayer<E, S> {
    final Function<E, AnimationUtilities.SurveyorWheel> wheelSupplier;

    public DebugSurveyorWheelRenderer(NecromancerEntityRenderer<E, S> renderer, Function<E, AnimationUtilities.SurveyorWheel> wheelSupplier) {
        super(renderer);
        this.wheelSupplier = wheelSupplier;
    }

    @Override
    public void render(E parent, S skeleton, NecromancerRenderer renderer, MatrixStack matrixStack, int packedLight, float partialTicks) {
        if (!Minecraft.getInstance().getEntityRenderDispatcher().shouldRenderHitBoxes()) return;
        AnimationUtilities.SurveyorWheel wheel = wheelSupplier.apply(parent);

        float radius = wheel.radius(partialTicks);
        float angle = wheel.angle(partialTicks);

        // draw the wheel
        matrixStack.matrixPush();
        matrixStack.applyScale(radius * 16);
        matrixStack.translate(0, 1, 0);

        Vector3fc movementDirection = parent.getLocomotionVectorForAnimation();
        float movementDirLength = movementDirection.length();
        float yaw = (float) Mth.atan2(movementDirection.z() / movementDirLength, movementDirection.x() / movementDirLength);
        matrixStack.rotate(-yaw + Mth.PI, 0, 1, 0);
        matrixStack.rotate(angle, 0, 0, 1);

        PoseStack poseStack = matrixStack.toPoseStack();
        VertexConsumer lineBuffer = renderer.getBuffer(RenderType.lines());

        int segments = 16;
        for (int i = 0; i < segments; i++) {
            float pVertexAngle = ((i + 0.0F) / segments) * Mth.TWO_PI;
            float nVertexAngle = ((i + 1.0F) / segments) * Mth.TWO_PI;

            float pX = Mth.sin(pVertexAngle), pY = Mth.cos(pVertexAngle);
            float nX = Mth.sin(nVertexAngle), nY = Mth.cos(nVertexAngle);

            DebugRenderUtil.renderLine(
                    poseStack,
                    lineBuffer,
                    pX, pY, 0,
                    nX, nY, 0,
                    1F, 1F, 1F, 0.5F
            );

            if (i % 4 == 0) {
                DebugRenderUtil.renderLine(
                        poseStack,
                        lineBuffer,
                        pX, pY, 0,
                        pX * 0.7, pY * 0.7, 0,
                        1F, 0F, 0F, 0.5F
                );
            }
        }
        matrixStack.matrixPop();
    }
}
