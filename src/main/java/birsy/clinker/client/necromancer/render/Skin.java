package birsy.clinker.client.necromancer.render;

import birsy.clinker.client.necromancer.Bone;
import birsy.clinker.client.necromancer.Skeleton;
import birsy.clinker.client.necromancer.render.mesh.Mesh;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.RenderType;

import java.util.HashMap;
import java.util.Map;

public class Skin<T extends Skeleton<?>> {
    final RenderType renderType;
    final Map<String, Mesh> meshes;

    public Skin(RenderType renderType) {
        this.renderType = renderType;
        this.meshes = new HashMap<>();
    }

    public void addMesh(Bone bone, Mesh mesh) {
        meshes.put(bone.identifier, mesh);
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
