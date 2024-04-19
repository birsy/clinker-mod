package birsy.clinker.common.world.entity.gnomad.squad;

import birsy.clinker.common.world.entity.gnomad.GnomadEntity;

public class ResupplyTask extends GnomadSquadTaskWithAssigner {
    public ResupplyTask(GnomadEntity assigner) {
        super(assigner, 160);
    }

    @Override
    public void assign(GnomadEntity assignee) {
        super.assign(assignee);
        this.timeOutTime += 600; // extend the timeOut timer when a member is assigned
    }
}
