package birsy.clinker.common.world.entity.gnomad.gnomind.behaviors.sets;

import birsy.clinker.common.world.entity.ai.behaviors.*;
import birsy.clinker.common.world.entity.gnomad.SuppliesDeliverer;
import birsy.clinker.common.world.entity.gnomad.SuppliesHolder;
import birsy.clinker.common.world.entity.gnomad.gnomind.squadtasks.ResupplyTask;
import birsy.clinker.common.world.entity.system.squad.SquadMember;
import birsy.clinker.common.world.entity.system.squad.SquadTask;
import birsy.clinker.core.registry.entity.ClinkerMemoryModules;
import com.google.common.base.Predicates;
import com.mojang.authlib.GameProfile;
import net.minecraft.core.GlobalPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.neoforged.neoforge.common.util.FakePlayer;
import net.neoforged.neoforge.common.util.FakePlayerFactory;
import net.tslat.smartbrainlib.api.core.behaviour.AllApplicableBehaviours;
import net.tslat.smartbrainlib.api.core.behaviour.ExtendedBehaviour;
import net.tslat.smartbrainlib.api.core.behaviour.SequentialBehaviour;
import net.tslat.smartbrainlib.api.core.behaviour.custom.misc.CustomBehaviour;
import net.tslat.smartbrainlib.api.core.behaviour.custom.misc.CustomDelayedBehaviour;
import net.tslat.smartbrainlib.api.core.behaviour.custom.misc.Idle;
import net.tslat.smartbrainlib.util.BrainUtils;
import org.jetbrains.annotations.Nullable;

