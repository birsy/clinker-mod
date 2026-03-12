package birsy.clinker.common.world.entity.gnomad.gnomind.behaviors.delivery;

import birsy.clinker.common.world.entity.gnomad.SuppliesDeliverer;
import birsy.clinker.common.world.entity.gnomad.gnomind.squad.SquadTask;
import birsy.clinker.common.world.entity.gnomad.gnomind.squad.squadtasks.ResupplyTask;
import birsy.clinker.core.registry.entity.ClinkerMemoryModules;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.BlockPosTracker;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.tslat.smartbrainlib.api.core.behaviour.HeldBehaviour;
import net.tslat.smartbrainlib.object.MemoryTest;
import net.tslat.smartbrainlib.util.BrainUtils;

import java.util.List;

class FetchSuppliesFromSupplyDepot<E extends LivingEntity & SuppliesDeliverer> extends HeldBehaviour<E> {
    private static final MemoryTest MEMORY_REQUIREMENTS = MemoryTest.builder(5)
            .hasMemory(ClinkerMemoryModules.NEAREST_SUPPLY_DEPOT.get())
            .hasMemory(ClinkerMemoryModules.CURRENTLY_ASSIGNED_SQUAD_TASK.get())
            .hasMemory(ClinkerMemoryModules.IS_CURRENTLY_ASSIGNED_SQUAD_TASK_ACTIVE.get())
            .usesMemory(MemoryModuleType.WALK_TARGET)
            .usesMemory(MemoryModuleType.LOOK_TARGET);

    FetchSuppliesFromSupplyDepot() {
        super();
        this.onTick(this::doTick);
    }

    @Override
    protected List<Pair<MemoryModuleType<?>, MemoryStatus>> getMemoryRequirements() {
        return MEMORY_REQUIREMENTS;
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel level, E mob) {
        // if we're already holding a delivery so we don't need to fetch new supplies
        if (mob.isHoldingDelivery()) return false;
        // check if our current task is a resupply task
        SquadTask currentTask = BrainUtils.getMemory(mob, ClinkerMemoryModules.CURRENTLY_ASSIGNED_SQUAD_TASK.get());
        if (!(currentTask instanceof ResupplyTask)) return false;
        // if we're far away from the supply target, we can't fetch supplies!
        BlockPos supplyDepotPos = BrainUtils.getMemory(mob, ClinkerMemoryModules.NEAREST_SUPPLY_DEPOT.get()).pos();
        return Math.sqrt(supplyDepotPos.distToCenterSqr(mob.position())) < 1.5F;
    }

    protected boolean doTick(E mob) {
        // stop moving and look at the depot
        BrainUtils.clearMemory(mob, MemoryModuleType.WALK_TARGET);
        BrainUtils.setMemory(
                mob, MemoryModuleType.LOOK_TARGET,
                new BlockPosTracker(BrainUtils.getMemory(mob, ClinkerMemoryModules.NEAREST_SUPPLY_DEPOT.get()).pos())
        );
        // stand there for two seconds and then hold the delivery
        if (this.runningTime > 40) {
            mob.setHoldingDelivery(true);
            return false;
        }
        return true;
    }
}
