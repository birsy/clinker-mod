package birsy.clinker.common.world.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public class ToxicGasCloud extends Entity {
    final float bouyancy;
    public ToxicGasCloud(EntityType<?> pEntityType, Level pLevel, float bouyancy) {
        super(pEntityType, pLevel);
        this.bouyancy = bouyancy;
        this.noPhysics = true;
    }

    @Override
    public void tick() {
        super.tick();
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {}

    @Override
    protected void readAdditionalSaveData(CompoundTag pCompound) {}

    @Override
    protected void addAdditionalSaveData(CompoundTag pCompound) {}
}
