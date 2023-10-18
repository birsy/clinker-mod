package birsy.clinker.client.model.base.constraint;

import birsy.clinker.client.model.base.InterpolatedSkeleton;
import birsy.clinker.client.model.base.InterpolatedSkeletonParent;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;

public interface Constraint {
    void initialize();

    void apply();

    boolean isSatisfied();

    boolean isIterative();

    default void renderDebugInfo(InterpolatedSkeleton skeleton, InterpolatedSkeletonParent parent, float pPartialTicks, PoseStack poseStack, MultiBufferSource pBuffer) {
        return;
    }
}
