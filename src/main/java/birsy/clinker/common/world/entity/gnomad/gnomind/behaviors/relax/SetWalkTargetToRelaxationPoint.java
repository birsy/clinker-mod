package birsy.clinker.common.world.entity.gnomad.gnomind.behaviors.relax;

import birsy.clinker.common.world.entity.gnomad.SuppliesDeliverer;
import birsy.clinker.common.world.entity.gnomad.gnomind.squadtasks.RelaxWithSquadTask;
import birsy.clinker.common.world.entity.gnomad.gnomind.squadtasks.ResupplyTask;
import birsy.clinker.common.world.entity.system.squad.SquadTask;
import birsy.clinker.core.registry.entity.ClinkerMemoryModules;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.tslat.smartbrainlib.api.core.behaviour.ExtendedBehaviour;
import net.tslat.smartbrainlib.object.MemoryTest;
import net.tslat.smartbrainlib.util.BrainUtils;

import java.util.List;

public class SetWalkTargetToRelaxationPoint<E extends LivingEntity> extends ExtendedBehaviour<E> {
    private static final MemoryTest MEMORY_REQUIREMENTS = MemoryTest.builder(3)
            .usesMemory(ClinkerMemoryModules.CURRENTLY_ASSIGNED_SQUAD_TASK.get())
            .usesMemory(ClinkerMemoryModules.IS_CURRENTLY_ASSIGNED_SQUAD_TASK_ACTIVE.get())
            .usesMemory(MemoryModuleType.WALK_TARGET);
    float minimumDistance = 3.0F, minimumDistanceSqr = minimumDistance * minimumDistance;
    private BlockPos targetPos;

    public SetWalkTargetToRelaxationPoint<E> minimumDistance(float distance) {
        minimumDistance = distance;
        minimumDistanceSqr = minimumDistance * minimumDistance;
        return this;
    }

    @Override
    protected List<Pair<MemoryModuleType<?>, MemoryStatus>> getMemoryRequirements() {
        return MEMORY_REQUIREMENTS;
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel level, E mob) {
        // check if our current task is the relaxation task and is active
        SquadTask currentTask = BrainUtils.getMemory(mob, ClinkerMemoryModules.CURRENTLY_ASSIGNED_SQUAD_TASK.get());
        if (currentTask instanceof RelaxWithSquadTask relaxationTask && relaxationTask.isActive()) {

            // we have to be in the same dimension
            if (relaxationTask.relaxationPoint.dimension() != level.dimension()) return false;

            // if we're already close to the relaxation point, we dont need to walk to it
            BlockPos pos = relaxationTask.relaxationPoint.pos();
            if (pos.distToCenterSqr(mob.position()) < minimumDistanceSqr) return false;

            targetPos = pos;
            return true;
        }
        return false;
    }

    @Override
    protected void start(E mob) {
        BrainUtils.setMemory(
                mob, MemoryModuleType.WALK_TARGET,
                new WalkTarget(targetPos, 1.0F, (int) Math.floor(minimumDistance))
        );
    }
}
