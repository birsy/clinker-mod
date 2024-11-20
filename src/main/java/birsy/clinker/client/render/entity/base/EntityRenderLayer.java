package birsy.clinker.client.render.entity.base;

import birsy.clinker.client.necromancer.Skeleton;
import birsy.clinker.client.necromancer.SkeletonParent;
import birsy.clinker.client.necromancer.RenderFactory;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.LivingEntity;

public abstract class EntityRenderLayer<T extends LivingEntity & SkeletonParent, M extends Skeleton> {
    public InterpolatedEntityRenderer<T, M> renderer;

    public EntityRenderLayer(InterpolatedEntityRenderer<T, M> pRenderer) {
        this.renderer = pRenderer;
    }

    public abstract void render(PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight, T pLivingEntity, M pSkeleton, float pPartialTicks);

    protected RenderFactory getModelFactory() {
        return this.renderer.modelFactory;
    }
}
