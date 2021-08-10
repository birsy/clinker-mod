package birsy.clinker.common.entity.monster;

import net.minecraft.block.BlockState;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.monster.CreeperEntity;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.pathfinding.PathNavigator;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;

public class ShoggothBodyEntity extends AbstractShoggothEntity
{

	public ShoggothBodyEntity(EntityType<? extends MonsterEntity> type, World worldIn) {
		super(type, worldIn);
	}
	
	protected void registerGoals() {
		this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.0D, true));
		this.goalSelector.addGoal(2, new FollowLeaderGoal(this, 1.0D));
		this.goalSelector.addGoal(7, new LookAtGoal(this, PlayerEntity.class, 6.0F));
		this.goalSelector.addGoal(8, new LookRandomlyGoal(this));
		this.targetSelector.addGoal(2, new HurtByTargetGoal(this));
		this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, PlayerEntity.class, true));
	}
	
	public static AttributeModifierMap.MutableAttribute setCustomAttributes()
	{
		return MobEntity.func_233666_p_()
		.createMutableAttribute(Attributes.MAX_HEALTH, 36.0D)
		.createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.25D)
		.createMutableAttribute(Attributes.KNOCKBACK_RESISTANCE, 0.2D)
		.createMutableAttribute(Attributes.ATTACK_DAMAGE, 15.0D);
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
		this.playSound(SoundEvents.ENTITY_HOGLIN_STEP, 0.15F, 1.0F);
	}
	
	protected void collideWithEntity(Entity entityIn) {
		if (entityIn instanceof ShoggothHeadEntity) {
			return;
		} else {
			super.collideWithEntity(entityIn);
		}
	}
}
