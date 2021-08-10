package birsy.clinker.common.entity.monster;

import java.util.UUID;
import java.util.function.Predicate;

import birsy.clinker.common.entity.merchant.GnomeBratEntity;
import birsy.clinker.common.entity.merchant.GnomeEntity;
import birsy.clinker.core.registry.ClinkerEntities;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.AgableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FollowParentGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.ResetUniversalAngerTargetGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Fox;
import net.minecraft.world.entity.player.Player;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;

public class HyenaEntity extends Animal implements NeutralMob
{
	@SuppressWarnings("unused")
	private static final EntityDataAccessor<Boolean> TAIL_SHAKING = SynchedEntityData.defineId(HyenaEntity.class, EntityDataSerializers.BOOLEAN);
	private static final EntityDataAccessor<Boolean> IS_BITING = SynchedEntityData.defineId(HyenaEntity.class, EntityDataSerializers.BOOLEAN);
	private int warningSoundTicks;
	
	public HyenaEntity(EntityType<? extends Animal> type, Level worldIn) {
		super(type, worldIn);
	}
	
	public boolean canBeLeashed(Player player) {
		return this.isBaby() ? this.isLeashed() : !this.isLeashed();
	}
	
	@Override
	protected void registerGoals()
	{
		super.registerGoals();
		this.goalSelector.addGoal(0, new FloatGoal(this));
		this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.0D, true));
		this.goalSelector.addGoal(1, new PanicGoal(this, 1.1D));
	    this.goalSelector.addGoal(5, new FollowParentGoal(this, 1.25D));
	    this.goalSelector.addGoal(4, new RandomStrollGoal(this, 1.0D));
	    this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 6.0F));
	    this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));
	    this.targetSelector.addGoal(2, new HurtByTargetGoal(this).setAlertOthers(HyenaEntity.class));
	    this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, GnomeEntity.class, true));
	    this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, GnomeBratEntity.class, true));
	    this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Player.class, 10, true, false, this::isAngryAt));
	    this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, Fox.class, 10, true, true, (Predicate<LivingEntity>)null));
	    this.targetSelector.addGoal(5, new ResetUniversalAngerTargetGoal<>(this, false));
	}
	
	//createMobAttributes --> registerAttributes
	public static AttributeSupplier.Builder setCustomAttributes()
	{
		return Mob.createMobAttributes()
				.add(Attributes.MAX_HEALTH, 17.0)
				.add(Attributes.MOVEMENT_SPEED, 0.25D)
				.add(Attributes.ATTACK_DAMAGE, 3.0D);
	}
	
	//Sounds
	protected SoundEvent getAmbientSound() {
		return this.isBaby() ? SoundEvents.WOLF_GROWL : SoundEvents.WOLF_GROWL;
	}
	
	protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
		return SoundEvents.WOLF_AMBIENT;
	}
	
	protected SoundEvent getDeathSound() {
		return SoundEvents.WOLF_DEATH;
	}
	
	protected void playStepSound(BlockPos pos, BlockState blockIn) {
		this.playSound(SoundEvents.POLAR_BEAR_STEP, 0.15F, 1.0F);
	}
	
	protected void playWarningSound() {
		if (this.warningSoundTicks <= 0) {
			this.playSound(SoundEvents.POLAR_BEAR_WARNING, 1.0F, this.getVoicePitch());
			this.warningSoundTicks = 40;
		}	
	}
	
	class AttackGoal extends MeleeAttackGoal {
		public AttackGoal() {
			super(HyenaEntity.this, 1.0D, true);
		}

		protected double getAttackReachSqr(LivingEntity attackTarget) {
			float f = HyenaEntity.this.getBbWidth() - 0.1F;
			return (double)(f * 2.0F * f * 2.0F + attackTarget.getBbWidth());
	      }
	}

	/**
	* Returns the volume for the sounds this mob makes.
	*/
	protected float getSoundVolume() {
		return 0.4F;
	}
	
	protected void defineSynchedData() {
		super.defineSynchedData();
		this.entityData.define(IS_BITING, false);
	}

	@Override
	public int getRemainingPersistentAngerTime() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setRemainingPersistentAngerTime(int time) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public UUID getPersistentAngerTarget() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setPersistentAngerTarget(UUID target) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void startPersistentAngerTimer() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public AgableMob getBreedOffspring(ServerLevel p_241840_1_, AgableMob p_241840_2_) {
		return ClinkerEntities.HYENA.get().create(this.level);
	}

}
