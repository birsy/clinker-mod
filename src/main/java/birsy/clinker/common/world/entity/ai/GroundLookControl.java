package birsy.clinker.common.world.entity.ai;

import birsy.clinker.common.world.entity.GroundLocomoteEntity;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.control.BodyRotationControl;
import net.minecraft.world.entity.ai.control.LookControl;

public class GroundLookControl extends LookControl {
    public GroundLookControl(GroundLocomoteEntity pMob) {
        super(pMob);
    }

    @Override
    protected boolean resetXRotOnTick() {
        return false;
    }

    @Override
    public void tick() {
        float desiredYAngle = this.getYRotD().orElse(this.mob.yBodyRot);
        float desiredXAngle = this.getXRotD().orElse(0.0F);

        if (this.mob.getMoveControl().hasWanted()) {
            float dX = (float) (this.mob.getMoveControl().getWantedX() - this.mob.getX());
            float dZ = (float) (this.mob.getMoveControl().getWantedZ() - this.mob.getZ());
            //desiredYAngle = ((float) Mth.atan2(dZ, dX) * Mth.RAD_TO_DEG) - 90;
        }

        float lerpFactor = 0.2F;
        this.mob.yHeadRot = Mth.rotLerp(lerpFactor, this.mob.yHeadRot, desiredYAngle);
        this.mob.setXRot(Mth.rotLerp(lerpFactor, this.mob.getXRot(), desiredXAngle));


        this.clampHeadRotationToBody();
    }
}
