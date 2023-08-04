package birsy.clinker.client.render.entity.model.gnomad.armor;

import birsy.clinker.client.render.entity.model.base.AnimFunctions;
import birsy.clinker.client.render.entity.model.base.DynamicModelPart;
import birsy.clinker.client.render.entity.model.gnomad.GnomadAccessoryModel;
import birsy.clinker.client.render.entity.model.gnomad.GnomadAxemanDynamicModel;
import birsy.clinker.common.world.entity.gnomad.GnomadAxemanEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.resources.ResourceLocation;

public class GnomadHelmetBrimModel<T extends GnomadAxemanDynamicModel, E extends GnomadAxemanEntity> extends GnomadAccessoryModel<T, E> {
    public DynamicModelPart gnomadHat;
    public DynamicModelPart gnomadHatBrim;

    public GnomadHelmetBrimModel(ResourceLocation location) {
        super(location);
        this.gnomadHat = new DynamicModelPart(this.skeleton, 0, 0);
        this.gnomadHat.setInitialPosition(0.0F, -2.0F, -3.5F);
        this.gnomadHat.addCube(-4.0F, -3.0F, -4.0F, 8.0F, 3.0F, 8.0F, 0.0F, 0.0F, 0.0F);
        this.gnomadHat.setInitialRotation( 0.01688540150551102F, 0.0F, 0.01688540150551102F);

        this.gnomadHatBrim = new DynamicModelPart(this.skeleton, 0, 11);
        this.gnomadHatBrim.setInitialPosition(0.0F, 0.0F, 0.0F);
        this.gnomadHatBrim.addCube(-6.0F, -0.5F, -6.0F, 12.0F, 1.0F, 12.0F, 0.0F, -0.1F, 0.0F);
        this.gnomadHatBrim.setInitialRotation( 0.0F, 0.0F, 0.0F);

        this.gnomadHat.addChild(this.gnomadHatBrim);
    }

    @Override
    public void render(T model, E entity, float partialTick, PoseStack pPoseStack, VertexConsumer pBuffer, int pPackedLight, int pPackedOverlay, float pRed, float pGreen, float pBlue, float pAlpha) {
        this.gnomadHat.resetPose();
        model.gnomadHead.getGlobalTransForm(pPoseStack);
        float ageInTicks = (entity.tickCount + partialTick) * 0.1F;
        AnimFunctions.swing(this.gnomadHat, 1.6F, 0.0625F, false, 1F, 0, ageInTicks, 0.5F, AnimFunctions.Axis.X);
        AnimFunctions.swing(this.gnomadHat, 1.6F, 0.0625F, false, 1F, 0, ageInTicks, 0.5F, AnimFunctions.Axis.Z);
        this.gnomadHat.render(pPoseStack, pBuffer, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue, pAlpha);
    }
}