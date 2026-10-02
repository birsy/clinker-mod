package birsy.clinker.common.entity.module;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.Level;

public abstract class ModularPathfindingMob<E extends ModularPathfindingMob<E>> extends PathfinderMob implements ModuleHolder<E> {
    private Module.Set<E> modules;

    protected ModularPathfindingMob(EntityType<? extends ModularPathfindingMob<E>> entityType, Level level) {
        super(entityType, level);
    }

    public void createModules() {};

    @Override
    public Module.Set<E> modules() { return modules; }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        // this is janky as hell but this is almost the earliest i can initialize them...
        // thanks java for SUCKING!!!
        if (this.modules == null) {
            modules = new Module.Set<>();
            createModules();
        }

        super.defineSynchedData(builder);
        this.modules.defineSyncedData(builder);
    }

    // module hooks
    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        this.modules.save(tag);
    }
    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.modules.load(tag);
    }


    @Override
    public void tick() {
        super.tick();
        this.modules.tick();
    }
    @Override
    public boolean canAddPassenger(Entity passenger) {
        return super.canAddPassenger(passenger) && modules().canAddPassenger(passenger);
    }
    @Override
    protected void positionRider(Entity passenger, MoveFunction callback) {
        if (!this.modules.positionRider(passenger, callback)) super.positionRider(passenger, callback);
    }
    @Override
    public boolean isPushable() {
        return super.isPushable() && this.modules.isPushable();
    }
}
