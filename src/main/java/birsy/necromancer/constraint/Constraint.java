package birsy.necromancer.constraint;

import birsy.necromancer.Skeleton;
import birsy.necromancer.SkeletonParent;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;

public interface Constraint {
    void initialize();

    void apply();

    boolean isSatisfied();

    boolean isIterative();

    default void renderDebugInfo(Skeleton skeleton, SkeletonParent parent, float pPartialTicks, PoseStack poseStack, MultiBufferSource pBuffer) {
        return;
    }
}
