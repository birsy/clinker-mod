package birsy.clinker.common.world.entity.gnomad.gnomind.behaviors.delivery;

import birsy.clinker.common.world.entity.gnomad.SuppliesHolder;
import birsy.clinker.common.world.entity.gnomad.SuppliesDeliverer;
import birsy.clinker.common.world.entity.system.squad.SquadMember;
import birsy.clinker.common.world.entity.system.squad.SquadTask;
import birsy.clinker.common.world.entity.gnomad.gnomind.squadtasks.ResupplyTask;
import birsy.clinker.core.registry.entity.ClinkerMemoryModules;
import com.mojang.datafixers.util.Pair;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.EntityTracker;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.phys.Vec3;
import net.tslat.smartbrainlib.api.core.behaviour.ExtendedBehaviour;
import net.tslat.smartbrainlib.object.MemoryTest;
import net.tslat.smartbrainlib.util.BrainUtils;
import net.tslat.smartbrainlib.util.EntityRetrievalUtil;

import java.util.List;
import java.util.function.Predicate;

public class SetWalkTargetToDeliveryTarget<E extends LivingEntity & SuppliesDeliverer> extends ExtendedBehaviour<E> {
    private static final MemoryTest MEMORY_REQUIREMENTS = MemoryTest.builder(5)
            .usesMemory(ClinkerMemoryModules.DELIVERY_TARGET.get())
            .usesMemory(ClinkerMemoryModules.CURRENTLY_ASSIGNED_SQUAD_TASK.get())
            .usesMemory(ClinkerMemoryModules.IS_CURRENTLY_ASSIGNED_SQUAD_TASK_ACTIVE.get())
            .usesMemory(MemoryModuleType.WALK_TARGET)
            .usesMemory(MemoryModuleType.LOOK_TARGET);

    @Override
    protected List<Pair<MemoryModuleType<?>, MemoryStatus>> getMemoryRequirements() {
        return MEMORY_REQUIREMENTS;
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel level, E mob) {
        // if we're not currently holding a delivery, then we're not delivering
        if (!mob.isHoldingDelivery()) return false;

        SuppliesHolder currentDeliveryTarget = BrainUtils.getMemory(mob, ClinkerMemoryModules.DELIVERY_TARGET.get());

        SquadTask currentTask = BrainUtils.getMemory(mob, ClinkerMemoryModules.CURRENTLY_ASSIGNED_SQUAD_TASK.get());
        boolean isCurrentTaskActive = BrainUtils.hasMemory(mob, ClinkerMemoryModules.IS_CURRENTLY_ASSIGNED_SQUAD_TASK_ACTIVE.get());
        if (isCurrentTaskActive && currentTask instanceof ResupplyTask resupplyTask) {
            currentDeliveryTarget = resupplyTask.taskMaster().asEntity();
            BrainUtils.setMemory(mob, ClinkerMemoryModules.DELIVERY_TARGET.get(), currentDeliveryTarget);
        } else if (currentDeliveryTarget == null) {
            Predicate<Entity> entityPredicate;
            if (mob instanceof SquadMember<?> squadMember) {
                // if we're a squad member, only deliver to members of the same squad
                entityPredicate = (entity) -> {
                    if (!(entity instanceof SuppliesHolder)) return false;
                    if (entity instanceof SquadMember<?> otherSquadMember) {
                        return squadMember.getSquad() == otherSquadMember.getSquad();
                    }
                    return true;
                };
            } else {
                // otherwise just deliver to anyone, it doesn't matter
                entityPredicate = (entity) -> entity instanceof SuppliesHolder;
            }

            SuppliesHolder nearestEntityThatMightNeedSupplies =
                    EntityRetrievalUtil.getNearestEntity(mob, 32, entityPredicate);
            // no nearby entity found, nothing to do with supplies :(
            // so, just throw it away.
            if (nearestEntityThatMightNeedSupplies == null) {
                mob.setHoldingDelivery(false);
                return false;
            }
            currentDeliveryTarget = nearestEntityThatMightNeedSupplies;
            BrainUtils.setMemory(mob, ClinkerMemoryModules.DELIVERY_TARGET.get(), currentDeliveryTarget);
        }

        // if we're already close to the delivery target, we don't need to move towards it
        Vec3 deliveryPoint = currentDeliveryTarget.position();
        if (deliveryPoint.distanceTo(mob.position()) < 1.5F) return false;
        return true;
    }

    @Override
    protected void start(E mob) {
        SuppliesHolder deliveryTarget = BrainUtils.getMemory(mob, ClinkerMemoryModules.DELIVERY_TARGET.get());
        BrainUtils.setMemory(
                mob, MemoryModuleType.WALK_TARGET,
                new WalkTarget(
                        deliveryTarget.suppliesHolderAsEntity(),
                        1.5F, 2
                )
        );
        BrainUtils.setMemory(
                mob, MemoryModuleType.LOOK_TARGET,
                new EntityTracker(deliveryTarget.suppliesHolderAsEntity(), true)
        );
    }
}
