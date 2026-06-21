package birsy.clinker.common.world.entity.gnomad;

import birsy.clinker.client.entity.gnomad.runt.GnomadRuntAnimator;
import birsy.clinker.client.entity.gnomad.runt.GnomadRuntSkeleton;
import birsy.clinker.common.world.entity.gnomad.gnomind.behaviors.ClaimSquadTask;
import birsy.clinker.common.world.entity.gnomad.gnomind.behaviors.sets.SharedGnomadBehaviorSets;
import birsy.clinker.common.world.entity.gnomad.gnomind.behaviors.StayNearSquadCenter;
import birsy.clinker.common.world.entity.gnomad.gnomind.behaviors.sets.FetchAndDeliverSuppliesBehaviorSet;
import birsy.clinker.common.world.entity.gnomad.gnomind.squadtasks.ResupplyTask;
import birsy.clinker.core.Clinker;
import birsy.clinker.core.registry.entity.ClinkerActivities;
import birsy.clinker.core.registry.entity.ClinkerMemoryModules;
import foundry.veil.api.client.necromancer.SkeletonParent;
import foundry.veil.api.client.necromancer.animation.Animator;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.tslat.smartbrainlib.api.core.BrainActivityGroup;
import net.tslat.smartbrainlib.api.core.behaviour.FirstApplicableBehaviour;
import net.tslat.smartbrainlib.api.core.behaviour.OneRandomBehaviour;
import net.tslat.smartbrainlib.api.core.behaviour.custom.misc.Idle;
import net.tslat.smartbrainlib.api.core.behaviour.custom.misc.Panic;
import net.tslat.smartbrainlib.api.core.behaviour.custom.path.SetRandomWalkTarget;
import net.tslat.smartbrainlib.util.BrainUtils;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static net.minecraft.world.entity.monster.Monster.createMonsterAttributes;

public class GnomadRuntEntity extends BaseGnomadEntity<GnomadRuntEntity> implements SuppliesDeliverer, SkeletonParent<GnomadRuntEntity, GnomadRuntSkeleton> {
    private static final EntityDataAccessor<Boolean> DATA_HOLDING_DELIVERY =
            SynchedEntityData.defineId(GnomadRuntEntity.class, EntityDataSerializers.BOOLEAN);

    public GnomadRuntEntity(EntityType<? extends PathfinderMob> entityType, Level level) {
        super(entityType, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return createMonsterAttributes()
                .add(Attributes.MOVEMENT_SPEED, 0.23F)
                .add(Attributes.ATTACK_DAMAGE, 1.0);
    }

    @Override
    public void knockback(double strength, double x, double z) {
        // runts take much additional knockback
        super.knockback(strength * 3, x, z);
    }

    protected void customServerAiStep() {
        super.customServerAiStep();
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
    public BrainActivityGroup<GnomadRuntEntity> getIdleTasks() {
        return BrainActivityGroup.idleTasks(
                new ClaimSquadTask<>(task -> task instanceof ResupplyTask && task.isPending()),
                new FirstApplicableBehaviour<>(
                        new Panic<GnomadRuntEntity>()
                                .panicIf((entity, source) -> entity.getLastDamageSource() != null),
                        new StayNearSquadCenter<GnomadRuntEntity>()
                                .maximumDistance(10.0F)
                                .speedModifier(2.0F),
                        SharedGnomadBehaviorSets.<GnomadRuntEntity>setIdleLookTargets(),
                        new OneRandomBehaviour<GnomadRuntEntity>(
                                new SetRandomWalkTarget<>().speedModifier(0.5F),
                                new Idle<>().runFor(mob -> mob.getRandom().nextInt(30, 60))
                        )
                )
        );
    }

    @Override
    public Map<Activity, BrainActivityGroup<? extends GnomadRuntEntity>> getAdditionalTasks() {
        Set<BrainActivityGroup<GnomadRuntEntity>> tasks = Set.of(
                FetchAndDeliverSuppliesBehaviorSet.createActivity()
        );
        return tasks.stream().collect(Collectors.toUnmodifiableMap(BrainActivityGroup::getActivity, task -> task));
    }

    @Override
    public Set<Activity> getScheduleIgnoringActivities() {
        return Set.of(Activity.FIGHT, ClinkerActivities.DELIVER_SUPPLIES.get());
    }

    @Override
    public List<Activity> getActivityPriorities() {
        return List.of(
                ClinkerActivities.DELIVER_SUPPLIES.get(),
                Activity.FIGHT,
                Activity.IDLE
        );
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        SoundType soundtype = state.getSoundType(this.level(), pos, this);
        this.playSound(soundtype.getStepSound(), soundtype.getVolume() * 0.15F, soundtype.getPitch() * 2.0F);
    }
    @Override
    protected float nextStep() {
        return this.moveDist + 0.3F;
    }

    private GnomadRuntSkeleton skeleton;
    private GnomadRuntAnimator animator;
    @Override public void setSkeleton(@Nullable GnomadRuntSkeleton skeleton) { this.skeleton = skeleton; }
    @Override public void setAnimator(@Nullable Animator<GnomadRuntEntity, GnomadRuntSkeleton> animator) { this.animator = (GnomadRuntAnimator) animator; }
    @Override public @Nullable GnomadRuntSkeleton getSkeleton() { return skeleton; }
    @Override public @Nullable GnomadRuntAnimator getAnimator() { return animator; }
}