public class FetchAndDeliverSuppliesBehaviorSet {
    private static final double COLLECTION_DISTANCE_THRESHOLD = 3, DELIVERY_DISTANCE_THRESHOLD = 4;
    public static <E extends LivingEntity & SuppliesDeliverer> ExtendedBehaviour<E> create() {
        return DecisionBehaviour.condition(
                // ensure we're resupplying
                // ...should probably do this in a better way.
                // todo: look into Activities???
                (entity) -> BrainUtils.getMemory(entity, ClinkerMemoryModules.ASSIGNED_SQUAD_TASK.get()) instanceof ResupplyTask resupplyTask &&
                        resupplyTask.isActive() &&
                        resupplyTask.taskMaster().asEntity().isAlive(),
                DecisionBehaviour.condition(
                        SuppliesDeliverer::isHoldingDelivery,
                        // if we're holding a delivery, deliver!
                        new AllApplicableBehaviours<E>(
                                // simultaneously move to & try to give the target our delivery
                                new SetWalkTargetToEntity<E>(FetchAndDeliverSuppliesBehaviorSet::getTarget)
                                        .lookAtTarget(Predicates.alwaysTrue())
                                        .closeEnoughWhen((int) (DELIVERY_DISTANCE_THRESHOLD - 2)),
                                new CustomDelayedBehaviour<E>(20)
                                        .whenActivating(FetchAndDeliverSuppliesBehaviorSet::deliver)
                                        .stopIf((entity) -> {
                                            Entity target = getTarget(entity);
                                            return target == null || entity.distanceToSqr(target) > DELIVERY_DISTANCE_THRESHOLD * DELIVERY_DISTANCE_THRESHOLD;
                                        })
                        ).stopIf((entity) -> {
                            Entity target = getTarget(entity);
                            return target == null || !target.isAlive();
                        }),
                        // otherwise, go fetch the delivery from the nearest depot
                        new DecisionBehaviour<>(
                                (entity) -> {
                                    GlobalPos supplyDepot = BrainUtils.getMemory(entity, ClinkerMemoryModules.NEAREST_SUPPLY_DEPOT.get());
                                    if (supplyDepot == null || supplyDepot.dimension() != entity.level().dimension()) return DecisionBehaviour.CANCEL; // invalid supply depot
                                    boolean tooFar = entity.distanceToSqr(supplyDepot.pos().getCenter()) >
                                            COLLECTION_DISTANCE_THRESHOLD * COLLECTION_DISTANCE_THRESHOLD;
                                    return tooFar ? 0 : 1;
                                },
                                // if we're too far from the supply depot, walk to it
                                new SetWalkTargetToRememberedPos<>(ClinkerMemoryModules.NEAREST_SUPPLY_DEPOT.get())
                                        .lookAtTarget(Predicates.alwaysTrue())
                                        .closeEnoughWhen((int) (COLLECTION_DISTANCE_THRESHOLD - 1)),
                                // otherwise, open the container and collect the supplies
                                new SequentialBehaviour<>(
                                        new CustomBehaviour<E>(FetchAndDeliverSuppliesBehaviorSet::openContainer),
                                        new SetLookTargetToRememberedPos<>(ClinkerMemoryModules.NEAREST_SUPPLY_DEPOT.get()),
                                        new CustomDelayedBehaviour<E>(20)
                                                .whenActivating(FetchAndDeliverSuppliesBehaviorSet::collectFromContainer),
                                        new Idle<>().runFor((entity) -> 20)
                                ).whenStopping(FetchAndDeliverSuppliesBehaviorSet::closeContainer)
                        ).shouldInterrupt(true)
                ).whenStopping(entity -> BrainUtils.clearMemory(entity, MemoryModuleType.LOOK_TARGET))
        ).shouldInterrupt(true);
    }
    private static <E extends LivingEntity & SuppliesDeliverer, T extends LivingEntity & SquadMember & SuppliesHolder> @Nullable T getTarget(E entity) {
        if (BrainUtils.getMemory(entity, ClinkerMemoryModules.ASSIGNED_SQUAD_TASK.get()) instanceof ResupplyTask resupplyTask)
            return (T) resupplyTask.taskMaster().asEntity();
        return null;
    }
    private static <E extends LivingEntity & SuppliesDeliverer> FakePlayer getFakePlayerForEntity(E entity) {
        return FakePlayerFactory.get(
                (ServerLevel) entity.level(),
                new GameProfile(entity.getUUID(), "ClinkerContainerSpoofer")
        );
    }
    // play open sound and do viz effects
    private static <E extends LivingEntity & SuppliesDeliverer> void openContainer(E entity) {
        GlobalPos supplyDepot = BrainUtils.getMemory(entity, ClinkerMemoryModules.NEAREST_SUPPLY_DEPOT.get());
        if (supplyDepot == null) return;
        if (entity.level().getBlockEntity(supplyDepot.pos()) instanceof BaseContainerBlockEntity container)
            container.startOpen(getFakePlayerForEntity(entity));
    }
    // play close sound and do viz effects
    private static <E extends LivingEntity & SuppliesDeliverer> void closeContainer(E entity) {
        GlobalPos supplyDepot = BrainUtils.getMemory(entity, ClinkerMemoryModules.NEAREST_SUPPLY_DEPOT.get());
        if (supplyDepot == null) return;
        if (entity.level().getBlockEntity(supplyDepot.pos()) instanceof BaseContainerBlockEntity container)
            container.stopOpen(getFakePlayerForEntity(entity));
    }
    private static <E extends LivingEntity & SuppliesDeliverer> void collectFromContainer(E entity) {
        entity.setHoldingDelivery(true);
        entity.playSound(SoundEvents.ARMOR_EQUIP_IRON.value());
    }
    private static <E extends LivingEntity & SuppliesDeliverer> void deliver(E entity) {
        entity.setHoldingDelivery(false);
        // finish!
        SquadTask currentTask = BrainUtils.getMemory(entity, ClinkerMemoryModules.ASSIGNED_SQUAD_TASK.get());
        if (currentTask instanceof ResupplyTask resupplyTask) {
            resupplyTask.taskMaster().asEntity().addSupplies(resupplyTask.taskMaster().asEntity().supplyDeliveryAmount());
            resupplyTask.succeed();
        }
    }
}