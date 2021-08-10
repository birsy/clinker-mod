package birsy.clinker.common.entity.monster;

import birsy.clinker.core.registry.ClinkerEntities;
import net.minecraft.block.BlockState;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.monster.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import java.util.List;

public class ShoggothHeadEntity extends AbstractShoggothEntity
{
	public int mitosisTicks;
	public List<AbstractShoggothEntity> followers;

	public ShoggothHeadEntity(EntityType<? extends MonsterEntity> type, World worldIn) {
		super(type, worldIn);
	}
	
	protected void registerGoals() {
		this.goalSelector.addGoal(1, new MitosisGoal(this, 60, 1, 4));
		this.goalSelector.addGoal(2, new MoveTowardsTargetGoal(this, 0.9D, 32.0F));
		this.goalSelector.addGoal(7, new LookAtGoal(this, PlayerEntity.class, 6.0F));
		this.goalSelector.addGoal(8, new LookRandomlyGoal(this));
		this.targetSelector.addGoal(2, new HurtByTargetGoal(this));
		this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, PlayerEntity.class, true));
	}
	
	public static AttributeModifierMap.MutableAttribute setCustomAttributes()
	{
		return MobEntity.func_233666_p_()
		.createMutableAttribute(Attributes.MAX_HEALTH, 36.0D)
		.createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.25D)
		.createMutableAttribute(Attributes.KNOCKBACK_RESISTANCE, 0.2D)
		.createMutableAttribute(Attributes.ATTACK_DAMAGE, 15.0D);
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
		public boolean shouldExecute() {
			if (shoggoth.mitosisTicks >= 0) {
				return this.shoggoth.rand.nextInt(100) == 0;
			} else {
				return false;
			}
		}

		@Override
		public void startExecuting() {
			this.shoggoth.mitosisTicks = ticksToComplete;
			this.amountToSpawn = this.minSpawn + this.shoggoth.rand.nextInt(this.maxSpawn - this.minSpawn);
		}

		@Override
		public void tick() {
			this.shoggoth.getNavigator().clearPath();
			if (this.delayTicks > 0) {
				this.delayTicks--;
			}

			int spawnTicks = (this.amountToSpawn * 5) + 1;

			if (shoggoth.mitosisTicks <= spawnTicks && this.delayTicks == 0) {
				ServerWorld serverworld = (ServerWorld)this.shoggoth.world;
				BlockPos blockpos = this.shoggoth.getPosition().add(-2 + this.shoggoth.rand.nextInt(5), 0, -2 + this.shoggoth.rand.nextInt(5));

				ShoggothBodyEntity shoggothBodyEntity = ClinkerEntities.SHOGGOTH_BODY.get().create(shoggoth.world);
				shoggothBodyEntity.moveToBlockPosAndAngles(blockpos, 0.0F, 0.0F);
				shoggothBodyEntity.onInitialSpawn(serverworld, this.shoggoth.world.getDifficultyForLocation(blockpos), SpawnReason.REINFORCEMENT, (ILivingEntityData)null, (CompoundNBT)null);
				serverworld.func_242417_l(shoggothBodyEntity);

				this.shoggoth.followers.add(shoggothBodyEntity);

				this.shoggoth.playSound(SoundEvents.ENTITY_ZOMBIE_VILLAGER_CURE, 1.0F, 1.0F);

				this.delayTicks = 5;
			}

			if (this.shoggoth.mitosisTicks >= -1) {
				this.shoggoth.mitosisTicks--;
			}

			super.tick();
		}

		@Override
		public boolean shouldContinueExecuting() {
			return shoggoth.mitosisTicks < -1;
		}

		@Override
		public void resetTask() {
			this.shoggoth.mitosisTicks = 0;
			this.delayTicks = 0;

			super.resetTask();
		}
	}

	//Sounds
	protected SoundEvent getAmbientSound() {
		return SoundEvents.ENTITY_SKELETON_HORSE_AMBIENT;
	}

	protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
		return SoundEvents.ENTITY_SKELETON_HORSE_HURT;
	}

	protected SoundEvent getDeathSound() {
		return SoundEvents.ENTITY_SKELETON_HORSE_DEATH;
	}

	protected void playStepSound(BlockPos pos, BlockState blockIn) {
		this.playSound(SoundEvents.ENTITY_HOGLIN_STEP, 0.15F, 1.0F);
	}

	protected void collideWithEntity(Entity entityIn) {
		if (entityIn instanceof ShoggothBodyEntity) {
			return;
		} else {
			super.collideWithEntity(entityIn);
		}
	}
	
	public boolean onLivingFall(float distance, float damageMultiplier) {
		return false;
	}
}
