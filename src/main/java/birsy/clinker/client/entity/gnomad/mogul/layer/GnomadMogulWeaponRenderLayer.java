package birsy.clinker.client.entity.gnomad.mogul.layer;

import birsy.clinker.client.entity.SingleBoneSkeleton;
import birsy.clinker.client.entity.gnomad.mogul.GnomadMogulRenderer;
import birsy.clinker.client.entity.gnomad.mogul.GnomadMogulSkeleton;
import birsy.clinker.client.entity.gnomad.mogul.GnomadMogulWeaponSkins;
import birsy.clinker.common.world.entity.gnomad.mogul.GnomadMogulEntity;
import foundry.veil.api.client.necromancer.Skeleton;
import foundry.veil.api.client.necromancer.render.*;
import foundry.veil.api.client.render.MatrixStack;
import foundry.veil.api.client.render.rendertype.VeilRenderType;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import org.joml.Matrix4x3f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class GnomadMogulWeaponRenderLayer extends NecromancerEntityRenderLayer<GnomadMogulEntity, GnomadMogulSkeleton> {
    private static final Skin WARHOOK_SKIN = GnomadMogulWeaponSkins.createWarhookModel("Root");
    private static final Skeleton SKELETON = new SingleBoneSkeleton();

    private final Matrix4x3f graspTransform = new Matrix4x3f();
    private final Vector3f graspPosition = new Vector3f();
    private final Quaternionf graspOrientation = new Quaternionf();

    public GnomadMogulWeaponRenderLayer(NecromancerEntityRenderer<GnomadMogulEntity, GnomadMogulSkeleton> renderer) {
        super(renderer);
    }

    @Override
    public void render(GnomadMogulEntity parent, GnomadMogulSkeleton skeleton, NecromancerRenderer renderer, MatrixStack matrixStack, int packedLight, float partialTicks) {
        RenderType renderType = VeilRenderType.get(GnomadMogulRenderer.RENDERTYPE, GnomadMogulWeaponSkins.WARHOOK_TEXTURE_LOCATION);
        matrixStack.matrixPush();

        skeleton.MogulRightArmGrasp.getModelTransform(graspTransform.identity(), graspOrientation.identity(), partialTicks);

        graspPosition.set(0, 0, 0);
        graspTransform.getTranslation(graspPosition);
        matrixStack.translate(graspPosition.x, graspPosition.y, graspPosition.z);

        graspTransform.getNormalizedRotation(graspOrientation);
        matrixStack.rotate(graspOrientation);

        if (renderType != null) {
            if (parent.hurtTime > 0 || !parent.isAlive())
                renderer.setOverlay(OverlayTexture.RED_OVERLAY_V);
            renderer.setTransform(matrixStack.position());
            renderer.setLight(packedLight);
            renderer.draw(renderType, SKELETON, WARHOOK_SKIN, partialTicks);
            renderer.reset();
        }

        matrixStack.matrixPop();
    }
}
