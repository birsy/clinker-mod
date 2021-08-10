package birsy.clinker.common.entity.monster;

import java.util.EnumSet;
import javax.annotation.Nullable;

import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.Pose;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.controller.MovementController;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.loot.LootTables;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.World;

public class WitchGlobEntity extends MobEntity implements IMob {
	private static final DataParameter<Integer> GLOB_SIZE = EntityDataManager.createKey(WitchGlobEntity.class, DataSerializers.VARINT);
	public float squishAmount;
	public float squishFactor;
	public float prevSquishFactor;
	private boolean wasOnGround;

	public WitchGlobEntity(EntityType<? extends WitchGlobEntity> type, World worldIn) {
		super(type, worldIn);
		this.moveController = new WitchGlobEntity.MoveHelperController(this);
	}

	protected void registerGoals() {
		this.goalSelector.addGoal(1, new WitchGlobEntity.FloatGoal(this));
		this.goalSelector.addGoal(2, new WitchGlobEntity.AttackGoal(this));
		this.goalSelector.addGoal(3, new WitchGlobEntity.FaceRandomGoal(this));
		this.goalSelector.addGoal(5, new WitchGlobEntity.HopGoal(this));
		this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, PlayerEntity.class, 10, true, false, (p_213811_1_) -> {
			return Math.abs(p_213811_1_.getPosY() - this.getPosY()) <= 4.0D;
		}));
		this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, IronGolemEntity.class, true));
	}

	protected void registerData() {
		super.registerData();
		this.dataManager.register(GLOB_SIZE, 1);
	}

	protected void setWitchGlobSize(int size, boolean resetHealth) {
		this.dataManager.set(GLOB_SIZE, size);
		this.recenterBoundingBox();
		this.recalculateSize();
		this.getAttribute(Attributes.MAX_HEALTH).setBaseValue((double)(size * size));
		this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue((double)(0.2F + 0.1F * (float)size));
		this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue((double)size);
		if (resetHealth) {
			this.setHealth(this.getMaxHealth());
		}
		this.experienceValue = size;
	}

	/**
	 * Returns the size of the witchglob.
	 */
	public int getWitchGlobSize() {
		return this.dataManager.get(GLOB_SIZE);
	}

	public void writeAdditional(CompoundNBT compound) {
		super.writeAdditional(compound);
		compound.putInt("Size", this.getWitchGlobSize() - 1);
		compound.putBoolean("wasOnGround", this.wasOnGround);
	}

	/**
	 * (abstract) Protected helper method to read subclass entity data from NBT.
	 */
	public void readAdditional(CompoundNBT compound) {
		int i = compound.getInt("Size");
		if (i < 0) {
			i = 0;
		}

		this.setWitchGlobSize(i + 1, false);
		super.readAdditional(compound);
		this.wasOnGround = compound.getBoolean("wasOnGround");
	}

	protected IParticleData getSquishParticle() {
		return ParticleTypes.ITEM_SLIME;
	}

	protected boolean isDespawnPeaceful() {
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
				float f = this.rand.nextFloat() * ((float)Math.PI * 2F);
				float f1 = this.rand.nextFloat() * 0.5F + 0.5F;
	            float f2 = MathHelper.sin(f) * (float)i * 0.5F * f1;
	            float f3 = MathHelper.cos(f) * (float)i * 0.5F * f1;
	            this.world.addParticle(this.getSquishParticle(), this.getPosX() + (double)f2, this.getPosY(), this.getPosZ() + (double)f3, 0.0D, 0.0D, 0.0D);
			}

			this.playSound(this.getSquishSound(), this.getSoundVolume(), ((this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F) / 0.8F);
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
		return this.rand.nextInt(20) + 10;
	}

	public void recalculateSize() {
		double d0 = this.getPosX();
		double d1 = this.getPosY();
		double d2 = this.getPosZ();
		super.recalculateSize();
		this.setPosition(d0, d1, d2);
	}

	public void notifyDataManagerChange(DataParameter<?> key) {
		if (GLOB_SIZE.equals(key)) {
			this.recalculateSize();
			this.rotationYaw = this.rotationYawHead;
			this.renderYawOffset = this.rotationYawHead;
			if (this.isInWater() && this.rand.nextInt(20) == 0) {
				this.doWaterSplashEffect();
			}
		}
		super.notifyDataManagerChange(key);
	}

	@SuppressWarnings("unchecked")
	public EntityType<? extends WitchGlobEntity> getType() {
		return (EntityType<? extends WitchGlobEntity>)super.getType();
	}

	@SuppressWarnings("deprecation")
	@Override
	public void remove(boolean keepData) {
		int i = this.getWitchGlobSize();
		if (!this.world.isRemote && i > 1 && this.getShouldBeDead() && !this.removed) {
			ITextComponent itextcomponent = this.getCustomName();
			boolean flag = this.isAIDisabled();
			float f = (float)i / 4.0F;
			int j = i / 2;
			int k = 2 + this.rand.nextInt(3);
			for(int l = 0; l < k; ++l) {
				float f1 = ((float)(l % 2) - 0.5F) * f;
				float f2 = ((float)(l / 2) - 0.5F) * f;
				WitchGlobEntity witchglobentity = this.getType().create(this.world);
	            if (this.isNoDespawnRequired()) {
	            	witchglobentity.enablePersistence();
	            }

	            witchglobentity.setCustomName(itextcomponent);
	            witchglobentity.setNoAI(flag);
	            witchglobentity.setInvulnerable(this.isInvulnerable());
	            witchglobentity.setWitchGlobSize(j, true);
	            witchglobentity.setLocationAndAngles(this.getPosX() + (double)f1, this.getPosY() + 0.5D, this.getPosZ() + (double)f2, this.rand.nextFloat() * 360.0F, 0.0F);
	            this.world.addEntity(witchglobentity);
			}
		}
		super.remove(keepData);
	}

	/**
	 * Called by a player entity when they collide with an entity
	 */
	public void onCollideWithPlayer(PlayerEntity entityIn) {
		this.dealDamage(entityIn);
	}

	protected void dealDamage(LivingEntity entityIn) {
		if (this.isAlive()) {
			int i = this.getWitchGlobSize();
			if (this.getDistanceSq(entityIn) < 0.6D * (double)i * 0.6D * (double)i && this.canEntityBeSeen(entityIn) && entityIn.attackEntityFrom(DamageSource.causeMobDamage(this), this.func_225512_er_())) {
				this.playSound(SoundEvents.ENTITY_SLIME_ATTACK, 1.0F, (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F);
				this.applyEnchantments(this, entityIn);
			}
		}
	}

	protected float getStandingEyeHeight(Pose poseIn, EntitySize sizeIn) {
		return 0.625F * sizeIn.height;
	}

	protected float func_225512_er_() {
		return (float)this.getAttributeValue(Attributes.ATTACK_DAMAGE);
	}

	protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
		return SoundEvents.ENTITY_SLIME_HURT;
	}

	protected SoundEvent getDeathSound() {
		return SoundEvents.ENTITY_SLIME_DEATH;
	}

	protected SoundEvent getSquishSound() {
		return SoundEvents.ENTITY_SLIME_SQUISH;
	}

	protected ResourceLocation getLootTable() {
		return this.getWitchGlobSize() == 1 ? this.getType().getLootTable() : LootTables.EMPTY;
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
	public int getVerticalFaceSpeed() {
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
	protected void jump() {
		Vector3d vector3d = this.getMotion();
		this.setMotion(vector3d.x, (double)this.getJumpUpwardsMotion(), vector3d.z);
		this.isAirBorne = true;
	}

	@Nullable
	public ILivingEntityData onInitialSpawn(IServerWorld worldIn, DifficultyInstance difficultyIn, SpawnReason reason, @Nullable ILivingEntityData spawnDataIn, @Nullable CompoundNBT dataTag) {
		int i = this.rand.nextInt(3);
		if (i < 2 && this.rand.nextFloat() < 0.5F * difficultyIn.getClampedAdditionalDifficulty()) {
			++i;
		}
		int j = 1 << i;
		this.setWitchGlobSize(j, true);
		return super.onInitialSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
	}

	protected SoundEvent getJumpSound() {
		return SoundEvents.ENTITY_SLIME_JUMP;
	}

	public EntitySize getSize(Pose poseIn) {
		return super.getSize(poseIn).scale(0.255F * (float)this.getWitchGlobSize());
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
			this.setMutexFlags(EnumSet.of(Goal.Flag.LOOK));
		}

		/**
		 * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
		 * method as well.
		 */
		public boolean shouldExecute() {
			LivingEntity livingentity = this.witchglob.getAttackTarget();
			if (livingentity == null) {
				return false;
			} else if (!livingentity.isAlive()) {
				return false;
			} else {
				return livingentity instanceof PlayerEntity && ((PlayerEntity)livingentity).abilities.disableDamage ? false : this.witchglob.getMoveHelper() instanceof WitchGlobEntity.MoveHelperController;
			}
		}

		/**
		 * Execute a one shot task or start executing a continuous task
		 */
		public void startExecuting() {
			this.growTieredTimer = 300;
			super.startExecuting();
		}

		/**
		 * Returns whether an in-progress EntityAIBase should continue executing
		 */
		public boolean shouldContinueExecuting() {
			LivingEntity livingentity = this.witchglob.getAttackTarget();
			if (livingentity == null) {
				return false;
			} else if (!livingentity.isAlive()) {
	            return false;
			} else if (livingentity instanceof PlayerEntity && ((PlayerEntity)livingentity).abilities.disableDamage) {
	            return false;
			} else {
	            return --this.growTieredTimer > 0;
			}
		}

		/**
		 * Keep ticking a continuous task that has already been started
		 */
		public void tick() {
			this.witchglob.faceEntity(this.witchglob.getAttackTarget(), 10.0F, 10.0F);
			((WitchGlobEntity.MoveHelperController)this.witchglob.getMoveHelper()).setDirection(this.witchglob.rotationYaw, true);
		}
	}

	static class FaceRandomGoal extends Goal {
		private final WitchGlobEntity witchglob;
		private float chosenDegrees;
		private int nextRandomizeTime;
		public FaceRandomGoal(WitchGlobEntity witchglobIn) {
			this.witchglob = witchglobIn;
			this.setMutexFlags(EnumSet.of(Goal.Flag.LOOK));
		}

		/**
		 * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
		 * method as well.
		 */
		public boolean shouldExecute() {
			return this.witchglob.getAttackTarget() == null && (this.witchglob.onGround || this.witchglob.isInWater() || this.witchglob.isInLava() || this.witchglob.isPotionActive(Effects.LEVITATION)) && this.witchglob.getMoveHelper() instanceof WitchGlobEntity.MoveHelperController;
		}

		/**
		 * Keep ticking a continuous task that has already been started
		 */
		public void tick() {
			if (--this.nextRandomizeTime <= 0) {
				this.nextRandomizeTime = 40 + this.witchglob.getRNG().nextInt(60);
				this.chosenDegrees = (float)this.witchglob.getRNG().nextInt(360);
			}

			((WitchGlobEntity.MoveHelperController)this.witchglob.getMoveHelper()).setDirection(this.chosenDegrees, false);
		}
	}

	static class FloatGoal extends Goal {
		private final WitchGlobEntity witchglob;
		public FloatGoal(WitchGlobEntity witchglobIn) {
			this.witchglob = witchglobIn;
			this.setMutexFlags(EnumSet.of(Goal.Flag.JUMP, Goal.Flag.MOVE));
			witchglobIn.getNavigator().setCanSwim(true);
		}

		/**
		 * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
		 * method as well.
		 */
		public boolean shouldExecute() {
			return (this.witchglob.isInWater() || this.witchglob.isInLava()) && this.witchglob.getMoveHelper() instanceof WitchGlobEntity.MoveHelperController;
		}

		/**
		 * Keep ticking a continuous task that has already been started
		 */
		public void tick() {
			if (this.witchglob.getRNG().nextFloat() < 0.8F) {
				this.witchglob.getJumpController().setJumping();
			}

			((WitchGlobEntity.MoveHelperController)this.witchglob.getMoveHelper()).setSpeed(1.2D);
		}
	}

	static class HopGoal extends Goal {
		private final WitchGlobEntity witchglob;
		public HopGoal(WitchGlobEntity witchglobIn) {
			this.witchglob = witchglobIn;
			this.setMutexFlags(EnumSet.of(Goal.Flag.JUMP, Goal.Flag.MOVE));
		}

		/**
		 * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
		 * method as well.
		 */
		public boolean shouldExecute() {
			return !this.witchglob.isPassenger();
		}

		/**
		 * Keep ticking a continuous task that has already been started
		 */
		public void tick() {
			((WitchGlobEntity.MoveHelperController)this.witchglob.getMoveHelper()).setSpeed(1.0D);
		}
	}

	static class MoveHelperController extends MovementController {
		private float yRot;
		private int jumpDelay;
		private final WitchGlobEntity witchglob;
		private boolean isAggressive;
		public MoveHelperController(WitchGlobEntity witchglobIn) {
			super(witchglobIn);
			this.witchglob = witchglobIn;
			this.yRot = 180.0F * witchglobIn.rotationYaw / (float)Math.PI;
		}

		public void setDirection(float yRotIn, boolean aggressive) {
			this.yRot = yRotIn;
			this.isAggressive = aggressive;
		}

		public void setSpeed(double speedIn) {
			this.speed = speedIn;
			this.action = MovementController.Action.MOVE_TO;
		}

		public void tick() {
			this.mob.rotationYaw = this.limitAngle(this.mob.rotationYaw, this.yRot, 90.0F);
			this.mob.rotationYawHead = this.mob.rotationYaw;
			this.mob.renderYawOffset = this.mob.rotationYaw;
			if (this.action != MovementController.Action.MOVE_TO) {
				this.mob.setMoveForward(0.0F);
			} else {
				this.action = MovementController.Action.WAIT;
				if (this.mob.isOnGround()) {
					this.mob.setAIMoveSpeed((float)(this.speed * this.mob.getAttributeValue(Attributes.MOVEMENT_SPEED)));
					if (this.jumpDelay-- <= 0) {
						this.jumpDelay = this.witchglob.getJumpDelay();
						if (this.isAggressive) {
							this.jumpDelay /= 3;
						}
						this.witchglob.getJumpController().setJumping();
						if (this.witchglob.makesSoundOnJump()) {
							this.witchglob.playSound(this.witchglob.getJumpSound(), this.witchglob.getSoundVolume(), 0.5F);
						}
					} else {
						this.witchglob.moveStrafing = 0.0F;
						this.witchglob.moveForward = 0.0F;
						this.mob.setAIMoveSpeed(0.0F);
					}
				} else {
					this.mob.setAIMoveSpeed((float)(this.speed * this.mob.getAttributeValue(Attributes.MOVEMENT_SPEED)));
				}
			}
		}
	}
}
