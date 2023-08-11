package birsy.clinker.client.model.base;

import birsy.clinker.client.model.base.mesh.ModelMesh;
import birsy.clinker.core.util.Quaternionf;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class InterpolatedBone {
    public float initialX, initialY, initialZ;
    public float x, y, z, pX, pY, pZ;
    private Quaternionf initialRotation;
    public Quaternionf rotation, pRotation;
    private Quaternionf currentRotation;
    public float initialXSize, initialYSize, initialZSize;
    public float xSize, ySize, zSize, pXSize, pYSize, pZSize;

    @Nullable
    public InterpolatedBone parent;
    public List<InterpolatedBone> children;

    public final String identifier;

    public InterpolatedBone(String identifier) {
        this.identifier = identifier;

        this.rotation = new Quaternionf();
        this.pRotation = this.rotation.clone();
        this.currentRotation = this.rotation.clone();
        this.initialRotation = new Quaternionf();

        this.xSize = 1.0F;
        this.ySize = 1.0F;
        this.zSize = 1.0F;
        this.pXSize = 1.0F;
        this.pYSize = 1.0F;
        this.pZSize = 1.0F;

        this.children = new ArrayList<>();
    }

    public void setInitialTransform(float x, float y, float z, Quaternionf rotation) {
        this.initialX = x;
        this.initialY = y;
        this.initialZ = z;
        this.x = x;
        this.y = y;
        this.z = z;
        this.pX = x;
        this.pY = y;
        this.pZ = z;
        this.initialRotation.set(rotation);
        this.rotation.set(rotation);
        this.pRotation.set(rotation);
        this.currentRotation.set(rotation);
    }

    protected void reset() {
        this.x = this.initialX;
        this.y = this.initialY;
        this.z = this.initialZ;
        this.rotation.set(initialRotation);
        this.xSize = initialXSize;
        this.ySize = initialYSize;
        this.zSize = initialZSize;
    }

    protected void tick() {
        this.pX = this.x;
        this.pY = this.y;
        this.pZ = this.z;
        this.pRotation.set(this.rotation);
        this.pXSize = this.xSize;
        this.pYSize = this.ySize;
        this.pZSize = this.zSize;
    }

    public void transform(PoseStack pPoseStack, float partialTick) {
        pPoseStack.translate(Mth.lerp(partialTick, pX, x), Mth.lerp(partialTick, pY, y), Mth.lerp(partialTick, pZ, z));
        pPoseStack.scale(Mth.lerp(partialTick, pXSize, xSize), Mth.lerp(partialTick, pYSize, ySize), Mth.lerp(partialTick, pZSize, zSize));
        pPoseStack.mulPose(pRotation.slerp(rotation, partialTick, currentRotation).toMojangQuaternion());
    }

    public void render(Map<String, ModelMesh> meshes, float partialTick, PoseStack pPoseStack, VertexConsumer pVertexConsumer, int pPackedLight, int pPackedOverlay, float pRed, float pGreen, float pBlue, float pAlpha) {
        ModelMesh mesh = meshes.getOrDefault(this.identifier, ModelMesh.EMPTY);

        pPoseStack.pushPose();

        this.transform(pPoseStack, partialTick);
        mesh.render(this, pPoseStack, pVertexConsumer,pPackedLight, pPackedOverlay, pRed, pGreen, pBlue, pAlpha);

        for (InterpolatedBone child : this.children) {
            child.render(meshes, partialTick, pPoseStack, pVertexConsumer, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue, pAlpha);
        }

        pPoseStack.popPose();
    }

    public void addChild(InterpolatedBone children) {
        if (children.parent != null) {
            children.parent.children.remove(children);
        }

        this.children.add(children);
        children.parent = this;
    }

    public void setParent(InterpolatedBone parent) {
        this.parent = parent;
        parent.children.add(this);
    }

    public Matrix4f getModelSpaceTransformMatrix(PoseStack pPoseStack, float partialTick) {
        InterpolatedBone parent = this.parent;
        if (parent != null) {
            parent.getModelSpaceTransformMatrix(pPoseStack, partialTick);
        }
        this.transform(pPoseStack, partialTick);

        return pPoseStack.last().pose();
    }
}
