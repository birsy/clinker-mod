package birsy.clinker.client.render.entity.model.base;

import birsy.clinker.core.Clinker;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

@OnlyIn(Dist.CLIENT)
public class AnimatedModelSkeleton {
    public AnimatedModelBone root;
    public final AnimatedModel baseModel;
    public Map<String, AnimatedModelBone> identifierMap;

    public AnimatedModelSkeleton(AnimatedModelBone root, AnimatedModel baseModel) {
        this.root = root;
        this.baseModel = baseModel;
        this.identifierMap = new HashMap<>();
        this.identifierMap.put(root.identifier, root);
    }

    public void addBone(AnimatedModelBone bone) {
        if (identifierMap.containsKey(bone.identifier)) {
            Clinker.LOGGER.warn("! Bones with identical identifier " + bone.identifier);
            return;
        }

        this.identifierMap.put(bone.identifier, bone);
    }

    public void get(String identifier) {
        this.identifierMap.get(identifier);
    }

    public void getBone(String identifier) {
        this.identifierMap.get(identifier);
    }

    public void traverseBoneTree(Consumer<AnimatedModelBone> consumer) {
        consumer.accept(this.root);
        traverseBoneTree(this.root, consumer);
    }

    public void traverseBoneTree(AnimatedModelBone root, Consumer<AnimatedModelBone> consumer) {
        for (AnimatedModelBone child : root.children) {
            consumer.accept(child);
            traverseBoneTree(child, consumer);
        }
    }

    public void render(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha, float partialTick) {
        this.renderBone(this.root, poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha, partialTick);
    }

    public void renderBone(AnimatedModelBone bone, PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha, float partialTick) {
        if (!bone.visible) return;
        poseStack.pushPose();
        bone.transform(poseStack, partialTick);
        PoseStack.Pose pose = poseStack.last();

        if (!bone.skipRender) {
            if (bone instanceof AnimatedMeshedModelBone meshedBone) {
                meshedBone.mesh.render(pose, buffer, packedLight, packedOverlay, red, green, blue, alpha);
            } else if (baseModel.bones.containsKey(bone.identifier)) {
                baseModel.bones.get(bone.identifier).mesh.render(pose, buffer, packedLight, packedOverlay, red, green, blue, alpha);
            }
        }

        for (AnimatedModelBone child : bone.children) {
            renderBone(child, poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha, partialTick);
        }

        poseStack.popPose();
    }
}
