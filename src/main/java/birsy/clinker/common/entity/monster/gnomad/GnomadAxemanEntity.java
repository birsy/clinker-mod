package birsy.clinker.common.entity.monster.gnomad;

import birsy.clinker.common.entity.merchant.GnomeBratEntity;
import birsy.clinker.common.entity.merchant.GnomeEntity;
import birsy.clinker.common.entity.monster.HyenaEntity;
import birsy.clinker.core.registry.ClinkerItems;
import birsy.clinker.core.util.MathUtils;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.monster.EndermanEntity;
import net.minecraft.entity.monster.WitherSkeletonEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.AxeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.pathfinding.GroundPathNavigator;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.IndirectEntityDamageSource;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;

public class GnomadAxemanEntity extends AbstractGnomadEntity
{
	public int stamina;
	private int maxStamina = 100;
	public static final DataParameter<Float> SIZE = EntityDataManager.createKey(GnomadAxemanEntity.class, DataSerializers.FLOAT);
	public static final DataParameter<Float> BRUTALITY = EntityDataManager.createKey(GnomadAxemanEntity.class, DataSerializers.FLOAT);
	public static final DataParameter<Float> PROWESS = EntityDataManager.createKey(GnomadAxemanEntity.class, DataSerializers.FLOAT);

	public static final DataParameter<Boolean> ATTACKING = EntityDataManager.createKey(GnomadAxemanEntity.class, DataSerializers.BOOLEAN);

	public static final DataParameter<Integer> PREV_WINDUP_TICKS = EntityDataManager.createKey(GnomadAxemanEntity.class, DataSerializers.VARINT);
	public static final DataParameter<Integer> WINDUP_TICKS = EntityDataManager.createKey(GnomadAxemanEntity.class, DataSerializers.VARINT);
	public static final DataParameter<Integer> MAX_WINDUP_TICKS = EntityDataManager.createKey(GnomadAxemanEntity.class, DataSerializers.VARINT);

	public static final DataParameter<Integer> PREV_SWING_TICKS = EntityDataManager.createKey(GnomadAxemanEntity.class, DataSerializers.VARINT);
	public static final DataParameter<Integer> SWING_TICKS = EntityDataManager.createKey(GnomadAxemanEntity.class, DataSerializers.VARINT);
	public static final DataParameter<Integer> MAX_SWING_TICKS = EntityDataManager.createKey(GnomadAxemanEntity.class, DataSerializers.VARINT);

	private boolean wasHit;

	public static final DataParameter<Integer> SHIELDS = EntityDataManager.createKey(GnomadAxemanEntity.class, DataSerializers.VARINT);
	public int shieldTransitionAnimation;
	public int shieldSizeRandomness;
	public int shieldRandomness;

	public static final DataParameter<Boolean> BUFFED = EntityDataManager.createKey(GnomadAxemanEntity.class, DataSerializers.BOOLEAN);
	private float buffLength;

	public static final DataParameter<Boolean> HELMET = EntityDataManager.createKey(GnomadAxemanEntity.class, DataSerializers.BOOLEAN);
	public static final DataParameter<Boolean> LEFT_PAULDRON = EntityDataManager.createKey(GnomadAxemanEntity.class, DataSerializers.BOOLEAN);
	public static final DataParameter<Boolean> RIGHT_PAULDRON = EntityDataManager.createKey(GnomadAxemanEntity.class, DataSerializers.BOOLEAN);


	public static final DataParameter<Boolean> DODGE_DIRECTION = EntityDataManager.createKey(GnomadAxemanEntity.class, DataSerializers.BOOLEAN);
	public static final DataParameter<Integer> DODGE_TIME = EntityDataManager.createKey(GnomadAxemanEntity.class, DataSerializers.VARINT);
	
	public AbstractGnomadEntity.ArmPose armPose;
	
	public GnomadAxemanEntity(EntityType<? extends AbstractGnomadEntity> type, World worldIn)
	{
		super(type, worldIn);
	    ((GroundPathNavigator)this.getNavigator()).setBreakDoors(true);
	    this.getNavigator().setCanSwim(true);
	    this.setCanPickUpLoot(true);
	}
	
