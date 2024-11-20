package birsy.clinker.client.entity.mogul.layer;

import birsy.clinker.client.entity.mogul.MogulRenderer;
import birsy.clinker.client.entity.mogul.MogulSkeleton;
import birsy.clinker.client.necromancer.render.NecromancerEntityRenderLayer;
import birsy.clinker.common.world.entity.gnomad.GnomadMogulEntity;
import birsy.clinker.core.Clinker;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;

public class MogulRobesLayer extends NecromancerEntityRenderLayer<GnomadMogulEntity, MogulSkeleton> {
    private static final ResourceLocation MOGUL_ROBES_LOCATION = new ResourceLocation(Clinker.MOD_ID, "textures/entity/gnomad/mogul/gnomad_mogul_robes.png");

    public MogulRobesLayer(MogulRenderer pRenderer) {
        super(pRenderer);
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight, GnomadMogulEntity entity, MogulSkeleton skeleton, float partialTicks) {
        Vec3 robeColor = Vec3.fromRGB24(entity.getRobeColor());
        this.renderer.renderSkin(
                entity,
                skeleton,
                this.renderer.getSkin(entity),
                entity.tickCount,
                partialTicks,
                poseStack,
                buffer.getBuffer(RenderType.entityCutoutNoCullZOffset(MOGUL_ROBES_LOCATION)),
                packedLight, LivingEntityRenderer.getOverlayCoords(entity, 0),
                (float) robeColor.x, (float) robeColor.y, (float) robeColor.z, 1.0F
        );
    }
}