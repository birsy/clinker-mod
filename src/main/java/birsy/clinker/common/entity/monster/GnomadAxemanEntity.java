package birsy.clinker.common.entity.monster;

import birsy.clinker.common.entity.merchant.GnomeBratEntity;
import birsy.clinker.common.entity.merchant.GnomeEntity;
import birsy.clinker.core.registry.ClinkerItems;
import birsy.clinker.core.registry.ClinkerSounds;
import net.minecraft.command.arguments.EntityAnchorArgument;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.monster.EndermanEntity;
import net.minecraft.entity.monster.WitherSkeletonEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
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
import net.minecraft.util.IndirectEntityDamageSource;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class GnomadAxemanEntity extends AbstractGnomadEntity
{
	private boolean canCharge;
	private boolean isCharging;
	private int attackDelay;
	public int stamina;

	public static final DataParameter<Boolean> ATTACKING = EntityDataManager.createKey(GnomadAxemanEntity.class, DataSerializers.BOOLEAN);
	public static final DataParameter<Integer> WINDUP_TICKS = EntityDataManager.createKey(GnomadAxemanEntity.class, DataSerializers.VARINT);
	private boolean wasHit;

	public static final DataParameter<Integer> SHIELDS = EntityDataManager.createKey(GnomadAxemanEntity.class, DataSerializers.VARINT);
	public int shieldTransitionAnimation;
	public int shieldSizeRandomness;
	public int shieldRandomness;

	public static final DataParameter<Boolean> BUFFED = EntityDataManager.createKey(GnomadAxemanEntity.class, DataSerializers.BOOLEAN);
	private float buffLength;

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
		this.dataManager.register(SHIELDS, 0);
		this.dataManager.register(BUFFED, false);

		this.dataManager.register(DODGE_TIME, 0);
		this.dataManager.register(DODGE_DIRECTION, false);

		this.dataManager.register(WINDUP_TICKS, 0);
		this.dataManager.register(ATTACKING, false);
	}

	public void writeAdditional(CompoundNBT compound) {
		super.writeAdditional(compound);
		compound.putInt("shields", this.getShieldNumber());
		compound.putBoolean("buffed", this.isBuffed());

		compound.putInt("dodge_time", this.getDodgeTime());
		compound.putBoolean("dodge_direction", this.getDodgeDirection());

		compound.putInt("windup_ticks", this.getWindupTicks());
		compound.putBoolean("attacking", this.isAttacking());
	}

	public void readAdditional(CompoundNBT compound) {
		super.readAdditional(compound);
		this.dataManager.set(SHIELDS, compound.getInt("shields"));
		this.dataManager.set(BUFFED, compound.getBoolean("buffed"));

		this.dataManager.set(DODGE_TIME, compound.getInt("dodge_time"));
		this.dataManager.set(DODGE_DIRECTION, compound.getBoolean("dodge_direction"));

		this.dataManager.set(WINDUP_TICKS, compound.getInt("windup_ticks"));
		this.dataManager.set(ATTACKING, compound.getBoolean("attacking"));
	}
	
	@Override
	public void tick() {
		super.tick();
		if (stamina < 100) {
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

		this.attackDelay++;
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
		this.dataManager.set(BUFFED, buffed);
		if (buffed) {
			this.playSound(SoundEvents.BLOCK_BEACON_ACTIVATE, 1.0F, 1.0F);
		} else {
			this.playSound(SoundEvents.BLOCK_BEACON_DEACTIVATE, 1.0F, 1.0F);
		}
	}

	/**
	 * Makes the entity dodge to the left or right. Might make them dodge off cliffs, but that will be kinda funny.
	 * Can't be done below 20 stamina. Takes 5 stamina to use.
	 */
	public void sideStepDodge() {
		if (stamina >= 20 && getDodgeTime() <= 0) {
			this.setDodgeDirection(this.rand.nextBoolean());
			this.setDodgeTime(15);
			this.stamina =- 5;

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
					return d1 > 1.0D - 0.15D / d0 && this.getAttackTarget().canEntityBeSeen(this) && !this.isCharging();
				} else {
					return d1 > 1.0D - 0.15D / d0 && this.getAttackTarget().canEntityBeSeen(this) && !this.isCharging() && this.rand.nextInt(3) == 0;
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
		if (source instanceof IndirectEntityDamageSource && getDodgeTime() > 10) {
			return false;
		} else {
			return super.attackEntityFrom(source, amount);
		}
	}

	private static class ChargedAttackGoal extends Goal {
		private final GnomadAxemanEntity gnomad;

		private final int attackTime;
		private final int damageFrame;
		private final double damage;
		private final int attackRange;
		private final boolean interuptable;

		/**
		 * An attack that takes a little bit to execute and has special animations attached to it.
		 *
		 * @param mob The entity you want to have a charged attack.
		 * @param attackTime The time it takes for the specific attack to wind up.
		 * @param damageFrame The frame damage is dealt on.
		 * @param damage The amount of damage the attack does.
		 * @param attackRange The range of the attack.
		 * @param interuptable Determines whether the attack can be interrupted.
		 */
		public ChargedAttackGoal(GnomadAxemanEntity mob, int attackTime, int damageFrame, double damage, int attackRange, boolean interuptable) {
			this.gnomad = mob;
			this.attackTime = attackTime;
			this.damageFrame = damageFrame;
			this.damage = damage;
			this.attackRange = attackRange;
			this.interuptable = interuptable;
		}

		@Override
		public boolean shouldExecute() {
			if (gnomad.getAttackTarget() != null) {
				return gnomad.getDistanceSq(gnomad.getAttackTarget()) < attackRange && gnomad.getAttackTarget().isAlive();
			} else {
				return false;
			}
		}

		@Override
		public void startExecuting() {
			gnomad.setAttacking(true);
			gnomad.setWindupTicks(attackTime);
			super.startExecuting();
		}

		@Override
		public void tick() {
			LivingEntity livingentity = this.gnomad.getAttackTarget();

			if (gnomad.getWindupTicks() > 0) {
				gnomad.setWindupTicks(gnomad.getWindupTicks() - 1);
			} else {
				gnomad.setAttacking(false);
			}

			if (gnomad.getWindupTicks() == damageFrame && gnomad.getBoundingBox().offset(gnomad.getLookVec().normalize().mul(0.5, 0.5, 0.5)).intersects(livingentity.getBoundingBox())) {
				this.gnomad.attackEntityAsMob(livingentity);
				this.gnomad.attackDelay = 0;
			}
		}

		@Override
		public boolean shouldContinueExecuting() {
			if (gnomad.getAttackTarget() == null) {
				return false;
			} else if (!gnomad.isAttacking()) {
				return false;
			} else if (gnomad.wasHit && this.interuptable) {
				return false;
			} else {
				return gnomad.getDistanceSq(gnomad.getAttackTarget()) < attackRange && gnomad.getAttackTarget().isAlive();
			}
		}
	}

	public boolean isAttacking() {
		return this.dataManager.get(ATTACKING);
	}
	public void setAttacking(boolean attacking) {
		this.dataManager.set(ATTACKING, attacking);
	}

	public int getWindupTicks() {
		return this.dataManager.get(WINDUP_TICKS);
	}
	public void setWindupTicks(int ticks) {
		this.dataManager.set(WINDUP_TICKS, ticks);
	}

	class ChargeAttackGoal extends Goal {
		private final GnomadAxemanEntity gnomad;
		private int attackTimer;
		
		public ChargeAttackGoal(GnomadAxemanEntity mob) {
			this.gnomad = mob;
		}

		public boolean shouldExecute() {
			if(this.gnomad.isCharging() == true) {
				return true;
			}
			
			if (this.gnomad.attackDelay < 30) {
				return false;
			} else if (this.gnomad.getAttackTarget() == null) {
				return false;
			} else if (this.gnomad.getAttackTarget().isAlive() == false) {
				return false;
			} else if (this.attackTimer > 0) {
				return false;
			} else if (this.gnomad.getRNG().nextInt(120) == 0 || this.gnomad.getAttackTarget().getDistance(this.gnomad) < 1) {
				return true;
			} else {
				return false;
			}
		}

		public boolean shouldContinueExecuting() {
			if (this.gnomad.getAttackTarget() == null || this.gnomad.getAttackTarget().isAlive() == false || this.gnomad.isAlive() == false) {
				return false;
			} else if (this.gnomad.isCharging == false) {
				return false;
			} else if (this.attackTimer > 75) {
				return false;
			} else {
				return true;
			}
		}

		public void startExecuting() {
			this.gnomad.setAggroed(true);
			this.gnomad.setCharging(true);
			this.gnomad.setCanCharge(false);
			this.gnomad.playSound(SoundEvents.ENTITY_EVOKER_PREPARE_WOLOLO, 1.0F, 1.0F);
			this.gnomad.attackDelay = 0;
		}

		public void resetTask() {
			this.gnomad.setCharging(false);
			this.attackTimer = 0;
			this.gnomad.attackDelay = 0;
		}

		public void tick() {
			LivingEntity livingentity = this.gnomad.getAttackTarget();
			Vector3d vector3d = livingentity.getEyePosition(1.0F);
			this.gnomad.moveController.setMoveTo(vector3d.x, vector3d.y, vector3d.z, 1.2D);
			this.gnomad.setCharging(true);
			this.gnomad.faceEntity(livingentity, 30.0F, 30.0F);
			this.gnomad.lookAt(EntityAnchorArgument.Type.EYES, vector3d);
			this.gnomad.setArmPose(ArmPose.ATTACKING);
			this.attackTimer++;
			if (this.attackTimer > 74) {
				this.gnomad.playSound(ClinkerSounds.ENTITY_GNOME_CHAT.get(), 1.0F, 1.0F);
				this.gnomad.setCharging(false);
				this.gnomad.attackDelay = 0;
			}
			if (this.gnomad.getBoundingBox().intersects(livingentity.getBoundingBox())) {
				this.gnomad.attackEntityAsMob(livingentity);
				this.gnomad.attackDelay = 0;
				this.gnomad.setCharging(false);
			}
			
		}
	}

	public boolean isCharging() {
		return this.isCharging;
	}

	public void setCharging(boolean charging) {
		this.isCharging = charging;
	}
	
	@OnlyIn(Dist.CLIENT)
	public AbstractGnomadEntity.ArmPose getArmPose() {
		return this.armPose;
	}
	
	@OnlyIn(Dist.CLIENT)
	public void setArmPose(AbstractGnomadEntity.ArmPose pose) {
		this.armPose = pose;
	}
	
	protected void setEquipmentBasedOnDifficulty(DifficultyInstance difficulty) {
		super.setEquipmentBasedOnDifficulty(difficulty);
		if (this.rand.nextFloat() < (this.world.getDifficulty() == Difficulty.HARD ? 0.05F : 0.01F)) {
			int i = this.rand.nextInt(3);
			if (i == 0) {
				this.setItemStackToSlot(EquipmentSlotType.MAINHAND, new ItemStack(Items.BOW));
			} else {
				this.setItemStackToSlot(EquipmentSlotType.MAINHAND, new ItemStack(ClinkerItems.LEAD_SWORD.get()));
			}
		} else {
			this.setItemStackToSlot(EquipmentSlotType.MAINHAND, new ItemStack(ClinkerItems.LEAD_AXE.get()));
		}
	}

	public boolean getCanCharge() {
		return canCharge;
	}

	public void setCanCharge(boolean canCharge) {
		this.canCharge = canCharge;
	}

	@Override
	protected boolean makeFlySound() {
		if (isBuffed()) {
			return true;
		} else {
			return super.makeFlySound();
		}
	}
}
