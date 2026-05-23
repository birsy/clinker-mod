package birsy.clinker.common.world.entity.gnomad.gnomind.behaviors;

import birsy.clinker.common.world.entity.ai.behaviors.ChooseRandomWeightedLookTarget;
import birsy.clinker.common.world.entity.ai.behaviors.InvalidateLookAtTarget;
import birsy.clinker.common.world.entity.gnomad.BaseGnomadEntity;
import birsy.clinker.common.world.entity.gnomad.SuppliesDeliverer;
import birsy.clinker.common.world.entity.gnomad.gnomind.behaviors.delivery.DeliverSuppliesToDeliveryTarget;
import birsy.clinker.common.world.entity.gnomad.gnomind.behaviors.delivery.FetchSuppliesFromSupplyDepot;
import birsy.clinker.common.world.entity.gnomad.gnomind.behaviors.delivery.SetWalkTargetToDeliveryTarget;
import birsy.clinker.common.world.entity.gnomad.gnomind.behaviors.delivery.SetWalkTargetToSupplyDepot;
import birsy.clinker.common.world.entity.gnomad.gnomind.behaviors.relax.RelaxAtRelaxationPoint;
import birsy.clinker.common.world.entity.gnomad.gnomind.behaviors.relax.SetWalkTargetToRelaxationPoint;
import birsy.clinker.common.world.entity.gnomad.gnomind.behaviors.relax.TryInitiateRelaxation;
import birsy.clinker.common.world.entity.gnomad.gnomind.squadtasks.RelaxWithSquadTask;
import birsy.clinker.common.world.entity.system.squad.SquadMember;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.behavior.EntityTracker;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.player.Player;
import net.tslat.smartbrainlib.api.core.behaviour.ExtendedBehaviour;
import net.tslat.smartbrainlib.api.core.behaviour.FirstApplicableBehaviour;
import net.tslat.smartbrainlib.api.core.behaviour.OneRandomBehaviour;
import net.tslat.smartbrainlib.api.core.behaviour.custom.target.SetPlayerLookTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.target.SetRandomLookTarget;
import net.tslat.smartbrainlib.util.BrainUtils;
import net.tslat.smartbrainlib.util.RandomUtil;

public class SharedGnomadBehaviorSets {
    public static <E extends Mob & SquadMember<E>> ExtendedBehaviour<E> setIdleLookTargets() {
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
                            if (other instanceof SquadMember<?> squadMember && squadMember.getSquad() == self.getSquad()) weight = 4;
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

    public static <E extends LivingEntity & SuppliesDeliverer> FirstApplicableBehaviour<E> fetchAndDeliverSupplies() {
        return new FirstApplicableBehaviour<>(
                new DeliverSuppliesToDeliveryTarget<>(),
                new SetWalkTargetToDeliveryTarget<>(),
                new FetchSuppliesFromSupplyDepot<>(),
                new SetWalkTargetToSupplyDepot<>()
        );
    }

    public static <E extends LivingEntity & SquadMember<E>> FirstApplicableBehaviour<E> sitAndRelax() {
        return new FirstApplicableBehaviour<>(
                new RelaxAtRelaxationPoint<>(),
                new SetWalkTargetToRelaxationPoint<>(),
                new ClaimSquadTask<E>()
                        .of(task -> task instanceof RelaxWithSquadTask),
                new TryInitiateRelaxation<E>()
        );
    }
}
