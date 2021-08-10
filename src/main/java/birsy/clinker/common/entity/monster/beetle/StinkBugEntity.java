package birsy.clinker.common.entity.monster.beetle;

import java.util.List;
import java.util.function.Predicate;

import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.HurtByTargetGoal;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.LookRandomlyGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.MoveTowardsTargetGoal;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.world.World;

@SuppressWarnings("unused")
public class StinkBugEntity extends MonsterEntity {
	public static final DataParameter<Boolean> SLEEPING = EntityDataManager.createKey(StinkBugEntity.class, DataSerializers.BOOLEAN);
	private int awakenTimer;
	
	private static final Predicate<LivingEntity> ENEMY_MATCHER = (entity) -> {
		if (entity == null) {
			return false;
		} else if (!(entity instanceof PlayerEntity) || !entity.isSpectator() && !((PlayerEntity)entity).isCreative()) {
			return true;
		} else if ((entity instanceof CreatureEntity) && !(entity instanceof StinkBugEntity)){
			return true;
		} else {
			return false;
		}
	};
	
	public StinkBugEntity(EntityType<? extends MonsterEntity> type, World worldIn) {
		super(type, worldIn);
	}
	
	protected void registerGoals() {
		this.goalSelector.addGoal(1, new StinkBugEntity.AwakenGoal(this));
		this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.0D, true));
		this.goalSelector.addGoal(3, new MoveTowardsTargetGoal(this, 0.9D, 32.0F));
		this.goalSelector.addGoal(7, new LookAtGoal(this, PlayerEntity.class, 6.0F));
		this.goalSelector.addGoal(8, new LookRandomlyGoal(this));
		this.targetSelector.addGoal(2, new HurtByTargetGoal(this));
		this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, PlayerEntity.class, true));
	}
	
	public static AttributeModifierMap.MutableAttribute setCustomAttributes()
	{
		return MobEntity.func_233666_p_()
				.createMutableAttribute(Attributes.MAX_HEALTH, 17.0)
				.createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.25D)
				.createMutableAttribute(Attributes.ATTACK_DAMAGE, 3.0D);
	}
	
	static class AwakenGoal extends Goal {
		private final StinkBugEntity stinkbug;

		public AwakenGoal(StinkBugEntity stinkbug) {
			this.stinkbug = stinkbug;
		}

		public boolean shouldExecute() {
			List<LivingEntity> list = this.stinkbug.world.getEntitiesWithinAABB(LivingEntity.class, this.stinkbug.getBoundingBox().grow(2.0D), StinkBugEntity.ENEMY_MATCHER);
			return !list.isEmpty();
		}

		public void startExecuting() {
			this.stinkbug.awakenTimer = 0;
		}

		public void tick() {
			stinkbug.awakenTimer++;
		}
		
		public boolean shouldContinueExecuting() {
			if (stinkbug.awakenTimer > 50) {
				return false;
			} else {
				return this.shouldExecute();
			}
		}
		
		public void resetTask() {
			this.stinkbug.awakenTimer = 0;
		}
	}
}	
