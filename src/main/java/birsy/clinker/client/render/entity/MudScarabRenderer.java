package birsy.clinker.client.render.entity;

import birsy.clinker.client.render.entity.model.MudScarabModel;
import birsy.clinker.client.render.world.blockentity.VinePlantRenderer;
import birsy.clinker.common.entity.MudScarabEntity;
import birsy.clinker.core.Clinker;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;

public class MudScarabRenderer extends ClinkerEntityRenderer<MudScarabEntity, MudScarabModel<MudScarabEntity>> {
    private static final ResourceLocation SCARAB_LOCATION = new ResourceLocation(Clinker.MOD_ID, "textures/entity/bug/mud_scarab.png");

    public MudScarabRenderer(EntityRendererProvider.Context context) {
        super(context, new MudScarabModel<>(context.bakeLayer(MudScarabModel.LAYER_LOCATION)), 1.7F);
    }

    protected float getFlipDegrees(MudScarabEntity pLivingEntity) {
        return 180.0F;
    }


    @Override
    public void render(MudScarabEntity pEntity, float pEntityYaw, float pPartialTicks, PoseStack pMatrixStack, MultiBufferSource pBuffer, int pPackedLight) {
        //VinePlantRenderer.renderVine(pEntity.getPosition(pPartialTicks), Vec3.ZERO, pMatrixStack, pBuffer);
        super.render(pEntity, pEntityYaw, pPartialTicks, pMatrixStack, pBuffer, pPackedLight);
    }

    /**
     * Returns the location of an entity's texture.
     */
    public ResourceLocation getTextureLocation(MudScarabEntity entity) {
        return SCARAB_LOCATION;
    }
}
