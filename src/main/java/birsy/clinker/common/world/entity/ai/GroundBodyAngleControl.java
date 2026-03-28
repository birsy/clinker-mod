package birsy.clinker.common.world.entity.ai;

import birsy.clinker.common.world.entity.GroundLocomotionEntity;
import birsy.clinker.core.Clinker;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.control.BodyRotationControl;

import java.util.function.Supplier;

public class GroundBodyAngleControl extends BodyRotationControl {
    protected final GroundLocomotionEntity me;
    public final LookTargetController lookTargetController;
    float currentBodyAngle;

    public GroundBodyAngleControl(GroundLocomotionEntity pMob, Supplier<Float> defaultYaw, Supplier<Float> defaultTurnSpeed) {
        super(pMob);
        this.me = pMob;
        this.lookTargetController = new LookTargetController(pMob, () -> 0F, defaultYaw, defaultTurnSpeed);
    }

    public void tick() {
        this.lookTargetController.tick();
        float desiredYAngle = this.lookTargetController.getDesiredYaw();
        float lerpFactor = this.lookTargetController.getRotationSpeed();
        currentBodyAngle = Mth.rotLerp(lerpFactor, currentBodyAngle, desiredYAngle);

        me.yBodyRot = currentBodyAngle;
        me.setSyncedBodyRotation(currentBodyAngle);
    }

    @Override
    public void clientTick() {
        currentBodyAngle = Mth.approachDegrees(currentBodyAngle, me.getSyncedBodyRotation(), 15);
        me.yBodyRot = currentBodyAngle;
    }
}
