package birsy.clinker.common.entity.monster;

import javax.annotation.Nullable;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.controller.LookController;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.pathfinding.Path;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.MoveTowardsTargetGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;

public class WitchBrickEntity extends Monster
{
	private int attackTimer;
	public boolean isWindingUp;
	public boolean isCharging;

	public WitchBrickEntity(EntityType<? extends Monster> type, Level worldIn) {
		super(type, worldIn);
		this.maxUpStep = 1.0F;
	}
	
	protected void registerGoals() {
		this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.0D, true));
		//this.goalSelector.addGoal(1, new RamGoal(this, 60, 180, 0.5F));
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
	
	public SpawnGroupData finalizeSpawn(ServerLevelAccessor worldIn, DifficultyInstance difficultyIn, MobSpawnType reason, @Nullable SpawnGroupData spawnDataIn, @Nullable CompoundTag dataTag) {
		this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.WOODEN_AXE));
		return super.finalizeSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
	}
	
	protected void defineSynchedData() {
		super.defineSynchedData();
	}

	static class RamGoal extends Goal {
		private final WitchBrickEntity witchBrick;
		private int attackChargeUpTime;
		private int attackLength;
		private int attackTime;
		private int attackWindup;
		private float turningPrecision;

		public RamGoal(WitchBrickEntity mobIn, int attackChargeUpIn, int attackLengthIn, float turningPrecisionIn) {
			this.witchBrick = mobIn;
			this.attackChargeUpTime = attackChargeUpIn;
			this.attackLength = attackLengthIn;
			this.turningPrecision = turningPrecisionIn;
		}

		@Override
		public boolean canUse() {
			LivingEntity livingEntity = this.witchBrick.getTarget();
			if (livingEntity == null) {
				return false;
			} else if (this.witchBrick.distanceToSqr(livingEntity.position()) <= 15.0D && this.witchBrick.random.nextInt(15) == 0) {
				return true;
			} else {
				return false;
			}
		}

		@Override
		public void start() {
			this.witchBrick.isWindingUp = true;
			this.attackWindup = attackChargeUpTime;
			this.witchBrick.playSound(SoundEvents.BLAZE_AMBIENT, 1.0F, 1.0F);

			super.start();
		}

		@Override
		public void tick() {
			PathNavigation navigator = this.witchBrick.getNavigation();
			LivingEntity livingEntity = this.witchBrick.getTarget();

			if (attackWindup > 1 || this.witchBrick.isWindingUp) {
				this.witchBrick.getLookControl().setLookAt(livingEntity.position());

				this.witchBrick.isWindingUp = true;
				attackWindup--;
			}

			if (attackWindup == 1) {
				this.witchBrick.isWindingUp = false;
				this.witchBrick.isCharging = true;
				attackTime = this.attackLength;
				attackWindup--;
				this.witchBrick.playSound(SoundEvents.CREEPER_PRIMED, 1.0F, 1.0F);
			}

			if (attackTime > 1) {
				this.witchBrick.isCharging = true;
				/**
				LookController lookController = this.witchBrick.getLookController();
				Vector3d currentPosition = new Vector3d(lookController.getLookPosX(), lookController.getLookPosY(), lookController.getLookPosZ());
				Vector3d targetPosition = livingEntity.getPositionVec();

				lookController.setLookPosition(new Vector3d(lerp(targetPosition.getX(), currentPosition.getX(), this.turningPrecision), lerp(targetPosition.getY(), currentPosition.getY(), this.turningPrecision), lerp(targetPosition.getZ(), currentPosition.getZ(), this.turningPrecision)));
				this.witchBrick.getMoveHelper().strafe(1.0F, 0.0F);
				 */

				navigator.moveTo(navigator.createPath(livingEntity, (int) witchBrick.distanceToSqr(livingEntity)), 1.5F);

				if (this.witchBrick.getBoundingBox().intersects(livingEntity.getBoundingBox())) {
					this.witchBrick.doHurtTarget(livingEntity);
					livingEntity.setPos(this.witchBrick.getX(), this.witchBrick.getY(), this.witchBrick.getZ());
				}

				attackTime--;
			}

			if (attackTime == 1) {
				this.witchBrick.isWindingUp = false;
				this.witchBrick.isCharging = false;

				this.attackTime = 0;
				this.attackWindup = 0;
			}

			super.tick();
		}

		@Override
		public boolean canContinueToUse() {
			if (this.witchBrick.getTarget() == null) {
				return false;
			} else if(this.witchBrick.isCharging == true || this.witchBrick.isWindingUp == true) {
				return true;
			} else {
				return false;
			}
		}

		@Override
		public void stop() {
			this.witchBrick.playSound(SoundEvents.CREEPER_DEATH, 1.0F, 1.0F);

			this.witchBrick.isWindingUp = false;
			this.witchBrick.isCharging = false;

			this.attackTime = 0;
			this.attackWindup = 0;

			super.stop();
		}

		private float lerp(double a, double b, float f)
		{
			return (float) ((a * (1.0 - f)) + (b * f));
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
	
	protected int decreaseAirSupply(int air) {
		return air;
	}
	
	protected void doPush(Entity entityIn) {
		if (entityIn instanceof Enemy && !(entityIn instanceof Creeper) && this.getRandom().nextInt(20) == 0) {
			this.setTarget((LivingEntity)entityIn);
		}
		super.doPush(entityIn);
	}
	
	@OnlyIn(Dist.CLIENT)
	public int getAttackTimer() {
		return this.attackTimer;
	}
	
	@OnlyIn(Dist.CLIENT)
	public void setAttackTimer(int value) {
		this.attackTimer = value;
	}
	
	public boolean causeFallDamage(float distance, float damageMultiplier) {
		return false;
	}
}
