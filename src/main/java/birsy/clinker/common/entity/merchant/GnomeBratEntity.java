package birsy.clinker.common.entity.merchant;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Predicate;

import javax.annotation.Nullable;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import birsy.clinker.core.registry.ClinkerEntities;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.AgableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.entity.IAngerable;
import net.minecraft.entity.IEquipable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.MoveTowardsTargetGoal;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;

@SuppressWarnings("unused")
public class GnomeBratEntity extends AgableMob
{	
	public GnomeBratEntity(EntityType<? extends AgableMob> type, Level worldIn)
	{
		super(type, worldIn);
	    ((GroundPathNavigation)this.getNavigation()).setCanOpenDoors(true);
	    this.getNavigation().setCanFloat(true);
	    this.setCanPickUpLoot(true);
	}
	
	@Override
	protected void registerGoals()
	{
		super.registerGoals();
		this.goalSelector.addGoal(0, new FloatGoal(this));
		this.goalSelector.addGoal(1, new PanicGoal(this, 1.1D));
	    this.goalSelector.addGoal(2, new MoveTowardsTargetGoal(this, 0.9D, 32.0F));
	    this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 1.0D));
	    this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 6.0F));
	    this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 6.0F));
	    this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
	}
	
	//createMobAttributes --> registerAttributes
	public static AttributeSupplier.Builder setCustomAttributes()
	{
		return Mob.createMobAttributes()
				.add(Attributes.MAX_HEALTH, 10.0F)
				.add(Attributes.MOVEMENT_SPEED, 0.3F)
				.add(Attributes.ATTACK_DAMAGE, 0.5D);
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

	/**
	* Returns the volume for the sounds this mob makes.
	*/
	protected float getSoundVolume() {
		return 0.4F;
	}

	@Override
	public AgableMob getBreedOffspring(ServerLevel p_241840_1_, AgableMob p_241840_2_) {
		return null;
	}
}
