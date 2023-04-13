package birsy.clinker.client.render.entity.model.gnomad.armor;

import birsy.clinker.client.render.entity.model.base.DynamicModelPart;
import birsy.clinker.client.render.entity.model.gnomad.GnomadAxemanDynamicModel;
import birsy.clinker.common.world.entity.gnomad.GnomadAxemanEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.resources.ResourceLocation;

public class GnomadHelmetSoldierModel extends GnomadHelmetHatModel {
    public DynamicModelPart gnomadHelmet;

    public GnomadHelmetSoldierModel(ResourceLocation location) {
        super(location);
        this.gnomadHelmet = new DynamicModelPart(this.skeleton, 0, 8);
        this.gnomadHelmet.setInitialPosition(0.0F, 1.5F, -1.5F);
        this.gnomadHelmet.addCube(-4.0F, -5.0F, -6.0F, 8.0F, 5.0F, 6.0F, 0.25F, 0.25F, 0.25F);
    }

    @Override
    public void render(GnomadAxemanDynamicModel model, GnomadAxemanEntity entity, float partialTick, PoseStack pPoseStack, VertexConsumer pBuffer, int pPackedLight, int pPackedOverlay, float pRed, float pGreen, float pBlue, float pAlpha) {
        super.render(model, entity, partialTick, pPoseStack, pBuffer, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue, pAlpha);
        this.gnomadHelmet.render(pPoseStack, pBuffer, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue, pAlpha);
    }
}
