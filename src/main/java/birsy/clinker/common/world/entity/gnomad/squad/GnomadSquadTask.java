package birsy.clinker.common.world.entity.gnomad.squad;

import birsy.clinker.common.world.entity.gnomad.GnomadEntity;
import org.jetbrains.annotations.Nullable;

public abstract class GnomadSquadTask {
    protected int ticksSinceLastUpdate;
    protected int tickRate = 0;

    protected int ticksExisted;
    protected int timeOutTime;

    boolean shouldRemove = false;

    private boolean initialized = false;
    private boolean assigned = false;
    protected GnomadEntity assignee;

    protected GnomadSquadTask(int timeOutTime) {
        this.timeOutTime = timeOutTime;
    }

    public final boolean isInitialized() {
        return initialized;
    }

    public final void initialize() {
        this.initialized = true;
        this.start();
    }

    protected void attemptInvalidate() {
        if (this.ticksExisted > this.timeOutTime) {
            this.fail(GnomadSquadTask.FailureType.TIMED_OUT);
        }
        if (this.assigned && (this.assignee == null || this.assignee.isDeadOrDying() || this.assignee.isRemoved())) {
            this.fail(GnomadSquadTask.FailureType.ASSIGNEE_DIED);
        }
    }

    protected void start() {}

    protected void tick() {}

    protected void fail(FailureType failureType) {
        this.shouldRemove = true;
    }

    protected void succeed() {
        this.shouldRemove = true;
    }

    public void assign(GnomadEntity assignee) {
        this.assignee = assignee;
        this.assigned = true;
    }

    public GnomadEntity getAssignee() {
        return this.assignee;
    }

    public boolean isAssigned() {
        return this.assignee != null;
    }

    public int remainingTime() {
        return this.timeOutTime - this.ticksExisted;
    }

    public enum FailureType {
        TIMED_OUT, SQUAD_DISBANDED, ASSIGNEE_DIED, ASSIGNER_DIED;
    }
}
