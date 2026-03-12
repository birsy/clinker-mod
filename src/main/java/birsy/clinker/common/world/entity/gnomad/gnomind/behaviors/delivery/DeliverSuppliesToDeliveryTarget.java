package birsy.clinker.common.world.entity.gnomad.gnomind.behaviors.delivery;

import birsy.clinker.common.world.entity.gnomad.SuppliesHolder;
import birsy.clinker.common.world.entity.gnomad.SuppliesDeliverer;
import birsy.clinker.common.world.entity.gnomad.gnomind.squad.SquadTask;
import birsy.clinker.common.world.entity.gnomad.gnomind.squad.squadtasks.ResupplyTask;
import birsy.clinker.core.registry.entity.ClinkerMemoryModules;
import com.mojang.datafixers.util.Pair;
import net.minecraft.server.level.ServerLevel;
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

class DeliverSuppliesToDeliveryTarget<E extends LivingEntity & SuppliesDeliverer> extends HeldBehaviour<E> {
    private static final MemoryTest MEMORY_REQUIREMENTS = MemoryTest.builder(3)
            .hasMemory(ClinkerMemoryModules.DELIVERY_TARGET.get())
            .usesMemory(MemoryModuleType.WALK_TARGET)
            .usesMemory(MemoryModuleType.LOOK_TARGET);

    DeliverSuppliesToDeliveryTarget() {
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
        return deliveryPoint.distanceTo(mob.position()) <= 1.5F;
    }

    protected boolean doTick(E mob) {
        SuppliesHolder deliveryTarget = BrainUtils.getMemory(mob, ClinkerMemoryModules.DELIVERY_TARGET.get());
        if (deliveryTarget == null) return false;
        if (deliveryTarget.position().distanceTo(mob.position()) > 1.5F) return false;

        // keep moving towards the target and looking at them
        BrainUtils.setMemory(
                mob, MemoryModuleType.WALK_TARGET,
                new WalkTarget(deliveryTarget.suppliesHolderAsEntity(), 1F, 1)
        );
        BrainUtils.setMemory(
                mob, MemoryModuleType.LOOK_TARGET,
                new EntityTracker(deliveryTarget.suppliesHolderAsEntity(), true)
        );

        // stand there for one second and then give the delivery
        if (this.runningTime > 20) {
            mob.setHoldingDelivery(false);
            deliveryTarget.addSupplies(deliveryTarget.supplyDeliveryAmount());
            // this will fulfill the task, if it exists!
            return false;
        }
        return true;
    }
}
