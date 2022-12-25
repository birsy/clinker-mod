package birsy.clinker.client.render.entity.model.gnomad.armor;

import birsy.clinker.client.render.entity.model.base.AnimFunctions;
import birsy.clinker.client.render.entity.model.base.DynamicModelPart;
import birsy.clinker.client.render.entity.model.gnomad.GnomadAccessoryModel;
import birsy.clinker.client.render.entity.model.gnomad.GnomadAxemanDynamicModel;
import birsy.clinker.common.entity.gnomad.GnomadAxemanEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.resources.ResourceLocation;

public class GnomadHelmetPlateModel<T extends GnomadAxemanDynamicModel, E extends GnomadAxemanEntity> extends GnomadAccessoryModel<T, E> {
    public DynamicModelPart gnomadHelmet;
    public DynamicModelPart gnomadPlate;
    public DynamicModelPart gnomadHat;

    public GnomadHelmetPlateModel(ResourceLocation location) {
        super(location);
        this.gnomadHelmet = new DynamicModelPart(this.skeleton, 24, 0);
        this.gnomadHelmet.setInitialPosition(0.0F, 1.5F, -1.5F);
        this.gnomadHelmet.addCube(-4.0F, -5.0F, -6.0F, 8.0F, 5.0F, 6.0F, 0.25F, 0.25F, 0.25F);

        this.gnomadPlate = new DynamicModelPart(this.skeleton, 24, 0);
        this.gnomadPlate.setInitialPosition(0.0F, 0.0F, -1.5F);
        this.gnomadPlate.addCube(-4.0F, -4.5F, -2.0F, 8.0F, 9.0F, 2.0F, 0.25F, 0.25F, 0.25F);

        this.gnomadHat = new DynamicModelPart(this.skeleton, 0, 0);
        this.gnomadHat.setInitialPosition(0.0F, -3.0F, -3.5F);
        this.gnomadHat.addCube(-3.0F, -3.5F, -2.5F, 6.0F, 3.0F, 6.0F, 0.0F, 0.0F, 0.0F);
        this.gnomadHat.setInitialRotation( 0.01688540150551102F, 0.0F, 0.01688540150551102F);

        this.gnomadHelmet.addChild(this.gnomadPlate);
    }

    @Override
    public void render(GnomadAxemanDynamicModel model, GnomadAxemanEntity entity, float partialTick, PoseStack pPoseStack, VertexConsumer pBuffer, int pPackedLight, int pPackedOverlay, float pRed, float pGreen, float pBlue, float pAlpha) {
        this.gnomadHat.resetPose();
        model.gnomadHead.getGlobalTransForm(pPoseStack);
        float ageInTicks = (entity.tickCount + partialTick) * 0.1F;
        AnimFunctions.swing(this.gnomadHat, 1.6F, 0.0625F, false, 1F, 0, ageInTicks, 0.5F, AnimFunctions.Axis.X);
        AnimFunctions.swing(this.gnomadHat, 1.6F, 0.0625F, false, 1F, 0, ageInTicks, 0.5F, AnimFunctions.Axis.Z);
        this.gnomadHat.render(pPoseStack, pBuffer, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue, pAlpha);
        this.gnomadHelmet.render(pPoseStack, pBuffer, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue, pAlpha);
    }
}
