package birsy.clinker.client.render.entity.layer;

import birsy.clinker.client.render.entity.model.gnomad.GnomadAccessoryModel;
import birsy.clinker.client.render.entity.model.gnomad.GnomadAxemanDynamicModel;
import birsy.clinker.client.render.entity.model.gnomad.armor.*;
import birsy.clinker.common.world.entity.gnomad.GnomadArmor;
import birsy.clinker.common.world.entity.gnomad.GnomadAxemanEntity;
import birsy.clinker.core.Clinker;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.Util;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.HashMap;

@OnlyIn(Dist.CLIENT)
public class GnomadArmorLayer<T extends GnomadAxemanEntity, M extends GnomadAxemanDynamicModel<T>> extends RenderLayer<T, M> {
    private static final HashMap<GnomadArmor, GnomadAccessoryModel> armorModels = Util.make(new HashMap<>(), (map) -> {
        for (GnomadArmor armor : GnomadArmor.values()) {
            map.put(armor, new GnomadEmptyAccessory());
        }

        map.put(GnomadArmor.HELMET_HAT, new GnomadHelmetHatModel(getArmorPath(GnomadArmor.HELMET_HAT.getResourceName())));
        map.put(GnomadArmor.HELMET_BRIM, new GnomadHelmetBrimModel(getArmorPath(GnomadArmor.HELMET_BRIM.getResourceName())));
        map.put(GnomadArmor.HELMET_SOLDIER, new GnomadHelmetSoldierModel(getArmorPath(GnomadArmor.HELMET_SOLDIER.getResourceName())));
        map.put(GnomadArmor.HELMET_SOLDIER_VISOR, new GnomadHelmetSoldierVisorModel(getArmorPath(GnomadArmor.HELMET_SOLDIER_VISOR.getResourceName())));
        map.put(GnomadArmor.HELMET_PLATE, new GnomadHelmetPlateModel(getArmorPath(GnomadArmor.HELMET_PLATE.getResourceName())));
        map.put(GnomadArmor.HELMET_CRUSADER, new GnomadHelmetCrusaderModel(getArmorPath(GnomadArmor.HELMET_CRUSADER.getResourceName())));
        map.put(GnomadArmor.HELMET_SKULL, new GnomadHelmetSkullModel(getArmorPath(GnomadArmor.HELMET_SKULL.getResourceName())));
    });


    public GnomadArmorLayer(RenderLayerParent<T, M> pRenderer) {
        super(pRenderer);
    }

    @Override
    public void render(PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight, T pLivingEntity, float pLimbSwing, float pLimbSwingAmount, float pPartialTick, float pAgeInTicks, float pNetHeadYaw, float pHeadPitch) {
        for (GnomadArmor.GnomadArmorLocation location : GnomadArmor.GnomadArmorLocation.values()) {
            GnomadArmor armorType = pLivingEntity.getArmor(location);
            GnomadAccessoryModel armorModel = armorModels.get(armorType);
            VertexConsumer consumer = pBuffer.getBuffer(RenderType.entityCutoutNoCull(armorModel.location));
            pPoseStack.pushPose();
            armorModel.render(this.getParentModel(), pLivingEntity, pPartialTick, pPoseStack, consumer, pPackedLight, LivingEntityRenderer.getOverlayCoords(pLivingEntity, 0.0F), 1.0F, 1.0F, 1.0F, 1.0F);
            pPoseStack.popPose();
        }
    }

    private static ResourceLocation getArmorPath(String armorName) {
        return new ResourceLocation(Clinker.MOD_ID, "textures/entity/gnomad/axeman/armor/" + armorName + ".png");
    }
}
