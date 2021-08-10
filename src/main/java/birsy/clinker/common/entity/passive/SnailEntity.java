package birsy.clinker.common.entity.passive;

import javax.annotation.Nullable;

import birsy.clinker.core.registry.ClinkerEntities;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.AgableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FollowParentGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;

public class SnailEntity extends Animal
{
	public int ticksClosed;
	public boolean closed;
	public boolean gary;
	
	public SnailEntity(EntityType<? extends Animal> type, Level worldIn) {
		super(type, worldIn);
	}
	
	@Override
	protected void registerGoals()
	{
		super.registerGoals();
		this.goalSelector.addGoal(0, new FloatGoal(this));
	    this.goalSelector.addGoal(1, new PanicGoal(this, 1.1D));
	    this.goalSelector.addGoal(4, new FollowParentGoal(this, 1.25D));
	    this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 1.0D));
	    this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 6.0F));
	    this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));
	}
	
	//createMobAttributes --> registerAttributes
	public static AttributeSupplier.Builder setCustomAttributes()
	{
		return Mob.createMobAttributes()
				.add(Attributes.MAX_HEALTH, 12.0)
				.add(Attributes.MOVEMENT_SPEED, 0.05D);
	}
	
	private boolean isHidden() {
		return this.closed;
	}
	
	public void addAdditionalSaveData(CompoundTag compound) {
		super.addAdditionalSaveData(compound);
		if (this.gary) {
			compound.putBoolean("Gary", true);
		}
	}
	
	public void readAdditionalSaveData(CompoundTag compound) {
		super.readAdditionalSaveData(compound);
		if (compound.contains("Gary", 99)) {
			this.gary = compound.getBoolean("Gary");
		}
	}
	
	public void setCustomName(@Nullable Component name) {
		super.setCustomName(name);
		if (!this.gary && name != null && name.getString().equals("Gary")) {
			this.gary = true;
		}
	}
	
	//Sounds
	protected SoundEvent getAmbientSound() {
		return SoundEvents.GUARDIAN_AMBIENT_LAND;
	}

	protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
		return this.isHidden() ? SoundEvents.SHULKER_HURT_CLOSED : SoundEvents.GUARDIAN_FLOP;
	}

	protected SoundEvent getDeathSound() {
		return SoundEvents.GUARDIAN_DEATH_LAND;
	}

	protected void playStepSound(BlockPos pos, BlockState blockIn) {
		this.playSound(SoundEvents.SPIDER_STEP, 0.15F, 1.0F);
	}

	/**
	* Returns the volume for the sounds this mob makes.
	*/
	protected float getSoundVolume() {
		return 0.4F;
	}

	@Override
	public AgableMob getBreedOffspring(ServerLevel p_241840_1_, AgableMob p_241840_2_) {
		return ClinkerEntities.SNAIL.get().create(this.level);
	}
}
