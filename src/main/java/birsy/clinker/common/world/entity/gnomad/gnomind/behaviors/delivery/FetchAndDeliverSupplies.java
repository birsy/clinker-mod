package birsy.clinker.common.world.entity.gnomad.gnomind.behaviors.delivery;

import birsy.clinker.common.world.entity.gnomad.SuppliesDeliverer;
import net.minecraft.world.entity.LivingEntity;
import net.tslat.smartbrainlib.api.core.behaviour.FirstApplicableBehaviour;

public class FetchAndDeliverSupplies {
    public static <E extends LivingEntity & SuppliesDeliverer> FirstApplicableBehaviour<E> behavior() {
        return new FirstApplicableBehaviour<>(
                new DeliverSuppliesToDeliveryTarget<>(),
                new SetWalkTargetToDeliveryTarget<>(),
                new FetchSuppliesFromSupplyDepot<>(),
                new SetWalkTargetToSupplyDepot<>()
        );
    }
}