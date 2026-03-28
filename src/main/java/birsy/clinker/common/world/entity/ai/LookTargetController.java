package birsy.clinker.common.world.entity.ai;

import birsy.clinker.core.Clinker;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.*;
import java.util.function.Supplier;

public class LookTargetController {
    final Mob entity;
    final Supplier<Float> defaultPitch, defaultYaw, defaultRotationalSpeed;
    final List<LookTargetHandle> lookTargets = new ArrayList<>(8);
    protected boolean priorityNeedsUpdate;
    float combinedDesiredPitch, combinedDesiredYaw, combinedRotationalSpeed;

    public LookTargetController(Mob entity,
                                Supplier<Float> defaultPitch,
                                Supplier<Float> defaultYaw,
                                Supplier<Float> defaultRotationalSpeed) {
        this.entity = entity;

        this.defaultPitch = defaultPitch;
        this.defaultYaw = defaultYaw;
        this.defaultRotationalSpeed = defaultRotationalSpeed;
    }

    public float getDesiredPitch() {
        return combinedDesiredPitch;
    }
    public float getDesiredYaw() {
        return combinedDesiredYaw;
    }
    public float getRotationSpeed() {
        return combinedRotationalSpeed;
    }

    public LookTargetHandle createHandle(float lookSpeed, int priority) {
        LookTargetHandle handle = new LookTargetHandle(this, lookSpeed, priority);
        this.lookTargets.add(handle);
        this.priorityNeedsUpdate = true;
        return handle;
    }

    private final Quaternionf scratchHandleRotation = new Quaternionf(),
                              scratchCombinedRotation = new Quaternionf();
    private final Vector3f scratchEulerAngles = new Vector3f();
    protected void tick() {
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

        // find the highest "effective" index
        // (e.g. all computed rotations before won't matter because it'll override them)
        int maxEffectiveIndex = -1;
        for (int i = 0; i < this.lookTargets.size(); i++) {
            LookTargetHandle lookTarget = this.lookTargets.get(i);
            maxEffectiveIndex = i;
            float weight = lookTarget.effectiveWeight();
            if (weight >= 0.999) break;
        }

        float rotationalSpeed = defaultRotationalSpeed.get();
        // todo: un-gimble lock this
        float pitch = defaultPitch.get();
        float yaw = defaultYaw.get();
        float defaultYaw = yaw;
        // loop from the highest effective index backwards.
        // meaning highest priority weights will come last and override previous ones
        for (int i = maxEffectiveIndex; i >= 0; i--) {
            LookTargetHandle lookTarget = this.lookTargets.get(i);
            float weight = lookTarget.effectiveWeight();
            if (weight <= 0) continue;

            rotationalSpeed = Mth.lerp(weight, rotationalSpeed, lookTarget.lookSpeed);
            pitch = Mth.rotLerp(weight, pitch, lookTarget.desiredPitch);
            yaw = Mth.rotLerp(weight, yaw, lookTarget.desiredYaw);
        }

        this.combinedRotationalSpeed = rotationalSpeed;
        this.combinedDesiredPitch = pitch;
        this.combinedDesiredYaw = yaw;
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
