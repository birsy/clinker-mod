package birsy.clinker.common.world.entity.gnomad.gnomind.squad;

import net.minecraft.world.entity.LivingEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public abstract class SquadTask {
    public final SquadMember<?> taskMaster;
    public final int minAssignees, maxAssignees;
    public final int priority;
    protected final List<SquadMember<?>> assignees = new ArrayList<>();

    Status status = Status.UNASSIGNED;
    FailureType failureType = null;
    final protected int waitTimeout, executionTimeout;
    protected int ticksExisted, stageTime;

    public SquadTask(SquadMember<?> taskMaster, int minAssignees, int maxAssignees, int waitTimeout, int executionTimeout, int priority) {
        this.taskMaster = taskMaster;
        this.minAssignees = minAssignees;
        this.maxAssignees = maxAssignees;
        this.waitTimeout = waitTimeout;
        this.executionTimeout = executionTimeout;
        this.priority = priority;
    }

    public boolean assign(SquadMember<?> assignee) {
        if (isAssigneeInvalid(assignee)) return false;
        if (!canBeAssigned(assignee)) return false;
        if (assignees.contains(assignee)) return false;
        assignees.add(assignee);
        return true;
    }

    public boolean unassign(SquadMember<?> assignee) {
        return assignees.remove(assignee);
    }
    public boolean canBeAssigned(SquadMember<?> assignee) {
        return assignees.size() < maxAssignees;
    }
    protected boolean isAssigneeInvalid(SquadMember<?> assignee) {
        LivingEntity entity = assignee.asEntity();
        return entity.isDeadOrDying() ||
               entity.isRemoved() ||
               entity.level() != taskMaster.asEntity().level() ||
               assignee.getSquad() != taskMaster.getSquad();
    }

    public boolean isPending() { return status == Status.UNASSIGNED; }
    public boolean isActive() { return status == Status.IN_PROGRESS; }
    public boolean isFinished() { return status == Status.SUCCEEDED || status == Status.FAILED; }

    // life cycle stuffs
    protected void tick() {
        ticksExisted++;
        stageTime++;
        // cull invalid assignees
        assignees.removeIf(this::isAssigneeInvalid);
        switch (this.status) {
            case SUCCEEDED, FAILED:
                return;
            case UNASSIGNED:
                if (shouldBegin()) begin();
                shouldFail().ifPresent(this::fail);
                break;
            case IN_PROGRESS:
                if (shouldSucceed()) succeed();
                else shouldFail().ifPresent(this::fail);
                break;
        }
    }

    protected boolean shouldBegin() {
        return assignees.size() >= minAssignees;
    }
    protected void begin() {
        this.status = Status.IN_PROGRESS;
        this.stageTime = 0;
        this.onBegin();
    }
    protected void onBegin() {}

    protected Optional<FailureType> shouldFail() {
        if (status == Status.UNASSIGNED && waitTimeout >= 0 && stageTime > waitTimeout)
            return Optional.of(FailureType.TIMED_OUT);

        if (status == Status.IN_PROGRESS && executionTimeout >= 0 && stageTime > executionTimeout)
            return Optional.of(FailureType.TIMED_OUT);

        if (taskMaster.getSquad() == null)
            return Optional.of(FailureType.SQUAD_DISBANDED);

        LivingEntity taskMasterEntity = taskMaster.asEntity();
        if (taskMasterEntity.isRemoved() || taskMasterEntity.isDeadOrDying() || taskMasterEntity.level() != taskMaster.getSquad().level)
            return Optional.of(FailureType.TASKMASTER_DIED);

        if (status == Status.IN_PROGRESS) {
            if (assignees.size() < minAssignees) return Optional.of(FailureType.NOT_ENOUGH_ASSIGNEES);
        }

        return Optional.empty();
    }
    public void fail(FailureType failureType) {
        this.status = Status.FAILED;
        this.failureType = failureType;
        this.stageTime = 0;
        onFailure();
    }
    protected void onFailure() {}

    public abstract boolean shouldSucceed();
    public void succeed() {
        this.status = Status.SUCCEEDED;
        this.stageTime = 0;
        onSuccess();
    }
    protected void onSuccess() {}

    protected enum Status { UNASSIGNED, IN_PROGRESS, SUCCEEDED, FAILED }
    public enum FailureType { TIMED_OUT, NOT_ENOUGH_ASSIGNEES, TASKMASTER_DIED, SQUAD_DISBANDED }
}
