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
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.INPC;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.ai.goal.RandomWalkingGoal;
import net.minecraft.entity.ai.goal.RangedBowAttackGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.merchant.IMerchant;
import net.minecraft.entity.merchant.villager.VillagerTrades;
import net.minecraft.entity.monster.EndermanEntity;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.monster.WitherSkeletonEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.entity.projectile.ProjectileHelper;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.MerchantOffer;
import net.minecraft.item.MerchantOffers;
import net.minecraft.item.ShootableItem;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.pathfinding.GroundPathNavigator;
import net.minecraft.util.DamageSource;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.Difficulty;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class GnomeEntity extends MonsterEntity implements IRangedAttackMob, INPC, IMerchant
{	
	private final RangedBowAttackGoal<GnomeEntity> aiArrowAttack = new RangedBowAttackGoal<>(this, 1.0D, 20, 15.0F);
	private final MeleeAttackGoal aiAttackOnCollide = new MeleeAttackGoal(this, 1.2D, false) {
		/**
		 * Reset the task's internal state. Called when this task is interrupted by another one
		 */
		public void resetTask() {
			super.resetTask();
			GnomeEntity.this.setAggroed(false);
		}

		/**
		 * Execute a one shot task or start executing a continuous task
		 */
		public void startExecuting() {
			super.startExecuting();
			GnomeEntity.this.setAggroed(true);
		}
	};
	private static final Predicate<Difficulty> field_213681_b = (p_213678_0_) -> {
		return p_213678_0_ == Difficulty.NORMAL || p_213678_0_ == Difficulty.HARD;
	};
	@Nullable
	private PlayerEntity customer;
	@Nullable
	protected MerchantOffers offers;
	
	public GnomeEntity(EntityType<? extends GnomeEntity> type, World worldIn)
	{
		super(type, worldIn);
	    ((GroundPathNavigator)this.getNavigator()).setBreakDoors(true);
	    this.getNavigator().setCanSwim(true);
	    this.setCanPickUpLoot(true);
	    this.setCombatTask();
	}
	protected void registerGoals() {
		super.registerGoals();
		this.goalSelector.addGoal(0, new SwimGoal(this));
		this.goalSelector.addGoal(1, new GnomeEntity.BreakDoorGoal(this));
		this.goalSelector.addGoal(4, new GnomeEntity.AttackGoal(this));
		this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, GnomadAxemanEntity.class, true));
		this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, HyenaEntity.class, true));
		this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, WitherSkeletonEntity.class, true));
		this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, EndermanEntity.class, true));
		this.goalSelector.addGoal(7, new GnomeEntity.PlaceTorchGoal(this));
		this.goalSelector.addGoal(8, new RandomWalkingGoal(this, 0.6D));
		this.goalSelector.addGoal(9, new LookAtGoal(this, PlayerEntity.class, 12.0F, 1.0F));
		this.goalSelector.addGoal(10, new LookAtGoal(this, GnomeEntity.class, 12.0F, 1.0F));
		this.goalSelector.addGoal(11, new LookAtGoal(this, AbstractGnomadEntity.class, 12.0F, 1.0F));
		this.goalSelector.addGoal(12, new LookAtGoal(this, MobEntity.class, 12.0F));
	}
	public static AttributeModifierMap.MutableAttribute setCustomAttributes()
	{
		return MobEntity.func_233666_p_()
				.createMutableAttribute(Attributes.MAX_HEALTH, 25.0F)
				.createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.3F)
				.createMutableAttribute(Attributes.ATTACK_DAMAGE, 3.0D);
	}
	public void readAdditional(CompoundNBT compound) {
		super.readAdditional(compound);
		this.setCombatTask();
	}
	public void setItemStackToSlot(EquipmentSlotType slotIn, ItemStack stack) {
		super.setItemStackToSlot(slotIn, stack);
		if (!this.world.isRemote) {
			this.setCombatTask();
		}
	}
	
	
	/**
	 * BOW STUFF
	 */
	
	public void setCombatTask() {
		if (this.world != null && !this.world.isRemote) {
			this.goalSelector.removeGoal(this.aiAttackOnCollide);
			this.goalSelector.removeGoal(this.aiArrowAttack);
			ItemStack itemstack = this.getHeldItem(ProjectileHelper.getHandWith(this, Items.BOW));
			if (itemstack.getItem() instanceof net.minecraft.item.BowItem) {
				int i = 20;
				if (this.world.getDifficulty() != Difficulty.HARD) {
					i = 40;
				}

				this.aiArrowAttack.setAttackCooldown(i);
				this.goalSelector.addGoal(4, this.aiArrowAttack);
			} else {
				this.goalSelector.addGoal(4, this.aiAttackOnCollide);
			}				
		}
	}
	public void attackEntityWithRangedAttack(LivingEntity target, float distanceFactor) {
		ItemStack itemstack = this.findAmmo(this.getHeldItem(ProjectileHelper.getHandWith(this, Items.BOW)));
		AbstractArrowEntity abstractarrowentity = this.fireArrow(itemstack, distanceFactor);
		if (this.getHeldItemMainhand().getItem() instanceof net.minecraft.item.BowItem)
			abstractarrowentity = ((net.minecraft.item.BowItem)this.getHeldItemMainhand().getItem()).customArrow(abstractarrowentity);
		double d0 = target.getPosX() - this.getPosX();
		double d1 = target.getPosYHeight(0.3333333333333333D) - abstractarrowentity.getPosY();
		double d2 = target.getPosZ() - this.getPosZ();
		double d3 = (double)MathHelper.sqrt(d0 * d0 + d2 * d2);
		abstractarrowentity.shoot(d0, d1 + d3 * (double)0.2F, d2, 1.6F, (float)(14 - this.world.getDifficulty().getId() * 4));
		this.playSound(SoundEvents.ENTITY_SKELETON_SHOOT, 1.0F, 1.0F / (this.getRNG().nextFloat() * 0.4F + 0.8F));
		this.world.addEntity(abstractarrowentity);
	}
	
	//fires an arrow
	protected AbstractArrowEntity fireArrow(ItemStack arrowStack, float distanceFactor) {
		return ProjectileHelper.fireArrow(this, arrowStack, distanceFactor);
	}
	public boolean func_230280_a_(ShootableItem p_230280_1_) {
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
	static class BreakDoorGoal extends net.minecraft.entity.ai.goal.BreakDoorGoal {
		public BreakDoorGoal(MobEntity p_i50578_1_) {
			super(p_i50578_1_, 6, GnomeEntity.field_213681_b);
			this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE));
		}
	}
	
	
	/**
	 * TRADING STUFF
	 */
	
	@Override
	public void setCustomer(PlayerEntity player) {
		this.customer = player;
	}
	@Override
	public PlayerEntity getCustomer() {
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
	public void setClientSideOffers(@Nullable MerchantOffers offers) {}
	@Override
	public void onTrade(MerchantOffer offer) {
		offer.increaseUses();
		this.livingSoundTime = -this.getTalkInterval();
		this.onVillagerTrade(offer);
	}
	protected void onVillagerTrade(MerchantOffer offer) {}
	@Override
	public void verifySellingItem(ItemStack stack) {
		if (!this.world.isRemote && this.livingSoundTime > -this.getTalkInterval() + 20) {
			this.livingSoundTime = -this.getTalkInterval();
			this.playSound(this.getVillagerYesNoSound(!stack.isEmpty()), this.getSoundVolume(), this.getSoundPitch());
		}
	}
	@Override
	public World getWorld() {
		return this.world;
	}
	@Override
	public int getXp() {
		return 1;
	}
	@Override
	public void setXP(int xpIn) {}
	
	protected void populateTradeData() {
		VillagerTrades.ITrade[] avillagertrades$itrade = VillagerTrades.field_221240_b.get(1);
		VillagerTrades.ITrade[] avillagertrades$itrade1 = VillagerTrades.field_221240_b.get(2);
		if (avillagertrades$itrade != null && avillagertrades$itrade1 != null) {
			MerchantOffers merchantoffers = this.getOffers();
			this.addTrades(merchantoffers, avillagertrades$itrade, 5);
			int i = this.rand.nextInt(avillagertrades$itrade1.length);
			VillagerTrades.ITrade villagertrades$itrade = avillagertrades$itrade1[i];
			MerchantOffer merchantoffer = villagertrades$itrade.getOffer(this, this.rand);
			if (merchantoffer != null) {
				merchantoffers.add(merchantoffer);
			}
		}
	}
	protected void addTrades(MerchantOffers givenMerchantOffers, VillagerTrades.ITrade[] newTrades, int maxNumbers) {
		Set<Integer> set = Sets.newHashSet();
		if (newTrades.length > maxNumbers) {
			while(set.size() < maxNumbers) {
				set.add(this.rand.nextInt(newTrades.length));
			}
		} else {
			for(int i = 0; i < newTrades.length; ++i) {
				set.add(i);
			}
		}
		for(Integer integer : set) {
			VillagerTrades.ITrade villagertrades$itrade = newTrades[integer];
			MerchantOffer merchantoffer = villagertrades$itrade.getOffer(this, this.rand);
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

	      public boolean shouldExecute() {
	    	  BlockPos lightLevelBlockPos = new BlockPos(this.gnome.getPosX(), this.gnome.getPosY(), this.gnome.getPosZ());
	    	  BlockState lightLevelBlockState = this.gnome.world.getBlockState(lightLevelBlockPos);
	    	  if (this.gnome.getHeldItemMainhand().getItem() != Items.TORCH || this.gnome.getHeldItemOffhand().getItem() != Items.TORCH) {
	    		  return false;
	    	  } else if (!net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent(this.gnome.world, this.gnome)) {
	    		  return false;
	    	  } else if (lightLevelBlockState.getLightValue() < 8) {
	    		  return true;
	    	  } else {
	    		  return false;
	    	  }
	      }

	      public void tick() {
	         Random random = this.gnome.getRNG();
	         RegistryKey<World> dimension = this.gnome.getWorld().getDimensionKey();
	         IWorld iworld = this.gnome.getWorld();
	         int i = MathHelper.floor(this.gnome.getPosX() - 1.0D + random.nextDouble() * 2.0D);
	         int j = MathHelper.floor(this.gnome.getPosY() + random.nextDouble() * 2.0D);
	         int k = MathHelper.floor(this.gnome.getPosZ() - 1.0D + random.nextDouble() * 2.0D);
	         BlockPos blockpos = new BlockPos(i, j, k);
	         BlockState blockstate = iworld.getBlockState(blockpos);
	         BlockPos blockpos1 = blockpos.down();
	         BlockState blockstate1 = iworld.getBlockState(blockpos1);
	         BlockState blockstate2 = Blocks.TORCH.getDefaultState();
	         if (blockstate2 != null && this.func_220836_a(iworld, blockpos, blockstate2, blockstate, blockstate1, blockpos1)  && !net.minecraftforge.event.ForgeEventFactory.onBlockPlace(gnome, net.minecraftforge.common.util.BlockSnapshot.create(dimension, iworld, blockpos1), net.minecraft.util.Direction.UP)) {
	            iworld.setBlockState(blockpos, Blocks.TORCH.getDefaultState(), 3);
	            
	            if (this.gnome.getHeldItemMainhand().getItem() == Items.TORCH || this.gnome.getHeldItemMainhand().getItem() == Items.TORCH && this.gnome.getHeldItemOffhand().getItem() == Items.TORCH) {
	            	this.gnome.getHeldItemMainhand().shrink(1);
	            } else if (this.gnome.getHeldItemOffhand().getItem() == Items.TORCH) {
	            	this.gnome.getHeldItemOffhand().shrink(1);
	            }
	         }

	      }

	      @SuppressWarnings("deprecation")
		private boolean func_220836_a(IWorldReader p_220836_1_, BlockPos p_220836_2_, BlockState p_220836_3_, BlockState p_220836_4_, BlockState p_220836_5_, BlockPos p_220836_6_) {
	         return p_220836_4_.isAir(p_220836_1_, p_220836_2_) && !p_220836_5_.isAir(p_220836_1_, p_220836_6_) && p_220836_5_.hasOpaqueCollisionShape(p_220836_1_, p_220836_6_) && p_220836_3_.isValidPosition(p_220836_1_, p_220836_2_);
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
	public SoundEvent getYesSound() {
		return null;
	}
	protected SoundEvent getVillagerYesNoSound(boolean getYesSound) {
		return getYesSound ? SoundEvents.ENTITY_VILLAGER_YES : SoundEvents.ENTITY_VILLAGER_NO;
	}
	protected void playStepSound(BlockPos pos, BlockState blockIn) {
		this.playSound(SoundEvents.BLOCK_METAL_STEP, 0.15F, 1.0F);
	}
	protected float getSoundVolume() {
		return 0.4F;
	}
	
	//other
	public DyeColor getHatColor(GnomeEntity entity) {
		int color = (int) Math.round(Math.abs(Math.sin(entity.getEntityId())*15));
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
		if(this.getHeldItemMainhand().getItem() instanceof net.minecraft.item.BlockItem) {
			return ArmPose.HOLDING_BLOCK;
		} else if (this.isAggressive() && this.getHeldItemMainhand().getItem() instanceof net.minecraft.item.BowItem) {
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
		if (this.getHeldItemMainhand().getItem() instanceof net.minecraft.item.BlockItem) {
			return ((BlockItem) this.getHeldItemMainhand().getItem()).getBlock().getDefaultState();
		} else {
			return null;
		}
	}
	@Override
	public boolean hasXPBar() {
		return false;
	}
}
