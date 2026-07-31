package birsy.clinker.common.entity.gnomad;

import birsy.clinker.common.entity.GroundLocomotionEntity;
import birsy.clinker.common.entity.ai.Sitter;
import birsy.clinker.common.entity.ai.behaviors.InvalidateLookAtTarget;
import birsy.clinker.common.entity.ai.behaviors.LocomotorLookAtTarget;
import birsy.clinker.common.entity.gnomad.gnomind.behaviors.ReportKnownEnemyLocations;
import birsy.clinker.common.entity.gnomad.gnomind.sensors.LastKnownEnemyPositionSensor;
import birsy.clinker.common.entity.gnomad.gnomind.sensors.NearestSupplyDepotSensor;
import birsy.clinker.common.entity.gnomad.gnomind.sensors.SquadSensor;
import birsy.clinker.common.entity.system.squad.Squad;
import birsy.clinker.common.entity.system.squad.SquadMember;
import birsy.clinker.core.Clinker;
import birsy.clinker.core.registry.entity.ClinkerActivities;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.schedule.Activity;
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
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static net.minecraft.world.entity.monster.Monster.createMonsterAttributes;

public abstract class BaseGnomadEntity<E extends BaseGnomadEntity<E>> extends GroundLocomotionEntity implements SquadMember<E>, SmartBrainOwner<E>, Enemy, Sitter {
    private static final EntityDataAccessor<Boolean> DATA_SITTING = SynchedEntityData.defineId(BaseGnomadEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> DATA_SIT_POSE = SynchedEntityData.defineId(BaseGnomadEntity.class, EntityDataSerializers.INT);

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
        builder.define(DATA_SIT_POSE, 0);
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
                        .cooldownFor((entity) -> 20), // run every second
                new InvalidateLookAtTarget<>(),
                new LocomotorLookAtTarget<>(),
                new MoveToWalkTarget<>()
        );
    }

    @Override
    public Set<Activity> getScheduleIgnoringActivities() {
        return Set.of(Activity.FIGHT, ClinkerActivities.RELAX.get(), ClinkerActivities.DELIVER_SUPPLIES.get());
    }

    @Override
    public List<Activity> getActivityPriorities() {
        return List.of(
                Activity.FIGHT,
                ClinkerActivities.DELIVER_SUPPLIES.get(),
                ClinkerActivities.RELAX.get(),
                Activity.IDLE
        );
    }

    protected Set<BrainActivityGroup<? extends E>> createAdditionalActivities() {
        return Set.of();
    }

    @Override
    public Map<Activity, BrainActivityGroup<? extends E>> getAdditionalTasks() {
        Set<BrainActivityGroup<? extends E>> tasks = createAdditionalActivities();
        return tasks.stream().collect(Collectors.toUnmodifiableMap(BrainActivityGroup::getActivity, task -> task));
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
    private static final AttributeModifier SPEED_MODIFIER_SITTING = new AttributeModifier(
            Clinker.resource("sitting"), -1.0, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
    );
    @Override
    public void setSitting(boolean sitting) {
        getEntityData().set(DATA_SITTING, sitting);
        if (sitting) getEntityData().set(DATA_SIT_POSE, this.random.nextInt());
        // can't move when sitting!
        AttributeInstance speedAttribute = this.getAttribute(Attributes.MOVEMENT_SPEED);
        if (speedAttribute != null) {
            if (sitting) speedAttribute.addTransientModifier(SPEED_MODIFIER_SITTING);
            else speedAttribute.removeModifier(SPEED_MODIFIER_SITTING);
        }
    }
    // sometimes we can sit in a different pose. just leave this for the animation driver to handle...
    public int getSitPose() {
        return getEntityData().get(DATA_SIT_POSE);
    }
}
