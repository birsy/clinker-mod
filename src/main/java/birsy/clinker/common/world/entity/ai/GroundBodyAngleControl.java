package birsy.clinker.common.world.entity.ai;

import birsy.clinker.common.world.entity.GroundLocomotionEntity;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.control.BodyRotationControl;

public class GroundBodyAngleControl extends BodyRotationControl {
    protected final GroundLocomotionEntity me;
    public final LookTargetController lookTargetController;

    public GroundBodyAngleControl(GroundLocomotionEntity pMob) {
        super(pMob);
        this.me = pMob;
        this.lookTargetController = new LookTargetController(pMob);
    }

    public void tick() {
        this.lookTargetController.tick();
        float desiredYAngle = Mth.wrapDegrees(this.lookTargetController.getDesiredYaw().orElse(me.yHeadRot));
        float lerpFactor = this.lookTargetController.getRotationSpeed();

        float bodyRot = Mth.rotLerp(lerpFactor, me.getSyncedBodyRotation(), desiredYAngle);
        me.yBodyRot = bodyRot;
        me.setSyncedBodyRotation(bodyRot);
    }

    @Override
    public void clientTick() {
        me.yBodyRot = Mth.approachDegrees(me.yBodyRot, me.getSyncedBodyRotation(), 5);
    }
}
