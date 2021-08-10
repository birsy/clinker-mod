package birsy.clinker.common.entity.monster;

import birsy.clinker.core.registry.ClinkerEntities;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.monster.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;

import java.util.List;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MoveTowardsTargetGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;

public class ShoggothHeadEntity extends AbstractShoggothEntity
{
	public int mitosisTicks;
	public List<AbstractShoggothEntity> followers;

	public ShoggothHeadEntity(EntityType<? extends Monster> type, Level worldIn) {
		super(type, worldIn);
	}
	
	protected void registerGoals() {
		this.goalSelector.addGoal(1, new MitosisGoal(this, 60, 1, 4));
		this.goalSelector.addGoal(2, new MoveTowardsTargetGoal(this, 0.9D, 32.0F));
		this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 6.0F));
		this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
		this.targetSelector.addGoal(2, new HurtByTargetGoal(this));
		this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Player.class, true));
	}
	
	public static AttributeSupplier.Builder setCustomAttributes()
	{
		return Mob.createMobAttributes()
		.add(Attributes.MAX_HEALTH, 36.0D)
		.add(Attributes.MOVEMENT_SPEED, 0.25D)
		.add(Attributes.KNOCKBACK_RESISTANCE, 0.2D)
		.add(Attributes.ATTACK_DAMAGE, 15.0D);
	}

	@Override
	public void tick() {
		super.tick();
	}

	static class MitosisGoal extends Goal {
		private final ShoggothHeadEntity shoggoth;
		private int ticksToComplete;
		private int amountToSpawn;
		private int delayTicks;

		private int minSpawn;
		private int maxSpawn;

		public MitosisGoal(ShoggothHeadEntity mob, int ticksToCompleteIn, int minSpawnIn, int maxSpawnIn) {
			this.shoggoth = mob;
			this.ticksToComplete = ticksToCompleteIn;

			this.minSpawn = minSpawnIn;
			this.maxSpawn = maxSpawnIn;
		}

		@Override
		public boolean canUse() {
			if (shoggoth.mitosisTicks >= 0) {
				return this.shoggoth.random.nextInt(100) == 0;
			} else {
				return false;
			}
		}

		@Override
		public void start() {
			this.shoggoth.mitosisTicks = ticksToComplete;
			this.amountToSpawn = this.minSpawn + this.shoggoth.random.nextInt(this.maxSpawn - this.minSpawn);
		}

		@Override
		public void tick() {
			this.shoggoth.getNavigation().stop();
			if (this.delayTicks > 0) {
				this.delayTicks--;
			}

			int spawnTicks = (this.amountToSpawn * 5) + 1;

			if (shoggoth.mitosisTicks <= spawnTicks && this.delayTicks == 0) {
				ServerLevel serverworld = (ServerLevel)this.shoggoth.level;
				BlockPos blockpos = this.shoggoth.blockPosition().offset(-2 + this.shoggoth.random.nextInt(5), 0, -2 + this.shoggoth.random.nextInt(5));

				ShoggothBodyEntity shoggothBodyEntity = ClinkerEntities.SHOGGOTH_BODY.get().create(shoggoth.level);
				shoggothBodyEntity.moveTo(blockpos, 0.0F, 0.0F);
				shoggothBodyEntity.finalizeSpawn(serverworld, this.shoggoth.level.getCurrentDifficultyAt(blockpos), MobSpawnType.REINFORCEMENT, (SpawnGroupData)null, (CompoundTag)null);
				serverworld.addFreshEntityWithPassengers(shoggothBodyEntity);

				this.shoggoth.followers.add(shoggothBodyEntity);

				this.shoggoth.playSound(SoundEvents.ZOMBIE_VILLAGER_CURE, 1.0F, 1.0F);

				this.delayTicks = 5;
			}

			if (this.shoggoth.mitosisTicks >= -1) {
				this.shoggoth.mitosisTicks--;
			}

			super.tick();
		}

		@Override
		public boolean canContinueToUse() {
			return shoggoth.mitosisTicks < -1;
		}

		@Override
		public void stop() {
			this.shoggoth.mitosisTicks = 0;
			this.delayTicks = 0;

			super.stop();
		}
	}

	//Sounds
	protected SoundEvent getAmbientSound() {
		return SoundEvents.SKELETON_HORSE_AMBIENT;
	}

	protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
		return SoundEvents.SKELETON_HORSE_HURT;
	}

	protected SoundEvent getDeathSound() {
		return SoundEvents.SKELETON_HORSE_DEATH;
	}

	protected void playStepSound(BlockPos pos, BlockState blockIn) {
		this.playSound(SoundEvents.HOGLIN_STEP, 0.15F, 1.0F);
	}

	protected void doPush(Entity entityIn) {
		if (entityIn instanceof ShoggothBodyEntity) {
			return;
		} else {
			super.doPush(entityIn);
		}
	}
	
	public boolean causeFallDamage(float distance, float damageMultiplier) {
		return false;
	}
}
