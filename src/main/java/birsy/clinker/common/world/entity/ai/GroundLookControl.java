package birsy.clinker.common.world.entity.ai;

import birsy.clinker.common.world.entity.GroundLocomoteEntity;
import birsy.clinker.core.util.PropertyModifierStack;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.control.BodyRotationControl;
import net.minecraft.world.entity.ai.control.LookControl;

import java.util.Optional;

public class GroundLookControl extends LookControl {
    protected LookTargetType lookTargetType;
    protected float wantedPitch, wantedYaw;

    public final PropertyModifierStack<Float> rotationLerpSpeed = new PropertyModifierStack<>(0.05F, 8);

    public GroundLookControl(GroundLocomoteEntity pMob) {
        super(pMob);
    }

    public GroundLocomoteEntity getEntity() {
        return (GroundLocomoteEntity) this.mob;
    }

    @Override
    protected boolean resetXRotOnTick() {
        return false;
    }

    @Override
    public void setLookAt(double x, double y, double z, float deltaYaw, float deltaPitch) {
        this.wantedX = x;
        this.wantedY = y;
        this.wantedZ = z;
        this.yMaxRotSpeed = deltaYaw;
        this.xMaxRotAngle = deltaPitch;
        this.lookAtCooldown = 2;

        this.lookTargetType = LookTargetType.POSITION;
    }

    public void setLookAt(float pitch, float yaw) {
       this.setLookAt(pitch, yaw, this.yMaxRotSpeed, this.xMaxRotAngle);
    }

    public void setLookAt(float pitch, float yaw, float deltaYaw, float deltaPitch) {
        this.wantedPitch = pitch;
        this.wantedYaw = yaw;
        this.yMaxRotSpeed = deltaYaw;
        this.xMaxRotAngle = deltaPitch;

        this.lookTargetType = LookTargetType.ANGLE;
    }

    public void clearLookTarget() {
        this.lookTargetType = LookTargetType.NONE;
    }

    @Override
    protected Optional<Float> getXRotD() {
        return switch (this.lookTargetType) {
            case POSITION -> super.getXRotD();
            case ANGLE -> Optional.of(this.wantedPitch);
            default -> Optional.empty();
        };
    }

    @Override
    protected Optional<Float> getYRotD() {
        return switch (this.lookTargetType) {
            case POSITION -> super.getYRotD();
            case ANGLE -> Optional.of(this.wantedYaw);
            default -> Optional.empty();
        };
    }

    @Override
    public void tick() {
        GroundLocomoteEntity me = this.getEntity();

        float desiredYAngle = Mth.wrapDegrees(this.getYRotD().orElse(me.getYRot()));
        float desiredXAngle = Mth.wrapDegrees(this.getXRotD().orElse(me.getXRot()));

        float lerpFactor = rotationLerpSpeed.value();

        me.yHeadRot = Mth.wrapDegrees(rotateTowards(me.yHeadRot, Mth.rotLerp(lerpFactor, me.yHeadRot, desiredYAngle), this.yMaxRotSpeed));
        me.setXRot(   Mth.wrapDegrees(rotateTowards(me.getXRot(), Mth.rotLerp(lerpFactor, me.getXRot(), desiredXAngle), this.xMaxRotAngle)));

        this.clampHeadRotationToBody();
    }

    enum LookTargetType {
        NONE, POSITION, ANGLE
    }
}
