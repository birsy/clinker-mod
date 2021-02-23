package birsy.clinker.common.entity.legacy;

import java.util.EnumSet;
import java.util.function.Predicate;

import birsy.clinker.common.entity.monster.AbstractGnomadEntity;
import birsy.clinker.common.entity.monster.HyenaEntity;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ICrossbowUser;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.ai.goal.RandomWalkingGoal;
import net.minecraft.entity.ai.goal.RangedBowAttackGoal;
import net.minecraft.entity.ai.goal.RangedCrossbowAttackGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.monster.EndermanEntity;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.monster.WitherSkeletonEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
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
import net.minecraft.world.Difficulty;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class GnomeEntity_doesnt_work extends MonsterEntity implements IRangedAttackMob, ICrossbowUser
{
	//Setup Data
	private static final DataParameter<Boolean> DATA_CHARGING_STATE = EntityDataManager.createKey(GnomeEntity_doesnt_work.class, DataSerializers.BOOLEAN);
	private final RangedBowAttackGoal<GnomeEntity_doesnt_work> aiArrowAttack = new RangedBowAttackGoal<>(this, 1.0D, 20, 15.0F);
	private final MeleeAttackGoal aiAttackOnCollide = new MeleeAttackGoal(this, 1.2D, false) {
		/**
		 * Reset the task's internal state. Called when this task is interrupted by another one
		 */
		public void resetTask() {
			super.resetTask();
			GnomeEntity_doesnt_work.this.setAggroed(false);
		}

		/**
		 * Execute a one shot task or start executing a continuous task
		 */
		public void startExecuting() {
			super.startExecuting();
			GnomeEntity_doesnt_work.this.setAggroed(true);
		}
	};
	private static final Predicate<Difficulty> field_213681_b = (p_213678_0_) -> {
		return p_213678_0_ == Difficulty.NORMAL || p_213678_0_ == Difficulty.HARD;
	};
	
	
	
	//Attributes
	public GnomeEntity_doesnt_work(EntityType<? extends MonsterEntity> type, World worldIn) {
		super(type, worldIn);
	    ((GroundPathNavigator)this.getNavigator()).setBreakDoors(true);
	    this.getNavigator().setCanSwim(true);
	    this.setCanPickUpLoot(true);
	    this.setCombatTask();
	}
	protected void registerGoals() {
		super.registerGoals();
		this.goalSelector.addGoal(0, new SwimGoal(this));
		this.goalSelector.addGoal(1, new GnomeEntity_doesnt_work.BreakDoorGoal(this));
		this.goalSelector.addGoal(4, new GnomeEntity_doesnt_work.AttackGoal(this));
		this.goalSelector.addGoal(5, new RangedCrossbowAttackGoal<>(this, 1.0D, 8.0F));
		this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, AbstractGnomadEntity.class, true));
		this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, HyenaEntity.class, true));
		this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, WitherSkeletonEntity.class, true));
		this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, EndermanEntity.class, true));
		this.goalSelector.addGoal(8, new RandomWalkingGoal(this, 0.6D));
		this.goalSelector.addGoal(9, new LookAtGoal(this, GnomeEntity_doesnt_work.class, 3.0F, 1.0F));
		this.goalSelector.addGoal(10, new LookAtGoal(this, AbstractGnomadEntity.class, 3.0F, 1.0F));
		this.goalSelector.addGoal(12, new LookAtGoal(this, MobEntity.class, 8.0F));
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
	protected float getSoundVolume() {
		return 0.4F;
	}

	
	
	//Custom AI Goals
	static class BreakDoorGoal extends net.minecraft.entity.ai.goal.BreakDoorGoal {
		public BreakDoorGoal(MobEntity p_i50578_1_) {
			super(p_i50578_1_, 6, GnomeEntity_doesnt_work.field_213681_b);
			this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE));
		}
	}
	static class AttackGoal extends net.minecraft.entity.ai.goal.MeleeAttackGoal {
		public AttackGoal(GnomeEntity_doesnt_work p_i50577_2_) {
			super(p_i50577_2_, 1.0D, false);
		}
	}
	
	
	
	//Bow stuff
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
	public void attackEntityWithRangedAttack(LivingEntity target, float distanceFactor) {
		if (this.getHeldItemMainhand().getItem() instanceof net.minecraft.item.CrossbowItem) {
			this.func_234281_b_(this, 1.6F);
		} else {
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
	
	
	
	//Crossbow Stuff
	@OnlyIn(Dist.CLIENT)
	public boolean isCharging() {
		return this.dataManager.get(DATA_CHARGING_STATE);
	}
	public void setCharging(boolean isCharging) {
		this.dataManager.set(DATA_CHARGING_STATE, isCharging);
	}
	@Override
	public void func_230284_a_(LivingEntity p_230284_1_, ItemStack p_230284_2_, ProjectileEntity p_230284_3_, float p_230284_4_) {
	      this.func_234279_a_(this, p_230284_1_, p_230284_3_, p_230284_4_, 1.6F);
	}
	@Override
	public void func_230283_U__() {
		this.idleTime = 0;
	}
	protected void registerData() {
		super.registerData();
		this.dataManager.register(DATA_CHARGING_STATE, false);
	}

	
	
	//Arm Pose Management
	@OnlyIn(Dist.CLIENT)
	public ArmPose getArmPose() {
		if (this.isCharging()) {
			return ArmPose.CROSSBOW_CHARGE;
		} else if (this.getHeldItemMainhand().getItem() instanceof net.minecraft.item.CrossbowItem) {
			return ArmPose.CROSSBOW_HOLD;
		} else if (this.isAggressive() && this.getHeldItemMainhand().getItem() instanceof net.minecraft.item.BowItem) {
			return ArmPose.BOW_AND_ARROW;
		} else if (this.isAggressive()) {
			return ArmPose.ATTACKING;
	    } else {
			return ArmPose.NEUTRAL;
		}
	}
	@OnlyIn(Dist.CLIENT)
	public static enum ArmPose {
		NEUTRAL,
		ATTACKING,
		SPELLCASTING,
		BOW_AND_ARROW,
		CROSSBOW_HOLD,
		CROSSBOW_CHARGE,
		CELEBRATING,
		SHAKING;
	}
}
