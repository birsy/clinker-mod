package birsy.clinker.common.entity.gnomad.gnomind.behaviors;

import birsy.clinker.common.entity.system.squad.Squad;
import birsy.clinker.common.entity.system.squad.SquadMember;
import birsy.clinker.core.registry.entity.ClinkerMemoryModules;
import com.mojang.datafixers.util.Pair;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.Vec3;
import net.tslat.smartbrainlib.api.core.behaviour.ExtendedBehaviour;
import net.tslat.smartbrainlib.object.MemoryTest;
import net.tslat.smartbrainlib.util.BrainUtils;

import java.util.List;

public class StayNearSquadCenter<E extends PathfinderMob & SquadMember<E>> extends ExtendedBehaviour<E> {
    private static final MemoryTest MEMORY_REQUIREMENTS = MemoryTest.builder(1)
            .hasMemory(ClinkerMemoryModules.SQUAD.get());

    private float maximumDistance = 16.0F, maximumDistanceSqr = maximumDistance * maximumDistance;
    private float speedModifier = 1.0F;
    private Path path = null;

    public StayNearSquadCenter<E> maximumDistance(float maximumDistance) {
        this.maximumDistance = maximumDistance;
        this.maximumDistanceSqr = maximumDistance * maximumDistance;
        return this;
    }

    public StayNearSquadCenter<E> speedModifier(float mod) {
        this.speedModifier = mod;
        return this;
    }

    @Override
    protected List<Pair<MemoryModuleType<?>, MemoryStatus>> getMemoryRequirements() {
        return MEMORY_REQUIREMENTS;
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel level, E entity) {
        Squad squad = BrainUtils.getMemory(entity, ClinkerMemoryModules.SQUAD.get());
        if (squad == null) return false;
        if (squad.size() <= 1) return false;

        Vec3 squadCenter = squad.getCenter(entity);
        double distToSquadCenterSqr = entity.distanceToSqr(squadCenter);
        if (distToSquadCenterSqr <= this.maximumDistanceSqr) return false;

        int radius = (int)(Math.sqrt(distToSquadCenterSqr) - this.maximumDistance) + 12;
        Vec3 targetPos = DefaultRandomPos.getPosTowards(entity, radius, 7, squadCenter, Mth.HALF_PI * 0.5);
        if (targetPos == null) return false;

        this.path = entity.getNavigation().createPath(targetPos.x, targetPos.y, targetPos.z, 0);
        return this.path != null;
    }

    @Override
    protected boolean shouldKeepRunning(E entity) {
        return !this.path.isDone();
    }

    @Override
    protected void start(E entity) {
        entity.getNavigation().moveTo(this.path, this.speedModifier);
    }

    @Override
    protected void stop(E entity) {
        this.path = null;
        entity.getNavigation().setSpeedModifier(1);
    }
}
