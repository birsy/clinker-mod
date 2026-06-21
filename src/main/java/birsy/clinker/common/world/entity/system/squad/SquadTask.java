package birsy.clinker.common.world.entity.system.squad;

import birsy.clinker.core.registry.entity.ClinkerMemoryModules;
import net.minecraft.world.entity.LivingEntity;
import net.tslat.smartbrainlib.util.BrainUtils;

import java.util.*;

public abstract class SquadTask {
    public final SquadMember<?> taskMaster;
    public final int minAssignees, maxAssignees;
    public final int priority;
    protected final List<SquadMember<?>> assignees = new ArrayList<>();

    Status status = Status.UNASSIGNED;
    FailureReason failureReason = null;
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
        // do memory stuff
        if (assignee instanceof LivingEntity livingEntity)
            BrainUtils.setMemory(livingEntity, ClinkerMemoryModules.ASSIGNED_SQUAD_TASK.get(), this);
        return true;
    }

    public boolean unassign(SquadMember<?> assignee) {
        boolean actuallyRemoved = assignees.remove(assignee);
        // do memory stuff
        if (assignee instanceof LivingEntity livingEntity) {
            if (BrainUtils.getMemory(livingEntity, ClinkerMemoryModules.ASSIGNED_SQUAD_TASK.get()) == this)
                BrainUtils.clearMemory(livingEntity, ClinkerMemoryModules.ASSIGNED_SQUAD_TASK.get());
        }
        return actuallyRemoved;
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
    protected void post() {
        if (taskMaster instanceof LivingEntity livingEntity) {
            Set<SquadTask> postedTasks = BrainUtils.memoryOrDefault(livingEntity, ClinkerMemoryModules.POSTED_SQUAD_TASKS.get(), HashSet::new);
            postedTasks.add(this);
            BrainUtils.setMemory(livingEntity, ClinkerMemoryModules.POSTED_SQUAD_TASKS.get(), postedTasks);
        }
        onPosted();
    }
    protected void onPosted() {}

    protected void tick() {
        ticksExisted++;
        stageTime++;
        // cull invalid assignees
        List<SquadMember<?>> toRemove = null;
        for (SquadMember<?> assignee : assignees) {
            if (isAssigneeInvalid(assignee)) {
                if (toRemove == null) toRemove = new ArrayList<>();
                toRemove.add(assignee);
            }
        }
        if (toRemove != null) toRemove.forEach(this::unassign);

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
        onBegin();
    }
    protected void onBegin() {}

    protected Optional<FailureReason> shouldFail() {
        if (status == Status.UNASSIGNED && waitTimeout >= 0 && stageTime > waitTimeout)
            return Optional.of(FailureReason.TIMED_OUT);

        if (status == Status.IN_PROGRESS && executionTimeout >= 0 && stageTime > executionTimeout)
            return Optional.of(FailureReason.TIMED_OUT);

        if (taskMaster.getSquad() == null)
            return Optional.of(FailureReason.SQUAD_DISBANDED);

        LivingEntity taskMasterEntity = taskMaster.asEntity();
        if (taskMasterEntity.isRemoved() || taskMasterEntity.isDeadOrDying() || taskMasterEntity.level() != taskMaster.getSquad().level)
            return Optional.of(FailureReason.TASKMASTER_DIED);

        if (status == Status.IN_PROGRESS) {
            if (assignees.size() < minAssignees) return Optional.of(FailureReason.NOT_ENOUGH_ASSIGNEES);
        }

        return Optional.empty();
    }
    public void fail(FailureReason failureReason) {
        this.status = Status.FAILED;
        this.failureReason = failureReason;
        this.stageTime = 0;
        onFailure();
        onStop();
        cleanup();
    }
    protected void onFailure() {}

    public abstract boolean shouldSucceed();
    public void succeed() {
        this.status = Status.SUCCEEDED;
        this.stageTime = 0;
        onSuccess();
        onStop();
        cleanup();
    }
    protected void onSuccess() {}

    protected void onStop() {}

    protected void cleanup() {
        if (taskMaster instanceof LivingEntity livingEntity) {
            Set<SquadTask> postedTasks = BrainUtils.getMemory(livingEntity, ClinkerMemoryModules.POSTED_SQUAD_TASKS.get());
            if (postedTasks != null) {
                postedTasks.remove(this);
                BrainUtils.setMemory(livingEntity, ClinkerMemoryModules.POSTED_SQUAD_TASKS.get(), postedTasks);
            }
        }
        List<SquadMember<?>> toUnassign = new ArrayList<>(assignees);
        toUnassign.forEach(this::unassign);
    }

    protected enum Status { UNASSIGNED, IN_PROGRESS, SUCCEEDED, FAILED }
    public enum FailureReason { NONE, TIMED_OUT, NOT_ENOUGH_ASSIGNEES, TASKMASTER_DIED, SQUAD_DISBANDED }
}
