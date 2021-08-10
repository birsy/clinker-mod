package birsy.clinker.common.entity.monster.gnomad;

import birsy.clinker.common.entity.merchant.GnomeBratEntity;
import birsy.clinker.common.entity.merchant.GnomeEntity;
import birsy.clinker.common.entity.monster.HyenaEntity;
import birsy.clinker.core.registry.ClinkerItems;
import birsy.clinker.core.util.MathUtils;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.entity.*;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.monster.WitherSkeleton;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.IndirectEntityDamageSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.phys.AABB;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LeapAtTargetGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;

import EntityDataAccessor;

public class GnomadAxemanEntity extends AbstractGnomadEntity
{
	public int stamina;
	private int maxStamina = 100;
	public static final EntityDataAccessor<Float> SIZE = SynchedEntityData.defineId(GnomadAxemanEntity.class, EntityDataSerializers.FLOAT);
	public static final EntityDataAccessor<Float> BRUTALITY = SynchedEntityData.defineId(GnomadAxemanEntity.class, EntityDataSerializers.FLOAT);
	public static final EntityDataAccessor<Float> PROWESS = SynchedEntityData.defineId(GnomadAxemanEntity.class, EntityDataSerializers.FLOAT);

	public static final EntityDataAccessor<Boolean> ATTACKING = SynchedEntityData.defineId(GnomadAxemanEntity.class, EntityDataSerializers.BOOLEAN);

	public static final EntityDataAccessor<Integer> PREV_WINDUP_TICKS = SynchedEntityData.defineId(GnomadAxemanEntity.class, EntityDataSerializers.INT);
	public static final EntityDataAccessor<Integer> WINDUP_TICKS = SynchedEntityData.defineId(GnomadAxemanEntity.class, EntityDataSerializers.INT);
	public static final EntityDataAccessor<Integer> MAX_WINDUP_TICKS = SynchedEntityData.defineId(GnomadAxemanEntity.class, EntityDataSerializers.INT);

	public static final EntityDataAccessor<Integer> PREV_SWING_TICKS = SynchedEntityData.defineId(GnomadAxemanEntity.class, EntityDataSerializers.INT);
	public static final EntityDataAccessor<Integer> SWING_TICKS = SynchedEntityData.defineId(GnomadAxemanEntity.class, EntityDataSerializers.INT);
	public static final EntityDataAccessor<Integer> MAX_SWING_TICKS = SynchedEntityData.defineId(GnomadAxemanEntity.class, EntityDataSerializers.INT);

	private boolean wasHit;

	public static final EntityDataAccessor<Integer> SHIELDS = SynchedEntityData.defineId(GnomadAxemanEntity.class, EntityDataSerializers.INT);
	public int shieldTransitionAnimation;
	public int shieldSizeRandomness;
	public int shieldRandomness;

	public static final EntityDataAccessor<Boolean> BUFFED = SynchedEntityData.defineId(GnomadAxemanEntity.class, EntityDataSerializers.BOOLEAN);
	private float buffLength;

	public static final EntityDataAccessor<Boolean> HELMET = SynchedEntityData.defineId(GnomadAxemanEntity.class, EntityDataSerializers.BOOLEAN);
	public static final EntityDataAccessor<Boolean> LEFT_PAULDRON = SynchedEntityData.defineId(GnomadAxemanEntity.class, EntityDataSerializers.BOOLEAN);
	public static final EntityDataAccessor<Boolean> RIGHT_PAULDRON = SynchedEntityData.defineId(GnomadAxemanEntity.class, EntityDataSerializers.BOOLEAN);


	public static final EntityDataAccessor<Boolean> DODGE_DIRECTION = SynchedEntityData.defineId(GnomadAxemanEntity.class, EntityDataSerializers.BOOLEAN);
	public static final EntityDataAccessor<Integer> DODGE_TIME = SynchedEntityData.defineId(GnomadAxemanEntity.class, EntityDataSerializers.INT);
	
	public AbstractGnomadEntity.ArmPose armPose;
	
