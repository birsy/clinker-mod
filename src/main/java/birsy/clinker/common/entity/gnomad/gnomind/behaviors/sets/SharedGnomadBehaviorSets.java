package birsy.clinker.common.entity.gnomad.gnomind.behaviors.sets;

import birsy.clinker.common.entity.ai.behaviors.*;
import birsy.clinker.common.entity.gnomad.BaseGnomadEntity;
import birsy.clinker.common.entity.module.ModuleHolder;
import birsy.clinker.common.entity.module.modules.EntityTosser;
import birsy.clinker.common.entity.module.modules.SquadMember;
import birsy.clinker.core.registry.entity.ClinkerMemoryModules;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.behavior.EntityTracker;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.player.Player;
import net.tslat.smartbrainlib.api.SmartBrainOwner;
import net.tslat.smartbrainlib.api.core.BrainActivityGroup;
import net.tslat.smartbrainlib.api.core.behaviour.ExtendedBehaviour;
import net.tslat.smartbrainlib.api.core.behaviour.FirstApplicableBehaviour;
import net.tslat.smartbrainlib.api.core.behaviour.SequentialBehaviour;
import net.tslat.smartbrainlib.api.core.behaviour.custom.misc.CustomBehaviour;
import net.tslat.smartbrainlib.api.core.behaviour.custom.misc.CustomDelayedBehaviour;
import net.tslat.smartbrainlib.api.core.behaviour.custom.target.SetRandomLookTarget;
import net.tslat.smartbrainlib.util.BrainUtils;
import net.tslat.smartbrainlib.util.EntityRetrievalUtil;
import net.tslat.smartbrainlib.util.RandomUtil;

public class SharedGnomadBehaviorSets {
    public static <E extends Mob & ModuleHolder<E> & SquadMember<E>> ExtendedBehaviour<E> setIdleLookTargets() {
        return new FirstApplicableBehaviour<>(
                // maybe stop looking at our target, if we're looking at our target...
                new InvalidateLookAtTarget<>()
                        .shouldInvalidate((livingEntity, entity) -> true)
                        .cooldownForBetween(20, 40)
                        .startCondition((entity) -> RandomUtil.oneInNChance(20 * 5)),
                // look at a squad member or the player, randomly.
                new ChooseRandomWeightedLookTarget<E>()
                        .lookWeight((self, other) -> {
                            int weight = 1;
                            // players are interesting!
                            if (other instanceof Player) weight = 5;
                            // as are other gnomads
                            if (other instanceof BaseGnomadEntity<?>) weight = 3;
                            // we like squadmates and critters like ourselves
                            if (other instanceof SquadMember<?> squadMember && squadMember.squadModule().getSquad() == self.squadModule().getSquad()) weight = 4;
                            if (other.getType() == self.getType()) weight += 2;

                            // if they're looking at us, we should look at them!
                            if (BrainUtils.hasMemory(other, MemoryModuleType.LOOK_TARGET) &&
                                BrainUtils.getMemory(other, MemoryModuleType.LOOK_TARGET) instanceof EntityTracker tracker) {
                                if (tracker.getEntity() == self) weight *= 2;
                            }

                            float distance = self.distanceTo(other);
                            weight = (int) (weight * Mth.clampedMap(distance, 0, 16, 4, 1));
                            return weight;
                        }).cooldownForBetween(20 * 5, 20 * 10),
                new SetRandomLookTarget<E>()
                        .cooldownForBetween(20 * 4, 20 * 10)
        );
    }

    public static <E extends PathfinderMob & ModuleHolder<E> & EntityTosser<E> & SmartBrainOwner<E>> ExtendedBehaviour<E> reactToLiveOrdnance() {
        // grab it and throw it at the nearest player
        return new FirstApplicableBehaviour<>(
                new ThrowHeldEntity<E>()
                        .startCondition(entity -> entity.tossModule().getHeldEntity() == BrainUtils.getMemory(entity, ClinkerMemoryModules.NEAREST_LIVE_ORDNANCE.get()))
                        .whenStopping(entity -> BrainUtils.clearMemory(entity, ClinkerMemoryModules.THROW_TARGET.get())),
                new CustomDelayedBehaviour<E>(20)
                        .whenActivating(entity -> {
                            Player nearestPlayer = EntityRetrievalUtil.getNearestPlayer(entity, 20.0F);
                            if (nearestPlayer == null) return;
                            BrainUtils.setMemory(entity, ClinkerMemoryModules.THROW_TARGET.get(), new EntityTracker(nearestPlayer, true));
                        }).startCondition(entity -> entity.tossModule().isHoldingEntity()),
                new GrabEntityForThrowing<E>()
                        .startCondition(entity -> BrainUtils.getMemory(entity, ClinkerMemoryModules.GRAB_TARGET.get()) == BrainUtils.getMemory(entity, ClinkerMemoryModules.NEAREST_LIVE_ORDNANCE.get())),
                new CustomBehaviour<E>(
                        entity -> BrainUtils.setMemory(entity, ClinkerMemoryModules.GRAB_TARGET.get(), BrainUtils.getMemory(entity, ClinkerMemoryModules.NEAREST_LIVE_ORDNANCE.get()))
                )
        ).startCondition(entity -> BrainUtils.hasMemory(entity, ClinkerMemoryModules.NEAREST_LIVE_ORDNANCE.get()));
    }
}
