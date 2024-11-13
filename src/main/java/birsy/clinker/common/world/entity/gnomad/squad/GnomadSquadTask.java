package birsy.clinker.common.world.entity.gnomad.squad;

import birsy.clinker.common.world.entity.gnomad.GnomadEntity;
import birsy.clinker.core.Clinker;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public abstract class GnomadSquadTask {
    int ticksSinceLastUpdate;
    int ticksPerUpdate = 1;

    int ticksExisted;
    int timeOutTime;

    boolean active = false;
    boolean shouldRemove = false;

    public final int minVolunteers;
    public final int maxVolunteers;
    public final List<GnomadEntity> volunteers;
    public final GnomadEntity taskmaster;

    protected GnomadSquadTask(int minVolunteers, int maxVolunteers, int timeOutTime, GnomadEntity taskmaster) {
        this.timeOutTime = timeOutTime;
        this.minVolunteers = Math.max(0, minVolunteers);
        this.maxVolunteers = Math.max(0, maxVolunteers);
        this.taskmaster = taskmaster;
        this.volunteers = new ArrayList<>();
    }

    protected GnomadSquadTask(int volunteers, int timeOutTime, GnomadEntity taskmaster) {
        this.timeOutTime = timeOutTime;
        this.minVolunteers = Math.max(0, volunteers);
        this.maxVolunteers = Math.max(0, volunteers);
        this.taskmaster = taskmaster;
        this.volunteers = new ArrayList<>();
    }

    protected boolean shouldRemoveVolunteer(GnomadEntity volunteer) {
        return volunteer == null || volunteer.isDeadOrDying() || volunteer.isRemoved();
    }

    protected boolean shouldBegin() {
        return this.volunteers.size() > minVolunteers;
    }

    protected boolean canVolunteer(GnomadEntity volunteer) {
        return volunteer != taskmaster;
    }

    protected boolean shouldSucceed() {
        return true;
    }

    protected Optional<FailureType> shouldFail() {
        if (taskmaster == null || taskmaster.isDeadOrDying() || taskmaster.isRemoved()) return Optional.of(FailureType.TASKMASTER_DIED);
        if (this.volunteers.size() > minVolunteers && this.active) return Optional.of(FailureType.NO_VOLUNTEERS);
        if (this.remainingTime() <= 0) return Optional.of(FailureType.TIMED_OUT);
        return Optional.empty();
    }

    protected void begin() {}

    protected void update(boolean isActive) {}

    protected void fail(FailureType failureType) {
        this.shouldRemove = true;
    }

    protected void succeed() {
        this.shouldRemove = true;
    }

    public boolean volunteer(GnomadEntity volunteer) {
        if (maxVolunteers != 0 && volunteers.size() > maxVolunteers) return false;
        if (!canVolunteer(volunteer)) return false;
        if (volunteer.activeTasks.contains(this)) return false;
        volunteer.activeTasks.add(this);
        return volunteers.add(volunteer);
    }

    public boolean leave(GnomadEntity volunteer) {
        return volunteers.remove(volunteer);
    }

    public int remainingTime() {
        return this.timeOutTime - this.ticksExisted;
    }

    public enum FailureType {
        TIMED_OUT, SQUAD_DISBANDED, NO_VOLUNTEERS, TASKMASTER_DIED;
    }
}
