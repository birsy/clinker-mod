package birsy.clinker.client.render.entity;

import birsy.clinker.client.model.entity.MudScarabModelFactory;
import birsy.clinker.client.render.entity.base.InterpolatedEntityRenderer;
import birsy.clinker.common.world.entity.MudScarabEntity;
import birsy.clinker.core.Clinker;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;

public class MudScarabRenderer extends InterpolatedEntityRenderer<MudScarabEntity, MudScarabModelFactory.MudScarabModel> {
    private static final ResourceLocation SCARAB_LOCATION = new ResourceLocation(Clinker.MOD_ID, "textures/entity/bug/mud_scarab.png");

    public MudScarabRenderer(EntityRendererProvider.Context context) {
        super(context, new MudScarabModelFactory(), 1.7F);
    }

    protected float getFlipDegrees(MudScarabEntity pLivingEntity) {
        return 180.0F;
    }

    @Override
    public void render(MudScarabEntity pEntity, float pEntityYaw, float pPartialTicks, PoseStack pMatrixStack, MultiBufferSource pBuffer, int pPackedLight) {
//        Vec3 normal = pEntity.getNormal(pPartialTicks).lerp(new Vec3(0, 1, 0), 0.25F);
//        float height = pEntity.getHeight(pPartialTicks);
//        height += (1.0 - normal.dot(new Vec3(0, 1, 0)));
//        pMatrixStack.translate(0, height - pEntity.getPosition(pPartialTicks).y(), 0);
//        pMatrixStack.mulPose(Vector3f.ZP.rotation((float) -Math.atan2(normal.x(), normal.y())));
//        pMatrixStack.mulPose(Vector3f.XP.rotation((float) Math.atan2(normal.z(), normal.y())));
        super.render(pEntity, pEntityYaw, pPartialTicks, pMatrixStack, pBuffer, pPackedLight);
    }

    public RenderType getRenderType(MudScarabEntity entity) {
        return RenderType.entityCutoutNoCull(this.getTextureLocation(entity));
    }

    /**
     * Returns the location of an entity's texture.
     */
    public ResourceLocation getTextureLocation(MudScarabEntity entity) {
        return SCARAB_LOCATION;
    }
}
