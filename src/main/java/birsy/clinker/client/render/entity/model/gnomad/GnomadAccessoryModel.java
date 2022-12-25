package birsy.clinker.client.render.entity.model.gnomad;

import birsy.clinker.client.render.entity.model.base.DynamicModel;
import birsy.clinker.common.entity.gnomad.AbstractGnomadEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

public abstract class GnomadAccessoryModel<T extends EntityModel, E extends AbstractGnomadEntity> extends Model {
    public DynamicModel skeleton;
    public final ResourceLocation location;
    public GnomadAccessoryModel(ResourceLocation location) {
        super(RenderType::entityCutoutNoCull);
        this.location = location;
        this.skeleton = new DynamicModel(64, 32);
    }

    public abstract void render(T model, E entity, float partialTick, PoseStack pPoseStack, VertexConsumer pBuffer, int pPackedLight, int pPackedOverlay, float pRed, float pGreen, float pBlue, float pAlpha);

    @Override
    public void renderToBuffer(PoseStack pPoseStack, VertexConsumer pBuffer, int pPackedLight, int pPackedOverlay, float pRed, float pGreen, float pBlue, float pAlpha) {

    }
}
