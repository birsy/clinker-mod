package birsy.clinker.client.necromancer.render;

import birsy.clinker.client.necromancer.animate.Bone;
import birsy.clinker.client.necromancer.animate.Skeleton;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.ShaderInstance;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class EntityTypeRenderer {
    private float[] finalBoneTransforms;
    protected void renderEntity(PoseStack stack, Skeleton skeleton, Skin skin) {
        for (Bone root : skeleton.roots()) root.precalculatePoseSpaceMatricies();
        for (int i = 0; i < skeleton.boneByIndex().size(); i++) {
            skeleton.boneByIndex().get(i).poseSpaceMatrix().get(finalBoneTransforms, i * 4 * 3);
        }

        ShaderInstance shader = RenderSystem.getShader();
        shader.safeGetUniform("BoneTransforms").set(finalBoneTransforms);

        skin.draw(stack);
    }
}