	public GnomadAxemanEntity(EntityType<? extends AbstractGnomadEntity> type, Level worldIn)
	{
		super(type, worldIn);
	    ((GroundPathNavigation)this.getNavigation()).setCanOpenDoors(true);
	    this.getNavigation().setCanFloat(true);
	    this.setCanPickUpLoot(true);
	}
	
	protected void registerGoals() {
		super.registerGoals();
		this.goalSelector.addGoal(0, new FloatGoal(this));

		this.goalSelector.addGoal(3, new LeapAtTargetGoal(this, 0.2F));
		this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.0D, false));

		this.targetSelector.addGoal(2, new HurtByTargetGoal(this).setAlertOthers(AbstractGnomadEntity.class));
		this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, GnomeEntity.class, true));
		this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, GnomeBratEntity.class, true));
		this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Player.class, true));
		this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, HyenaEntity.class, true));
		this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, WitherSkeleton.class, true));
		this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, EnderMan.class, true));

		this.goalSelector.addGoal(8, new RandomStrollGoal(this, 0.6D));

		this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, Player.class, 3.0F, 1.0F));
		this.goalSelector.addGoal(11, new LookAtPlayerGoal(this, GnomeEntity.class, 3.0F, 1.0F));
		this.goalSelector.addGoal(12, new LookAtPlayerGoal(this, AbstractGnomadEntity.class, 3.0F, 1.0F));
		this.goalSelector.addGoal(13, new LookAtPlayerGoal(this, Mob.class, 8.0F));
	}

	protected void defineSynchedData() {
		super.defineSynchedData();
		this.entityData.define(SIZE, 1.0F);
		this.entityData.define(BRUTALITY, 0.0F);
		this.entityData.define(PROWESS, 0.0F);

		this.entityData.define(SHIELDS, 0);
		this.entityData.define(BUFFED, false);

		this.entityData.define(DODGE_TIME, 0);
		this.entityData.define(DODGE_DIRECTION, false);


		this.entityData.define(PREV_WINDUP_TICKS, 0);
		this.entityData.define(WINDUP_TICKS, 0);
		this.entityData.define(MAX_WINDUP_TICKS, 0);

		this.entityData.define(PREV_SWING_TICKS, 0);
		this.entityData.define(SWING_TICKS, 0);
		this.entityData.define(MAX_SWING_TICKS, 0);

		this.entityData.define(ATTACKING, false);


		this.entityData.define(HELMET, false);
		this.entityData.define(LEFT_PAULDRON, false);
		this.entityData.define(RIGHT_PAULDRON, false);
	}

	public void addAdditionalSaveData(CompoundTag compound) {
		super.addAdditionalSaveData(compound);
		compound.putFloat("Size", this.entityData.get(SIZE));
		compound.putFloat("brutality", this.entityData.get(BRUTALITY));
		compound.putFloat("prowess", this.entityData.get(PROWESS));

		compound.putInt("shields", this.getShieldNumber());
		compound.putBoolean("buffed", this.isBuffed());

		compound.putInt("dodge_time", this.getDodgeTime());
		compound.putBoolean("dodge_direction", this.getDodgeDirection());


		compound.putInt("prev_windup_ticks", this.entityData.get(PREV_WINDUP_TICKS));
		compound.putInt("windup_ticks", this.getWindupTicks());
		compound.putInt("max_windup_ticks", this.entityData.get(MAX_WINDUP_TICKS));

		compound.putInt("prev_swing_ticks", this.entityData.get(PREV_SWING_TICKS));
		compound.putInt("swing_ticks", this.getSwingTicks());
		compound.putInt("max_swing_ticks", this.entityData.get(MAX_SWING_TICKS));

		compound.putBoolean("attacking", this.isAttacking());


		compound.putBoolean("helmet", this.isWearingHelmet());
		compound.putBoolean("left_pauldron", this.isWearingLeftPauldron());
		compound.putBoolean("right_pauldron", this.isWearingRightPauldron());
	}

	public void readAdditionalSaveData(CompoundTag compound) {
		super.readAdditionalSaveData(compound);
		this.setSize(compound.getFloat("Size"));
		this.entityData.set(BRUTALITY, compound.getFloat("brutality"));
		this.entityData.set(PROWESS, compound.getFloat("prowess"));

		this.entityData.set(SHIELDS, compound.getInt("shields"));
		this.entityData.set(BUFFED, compound.getBoolean("buffed"));

		this.entityData.set(DODGE_TIME, compound.getInt("dodge_time"));
		this.entityData.set(DODGE_DIRECTION, compound.getBoolean("dodge_direction"));

		this.entityData.set(PREV_WINDUP_TICKS, compound.getInt("prev_windup_ticks"));
		this.entityData.set(WINDUP_TICKS, compound.getInt("windup_ticks"));
		this.entityData.set(MAX_WINDUP_TICKS, compound.getInt("max_windup_ticks"));
		this.entityData.set(PREV_SWING_TICKS, compound.getInt("prev_swing_ticks"));
		this.entityData.set(SWING_TICKS, compound.getInt("swing_ticks"));
		this.entityData.set(MAX_SWING_TICKS, compound.getInt("max_swing_ticks"));
		this.entityData.set(ATTACKING, compound.getBoolean("attacking"));

		this.entityData.set(HELMET, compound.getBoolean("helmet"));
		this.entityData.set(LEFT_PAULDRON, compound.getBoolean("left_pauldron"));
		this.entityData.set(RIGHT_PAULDRON, compound.getBoolean("right_pauldron"));
	}
	
	@Override
	public void tick() {
		this.entityData.set(PREV_SWING_TICKS, getSwingTicks());
		this.entityData.set(PREV_WINDUP_TICKS, getWindupTicks());

		super.tick();

		if (stamina < maxStamina) {
			stamina++;
		}

		if (getDodgeTime() > 0) {
			setDodgeTime(getDodgeTime() - 1);
			if (this.getTarget() !=  null) {
				this.getLookControl().setLookAt(this.getTarget().getEyePosition(0.5f));
				this.lookAt(this.getTarget(), 360, 360);
			}
		} else if (getDodgeTime() < 0) {
			setDodgeTime(0);
		}

		if (getDodgeTime() > 10) {
			Vec3 particleMotion = this.getDeltaMovement().reverse().multiply(0.75, 0.75, 0.75);
			this.level.addParticle(ParticleTypes.SMOKE, this.getRandomX(0.5D), this.getRandomY(), this.getRandomZ(0.5D), particleMotion.x, particleMotion.y, particleMotion.z);
		}

		if (this.isAggressive() && this.getMainHandItem().getItem() instanceof net.minecraft.world.item.BowItem) {
			setArmPose(AbstractGnomadEntity.ArmPose.BOW_AND_ARROW);
		} else {
			setArmPose(AbstractGnomadEntity.ArmPose.NEUTRAL);
		}

		if (this.shieldTransitionAnimation < 25) {
			this.shieldTransitionAnimation++;
		}

		shieldSizeRandomness = this.random.nextInt(5) + 15;
		shieldRandomness = this.random.nextInt(100);
	}

	@Override
	public void aiStep() {
		this.wasHit = false;
		if (shouldAttemptDodge()) {
			this.sideStepDodge();
		}
		super.aiStep();
	}

	@Override
	public boolean skipAttackInteraction(Entity entityIn) {
		this.wasHit = true;
		if (getShieldNumber() > 0) {
			breakShield();
			return true;
		} else {
			return super.skipAttackInteraction(entityIn);
		}
	}

	public int getShieldNumber() {
		return this.entityData.get(SHIELDS);
	}
	public void setShielded(int shieldNumber) {
		boolean flag = this.entityData.get(SHIELDS) == 0;
		this.entityData.set(SHIELDS, shieldNumber);
		if (flag && shieldNumber > 0) {
			this.shieldTransitionAnimation = 0;
			this.playSound(SoundEvents.BEACON_POWER_SELECT, 1.0F, 1.0F);
		}
	}
	public void breakShield() {
		if (getShieldNumber() > 0) {
			if (getShieldNumber() == 1) {
				this.playSound(SoundEvents.BEACON_DEACTIVATE, 1.0F, 1.0F);
			}

			this.playSound(SoundEvents.GLASS_BREAK, 1.0F, 1.0F);

			this.entityData.set(SHIELDS, getShieldNumber() - 1);
			this.level.addParticle(ParticleTypes.POOF, this.getRandomX(0.5D), this.getRandomY(), this.getRandomZ(0.5D), 0.0D, 0.0D, 0.0D);
		}
	}

	public boolean isBuffed() {
		return this.entityData.get(BUFFED);
	}
	public void setBuffed(boolean buffed) {
		if (buffed && !this.entityData.get(BUFFED)) {
			this.playSound(SoundEvents.BEACON_ACTIVATE, 1.0F, 1.0F);
		} else if (this.entityData.get(BUFFED)) {
			this.playSound(SoundEvents.BEACON_DEACTIVATE, 1.0F, 1.0F);
		}

		this.entityData.set(BUFFED, buffed);
	}

	public boolean isWearingHelmet() {
		return this.entityData.get(HELMET);
	}
	public boolean isWearingLeftPauldron() {
		return this.entityData.get(LEFT_PAULDRON);
	}
	public boolean isWearingRightPauldron() {
		return this.entityData.get(RIGHT_PAULDRON);
	}

	public void setWearingHelmet(boolean wearingHelmet) {
		if (wearingHelmet) {
			this.playSound(SoundEvents.ARMOR_EQUIP_IRON, 0.5F, 0.5F);

			this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(this.getAttribute(Attributes.MOVEMENT_SPEED).getBaseValue() - 2);
			this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(this.getAttribute(Attributes.ARMOR).getBaseValue() + 6);
			this.getAttribute(Attributes.KNOCKBACK_RESISTANCE).setBaseValue(this.getAttribute(Attributes.KNOCKBACK_RESISTANCE).getBaseValue() + 10);

			this.setHealth(this.getMaxHealth());
		}
		this.entityData.set(HELMET, wearingHelmet);
	}
	public void setWearingLeftPauldron(boolean wearingLeftPauldron) {
		if (wearingLeftPauldron) {
			this.playSound(SoundEvents.ARMOR_EQUIP_IRON, 0.5F, 0.5F);

			this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(this.getAttribute(Attributes.MOVEMENT_SPEED).getBaseValue() - 1);
			this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(this.getAttribute(Attributes.ARMOR).getBaseValue() + 4);
			this.getAttribute(Attributes.KNOCKBACK_RESISTANCE).setBaseValue(this.getAttribute(Attributes.KNOCKBACK_RESISTANCE).getBaseValue() + 7);

			this.setHealth(this.getMaxHealth());
		}
		this.entityData.set(LEFT_PAULDRON, wearingLeftPauldron);
	}
	public void setWearingRightPauldron(boolean wearingRightPauldron) {
		if (wearingRightPauldron) {
			this.playSound(SoundEvents.ARMOR_EQUIP_IRON, 0.5F, 0.5F);

			this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(this.getAttribute(Attributes.MOVEMENT_SPEED).getBaseValue() - 1);
			this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(this.getAttribute(Attributes.ARMOR).getBaseValue() + 4);
			this.getAttribute(Attributes.KNOCKBACK_RESISTANCE).setBaseValue(this.getAttribute(Attributes.KNOCKBACK_RESISTANCE).getBaseValue() + 7);

			this.setHealth(this.getMaxHealth());
		}
		this.entityData.set(RIGHT_PAULDRON, wearingRightPauldron);
	}


	/**
	 * Makes the entity dodge to the left or right. Might make them dodge off cliffs, but that will be kinda funny.
	 * Can't be done below 20 stamina. Takes 5 stamina to use.
	 */
	public void sideStepDodge() {
		if (stamina >= 20 && getDodgeTime() <= 0) {
			this.setDodgeDirection(this.random.nextBoolean());
			this.setDodgeTime(15);
			this.stamina =- 25;

			double dodgeIntensity = ((random.nextInt(2) + 8) * 0.1) * 0.9;
			Vec3 motionVector = this.getViewVector(1.0F).normalize().yRot(this.getDodgeDirection() ? 90 : -90); //.mul(dodgeIntensity, dodgeIntensity, dodgeIntensity);

			//this.jump();
			this.push(motionVector.x() * dodgeIntensity, motionVector.y() * dodgeIntensity * 0.5F, motionVector.z() * dodgeIntensity);
			this.playSound(SoundEvents.UI_TOAST_IN, 1.5F, 0.75F);
		}
	}

	/**
	 * If a player is its current target, and the player is looking at it, then it will attempt to dodge out of the way.
	 * It also randomly tries to dodge if a player is its current target.
	 */
	private boolean shouldAttemptDodge() {
		boolean flag;
		if (this.getTarget() != null) {
			flag = isEntityHolding(this.getTarget(), Items.BOW) || isEntityHolding(this.getTarget(), Items.CROSSBOW);
			if (this.distanceToSqr(this.getTarget()) < 12 || flag) {
				Vec3 lookVector = this.getTarget().getViewVector(1.0F).normalize();
				Vec3 vector3d1 = new Vec3(this.getX() - this.getTarget().getX(), this.getEyeY() - this.getTarget().getEyeY(), this.getZ() - this.getTarget().getZ());
				double d0 = vector3d1.length();
				vector3d1 = vector3d1.normalize();
				double d1 = lookVector.dot(vector3d1);
				if (flag) {
					return d1 > 1.0D - 0.15D / d0 && this.getTarget().canSee(this);
				} else {
					return d1 > 1.0D - 0.15D / d0 && this.getTarget().canSee(this) && this.random.nextInt(3) == 0;
				}
			}
		}

		return false;
	}

	private boolean isEntityHolding(LivingEntity entityIn, Item itemIn) {
		return entityIn.getMainHandItem().getItem() == itemIn || entityIn.getOffhandItem().getItem() == itemIn;
	}

	public boolean getDodgeDirection() {
		return this.entityData.get(DODGE_DIRECTION);
	}
	public void setDodgeDirection(boolean direction) {
		this.entityData.set(DODGE_DIRECTION, direction);
	}

	public int getDodgeTime() {
		return this.entityData.get(DODGE_TIME);
	}
	public void setDodgeTime(int time) {
		this.entityData.set(DODGE_TIME, time);
	}

	@Override
	public boolean hurt(DamageSource source, float amount) {
		if (source instanceof IndirectEntityDamageSource && getDodgeTime() > 10 * Math.min(0.75, this.entityData.get(PROWESS) + 0.5)) {
			return false;
		} else {
			return super.hurt(source, amount * this.getArmorAttackMultiplier());
		}
	}

	public static class ChargedAttackGoal extends MeleeAttackGoal {
		private final GnomadAxemanEntity gnomad;

		private final int attackTime;
		private final int swingTime;
		private final int damageFrame;
		private final float damage;
		private final float attackRange;
		private final boolean interuptable;

		private int swingCooldown;

		/**
		 * An attack that takes a little bit to execute and has special animations attached to it.
		 *
		 * @param mob The entity you want to have a charged attack.
		 * @param attackTime The time it takes for the specific attack to wind up.
		 * @param swingTime The time it takes for the specific attack to swing.
		 * @param damageFrame The frame damage is dealt on.
		 * @param damage The amount of damage the attack does.
		 * @param attackRange The range of the attack.
		 * @param interuptable Determines whether the attack can be interrupted.
		 */
		public ChargedAttackGoal(GnomadAxemanEntity mob, int attackTime, int swingTime, int damageFrame, float damage, float attackRange, boolean interuptable) {
			super(mob, 1.0D, false);

			this.gnomad = mob;
			this.attackTime = attackTime;
			this.swingTime = swingTime;
			this.damageFrame = damageFrame;
			this.damage = damage;
			this.attackRange = attackRange;
			this.interuptable = interuptable;
		}

		@Override
		public boolean canUse() {
			if (gnomad.getTarget() != null) {
				if (gnomad.getSwingTicks() > 0 || gnomad.getWindupTicks() > 0) {
					return entityInRange(gnomad.getTarget(), this.attackRange) && gnomad.getTarget().isAlive() && super.canUse();
				}
			}

			return false;
		}

		@Override
		public void tick() {
			super.tick();
			LivingEntity livingentity = this.gnomad.getTarget();
			boolean inRange = entityInRange(livingentity, this.attackRange * 1.5F);

			if (swingCooldown <= 0) {
				if (!gnomad.isAttacking() && inRange && gnomad.stamina > 10) {
					this.primeAttack();
				}

				if (gnomad.isAttacking()) {
					if (gnomad.getWindupTicks() > 0) {
						if (inRange) {
							gnomad.setWindupTicks(gnomad.getWindupTicks() - 1);
						} else {
							gnomad.setWindupTicks(Math.min(gnomad.getWindupTicks() + 1, this.attackTime));
						}
					} else {
						gnomad.setSwingTicks(gnomad.getSwingTicks() - 1);
					}

					if (gnomad.getSwingTicks() == damageFrame) {
						attack();
					}
				}
			}

			this.swingCooldown = Math.max(this.swingCooldown - 1, 0);
		}

		@Override
		public boolean canContinueToUse() {
			if (gnomad.getTarget() == null) {
				return false;
			} else if (gnomad.wasHit && this.interuptable) {
				return false;
			} else {
				return super.canContinueToUse();
			}
		}

		private void primeAttack() {
			gnomad.setAttacking(true);
			gnomad.setWindupTicks(attackTime);
			gnomad.setSwingTicks(swingTime);
		}

		private void attack() {
			if (this.gnomad.getTarget() != null) {
				boolean attackSuccessful = false;

				this.gnomad.swing(InteractionHand.MAIN_HAND);
				AABB hurtBox = this.gnomad.getBoundingBox()
						.inflate(0, -(this.gnomad.getBoundingBox().getYsize() / 2), 0)
						.inflate(this.attackRange / 2)
						.move(this.getHitboxOffset().x(), this.getHitboxOffset().y(), this.getHitboxOffset().y());

				for (Entity entity : this.gnomad.getCommandSenderWorld().getEntitiesOfClass(Entity.class, hurtBox)) {
					if (entity != this.gnomad) {
						this.gnomad.attackEntityAsMob(entity, this.damage);
						attackSuccessful = true;
					}
				}

				this.gnomad.stamina -= attackSuccessful ? 15 : 10;
				this.swingCooldown = attackSuccessful ? 25 : 15;
				resetAttack();
			}
		}

		private void resetAttack() {
			gnomad.setAttacking(false);
			gnomad.setWindupTicks(0);
			gnomad.setSwingTicks(0);
		}

		@Override
		public void stop() {
			resetAttack();
			super.stop();
		}

		@Override
		protected void checkAndPerformAttack(LivingEntity enemy, double distToEnemySqr) {}

		private Vec3 getHitboxOffset() {
			double xOffset = -Mth.sin(this.gnomad.yRot * ((float)Math.PI / 180F)) * this.attackRange;
			double zOffset = Mth.cos(this.gnomad.yRot * ((float)Math.PI / 180F)) * this.attackRange;
			return new Vec3(this.gnomad.getX() + xOffset, this.gnomad.getY(0.5D), this.gnomad.getZ() + zOffset);
		}

		private boolean entityInRange(LivingEntity entityIn, float range) {
			return this.gnomad.distanceToSqr(entityIn) < range;
		}
	}

	public boolean isAttacking() {
		return this.entityData.get(ATTACKING);
	}
	public void setAttacking(boolean attacking) {
		this.entityData.set(ATTACKING, attacking);
	}

	@OnlyIn(Dist.CLIENT)
	public float getWindupTicks(float partialTick) {
		return Mth.lerp(partialTick, this.entityData.get(PREV_WINDUP_TICKS), this.entityData.get(WINDUP_TICKS));
	}
	public int getWindupTicks() {
		return this.entityData.get(WINDUP_TICKS);
	}
	public void setWindupTicks(int ticks) {
		this.entityData.set(WINDUP_TICKS, ticks);
	}

	@OnlyIn(Dist.CLIENT)
	public float getSwingTicks(float partialTick) {
		return Mth.lerp(partialTick, this.entityData.get(PREV_SWING_TICKS), this.entityData.get(SWING_TICKS));
	}
	public int getSwingTicks() {
		return this.entityData.get(SWING_TICKS);
	}
	public void setSwingTicks(int ticks) {
		this.entityData.set(SWING_TICKS, ticks);
	}

	public void setSize(float size) {
		this.entityData.set(SIZE, size);
		this.reapplyPosition();
		this.refreshDimensions();

		this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(this.getAttribute(Attributes.MAX_HEALTH).getValue() * size);
		this.setHealth(this.getMaxHealth());
		this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(this.getAttribute(Attributes.MOVEMENT_SPEED).getValue() * ((size * -1) + 2));
		this.getAttribute(Attributes.KNOCKBACK_RESISTANCE).setBaseValue(Math.min(0, size - 1));

		this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(this.getAttribute(Attributes.ATTACK_DAMAGE).getValue() * size);
		this.getAttribute(Attributes.ATTACK_KNOCKBACK).setBaseValue(this.getAttribute(Attributes.ATTACK_KNOCKBACK).getValue() * Math.min(1, size));
	}

	public float getSize() {
		return this.entityData.get(SIZE);
	}

	@OnlyIn(Dist.CLIENT)
	public AbstractGnomadEntity.ArmPose getArmPose() {
		return this.armPose;
	}
	
	@OnlyIn(Dist.CLIENT)
	public void setArmPose(AbstractGnomadEntity.ArmPose pose) {
		this.armPose = pose;
	}

	public boolean attackEntityAsMob(Entity entityIn, float damage) {
		float knockback = (float)this.getAttributeValue(Attributes.ATTACK_KNOCKBACK);
		if (entityIn instanceof LivingEntity) {
			damage += EnchantmentHelper.getDamageBonus(this.getMainHandItem(), ((LivingEntity)entityIn).getMobType());
			knockback += (float)EnchantmentHelper.getKnockbackBonus(this);
		}

		int i = EnchantmentHelper.getFireAspect(this);
		if (i > 0) {
			entityIn.setSecondsOnFire(i * 4);
		}

		boolean flag = entityIn.hurt(DamageSource.mobAttack(this), damage);
		if (flag) {
			if (knockback > 0.0F && entityIn instanceof LivingEntity) {
				((LivingEntity)entityIn).knockback(knockback * 0.5F, Mth.sin(this.yRot * ((float)Math.PI / 180F)), -Mth.cos(this.yRot * ((float)Math.PI / 180F)));
				this.setDeltaMovement(this.getDeltaMovement().multiply(0.6D, 1.0D, 0.6D));
			}

			if (entityIn instanceof Player) {
				Player playerentity = (Player)entityIn;
				this.disableShield(playerentity, this.getMainHandItem(), playerentity.isUsingItem() ? playerentity.getUseItem() : ItemStack.EMPTY);
			}

			this.doEnchantDamageEffects(this, entityIn);
			this.setLastHurtMob(entityIn);
		}

		return flag;
	}

	public void disableShield (Player entity, ItemStack mainHandItem, ItemStack otherHandItem) {
		if (!mainHandItem.isEmpty() && !otherHandItem.isEmpty() && mainHandItem.getItem() instanceof AxeItem && otherHandItem.getItem() == Items.SHIELD) {
			float f = 0.25F + (float)EnchantmentHelper.getBlockEfficiency(this) * 0.05F;
			if (this.random.nextFloat() < f) {
				entity.getCooldowns().addCooldown(Items.SHIELD, 100);
				this.level.broadcastEntityEvent(entity, (byte)30);
			}
		}
	}
	
	@Override
	protected void populateDefaultEquipmentSlots(DifficultyInstance difficulty) {
		//Returns a lower value the more difficult it is.
		// <1 = hard, <2 = normal, <3 = easy
		float difficultyValue = (this.level.getDifficulty() == Difficulty.HARD ? 1.0f : this.level.getDifficulty() == Difficulty.NORMAL ? 2.0f : 3.0f) - difficulty.getEffectiveDifficulty();
		float inverseDifficulty = (difficultyValue - 3) * -1;

		for (int i = 0; i < inverseDifficulty + (this.getSkillValue() * 2); i++) {
			if (MathUtils.getRandomFloatBetween(random, 0, 3) < difficultyValue) {
				this.setRandomArmor();
			}
		}

		if (random.nextBoolean()) {
			if (MathUtils.getRandomFloatBetween(random, 0, 3) < difficultyValue && random.nextBoolean()) {
				this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(ClinkerItems.LEAD_AXE.get()));
			} else if (random.nextInt(5) != 0) {
				this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(ClinkerItems.LEAD_SWORD.get()));
			} else {
				this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.BOW));
			}
		} else {
			this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(ClinkerItems.LEAD_AXE.get()));
		}

		if (MathUtils.getRandomFloatBetween(random, 0, 3) < difficultyValue * 0.25F) {
			this.setItemSlot(EquipmentSlot.OFFHAND, new ItemStack(Items.SHIELD.getItem()));
		}

		super.populateDefaultEquipmentSlots(difficulty);
	}
	
	private void setRandomArmor() {
		if (!this.entityData.get(HELMET) || !this.entityData.get(LEFT_PAULDRON) || !this.entityData.get(RIGHT_PAULDRON)) {
			int randomInt = random.nextInt(3);

			if (randomInt == 0) {
				if (!entityData.get(HELMET)) {
					this.setWearingHelmet(true);
				} else {
					this.setRandomArmor();
				}
			} else if (randomInt == 1) {
				if (!entityData.get(LEFT_PAULDRON)) {
					this.setWearingLeftPauldron(true);
				} else {
					this.setRandomArmor();
				}
			} else {
				if (!entityData.get(RIGHT_PAULDRON)) {
					this.setWearingRightPauldron(true);
				} else {
					this.setRandomArmor();
				}
			}
		}
	}

	private void setSkills(DifficultyInstance difficulty) {
		//Returns a lower value the more difficult it is.
		// <1 = hard, <2 = normal, <3 = easy
		float difficultyValue = (this.level.getDifficulty() == Difficulty.HARD ? 1.0f : this.level.getDifficulty() == Difficulty.NORMAL ? 2.0f : 3.0f) - difficulty.getEffectiveDifficulty();
		float inverseDifficulty = (difficultyValue - 3) * -1;

		float skillAllocationAmount = MathUtils.bias(random.nextFloat(), 0.7F) * inverseDifficulty;
		float brutality = MathUtils.getRandomFloatBetween(random, 0, skillAllocationAmount);
		float prowess = skillAllocationAmount - brutality;

		float sizeRand = MathUtils.getRandomFloatBetween(random, 0.8F, 1.0F);
		float size = MathUtils.getRandomFloatBetween(random, sizeRand, ((sizeRand + (brutality / inverseDifficulty)) / 2) + 1);

		this.setSize(size);
		this.entityData.set(BRUTALITY, brutality);

		this.maxStamina = (int) (100 * Math.min(0.75, prowess + 0.5));
		this.entityData.set(PROWESS, prowess);
	}

	public float getSkillValue() {
		return this.entityData.get(BRUTALITY) + this.entityData.get(PROWESS);
	}

	private float getArmorAttackMultiplier() {
		float multiplier = 1;

		if (entityData.get(HELMET)) {
			multiplier =- 0.25F;
		}
		if (entityData.get(LEFT_PAULDRON)) {
			multiplier =- 0.125F;
		}
		if (entityData.get(RIGHT_PAULDRON)) {
			multiplier =- 0.125F;
		}

		return multiplier;
	}

	@Nullable
	@Override
	public SpawnGroupData finalizeSpawn(ServerLevelAccessor worldIn, DifficultyInstance difficultyIn, MobSpawnType reason, @Nullable SpawnGroupData spawnDataIn, @Nullable CompoundTag dataTag) {
		this.setSkills(difficultyIn);
		this.populateDefaultEquipmentSlots(difficultyIn);
		return super.finalizeSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
	}
}
