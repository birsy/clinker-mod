package birsy.clinker.common.entity.ai;

import birsy.clinker.common.entity.GroundLocomotionEntity;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.control.LookControl;

import java.util.function.Supplier;

public class GroundLookAngleControl extends LookControl {
    public final LookTargetController lookTargetController;
    final float pitchLimits, yawLimits, baseSpeed;
    float currentHeadYaw, currentHeadPitch;
    public GroundLookAngleControl(GroundLocomotionEntity mob, float pitchLimits, float yawLimits, float baseSpeed) {
        super(mob);
        this.lookTargetController = new LookTargetController(mob);
        this.pitchLimits = pitchLimits;
        this.yawLimits = yawLimits;
        this.baseSpeed = baseSpeed;
    }

    public GroundLocomotionEntity getEntity() {
        return (GroundLocomotionEntity) this.mob;
    }

    @Override
    public void tick() {
        float currentBodyYaw = this.getEntity().getSyncedBodyRotation();
        this.lookTargetController.tick(0, currentBodyYaw, baseSpeed);

        GroundLocomotionEntity me = this.getEntity();
        float yaw = this.lookTargetController.getDesiredYaw(),
              pitch = this.lookTargetController.getDesiredPitch();
        float lerpFactor = this.lookTargetController.getRotationSpeed();

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

    public void setLookAt(double x, double y, double z, float deltaYaw, float deltaPitch) {
        super.setLookAt(x, y, z, deltaYaw, deltaPitch);
        this.lookTargetController.lookAt(0, x, y, z, 0.1F, 0.2F);
    }
}
