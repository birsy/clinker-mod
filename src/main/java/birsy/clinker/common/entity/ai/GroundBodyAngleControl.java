package birsy.clinker.common.entity.ai;

import birsy.clinker.common.entity.GroundLocomotionEntity;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.control.BodyRotationControl;

import java.util.function.Supplier;

public class GroundBodyAngleControl extends BodyRotationControl {
    public final LookTargetController lookTargetController;
    protected final GroundLocomotionEntity me;
    protected final Supplier<Float> defaultYaw, defaultTurnSpeed;
    float currentBodyAngle;

    public GroundBodyAngleControl(GroundLocomotionEntity pMob, Supplier<Float> defaultYaw, Supplier<Float> defaultTurnSpeed) {
        super(pMob);
        this.me = pMob;
        this.lookTargetController = new LookTargetController(pMob);
        this.defaultYaw = defaultYaw;
        this.defaultTurnSpeed = defaultTurnSpeed;
    }

    public void tick() {
        this.lookTargetController.tick(0, defaultYaw.get(), defaultTurnSpeed.get());
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
