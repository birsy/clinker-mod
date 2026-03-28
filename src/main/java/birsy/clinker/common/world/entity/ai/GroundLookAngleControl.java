package birsy.clinker.common.world.entity.ai;

import birsy.clinker.common.world.entity.GroundLocomotionEntity;
import birsy.clinker.core.Clinker;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.control.LookControl;

import java.util.Optional;
import java.util.function.Supplier;

public class GroundLookAngleControl extends LookControl {
    public final LookTargetController lookTargetController;
    final float pitchLimits, yawLimits;
    float currentHeadYaw,
          currentHeadPitch;
    public GroundLookAngleControl(GroundLocomotionEntity mob,
                                  Supplier<Float> defaultPitch, float pitchLimits,
                                  Supplier<Float> defaultYaw, float yawLimits,
                                  Supplier<Float> defaultRotationalSpeed) {
        super(mob);
        this.lookTargetController = new LookTargetController(mob, defaultPitch, defaultYaw, defaultRotationalSpeed);
        this.pitchLimits = pitchLimits;
        this.yawLimits = yawLimits;
    }

    public GroundLocomotionEntity getEntity() {
        return (GroundLocomotionEntity) this.mob;
    }

    @Override
    public void tick() {
        this.lookTargetController.tick();
        GroundLocomotionEntity me = this.getEntity();
        float yaw = this.lookTargetController.getDesiredYaw(),
              pitch = this.lookTargetController.getDesiredPitch();
        float lerpFactor = this.lookTargetController.getRotationSpeed();

        float currentBodyYaw = this.getEntity().getSyncedBodyRotation();
        float netYaw = Mth.degreesDifference(currentBodyYaw, yaw);
        netYaw = Mth.clamp(netYaw, -yawLimits, yawLimits);
        yaw = currentBodyYaw + netYaw;
        currentHeadYaw = Mth.rotLerp(lerpFactor, currentHeadYaw, yaw);

        float currentBodyPitch = 0.0F;
        float netPitch = Mth.degreesDifference(currentBodyPitch, pitch);
        netPitch = Mth.clamp(netPitch, -pitchLimits, pitchLimits);
        pitch = currentBodyPitch + netPitch;
        currentHeadPitch = Mth.rotLerp(lerpFactor, currentHeadPitch, pitch);

        me.setYHeadRot(currentHeadYaw);
        me.setXRot(currentHeadPitch);
    }
}
