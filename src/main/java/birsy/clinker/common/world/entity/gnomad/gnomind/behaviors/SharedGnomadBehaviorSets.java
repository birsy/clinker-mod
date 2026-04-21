package birsy.clinker.common.world.entity.gnomad.gnomind.behaviors;

import birsy.clinker.common.world.entity.ai.behaviors.InvalidateLookAtTarget;
import birsy.clinker.common.world.entity.gnomad.SuppliesDeliverer;
import birsy.clinker.common.world.entity.gnomad.gnomind.behaviors.delivery.DeliverSuppliesToDeliveryTarget;
import birsy.clinker.common.world.entity.gnomad.gnomind.behaviors.delivery.FetchSuppliesFromSupplyDepot;
import birsy.clinker.common.world.entity.gnomad.gnomind.behaviors.delivery.SetWalkTargetToDeliveryTarget;
import birsy.clinker.common.world.entity.gnomad.gnomind.behaviors.delivery.SetWalkTargetToSupplyDepot;
import birsy.clinker.common.world.entity.system.squad.SquadMember;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.tslat.smartbrainlib.api.core.behaviour.ExtendedBehaviour;
import net.tslat.smartbrainlib.api.core.behaviour.FirstApplicableBehaviour;
import net.tslat.smartbrainlib.api.core.behaviour.OneRandomBehaviour;
import net.tslat.smartbrainlib.api.core.behaviour.custom.target.SetPlayerLookTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.target.SetRandomLookTarget;
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
                new OneRandomBehaviour<E>(
                        new SetSquadLookTarget<E>()
                                .cooldownForBetween(20 * 6, 20 * 15),
                        new SetPlayerLookTarget<E>()
                                .cooldownForBetween(20 * 6, 20 * 15)
                ),
                new SetRandomLookTarget<E>()
                        .cooldownForBetween(20 * 1, 20 * 3)
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
}
