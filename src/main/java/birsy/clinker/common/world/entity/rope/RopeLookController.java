package birsy.clinker.common.world.entity.rope;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.control.BodyRotationControl;
import net.minecraft.world.entity.ai.control.LookControl;
import org.joml.Quaterniond;
import org.joml.Vector3d;
import org.joml.Vector3dc;

public class RopeLookController extends LookControl {
    public RopeLookController(RopeEntity pMob) {
        super(pMob);
    }

    public RopeEntity<? extends RopeEntitySegment> getEntity() {
        return (RopeEntity<? extends RopeEntitySegment>) this.mob;
    }

    Quaterniond rotationA = new Quaterniond();
    Quaterniond rotationB = new Quaterniond();

    Vector3d headDirection = new Vector3d();
    @Override
    protected void clampHeadRotationToBody() {
        Vector3dc bodyDirection = this.getEntity().segments.get(0).getAttachmentDirection(1.0F);
        // get the look vector....
        this.headDirection.set(this.mob.getLookAngle().x(), this.mob.getLookAngle().y(), this.mob.getLookAngle().z());

        // transform the look vector such that z = the body direction
        rotationA.set(0, 0, 0, 1).rotationTo(
                bodyDirection.x(), bodyDirection.y(), bodyDirection.z(),
                0, 0, 1);
        rotationA.transform(headDirection);

        // clamp it such that the angle never exceeds uhhhh like probably 70 degrees is good??
        double yRot = Mth.rotateIfNecessary(
                (float) (Mth.atan2(headDirection.z(), headDirection.x()) * Mth.RAD_TO_DEG),
                0.0F,
                70.0F
        ) * Mth.DEG_TO_RAD;
        double xRot = Mth.rotateIfNecessary(
                (float) (Math.acos(headDirection.y()) * Mth.RAD_TO_DEG),
                0.0F,
                70.0F
        ) * Mth.DEG_TO_RAD;
        rotationB.set(0, 0, 0, 1).rotateY((float) yRot).rotateX((float) xRot - Mth.HALF_PI);
        rotationB.transform(0, 0, 1, headDirection);

        // undo that rotation
        rotationA.invert().transform(headDirection);

        // RECALCULATE THE ROTATIONS... ;~;
        this.mob.setXRot((float) Math.acos(headDirection.y()) * Mth.RAD_TO_DEG);
        this.mob.yHeadRot = (float) Mth.atan2(headDirection.z(), headDirection.x()) * Mth.RAD_TO_DEG;
    }

    public static class RopeBodyRotationControl extends BodyRotationControl {
        public RopeBodyRotationControl(RopeEntity pMob) {
            super(pMob);
        }

        @Override
        public void clientTick() {
            // Don't Do Shit : )
        }
    }
}
