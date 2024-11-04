package birsy.clinker.common.world.entity.ai;

import birsy.clinker.common.world.entity.GroundLocomoteEntity;
import net.minecraft.world.entity.ai.control.BodyRotationControl;
import net.minecraft.world.entity.ai.control.LookControl;

public class GroundLookControl extends LookControl {
    public GroundLookControl(GroundLocomoteEntity pMob) {
        super(pMob);
    }
}
