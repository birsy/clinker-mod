package birsy.clinker.common.world.entity.gnomad.gnomind.behaviors.delivery;

import birsy.clinker.common.world.entity.gnomad.SuppliesDeliverer;
import birsy.clinker.common.world.entity.gnomad.gnomind.squad.SquadTask;
import birsy.clinker.common.world.entity.gnomad.gnomind.squad.squadtasks.ResupplyTask;
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

class SetWalkTargetToSupplyDepot<E extends LivingEntity & SuppliesDeliverer> extends ExtendedBehaviour<E> {
    private static final MemoryTest MEMORY_REQUIREMENTS = MemoryTest.builder(4)
            .hasMemory(ClinkerMemoryModules.NEAREST_SUPPLY_DEPOT.get())
            .hasMemory(ClinkerMemoryModules.CURRENTLY_ASSIGNED_SQUAD_TASK.get())
            .hasMemory(ClinkerMemoryModules.IS_CURRENTLY_ASSIGNED_SQUAD_TASK_ACTIVE.get())
            .usesMemory(MemoryModuleType.WALK_TARGET);
    float minimumDistance = 2.5F, minimumDistanceSqr = minimumDistance * minimumDistance;

    @Override
    protected List<Pair<MemoryModuleType<?>, MemoryStatus>> getMemoryRequirements() {
        return MEMORY_REQUIREMENTS;
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel level, E mob) {
        // if we're already holding a delivery we don't need to fetch new supplies
        if (mob.isHoldingDelivery()) return false;
        // check if our current task is a resupply task
        SquadTask currentTask = BrainUtils.getMemory(mob, ClinkerMemoryModules.CURRENTLY_ASSIGNED_SQUAD_TASK.get());
        if (!(currentTask instanceof ResupplyTask) || !currentTask.isActive()) return false;
        // if we're already close to the supply depot, we don't need to move towards it
        BlockPos supplyDepotPos = BrainUtils.getMemory(mob, ClinkerMemoryModules.NEAREST_SUPPLY_DEPOT.get()).pos();
        if (supplyDepotPos.distToCenterSqr(mob.position()) < minimumDistanceSqr) return false;
        // if we're not holding a delivery + are resupplying + far away from the depot, move to the depot
        return true;
    }

    @Override
    protected void start(E mob) {
        BrainUtils.setMemory(
                mob, MemoryModuleType.WALK_TARGET,
                new WalkTarget(
                        BrainUtils.getMemory(mob, ClinkerMemoryModules.NEAREST_SUPPLY_DEPOT.get()).pos(),
                        1.5F, 2
                )
        );
    }
}
