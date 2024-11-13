package birsy.clinker.common.world.entity.ai;

import birsy.clinker.common.world.entity.GroundLocomoteEntity;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.control.BodyRotationControl;
import net.minecraft.world.entity.ai.control.LookControl;

public class GroundLookControl extends LookControl {
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
    public void tick() {
        GroundLocomoteEntity me = this.getEntity();

        float desiredYAngle = this.getYRotD().orElse(me.yBodyRot);
        float desiredXAngle = this.getXRotD().orElse(0.0F);

        float lerpFactor = 0.05F;
        if (me.isWatchingEntity()) lerpFactor = 0.8F;

        me.yHeadRot = rotateTowards(me.yHeadRot, Mth.rotLerp(lerpFactor, me.yHeadRot, desiredYAngle), 5);
        me.setXRot(   rotateTowards(me.getXRot(), Mth.rotLerp(lerpFactor, me.getXRot(), desiredXAngle), 5));

        this.clampHeadRotationToBody();
    }
}
