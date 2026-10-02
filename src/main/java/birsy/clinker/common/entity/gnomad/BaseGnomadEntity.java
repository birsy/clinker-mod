package birsy.clinker.common.entity.gnomad;

import birsy.clinker.common.entity.GroundLocomotionEntity;
import birsy.clinker.common.entity.module.modules.EntityTosser;
import birsy.clinker.common.entity.ai.behaviors.InvalidateLookAtTarget;
import birsy.clinker.common.entity.ai.behaviors.LocomotorLookAtTarget;
import birsy.clinker.common.entity.gnomad.gnomind.behaviors.ReportKnownEnemyLocations;
import birsy.clinker.common.entity.gnomad.gnomind.sensors.LastKnownEnemyPositionSensor;
import birsy.clinker.common.entity.gnomad.gnomind.sensors.NearestSupplyDepotSensor;
import birsy.clinker.common.entity.gnomad.gnomind.sensors.SquadSensor;
import birsy.clinker.common.entity.module.modules.Sitter;
import birsy.clinker.common.entity.module.modules.SquadMember;
import birsy.clinker.core.registry.entity.ClinkerActivities;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.tslat.smartbrainlib.api.SmartBrainOwner;
import net.tslat.smartbrainlib.api.core.BrainActivityGroup;
import net.tslat.smartbrainlib.api.core.SmartBrain;
import net.tslat.smartbrainlib.api.core.SmartBrainProvider;
import net.tslat.smartbrainlib.api.core.behaviour.custom.move.MoveToWalkTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.target.SetAttackTarget;
import net.tslat.smartbrainlib.api.core.sensor.ExtendedSensor;
import net.tslat.smartbrainlib.api.core.sensor.vanilla.HurtBySensor;
import net.tslat.smartbrainlib.api.core.sensor.vanilla.NearbyLivingEntitySensor;
import net.tslat.smartbrainlib.api.core.sensor.vanilla.NearbyPlayersSensor;
import net.tslat.smartbrainlib.util.EntityRetrievalUtil;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static net.minecraft.world.entity.monster.Monster.createMonsterAttributes;

public abstract class BaseGnomadEntity<E extends BaseGnomadEntity<E>> extends GroundLocomotionEntity<E>
        implements Enemy, SmartBrainOwner<E>, SquadMember<E>, Sitter<E>, EntityTosser<E> {
    protected static final EntityDataAccessor<Integer> DATA_SIT_POSE = SynchedEntityData.defineId(BaseGnomadEntity.class, EntityDataSerializers.INT);
    protected static final EntityDataAccessor<Integer> DATA_HELD_ENTITY = SynchedEntityData.defineId(BaseGnomadEntity.class, EntityDataSerializers.INT);

    protected SquadModule<E> squadModule;
    protected EntityTosserModule<E> tossModule;
    protected SitterModule<E> sitModule;

    public BaseGnomadEntity(EntityType<? extends BaseGnomadEntity<E>> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public void createModules() {
        super.createModules();
        this.squadModule = modules().add(new SquadModule<>(this.moduleMe(), this.squadPositionWeight()));
        this.tossModule = modules().add(new EntityTosserModule<>(this.moduleMe(), DATA_HELD_ENTITY, new Vec3(0, this.getDimensions(Pose.STANDING).height() + 0.5F, 0), 1));
        this.sitModule = modules().add(new SitterModule<>(this.moduleMe(), DATA_SIT_POSE, 2));
    }

    @Override public SquadModule<E> squadModule() { return this.squadModule; }
    @Override public EntityTosserModule<E> tossModule() { return this.tossModule; }
    @Override public SitterModule<E> sitModule() { return this.sitModule; }

    protected float squadPositionWeight() {
        return 1.0F;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 20.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.23F)
                .add(Attributes.STEP_HEIGHT, 1.1D);
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
    public void handleAdditionalBrainSetup(SmartBrain<? extends E> brain) {
        SmartBrainOwner.super.handleAdditionalBrainSetup(brain);
    }

    @Override
    public List<? extends ExtendedSensor<E>> getSensors() {
        return ObjectArrayList.of(
                new NearbyPlayersSensor<E>(),
                new NearbyLivingEntitySensor<E>(),
                new HurtBySensor<E>(),
                new SquadSensor<E>(),
                new NearestSupplyDepotSensor<E>(),
                new LastKnownEnemyPositionSensor<E>()
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
}
