package birsy.clinker.common.entity.monster;

import javax.annotation.Nullable;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.entity.ai.goal.LookRandomlyGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.WaterAvoidingRandomWalkingGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.BannerItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.level.block.entity.BannerPattern;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.Level;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;

public class WitherRevenantEntity extends Monster
{
	private int windupTick;
	private int stunTick;
	public AIPhase phase;
	
	boolean debug = true;
	
	public WitherRevenantEntity(EntityType<? extends Monster> type, Level worldIn) {
		super(type, worldIn);
	}
	
	@Override
	protected void registerGoals()
	{
		super.registerGoals();
		//this.goalSelector.addGoal(0, new SwimGoal(this));
		this.goalSelector.addGoal(2, new DuelGoal(this));
	    //this.goalSelector.addGoal(5, new WaterAvoidingRandomWalkingGoal(this, 1.0D));
	    this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 6.0F));
	    //this.goalSelector.addGoal(7, new LookRandomlyGoal(this));
	    //this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
	    this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
	}
	
	public static AttributeSupplier.Builder setCustomAttributes()
	{
		return Mob.createMobAttributes()
				.add(Attributes.MAX_HEALTH, 30.0)
				.add(Attributes.MOVEMENT_SPEED, 0.25D)
				.add(Attributes.ATTACK_KNOCKBACK, 0.25D)
				.add(Attributes.ATTACK_DAMAGE, 4.0D);
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
		if (this.getTarget() == null) {
			phase = AIPhase.WANDERING;
		}
		
		if(debug == true) {
			this.setCustomName(new TextComponent("AI Phase: ").append(new TextComponent(this.phase.toString()).withStyle(ChatFormatting.RED)));
			this.setCustomNameVisible(true);
		}
		//(new StringTextComponent(this.phase.toString()).mergeStyle(TextFormatting.BLUE))
		super.tick();
	}
	
	class DuelGoal extends Goal {
		private final WitherRevenantEntity revenant;
		
		public DuelGoal(WitherRevenantEntity mob) {
			this.revenant = mob;
		}
		
		public boolean canUse() {
			if (revenant.getTarget() != null && revenant.getSensing().canSee(revenant.getTarget())) {
				return revenant.distanceToSqr(revenant.getTarget()) < 10.0F;
			} else {
				return false;
			}
		}
		
		public boolean canContinueToUse() {
			if (revenant.getTarget() == null) {
				return false;
			} else if (!revenant.getTarget().isAlive() || revenant.phase == AIPhase.WANDERING) {
				return false;
			} else {
				return true;
			}
		}
		
		public void start() {
			LivingEntity target = revenant.getTarget();
			
			if (revenant.distanceToSqr(target) < 10.0F) {
				revenant.phase = AIPhase.PURSUING;
			} else if (revenant.distanceToSqr(target) < 5.0F) {
				revenant.phase = AIPhase.CIRCLING;
			}
			
			super.start();
		}
		
		@SuppressWarnings("unused")
		@Override
		public void tick() {
			LivingEntity target = revenant.getTarget();
			double targetDistance = revenant.distanceToSqr(target.position());
			int targetShieldUpTicks = 0;
			AIPhase phase = revenant.phase;
			
			if (revenant.windupTick > 0) {
				revenant.windupTick--;
			}
			if (revenant.stunTick > 0) {
				revenant.stunTick--;
			}
			
			if (target.isBlocking()) {
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
					//revenant.getNavigator().tryMoveToEntityLiving(target, 1.2);
					break;
					
				case CIRCLING:
					if (this.revenant.getMainHandItem().getItem() == Items.SHIELD || this.revenant.getOffhandItem().getItem() == Items.SHIELD) {
						this.revenant.blockUsingShield(this.revenant);
						this.revenant.startUsingItem(ProjectileUtil.getWeaponHoldingHand(this.revenant, Items.SHIELD));
					}

					strafeAroundTarget(target, 4.75F, 1.0F);
					revenant.getLookControl().setLookAt(target.getX(), target.getY(), target.getZ());
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

			if (revenant.distanceToSqr(target) < 5.0F) {
				revenant.phase = AIPhase.CIRCLING;
			} else if (revenant.distanceToSqr(target) < 10.0F) {
				revenant.phase = AIPhase.PURSUING;
			} else {
				revenant.phase = AIPhase.WAITING;
			}
		}
		
		@Override
		public void stop() {
			super.stop();
			
			revenant.phase = AIPhase.WANDERING;
		}

		public void strafeAroundTarget(Entity target, float strafeSpeed, float moveSpeed) {
			boolean rotationDirection = true;

			if (revenant.random.nextInt(80) == 0) {
				if (rotationDirection) {
					rotationDirection = false;
				} else {
					rotationDirection = true;
				}
			}

			this.revenant.getMoveControl().strafe(0.5F, rotationDirection ? 0.5F : -0.5F);
			this.revenant.lookAt(target, 30.0F, 30.0F);
			revenant.getLookControl().setLookAt(target.getX(), target.getY(), target.getZ());
		}
	}

	public int getWindupTick() {return windupTick;}
	public int getStunTick() {return stunTick;}
	public void setWindupTick(int windupTickIn) {this.windupTick = windupTickIn;}
	public void setStunTick(int stunTickIn) {this.stunTick = stunTickIn;}
	
	
	public SpawnGroupData finalizeSpawn(ServerLevelAccessor worldIn, DifficultyInstance difficultyIn, MobSpawnType reason, @Nullable SpawnGroupData spawnDataIn, @Nullable CompoundTag dataTag) {
		this.setItemSlot(EquipmentSlot.CHEST, new ItemStack(Items.NETHERITE_CHESTPLATE));
		this.setItemSlot(EquipmentSlot.FEET, new ItemStack(Items.NETHERITE_BOOTS));
		this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.NETHERITE_SWORD));
		this.setItemSlot(EquipmentSlot.OFFHAND, createShieldWithBanner());
		return super.finalizeSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
	}
	
	public static ItemStack createShieldWithBanner() {
		ItemStack shield = new ItemStack(Items.SHIELD);
		CompoundTag compoundnbt = shield.getOrCreateTagElement("BlockEntityTag");
		ListTag listnbt = (new BannerPattern.Builder())
				.addPattern(BannerPattern.STRIPE_TOP, DyeColor.GRAY)
				.addPattern(BannerPattern.STRIPE_LEFT, DyeColor.GRAY)
				.addPattern(BannerPattern.STRIPE_RIGHT, DyeColor.GRAY)
				.addPattern(BannerPattern.GLOBE, DyeColor.LIGHT_GRAY)
				.addPattern(BannerPattern.SKULL, DyeColor.BLACK)
				.addPattern(BannerPattern.HALF_HORIZONTAL_MIRROR, DyeColor.GRAY)
				.addPattern(BannerPattern.FLOWER, DyeColor.BLACK)
				.addPattern(BannerPattern.BORDER, DyeColor.BLACK)
				.toListTag();
		compoundnbt.putInt("Base", ((BannerItem) Items.LIGHT_GRAY_BANNER.getItem()).getColor().getId());
		compoundnbt.put("Patterns", listnbt);
		shield.setHoverName((new TranslatableComponent("item.clinker.ancient_shield")).withStyle(ChatFormatting.GOLD));
		return shield;
	}
	
	protected SoundEvent getAmbientSound() {
		return SoundEvents.WITHER_SKELETON_AMBIENT;
	}

	protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
		return SoundEvents.WITHER_SKELETON_HURT;
	}

	protected SoundEvent getDeathSound() {
		return SoundEvents.WITHER_SKELETON_DEATH;
	}

	protected void playStepSound(BlockPos pos, BlockState blockIn) {
		this.playSound(SoundEvents.WITHER_SKELETON_STEP, 0.15F, 1.0F);
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
