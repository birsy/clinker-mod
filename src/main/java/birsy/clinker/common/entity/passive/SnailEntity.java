package birsy.clinker.common.entity.passive;

import javax.annotation.Nullable;

import birsy.clinker.core.registry.ClinkerEntities;
import net.minecraft.block.BlockState;
import net.minecraft.entity.AgeableEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.FollowParentGoal;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.LookRandomlyGoal;
import net.minecraft.entity.ai.goal.PanicGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.WaterAvoidingRandomWalkingGoal;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class SnailEntity extends AnimalEntity
{
	public int ticksClosed;
	public boolean closed;
	public boolean gary;
	
	public SnailEntity(EntityType<? extends AnimalEntity> type, World worldIn) {
		super(type, worldIn);
	}
	
	@Override
	protected void registerGoals()
	{
		super.registerGoals();
		this.goalSelector.addGoal(0, new SwimGoal(this));
	    this.goalSelector.addGoal(1, new PanicGoal(this, 1.1D));
	    this.goalSelector.addGoal(4, new FollowParentGoal(this, 1.25D));
	    this.goalSelector.addGoal(5, new WaterAvoidingRandomWalkingGoal(this, 1.0D));
	    this.goalSelector.addGoal(6, new LookAtGoal(this, PlayerEntity.class, 6.0F));
	    this.goalSelector.addGoal(7, new LookRandomlyGoal(this));
	}
	
	//func_233666_p_ --> registerAttributes
	public static AttributeModifierMap.MutableAttribute setCustomAttributes()
	{
		return MobEntity.func_233666_p_()
				.createMutableAttribute(Attributes.MAX_HEALTH, 12.0)
				.createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.05D);
	}
	
	private boolean isHidden() {
		return this.closed;
	}
	
	public void writeAdditional(CompoundNBT compound) {
		super.writeAdditional(compound);
		if (this.gary) {
			compound.putBoolean("Gary", true);
		}
	}
	
	public void readAdditional(CompoundNBT compound) {
		super.readAdditional(compound);
		if (compound.contains("Gary", 99)) {
			this.gary = compound.getBoolean("Gary");
		}
	}
	
	public void setCustomName(@Nullable ITextComponent name) {
		super.setCustomName(name);
		if (!this.gary && name != null && name.getString().equals("Gary")) {
			this.gary = true;
		}
	}
	
	//Sounds
	protected SoundEvent getAmbientSound() {
		return SoundEvents.ENTITY_GUARDIAN_AMBIENT_LAND;
	}

	protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
		return this.isHidden() ? SoundEvents.ENTITY_SHULKER_HURT_CLOSED : SoundEvents.ENTITY_GUARDIAN_FLOP;
	}

	protected SoundEvent getDeathSound() {
		return SoundEvents.ENTITY_GUARDIAN_DEATH_LAND;
	}

	protected void playStepSound(BlockPos pos, BlockState blockIn) {
		this.playSound(SoundEvents.ENTITY_SPIDER_STEP, 0.15F, 1.0F);
	}

	/**
	* Returns the volume for the sounds this mob makes.
	*/
	protected float getSoundVolume() {
		return 0.4F;
	}

	@Override
	public AgeableEntity createChild(ServerWorld p_241840_1_, AgeableEntity p_241840_2_) {
		return ClinkerEntities.SNAIL.get().create(this.world);
	}
}
