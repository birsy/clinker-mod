package birsy.clinker.common.entity.ai;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.control.MoveControl;

public class ClinkerMoveController extends MoveControl {
    public boolean canSwivel;
    public float acceptableDistanceFromNode;
    public float xTurnAmount; //Used to interpolate between current angle and desired angle.
    public float yTurnAmount; //1.0 being able to turn exactly to the desired direction each tick, 0.0 being totally unable to turn.

    public ClinkerMoveController(Mob pMob) {
        super(pMob);
    }
}
