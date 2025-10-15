package birsy.clinker.common.world.entity.ai;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class LookTargetController {
    final Mob entity;
    final List<LookTargetHandle> lookTargets = new ArrayList<>(4);
    @Nullable
    protected LookTargetHandle currentHandle;
    protected boolean needsUpdate;

    public LookTargetController(Mob entity) {
        this.entity = entity;
    }

    public Optional<Float> getDesiredPitch() {
        return this.currentHandle == null ? Optional.empty() : Optional.of(this.currentHandle.desiredPitch);
    }
    public Optional<Float> getDesiredYaw() {
        return this.currentHandle == null ? Optional.empty() : Optional.of(this.currentHandle.desiredYaw);
    }
    public float getRotationSpeed() {
        return this.currentHandle == null ? 0.0F : this.currentHandle.lookSpeed;
    }

    public LookTargetHandle createHandle(float lookSpeed, int priority) {
        LookTargetHandle handle = new LookTargetHandle(this, lookSpeed, priority);
        this.lookTargets.add(handle);
        if (this.currentHandle == null || this.currentHandle.priority < priority)
            this.needsUpdate = true;
        return handle;
    }

    protected void tick() {
        if (this.needsUpdate) this.chooseCurrentHandle();
    }

    private void chooseCurrentHandle() {
        if (lookTargets.isEmpty()) {
            currentHandle = null;
            return;
        }

        this.currentHandle = null;
        for (LookTargetHandle lookTarget : this.lookTargets) {
            if (!lookTarget.isActive()) continue; // inactive look targets are skipped
            if (this.currentHandle == null || lookTarget.priority > this.currentHandle.priority)
                this.currentHandle = lookTarget;
        }
    }

    public static class LookTargetHandle {
        final LookTargetController controller;
        final float lookSpeed;
        final int priority;

        protected boolean active = true;
        protected float desiredPitch, desiredYaw;

        private LookTargetHandle(LookTargetController controller, float lookSpeed, int priority) {
            this.controller = controller;
            this.lookSpeed = lookSpeed;
            this.priority = priority;
        }

        public void face(float pitch, float yaw) {
            this.desiredPitch = pitch;
            this.desiredYaw = yaw;
        }

        public void face(double x, double y, double z) {
            double dX = x - controller.entity.getX(),
                   dY = y - controller.entity.getEyeY(),
                   dZ = z - controller.entity.getZ();
            float pitch = (float) (-Mth.atan2(dY, Math.sqrt(dX * dX + dZ * dZ)) * 180.0 / Math.PI),
                  yaw = (float) (Mth.atan2(dZ, dX) * 180.0 / Math.PI) - 90.0F;
            this.face(pitch, yaw);
        }

        public void face(Entity entity) {
            this.face(entity.getX(), entity.getEyeY(), entity.getZ());
        }

        public void setActive(boolean active) {
            if (this == controller.currentHandle && this.active != active) controller.needsUpdate = true;
            this.active = active;
        }

        public boolean isActive() {
            return this.active;
        }

        public void remove() {
            this.controller.lookTargets.remove(this);
            if (this == controller.currentHandle) controller.needsUpdate = true;
        }
    }
}
