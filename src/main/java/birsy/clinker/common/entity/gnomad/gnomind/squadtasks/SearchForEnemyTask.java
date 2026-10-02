package birsy.clinker.common.entity.gnomad.gnomind.squadtasks;

import birsy.clinker.common.entity.gnomad.gnomind.memories.LastKnownEntityPosition;
import birsy.clinker.common.entity.gnomad.gnomind.memories.LastKnownEntityPositionsTracker;
import birsy.clinker.common.entity.module.modules.SquadMember;
import birsy.clinker.common.entity.system.squad.SquadTask;

import java.util.Optional;
import java.util.UUID;

public class SearchForEnemyTask extends SquadTask {
    final LastKnownEntityPositionsTracker tracker;
    final UUID entityUUID;

    public SearchForEnemyTask(SquadMember<?> taskmaster, LastKnownEntityPositionsTracker tracker, UUID entityUUID) {
        super(taskmaster, 1, 1, 160, 600, 0);
        this.tracker = tracker;
        this.entityUUID = entityUUID;
    }

    @Override
    protected Optional<FailureReason> shouldFail() {
        Optional<FailureReason> shouldFail = super.shouldFail();
        if (shouldFail.isPresent())
            return shouldFail;
        if (tracker.lastKnownPosition(entityUUID) == null)
            return Optional.of(FailureReason.NONE);
        return Optional.empty();
    }

    @Override
    public boolean shouldSucceed() {
        LastKnownEntityPosition entityPosition = tracker.lastKnownPosition(entityUUID);
        if (entityPosition != null)
            return entityPosition.state() == LastKnownEntityPosition.State.KNOWN;
        return false;
    }
}
