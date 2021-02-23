package birsy.clinker.common.entity.monster;

import birsy.clinker.common.entity.merchant.GnomeBratEntity;
import birsy.clinker.common.entity.merchant.GnomeEntity;
import birsy.clinker.core.registry.ClinkerItems;
import birsy.clinker.core.registry.ClinkerSounds;
import net.minecraft.command.arguments.EntityAnchorArgument;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.monster.EndermanEntity;
import net.minecraft.entity.monster.WitherSkeletonEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.entity.projectile.ProjectileHelper;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.ShootableItem;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.pathfinding.GroundPathNavigator;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.EnumSet;

public class GnomadAxemanEntity extends AbstractGnomadEntity
{
	private boolean canCharge;
	private boolean isCharging;
	private int attackDelay;

	public static final DataParameter<Integer> SHIELDS = EntityDataManager.createKey(GnomadAxemanEntity.class, DataSerializers.VARINT);
	public int shieldTransitionAnimation;
	public int shieldSizeRandomness;
	public int shieldRandomness;

	public static final DataParameter<Boolean> BUFFED = EntityDataManager.createKey(GnomadAxemanEntity.class, DataSerializers.BOOLEAN);
	private float buffLength;

	
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
		//this.goalSelector.addGoal(1, new RangedBowAttackGoal<>(this, 1.0D, 20, 15.0F));
		this.goalSelector.addGoal(2, new GnomadAxemanEntity.ChargeAttackGoal(this));
		this.goalSelector.addGoal(3, new GnomadAxemanEntity.TargetStrafeGoal(this));
		this.targetSelector.addGoal(2, new HurtByTargetGoal(this).setCallsForHelp(AbstractGnomadEntity.class));
		this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, GnomeEntity.class, true));
		this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, GnomeBratEntity.class, true));
		this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, PlayerEntity.class, true));
		this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, HyenaEntity.class, true));
		this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, WitherSkeletonEntity.class, true));
		this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, EndermanEntity.class, true));
		this.goalSelector.addGoal(8, new RandomWalkingGoal(this, 0.6D));
		//this.goalSelector.addGoal(1, new AbstractGnomadEntity.SitGoal<>(this));
		this.goalSelector.addGoal(10, new LookAtGoal(this, PlayerEntity.class, 3.0F, 1.0F));
		this.goalSelector.addGoal(11, new LookAtGoal(this, GnomeEntity.class, 3.0F, 1.0F));
		this.goalSelector.addGoal(12, new LookAtGoal(this, AbstractGnomadEntity.class, 3.0F, 1.0F));
		this.goalSelector.addGoal(13, new LookAtGoal(this, MobEntity.class, 8.0F));
	}

	protected void registerData() {
		super.registerData();
		this.dataManager.register(SHIELDS, 0);
		this.dataManager.register(BUFFED, false);
	}

	public void writeAdditional(CompoundNBT compound) {
		super.writeAdditional(compound);
		compound.putInt("shields", this.getShieldNumber());
		compound.putBoolean("buffed", this.isBuffed());
	}

	public void readAdditional(CompoundNBT compound) {
		super.readAdditional(compound);
		this.dataManager.set(SHIELDS, compound.getInt("shields"));
		this.dataManager.set(BUFFED, compound.getBoolean("buffed"));
	}
	
	@Override
	public void tick() {
		super.tick();
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
	public boolean hitByEntity(Entity entityIn) {
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

	static class TargetStrafeGoal extends Goal {
		private final GnomadAxemanEntity gnomad;
		private final double moveSpeedAmp = 1.0D;
		private int attackCooldown = 20;
		private final float maxAttackDistance = 15.0F;
		private int attackTime = -1;
		private int seeTime;
		private boolean strafingClockwise;
		private boolean strafingBackwards;
		private int strafingTime = -1;

		public TargetStrafeGoal(GnomadAxemanEntity mob) {
			this.gnomad = mob;
		}

		public void setAttackCooldown(int attackCooldownIn) {
			this.attackCooldown = attackCooldownIn;
		}

		public boolean shouldExecute() {
			if (this.gnomad.getAttackTarget() == null) {
				return false;
			} else if (this.gnomad.isCharging()) {
				return false;
			} else {
				return true;
			}
		}

		public boolean shouldContinueExecuting() {
			if (this.gnomad.canCharge == true) {
				return false;
			} else  if (this.gnomad.isCharging()) {
				return false;
			} else {
				return (this.shouldExecute() || !this.gnomad.getNavigator().noPath());
			}
		}

		public void startExecuting() {
			super.startExecuting();
			this.gnomad.setAggroed(true);
		}

		
		public void resetTask() {
			super.resetTask();
			this.gnomad.setAggroed(false);
			this.seeTime = 0;
			this.attackTime = -1;
			this.gnomad.resetActiveHand();
			this.gnomad.setCanCharge(true);
		}

		public void tick() {
			LivingEntity livingentity = this.gnomad.getAttackTarget();
			
			if(this.gnomad.isActiveItemStackBlocking()) {
				this.gnomad.setCanCharge(false);
			}
			
			if (livingentity != null) {
				double d0 = this.gnomad.getDistanceSq(livingentity.getPosX(), livingentity.getPosY(), livingentity.getPosZ());
				boolean flag = this.gnomad.getEntitySenses().canSee(livingentity);
				boolean flag1 = this.seeTime > 0;
				
				if (flag != flag1) {
					this.seeTime = 0;
				}

				if (flag) {
					++this.seeTime;
				} else {
					--this.seeTime;
				}

				if (!(d0 > (double)this.maxAttackDistance) && this.seeTime >= 20) {
					this.gnomad.getNavigator().clearPath();
					++this.strafingTime;
				} else {
					this.gnomad.getNavigator().tryMoveToEntityLiving(livingentity, this.moveSpeedAmp);
					this.strafingTime = -1;
				}

				if (this.strafingTime >= 20) {
					if ((double)this.gnomad.getRNG().nextFloat() < 0.3D) {
						this.strafingClockwise = !this.strafingClockwise;
					}

					if ((double)this.gnomad.getRNG().nextFloat() < 0.3D) {
						this.strafingBackwards = !this.strafingBackwards;
						if (this.gnomad.getHeldItemMainhand().getItem() == Items.SHIELD || this.gnomad.getHeldItemOffhand().getItem() == Items.SHIELD) {
	   	        			this.gnomad.blockUsingShield(this.gnomad);
						}
					}

					this.strafingTime = 0;
				}

				if (this.strafingTime > -1) {
					if (d0 > (double)(this.maxAttackDistance * 0.75F)) {
						this.strafingBackwards = false;
					} else if (d0 < (double)(this.maxAttackDistance * 0.25F)) {
						this.strafingBackwards = true;
					}

					this.gnomad.getMoveHelper().strafe(this.strafingBackwards ? -0.5F : 0.5F, this.strafingClockwise ? 0.5F : -0.5F);
					this.gnomad.faceEntity(livingentity, 30.0F, 30.0F);
				} else {
					this.gnomad.getLookController().setLookPositionWithEntity(livingentity, 30.0F, 30.0F);
				}

				if (this.gnomad.isHandActive()) {
		            if (!flag && this.seeTime < -60) {
		            	this.gnomad.resetActiveHand();
		   	        } else if (flag) {
		   	        	int i = this.gnomad.getItemInUseMaxCount();
		   	        	this.gnomad.setCanCharge(true);
		   	        	if (i >= 20) {
		   	        		this.gnomad.resetActiveHand();
		   	        		if (this.gnomad.getHeldItemMainhand().getItem() == Items.SHIELD || this.gnomad.getHeldItemOffhand().getItem() == Items.SHIELD) {
		   	        			this.gnomad.blockUsingShield(this.gnomad);
							}
		   	        		this.attackTime = this.attackCooldown;
		   	        	}
		   	        }
				} else if (--this.attackTime <= 0 && this.seeTime >= -60) {
					if (this.gnomad.getHeldItemMainhand().getItem() == Items.SHIELD || this.gnomad.getHeldItemOffhand().getItem() == Items.SHIELD) {
						this.gnomad.setActiveHand(ProjectileHelper.getHandWith(this.gnomad, Items.SHIELD));
						this.gnomad.blockUsingShield(this.gnomad);
						this.gnomad.setCanCharge(true);
					}
				}
			}
		}
	}
	
	class ChargeAttackGoal extends MeleeAttackGoal {
		private final GnomadAxemanEntity gnomad;
		private int attackTimer;
		
		public ChargeAttackGoal(GnomadAxemanEntity mob) {
			super(mob, 1.0D, false);
			this.gnomad = mob;
			this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE));
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

	public void attackEntityWithRangedAttack(LivingEntity target, float distanceFactor) {
		ItemStack itemstack = this.findAmmo(this.getHeldItem(ProjectileHelper.getHandWith(this, Items.BOW)));
		AbstractArrowEntity abstractarrowentity = this.fireArrow(itemstack, distanceFactor);
		if (this.getHeldItemMainhand().getItem() instanceof net.minecraft.item.BowItem)
			abstractarrowentity = ((net.minecraft.item.BowItem)this.getHeldItemMainhand().getItem()).customArrow(abstractarrowentity);
		double d0 = target.getPosX() - this.getPosX();
		double d1 = target.getPosYHeight(0.3333333333333333D) - abstractarrowentity.getPosY();
		double d2 = target.getPosZ() - this.getPosZ();
		double d3 = (double)MathHelper.sqrt(d0 * d0 + d2 * d2);
		abstractarrowentity.shoot(d0, d1 + d3 * (double)0.2F, d2, 1.6F, (float)(14 - this.world.getDifficulty().getId() * 4));
		this.playSound(SoundEvents.ENTITY_SKELETON_SHOOT, 1.0F, 1.0F / (this.getRNG().nextFloat() * 0.4F + 0.8F));
		this.world.addEntity(abstractarrowentity);
	}

	/**
	 * Fires an arrow
	 */
	protected AbstractArrowEntity fireArrow(ItemStack arrowStack, float distanceFactor) {
		return ProjectileHelper.fireArrow(this, arrowStack, distanceFactor);
	}

	public boolean func_230280_a_(ShootableItem p_230280_1_) {
		return p_230280_1_ == Items.BOW;
	}

	public void setItemStackToSlot(EquipmentSlotType slotIn, ItemStack stack) {
		super.setItemStackToSlot(slotIn, stack);
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
