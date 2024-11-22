package birsy.necromancer.render;

import birsy.necromancer.Bone;
import birsy.necromancer.Skeleton;
import birsy.necromancer.render.mesh.Mesh;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import java.util.HashMap;
import java.util.Map;

public class Skin<T extends Skeleton<?>> {
    final Map<String, Mesh> meshes;

    public Skin() {
        this.meshes = new HashMap<>();
    }

    public void addMesh(String boneID, Mesh mesh) {
        meshes.put(boneID, mesh);
    }

    public Mesh getMesh(Bone bone) {
        return meshes.getOrDefault(bone.identifier, Mesh.EMPTY);
    }

    public void render(T skeleton, int ticksExisted, float partialTick, PoseStack pPoseStack, VertexConsumer pVertexConsumer, int pPackedLight, int pPackedOverlay, float pRed, float pGreen, float pBlue, float pAlpha) {
        for (Map.Entry<String, Mesh> stringMeshEntry : this.meshes.entrySet()) {
            Mesh mesh = stringMeshEntry.getValue();
            if (!mesh.isStatic) mesh.update(skeleton.bones.getOrDefault(stringMeshEntry.getKey(), null), skeleton, ticksExisted + partialTick);
        }
        for (Bone bone : skeleton.roots) {
            bone.render(this, partialTick, pPoseStack, pVertexConsumer, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue, pAlpha, true);
        }
    }
}
