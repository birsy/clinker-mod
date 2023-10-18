package birsy.clinker.client.render.entity.base;

import birsy.clinker.client.model.base.AnimationProperties;
import birsy.clinker.client.model.base.InterpolatedSkeleton;
import birsy.clinker.client.model.base.InterpolatedSkeletonParent;
import birsy.clinker.client.model.base.SkeletonFactory;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.LivingEntity;

public abstract class EntityRenderLayer<T extends LivingEntity & InterpolatedSkeletonParent, M extends InterpolatedSkeleton> {
    public InterpolatedEntityRenderer<T, M> renderer;

    public EntityRenderLayer(InterpolatedEntityRenderer<T, M> pRenderer) {
        this.renderer = pRenderer;
    }

    public abstract void render(PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight, T pLivingEntity, M pSkeleton, float pPartialTicks);

    protected SkeletonFactory getModelFactory() {
        return this.renderer.modelFactory;
    }
}
