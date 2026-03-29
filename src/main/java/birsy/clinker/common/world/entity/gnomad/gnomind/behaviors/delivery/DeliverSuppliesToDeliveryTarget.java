package birsy.clinker.common.world.entity.gnomad.gnomind.behaviors.delivery;

import birsy.clinker.common.world.entity.gnomad.SuppliesHolder;
import birsy.clinker.common.world.entity.gnomad.SuppliesDeliverer;
import birsy.clinker.common.world.entity.gnomad.gnomind.squad.Squad;
import birsy.clinker.common.world.entity.gnomad.gnomind.squad.SquadTask;
import birsy.clinker.common.world.entity.gnomad.gnomind.squad.squadtasks.ResupplyTask;
import birsy.clinker.core.registry.entity.ClinkerMemoryModules;
import com.mojang.datafixers.util.Pair;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.EntityTracker;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.phys.Vec3;
import net.tslat.smartbrainlib.api.core.behaviour.HeldBehaviour;
import net.tslat.smartbrainlib.object.MemoryTest;
import net.tslat.smartbrainlib.util.BrainUtils;

import java.util.List;

public class DeliverSuppliesToDeliveryTarget<E extends LivingEntity & SuppliesDeliverer> extends HeldBehaviour<E> {
    private static final MemoryTest MEMORY_REQUIREMENTS = MemoryTest.builder(3)
            .hasMemory(ClinkerMemoryModules.DELIVERY_TARGET.get())
            .usesMemory(MemoryModuleType.WALK_TARGET)
            .usesMemory(MemoryModuleType.LOOK_TARGET);
    SuppliesHolder currentDeliveryTarget;

    public DeliverSuppliesToDeliveryTarget() {
        super();
        this.onTick(this::doTick);
    }

    @Override
    protected List<Pair<MemoryModuleType<?>, MemoryStatus>> getMemoryRequirements() {
        return MEMORY_REQUIREMENTS;
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel level, E mob) {
        // can't do this if we don't have a delivery
        if (!mob.isHoldingDelivery()) return false;

        // if we're far away from the delivery target, we can't deliver supplies!
        SuppliesHolder deliveryTarget = BrainUtils.getMemory(mob, ClinkerMemoryModules.DELIVERY_TARGET.get());
        Vec3 deliveryPoint = deliveryTarget.position();
        return deliveryPoint.distanceTo(mob.position()) <= 2.0F;
    }

    @Override
    protected void start(E mob) {
        super.start(mob);
        this.currentDeliveryTarget = BrainUtils.getMemory(mob, ClinkerMemoryModules.DELIVERY_TARGET.get());
    }

    protected boolean doTick(E mob) {
        LivingEntity currentDeliveryTargetEntity = currentDeliveryTarget.suppliesHolderAsEntity();
        if (currentDeliveryTarget == null) return false;
        if (currentDeliveryTargetEntity.isDeadOrDying() || currentDeliveryTargetEntity.isRemoved()) return false;
        if (currentDeliveryTarget.position().distanceTo(mob.position()) > 2.0F) return false;

        // keep moving towards the target and looking at them
        BrainUtils.setMemory(
                mob, MemoryModuleType.WALK_TARGET,
                new WalkTarget(currentDeliveryTargetEntity, 1F, 1)
        );
        BrainUtils.setMemory(
                mob, MemoryModuleType.LOOK_TARGET,
                new EntityTracker(currentDeliveryTargetEntity, true)
        );

        // we're too far away to deliver, abort
        Vec3 deliveryPoint = currentDeliveryTarget.position();
        if (deliveryPoint.distanceTo(mob.position()) > 2.0F) {
            return false;
        }

        // stand there for one second and then give the delivery
        if (this.runningTime > 20) {
            finishDeliveringSupplies(mob);
            // this will fulfill the task, if it exists!
            return false;
        }
        return true;
    }

    protected void finishDeliveringSupplies(E mob) {
        // give them the delivery
        mob.setHoldingDelivery(false);
        currentDeliveryTarget.addSupplies(currentDeliveryTarget.supplyDeliveryAmount());

        // if we've just finished the delivery, then forget about it and move on
        SquadTask currentTask = BrainUtils.getMemory(mob, ClinkerMemoryModules.CURRENTLY_ASSIGNED_SQUAD_TASK.get());
        if (currentTask instanceof ResupplyTask && currentTask.isFinished()) {
            BrainUtils.clearMemory(mob, ClinkerMemoryModules.CURRENTLY_ASSIGNED_SQUAD_TASK.get());
            BrainUtils.clearMemory(mob, ClinkerMemoryModules.IS_CURRENTLY_ASSIGNED_SQUAD_TASK_ACTIVE.get());
        }
    }

    @Override
    protected void stop(E mob) {
        super.stop(mob);
        this.currentDeliveryTarget = null;
    }
}
