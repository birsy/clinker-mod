package birsy.clinker.common.world.entity.system.squad.squadtasks;

import birsy.clinker.common.world.entity.gnomad.SuppliesHolder;
import birsy.clinker.common.world.entity.system.squad.SquadMember;
import birsy.clinker.common.world.entity.system.squad.SquadTask;

public class ResupplyTask extends SquadTask {
    public ResupplyTask(SquadMember<? extends SuppliesHolder> taskmaster) {
        super(taskmaster, 1, 1, 160, 600, 0);
    }

    @Override
    public boolean shouldSucceed() {
        SuppliesHolder taskMaster = (SuppliesHolder) this.taskMaster;
        return !taskMaster.outOfSupplies();
    }

    public SquadMember<? extends SuppliesHolder> taskMaster() {
        return (SquadMember<? extends SuppliesHolder>) this.taskMaster;
    }
}
