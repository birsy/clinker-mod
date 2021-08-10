package birsy.clinker.common.entity.merchant;
import java.util.EnumSet;
import java.util.Random;
import java.util.Set;
import java.util.function.Predicate;
import javax.annotation.Nullable;

import birsy.clinker.common.entity.monster.gnomad.GnomadAxemanEntity;
import com.google.common.collect.Sets;

import birsy.clinker.common.entity.monster.gnomad.AbstractGnomadEntity;
import birsy.clinker.common.entity.monster.HyenaEntity;
import birsy.clinker.core.registry.ClinkerSounds;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.npc.Npc;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.RangedBowAttackGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.item.trading.Merchant;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.WitherSkeleton;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.Difficulty;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class GnomeEntity extends Monster implements RangedAttackMob, Npc, Merchant
{	
	private final RangedBowAttackGoal<GnomeEntity> aiArrowAttack = new RangedBowAttackGoal<>(this, 1.0D, 20, 15.0F);
	private final MeleeAttackGoal aiAttackOnCollide = new MeleeAttackGoal(this, 1.2D, false) {
		/**
		 * Reset the task's internal state. Called when this task is interrupted by another one
		 */
		public void stop() {
			super.stop();
			GnomeEntity.this.setAggressive(false);
		}

		/**
		 * Execute a one shot task or start executing a continuous task
		 */
		public void start() {
			super.start();
			GnomeEntity.this.setAggressive(true);
		}
	};
	private static final Predicate<Difficulty> DOOR_BREAKING_PREDICATE = (p_213678_0_) -> {
		return p_213678_0_ == Difficulty.NORMAL || p_213678_0_ == Difficulty.HARD;
	};
	@Nullable
	private Player customer;
	@Nullable
	protected MerchantOffers offers;
	
	public GnomeEntity(EntityType<? extends GnomeEntity> type, Level worldIn)
	{
		super(type, worldIn);
	    ((GroundPathNavigation)this.getNavigation()).setCanOpenDoors(true);
	    this.getNavigation().setCanFloat(true);
	    this.setCanPickUpLoot(true);
	    this.setCombatTask();
	}
	protected void registerGoals() {
		super.registerGoals();
		this.goalSelector.addGoal(0, new FloatGoal(this));
		this.goalSelector.addGoal(1, new GnomeEntity.BreakDoorGoal(this));
		this.goalSelector.addGoal(4, new GnomeEntity.AttackGoal(this));
		this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, GnomadAxemanEntity.class, true));
		this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, HyenaEntity.class, true));
		this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, WitherSkeleton.class, true));
		this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, EnderMan.class, true));
		this.goalSelector.addGoal(7, new GnomeEntity.PlaceTorchGoal(this));
		this.goalSelector.addGoal(8, new RandomStrollGoal(this, 0.6D));
		this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, Player.class, 12.0F, 1.0F));
		this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, GnomeEntity.class, 12.0F, 1.0F));
		this.goalSelector.addGoal(11, new LookAtPlayerGoal(this, AbstractGnomadEntity.class, 12.0F, 1.0F));
		this.goalSelector.addGoal(12, new LookAtPlayerGoal(this, Mob.class, 12.0F));
	}
	public static AttributeSupplier.Builder setCustomAttributes()
	{
		return Mob.createMobAttributes()
				.add(Attributes.MAX_HEALTH, 25.0F)
				.add(Attributes.MOVEMENT_SPEED, 0.3F)
				.add(Attributes.ATTACK_DAMAGE, 3.0D);
	}
	public void readAdditionalSaveData(CompoundTag compound) {
		super.readAdditionalSaveData(compound);
		this.setCombatTask();
	}
	public void setItemSlot(EquipmentSlot slotIn, ItemStack stack) {
		super.setItemSlot(slotIn, stack);
		if (!this.level.isClientSide) {
			this.setCombatTask();
		}
	}
	
	
	/**
	 * BOW STUFF
	 */
	
	public void setCombatTask() {
		if (this.level != null && !this.level.isClientSide) {
			this.goalSelector.removeGoal(this.aiAttackOnCollide);
			this.goalSelector.removeGoal(this.aiArrowAttack);
			ItemStack itemstack = this.getItemInHand(ProjectileUtil.getWeaponHoldingHand(this, Items.BOW));
			if (itemstack.getItem() instanceof net.minecraft.world.item.BowItem) {
				int i = 20;
				if (this.level.getDifficulty() != Difficulty.HARD) {
					i = 40;
				}

				this.aiArrowAttack.setMinAttackInterval(i);
				this.goalSelector.addGoal(4, this.aiArrowAttack);
			} else {
				this.goalSelector.addGoal(4, this.aiAttackOnCollide);
			}				
		}
	}
	public void performRangedAttack(LivingEntity target, float distanceFactor) {
		ItemStack itemstack = this.getProjectile(this.getItemInHand(ProjectileUtil.getWeaponHoldingHand(this, Items.BOW)));
		AbstractArrow abstractarrowentity = this.fireArrow(itemstack, distanceFactor);
		if (this.getMainHandItem().getItem() instanceof net.minecraft.world.item.BowItem)
			abstractarrowentity = ((net.minecraft.world.item.BowItem)this.getMainHandItem().getItem()).customArrow(abstractarrowentity);
		double d0 = target.getX() - this.getX();
		double d1 = target.getY(0.3333333333333333D) - abstractarrowentity.getY();
		double d2 = target.getZ() - this.getZ();
		double d3 = (double)Mth.sqrt(d0 * d0 + d2 * d2);
		abstractarrowentity.shoot(d0, d1 + d3 * (double)0.2F, d2, 1.6F, (float)(14 - this.level.getDifficulty().getId() * 4));
		this.playSound(SoundEvents.SKELETON_SHOOT, 1.0F, 1.0F / (this.getRandom().nextFloat() * 0.4F + 0.8F));
		this.level.addFreshEntity(abstractarrowentity);
	}
	
	//fires an arrow
	protected AbstractArrow fireArrow(ItemStack arrowStack, float distanceFactor) {
		return ProjectileUtil.getMobArrow(this, arrowStack, distanceFactor);
	}
	public boolean canFireProjectileWeapon(ProjectileWeaponItem p_230280_1_) {
		return p_230280_1_ == Items.BOW;
	}

	
	/**
	 * CUSTOM GOALS
	 */
	
	class AttackGoal extends MeleeAttackGoal {
		public AttackGoal(GnomeEntity p_i50577_2_) {
			super(p_i50577_2_, 1.0D, false);
		}
	}
	static class BreakDoorGoal extends net.minecraft.world.entity.ai.goal.BreakDoorGoal {
		public BreakDoorGoal(Mob p_i50578_1_) {
			super(p_i50578_1_, 6, GnomeEntity.DOOR_BREAKING_PREDICATE);
			this.setFlags(EnumSet.of(Goal.Flag.MOVE));
		}
	}
	
	
	/**
	 * TRADING STUFF
	 */
	
	@Override
	public void setTradingPlayer(Player player) {
		this.customer = player;
	}
	@Override
	public Player getTradingPlayer() {
		return this.customer;
	}
	@Override
	public MerchantOffers getOffers() {
		if (this.offers == null) {
	         this.offers = new MerchantOffers();
	         this.populateTradeData();
	      }

	      return this.offers;
	}
	@Override
	@OnlyIn(Dist.CLIENT)
	public void overrideOffers(@Nullable MerchantOffers offers) {}
	@Override
	public void notifyTrade(MerchantOffer offer) {
		offer.increaseUses();
		this.ambientSoundTime = -this.getAmbientSoundInterval();
		this.onVillagerTrade(offer);
	}
	protected void onVillagerTrade(MerchantOffer offer) {}
	@Override
	public void notifyTradeUpdated(ItemStack stack) {
		if (!this.level.isClientSide && this.ambientSoundTime > -this.getAmbientSoundInterval() + 20) {
			this.ambientSoundTime = -this.getAmbientSoundInterval();
			this.playSound(this.getVillagerYesNoSound(!stack.isEmpty()), this.getSoundVolume(), this.getVoicePitch());
		}
	}
	@Override
	public Level getLevel() {
		return this.level;
	}
	@Override
	public int getVillagerXp() {
		return 1;
	}
	@Override
	public void overrideXp(int xpIn) {}
	
	protected void populateTradeData() {
		VillagerTrades.ItemListing[] avillagertrades$itrade = VillagerTrades.WANDERING_TRADER_TRADES.get(1);
		VillagerTrades.ItemListing[] avillagertrades$itrade1 = VillagerTrades.WANDERING_TRADER_TRADES.get(2);
		if (avillagertrades$itrade != null && avillagertrades$itrade1 != null) {
			MerchantOffers merchantoffers = this.getOffers();
			this.addTrades(merchantoffers, avillagertrades$itrade, 5);
			int i = this.random.nextInt(avillagertrades$itrade1.length);
			VillagerTrades.ItemListing villagertrades$itrade = avillagertrades$itrade1[i];
			MerchantOffer merchantoffer = villagertrades$itrade.getOffer(this, this.random);
			if (merchantoffer != null) {
				merchantoffers.add(merchantoffer);
			}
		}
	}
	protected void addTrades(MerchantOffers givenMerchantOffers, VillagerTrades.ItemListing[] newTrades, int maxNumbers) {
		Set<Integer> set = Sets.newHashSet();
		if (newTrades.length > maxNumbers) {
			while(set.size() < maxNumbers) {
				set.add(this.random.nextInt(newTrades.length));
			}
		} else {
			for(int i = 0; i < newTrades.length; ++i) {
				set.add(i);
			}
		}
		for(Integer integer : set) {
			VillagerTrades.ItemListing villagertrades$itrade = newTrades[integer];
			MerchantOffer merchantoffer = villagertrades$itrade.getOffer(this, this.random);
			if (merchantoffer != null) {
				givenMerchantOffers.add(merchantoffer);
			}
		}
	}
	
	
	
	static class PlaceTorchGoal extends Goal {
	      private final GnomeEntity gnome;

	      public PlaceTorchGoal(GnomeEntity entityIn) {
	         this.gnome = entityIn;
	      }

	      public boolean canUse() {
	    	  BlockPos lightLevelBlockPos = new BlockPos(this.gnome.getX(), this.gnome.getY(), this.gnome.getZ());
	    	  BlockState lightLevelBlockState = this.gnome.level.getBlockState(lightLevelBlockPos);
	    	  if (this.gnome.getMainHandItem().getItem() != Items.TORCH || this.gnome.getOffhandItem().getItem() != Items.TORCH) {
	    		  return false;
	    	  } else if (!net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent(this.gnome.level, this.gnome)) {
	    		  return false;
	    	  } else if (lightLevelBlockState.getLightEmission() < 8) {
	    		  return true;
	    	  } else {
	    		  return false;
	    	  }
	      }

	      public void tick() {
	         Random random = this.gnome.getRandom();
	         ResourceKey<Level> dimension = this.gnome.getLevel().dimension();
	         LevelAccessor iworld = this.gnome.getLevel();
	         int i = Mth.floor(this.gnome.getX() - 1.0D + random.nextDouble() * 2.0D);
	         int j = Mth.floor(this.gnome.getY() + random.nextDouble() * 2.0D);
	         int k = Mth.floor(this.gnome.getZ() - 1.0D + random.nextDouble() * 2.0D);
	         BlockPos blockpos = new BlockPos(i, j, k);
	         BlockState blockstate = iworld.getBlockState(blockpos);
	         BlockPos blockpos1 = blockpos.below();
	         BlockState blockstate1 = iworld.getBlockState(blockpos1);
	         BlockState blockstate2 = Blocks.TORCH.defaultBlockState();
	         if (blockstate2 != null && this.canPlaceBlock(iworld, blockpos, blockstate2, blockstate, blockstate1, blockpos1)  && !net.minecraftforge.event.ForgeEventFactory.onBlockPlace(gnome, net.minecraftforge.common.util.BlockSnapshot.create(dimension, iworld, blockpos1), net.minecraft.core.Direction.UP)) {
	            iworld.setBlock(blockpos, Blocks.TORCH.defaultBlockState(), 3);
	            
	            if (this.gnome.getMainHandItem().getItem() == Items.TORCH || this.gnome.getMainHandItem().getItem() == Items.TORCH && this.gnome.getOffhandItem().getItem() == Items.TORCH) {
	            	this.gnome.getMainHandItem().shrink(1);
	            } else if (this.gnome.getOffhandItem().getItem() == Items.TORCH) {
	            	this.gnome.getOffhandItem().shrink(1);
	            }
	         }

	      }

	      @SuppressWarnings("deprecation")
		private boolean canPlaceBlock(LevelReader p_220836_1_, BlockPos p_220836_2_, BlockState p_220836_3_, BlockState p_220836_4_, BlockState p_220836_5_, BlockPos p_220836_6_) {
	         return p_220836_4_.isAir(p_220836_1_, p_220836_2_) && !p_220836_5_.isAir(p_220836_1_, p_220836_6_) && p_220836_5_.isCollisionShapeFullBlock(p_220836_1_, p_220836_6_) && p_220836_3_.canSurvive(p_220836_1_, p_220836_2_);
	      }
	}


	/**
	 * COSMETICS
	 */
	
	//sounds
	protected SoundEvent getAmbientSound() {
		return ClinkerSounds.ENTITY_GNOME_AMBIENT.get();
	}
	protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
		return ClinkerSounds.ENTITY_GNOME_HURT.get();
	}
	protected SoundEvent getDeathSound() {
		return ClinkerSounds.ENTITY_GNOME_DEATH.get();
	}
	
	@Override
	public SoundEvent getNotifyTradeSound() {
		return null;
	}
	protected SoundEvent getVillagerYesNoSound(boolean getYesSound) {
		return getYesSound ? SoundEvents.VILLAGER_YES : SoundEvents.VILLAGER_NO;
	}
	protected void playStepSound(BlockPos pos, BlockState blockIn) {
		this.playSound(SoundEvents.METAL_STEP, 0.15F, 1.0F);
	}
	protected float getSoundVolume() {
		return 0.4F;
	}
	
	//other
	public DyeColor getHatColor(GnomeEntity entity) {
		int color = (int) Math.round(Math.abs(Math.sin(entity.getId())*15));
		switch(color) {
			case 0:
				return DyeColor.BLACK;
			case 1:
				return DyeColor.WHITE;
			case 2:
				return DyeColor.ORANGE;
			case 3:
				return DyeColor.MAGENTA;
			case 4:
				return DyeColor.LIGHT_BLUE;
			case 5:
				return DyeColor.YELLOW;
			case 6:
				return DyeColor.LIME;
			case 7:
				return DyeColor.PINK;
			case 8:
				return DyeColor.GRAY;
			case 9:
				return DyeColor.LIGHT_GRAY;
			case 10:
				return DyeColor.CYAN;
			case 11:
				return DyeColor.PURPLE;
			case 12:
				return DyeColor.BLUE;
			case 13:
				return DyeColor.BROWN;
			case 14:
				return DyeColor.GREEN;
			case 15:
				return DyeColor.GREEN;
			default:
				return DyeColor.RED;
		}      
	}
	@OnlyIn(Dist.CLIENT)
	public ArmPose getArmPose() {
		if(this.getMainHandItem().getItem() instanceof net.minecraft.world.item.BlockItem) {
			return ArmPose.HOLDING_BLOCK;
		} else if (this.isAggressive() && this.getMainHandItem().getItem() instanceof net.minecraft.world.item.BowItem) {
			return ArmPose.BOW_AND_ARROW;
		} else if (this.isAggressive()) {
			return ArmPose.ATTACKING;
	    } else {
			return ArmPose.NEUTRAL;
		}
	}
	@OnlyIn(Dist.CLIENT)
	public static enum ArmPose {
		NEUTRAL,
		HOLDING_BLOCK,
		ATTACKING,
		SPELLCASTING,
		BOW_AND_ARROW,
		CROSSBOW_HOLD,
		CROSSBOW_CHARGE,
		CELEBRATING,
		SHAKING;
	}
	public BlockState getHeldBlockState() {
		if (this.getMainHandItem().getItem() instanceof net.minecraft.world.item.BlockItem) {
			return ((BlockItem) this.getMainHandItem().getItem()).getBlock().defaultBlockState();
		} else {
			return null;
		}
	}
	@Override
	public boolean showProgressBar() {
		return false;
	}
}
