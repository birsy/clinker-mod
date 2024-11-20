package birsy.clinker.client.necromancer.render;

import birsy.clinker.client.necromancer.Skeleton;
import birsy.clinker.client.necromancer.SkeletonParent;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.Entity;

public abstract class NecromancerEntityRenderLayer<T extends Entity & SkeletonParent, M extends Skeleton<T>> {
    public NecromancerEntityRenderer<T, M> renderer;

    public NecromancerEntityRenderLayer(NecromancerEntityRenderer<T, M> pRenderer) {
        this.renderer = pRenderer;
    }

    public abstract void render(PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight, T pLivingEntity, M pSkeleton, float pPartialTicks);
}
