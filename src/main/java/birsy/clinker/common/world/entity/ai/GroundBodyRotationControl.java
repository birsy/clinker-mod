package birsy.clinker.common.world.entity.ai;

import birsy.clinker.common.world.entity.GroundLocomoteEntity;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.control.BodyRotationControl;
import net.tslat.smartbrainlib.util.RandomUtil;

public class GroundBodyRotationControl extends BodyRotationControl {
    protected final GroundLocomoteEntity me;
    protected float desiredBodyRot = 0.0F;
    public GroundBodyRotationControl(GroundLocomoteEntity pMob) {
        super(pMob);
        this.me = pMob;
        desiredBodyRot = me.yBodyRot;
    }

    @Override
    public void clientTick() {
        float bodyRotLerpFactor = 0.2F;
        if (this.isMoving()) {
            float dX = me.walk.x;
            float dZ = me.walk.z;
            desiredBodyRot = ((float) Mth.atan2(dZ, dX) * Mth.RAD_TO_DEG) - 90;
        } else {
            // occasionally do a big correction...
            if (RandomUtil.oneInNChance(50)) {
                desiredBodyRot = me.yHeadRot;
            } else {
                desiredBodyRot= Mth.rotateIfNecessary(desiredBodyRot, me.yHeadRot, (float)me.getMaxHeadYRot());
            }
            bodyRotLerpFactor = Mth.lerp(Mth.sin(me.tickCount * 0.5F)*0.5F+0.5F, 0.05F, 0.1F);
        }
        me.yHeadRot = Mth.rotateIfNecessary(me.yHeadRot, me.yBodyRot, (float)me.getMaxHeadYRot());
        me.yBodyRot = Mth.rotLerp(bodyRotLerpFactor, me.yBodyRot, desiredBodyRot);
    }

    protected boolean isMoving() {
        return me.walk.length() > 0.01 || me.getMoveControl().hasWanted();
    }
}
