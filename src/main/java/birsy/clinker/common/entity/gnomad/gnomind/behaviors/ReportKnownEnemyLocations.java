package birsy.clinker.common.entity.gnomad.gnomind.behaviors;

import birsy.clinker.common.entity.gnomad.gnomind.LastKnownEntityPositionsTracker;
import birsy.clinker.common.entity.system.squad.Squad;
import birsy.clinker.common.entity.system.squad.SquadMember;
import birsy.clinker.core.registry.entity.ClinkerMemoryModules;
import com.mojang.datafixers.util.Pair;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.tslat.smartbrainlib.api.core.behaviour.ExtendedBehaviour;
import net.tslat.smartbrainlib.object.MemoryTest;
import net.tslat.smartbrainlib.util.BrainUtils;
import net.tslat.smartbrainlib.util.SensoryUtils;

import java.util.List;

public class ReportKnownEnemyLocations<E extends LivingEntity & SquadMember<E>> extends ExtendedBehaviour<E> {
    private static final MemoryTest MEMORY_REQUIREMENTS = MemoryTest.builder(2)
            .hasMemory(ClinkerMemoryModules.SQUAD.get())
            .hasMemory(ClinkerMemoryModules.LAST_KNOWN_ENEMY_POSITIONS.get());
    public float maximumDistanceFromLeader = 10.0F,
                 maximumDistanceFromLeaderSq = maximumDistanceFromLeader * maximumDistanceFromLeader;

    public ReportKnownEnemyLocations<E> maximumDistanceFromLeader(float maximumDistanceFromLeader) {
        this.maximumDistanceFromLeader = maximumDistanceFromLeader;
        this.maximumDistanceFromLeaderSq = maximumDistanceFromLeader * maximumDistanceFromLeader;
        return this;
    }

    @Override
    protected List<Pair<MemoryModuleType<?>, MemoryStatus>> getMemoryRequirements() {
        return MEMORY_REQUIREMENTS;
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel level, E entity) {
        Squad squad = BrainUtils.getMemory(entity, ClinkerMemoryModules.SQUAD.get());
        if (!squad.hasLeader())
            return false;

        LivingEntity squadLeader = squad.getLeader().asEntity();
        if (entity.distanceToSqr(squadLeader) > maximumDistanceFromLeaderSq)
            return false;
        if (!SensoryUtils.hasLineOfSight(entity, squadLeader)) {
            return false;
        }

        return true;
    }

    @Override
    protected void start(ServerLevel level, E entity, long gameTime) {
        super.start(level, entity, gameTime);
        LastKnownEntityPositionsTracker myLastKnownEnemyPositions =
                BrainUtils.getMemory(entity, ClinkerMemoryModules.LAST_KNOWN_ENEMY_POSITIONS.get());
        LastKnownEntityPositionsTracker squadLastKnownEntityPositions =
                BrainUtils.getMemory(entity, ClinkerMemoryModules.SQUAD.get()).lastKnownEnemyPositions;
        // update the squad's knowledge and our own
        squadLastKnownEntityPositions.updateTrackingFromOtherTracker(myLastKnownEnemyPositions);
        myLastKnownEnemyPositions.updateTrackingFromOtherTracker(squadLastKnownEntityPositions);
    }
}
