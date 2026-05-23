package birsy.clinker.common.world.entity.gnomad.gnomind.squadtasks;

import birsy.clinker.common.world.entity.system.squad.SquadMember;
import birsy.clinker.common.world.entity.system.squad.SquadTask;
import net.minecraft.core.GlobalPos;

public class RelaxWithSquadTask extends SquadTask {
    public final GlobalPos relaxationPoint;

    public RelaxWithSquadTask(SquadMember<?> taskmaster, GlobalPos relaxationPoint) {
        super(taskmaster, 1, 5, 20 * 60, 20 * 120, Integer.MIN_VALUE);
        this.relaxationPoint = relaxationPoint;
    }

    @Override
    public boolean shouldSucceed() { return false; }
}
