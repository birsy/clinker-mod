package birsy.clinker.common.entity.monster;

import java.util.UUID;
import java.util.function.Predicate;

import birsy.clinker.common.entity.merchant.GnomeBratEntity;
import birsy.clinker.common.entity.merchant.GnomeEntity;
import birsy.clinker.core.registry.ClinkerEntities;
import net.minecraft.block.BlockState;
import net.minecraft.entity.AgeableEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.IAngerable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.FollowParentGoal;
import net.minecraft.entity.ai.goal.HurtByTargetGoal;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.LookRandomlyGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.ai.goal.PanicGoal;
import net.minecraft.entity.ai.goal.RandomWalkingGoal;
import net.minecraft.entity.ai.goal.ResetAngerGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.FoxEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class HyenaEntity extends AnimalEntity implements IAngerable
{
	@SuppressWarnings("unused")
	private static final DataParameter<Boolean> TAIL_SHAKING = EntityDataManager.createKey(HyenaEntity.class, DataSerializers.BOOLEAN);
	private static final DataParameter<Boolean> IS_BITING = EntityDataManager.createKey(HyenaEntity.class, DataSerializers.BOOLEAN);
	private int warningSoundTicks;
	
	public HyenaEntity(EntityType<? extends AnimalEntity> type, World worldIn) {
		super(type, worldIn);
	}
	
	public boolean canBeLeashedTo(PlayerEntity player) {
		return this.isChild() ? this.getLeashed() : !this.getLeashed();
	}
	
	@Override
	protected void registerGoals()
	{
		super.registerGoals();
		this.goalSelector.addGoal(0, new SwimGoal(this));
		this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.0D, true));
		this.goalSelector.addGoal(1, new PanicGoal(this, 1.1D));
	    this.goalSelector.addGoal(5, new FollowParentGoal(this, 1.25D));
	    this.goalSelector.addGoal(4, new RandomWalkingGoal(this, 1.0D));
	    this.goalSelector.addGoal(6, new LookAtGoal(this, PlayerEntity.class, 6.0F));
	    this.goalSelector.addGoal(7, new LookRandomlyGoal(this));
	    this.targetSelector.addGoal(2, new HurtByTargetGoal(this).setCallsForHelp(HyenaEntity.class));
	    this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, GnomeEntity.class, true));
	    this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, GnomeBratEntity.class, true));
	    this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, PlayerEntity.class, 10, true, false, this::func_233680_b_));
	    this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, FoxEntity.class, 10, true, true, (Predicate<LivingEntity>)null));
	    this.targetSelector.addGoal(5, new ResetAngerGoal<>(this, false));
	}
	
	//func_233666_p_ --> registerAttributes
	public static AttributeModifierMap.MutableAttribute setCustomAttributes()
	{
		return MobEntity.func_233666_p_()
				.createMutableAttribute(Attributes.MAX_HEALTH, 17.0)
				.createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.25D)
				.createMutableAttribute(Attributes.ATTACK_DAMAGE, 3.0D);
	}
	
	//Sounds
	protected SoundEvent getAmbientSound() {
		return this.isChild() ? SoundEvents.ENTITY_WOLF_GROWL : SoundEvents.ENTITY_WOLF_GROWL;
	}
	
	protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
		return SoundEvents.ENTITY_WOLF_AMBIENT;
	}
	
	protected SoundEvent getDeathSound() {
		return SoundEvents.ENTITY_WOLF_DEATH;
	}
	
	protected void playStepSound(BlockPos pos, BlockState blockIn) {
		this.playSound(SoundEvents.ENTITY_POLAR_BEAR_STEP, 0.15F, 1.0F);
	}
	
	protected void playWarningSound() {
		if (this.warningSoundTicks <= 0) {
			this.playSound(SoundEvents.ENTITY_POLAR_BEAR_WARNING, 1.0F, this.getSoundPitch());
			this.warningSoundTicks = 40;
		}	
	}
	
	class AttackGoal extends MeleeAttackGoal {
		public AttackGoal() {
			super(HyenaEntity.this, 1.0D, true);
		}

		protected double getAttackReachSqr(LivingEntity attackTarget) {
			float f = HyenaEntity.this.getWidth() - 0.1F;
			return (double)(f * 2.0F * f * 2.0F + attackTarget.getWidth());
	      }
	}

	/**
	* Returns the volume for the sounds this mob makes.
	*/
	protected float getSoundVolume() {
		return 0.4F;
	}
	
	protected void registerData() {
		super.registerData();
		this.dataManager.register(IS_BITING, false);
	}

	@Override
	public int getAngerTime() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setAngerTime(int time) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public UUID getAngerTarget() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setAngerTarget(UUID target) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void func_230258_H__() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public AgeableEntity func_241840_a(ServerWorld p_241840_1_, AgeableEntity p_241840_2_) {
		return ClinkerEntities.HYENA.get().create(this.world);
	}

}
