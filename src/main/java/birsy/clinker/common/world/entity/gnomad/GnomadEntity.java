package birsy.clinker.common.world.entity.gnomad;

import birsy.clinker.client.entity.gnomad.basic.GnomadAnimator;
import birsy.clinker.client.entity.gnomad.basic.GnomadSkeleton;
import birsy.clinker.common.world.entity.gnomad.gnomind.behaviors.PostSquadTask;
import birsy.clinker.common.world.entity.gnomad.gnomind.behaviors.sets.RelaxWithSquadBehaviorSet;
import birsy.clinker.common.world.entity.gnomad.gnomind.behaviors.sets.SharedGnomadBehaviorSets;
import birsy.clinker.common.world.entity.gnomad.gnomind.behaviors.StayNearSquadCenter;
import birsy.clinker.common.world.entity.gnomad.gnomind.squadtasks.ResupplyTask;
import birsy.clinker.core.registry.ClinkerItems;
import foundry.veil.api.client.necromancer.SkeletonParent;
import foundry.veil.api.client.necromancer.animation.Animator;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.projectile.Snowball;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.tslat.smartbrainlib.api.core.BrainActivityGroup;
import net.tslat.smartbrainlib.api.core.behaviour.FirstApplicableBehaviour;
import net.tslat.smartbrainlib.api.core.behaviour.OneRandomBehaviour;
import net.tslat.smartbrainlib.api.core.behaviour.custom.attack.AnimatableRangedAttack;
import net.tslat.smartbrainlib.api.core.behaviour.custom.misc.Idle;
import net.tslat.smartbrainlib.api.core.behaviour.custom.path.SetRandomWalkTarget;
import org.jetbrains.annotations.Nullable;

public class GnomadEntity extends BaseGnomadEntity<GnomadEntity>
        implements SuppliesHolder, RangedAttackMob, SkeletonParent<GnomadEntity, GnomadSkeleton> {
    int supplies;
    public GnomadEntity(EntityType<? extends PathfinderMob> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public @Nullable SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType spawnType, @Nullable SpawnGroupData spawnGroupData) {
        this.supplies = this.supplyDeliveryAmount();
        this.setLeftHanded(this.getRandom().nextBoolean());
        this.setItemInHand(InteractionHand.MAIN_HAND,
                this.getRandom().nextBoolean() ?
                        ClinkerItems.LEAD_AXE.toStack() :
                        ClinkerItems.LEAD_SWORD.toStack()
                );
        return super.finalizeSpawn(level, difficulty, spawnType, spawnGroupData);
    }

    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();
    }

    @Override
    public void addAdditionalSaveData(CompoundTag nbt) {
        super.addAdditionalSaveData(nbt);
        serializeSupplies(nbt);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag nbt) {
        super.readAdditionalSaveData(nbt);
        deserializeSupplies(nbt);
    }

    @Override
    public int getSupplyCount() { return supplies; }
    @Override
    public void setSupplyCount(int count) { this.supplies = count; }
    @Override
    public BrainActivityGroup<GnomadEntity> getIdleTasks() {
        return BrainActivityGroup.idleTasks(
                new PostSquadTask<GnomadEntity, ResupplyTask>
                        (ResupplyTask.class, ResupplyTask::new)
                        .startCondition(SuppliesHolder::outOfSupplies),
                new AnimatableRangedAttack<GnomadEntity>
                        (0)
                        .startCondition(mob -> !outOfSupplies()),
                SharedGnomadBehaviorSets.<GnomadEntity>setIdleLookTargets(),
                RelaxWithSquadBehaviorSet.<GnomadEntity>tryInitiate(),
                new FirstApplicableBehaviour<>(
                        RelaxWithSquadBehaviorSet.<GnomadEntity>goRelax(),
                        new StayNearSquadCenter<GnomadEntity>()
                                .maximumDistance(10.0F)
                                .speedModifier(0.5F),
                        new OneRandomBehaviour<GnomadEntity>(
                                new SetRandomWalkTarget<>().speedModifier(0.5F),
                                new Idle<>().runFor(mob -> mob.getRandom().nextInt(30, 120))
                        )
                )
        );
    }

    // just throw snowballs at them :P
    @Override
    public void performRangedAttack(LivingEntity target, float velocity) {
        if (target.isSpectator() || !target.isAlive()) return;
        if (!this.tryConsumeSupplies()) return;
        Snowball snowball = new Snowball(this.level(), this);
        double d0 = target.getEyeY() - 1.1F;
        double d1 = target.getX() - this.getX();
        double d2 = d0 - snowball.getY();
        double d3 = target.getZ() - this.getZ();
        double d4 = Math.sqrt(d1 * d1 + d3 * d3) * 0.2F;
        snowball.shoot(d1, d2 + d4, d3, 1.6F, 12.0F);
        this.playSound(SoundEvents.SNOW_GOLEM_SHOOT, 0.2F, 0.4F / (this.getRandom().nextFloat() * 0.4F + 0.8F));
        this.level().addFreshEntity(snowball);
    }

    private GnomadSkeleton skeleton;
    private GnomadAnimator animator;
    @Override public void setSkeleton(@Nullable GnomadSkeleton skeleton) { this.skeleton = skeleton; }
    @Override public void setAnimator(@Nullable Animator<GnomadEntity, GnomadSkeleton> animator) { this.animator = (GnomadAnimator) animator; }
    @Override public @Nullable GnomadSkeleton getSkeleton() { return skeleton; }
    @Override public @Nullable GnomadAnimator getAnimator() { return animator; }
}
