package birsy.clinker.client.entity;

import birsy.clinker.common.entity.projectile.ShotItemEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class ShotItemRenderer extends EntityRenderer<ShotItemEntity> {
    private final ItemRenderer itemRenderer;

    public ShotItemRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.itemRenderer = context.getItemRenderer();
    }

    @Override
    public void render(ShotItemEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        if (this.entityRenderDispatcher.camera.getEntity().getEyePosition(partialTicks).distanceToSqr(entity.getEyePosition(partialTicks)) >= (0.5 * 0.5)) {
            poseStack.pushPose();
            Vector3f axis = entity.getAxis();
            Quaternionf rotation = new Quaternionf()
                    .rotationTo(0, 0, -1, axis.x, axis.y, axis.z)
                    .rotateZ(entity.getRoll(partialTicks));

            poseStack.translate(0, 4 / 16F, 0);
            poseStack.mulPose(rotation);
            poseStack.mulPose(Axis.XN.rotationDegrees(10F));
            poseStack.scale(0.8F, 0.8F, 0.8F);
            this.itemRenderer
                    .renderStatic(
                            entity.getItem(),
                            ItemDisplayContext.FIXED,
                            packedLight,
                            OverlayTexture.NO_OVERLAY,
                            poseStack,
                            buffer,
                            entity.level(),
                            entity.getId()
                    );
            poseStack.popPose();
            super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
        }
    }


    @Override
    public ResourceLocation getTextureLocation(ShotItemEntity entity) {
        return TextureAtlas.LOCATION_BLOCKS;
    }
}
