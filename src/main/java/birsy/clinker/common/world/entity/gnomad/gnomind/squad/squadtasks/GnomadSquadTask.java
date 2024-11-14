package birsy.clinker.common.world.entity.gnomad.gnomind.squad.squadtasks;

import birsy.clinker.common.world.entity.gnomad.GnomadEntity;
import birsy.clinker.core.Clinker;
import birsy.clinker.core.registry.entity.ClinkerMemoryModules;
import net.minecraft.network.protocol.game.DebugEntityNameGenerator;
import net.tslat.smartbrainlib.util.BrainUtils;

import java.util.*;

public abstract class GnomadSquadTask {
    public int ticksSinceLastUpdate = 0, ticksPerUpdate = 1, ticksExisted = 0, timeOutTime = 200;

    public boolean hasBegun = false;
    public boolean markedForRemoval = false;

    public final int minVolunteers;
    public final int maxVolunteers;
    public final Map<UUID, GnomadEntity> volunteers;
    public final GnomadEntity taskmaster;

    protected GnomadSquadTask(int minVolunteers, int maxVolunteers, int timeOutTime, GnomadEntity taskmaster) {
        this.timeOutTime = timeOutTime;
        this.minVolunteers = Math.max(0, minVolunteers);
        this.maxVolunteers = Math.max(0, maxVolunteers);
        this.taskmaster = taskmaster;
        this.volunteers = new HashMap<>();
    }

    protected GnomadSquadTask(int volunteers, int timeOutTime, GnomadEntity taskmaster) {
        this(volunteers, volunteers, timeOutTime, taskmaster);
    }

    public boolean shouldBegin() {
        return this.volunteers.size() > minVolunteers;
    }

    public void begin() {}

    public void update(boolean hasBegun) {}

    public Optional<FailureType> shouldFail() {
        if (taskmaster == null || taskmaster.isDeadOrDying() || taskmaster.isRemoved()) return Optional.of(FailureType.TASKMASTER_DIED);
        if (this.volunteers.size() > minVolunteers && this.hasBegun) return Optional.of(FailureType.NO_VOLUNTEERS);
        if (this.remainingTime() <= 0) return Optional.of(FailureType.TIMED_OUT);
        return Optional.empty();
    }

    public void fail(FailureType failureType) {}

    public boolean shouldSucceed() {
        return true;
    }

    public void succeed() {}

    public boolean canVolunteer(GnomadEntity volunteer) {
        return volunteer != taskmaster;
    }

    public boolean shouldRemoveVolunteer(GnomadEntity volunteer) {
        return volunteer == null || volunteer.isDeadOrDying() || volunteer.isRemoved();
    }

    public boolean volunteer(GnomadEntity volunteer) {
        if (maxVolunteers != 0 && volunteers.size() > maxVolunteers) return false;
        if (!canVolunteer(volunteer)) return false;
        volunteers.put(volunteer.getUUID(), volunteer);
        return true;
    }

    public void leave(GnomadEntity volunteer) {
        volunteers.remove(volunteer.getUUID());
    }

    public int remainingTime() {
        return this.timeOutTime - this.ticksExisted;
    }

    public static String debugName(GnomadSquadTask task) {
        return DebugEntityNameGenerator.getEntityName(task.taskmaster) + "'s " + task.getClass().getSimpleName();
    }

    public enum FailureType {
        TIMED_OUT, SQUAD_DISBANDED, NO_VOLUNTEERS, TASKMASTER_DIED;
    }
}
