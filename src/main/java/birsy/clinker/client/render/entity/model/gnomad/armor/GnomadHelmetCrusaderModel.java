package birsy.clinker.client.render.entity.model.gnomad.armor;

import birsy.clinker.client.render.entity.model.base.DynamicModelPart;
import birsy.clinker.client.render.entity.model.gnomad.GnomadAccessoryModel;
import birsy.clinker.client.render.entity.model.gnomad.GnomadAxemanDynamicModel;
import birsy.clinker.common.world.entity.gnomad.OldGnomadAxemanEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.resources.ResourceLocation;

public class GnomadHelmetCrusaderModel<T extends GnomadAxemanDynamicModel, E extends OldGnomadAxemanEntity> extends GnomadAccessoryModel<T, E> {
    public DynamicModelPart gnomadHelmet;

    public GnomadHelmetCrusaderModel(ResourceLocation location) {
        super(location);
        this.gnomadHelmet = new DynamicModelPart(this.skeleton, 0, 0);
        this.gnomadHelmet.setInitialPosition(0.0F, 1.5F, -1.5F);
        this.gnomadHelmet.addCube(-4.5F, -5.0F, -6.0F, 9.0F, 10.0F, 9.0F, 0.0F, 0.25F, 0.0F);

    }

    @Override
    public void render(GnomadAxemanDynamicModel model, OldGnomadAxemanEntity entity, float partialTick, PoseStack pPoseStack, VertexConsumer pBuffer, int pPackedLight, int pPackedOverlay, float pRed, float pGreen, float pBlue, float pAlpha) {
        model.gnomadHead.setScale(0.9F, 0.9F, 0.9F);
        model.gnomadHead.getGlobalTransForm(pPoseStack);
        this.gnomadHelmet.render(pPoseStack, pBuffer, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue, pAlpha);
    }
}
