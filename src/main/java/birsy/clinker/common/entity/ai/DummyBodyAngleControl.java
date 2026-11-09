package birsy.clinker.common.entity.ai;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.control.BodyRotationControl;

public class DummyBodyAngleControl extends BodyRotationControl {
    private final Mob mob;

    public DummyBodyAngleControl(Mob mob) {
        super(mob);
        this.mob = mob;
    }

    @Override
    public void clientTick() { this.mob.yBodyRot = this.mob.getYRot(); }
}
