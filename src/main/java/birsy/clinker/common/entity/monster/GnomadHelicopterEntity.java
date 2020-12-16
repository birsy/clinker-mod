package birsy.clinker.common.entity.monster;

import java.util.EnumSet;

import javax.annotation.Nullable;

import birsy.clinker.core.registry.ClinkerItems;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.controller.MovementController;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
//import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.ai.goal.RangedBowAttackGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
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
import net.minecraft.pathfinding.GroundPathNavigator;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class GnomadHelicopterEntity extends AbstractGnomadEntity implements IRangedAttackMob
{
	private BlockPos boundOrigin;
	protected static final DataParameter<Byte> FLYING_GNOMAD_FLAGS = EntityDataManager.createKey(GnomadHelicopterEntity.class, DataSerializers.BYTE);
	private final RangedBowAttackGoal<GnomadHelicopterEntity> aiArrowAttack = new RangedBowAttackGoal<>(this, 1.0D, 20, 15.0F);
	private final MeleeAttackGoal aiAttackOnCollide = new MeleeAttackGoal(this, 1.2D, false) {
		/**
		 * Reset the task's internal state. Called when this task is interrupted by another one
		 */
		public void resetTask() {
			super.resetTask();
			GnomadHelicopterEntity.this.setAggroed(false);
		}

		/**
		 * Execute a one shot task or start executing a continuous task
		 */
		public void startExecuting() {
			super.startExecuting();
			GnomadHelicopterEntity.this.setAggroed(true);
		}
	};
	
	public GnomadHelicopterEntity(EntityType<? extends AbstractGnomadEntity> type, World worldIn)
	{
		super(type, worldIn);
		this.moveController = new GnomadHelicopterEntity.MoveHelperController(this);
	}
	
	public void move(MoverType typeIn, Vector3d pos) {
		super.move(typeIn, pos);
		this.doBlockCollisions();
		((GroundPathNavigator)this.getNavigator()).setBreakDoors(true);
	    this.getNavigator().setCanSwim(true);
	    this.setCanPickUpLoot(true);
	    this.setCombatTask();
	}
	
	public void tick() {
		super.tick();
		this.noClip = false;
		this.setNoGravity(true);
	}
	
	protected void registerGoals() {
		super.registerGoals();
		this.goalSelector.addGoal(0, new SwimGoal(this));
		this.goalSelector.addGoal(4, new GnomadHelicopterEntity.ChargeAttackGoal());
		this.goalSelector.addGoal(8, new GnomadHelicopterEntity.MoveRandomGoal());
		this.goalSelector.addGoal(9, new LookAtGoal(this, PlayerEntity.class, 3.0F, 1.0F));
		this.goalSelector.addGoal(10, new LookAtGoal(this, MobEntity.class, 8.0F));
		//this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, PlayerEntity.class, true));
	}
	
	public static AttributeModifierMap.MutableAttribute setCustomAttributes()
	{
		return MobEntity.func_233666_p_()
				.createMutableAttribute(Attributes.MAX_HEALTH, 25.0F)
				.createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.3F)
				.createMutableAttribute(Attributes.ATTACK_DAMAGE, 3.0D);
	}
	
	//Sounds
	protected SoundEvent getAmbientSound() {
		return SoundEvents.ENTITY_HUSK_AMBIENT;
	}

	protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
		return SoundEvents.ENTITY_VINDICATOR_HURT;
	}

	protected SoundEvent getDeathSound() {
		return SoundEvents.ENTITY_VINDICATOR_DEATH;
	}

	protected void playStepSound(BlockPos pos, BlockState blockIn) {
		this.playSound(SoundEvents.ENTITY_HUSK_STEP, 0.15F, 1.0F);
	}
	
	protected boolean makeFlySound()
	{
		return true;
	}
	
	public void setCombatTask() {
		if (this.world != null && !this.world.isRemote) {
			this.goalSelector.removeGoal(this.aiAttackOnCollide);
			this.goalSelector.removeGoal(this.aiArrowAttack);
			ItemStack itemstack = this.getHeldItem(ProjectileHelper.getHandWith(this, Items.BOW));
			if (itemstack.getItem() instanceof net.minecraft.item.BowItem) {
				int i = 20;
				if (this.world.getDifficulty() != Difficulty.HARD) {
					i = 40;
				}

				this.aiArrowAttack.setAttackCooldown(i);
				this.goalSelector.addGoal(4, this.aiArrowAttack);
			} else {
				this.goalSelector.addGoal(4, this.aiAttackOnCollide);
			}				
		}
	}
	
	@OnlyIn(Dist.CLIENT)
	public AbstractGnomadEntity.ArmPose getArmPose() {
		if (this.isAggressive() && this.getHeldItemMainhand().getItem() instanceof net.minecraft.item.BowItem) {
			return AbstractGnomadEntity.ArmPose.BOW_AND_ARROW;
		} else if (this.isAggressive()) {
			return AbstractGnomadEntity.ArmPose.ATTACKING;
	    } else {
			return AbstractGnomadEntity.ArmPose.NEUTRAL;
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

	/**
	 * (abstract) Protected helper method to read subclass entity data from NBT.
	 */
	public void readAdditional(CompoundNBT compound) {
		super.readAdditional(compound);
		this.setCombatTask();
	}

	public void setItemStackToSlot(EquipmentSlotType slotIn, ItemStack stack) {
		super.setItemStackToSlot(slotIn, stack);
		if (!this.world.isRemote) {
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
		int i = this.dataManager.get(FLYING_GNOMAD_FLAGS);
		return (i & mask) != 0;
	}
	
	private void setFlyingGnomadFlag(int mask, boolean value) {
		int i = this.dataManager.get(FLYING_GNOMAD_FLAGS);
		if (value) {
			i = i | mask;
		} else {
			i = i & ~mask;
		}
		
		this.dataManager.set(FLYING_GNOMAD_FLAGS, (byte)(i & 255));
	}
	
	public boolean isCharging() {
		return this.getFlyingGnomadFlag(1);
	}
	
	public void setCharging(boolean charging) {
		this.setFlyingGnomadFlag(1, charging);
	}
	
	protected void setEquipmentBasedOnDifficulty(DifficultyInstance difficulty) {
		this.setItemStackToSlot(EquipmentSlotType.MAINHAND, new ItemStack(ClinkerItems.LEAD_SWORD.get()));
		this.setDropChance(EquipmentSlotType.MAINHAND, 0.0F);
	}
	
	class ChargeAttackGoal extends Goal {
		public ChargeAttackGoal() {
			this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE));
		}
		
		/**
		 * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
		 * method as well.
		 */
		public boolean shouldExecute() {
			if (GnomadHelicopterEntity.this.getAttackTarget() != null && !GnomadHelicopterEntity.this.getMoveHelper().isUpdating() && GnomadHelicopterEntity.this.rand.nextInt(7) == 0) {
				return GnomadHelicopterEntity.this.getDistanceSq(GnomadHelicopterEntity.this.getAttackTarget()) > 4.0D;
			} else {
				return false;
			}
		}
		
		/**
		 * Returns whether an in-progress EntityAIBase should continue executing
		 */
		public boolean shouldContinueExecuting() {
			return GnomadHelicopterEntity.this.getMoveHelper().isUpdating() && GnomadHelicopterEntity.this.isCharging() && GnomadHelicopterEntity.this.getAttackTarget() != null && GnomadHelicopterEntity.this.getAttackTarget().isAlive();
		}

		/**
		 * Execute a one shot task or start executing a continuous task
		 */
		public void startExecuting() {
			LivingEntity livingentity = GnomadHelicopterEntity.this.getAttackTarget();
			Vector3d vector3d = livingentity.getEyePosition(1.0F);
			GnomadHelicopterEntity.this.moveController.setMoveTo(vector3d.x, vector3d.y, vector3d.z, 1.0D);
			GnomadHelicopterEntity.this.setCharging(true);
			GnomadHelicopterEntity.this.playSound(SoundEvents.ENTITY_VEX_CHARGE, 1.0F, 1.0F);
		}

		/**
		 * Reset the task's internal state. Called when this task is interrupted by another one
		 */
		public void resetTask() {
			GnomadHelicopterEntity.this.setCharging(false);
		}

		/**
		 * Keep ticking a continuous task that has already been started
		 */
		public void tick() {
			LivingEntity livingentity = GnomadHelicopterEntity.this.getAttackTarget();
			if (GnomadHelicopterEntity.this.getBoundingBox().intersects(livingentity.getBoundingBox())) {
				GnomadHelicopterEntity.this.attackEntityAsMob(livingentity);
				GnomadHelicopterEntity.this.setCharging(false);
			} else {
				double d0 = GnomadHelicopterEntity.this.getDistanceSq(livingentity);
				if (d0 < 9.0D) {
					Vector3d vector3d = livingentity.getEyePosition(1.0F);
					GnomadHelicopterEntity.this.moveController.setMoveTo(vector3d.x, vector3d.y, vector3d.z, 1.0D);
				}
			}
		}
	}
	
	class MoveHelperController extends MovementController {
		public MoveHelperController(GnomadHelicopterEntity vex) {
			super(vex);
		}
		
		public void tick() {
			if (this.action == MovementController.Action.MOVE_TO) {
				Vector3d vector3d = new Vector3d(this.posX - GnomadHelicopterEntity.this.getPosX(), this.posY - GnomadHelicopterEntity.this.getPosY(), this.posZ - GnomadHelicopterEntity.this.getPosZ());
				double d0 = vector3d.length();
				if (d0 < GnomadHelicopterEntity.this.getBoundingBox().getAverageEdgeLength()) {
					this.action = MovementController.Action.WAIT;
					GnomadHelicopterEntity.this.setMotion(GnomadHelicopterEntity.this.getMotion().scale(0.5D));
				} else {
					GnomadHelicopterEntity.this.setMotion(GnomadHelicopterEntity.this.getMotion().add(vector3d.scale(this.speed * 0.05D / d0)));
					if (GnomadHelicopterEntity.this.getAttackTarget() == null) {
						Vector3d vector3d1 = GnomadHelicopterEntity.this.getMotion();
						GnomadHelicopterEntity.this.rotationYaw = -((float)MathHelper.atan2(vector3d1.x, vector3d1.z)) * (180F / (float)Math.PI);
						GnomadHelicopterEntity.this.renderYawOffset = GnomadHelicopterEntity.this.rotationYaw;
					} else {
						double d2 = GnomadHelicopterEntity.this.getAttackTarget().getPosX() - GnomadHelicopterEntity.this.getPosX();
						double d1 = GnomadHelicopterEntity.this.getAttackTarget().getPosZ() - GnomadHelicopterEntity.this.getPosZ();
						GnomadHelicopterEntity.this.rotationYaw = -((float)MathHelper.atan2(d2, d1)) * (180F / (float)Math.PI);
						GnomadHelicopterEntity.this.renderYawOffset = GnomadHelicopterEntity.this.rotationYaw;
					}
				}				
			}
		}
	}

	class MoveRandomGoal extends Goal {
		public MoveRandomGoal() {
			this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE));
		}
		
		/**
		 * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
		 * method as well.
		 */
		public boolean shouldExecute() {
			return !GnomadHelicopterEntity.this.getMoveHelper().isUpdating() && GnomadHelicopterEntity.this.rand.nextInt(7) == 0;
		}

		/**
		 * Returns whether an in-progress EntityAIBase should continue executing
		 */
		public boolean shouldContinueExecuting() {
			return false;
		}

		/**
		 * Keep ticking a continuous task that has already been started
		 */
		public void tick() {
			BlockPos blockpos = GnomadHelicopterEntity.this.getBoundOrigin();
			if (blockpos == null) {
				blockpos = GnomadHelicopterEntity.this.getPosition();
			}

			for(int i = 0; i < 3; ++i) {
				BlockPos blockpos1 = blockpos.add(GnomadHelicopterEntity.this.rand.nextInt(15) - 7, GnomadHelicopterEntity.this.rand.nextInt(11) - 5, GnomadHelicopterEntity.this.rand.nextInt(15) - 7);
				if (GnomadHelicopterEntity.this.world.isAirBlock(blockpos1)) {
					GnomadHelicopterEntity.this.moveController.setMoveTo((double)blockpos1.getX() + 0.5D, (double)blockpos1.getY() + 0.5D, (double)blockpos1.getZ() + 0.5D, 0.25D);
					if (GnomadHelicopterEntity.this.getAttackTarget() == null) {
						GnomadHelicopterEntity.this.getLookController().setLookPosition((double)blockpos1.getX() + 0.5D, (double)blockpos1.getY() + 0.5D, (double)blockpos1.getZ() + 0.5D, 180.0F, 20.0F);
					}
					break;
				}
			}	
		}
	}
}
