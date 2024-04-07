package birsy.clinker.common.world.entity.gnomad.squad;

import birsy.clinker.common.world.entity.gnomad.GnomadEntity;

public abstract class GnomadSquadTaskWithAssigner extends GnomadSquadTask {
    private final GnomadEntity assinger;

    protected GnomadSquadTaskWithAssigner(GnomadEntity assigner, int timeOutTime) {
        super(timeOutTime);
        this.assinger = assigner;
    }

    @Override
    protected void attemptInvalidate() {
        super.attemptInvalidate();
        if (this.isAssignerInvalid()) this.fail(FailureType.ASSIGNER_DIED);
    }

    public boolean isAssignerInvalid() {
        return assinger == null || assinger.isDeadOrDying() || assinger.isRemoved();
    }
}
