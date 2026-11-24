package birsy.clinker.common.entity.lungwyrm;

import birsy.clinker.common.entity.ai.UnclampedLookAngleControl;
import net.minecraft.util.Mth;
import org.joml.Vector3d;

public class LungWyrmLookControl extends UnclampedLookAngleControl {
    Vector3d bodyDir = new Vector3d(), nBodyDir = new Vector3d();

    public LungWyrmLookControl(LungWyrmEntity mob, float baseSpeed) {
        super(mob, 80F, baseSpeed);
    }

    @Override
    public LungWyrmEntity getEntity() {
        return (LungWyrmEntity) super.getEntity();
    }

    @Override
    public void tick() {
        this.lookTargetController.tick(0, currentHeadYaw, baseSpeed);

        LungWyrmEntity wyrm = this.getEntity();
        float yaw = this.lookTargetController.getDesiredYaw(),
              pitch = this.lookTargetController.getDesiredPitch();
        float lerpFactor = this.lookTargetController.getRotationSpeed();

        wyrm.joints.get(1).getSegmentPosAndDirection(null, bodyDir, 1F);
        wyrm.joints.get(2).getSegmentPosAndDirection(null, nBodyDir, 1F);
        nBodyDir.sub(bodyDir);
        bodyDir.sub(nBodyDir).normalize();
        float bodyYaw = (float) Math.atan2(-bodyDir.x(), bodyDir.z()) * Mth.RAD_TO_DEG,
              bodyPitch = (float) -Math.asin(Math.clamp(bodyDir.y(), -1, 1)) * Mth.RAD_TO_DEG;

        yaw = bodyYaw + Mth.clamp(Mth.degreesDifference(bodyYaw, yaw), -15f, 15f);
        pitch = bodyPitch + Mth.clamp(Mth.degreesDifference(bodyPitch, pitch), -15f, 15f);

        currentHeadYaw = Mth.rotLerp(lerpFactor, currentHeadYaw, yaw);
        currentHeadPitch = Mth.rotLerp(lerpFactor, currentHeadPitch, pitch);

        wyrm.setYHeadRot(currentHeadYaw);
        wyrm.setXRot(currentHeadPitch);
    }
}
