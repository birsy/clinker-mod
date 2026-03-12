package birsy.clinker.common.world.entity.gnomad;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;

public interface SuppliesDeliverer {
    boolean isHoldingDelivery();
    void setHoldingDelivery(boolean delivery);

    default void serializeHoldingDelivery(CompoundTag nbt) {
        nbt.putBoolean("HoldingDelivery", this.isHoldingDelivery());
    }
    default void deserializeHoldingDelivery(CompoundTag nbt) {
        setHoldingDelivery(nbt.getBoolean("HoldingDelivery"));
    }
    default LivingEntity supplyDelivererAsEntity() { return (LivingEntity) this; }
}