	protected void registerGoals() {
		super.registerGoals();
		this.goalSelector.addGoal(0, new SwimGoal(this));

		this.goalSelector.addGoal(3, new LeapAtTargetGoal(this, 0.2F));
		this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.0D, false));

		this.targetSelector.addGoal(2, new HurtByTargetGoal(this).setCallsForHelp(AbstractGnomadEntity.class));
		this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, GnomeEntity.class, true));
		this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, GnomeBratEntity.class, true));
		this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, PlayerEntity.class, true));
		this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, HyenaEntity.class, true));
		this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, WitherSkeletonEntity.class, true));
		this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, EndermanEntity.class, true));

		this.goalSelector.addGoal(8, new RandomWalkingGoal(this, 0.6D));

		this.goalSelector.addGoal(9, new LookAtGoal(this, PlayerEntity.class, 3.0F, 1.0F));
		this.goalSelector.addGoal(11, new LookAtGoal(this, GnomeEntity.class, 3.0F, 1.0F));
		this.goalSelector.addGoal(12, new LookAtGoal(this, AbstractGnomadEntity.class, 3.0F, 1.0F));
		this.goalSelector.addGoal(13, new LookAtGoal(this, MobEntity.class, 8.0F));
	}

	protected void registerData() {
		super.registerData();
		this.dataManager.register(SIZE, 1.0F);
		this.dataManager.register(BRUTALITY, 0.0F);
		this.dataManager.register(PROWESS, 0.0F);

		this.dataManager.register(SHIELDS, 0);
		this.dataManager.register(BUFFED, false);

		this.dataManager.register(DODGE_TIME, 0);
		this.dataManager.register(DODGE_DIRECTION, false);


		this.dataManager.register(PREV_WINDUP_TICKS, 0);
		this.dataManager.register(WINDUP_TICKS, 0);
		this.dataManager.register(MAX_WINDUP_TICKS, 0);

		this.dataManager.register(PREV_SWING_TICKS, 0);
		this.dataManager.register(SWING_TICKS, 0);
		this.dataManager.register(MAX_SWING_TICKS, 0);

		this.dataManager.register(ATTACKING, false);


		this.dataManager.register(HELMET, false);
		this.dataManager.register(LEFT_PAULDRON, false);
		this.dataManager.register(RIGHT_PAULDRON, false);
	}

	public void writeAdditional(CompoundNBT compound) {
		super.writeAdditional(compound);
		compound.putFloat("Size", this.dataManager.get(SIZE));
		compound.putFloat("brutality", this.dataManager.get(BRUTALITY));
		compound.putFloat("prowess", this.dataManager.get(PROWESS));

		compound.putInt("shields", this.getShieldNumber());
		compound.putBoolean("buffed", this.isBuffed());

		compound.putInt("dodge_time", this.getDodgeTime());
		compound.putBoolean("dodge_direction", this.getDodgeDirection());


		compound.putInt("prev_windup_ticks", this.dataManager.get(PREV_WINDUP_TICKS));
		compound.putInt("windup_ticks", this.getWindupTicks());
		compound.putInt("max_windup_ticks", this.dataManager.get(MAX_WINDUP_TICKS));

		compound.putInt("prev_swing_ticks", this.dataManager.get(PREV_SWING_TICKS));
		compound.putInt("swing_ticks", this.getSwingTicks());
		compound.putInt("max_swing_ticks", this.dataManager.get(MAX_SWING_TICKS));

		compound.putBoolean("attacking", this.isAttacking());


		compound.putBoolean("helmet", this.isWearingHelmet());
		compound.putBoolean("left_pauldron", this.isWearingLeftPauldron());
		compound.putBoolean("right_pauldron", this.isWearingRightPauldron());
	}

	public void readAdditional(CompoundNBT compound) {
		super.readAdditional(compound);
		this.setSize(compound.getFloat("Size"));
		this.dataManager.set(BRUTALITY, compound.getFloat("brutality"));
		this.dataManager.set(PROWESS, compound.getFloat("prowess"));

		this.dataManager.set(SHIELDS, compound.getInt("shields"));
		this.dataManager.set(BUFFED, compound.getBoolean("buffed"));

		this.dataManager.set(DODGE_TIME, compound.getInt("dodge_time"));
		this.dataManager.set(DODGE_DIRECTION, compound.getBoolean("dodge_direction"));

		this.dataManager.set(PREV_WINDUP_TICKS, compound.getInt("prev_windup_ticks"));
		this.dataManager.set(WINDUP_TICKS, compound.getInt("windup_ticks"));
		this.dataManager.set(MAX_WINDUP_TICKS, compound.getInt("max_windup_ticks"));
		this.dataManager.set(PREV_SWING_TICKS, compound.getInt("prev_swing_ticks"));
		this.dataManager.set(SWING_TICKS, compound.getInt("swing_ticks"));
		this.dataManager.set(MAX_SWING_TICKS, compound.getInt("max_swing_ticks"));
		this.dataManager.set(ATTACKING, compound.getBoolean("attacking"));

		this.dataManager.set(HELMET, compound.getBoolean("helmet"));
		this.dataManager.set(LEFT_PAULDRON, compound.getBoolean("left_pauldron"));
		this.dataManager.set(RIGHT_PAULDRON, compound.getBoolean("right_pauldron"));
	}
	
	@Override
	public void tick() {
		this.dataManager.set(PREV_SWING_TICKS, getSwingTicks());
		this.dataManager.set(PREV_WINDUP_TICKS, getWindupTicks());

		super.tick();

		if (stamina < maxStamina) {
			stamina++;
		}

		if (getDodgeTime() > 0) {
			setDodgeTime(getDodgeTime() - 1);
			if (this.getAttackTarget() !=  null) {
				this.getLookController().setLookPosition(this.getAttackTarget().getEyePosition(0.5f));
				this.faceEntity(this.getAttackTarget(), 360, 360);
			}
		} else if (getDodgeTime() < 0) {
			setDodgeTime(0);
		}

		if (getDodgeTime() > 10) {
			Vector3d particleMotion = this.getMotion().inverse().mul(0.75, 0.75, 0.75);
			this.world.addParticle(ParticleTypes.SMOKE, this.getPosXRandom(0.5D), this.getPosYRandom(), this.getPosZRandom(0.5D), particleMotion.x, particleMotion.y, particleMotion.z);
		}

		if (this.isAggressive() && this.getHeldItemMainhand().getItem() instanceof net.minecraft.item.BowItem) {
			setArmPose(AbstractGnomadEntity.ArmPose.BOW_AND_ARROW);
		} else {
			setArmPose(AbstractGnomadEntity.ArmPose.NEUTRAL);
		}

		if (this.shieldTransitionAnimation < 25) {
			this.shieldTransitionAnimation++;
		}

		shieldSizeRandomness = this.rand.nextInt(5) + 15;
		shieldRandomness = this.rand.nextInt(100);
	}

	@Override
	public void livingTick() {
		this.wasHit = false;
		if (shouldAttemptDodge()) {
			this.sideStepDodge();
		}
		super.livingTick();
	}

	@Override
	public boolean hitByEntity(Entity entityIn) {
		this.wasHit = true;
		if (getShieldNumber() > 0) {
			breakShield();
			return true;
		} else {
			return super.hitByEntity(entityIn);
		}
	}

	public int getShieldNumber() {
		return this.dataManager.get(SHIELDS);
	}
	public void setShielded(int shieldNumber) {
		boolean flag = this.dataManager.get(SHIELDS) == 0;
		this.dataManager.set(SHIELDS, shieldNumber);
		if (flag && shieldNumber > 0) {
			this.shieldTransitionAnimation = 0;
			this.playSound(SoundEvents.BLOCK_BEACON_POWER_SELECT, 1.0F, 1.0F);
		}
	}
	public void breakShield() {
		if (getShieldNumber() > 0) {
			if (getShieldNumber() == 1) {
				this.playSound(SoundEvents.BLOCK_BEACON_DEACTIVATE, 1.0F, 1.0F);
			}

			this.playSound(SoundEvents.BLOCK_GLASS_BREAK, 1.0F, 1.0F);

			this.dataManager.set(SHIELDS, getShieldNumber() - 1);
			this.world.addParticle(ParticleTypes.POOF, this.getPosXRandom(0.5D), this.getPosYRandom(), this.getPosZRandom(0.5D), 0.0D, 0.0D, 0.0D);
		}
	}

	public boolean isBuffed() {
		return this.dataManager.get(BUFFED);
	}
	public void setBuffed(boolean buffed) {
		if (buffed && !this.dataManager.get(BUFFED)) {
			this.playSound(SoundEvents.BLOCK_BEACON_ACTIVATE, 1.0F, 1.0F);
		} else if (this.dataManager.get(BUFFED)) {
			this.playSound(SoundEvents.BLOCK_BEACON_DEACTIVATE, 1.0F, 1.0F);
		}

		this.dataManager.set(BUFFED, buffed);
	}

	public boolean isWearingHelmet() {
		return this.dataManager.get(HELMET);
	}
	public boolean isWearingLeftPauldron() {
		return this.dataManager.get(LEFT_PAULDRON);
	}
	public boolean isWearingRightPauldron() {
		return this.dataManager.get(RIGHT_PAULDRON);
	}

	public void setWearingHelmet(boolean wearingHelmet) {
		if (wearingHelmet) {
			this.playSound(SoundEvents.ITEM_ARMOR_EQUIP_IRON, 0.5F, 0.5F);

			this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(this.getAttribute(Attributes.MOVEMENT_SPEED).getBaseValue() - 2);
			this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(this.getAttribute(Attributes.ARMOR).getBaseValue() + 6);
			this.getAttribute(Attributes.KNOCKBACK_RESISTANCE).setBaseValue(this.getAttribute(Attributes.KNOCKBACK_RESISTANCE).getBaseValue() + 10);

			this.setHealth(this.getMaxHealth());
		}
		this.dataManager.set(HELMET, wearingHelmet);
	}
	public void setWearingLeftPauldron(boolean wearingLeftPauldron) {
		if (wearingLeftPauldron) {
			this.playSound(SoundEvents.ITEM_ARMOR_EQUIP_IRON, 0.5F, 0.5F);

			this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(this.getAttribute(Attributes.MOVEMENT_SPEED).getBaseValue() - 1);
			this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(this.getAttribute(Attributes.ARMOR).getBaseValue() + 4);
			this.getAttribute(Attributes.KNOCKBACK_RESISTANCE).setBaseValue(this.getAttribute(Attributes.KNOCKBACK_RESISTANCE).getBaseValue() + 7);

			this.setHealth(this.getMaxHealth());
		}
		this.dataManager.set(LEFT_PAULDRON, wearingLeftPauldron);
	}
	public void setWearingRightPauldron(boolean wearingRightPauldron) {
		if (wearingRightPauldron) {
			this.playSound(SoundEvents.ITEM_ARMOR_EQUIP_IRON, 0.5F, 0.5F);

			this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(this.getAttribute(Attributes.MOVEMENT_SPEED).getBaseValue() - 1);
			this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(this.getAttribute(Attributes.ARMOR).getBaseValue() + 4);
			this.getAttribute(Attributes.KNOCKBACK_RESISTANCE).setBaseValue(this.getAttribute(Attributes.KNOCKBACK_RESISTANCE).getBaseValue() + 7);

			this.setHealth(this.getMaxHealth());
		}
		this.dataManager.set(RIGHT_PAULDRON, wearingRightPauldron);
	}


	/**
	 * Makes the entity dodge to the left or right. Might make them dodge off cliffs, but that will be kinda funny.
	 * Can't be done below 20 stamina. Takes 5 stamina to use.
	 */
	public void sideStepDodge() {
		if (stamina >= 20 && getDodgeTime() <= 0) {
			this.setDodgeDirection(this.rand.nextBoolean());
			this.setDodgeTime(15);
			this.stamina =- 25;

			double dodgeIntensity = ((rand.nextInt(2) + 8) * 0.1) * 0.9;
			Vector3d motionVector = this.getLook(1.0F).normalize().rotateYaw(this.getDodgeDirection() ? 90 : -90); //.mul(dodgeIntensity, dodgeIntensity, dodgeIntensity);

			//this.jump();
			this.addVelocity(motionVector.getX() * dodgeIntensity, motionVector.getY() * dodgeIntensity * 0.5F, motionVector.getZ() * dodgeIntensity);
			this.playSound(SoundEvents.UI_TOAST_IN, 1.5F, 0.75F);
		}
	}

	/**
	 * If a player is its current target, and the player is looking at it, then it will attempt to dodge out of the way.
	 * It also randomly tries to dodge if a player is its current target.
	 */
	private boolean shouldAttemptDodge() {
		boolean flag;
		if (this.getAttackTarget() != null) {
			flag = isEntityHolding(this.getAttackTarget(), Items.BOW) || isEntityHolding(this.getAttackTarget(), Items.CROSSBOW);
			if (this.getDistanceSq(this.getAttackTarget()) < 12 || flag) {
				Vector3d lookVector = this.getAttackTarget().getLook(1.0F).normalize();
				Vector3d vector3d1 = new Vector3d(this.getPosX() - this.getAttackTarget().getPosX(), this.getPosYEye() - this.getAttackTarget().getPosYEye(), this.getPosZ() - this.getAttackTarget().getPosZ());
				double d0 = vector3d1.length();
				vector3d1 = vector3d1.normalize();
				double d1 = lookVector.dotProduct(vector3d1);
				if (flag) {
					return d1 > 1.0D - 0.15D / d0 && this.getAttackTarget().canEntityBeSeen(this);
				} else {
					return d1 > 1.0D - 0.15D / d0 && this.getAttackTarget().canEntityBeSeen(this) && this.rand.nextInt(3) == 0;
				}
			}
		}

		return false;
	}

	private boolean isEntityHolding(LivingEntity entityIn, Item itemIn) {
		return entityIn.getHeldItemMainhand().getItem() == itemIn || entityIn.getHeldItemOffhand().getItem() == itemIn;
	}

	public boolean getDodgeDirection() {
		return this.dataManager.get(DODGE_DIRECTION);
	}
	public void setDodgeDirection(boolean direction) {
		this.dataManager.set(DODGE_DIRECTION, direction);
	}

	public int getDodgeTime() {
		return this.dataManager.get(DODGE_TIME);
	}
	public void setDodgeTime(int time) {
		this.dataManager.set(DODGE_TIME, time);
	}

	@Override
	public boolean attackEntityFrom(DamageSource source, float amount) {
		if (source instanceof IndirectEntityDamageSource && getDodgeTime() > 10 * Math.min(0.75, this.dataManager.get(PROWESS) + 0.5)) {
			return false;
		} else {
			return super.attackEntityFrom(source, amount * this.getArmorAttackMultiplier());
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
		public boolean shouldExecute() {
			if (gnomad.getAttackTarget() != null) {
				if (gnomad.getSwingTicks() > 0 || gnomad.getWindupTicks() > 0) {
					return entityInRange(gnomad.getAttackTarget(), this.attackRange) && gnomad.getAttackTarget().isAlive() && super.shouldExecute();
				}
			}

			return false;
		}

		@Override
		public void tick() {
			super.tick();
			LivingEntity livingentity = this.gnomad.getAttackTarget();
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
		public boolean shouldContinueExecuting() {
			if (gnomad.getAttackTarget() == null) {
				return false;
			} else if (gnomad.wasHit && this.interuptable) {
				return false;
			} else {
				return super.shouldContinueExecuting();
			}
		}

		private void primeAttack() {
			gnomad.setAttacking(true);
			gnomad.setWindupTicks(attackTime);
			gnomad.setSwingTicks(swingTime);
		}

		private void attack() {
			if (this.gnomad.getAttackTarget() != null) {
				boolean attackSuccessful = false;

				this.gnomad.swingArm(Hand.MAIN_HAND);
				AxisAlignedBB hurtBox = this.gnomad.getBoundingBox()
						.grow(0, -(this.gnomad.getBoundingBox().getYSize() / 2), 0)
						.grow(this.attackRange / 2)
						.offset(this.getHitboxOffset().getX(), this.getHitboxOffset().getY(), this.getHitboxOffset().getY());

				for (Entity entity : this.gnomad.getEntityWorld().getEntitiesWithinAABB(Entity.class, hurtBox)) {
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
		public void resetTask() {
			resetAttack();
			super.resetTask();
		}

		@Override
		protected void checkAndPerformAttack(LivingEntity enemy, double distToEnemySqr) {}

		private Vector3d getHitboxOffset() {
			double xOffset = -MathHelper.sin(this.gnomad.rotationYaw * ((float)Math.PI / 180F)) * this.attackRange;
			double zOffset = MathHelper.cos(this.gnomad.rotationYaw * ((float)Math.PI / 180F)) * this.attackRange;
			return new Vector3d(this.gnomad.getPosX() + xOffset, this.gnomad.getPosYHeight(0.5D), this.gnomad.getPosZ() + zOffset);
		}

		private boolean entityInRange(LivingEntity entityIn, float range) {
			return this.gnomad.getDistanceSq(entityIn) < range;
		}
	}

	public boolean isAttacking() {
		return this.dataManager.get(ATTACKING);
	}
	public void setAttacking(boolean attacking) {
		this.dataManager.set(ATTACKING, attacking);
	}

	@OnlyIn(Dist.CLIENT)
	public float getWindupTicks(float partialTick) {
		return MathHelper.lerp(partialTick, this.dataManager.get(PREV_WINDUP_TICKS), this.dataManager.get(WINDUP_TICKS));
	}
	public int getWindupTicks() {
		return this.dataManager.get(WINDUP_TICKS);
	}
	public void setWindupTicks(int ticks) {
		this.dataManager.set(WINDUP_TICKS, ticks);
	}

	@OnlyIn(Dist.CLIENT)
	public float getSwingTicks(float partialTick) {
		return MathHelper.lerp(partialTick, this.dataManager.get(PREV_SWING_TICKS), this.dataManager.get(SWING_TICKS));
	}
	public int getSwingTicks() {
		return this.dataManager.get(SWING_TICKS);
	}
	public void setSwingTicks(int ticks) {
		this.dataManager.set(SWING_TICKS, ticks);
	}

	public void setSize(float size) {
		this.dataManager.set(SIZE, size);
		this.recenterBoundingBox();
		this.recalculateSize();

		this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(this.getAttribute(Attributes.MAX_HEALTH).getValue() * size);
		this.setHealth(this.getMaxHealth());
		this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(this.getAttribute(Attributes.MOVEMENT_SPEED).getValue() * ((size * -1) + 2));
		this.getAttribute(Attributes.KNOCKBACK_RESISTANCE).setBaseValue(Math.min(0, size - 1));

		this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(this.getAttribute(Attributes.ATTACK_DAMAGE).getValue() * size);
		this.getAttribute(Attributes.ATTACK_KNOCKBACK).setBaseValue(this.getAttribute(Attributes.ATTACK_KNOCKBACK).getValue() * Math.min(1, size));
	}

	public float getSize() {
		return this.dataManager.get(SIZE);
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
			damage += EnchantmentHelper.getModifierForCreature(this.getHeldItemMainhand(), ((LivingEntity)entityIn).getCreatureAttribute());
			knockback += (float)EnchantmentHelper.getKnockbackModifier(this);
		}

		int i = EnchantmentHelper.getFireAspectModifier(this);
		if (i > 0) {
			entityIn.setFire(i * 4);
		}

		boolean flag = entityIn.attackEntityFrom(DamageSource.causeMobDamage(this), damage);
		if (flag) {
			if (knockback > 0.0F && entityIn instanceof LivingEntity) {
				((LivingEntity)entityIn).applyKnockback(knockback * 0.5F, MathHelper.sin(this.rotationYaw * ((float)Math.PI / 180F)), -MathHelper.cos(this.rotationYaw * ((float)Math.PI / 180F)));
				this.setMotion(this.getMotion().mul(0.6D, 1.0D, 0.6D));
			}

			if (entityIn instanceof PlayerEntity) {
				PlayerEntity playerentity = (PlayerEntity)entityIn;
				this.disableShield(playerentity, this.getHeldItemMainhand(), playerentity.isHandActive() ? playerentity.getActiveItemStack() : ItemStack.EMPTY);
			}

			this.applyEnchantments(this, entityIn);
			this.setLastAttackedEntity(entityIn);
		}

		return flag;
	}

	public void disableShield (PlayerEntity entity, ItemStack mainHandItem, ItemStack otherHandItem) {
		if (!mainHandItem.isEmpty() && !otherHandItem.isEmpty() && mainHandItem.getItem() instanceof AxeItem && otherHandItem.getItem() == Items.SHIELD) {
			float f = 0.25F + (float)EnchantmentHelper.getEfficiencyModifier(this) * 0.05F;
			if (this.rand.nextFloat() < f) {
				entity.getCooldownTracker().setCooldown(Items.SHIELD, 100);
				this.world.setEntityState(entity, (byte)30);
			}
		}
	}
	
	@Override
	protected void setEquipmentBasedOnDifficulty(DifficultyInstance difficulty) {
		//Returns a lower value the more difficult it is.
		// <1 = hard, <2 = normal, <3 = easy
		float difficultyValue = (this.world.getDifficulty() == Difficulty.HARD ? 1.0f : this.world.getDifficulty() == Difficulty.NORMAL ? 2.0f : 3.0f) - difficulty.getAdditionalDifficulty();
		float inverseDifficulty = (difficultyValue - 3) * -1;

		for (int i = 0; i < inverseDifficulty + (this.getSkillValue() * 2); i++) {
			if (MathUtils.getRandomFloatBetween(rand, 0, 3) < difficultyValue) {
				this.setRandomArmor();
			}
		}

		if (rand.nextBoolean()) {
			if (MathUtils.getRandomFloatBetween(rand, 0, 3) < difficultyValue && rand.nextBoolean()) {
				this.setItemStackToSlot(EquipmentSlotType.MAINHAND, new ItemStack(ClinkerItems.LEAD_AXE.get()));
			} else if (rand.nextInt(5) != 0) {
				this.setItemStackToSlot(EquipmentSlotType.MAINHAND, new ItemStack(ClinkerItems.LEAD_SWORD.get()));
			} else {
				this.setItemStackToSlot(EquipmentSlotType.MAINHAND, new ItemStack(Items.BOW));
			}
		} else {
			this.setItemStackToSlot(EquipmentSlotType.MAINHAND, new ItemStack(ClinkerItems.LEAD_AXE.get()));
		}

		if (MathUtils.getRandomFloatBetween(rand, 0, 3) < difficultyValue * 0.25F) {
			this.setItemStackToSlot(EquipmentSlotType.OFFHAND, new ItemStack(Items.SHIELD.getItem()));
		}

		super.setEquipmentBasedOnDifficulty(difficulty);
	}
	
	private void setRandomArmor() {
		if (!this.dataManager.get(HELMET) || !this.dataManager.get(LEFT_PAULDRON) || !this.dataManager.get(RIGHT_PAULDRON)) {
			int randomInt = rand.nextInt(3);

			if (randomInt == 0) {
				if (!dataManager.get(HELMET)) {
					this.setWearingHelmet(true);
				} else {
					this.setRandomArmor();
				}
			} else if (randomInt == 1) {
				if (!dataManager.get(LEFT_PAULDRON)) {
					this.setWearingLeftPauldron(true);
				} else {
					this.setRandomArmor();
				}
			} else {
				if (!dataManager.get(RIGHT_PAULDRON)) {
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
		float difficultyValue = (this.world.getDifficulty() == Difficulty.HARD ? 1.0f : this.world.getDifficulty() == Difficulty.NORMAL ? 2.0f : 3.0f) - difficulty.getAdditionalDifficulty();
		float inverseDifficulty = (difficultyValue - 3) * -1;

		float skillAllocationAmount = MathUtils.bias(rand.nextFloat(), 0.7F) * inverseDifficulty;
		float brutality = MathUtils.getRandomFloatBetween(rand, 0, skillAllocationAmount);
		float prowess = skillAllocationAmount - brutality;

		float sizeRand = MathUtils.getRandomFloatBetween(rand, 0.8F, 1.0F);
		float size = MathUtils.getRandomFloatBetween(rand, sizeRand, ((sizeRand + (brutality / inverseDifficulty)) / 2) + 1);

		this.setSize(size);
		this.dataManager.set(BRUTALITY, brutality);

		this.maxStamina = (int) (100 * Math.min(0.75, prowess + 0.5));
		this.dataManager.set(PROWESS, prowess);
	}

	public float getSkillValue() {
		return this.dataManager.get(BRUTALITY) + this.dataManager.get(PROWESS);
	}

	private float getArmorAttackMultiplier() {
		float multiplier = 1;

		if (dataManager.get(HELMET)) {
			multiplier =- 0.25F;
		}
		if (dataManager.get(LEFT_PAULDRON)) {
			multiplier =- 0.125F;
		}
		if (dataManager.get(RIGHT_PAULDRON)) {
			multiplier =- 0.125F;
		}

		return multiplier;
	}

	@Nullable
	@Override
	public ILivingEntityData onInitialSpawn(IServerWorld worldIn, DifficultyInstance difficultyIn, SpawnReason reason, @Nullable ILivingEntityData spawnDataIn, @Nullable CompoundNBT dataTag) {
		this.setSkills(difficultyIn);
		this.setEquipmentBasedOnDifficulty(difficultyIn);
		return super.onInitialSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
	}
}
