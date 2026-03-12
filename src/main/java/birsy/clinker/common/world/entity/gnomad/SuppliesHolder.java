package birsy.clinker.common.world.entity.gnomad;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

public interface SuppliesHolder {
    int getSupplyCount();
    void setSupplyCount(int count);
    Vec3 position();

    default int supplyDeliveryAmount() {
        return 10;
    }
    default void serializeSupplies(CompoundTag nbt) {
        nbt.putInt("Supplies", this.getSupplyCount());
    }
    default void deserializeSupplies(CompoundTag nbt) {
        setSupplyCount(nbt.getInt("Supplies"));
    }

    default boolean outOfSupplies() {
        return getSupplyCount() <= 0;
    }
    default void addSupplies(int count) {
        this.setSupplyCount(getSupplyCount() + count);
    }
    default boolean tryConsumeSupplies() {
        int supplyCount = getSupplyCount();
        if (outOfSupplies()) return false;
        setSupplyCount(supplyCount - 1);
        return true;
    }
    default LivingEntity suppliesHolderAsEntity() { return (LivingEntity) this; }
}
