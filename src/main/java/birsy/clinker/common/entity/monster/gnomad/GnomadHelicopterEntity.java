package birsy.clinker.common.entity.monster.gnomad;

import java.util.EnumSet;

import javax.annotation.Nullable;

import birsy.clinker.core.registry.ClinkerItems;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
//import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.RangedBowAttackGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class GnomadHelicopterEntity extends AbstractGnomadEntity implements RangedAttackMob
{
	private BlockPos boundOrigin;
	protected static final EntityDataAccessor<Byte> FLYING_GNOMAD_FLAGS = SynchedEntityData.defineId(GnomadHelicopterEntity.class, EntityDataSerializers.BYTE);
	private final RangedBowAttackGoal<GnomadHelicopterEntity> aiArrowAttack = new RangedBowAttackGoal<>(this, 1.0D, 20, 15.0F);
	private final MeleeAttackGoal aiAttackOnCollide = new MeleeAttackGoal(this, 1.2D, false) {
		/**
		 * Reset the task's internal state. Called when this task is interrupted by another one
		 */
		public void stop() {
			super.stop();
			GnomadHelicopterEntity.this.setAggressive(false);
		}

		/**
		 * Execute a one shot task or start executing a continuous task
		 */
		public void start() {
			super.start();
			GnomadHelicopterEntity.this.setAggressive(true);
		}
	};
	
	public GnomadHelicopterEntity(EntityType<? extends AbstractGnomadEntity> type, Level worldIn)
	{
		super(type, worldIn);
		this.moveControl = new GnomadHelicopterEntity.MoveHelperController(this);
	}
	
	public void move(MoverType typeIn, Vec3 pos) {
		super.move(typeIn, pos);
		this.checkInsideBlocks();
		((GroundPathNavigation)this.getNavigation()).setCanOpenDoors(true);
	    this.getNavigation().setCanFloat(true);
	    this.setCanPickUpLoot(true);
	    this.setCombatTask();
	}
	
	public void tick() {
		super.tick();
		this.noPhysics = false;
		this.setNoGravity(true);
	}
	
	protected void registerGoals() {
		super.registerGoals();
		this.goalSelector.addGoal(0, new FloatGoal(this));
		this.goalSelector.addGoal(4, new GnomadHelicopterEntity.ChargeAttackGoal());
		this.goalSelector.addGoal(8, new GnomadHelicopterEntity.MoveRandomGoal());
		this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, Player.class, 3.0F, 1.0F));
		this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Mob.class, 8.0F));
		//this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, PlayerEntity.class, true));
	}
	
	public static AttributeSupplier.Builder setCustomAttributes()
	{
		return Mob.createMobAttributes()
				.add(Attributes.MAX_HEALTH, 25.0F)
				.add(Attributes.MOVEMENT_SPEED, 0.3F)
				.add(Attributes.ATTACK_DAMAGE, 3.0D);
	}
	
	//Sounds
	protected SoundEvent getAmbientSound() {
		return SoundEvents.HUSK_AMBIENT;
	}

	protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
		return SoundEvents.VINDICATOR_HURT;
	}

	protected SoundEvent getDeathSound() {
		return SoundEvents.VINDICATOR_DEATH;
	}

	protected void playStepSound(BlockPos pos, BlockState blockIn) {
		this.playSound(SoundEvents.HUSK_STEP, 0.15F, 1.0F);
	}
	
	protected boolean makeFlySound()
	{
		return true;
	}
	
	public void setCombatTask() {
		if (this.level != null && !this.level.isClientSide) {
			this.goalSelector.removeGoal(this.aiAttackOnCollide);
			this.goalSelector.removeGoal(this.aiArrowAttack);
			ItemStack itemstack = this.getItemInHand(ProjectileUtil.getWeaponHoldingHand(this, Items.BOW));
			if (itemstack.getItem() instanceof net.minecraft.world.item.BowItem) {
				int i = 20;
				if (this.level.getDifficulty() != Difficulty.HARD) {
					i = 40;
				}

				this.aiArrowAttack.setMinAttackInterval(i);
				this.goalSelector.addGoal(4, this.aiArrowAttack);
			} else {
				this.goalSelector.addGoal(4, this.aiAttackOnCollide);
			}				
		}
	}
	
	@OnlyIn(Dist.CLIENT)
	public AbstractGnomadEntity.ArmPose getArmPose() {
		if (this.isAggressive() && this.getMainHandItem().getItem() instanceof net.minecraft.world.item.BowItem) {
			return AbstractGnomadEntity.ArmPose.BOW_AND_ARROW;
		} else if (this.isAggressive()) {
			return AbstractGnomadEntity.ArmPose.ATTACKING;
	    } else {
			return AbstractGnomadEntity.ArmPose.NEUTRAL;
		}
	}

	public void performRangedAttack(LivingEntity target, float distanceFactor) {
		ItemStack itemstack = this.getProjectile(this.getItemInHand(ProjectileUtil.getWeaponHoldingHand(this, Items.BOW)));
		AbstractArrow abstractarrowentity = this.fireArrow(itemstack, distanceFactor);
		if (this.getMainHandItem().getItem() instanceof net.minecraft.world.item.BowItem)
			abstractarrowentity = ((net.minecraft.world.item.BowItem)this.getMainHandItem().getItem()).customArrow(abstractarrowentity);
		double d0 = target.getX() - this.getX();
		double d1 = target.getY(0.3333333333333333D) - abstractarrowentity.getY();
		double d2 = target.getZ() - this.getZ();
		double d3 = (double)Mth.sqrt(d0 * d0 + d2 * d2);
		abstractarrowentity.shoot(d0, d1 + d3 * (double)0.2F, d2, 1.6F, (float)(14 - this.level.getDifficulty().getId() * 4));
		this.playSound(SoundEvents.SKELETON_SHOOT, 1.0F, 1.0F / (this.getRandom().nextFloat() * 0.4F + 0.8F));
		this.level.addFreshEntity(abstractarrowentity);
	}

	/**
	 * Fires an arrow
	 */
	protected AbstractArrow fireArrow(ItemStack arrowStack, float distanceFactor) {
		return ProjectileUtil.getMobArrow(this, arrowStack, distanceFactor);
	}

	public boolean canFireProjectileWeapon(ProjectileWeaponItem p_230280_1_) {
		return p_230280_1_ == Items.BOW;
	}

	/**
	 * (abstract) Protected helper method to read subclass entity data from NBT.
	 */
	public void readAdditionalSaveData(CompoundTag compound) {
		super.readAdditionalSaveData(compound);
		this.setCombatTask();
	}

	public void setItemSlot(EquipmentSlot slotIn, ItemStack stack) {
		super.setItemSlot(slotIn, stack);
		if (!this.level.isClientSide) {
			this.setCombatTask();
		}
	}
	
	@Nullable
	public BlockPos getBoundOrigin() {
		return this.boundOrigin;
	}

	public void setBoundOrigin(@Nullable BlockPos boundOriginIn) {
		this.boundOrigin = boundOriginIn;
	}
	
	private boolean getFlyingGnomadFlag(int mask) {
		int i = this.entityData.get(FLYING_GNOMAD_FLAGS);
		return (i & mask) != 0;
	}
	
	private void setFlyingGnomadFlag(int mask, boolean value) {
		int i = this.entityData.get(FLYING_GNOMAD_FLAGS);
		if (value) {
			i = i | mask;
		} else {
			i = i & ~mask;
		}
		
		this.entityData.set(FLYING_GNOMAD_FLAGS, (byte)(i & 255));
	}
	
	public boolean isCharging() {
		return this.getFlyingGnomadFlag(1);
	}
	
	public void setCharging(boolean charging) {
		this.setFlyingGnomadFlag(1, charging);
	}
	
	protected void populateDefaultEquipmentSlots(DifficultyInstance difficulty) {
		this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(ClinkerItems.LEAD_SWORD.get()));
		this.setDropChance(EquipmentSlot.MAINHAND, 0.0F);
	}
	
	class ChargeAttackGoal extends Goal {
		public ChargeAttackGoal() {
			this.setFlags(EnumSet.of(Goal.Flag.MOVE));
		}
		
		/**
		 * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
		 * method as well.
		 */
		public boolean canUse() {
			if (GnomadHelicopterEntity.this.getTarget() != null && !GnomadHelicopterEntity.this.getMoveControl().hasWanted() && GnomadHelicopterEntity.this.random.nextInt(7) == 0) {
				return GnomadHelicopterEntity.this.distanceToSqr(GnomadHelicopterEntity.this.getTarget()) > 4.0D;
			} else {
				return false;
			}
		}
		
		/**
		 * Returns whether an in-progress EntityAIBase should continue executing
		 */
		public boolean canContinueToUse() {
			return GnomadHelicopterEntity.this.getMoveControl().hasWanted() && GnomadHelicopterEntity.this.isCharging() && GnomadHelicopterEntity.this.getTarget() != null && GnomadHelicopterEntity.this.getTarget().isAlive();
		}

		/**
		 * Execute a one shot task or start executing a continuous task
		 */
		public void start() {
			LivingEntity livingentity = GnomadHelicopterEntity.this.getTarget();
			Vec3 vector3d = livingentity.getEyePosition(1.0F);
			GnomadHelicopterEntity.this.moveControl.setWantedPosition(vector3d.x, vector3d.y, vector3d.z, 1.0D);
			GnomadHelicopterEntity.this.setCharging(true);
			GnomadHelicopterEntity.this.playSound(SoundEvents.VEX_CHARGE, 1.0F, 1.0F);
		}

		/**
		 * Reset the task's internal state. Called when this task is interrupted by another one
		 */
		public void stop() {
			GnomadHelicopterEntity.this.setCharging(false);
		}

		/**
		 * Keep ticking a continuous task that has already been started
		 */
		public void tick() {
			LivingEntity livingentity = GnomadHelicopterEntity.this.getTarget();
			if (GnomadHelicopterEntity.this.getBoundingBox().intersects(livingentity.getBoundingBox())) {
				GnomadHelicopterEntity.this.doHurtTarget(livingentity);
				GnomadHelicopterEntity.this.setCharging(false);
			} else {
				double d0 = GnomadHelicopterEntity.this.distanceToSqr(livingentity);
				if (d0 < 9.0D) {
					Vec3 vector3d = livingentity.getEyePosition(1.0F);
					GnomadHelicopterEntity.this.moveControl.setWantedPosition(vector3d.x, vector3d.y, vector3d.z, 1.0D);
				}
			}
		}
	}
	
	class MoveHelperController extends MoveControl {
		public MoveHelperController(GnomadHelicopterEntity vex) {
			super(vex);
		}
		
		public void tick() {
			if (this.operation == MoveControl.Operation.MOVE_TO) {
				Vec3 vector3d = new Vec3(this.wantedX - GnomadHelicopterEntity.this.getX(), this.wantedY - GnomadHelicopterEntity.this.getY(), this.wantedZ - GnomadHelicopterEntity.this.getZ());
				double d0 = vector3d.length();
				if (d0 < GnomadHelicopterEntity.this.getBoundingBox().getSize()) {
					this.operation = MoveControl.Operation.WAIT;
					GnomadHelicopterEntity.this.setDeltaMovement(GnomadHelicopterEntity.this.getDeltaMovement().scale(0.5D));
				} else {
					GnomadHelicopterEntity.this.setDeltaMovement(GnomadHelicopterEntity.this.getDeltaMovement().add(vector3d.scale(this.speedModifier * 0.05D / d0)));
					if (GnomadHelicopterEntity.this.getTarget() == null) {
						Vec3 vector3d1 = GnomadHelicopterEntity.this.getDeltaMovement();
						GnomadHelicopterEntity.this.yRot = -((float)Mth.atan2(vector3d1.x, vector3d1.z)) * (180F / (float)Math.PI);
						GnomadHelicopterEntity.this.yBodyRot = GnomadHelicopterEntity.this.yRot;
					} else {
						double d2 = GnomadHelicopterEntity.this.getTarget().getX() - GnomadHelicopterEntity.this.getX();
						double d1 = GnomadHelicopterEntity.this.getTarget().getZ() - GnomadHelicopterEntity.this.getZ();
						GnomadHelicopterEntity.this.yRot = -((float)Mth.atan2(d2, d1)) * (180F / (float)Math.PI);
						GnomadHelicopterEntity.this.yBodyRot = GnomadHelicopterEntity.this.yRot;
					}
				}				
			}
		}
	}

	class MoveRandomGoal extends Goal {
		public MoveRandomGoal() {
			this.setFlags(EnumSet.of(Goal.Flag.MOVE));
		}
		
		/**
		 * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
		 * method as well.
		 */
		public boolean canUse() {
			return !GnomadHelicopterEntity.this.getMoveControl().hasWanted() && GnomadHelicopterEntity.this.random.nextInt(7) == 0;
		}

		/**
		 * Returns whether an in-progress EntityAIBase should continue executing
		 */
		public boolean canContinueToUse() {
			return false;
		}

		/**
		 * Keep ticking a continuous task that has already been started
		 */
		public void tick() {
			BlockPos blockpos = GnomadHelicopterEntity.this.getBoundOrigin();
			if (blockpos == null) {
				blockpos = GnomadHelicopterEntity.this.blockPosition();
			}

			for(int i = 0; i < 3; ++i) {
				BlockPos blockpos1 = blockpos.offset(GnomadHelicopterEntity.this.random.nextInt(15) - 7, GnomadHelicopterEntity.this.random.nextInt(11) - 5, GnomadHelicopterEntity.this.random.nextInt(15) - 7);
				if (GnomadHelicopterEntity.this.level.isEmptyBlock(blockpos1)) {
					GnomadHelicopterEntity.this.moveControl.setWantedPosition((double)blockpos1.getX() + 0.5D, (double)blockpos1.getY() + 0.5D, (double)blockpos1.getZ() + 0.5D, 0.25D);
					if (GnomadHelicopterEntity.this.getTarget() == null) {
						GnomadHelicopterEntity.this.getLookControl().setLookAt((double)blockpos1.getX() + 0.5D, (double)blockpos1.getY() + 0.5D, (double)blockpos1.getZ() + 0.5D, 180.0F, 20.0F);
					}
					break;
				}
			}	
		}
	}
}
