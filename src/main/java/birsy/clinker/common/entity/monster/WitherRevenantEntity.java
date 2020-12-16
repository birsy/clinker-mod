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
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class WitherRevenantEntity extends MonsterEntity
{
	private int windupTick;
	private int stunTick;
	
	public WitherRevenantEntity(EntityType<? extends MonsterEntity> type, World worldIn) {
		super(type, worldIn);
	}
	
	@Override
	protected void registerGoals()
	{
		super.registerGoals();
		this.goalSelector.addGoal(0, new SwimGoal(this));
		this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.0D, false));
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
	
	public ILivingEntityData onInitialSpawn(IServerWorld worldIn, DifficultyInstance difficultyIn, SpawnReason reason, @Nullable ILivingEntityData spawnDataIn, @Nullable CompoundNBT dataTag) {
		this.setItemStackToSlot(EquipmentSlotType.CHEST, new ItemStack(Items.NETHERITE_CHESTPLATE));
		this.setItemStackToSlot(EquipmentSlotType.FEET, new ItemStack(Items.NETHERITE_BOOTS));
		this.setItemStackToSlot(EquipmentSlotType.MAINHAND, new ItemStack(Items.NETHERITE_SWORD));
		this.setItemStackToSlot(EquipmentSlotType.OFFHAND, createShieldWithBanner());
		return super.onInitialSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
	}
	
	public void tick() {
		if (windupTick > 0) {windupTick--;}
		if (stunTick > 0) {stunTick--;}
		
		super.tick();
	}
	
	class ChargedAttackGoal extends MeleeAttackGoal {
		public ChargedAttackGoal() {
			super(WitherRevenantEntity.this, 1.0D, true);
		}
		
		public boolean shouldExecute() {
			if (windupTick > 0) {
				return false;
			} else if (stunTick > 0) {
				return false;
			}
			return super.shouldExecute();
		}

		protected double getAttackReachSqr(LivingEntity attackTarget) {
			float f = WitherRevenantEntity.this.getWidth() - 0.1F;
			return (double)(f * 2.0F * f * 2.0F + attackTarget.getWidth());
		}
	}
	
	public int getWindupTick() {return windupTick;}
	public int getStunTick() {return stunTick;}
	public void setWindupTick(int windupTickIn) {this.windupTick = windupTickIn;}
	public void setStunTick(int stunTickIn) {this.stunTick = stunTickIn;}
	
	//Sounds
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
	
	@OnlyIn(Dist.CLIENT)
	public static enum AttackPose {
		NEUTRAL,
		BLOCKING,
		BOW_AND_ARROW,
		LUNGE_CHARGE,
		LUNGE_MISS,
		SWING_CHARGE,
		SWING_MISS,
		BASH_CHARGE,
		BASH_MISS
	}
	
	@OnlyIn(Dist.CLIENT)
	public AttackPose ArmPose;
	
	@OnlyIn(Dist.CLIENT)
	public AttackPose getArmPose() {
		return ArmPose;
	}
}
