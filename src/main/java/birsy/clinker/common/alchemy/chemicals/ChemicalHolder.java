package birsy.clinker.common.alchemy.chemicals;

import birsy.clinker.common.alchemy.MatterState;
import net.minecraft.nbt.CompoundTag;

import java.util.Optional;

public interface ChemicalHolder {
    float getAmount();
    float getVolume();
    default void tick(Mixture currentMixture, float deltaTime) {}
    default float getDensity() { return this.getVolume() / this.getAmount(); }
    boolean setAmount(float amount);
    MatterState getState();
    ChemicalHolder create(float amount);
    default ChemicalHolder create() { return this.create(this.getAmount()); }
    boolean is (ChemicalHolder holder);
    default boolean addAmount(float amount) { return this.setAmount(this.getAmount() + amount); }
    default boolean subtractAmount(float amount) { return this.setAmount(this.getAmount() - amount); }
    CompoundTag write(CompoundTag tagIn);
    ChemicalHolder read(CompoundTag tagIn);
}
