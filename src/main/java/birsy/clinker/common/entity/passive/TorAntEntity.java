package birsy.clinker.common.entity.passive;

import javax.annotation.Nullable;

import birsy.clinker.core.registry.ClinkerEntities;
import net.minecraft.block.BlockState;
import net.minecraft.entity.AgeableEntity;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.Pose;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.BreedGoal;
import net.minecraft.entity.ai.goal.FollowParentGoal;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.LookRandomlyGoal;
import net.minecraft.entity.ai.goal.PanicGoal;
import net.minecraft.entity.ai.goal.RunAroundLikeCrazyGoal;
import net.minecraft.entity.ai.goal.WaterAvoidingRandomWalkingGoal;
import net.minecraft.entity.passive.horse.AbstractHorseEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.ClimberPathNavigator;
import net.minecraft.pathfinding.PathNavigator;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class TorAntEntity extends AbstractHorseEntity
{
	private static final DataParameter<Byte> CLIMBING = EntityDataManager.createKey(TorAntEntity.class, DataSerializers.BYTE);
	
	public TorAntEntity(EntityType<? extends AbstractHorseEntity> type, World worldIn) {
		super(type, worldIn);
	}
	
	@Override
	protected void registerGoals() {
		this.goalSelector.addGoal(1, new PanicGoal(this, 1.2D));
		this.goalSelector.addGoal(1, new RunAroundLikeCrazyGoal(this, 1.2D));
		this.goalSelector.addGoal(2, new BreedGoal(this, 1.0D, AbstractHorseEntity.class));
		this.goalSelector.addGoal(4, new FollowParentGoal(this, 1.0D));
		this.goalSelector.addGoal(6, new WaterAvoidingRandomWalkingGoal(this, 0.7D));
		this.goalSelector.addGoal(7, new LookAtGoal(this, PlayerEntity.class, 6.0F));
		this.goalSelector.addGoal(8, new LookRandomlyGoal(this));
		this.initExtraAI();
	}
	
	protected PathNavigator createNavigator(World worldIn) {
		return new ClimberPathNavigator(this, worldIn);
	}
	
	public static AttributeModifierMap.MutableAttribute setCustomAttributes()
	{
		return MobEntity.func_233666_p_()
				.createMutableAttribute(Attributes.MAX_HEALTH, 15.0D)
				.createMutableAttribute(Attributes.MOVEMENT_SPEED, (double)0.2F)
				.createMutableAttribute(Attributes.HORSE_JUMP_STRENGTH, 0.2F);
	}
	
	protected void registerData() {
		super.registerData();
		this.dataManager.register(CLIMBING, (byte)0);
	}
	
	protected void func_230273_eI_() {
		this.getAttribute(Attributes.HORSE_JUMP_STRENGTH).setBaseValue(this.getModifiedJumpStrength());
	}
	
	@Nullable
	public AgeableEntity createChild(AgeableEntity ageable) {
		return ClinkerEntities.TOR_ANT.get().create(this.world);
	}
	
	public ActionResultType func_230254_b_(PlayerEntity p_230254_1_, Hand p_230254_2_) {
		ItemStack itemstack = p_230254_1_.getHeldItem(p_230254_2_);
		if (!this.isTame()) {
			return ActionResultType.PASS;
		} else if (this.isChild()) {
			return super.func_230254_b_(p_230254_1_, p_230254_2_);
		} else if (p_230254_1_.isSecondaryUseActive()) {
			this.openGUI(p_230254_1_);
			return ActionResultType.func_233537_a_(this.world.isRemote);
		} else if (this.isBeingRidden()) {
			return super.func_230254_b_(p_230254_1_, p_230254_2_);
		} else {
			if (!itemstack.isEmpty()) {
				if (itemstack.getItem() == Items.SADDLE && !this.isHorseSaddled()) {
					this.openGUI(p_230254_1_);
					return ActionResultType.func_233537_a_(this.world.isRemote);
				}

				ActionResultType actionresulttype = itemstack.interactWithEntity(p_230254_1_, this, p_230254_2_);
				if (actionresulttype.isSuccessOrConsume()) {
					return actionresulttype;
				}
			}

			this.mountTo(p_230254_1_);
	         return ActionResultType.func_233537_a_(this.world.isRemote);
		}
	}
	
	
	/**
	 * CLIMBING STUFF
	 */
	
	public void tick() {
		super.tick();
		if (!this.world.isRemote) {
			this.setBesideClimbableBlock(this.collidedHorizontally);
		}
	}
	
	@Override
	public boolean isOnLadder() {
		return this.isBesideClimbableBlock();
	}
	
	public boolean isBesideClimbableBlock() {
		return (this.dataManager.get(CLIMBING) & 1) != 0;
	}
	
	public void setBesideClimbableBlock(boolean climbing) {
		byte b0 = this.dataManager.get(CLIMBING);
		if (climbing) {
			b0 = (byte)(b0 | 1);
		} else {
			b0 = (byte)(b0 & -2);
		}
		this.dataManager.set(CLIMBING, b0);
	}

	
	/**
	 * COSMETICS
	 */
	
	protected float getStandingEyeHeight(Pose poseIn, EntitySize sizeIn) {
	      return 1.65F;
	}
	
	public double getMountedYOffset() {
		return (double)(this.getHeight() * 0.7F);
	}

	protected SoundEvent getAmbientSound() {
		return SoundEvents.ENTITY_SPIDER_AMBIENT;
	}

	protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
		return SoundEvents.ENTITY_SPIDER_HURT;
	}

	protected SoundEvent getDeathSound() {
		return SoundEvents.ENTITY_SPIDER_DEATH;
	}

	protected void playStepSound(BlockPos pos, BlockState blockIn) {
		this.playSound(SoundEvents.ENTITY_SPIDER_STEP, 0.15F, 1.0F);
	}
	
	public CreatureAttribute getCreatureAttribute() {
	      return CreatureAttribute.ARTHROPOD;
	}
}
