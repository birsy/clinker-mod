package birsy.clinker.common.world.entity.ai;

import birsy.clinker.common.world.entity.GroundLocomoteEntity;
import birsy.clinker.core.util.PropertyModifierStack;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.control.BodyRotationControl;
import net.minecraft.world.entity.ai.control.LookControl;

public class GroundLookControl extends LookControl {
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
    public void tick() {
        GroundLocomoteEntity me = this.getEntity();

        float desiredYAngle = Mth.wrapDegrees(this.getYRotD().orElse(me.yBodyRot));
        float desiredXAngle = Mth.wrapDegrees(this.getXRotD().orElse(0.0F));

        float lerpFactor = rotationLerpSpeed.value();

        me.yHeadRot = Mth.wrapDegrees(rotateTowards(me.yHeadRot, Mth.rotLerp(lerpFactor, me.yHeadRot, desiredYAngle), 100));
        me.setXRot(   Mth.wrapDegrees(rotateTowards(me.getXRot(), Mth.rotLerp(lerpFactor, me.getXRot(), desiredXAngle), 100)));

        this.clampHeadRotationToBody();
    }
}
