package birsy.clinker.client.model.base;

import birsy.clinker.client.model.base.constraint.Constraint;
import birsy.clinker.client.model.base.constraint.InverseKinematicsConstraint;
import birsy.clinker.client.model.base.mesh.ModelMesh;
import birsy.clinker.client.render.DebugRenderUtil;
import birsy.clinker.core.Clinker;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;

import java.util.*;

public abstract class InterpolatedSkeleton<P extends InterpolatedSkeletonParent> {
    public List<InterpolatedBone> roots;
    public List<Constraint> constraints;
    public Map<String, InterpolatedBone> parts;
    public Map<String, ModelMesh> meshes;

    private int ticksExisted = 0;

    public InterpolatedSkeleton() {
        this.roots = new ArrayList<>();
        this.constraints = new ArrayList<>();
        this.parts = new HashMap<>();
        this.meshes = new HashMap<>();
    }

    protected void updatePreviousPosition() {
        for (InterpolatedBone part : this.parts.values()) {
            part.updatePreviousPosition();
        }
    }

    public void tick(AnimationProperties properties) {
        ticksExisted++;
        this.updatePreviousPosition();
        for (InterpolatedBone part : this.parts.values()) {
            part.tick(1.0F / 20.0F);
        }
        this.animate(properties);
        for (Constraint constraint : this.constraints) {
            //constraint.apply();
        }
        //this.applyConstraints(8);
    }

    public void addAnimationProperties(AnimationProperties properties, P parent) {
        if (parent instanceof LivingEntity entity) {
            properties.addProperty("entity", entity);
            properties.addProperty("limbSwing", entity.walkAnimation.position());
            properties.addProperty("limbSwingAmount", entity.walkAnimation.speed());
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
            if (!mesh.isStatic) mesh.update(this.parts.getOrDefault(stringMeshEntry.getKey(), null), this, ticksExisted, partialTick);
        }

        for (InterpolatedBone part : this.roots) {
            part.render(this.meshes, partialTick, pPoseStack, pVertexConsumer, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue, pAlpha, true);
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
            } else {
                InterpolatedBone parentBone = part.parent;
                while (parentBone != null) {
                    part.parentChain.add(parentBone);
                    parentBone = parentBone.parent;
                }
                Collections.reverse(part.parentChain);
            }


        }
    }
}
