package birsy.clinker.client.entity.mogul.layer;

import birsy.clinker.client.entity.SingleBoneSkeleton;
import birsy.clinker.client.entity.mogul.MogulRenderer;
import birsy.clinker.client.entity.mogul.MogulSkeleton;
import birsy.clinker.client.entity.mogul.MogulWeaponSkins;
import birsy.clinker.common.world.entity.gnomad.mogul.GnomadMogulEntity;
import birsy.clinker.core.Clinker;
import foundry.veil.api.client.necromancer.Skeleton;
import foundry.veil.api.client.necromancer.render.*;
import foundry.veil.api.client.render.MatrixStack;
import foundry.veil.api.client.render.rendertype.VeilRenderType;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4x3f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.text.NumberFormat;
import java.util.Locale;

public class MogulWeaponRenderLayer extends NecromancerEntityRenderLayer<GnomadMogulEntity, MogulSkeleton> {
    private static final Skin WARHOOK_SKIN = MogulWeaponSkins.createWarhookModel("Root");
    private static final Skeleton SKELETON = new SingleBoneSkeleton();

    private final Matrix4x3f graspTransform = new Matrix4x3f();
    private final Vector3f graspPosition = new Vector3f();
    private final Quaternionf graspOrientation = new Quaternionf();

    public MogulWeaponRenderLayer(NecromancerEntityRenderer<GnomadMogulEntity, MogulSkeleton> renderer) {
        super(renderer);
    }

    @Override
    public void render(GnomadMogulEntity parent, MogulSkeleton skeleton, NecromancerRenderer renderer, MatrixStack matrixStack, int packedLight, float partialTicks) {
        RenderType renderType = VeilRenderType.get(MogulRenderer.RENDERTYPE, MogulWeaponSkins.WARHOOK_TEXTURE_LOCATION);
        matrixStack.matrixPush();

        graspPosition.set(0, 0, 0);
        skeleton.MogulRightArmGrasp.getModelTransform(graspTransform.identity(), graspOrientation.identity(), partialTicks);

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
