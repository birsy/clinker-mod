package birsy.clinker.common.entity.monster;

import javax.annotation.Nullable;

import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.HurtByTargetGoal;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.LookRandomlyGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.WaterAvoidingRandomWalkingGoal;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.BannerItem;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.tileentity.BannerPattern;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.World;

public class WitherRevenantEntity extends MonsterEntity
{
	private int windupTick;
	private int stunTick;
	public AIPhase phase;
	
	boolean debug = true;
	
	public WitherRevenantEntity(EntityType<? extends MonsterEntity> type, World worldIn) {
		super(type, worldIn);
	}
	
	@Override
	protected void registerGoals()
	{
		super.registerGoals();
		this.goalSelector.addGoal(0, new SwimGoal(this));
		this.goalSelector.addGoal(2, new DuelGoal(this));
	    this.goalSelector.addGoal(5, new WaterAvoidingRandomWalkingGoal(this, 1.0D));
	    this.goalSelector.addGoal(6, new LookAtGoal(this, PlayerEntity.class, 6.0F));
	    this.goalSelector.addGoal(7, new LookRandomlyGoal(this));
	    this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
	    this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, PlayerEntity.class, true));
	}
	
	public static AttributeModifierMap.MutableAttribute setCustomAttributes()
	{
		return MobEntity.func_233666_p_()
				.createMutableAttribute(Attributes.MAX_HEALTH, 30.0)
				.createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.25D)
				.createMutableAttribute(Attributes.ATTACK_KNOCKBACK, 0.25D)
				.createMutableAttribute(Attributes.ATTACK_DAMAGE, 4.0D);
	}
	
	public static enum AIPhase {
		WANDERING("Wandering"),
		WAITING("Waiting"),
		PURSUING("Pursuing"),
		CIRCLING("Circling"),
		ATTACKING("Attacking"),
		RECOVERING("Recovering");
		
		String name;
		
		private AIPhase(String nameIn) {
			name = nameIn;
		}
		
		public String toString() {
			return name;
		}
	}
	
	public void tick() {
		if (this.getAttackTarget() == null) {
			phase = AIPhase.WANDERING;
		}
		
		if(debug == true) {
			this.setCustomName(new StringTextComponent("AI Phase: ").append(new StringTextComponent(this.phase.toString()).mergeStyle(TextFormatting.BLUE)));
		}
		//(new StringTextComponent(this.phase.toString()).mergeStyle(TextFormatting.BLUE))
		super.tick();
	}
	
	class DuelGoal extends Goal {
		private final WitherRevenantEntity revenant;
		
		public DuelGoal(WitherRevenantEntity mob) {
			this.revenant = mob;
		}
		
		public boolean shouldExecute() {
			if (revenant.getAttackTarget() != null && revenant.getEntitySenses().canSee(revenant.getAttackTarget())) {
				return revenant.getDistanceSq(revenant.getAttackTarget()) < 10.0F;
			} else {
				return false;
			}
		}
		
		public boolean shouldContinueExecuting() {
			if (revenant.getAttackTarget() == null) {
				return false;
			} else if (!revenant.getAttackTarget().isAlive() || revenant.phase == AIPhase.WANDERING) {
				return false;
			} else {
				return true;
			}
		}
		
		public void startExecuting() {
			LivingEntity target = revenant.getAttackTarget();
			
			if (revenant.getDistanceSq(target) < 10.0F) {
				revenant.phase = AIPhase.PURSUING;
			} else if (revenant.getDistanceSq(target) < 5.0F) {
				revenant.phase = AIPhase.CIRCLING;
			}
			
			super.startExecuting();
		}
		
		@SuppressWarnings("unused")
		@Override
		public void tick() {
			LivingEntity target = revenant.getAttackTarget();
			double targetDistance = revenant.getDistanceSq(target.getPositionVec());
			int targetShieldUpTicks = 0;
			AIPhase phase = revenant.phase;
			
			if (revenant.windupTick > 0) {
				revenant.windupTick--;
			}
			if (revenant.stunTick > 0) {
				revenant.stunTick--;
			}
			
			if (target.isActiveItemStackBlocking()) {
				targetShieldUpTicks++;
			} else {
				targetShieldUpTicks = 0;
			}
			
			
			/**
			 * PHASE AI 
			 * Decides what to do when pursuing, attacking, etc.
			 */
			
			switch (phase) {
				case PURSUING:
					revenant.getNavigator().tryMoveToEntityLiving(target, 1.2);
					break;
					
				case CIRCLING:
					break;
					
				case RECOVERING:
					break;
					
				case ATTACKING:
					break;
					
				case WAITING:
					break;
					
				default:
					break;
			}
			
			
			/**
			 * PHASE CONTROLLER 
			 * Decides what phase the entity should be in.
			 */
			
			if (revenant.getDistanceSq(target) < 10.0F) {
				revenant.phase = AIPhase.PURSUING;
			} else {
				revenant.phase = AIPhase.WAITING;
			}
		}
		
		@Override
		public void resetTask() {
			super.resetTask();
			
			revenant.phase = AIPhase.WANDERING;
		}
	}
	
	public int getWindupTick() {return windupTick;}
	public int getStunTick() {return stunTick;}
	public void setWindupTick(int windupTickIn) {this.windupTick = windupTickIn;}
	public void setStunTick(int stunTickIn) {this.stunTick = stunTickIn;}
	
	
	public ILivingEntityData onInitialSpawn(IServerWorld worldIn, DifficultyInstance difficultyIn, SpawnReason reason, @Nullable ILivingEntityData spawnDataIn, @Nullable CompoundNBT dataTag) {
		this.setItemStackToSlot(EquipmentSlotType.CHEST, new ItemStack(Items.NETHERITE_CHESTPLATE));
		this.setItemStackToSlot(EquipmentSlotType.FEET, new ItemStack(Items.NETHERITE_BOOTS));
		this.setItemStackToSlot(EquipmentSlotType.MAINHAND, new ItemStack(Items.NETHERITE_SWORD));
		this.setItemStackToSlot(EquipmentSlotType.OFFHAND, createShieldWithBanner());
		return super.onInitialSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
	}
	
	public static ItemStack createShieldWithBanner() {
		ItemStack shield = new ItemStack(Items.SHIELD);
		CompoundNBT compoundnbt = shield.getOrCreateChildTag("BlockEntityTag");
		ListNBT listnbt = (new BannerPattern.Builder())
				.setPatternWithColor(BannerPattern.STRIPE_TOP, DyeColor.GRAY)
				.setPatternWithColor(BannerPattern.STRIPE_LEFT, DyeColor.GRAY)
				.setPatternWithColor(BannerPattern.STRIPE_RIGHT, DyeColor.GRAY)
				.setPatternWithColor(BannerPattern.GLOBE, DyeColor.LIGHT_GRAY)
				.setPatternWithColor(BannerPattern.SKULL, DyeColor.BLACK)
				.setPatternWithColor(BannerPattern.HALF_HORIZONTAL_MIRROR, DyeColor.GRAY)
				.setPatternWithColor(BannerPattern.FLOWER, DyeColor.BLACK)
				.setPatternWithColor(BannerPattern.BORDER, DyeColor.BLACK)
				.buildNBT();
		compoundnbt.putInt("Base", ((BannerItem) Items.LIGHT_GRAY_BANNER.getItem()).getColor().getId());
		compoundnbt.put("Patterns", listnbt);
		shield.setDisplayName((new TranslationTextComponent("item.clinker.ancient_shield")).mergeStyle(TextFormatting.GOLD));
		return shield;
	}
	
	protected SoundEvent getAmbientSound() {
		return SoundEvents.ENTITY_WITHER_SKELETON_AMBIENT;
	}

	protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
		return SoundEvents.ENTITY_WITHER_SKELETON_HURT;
	}

	protected SoundEvent getDeathSound() {
		return SoundEvents.ENTITY_WITHER_SKELETON_DEATH;
	}

	protected void playStepSound(BlockPos pos, BlockState blockIn) {
		this.playSound(SoundEvents.ENTITY_WITHER_SKELETON_STEP, 0.15F, 1.0F);
	}
	
	public static enum Pose {
		NEUTRAL,
		BLOCKING,
		BOW_AND_ARROW,
		LUNGE_CHARGE,
		LUNGE_MISS,
		SWING_CHARGE,
		SWING_MISS,
		BASH_CHARGE,
		BASH_MISS;
	}
	
	public Pose ArmPose;
	
	public Pose getArmPose() {
		return ArmPose;
	}
}
