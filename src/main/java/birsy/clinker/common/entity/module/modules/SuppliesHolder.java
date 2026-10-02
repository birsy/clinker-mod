package birsy.clinker.common.entity.module.modules;

import birsy.clinker.common.entity.module.Module;
import birsy.clinker.common.entity.module.ModuleHolder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;

public interface SuppliesHolder<E extends Entity & ModuleHolder<E> & SuppliesHolder<E>> {
    SuppliesHolder.SuppliesModule<E> suppliesModule();

    class SuppliesModule<E extends Entity & ModuleHolder<E> & SuppliesHolder<E>> extends Module<E> {
        public final int maxSupplies;
        public int supplies = 0;

        public SuppliesModule(E self, int maxSupplies) {
            super(self);
            this.maxSupplies = maxSupplies;
        }

        public int getSupplyCount() {
            return this.supplies;
        }
        public void setSupplyCount(int count) {
           this.supplies = count;
        }
        public boolean outOfSupplies() {
            return getSupplyCount() <= 0;
        }
        public void addSupplies(int count) {
            self.playSound(
                    SoundEvents.ARMOR_EQUIP_IRON.value(),
                    1.0F, 1.0F
            );
            this.setSupplyCount(getSupplyCount() + count);
        }
        public boolean tryConsumeSupplies() {
            int supplyCount = getSupplyCount();
            if (outOfSupplies()) return false;
            setSupplyCount(supplyCount - 1);
            return true;
        }

        @Override
        public void save(CompoundTag tag) {
            tag.putInt("Supplies", this.getSupplyCount());
        }
        @Override
        public void load(CompoundTag tag) {
            setSupplyCount(tag.getInt("Supplies"));
        }
    }
}
