package birsy.clinker.common.world.entity.gnomad.gnomind.squad.squadtasks;

import birsy.clinker.common.world.entity.gnomad.GnomadEntity;

public class ResupplyTask extends GnomadSquadTask {
    public ResupplyTask(GnomadEntity taskmaster) {
        super(1, 160, taskmaster);
    }

    @Override
    public void begin() {
        this.timeOutTime += 600; // extend the timeOut timer when a member is assigned
    }
}
