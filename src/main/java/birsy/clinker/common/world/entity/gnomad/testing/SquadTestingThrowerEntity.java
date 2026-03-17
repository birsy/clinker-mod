package birsy.clinker.common.world.entity.gnomad.testing;

import birsy.clinker.client.entity.gnomad.GnomadSkeleton;
import birsy.clinker.common.world.entity.gnomad.SuppliesHolder;
import birsy.clinker.common.world.entity.gnomad.gnomind.behaviors.PostSquadTask;
import birsy.clinker.common.world.entity.gnomad.gnomind.squad.squadtasks.ResupplyTask;
import birsy.clinker.core.Clinker;
import foundry.veil.api.client.necromancer.Skeleton;
import foundry.veil.api.client.necromancer.SkeletonParent;
import foundry.veil.api.client.necromancer.animation.Animator;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.animal.SnowGolem;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.projectile.Snowball;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.tslat.smartbrainlib.api.core.BrainActivityGroup;
import net.tslat.smartbrainlib.api.core.behaviour.custom.attack.AnimatableRangedAttack;
import net.tslat.smartbrainlib.api.core.behaviour.custom.target.SetAttackTarget;
import org.jetbrains.annotations.Nullable;

public class SquadTestingThrowerEntity extends SquadTestingEntity<SquadTestingThrowerEntity>
        implements SuppliesHolder, RangedAttackMob, SkeletonParent<SquadTestingThrowerEntity, GnomadSkeleton> {
    int supplies;
    public SquadTestingThrowerEntity(EntityType<? extends PathfinderMob> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public @Nullable SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType spawnType, @Nullable SpawnGroupData spawnGroupData) {
        this.supplies = this.supplyDeliveryAmount();
        return super.finalizeSpawn(level, difficulty, spawnType, spawnGroupData);
    }

    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();
        if (this.outOfSupplies()) {
            this.setCustomNameVisible(false);
            this.setCustomName(Component.literal("no supplies!"));
        } else {
            this.setCustomNameVisible(false);
            this.setCustomName(null);
        }
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
    public BrainActivityGroup<SquadTestingThrowerEntity> getIdleTasks() {
        return BrainActivityGroup.idleTasks(
                new PostSquadTask<SquadTestingThrowerEntity, ResupplyTask>
                        (ResupplyTask.class, ResupplyTask::new)
                        .startCondition(SuppliesHolder::outOfSupplies),
                new AnimatableRangedAttack<SquadTestingThrowerEntity>
                        (0)
                        .startCondition(mob -> !outOfSupplies())
        );
    }

    // just throw snowballs at them :P
    @Override
    public void performRangedAttack(LivingEntity target, float velocity) {
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
    private Animator<SquadTestingThrowerEntity, GnomadSkeleton> animator;
    @Override public void setSkeleton(@Nullable GnomadSkeleton skeleton) { this.skeleton = skeleton; }
    @Override public void setAnimator(@Nullable Animator<SquadTestingThrowerEntity, GnomadSkeleton> animator) { this.animator = animator; }
    @Override public @Nullable GnomadSkeleton getSkeleton() { return skeleton; }
    @Override public @Nullable Animator<SquadTestingThrowerEntity, GnomadSkeleton> getAnimator() { return animator; }
}
