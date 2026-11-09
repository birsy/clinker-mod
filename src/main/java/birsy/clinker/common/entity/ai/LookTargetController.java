package birsy.clinker.common.entity.ai;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;

import java.util.*;

public class LookTargetController {
    private final Mob entity;
    private final TreeMap<Integer, LookTarget> targets = new TreeMap<>();

    private float pitch, yaw, rotationSpeed;

    public LookTargetController(Mob entity) {
        this.entity = entity;
    }

    public float getDesiredPitch() { return pitch; }
    public float getDesiredYaw() { return yaw; }
    public float getRotationSpeed() { return rotationSpeed; }

    public void lookAt(int priority, double x, double y, double z, float speed, float fadeSpeed) {
        LookTarget target = targets.computeIfAbsent(priority, k -> new LookTarget());
        if (target.requested) return;
        target.requested = true;
        target.speed = speed;
        target.fadeSpeed = fadeSpeed;

        double dX = x - entity.getX();
        double dY = y - entity.getEyeY();
        double dZ = z - entity.getZ();
        target.pitch = (float) (-Mth.atan2(dY, Math.sqrt(dX * dX + dZ * dZ)) * Mth.RAD_TO_DEG);
        target.yaw = (float) (Mth.atan2(dZ, dX) * Mth.RAD_TO_DEG) - 90f;
    }

    public void lookAt(int priority, Entity target, float speed, float fadeSpeed) {
        lookAt(priority, target.getX(), target.getEyeY(), target.getZ(), speed, fadeSpeed);
    }

    protected void tick(float basePitch, float baseYaw, float baseSpeed) {
        float nextPitch = basePitch,
              nextYaw = baseYaw,
              nextSpeed = baseSpeed;

        for (Iterator<LookTarget> it = targets.values().iterator(); it.hasNext(); ) {
            LookTarget lookTarget = it.next();
            lookTarget.fade = Mth.approach(lookTarget.fade, lookTarget.requested ? 1f : 0f, lookTarget.fadeSpeed);

            if (lookTarget.fade <= 0f && !lookTarget.requested) {
                it.remove();
                continue;
            }
            lookTarget.requested = false;

            nextSpeed = Mth.lerp(lookTarget.fade, nextSpeed, lookTarget.speed);
            nextPitch = Mth.rotLerp(lookTarget.fade, nextPitch, lookTarget.pitch);
            nextYaw = Mth.rotLerp(lookTarget.fade, nextYaw, lookTarget.yaw);
        }

        this.rotationSpeed = nextSpeed;
        this.pitch = nextPitch;
        this.yaw = nextYaw;
    }

    private static final class LookTarget {
        float pitch, yaw, speed;
        float fadeSpeed;
        float fade;
        boolean requested;
    }
}
