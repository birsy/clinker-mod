package birsy.clinker.common.world.entity.gnomad.testing;

import birsy.clinker.common.world.entity.gnomad.SuppliesDeliverer;
import birsy.clinker.common.world.entity.gnomad.gnomind.behaviors.ClaimSquadTask;
import birsy.clinker.common.world.entity.gnomad.gnomind.behaviors.PostSquadTask;
import birsy.clinker.common.world.entity.gnomad.gnomind.behaviors.delivery.FetchAndDeliverSupplies;
import birsy.clinker.common.world.entity.gnomad.gnomind.squad.squadtasks.ResupplyTask;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.Level;
import net.tslat.smartbrainlib.api.core.BrainActivityGroup;

public class SquadTestingSupplierEntity extends SquadTestingEntity<SquadTestingSupplierEntity> implements SuppliesDeliverer {
    private static final EntityDataAccessor<Boolean> DATA_HOLDING_DELIVERY =
            SynchedEntityData.defineId(SquadTestingSupplierEntity.class, EntityDataSerializers.BOOLEAN);

    public SquadTestingSupplierEntity(EntityType<? extends PathfinderMob> entityType, Level level) {
        super(entityType, level);
    }

    protected void customServerAiStep() {
        super.customServerAiStep();
        if (this.isHoldingDelivery()) {
            this.setCustomNameVisible(true);
            this.setCustomName(Component.literal("carrying delivery!"));
        } else {
            this.setCustomNameVisible(false);
            this.setCustomName(null);
        }
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_HOLDING_DELIVERY, false);
    }
    @Override
    public void addAdditionalSaveData(CompoundTag nbt) {
        super.addAdditionalSaveData(nbt);
        serializeHoldingDelivery(nbt);
    }
    @Override
    public void readAdditionalSaveData(CompoundTag nbt) {
        super.readAdditionalSaveData(nbt);
        deserializeHoldingDelivery(nbt);
    }

    @Override
    public boolean isHoldingDelivery() {
        return this.entityData.get(DATA_HOLDING_DELIVERY);
    }
    @Override
    public void setHoldingDelivery(boolean delivery) {
        this.entityData.set(DATA_HOLDING_DELIVERY, delivery);
    }

    @Override
    public BrainActivityGroup<SquadTestingSupplierEntity> getIdleTasks() {
        return BrainActivityGroup.idleTasks(
                new ClaimSquadTask<>().of(task -> task instanceof ResupplyTask && task.isPending()),
                FetchAndDeliverSupplies.<SquadTestingSupplierEntity>behavior()
        );
    }
}
