package birsy.clinker.client.render.entity.base;

import birsy.clinker.client.model.base.InterpolatedSkeleton;
import birsy.clinker.client.model.base.InterpolatedSkeletonParent;
import birsy.clinker.client.model.base.ModelFactory;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.LivingEntity;

public abstract class EntityRenderLayer<T extends LivingEntity & InterpolatedSkeletonParent, M extends InterpolatedSkeleton> {
    public InterpolatedEntityRenderer<T, M> renderer;

    public EntityRenderLayer(InterpolatedEntityRenderer<T, M> pRenderer) {
        this.renderer = pRenderer;
    }

    public abstract void render(PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight, T pLivingEntity, float pPartialTicks);

    protected ModelFactory getModelFactory() {
        return this.renderer.modelFactory;
    }
}
