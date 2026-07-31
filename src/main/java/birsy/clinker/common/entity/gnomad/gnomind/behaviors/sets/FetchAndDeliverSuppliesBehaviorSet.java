package birsy.clinker.common.entity.gnomad.gnomind.behaviors.sets;

import birsy.clinker.common.entity.ai.behaviors.DecisionBehaviour;
import birsy.clinker.common.entity.ai.behaviors.SetLookTargetToRememberedPos;
import birsy.clinker.common.entity.ai.behaviors.SetWalkTargetToEntity;
import birsy.clinker.common.entity.ai.behaviors.SetWalkTargetToRememberedPos;
import birsy.clinker.common.entity.gnomad.SuppliesDeliverer;
import birsy.clinker.common.entity.gnomad.SuppliesHolder;
import birsy.clinker.common.entity.gnomad.gnomind.squadtasks.ResupplyTask;
import birsy.clinker.common.entity.system.squad.SquadTask;
import birsy.clinker.core.registry.entity.ClinkerActivities;
import birsy.clinker.core.registry.entity.ClinkerMemoryModules;
import com.google.common.base.Predicates;
import com.mojang.authlib.GameProfile;
import net.minecraft.core.GlobalPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.neoforged.neoforge.common.util.FakePlayer;
import net.neoforged.neoforge.common.util.FakePlayerFactory;
import net.tslat.smartbrainlib.api.SmartBrainOwner;
import net.tslat.smartbrainlib.api.core.BrainActivityGroup;
import net.tslat.smartbrainlib.api.core.behaviour.AllApplicableBehaviours;
import net.tslat.smartbrainlib.api.core.behaviour.SequentialBehaviour;
import net.tslat.smartbrainlib.api.core.behaviour.custom.misc.CustomBehaviour;
import net.tslat.smartbrainlib.api.core.behaviour.custom.misc.CustomDelayedBehaviour;
import net.tslat.smartbrainlib.api.core.behaviour.custom.misc.Idle;
import net.tslat.smartbrainlib.util.BrainUtils;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class FetchAndDeliverSuppliesBehaviorSet {
    private static final double COLLECTION_DISTANCE_THRESHOLD = 3, DELIVERY_DISTANCE_THRESHOLD = 4;
    public static <E extends PathfinderMob & SuppliesDeliverer & SmartBrainOwner<E>> BrainActivityGroup<E> createActivity() {
        return new BrainActivityGroup<E>(ClinkerActivities.DELIVER_SUPPLIES.get())
                .behaviours(
                        DecisionBehaviour.condition(
                                SuppliesDeliverer::isHoldingDelivery,
                                // if we're holding a delivery, deliver!
                                new AllApplicableBehaviours<E>(
                                        // simultaneously move to & try to give the target our delivery
                                        new SetWalkTargetToEntity<E>(FetchAndDeliverSuppliesBehaviorSet::getTarget)
                                                .lookAtTarget(Predicates.alwaysTrue())
                                                .closeEnoughWhen((int) (DELIVERY_DISTANCE_THRESHOLD - 2)),
                                        new CustomDelayedBehaviour<E>(30)
                                                .whenActivating(FetchAndDeliverSuppliesBehaviorSet::deliver)
                                                .stopIf((entity) -> entity.distanceToSqr(getTarget(entity)) > DELIVERY_DISTANCE_THRESHOLD * DELIVERY_DISTANCE_THRESHOLD)
                                ).stopIf((entity) -> {
                                    LivingEntity target = getTarget(entity);
                                    return target == null || !target.isAlive();
                                }).whenStopping((entity) -> BrainUtils.clearMemory(entity, MemoryModuleType.LOOK_TARGET)),
                                // otherwise, go fetch the delivery from the nearest depot
                                new DecisionBehaviour<>(
                                        (entity) -> {
                                            GlobalPos supplyDepot = BrainUtils.getMemory(entity, ClinkerMemoryModules.NEAREST_SUPPLY_DEPOT.get());
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
                                                new CustomDelayedBehaviour<E>(60)
                                                        .whenActivating(FetchAndDeliverSuppliesBehaviorSet::collectFromContainer),
                                                new Idle<>().runFor((entity) -> 30)
                                        ).whenStopping(FetchAndDeliverSuppliesBehaviorSet::closeContainer)
                                ).shouldInterrupt(true)
                                 .stopIf((entity) -> BrainUtils.getMemory(entity, ClinkerMemoryModules.NEAREST_SUPPLY_DEPOT.get()) == null)
                                 .whenStopping((entity) -> BrainUtils.clearMemory(entity, MemoryModuleType.LOOK_TARGET))
                        )
                )
                .onlyStartWithMemoryStatus(ClinkerMemoryModules.DELIVERY_TARGET.get(), MemoryStatus.VALUE_PRESENT)
                .onlyStartWithMemoryStatus(ClinkerMemoryModules.NEAREST_SUPPLY_DEPOT.get(), MemoryStatus.VALUE_PRESENT)
                .wipeMemoriesWhenFinished(ClinkerMemoryModules.DELIVERY_TARGET.get(), MemoryModuleType.LOOK_TARGET);
    }

    private static <E extends PathfinderMob & SuppliesDeliverer> @Nullable LivingEntity getTarget(E entity) {
        SuppliesHolder holder = BrainUtils.getMemory(entity, ClinkerMemoryModules.DELIVERY_TARGET.get());
        return holder instanceof LivingEntity livingEntity ? livingEntity : null;
    }
    private static <E extends PathfinderMob & SuppliesDeliverer> FakePlayer getFakePlayerForEntity(E entity) {
        return FakePlayerFactory.get(
                (ServerLevel) entity.level(),
                new GameProfile(entity.getUUID(), "ClinkerContainerSpoofer")
        );
    }
    // play open sound and do viz effects
    private static <E extends PathfinderMob & SuppliesDeliverer> void openContainer(E entity) {
        GlobalPos supplyDepot = BrainUtils.getMemory(entity, ClinkerMemoryModules.NEAREST_SUPPLY_DEPOT.get());
        if (supplyDepot == null) return;
        if (entity.level().getBlockEntity(supplyDepot.pos()) instanceof BaseContainerBlockEntity container && entity.level() instanceof ServerLevel level){
            ServerPlayer fakePlayer = getFakePlayerForEntity(entity);
            List<ServerPlayer> levelPlayers = level.players();
            if (!levelPlayers.contains(fakePlayer)) levelPlayers.add(fakePlayer);
            fakePlayer.moveTo(entity.getX(), entity.getY(), entity.getZ());
            container.startOpen(fakePlayer);
        }
    }
    // play close sound and do viz effects
    private static <E extends PathfinderMob & SuppliesDeliverer> void closeContainer(E entity) {
        GlobalPos supplyDepot = BrainUtils.getMemory(entity, ClinkerMemoryModules.NEAREST_SUPPLY_DEPOT.get());
        if (supplyDepot == null) return;
        if (entity.level().getBlockEntity(supplyDepot.pos()) instanceof BaseContainerBlockEntity container && entity.level() instanceof ServerLevel level){
            ServerPlayer fakePlayer = getFakePlayerForEntity(entity);
            container.stopOpen(fakePlayer);
            level.players().remove(fakePlayer);
        }
    }
    private static <E extends PathfinderMob & SuppliesDeliverer> void collectFromContainer(E entity) {
        entity.setHoldingDelivery(true);
        entity.playSound(SoundEvents.ARMOR_EQUIP_IRON.value());
    }
    private static <E extends PathfinderMob & SuppliesDeliverer> void deliver(E entity) {
        entity.setHoldingDelivery(false);
        // finish!
        SquadTask currentTask = BrainUtils.getMemory(entity, ClinkerMemoryModules.ASSIGNED_SQUAD_TASK.get());
        if (currentTask instanceof ResupplyTask resupplyTask) {
            resupplyTask.taskMaster().asEntity().addSupplies(resupplyTask.taskMaster().asEntity().supplyDeliveryAmount());
            resupplyTask.succeed();
        }
    }
}