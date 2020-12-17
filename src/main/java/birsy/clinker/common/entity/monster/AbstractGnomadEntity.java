package birsy.clinker.common.entity.monster;

import java.util.EnumSet;

import birsy.clinker.core.registry.ClinkerSounds;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public abstract class AbstractGnomadEntity extends MonsterEntity
{
	public boolean isSitting;
	public int gnomadRank = 0;
	
	protected AbstractGnomadEntity(EntityType<? extends AbstractGnomadEntity> type, World worldIn)
	{
		super(type, worldIn);
	}
	
	public static AttributeModifierMap.MutableAttribute setCustomAttributes()
	{
		return MobEntity.func_233666_p_()
				.createMutableAttribute(Attributes.MAX_HEALTH, 25.0F)
				.createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.3F)
				.createMutableAttribute(Attributes.ATTACK_DAMAGE, 3.0D);
	}
	

	protected void registerGoals()
	{
		super.registerGoals();
	}
	
	/**
	 *
	class FollowSquadronGoal<T extends AbstractGnomadEntity> extends Goal {
		protected final Class<T> classToFollow;
		private final AbstractGnomadEntity gnomad;
		
		@Override
		public boolean shouldExecute() {
			return false;
		}
		
	}
	*/
	
	class SitGoal<T extends AbstractGnomadEntity> extends Goal {
		private final AbstractGnomadEntity gnomad;

		public SitGoal(T mob) {
			this.gnomad = mob;
			this.setMutexFlags(EnumSet.of(Goal.Flag.JUMP, Goal.Flag.MOVE));
		}
		
		public boolean shouldExecute() {
			if (this.gnomad.isInWaterOrBubbleColumn()) {
				return false;
			} else if (!this.gnomad.isOnGround()) {
				return false;
			} else {
				//return this.gnomad.getRNG().nextInt(120) == 0;
				return true;
			}
		}
		
		public void startExecuting() {
			this.gnomad.getNavigator().clearPath();
			this.gnomad.isSitting = true;
		}
		
		@Override
		public void tick() {
			this.gnomad.getNavigator().clearPath();
			super.tick();
		}
		
		public boolean shouldContinueExecuting() {
			if (this.gnomad.getAttackTarget() != null) {
				return false;
			} else if (this.gnomad.isInWaterOrBubbleColumn() || !this.gnomad.isOnGround()) {
				return false;
			} else if (this.gnomad.getRNG().nextInt(500) == 0) {
				return false;
			}
			
			return super.shouldContinueExecuting();
		}
		
		@Override
		public void resetTask() {
			this.gnomad.isSitting = false;
			super.resetTask();
		}
	}

	
	//Sounds
	@Override
	protected float getSoundVolume() {
		return 0.5F;
	}
	
	protected SoundEvent getAmbientSound() {
		if (this.isSitting == true) {
			return SoundEvents.ENTITY_VINDICATOR_CELEBRATE;
		}
		return ClinkerSounds.ENTITY_GNOME_AMBIENT.get();
	}

	protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
		return ClinkerSounds.ENTITY_GNOME_HURT.get();
	}
	protected SoundEvent getDeathSound() {
		return ClinkerSounds.ENTITY_GNOME_DEATH.get();
	}

	protected void playStepSound(BlockPos pos, BlockState blockIn) {
		this.playSound(SoundEvents.ENTITY_PIGLIN_STEP, 0.15F, 1.0F);
	}
	
	@OnlyIn(Dist.CLIENT)
	public AbstractGnomadEntity.ArmPose getArmPose()
	{
		return AbstractGnomadEntity.ArmPose.NEUTRAL;
	}

	@OnlyIn(Dist.CLIENT)
	public static enum ArmPose {
		NEUTRAL,
		ATTACKING,
		SPELLCASTING,
		BOW_AND_ARROW,
		CROSSBOW_HOLD,
		CROSSBOW_CHARGE,
		TOSSING,
		THROWN,
		CELEBRATING,
		SHAKING;
	}
}
