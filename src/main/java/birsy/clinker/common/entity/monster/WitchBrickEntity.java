package birsy.clinker.common.entity.monster;

import javax.annotation.Nullable;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.controller.LookController;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.monster.CreeperEntity;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathNavigator;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class WitchBrickEntity extends MonsterEntity
{
	private int attackTimer;
	public boolean isWindingUp;
	public boolean isCharging;

	public WitchBrickEntity(EntityType<? extends MonsterEntity> type, World worldIn) {
		super(type, worldIn);
		this.stepHeight = 1.0F;
	}
	
	protected void registerGoals() {
		this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.0D, true));
		//this.goalSelector.addGoal(1, new RamGoal(this, 60, 180, 0.5F));
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
	
	public ILivingEntityData onInitialSpawn(IServerWorld worldIn, DifficultyInstance difficultyIn, SpawnReason reason, @Nullable ILivingEntityData spawnDataIn, @Nullable CompoundNBT dataTag) {
		this.setItemStackToSlot(EquipmentSlotType.MAINHAND, new ItemStack(Items.WOODEN_AXE));
		return super.onInitialSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
	}
	
	protected void registerData() {
		super.registerData();
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
		public boolean shouldExecute() {
			LivingEntity livingEntity = this.witchBrick.getAttackTarget();
			if (livingEntity == null) {
				return false;
			} else if (this.witchBrick.getDistanceSq(livingEntity.getPositionVec()) <= 15.0D && this.witchBrick.rand.nextInt(15) == 0) {
				return true;
			} else {
				return false;
			}
		}

		@Override
		public void startExecuting() {
			this.witchBrick.isWindingUp = true;
			this.attackWindup = attackChargeUpTime;
			this.witchBrick.playSound(SoundEvents.ENTITY_BLAZE_AMBIENT, 1.0F, 1.0F);

			super.startExecuting();
		}

		@Override
		public void tick() {
			PathNavigator navigator = this.witchBrick.getNavigator();
			LivingEntity livingEntity = this.witchBrick.getAttackTarget();

			if (attackWindup > 1 || this.witchBrick.isWindingUp) {
				this.witchBrick.getLookController().setLookPosition(livingEntity.getPositionVec());

				this.witchBrick.isWindingUp = true;
				attackWindup--;
			}

			if (attackWindup == 1) {
				this.witchBrick.isWindingUp = false;
				this.witchBrick.isCharging = true;
				attackTime = this.attackLength;
				attackWindup--;
				this.witchBrick.playSound(SoundEvents.ENTITY_CREEPER_PRIMED, 1.0F, 1.0F);
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

				navigator.setPath(navigator.getPathToEntity(livingEntity, (int) witchBrick.getDistanceSq(livingEntity)), 1.5F);

				if (this.witchBrick.getBoundingBox().intersects(livingEntity.getBoundingBox())) {
					this.witchBrick.attackEntityAsMob(livingEntity);
					livingEntity.setPosition(this.witchBrick.getPosX(), this.witchBrick.getPosY(), this.witchBrick.getPosZ());
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
		public boolean shouldContinueExecuting() {
			if (this.witchBrick.getAttackTarget() == null) {
				return false;
			} else if(this.witchBrick.isCharging == true || this.witchBrick.isWindingUp == true) {
				return true;
			} else {
				return false;
			}
		}

		@Override
		public void resetTask() {
			this.witchBrick.playSound(SoundEvents.ENTITY_CREEPER_DEATH, 1.0F, 1.0F);

			this.witchBrick.isWindingUp = false;
			this.witchBrick.isCharging = false;

			this.attackTime = 0;
			this.attackWindup = 0;

			super.resetTask();
		}

		private float lerp(double a, double b, float f)
		{
			return (float) ((a * (1.0 - f)) + (b * f));
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
	
	protected int decreaseAirSupply(int air) {
		return air;
	}
	
	protected void collideWithEntity(Entity entityIn) {
		if (entityIn instanceof IMob && !(entityIn instanceof CreeperEntity) && this.getRNG().nextInt(20) == 0) {
			this.setAttackTarget((LivingEntity)entityIn);
		}
		super.collideWithEntity(entityIn);
	}
	
	@OnlyIn(Dist.CLIENT)
	public int getAttackTimer() {
		return this.attackTimer;
	}
	
	@OnlyIn(Dist.CLIENT)
	public void setAttackTimer(int value) {
		this.attackTimer = value;
	}
	
	public boolean onLivingFall(float distance, float damageMultiplier) {
		return false;
	}
}
