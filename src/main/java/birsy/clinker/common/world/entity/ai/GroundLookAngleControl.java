package birsy.clinker.common.world.entity.ai;

import birsy.clinker.common.world.entity.GroundLocomotionEntity;
import birsy.clinker.core.Clinker;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.control.LookControl;

import java.util.Optional;

public class GroundLookAngleControl extends LookControl {
    public final LookTargetController lookTargetController;

    public GroundLookAngleControl(GroundLocomotionEntity mob) {
        super(mob);
        this.lookTargetController = new LookTargetController(mob);
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
//        me.setYHeadRot(yaw);
//        me.setXRot(pitch);
        me.setYHeadRot(Mth.rotLerp(lerpFactor, me.getYHeadRot(), yaw));
        me.setXRot(Mth.rotLerp(lerpFactor, me.getXRot(), pitch));
    }
}
