package birsy.clinker.common.entity.monster.beetle;

import java.util.List;
import java.util.function.Predicate;

import net.minecraft.entity.CreatureAttribute;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.MoveTowardsTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.level.Level;

@SuppressWarnings("unused")
public class StinkBugEntity extends Monster {
	public static final EntityDataAccessor<Boolean> SLEEPING = SynchedEntityData.defineId(StinkBugEntity.class, EntityDataSerializers.BOOLEAN);
	private int awakenTimer;
	
	private static final Predicate<LivingEntity> ENEMY_MATCHER = (entity) -> {
		if (entity == null) {
			return false;
		} else if (!(entity instanceof Player) || !entity.isSpectator() && !((Player)entity).isCreative()) {
			return true;
		} else if ((entity instanceof PathfinderMob) && !(entity instanceof StinkBugEntity)){
			return true;
		} else {
			return false;
		}
	};
	
	public StinkBugEntity(EntityType<? extends Monster> type, Level worldIn) {
		super(type, worldIn);
	}
	
	protected void registerGoals() {
		this.goalSelector.addGoal(1, new StinkBugEntity.AwakenGoal(this));
		this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.0D, true));
		this.goalSelector.addGoal(3, new MoveTowardsTargetGoal(this, 0.9D, 32.0F));
		this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 6.0F));
		this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
		this.targetSelector.addGoal(2, new HurtByTargetGoal(this));
		this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Player.class, true));
	}
	
	public static AttributeSupplier.Builder setCustomAttributes()
	{
		return Mob.createMobAttributes()
				.add(Attributes.MAX_HEALTH, 17.0)
				.add(Attributes.MOVEMENT_SPEED, 0.25D)
				.add(Attributes.ATTACK_DAMAGE, 3.0D);
	}
	
	static class AwakenGoal extends Goal {
		private final StinkBugEntity stinkbug;

		public AwakenGoal(StinkBugEntity stinkbug) {
			this.stinkbug = stinkbug;
		}

		public boolean canUse() {
			List<LivingEntity> list = this.stinkbug.level.getEntitiesOfClass(LivingEntity.class, this.stinkbug.getBoundingBox().inflate(2.0D), StinkBugEntity.ENEMY_MATCHER);
			return !list.isEmpty();
		}

		public void start() {
			this.stinkbug.awakenTimer = 0;
		}

		public void tick() {
			stinkbug.awakenTimer++;
		}
		
		public boolean canContinueToUse() {
			if (stinkbug.awakenTimer > 50) {
				return false;
			} else {
				return this.canUse();
			}
		}
		
		public void stop() {
			this.stinkbug.awakenTimer = 0;
		}
	}
}	
