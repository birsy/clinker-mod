package birsy.clinker.client.render.entity.model.gnomad.armor;

import birsy.clinker.client.render.entity.model.gnomad.GnomadAccessoryModel;
import birsy.clinker.client.render.entity.model.gnomad.GnomadAxemanDynamicModel;
import birsy.clinker.common.world.entity.gnomad.GnomadAxemanEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.resources.ResourceLocation;

public class GnomadEmptyAccessory<T extends GnomadAxemanDynamicModel, E extends GnomadAxemanEntity> extends GnomadAccessoryModel<T, E> {
    public GnomadEmptyAccessory() {
        super(new ResourceLocation("empty"));
    }

    @Override
    public void render(T model, E entity, float partialTick, PoseStack pPoseStack, VertexConsumer pBuffer, int pPackedLight, int pPackedOverlay, float pRed, float pGreen, float pBlue, float pAlpha) {}
}
