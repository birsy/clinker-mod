package birsy.clinker.common.world.entity.rope;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.control.BodyRotationControl;
import net.minecraft.world.entity.ai.control.LookControl;

public class RopeLookController extends LookControl {
    public RopeLookController(RopeEntity pMob) {
        super(pMob);
    }



    public static class RopeBodyRotationControl extends BodyRotationControl {
        public RopeBodyRotationControl(RopeEntity pMob) {
            super(pMob);
        }

        @Override
        public void clientTick() {
            // if we're not walking, don't do shit!!

            // if we are walking, then move a particular amount per tick.
        }
    }
}
