package birsy.clinker.client.model.base;

import birsy.clinker.client.model.base.constraint.Constraint;
import birsy.clinker.client.model.base.constraint.InverseKinematicsConstraint;
import birsy.clinker.client.model.base.mesh.ModelMesh;
import birsy.clinker.client.render.DebugRenderUtil;
import birsy.clinker.core.Clinker;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import com.mojang.math.Vector4f;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class InterpolatedSkeleton<P extends InterpolatedSkeletonParent> {
    public List<InterpolatedBone> roots;
    public List<Constraint> constraints;
    public Map<String, InterpolatedBone> parts;
    public Map<String, ModelMesh> meshes;

    public InterpolatedSkeleton() {
        this.roots = new ArrayList<>();
        this.constraints = new ArrayList<>();
        this.parts = new HashMap<>();
        this.meshes = new HashMap<>();
    }

    protected void updatePreviousPosition() {
        for (InterpolatedBone part : this.parts.values()) {
            part.tick();
        }
    }

    public void tick(AnimationProperties properties) {
        this.updatePreviousPosition();
        this.animate(properties);
        for (Constraint constraint : this.constraints) {
            if (constraint instanceof InverseKinematicsConstraint ik) {
                LivingEntity entity = (LivingEntity) properties.getProperty("entity");
                Vec3 pos = entity.position();
                Vec3 targetPos = Minecraft.getInstance().cameraEntity.position();

                Vec3 tPos = targetPos.subtract(pos).scale( 16);
                ik.target.set((float) tPos.x(), (float) tPos.y(), (float) tPos.z());
                ik.poleTarget.set(32, ik.poleTarget.y(),Mth.sin(properties.getNumProperty("ageInTicks") * 0.15F) * 16);
            }
            constraint.apply();
        }
        //this.applyConstraints(8);
    }

    public void addAnimationProperties(AnimationProperties properties, P parent) {
        if (parent instanceof LivingEntity entity) {
            properties.addProperty("entity", entity);
            properties.addProperty("limbSwing", entity.animationPosition);
            properties.addProperty("limbSwingAmount", entity.animationSpeed);
            properties.addProperty("ageInTicks", entity.tickCount);
            properties.addProperty("bodyYaw", 180 - entity.yBodyRot);
            properties.addProperty("netHeadYaw", -(entity.yHeadRot - entity.yBodyRot));
            properties.addProperty("headPitch", -entity.getViewXRot(1.0F));
        }
    }
    public abstract void animate(AnimationProperties properties);

    protected void applyConstraints(int iterations) {
        for (Constraint constraint : constraints) {
            constraint.initialize();
        }

        int satisfiedConstraints = 0;
        for (int i = 0; i < iterations; i++) {
            for (Constraint constraint : constraints) {
                if (!constraint.isSatisfied() && constraint.isIterative()) {
                    constraint.apply();
                } else {
                    satisfiedConstraints++;
                }
            }

            if (satisfiedConstraints == constraints.size()) return;
        }

        for (Constraint constraint : constraints) {
            if (!constraint.isIterative()) {
                constraint.apply();
            }
        }
    }

    public void render(float partialTick, PoseStack pPoseStack, VertexConsumer pVertexConsumer, int pPackedLight, int pPackedOverlay, float pRed, float pGreen, float pBlue, float pAlpha) {
        for (Map.Entry<String, ModelMesh> stringMeshEntry : this.meshes.entrySet()) {
            ModelMesh mesh = stringMeshEntry.getValue();
            if (!mesh.isStatic) mesh.update(this.parts.getOrDefault(stringMeshEntry.getKey(), null), this, partialTick);
        }

//        for (InterpolatedBone bone : this.parts.values()) {
//            PoseStack stack = new PoseStack();
//
//            stack.pushPose();
//            Matrix4f matrix = bone.getModelSpaceTransformMatrix(stack, partialTick).copy();
//            stack.popPose();
//            Vector4f vec = new Vector4f(0, 0, 0, 1);
//            vec.transform(matrix);
//            DebugRenderUtil.renderSphere(pPoseStack, pVertexConsumer, 16, 2.0F, vec.x(), vec.y(), vec.z(), 1.0F, 1.0F, 1.0F, 1.0F);
//
//            for (InterpolatedBone child : bone.children) {
//                stack.pushPose();
//                Matrix4f matrix2 = child.getModelSpaceTransformMatrix(stack, partialTick).copy();
//                stack.popPose();
//                Vector4f vec2 = new Vector4f(0, 0, 0, 1);
//                vec2.transform(matrix2);
//                DebugRenderUtil.renderLine(pPoseStack, pVertexConsumer, vec.x(), vec.y(), vec.z(), vec2.x(), vec2.y(), vec2.z(), 0.0F, 1.0F, 0.0F, 1.0F);
//            }
//        }

        for (InterpolatedBone part : this.roots) {
            part.render(this.meshes, partialTick, pPoseStack, pVertexConsumer, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue, pAlpha);
        }
    }

    public void addBone(InterpolatedBone part, ModelMesh mesh) {
        this.parts.put(part.identifier, part);
        this.meshes.put(part.identifier, mesh);
    }

    public void buildRoots() {
        for (InterpolatedBone part : this.parts.values()) {
            if (part.parent == null) {
                roots.add(part);
            }
        }
    }
}
