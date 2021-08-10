package birsy.clinker.common.entity.monster;

import java.util.EnumSet;
import javax.annotation.Nullable;

import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.minecraft.network.chat.Component;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.Level;

public class WitchGlobEntity extends Mob implements Enemy {
	private static final EntityDataAccessor<Integer> GLOB_SIZE = SynchedEntityData.defineId(WitchGlobEntity.class, EntityDataSerializers.INT);
	public float squishAmount;
	public float squishFactor;
	public float prevSquishFactor;
	private boolean wasOnGround;

	public WitchGlobEntity(EntityType<? extends WitchGlobEntity> type, Level worldIn) {
		super(type, worldIn);
		this.moveControl = new WitchGlobEntity.MoveHelperController(this);
	}

	protected void registerGoals() {
		this.goalSelector.addGoal(1, new WitchGlobEntity.FloatGoal(this));
		this.goalSelector.addGoal(2, new WitchGlobEntity.AttackGoal(this));
		this.goalSelector.addGoal(3, new WitchGlobEntity.FaceRandomGoal(this));
		this.goalSelector.addGoal(5, new WitchGlobEntity.HopGoal(this));
		this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, 10, true, false, (p_213811_1_) -> {
			return Math.abs(p_213811_1_.getY() - this.getY()) <= 4.0D;
		}));
		this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, IronGolem.class, true));
	}

	protected void defineSynchedData() {
		super.defineSynchedData();
		this.entityData.define(GLOB_SIZE, 1);
	}

	protected void setWitchGlobSize(int size, boolean resetHealth) {
		this.entityData.set(GLOB_SIZE, size);
		this.reapplyPosition();
		this.refreshDimensions();
		this.getAttribute(Attributes.MAX_HEALTH).setBaseValue((double)(size * size));
		this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue((double)(0.2F + 0.1F * (float)size));
		this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue((double)size);
		if (resetHealth) {
			this.setHealth(this.getMaxHealth());
		}
		this.xpReward = size;
	}

	/**
	 * Returns the size of the witchglob.
	 */
	public int getWitchGlobSize() {
		return this.entityData.get(GLOB_SIZE);
	}

	public void addAdditionalSaveData(CompoundTag compound) {
		super.addAdditionalSaveData(compound);
		compound.putInt("Size", this.getWitchGlobSize() - 1);
		compound.putBoolean("wasOnGround", this.wasOnGround);
	}

	/**
	 * (abstract) Protected helper method to read subclass entity data from NBT.
	 */
	public void readAdditionalSaveData(CompoundTag compound) {
		int i = compound.getInt("Size");
		if (i < 0) {
			i = 0;
		}

		this.setWitchGlobSize(i + 1, false);
		super.readAdditionalSaveData(compound);
		this.wasOnGround = compound.getBoolean("wasOnGround");
	}

	protected ParticleOptions getSquishParticle() {
		return ParticleTypes.ITEM_SLIME;
	}

	protected boolean shouldDespawnInPeaceful() {
		return this.getWitchGlobSize() > 0;
	}

	/**
	 * Called to update the entity's position/logic.
	 */
	public void tick() {
		this.squishFactor += (this.squishAmount - this.squishFactor) * 0.5F;
		this.prevSquishFactor = this.squishFactor;
		super.tick();
		if (this.onGround && !this.wasOnGround) {
			int i = this.getWitchGlobSize();
			if (spawnCustomParticles()) i = 0; // don't spawn particles if it's handled by the implementation itself
			for(int j = 0; j < i * 8; ++j) {
				float f = this.random.nextFloat() * ((float)Math.PI * 2F);
				float f1 = this.random.nextFloat() * 0.5F + 0.5F;
	            float f2 = Mth.sin(f) * (float)i * 0.5F * f1;
	            float f3 = Mth.cos(f) * (float)i * 0.5F * f1;
	            this.level.addParticle(this.getSquishParticle(), this.getX() + (double)f2, this.getY(), this.getZ() + (double)f3, 0.0D, 0.0D, 0.0D);
			}

			this.playSound(this.getSquishSound(), this.getSoundVolume(), ((this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F) / 0.8F);
			this.squishAmount = -0.5F;
		} else if (!this.onGround && this.wasOnGround) {
			this.squishAmount = 1.0F;
		}

		this.wasOnGround = this.onGround;
		this.alterSquishAmount();
	}

	protected void alterSquishAmount() {
		this.squishAmount *= 0.6F;
	}

	/**
	 * Gets the amount of time the witchglob needs to wait between jumps.
	 */
	protected int getJumpDelay() {
		return this.random.nextInt(20) + 10;
	}

	public void refreshDimensions() {
		double d0 = this.getX();
		double d1 = this.getY();
		double d2 = this.getZ();
		super.refreshDimensions();
		this.setPos(d0, d1, d2);
	}

	public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
		if (GLOB_SIZE.equals(key)) {
			this.refreshDimensions();
			this.yRot = this.yHeadRot;
			this.yBodyRot = this.yHeadRot;
			if (this.isInWater() && this.random.nextInt(20) == 0) {
				this.doWaterSplashEffect();
			}
		}
		super.onSyncedDataUpdated(key);
	}

	@SuppressWarnings("unchecked")
	public EntityType<? extends WitchGlobEntity> getType() {
		return (EntityType<? extends WitchGlobEntity>)super.getType();
	}

	@SuppressWarnings("deprecation")
	@Override
	public void remove(boolean keepData) {
		int i = this.getWitchGlobSize();
		if (!this.level.isClientSide && i > 1 && this.isDeadOrDying() && !this.removed) {
			Component itextcomponent = this.getCustomName();
			boolean flag = this.isNoAi();
			float f = (float)i / 4.0F;
			int j = i / 2;
			int k = 2 + this.random.nextInt(3);
			for(int l = 0; l < k; ++l) {
				float f1 = ((float)(l % 2) - 0.5F) * f;
				float f2 = ((float)(l / 2) - 0.5F) * f;
				WitchGlobEntity witchglobentity = this.getType().create(this.level);
	            if (this.isPersistenceRequired()) {
	            	witchglobentity.setPersistenceRequired();
	            }

	            witchglobentity.setCustomName(itextcomponent);
	            witchglobentity.setNoAi(flag);
	            witchglobentity.setInvulnerable(this.isInvulnerable());
	            witchglobentity.setWitchGlobSize(j, true);
	            witchglobentity.moveTo(this.getX() + (double)f1, this.getY() + 0.5D, this.getZ() + (double)f2, this.random.nextFloat() * 360.0F, 0.0F);
	            this.level.addFreshEntity(witchglobentity);
			}
		}
		super.remove(keepData);
	}

	/**
	 * Called by a player entity when they collide with an entity
	 */
	public void playerTouch(Player entityIn) {
		this.dealDamage(entityIn);
	}

	protected void dealDamage(LivingEntity entityIn) {
		if (this.isAlive()) {
			int i = this.getWitchGlobSize();
			if (this.distanceToSqr(entityIn) < 0.6D * (double)i * 0.6D * (double)i && this.canSee(entityIn) && entityIn.hurt(DamageSource.mobAttack(this), this.getAttackDamage())) {
				this.playSound(SoundEvents.SLIME_ATTACK, 1.0F, (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
				this.doEnchantDamageEffects(this, entityIn);
			}
		}
	}

	protected float getStandingEyeHeight(Pose poseIn, EntityDimensions sizeIn) {
		return 0.625F * sizeIn.height;
	}

	protected float getAttackDamage() {
		return (float)this.getAttributeValue(Attributes.ATTACK_DAMAGE);
	}

	protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
		return SoundEvents.SLIME_HURT;
	}

	protected SoundEvent getDeathSound() {
		return SoundEvents.SLIME_DEATH;
	}

	protected SoundEvent getSquishSound() {
		return SoundEvents.SLIME_SQUISH;
	}

	protected ResourceLocation getDefaultLootTable() {
		return this.getWitchGlobSize() == 1 ? this.getType().getDefaultLootTable() : BuiltInLootTables.EMPTY;
	}

	/**
	 * Returns the volume for the sounds this mob makes.
	 */
	protected float getSoundVolume() {
		return 0.4F * (float)this.getWitchGlobSize();
	}

	/**
	 * The speed it takes to move the entityliving's rotationPitch through the faceEntity method. This is only currently
	 * use in wolves.
	 */
	public int getMaxHeadXRot() {
		return 0;
	}	

	/**
	 * Returns true if the witchglob makes a sound when it jumps (based upon the witchglob's size)
	 */
	protected boolean makesSoundOnJump() {
		return this.getWitchGlobSize() > 0;
	}

	/**
	 * Causes this entity to do an upwards motion (jumping).
	 */
	protected void jumpFromGround() {
		Vec3 vector3d = this.getDeltaMovement();
		this.setDeltaMovement(vector3d.x, (double)this.getJumpPower(), vector3d.z);
		this.hasImpulse = true;
	}

	@Nullable
	public SpawnGroupData finalizeSpawn(ServerLevelAccessor worldIn, DifficultyInstance difficultyIn, MobSpawnType reason, @Nullable SpawnGroupData spawnDataIn, @Nullable CompoundTag dataTag) {
		int i = this.random.nextInt(3);
		if (i < 2 && this.random.nextFloat() < 0.5F * difficultyIn.getSpecialMultiplier()) {
			++i;
		}
		int j = 1 << i;
		this.setWitchGlobSize(j, true);
		return super.finalizeSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
	}

	protected SoundEvent getJumpSound() {
		return SoundEvents.SLIME_JUMP;
	}

	public EntityDimensions getDimensions(Pose poseIn) {
		return super.getDimensions(poseIn).scale(0.255F * (float)this.getWitchGlobSize());
	}

	/**
	 * Called when the witchglob spawns particles on landing, see onUpdate.
	 * Return true to prevent the spawning of the default particles.
	 */
	protected boolean spawnCustomParticles() { return false; }
	static class AttackGoal extends Goal {
		private final WitchGlobEntity witchglob;
		private int growTieredTimer;
		public AttackGoal(WitchGlobEntity witchglobIn) {
			this.witchglob = witchglobIn;
			this.setFlags(EnumSet.of(Goal.Flag.LOOK));
		}

		/**
		 * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
		 * method as well.
		 */
		public boolean canUse() {
			LivingEntity livingentity = this.witchglob.getTarget();
			if (livingentity == null) {
				return false;
			} else if (!livingentity.isAlive()) {
				return false;
			} else {
				return livingentity instanceof Player && ((Player)livingentity).abilities.invulnerable ? false : this.witchglob.getMoveControl() instanceof WitchGlobEntity.MoveHelperController;
			}
		}

		/**
		 * Execute a one shot task or start executing a continuous task
		 */
		public void start() {
			this.growTieredTimer = 300;
			super.start();
		}

		/**
		 * Returns whether an in-progress EntityAIBase should continue executing
		 */
		public boolean canContinueToUse() {
			LivingEntity livingentity = this.witchglob.getTarget();
			if (livingentity == null) {
				return false;
			} else if (!livingentity.isAlive()) {
	            return false;
			} else if (livingentity instanceof Player && ((Player)livingentity).abilities.invulnerable) {
	            return false;
			} else {
	            return --this.growTieredTimer > 0;
			}
		}

		/**
		 * Keep ticking a continuous task that has already been started
		 */
		public void tick() {
			this.witchglob.lookAt(this.witchglob.getTarget(), 10.0F, 10.0F);
			((WitchGlobEntity.MoveHelperController)this.witchglob.getMoveControl()).setDirection(this.witchglob.yRot, true);
		}
	}

	static class FaceRandomGoal extends Goal {
		private final WitchGlobEntity witchglob;
		private float chosenDegrees;
		private int nextRandomizeTime;
		public FaceRandomGoal(WitchGlobEntity witchglobIn) {
			this.witchglob = witchglobIn;
			this.setFlags(EnumSet.of(Goal.Flag.LOOK));
		}

		/**
		 * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
		 * method as well.
		 */
		public boolean canUse() {
			return this.witchglob.getTarget() == null && (this.witchglob.onGround || this.witchglob.isInWater() || this.witchglob.isInLava() || this.witchglob.hasEffect(MobEffects.LEVITATION)) && this.witchglob.getMoveControl() instanceof WitchGlobEntity.MoveHelperController;
		}

		/**
		 * Keep ticking a continuous task that has already been started
		 */
		public void tick() {
			if (--this.nextRandomizeTime <= 0) {
				this.nextRandomizeTime = 40 + this.witchglob.getRandom().nextInt(60);
				this.chosenDegrees = (float)this.witchglob.getRandom().nextInt(360);
			}

			((WitchGlobEntity.MoveHelperController)this.witchglob.getMoveControl()).setDirection(this.chosenDegrees, false);
		}
	}

	static class FloatGoal extends Goal {
		private final WitchGlobEntity witchglob;
		public FloatGoal(WitchGlobEntity witchglobIn) {
			this.witchglob = witchglobIn;
			this.setFlags(EnumSet.of(Goal.Flag.JUMP, Goal.Flag.MOVE));
			witchglobIn.getNavigation().setCanFloat(true);
		}

		/**
		 * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
		 * method as well.
		 */
		public boolean canUse() {
			return (this.witchglob.isInWater() || this.witchglob.isInLava()) && this.witchglob.getMoveControl() instanceof WitchGlobEntity.MoveHelperController;
		}

		/**
		 * Keep ticking a continuous task that has already been started
		 */
		public void tick() {
			if (this.witchglob.getRandom().nextFloat() < 0.8F) {
				this.witchglob.getJumpControl().jump();
			}

			((WitchGlobEntity.MoveHelperController)this.witchglob.getMoveControl()).setSpeed(1.2D);
		}
	}

	static class HopGoal extends Goal {
		private final WitchGlobEntity witchglob;
		public HopGoal(WitchGlobEntity witchglobIn) {
			this.witchglob = witchglobIn;
			this.setFlags(EnumSet.of(Goal.Flag.JUMP, Goal.Flag.MOVE));
		}

		/**
		 * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
		 * method as well.
		 */
		public boolean canUse() {
			return !this.witchglob.isPassenger();
		}

		/**
		 * Keep ticking a continuous task that has already been started
		 */
		public void tick() {
			((WitchGlobEntity.MoveHelperController)this.witchglob.getMoveControl()).setSpeed(1.0D);
		}
	}

	static class MoveHelperController extends MoveControl {
		private float yRot;
		private int jumpDelay;
		private final WitchGlobEntity witchglob;
		private boolean isAggressive;
		public MoveHelperController(WitchGlobEntity witchglobIn) {
			super(witchglobIn);
			this.witchglob = witchglobIn;
			this.yRot = 180.0F * witchglobIn.yRot / (float)Math.PI;
		}

		public void setDirection(float yRotIn, boolean aggressive) {
			this.yRot = yRotIn;
			this.isAggressive = aggressive;
		}

		public void setSpeed(double speedIn) {
			this.speedModifier = speedIn;
			this.operation = MoveControl.Operation.MOVE_TO;
		}

		public void tick() {
			this.mob.yRot = this.rotlerp(this.mob.yRot, this.yRot, 90.0F);
			this.mob.yHeadRot = this.mob.yRot;
			this.mob.yBodyRot = this.mob.yRot;
			if (this.operation != MoveControl.Operation.MOVE_TO) {
				this.mob.setZza(0.0F);
			} else {
				this.operation = MoveControl.Operation.WAIT;
				if (this.mob.isOnGround()) {
					this.mob.setSpeed((float)(this.speedModifier * this.mob.getAttributeValue(Attributes.MOVEMENT_SPEED)));
					if (this.jumpDelay-- <= 0) {
						this.jumpDelay = this.witchglob.getJumpDelay();
						if (this.isAggressive) {
							this.jumpDelay /= 3;
						}
						this.witchglob.getJumpControl().jump();
						if (this.witchglob.makesSoundOnJump()) {
							this.witchglob.playSound(this.witchglob.getJumpSound(), this.witchglob.getSoundVolume(), 0.5F);
						}
					} else {
						this.witchglob.xxa = 0.0F;
						this.witchglob.zza = 0.0F;
						this.mob.setSpeed(0.0F);
					}
				} else {
					this.mob.setSpeed((float)(this.speedModifier * this.mob.getAttributeValue(Attributes.MOVEMENT_SPEED)));
				}
			}
		}
	}
}
