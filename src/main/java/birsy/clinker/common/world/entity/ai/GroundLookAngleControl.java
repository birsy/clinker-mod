package birsy.clinker.common.world.entity.ai;

import birsy.clinker.common.world.entity.GroundLocomotionEntity;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.control.LookControl;

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
        float desiredYAngle = Mth.wrapDegrees(this.lookTargetController.getDesiredYaw().orElse(me.yHeadRot));
        float desiredXAngle = Mth.wrapDegrees(this.lookTargetController.getDesiredPitch().orElse(me.getXRot()));
        float lerpFactor = this.lookTargetController.getRotationSpeed();
        me.yHeadRot = Mth.wrapDegrees(Mth.rotLerp(lerpFactor, me.yHeadRot, desiredYAngle));
        me.setXRot(Mth.wrapDegrees(Mth.rotLerp(lerpFactor, me.getXRot(), desiredXAngle)));
    }
}
