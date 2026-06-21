package birsy.clinker.common.world.entity.gnomad;

import birsy.clinker.common.world.entity.GroundLocomotionEntity;
import birsy.clinker.common.world.entity.ai.Sittable;
import birsy.clinker.common.world.entity.ai.behaviors.InvalidateLookAtTarget;
import birsy.clinker.common.world.entity.ai.behaviors.LocomotorLookAtTarget;
import birsy.clinker.common.world.entity.gnomad.gnomind.behaviors.ReportKnownEnemyLocations;
import birsy.clinker.common.world.entity.gnomad.gnomind.sensors.*;
import birsy.clinker.common.world.entity.gnomad.mogul.GnomadMogulEntity;
import birsy.clinker.common.world.entity.system.squad.Squad;
import birsy.clinker.common.world.entity.system.squad.SquadMember;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.level.Level;
import net.tslat.smartbrainlib.api.SmartBrainOwner;
import net.tslat.smartbrainlib.api.core.BrainActivityGroup;
import net.tslat.smartbrainlib.api.core.SmartBrainProvider;
import net.tslat.smartbrainlib.api.core.behaviour.custom.move.MoveToWalkTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.target.SetAttackTarget;
import net.tslat.smartbrainlib.api.core.sensor.ExtendedSensor;
import net.tslat.smartbrainlib.api.core.sensor.vanilla.HurtBySensor;
import net.tslat.smartbrainlib.api.core.sensor.vanilla.NearbyLivingEntitySensor;
import net.tslat.smartbrainlib.api.core.sensor.vanilla.NearbyPlayersSensor;
import net.tslat.smartbrainlib.util.EntityRetrievalUtil;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static net.minecraft.world.entity.monster.Monster.createMonsterAttributes;

public class BaseGnomadEntity<E extends BaseGnomadEntity<E>> extends GroundLocomotionEntity implements SquadMember<E>, SmartBrainOwner<E>, Enemy, Sittable {
    private static final EntityDataAccessor<Boolean> DATA_SITTING = SynchedEntityData.defineId(BaseGnomadEntity.class, EntityDataSerializers.BOOLEAN);

    private Squad squad;

    public BaseGnomadEntity(EntityType<? extends PathfinderMob> entityType, Level level) {
        super(entityType, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 20.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.23F)
                .add(Attributes.STEP_HEIGHT, 1.1D);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_SITTING, false);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag nbt) {
        super.addAdditionalSaveData(nbt);
        this.serializeSquad(nbt);
    }
    @Override
    public void readAdditionalSaveData(CompoundTag nbt) {
        super.readAdditionalSaveData(nbt);
        this.deserializeSquad(nbt);
    }

    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();
        tickBrain((E)this);
    }

    protected Brain.Provider<?> brainProvider() {
        return new SmartBrainProvider<>((E)this);
    }

    @Override
    public List<? extends ExtendedSensor<E>> getSensors() {
        return ObjectArrayList.of(
                new NearbyPlayersSensor<>(),
                new NearbyLivingEntitySensor<>(),
                new HurtBySensor<>(),
                new SquadSensor<>(),
                new NearestSupplyDepotSensor<>(),
                new LastKnownEnemyPositionSensor<>()
        );
    }

    @Override
    public BrainActivityGroup<E> getCoreTasks() {
        return BrainActivityGroup.coreTasks(
                new SetAttackTarget<>(false)
                        .targetFinder(mob -> EntityRetrievalUtil.getNearestPlayer(mob, 32.0F)),
                new ReportKnownEnemyLocations<>()
                        .cooldownFor((entity) -> 10), // run every ten ticks
                new InvalidateLookAtTarget<>(),
                new LocomotorLookAtTarget<>(),
                new MoveToWalkTarget<>()
        );
    }

    @Override
    public boolean isPushable() {
        return super.isPushable() && !this.isSitting();
    }

    @Override
    public @Nullable Squad getSquad() { return squad; }
    @Override
    public void setSquad(@Nullable Squad newSquad) {
        if (this.squad != null) this.squad.removeMember(this);
        if (newSquad != null) newSquad.addMember(this);
        this.squad = newSquad;
    }

    @Override
    public boolean isSitting() {
        return getEntityData().get(DATA_SITTING);
    }
    @Override
    public void setSitting(boolean value) {
        getEntityData().set(DATA_SITTING, value);
    }
}
