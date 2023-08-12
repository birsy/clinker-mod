package birsy.clinker.client.model.base;

import birsy.clinker.client.model.base.constraint.Constraint;
import birsy.clinker.client.model.base.mesh.ModelMesh;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.world.entity.LivingEntity;

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
        this.applyConstraints(8);
    }

    public void addAnimationProperties(AnimationProperties properties, P parent) {
        if (parent instanceof LivingEntity entity) {
            properties.addProperty("limbSwing", entity.animationPosition);
            properties.addProperty("limbSwingAmount", entity.animationSpeed);
            properties.addProperty("ageInTicks", entity.tickCount);
            properties.addProperty("netHeadYaw", entity.yHeadRot - entity.yBodyRot);
            properties.addProperty("headPitch", entity.getViewXRot(1.0F));
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
