package birsy.clinker.common.world.entity.ai;

import birsy.clinker.common.world.entity.GroundLocomotionEntity;
import birsy.clinker.core.Clinker;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.control.LookControl;

import java.util.Optional;

public class GroundLookAngleControl extends LookControl {
    public final LookTargetController lookTargetController;
    final float pitchLimits, yawLimits;
    public GroundLookAngleControl(GroundLocomotionEntity mob, float pitchLimits, float yawLimits) {
        super(mob);
        this.lookTargetController = new LookTargetController(mob);
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
        float yaw = this.lookTargetController.getDesiredYaw().orElse(me.getYHeadRot()),
              pitch = this.lookTargetController.getDesiredPitch().orElse(me.getXRot());
        float lerpFactor = this.lookTargetController.getRotationSpeed();

        float currentBodyYaw = this.getEntity().getSyncedBodyRotation();
        float netYaw = Mth.degreesDifference(currentBodyYaw, yaw);
        netYaw = Mth.clamp(netYaw, -yawLimits, yawLimits);
        yaw = currentBodyYaw + netYaw;
        me.setYHeadRot(Mth.rotLerp(lerpFactor, me.getYHeadRot(), yaw));

        float currentBodyPitch = 0.0F;
        float netPitch = Mth.degreesDifference(currentBodyPitch, pitch);
        netPitch = Mth.clamp(netPitch, -pitchLimits, pitchLimits);
        pitch = currentBodyPitch + netPitch;
        me.setXRot(Mth.rotLerp(lerpFactor, me.getXRot(), pitch));
    }
}
