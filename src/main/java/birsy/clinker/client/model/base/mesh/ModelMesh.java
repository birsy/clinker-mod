package birsy.clinker.client.model.base.mesh;

import birsy.clinker.client.model.base.InterpolatedSkeleton;
import birsy.clinker.client.model.base.InterpolatedBone;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import javax.annotation.Nullable;

public abstract class ModelMesh {
    public final boolean isStatic;
    public static final ModelMesh EMPTY = new ModelMesh(true) {
        @Override
        public void render(InterpolatedBone part, PoseStack pPoseStack, VertexConsumer pVertexConsumer, int pPackedLight, int pPackedOverlay, float pRed, float pGreen, float pBlue, float pAlpha) {
            return;
        }
    };

    protected ModelMesh(boolean isStatic) {
        this.isStatic = isStatic;
    }

    public void update(@Nullable InterpolatedBone part, InterpolatedSkeleton model, float partialTick) {}
    public abstract void render(InterpolatedBone part, PoseStack pPoseStack, VertexConsumer pVertexConsumer, int pPackedLight, int pPackedOverlay, float pRed, float pGreen, float pBlue, float pAlpha);
}
