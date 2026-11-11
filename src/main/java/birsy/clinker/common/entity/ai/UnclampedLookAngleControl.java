package birsy.clinker.common.entity.ai;

import birsy.clinker.common.entity.module.ModularPathfindingMob;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.control.LookControl;

public class UnclampedLookAngleControl extends LookControl {
    public final LookTargetController lookTargetController;
    final float pitchLimits, baseSpeed;
    float currentHeadYaw, currentHeadPitch;

    public UnclampedLookAngleControl(ModularPathfindingMob<?> mob, float pitchLimits, float baseSpeed) {
        super(mob);
        this.lookTargetController = new LookTargetController(mob);
        this.pitchLimits = pitchLimits;
        this.baseSpeed = baseSpeed;
    }

    public ModularPathfindingMob<?> getEntity() {
        return (ModularPathfindingMob<?>) this.mob;
    }

    @Override
    public void tick() {
        this.lookTargetController.tick(0, currentHeadYaw, this.baseSpeed);

        ModularPathfindingMob<?> me = this.getEntity();
        float yaw = this.lookTargetController.getDesiredYaw(),
                pitch = this.lookTargetController.getDesiredPitch();
        float lerpFactor = this.lookTargetController.getRotationSpeed();

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
        this.lookTargetController.lookAt(-9999, x, y, z, 0.1F, 0.2F);
    }
}
