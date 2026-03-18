package birsy.clinker.common.world.entity.ai;

import birsy.clinker.core.Clinker;
import birsy.clinker.core.util.VectorUtils;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import org.joml.Vector3f;

import java.util.*;

public class LookTargetController {
    final Mob entity;
    final List<LookTargetHandle> lookTargets = new ArrayList<>(8);
    protected boolean priorityNeedsUpdate;

    boolean anyActive = false;
    float combinedDesiredPitch, combinedDesiredYaw, combinedRotationalSpeed;

    public LookTargetController(Mob entity) {
        this.entity = entity;
    }

    public Optional<Float> getDesiredPitch() {
        return anyActive ? Optional.of(combinedDesiredPitch) : Optional.empty();
    }
    public Optional<Float> getDesiredYaw() {
        return anyActive ? Optional.of(combinedDesiredYaw) : Optional.empty();
    }
    public float getRotationSpeed() {
        return anyActive ? combinedRotationalSpeed : 0.0F;
    }

    public LookTargetHandle createHandle(float lookSpeed, int priority) {
        LookTargetHandle handle = new LookTargetHandle(this, lookSpeed, priority);
        this.lookTargets.add(handle);
        this.priorityNeedsUpdate = true;
        return handle;
    }

    private final Vector3f scratch0 = new Vector3f(), scratch1 = new Vector3f();
    protected void tick() {
        // skip if nothing's active
        if (this.lookTargets.isEmpty()) {
            anyActive = false;
            return;
        }

        // update fades
        for (Iterator<LookTargetHandle> iterator = this.lookTargets.iterator(); iterator.hasNext(); ) {
            LookTargetHandle lookTarget = iterator.next();

            switch (lookTarget.fadeType) {
                case IN -> {
                    lookTarget.fadeAmount = Mth.approach(lookTarget.fadeAmount, 1.0F, lookTarget.fadeSpeed);
                    if (lookTarget.fadeAmount >= 1.0F) {
                        lookTarget.fadeType = FadeType.NONE;
                    }
                }
                case OUT -> {
                    lookTarget.fadeAmount = Mth.approach(lookTarget.fadeAmount, 0.0F, lookTarget.fadeSpeed);
                    if (lookTarget.fadeAmount <= 0.0F) {
                        lookTarget.fadeType = FadeType.NONE;
                        iterator.remove();
                        this.priorityNeedsUpdate = true;
                    }
                }
            }
        }

        // resort list, if we need to.
        if (priorityNeedsUpdate) {
            // highest priority comes first
            this.lookTargets.sort(Comparator.comparingInt(LookTargetHandle::priority).reversed());
            this.priorityNeedsUpdate = false;
        }

        // update by weight order
        int maxIndex = 0;
        for (int i = 0; i < this.lookTargets.size(); i++) {
            LookTargetHandle lookTarget = this.lookTargets.get(i);
            if (lookTarget.effectiveWeight() >= 0.999) {
                maxIndex = i;
                break;
            }
        }

        anyActive = false;
        float rotationalSpeed = 0.0F;
        for (int i = maxIndex; i >= 0; i--) {
            LookTargetHandle lookTarget = this.lookTargets.get(i);
            float weight = lookTarget.effectiveWeight();
            if (weight <= 0) continue;

            float pitchRadians = lookTarget.desiredPitch * Mth.DEG_TO_RAD;
            float desiredY = Mth.sin(pitchRadians), pitchCos = Mth.cos(pitchRadians);

            float yawRadians = lookTarget.desiredYaw * Mth.DEG_TO_RAD;
            float desiredX = Mth.cos(yawRadians) * pitchCos,
                  desiredZ = Mth.sin(yawRadians) * pitchCos;
            scratch1.set(desiredX, desiredY, desiredZ);

            if (!anyActive) {
                rotationalSpeed = lookTarget.lookSpeed;
                scratch0.set(scratch1);
                anyActive = true;
            } else {
                rotationalSpeed = Mth.lerp(weight, rotationalSpeed, lookTarget.lookSpeed);
                VectorUtils.slerp(scratch0, scratch1, weight, scratch0);
            }
        }

        this.combinedRotationalSpeed = rotationalSpeed;
        this.combinedDesiredPitch = (float) (Mth.atan2(scratch0.y(), Mth.sqrt(scratch0.x() * scratch0.x() + scratch0.z() * scratch0.z())) * Mth.RAD_TO_DEG);
        this.combinedDesiredYaw = (float) (Mth.atan2(scratch0.z(), scratch0.x()) * Mth.RAD_TO_DEG);
    }

    public static class LookTargetHandle {
        final int priority;
        final LookTargetController controller;
        final float lookSpeed;

        public float desiredPitch, desiredYaw;
        float weight = 1f;

        FadeType fadeType = FadeType.NONE;
        float fadeSpeed, fadeAmount;

        private LookTargetHandle(LookTargetController controller, float lookSpeed, int priority) {
            this.controller = controller;
            this.lookSpeed = lookSpeed;
            this.priority = priority;
        }

        public void face(float pitch, float yaw) {
            this.desiredPitch = Mth.wrapDegrees(pitch);
            this.desiredYaw = Mth.wrapDegrees(yaw);
        }

        public void face(double x, double y, double z) {
            double dX = x - controller.entity.getX();
            double dY = y - controller.entity.getEyeY();
            double dZ = z - controller.entity.getZ();

            float pitch = (float) (-Mth.atan2(dY, Math.sqrt(dX * dX + dZ * dZ)) * Mth.RAD_TO_DEG);
            float yaw = (float) (Mth.atan2(dZ, dX) * Mth.RAD_TO_DEG) - 90.0F;

            this.face(pitch, yaw);
        }

        public void face(Entity entity) {
            this.face(entity.getX(), entity.getEyeY(), entity.getZ());
        }

        public void setWeight(float weight) {
            this.weight = Math.clamp(weight, 0, 1);
        }

        public void remove() {
            this.controller.lookTargets.remove(this);
            this.controller.priorityNeedsUpdate = true;
        }

        public void fadeIn(float fadeSpeed, boolean init) {
            this.fadeType = FadeType.IN;
            this.fadeSpeed = fadeSpeed;
            if (init) this.fadeAmount = 0.0F;
        }

        public void fadeOut(float fadeSpeed, boolean init) {
            this.fadeType = FadeType.OUT;
            this.fadeSpeed = fadeSpeed;
            if (init) this.fadeAmount = 1.0F;
        }

        public float effectiveWeight() { return fadeType == FadeType.NONE ? weight : weight * fadeAmount; }

        public float weight() { return weight; }
        public int priority() { return priority; }
    }

    private enum FadeType {
        NONE, IN, OUT
    }
}
