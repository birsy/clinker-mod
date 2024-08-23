package birsy.clinker.client.render.entity;

import birsy.clinker.client.model.entity.FrogNoMoreSkeletonFactory;
import birsy.clinker.client.render.DebugRenderUtil;
import birsy.clinker.client.render.entity.base.InterpolatedEntityRenderer;
import birsy.clinker.common.world.entity.FrogNoMoreEntity;
import birsy.clinker.core.Clinker;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;

public class FrogNoMoreRenderer extends InterpolatedEntityRenderer<FrogNoMoreEntity, FrogNoMoreSkeletonFactory.FrogNoMoreModel> {
    private static final ResourceLocation FROG_NO_MORE_LOCATION = new ResourceLocation(Clinker.MOD_ID, "textures/entity/frog_no_more.png");

    public FrogNoMoreRenderer(EntityRendererProvider.Context context) {
        super(context, new FrogNoMoreSkeletonFactory(), 1.7F);
    }

    @Override
    public void render(FrogNoMoreEntity pEntity, float pEntityYaw, float pPartialTicks, PoseStack poseStack, MultiBufferSource pBuffer, int pPackedLight) {
        Vec3 tailPos = pEntity.tail.pPosition.lerp(pEntity.tail.position, pPartialTicks).subtract(pEntity.getPosition(pPartialTicks));
        DebugRenderUtil.renderSphere(poseStack, pBuffer.getBuffer(RenderType.LINES), 16, 0.5F, tailPos.x, tailPos.y + 0.5, tailPos.z, 1.0F, 1.0F, 1.0F, 1.0F);
        super.render(pEntity, pEntityYaw, pPartialTicks, poseStack, pBuffer, pPackedLight);
    }

    public RenderType getRenderType(FrogNoMoreEntity entity) {
        return RenderType.entityCutoutNoCull(this.getTextureLocation(entity));
    }

    /**
     * Returns the location of an entity's texture.
     */
    public ResourceLocation getTextureLocation(FrogNoMoreEntity entity) {
        return FROG_NO_MORE_LOCATION;
    }
}
