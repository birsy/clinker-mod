package birsy.clinker.common.entity.monster;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.monster.CreeperEntity;
import net.minecraft.entity.monster.IMob;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.pathfinding.PathNavigator;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;

public class ShoggothBodyEntity extends AbstractShoggothEntity
{

	public ShoggothBodyEntity(EntityType<? extends Monster> type, Level worldIn) {
		super(type, worldIn);
	}
	
	protected void registerGoals() {
		this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.0D, true));
		this.goalSelector.addGoal(2, new FollowLeaderGoal(this, 1.0D));
		this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 6.0F));
		this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
		this.targetSelector.addGoal(2, new HurtByTargetGoal(this));
		this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Player.class, true));
	}
	
	public static AttributeSupplier.Builder setCustomAttributes()
	{
		return Mob.createMobAttributes()
		.add(Attributes.MAX_HEALTH, 36.0D)
		.add(Attributes.MOVEMENT_SPEED, 0.25D)
		.add(Attributes.KNOCKBACK_RESISTANCE, 0.2D)
		.add(Attributes.ATTACK_DAMAGE, 15.0D);
	}

	@Override
	public void tick() {
		/**
		AbstractShoggothEntity closestParent = findClosestParent(this, ShoggothHeadEntity.class);
		this.parent = (ShoggothHeadEntity) closestParent;

		if (parent == null || !this.getEntitySenses().canSee(parent) || !parent.isAlive()) {
			this.attackEntityFrom(DamageSource.STARVE, 2.0F);
		}
		 */
		super.tick();
	}

	protected void playStepSound(BlockPos pos, BlockState blockIn) {
		this.playSound(SoundEvents.HOGLIN_STEP, 0.15F, 1.0F);
	}
	
	protected void doPush(Entity entityIn) {
		if (entityIn instanceof ShoggothHeadEntity) {
			return;
		} else {
			super.doPush(entityIn);
		}
	}
}
