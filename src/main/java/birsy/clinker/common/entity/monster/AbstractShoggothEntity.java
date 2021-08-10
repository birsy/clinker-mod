package birsy.clinker.common.entity.monster;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public abstract class AbstractShoggothEntity extends Monster
{
	@SuppressWarnings("unused")
	public ShoggothHeadEntity parent;

	public AbstractShoggothEntity(EntityType<? extends Monster> type, Level worldIn) {
		super(type, worldIn);
	}

	class FollowLeaderGoal extends Goal {
		private final AbstractShoggothEntity child;
		private AbstractShoggothEntity parent;
		private int circleTicks;
		private final double moveSpeed;

		public FollowLeaderGoal(AbstractShoggothEntity mob, double speed) {
			this.child = mob;
			this.parent = mob.parent;
			this.moveSpeed = speed;
		}

		@Override
		public boolean canUse() {
			return child.parent != null;
		}

		@Override
		public void tick() {
			if(child.distanceTo(parent) < 0.5) {
				this.circleTicks++;
				circleEntity(parent, 7, 0.3f, true, circleTicks, 0, 1.75f);
			} else {
				this.circleTicks = 0;
				this.child.getNavigation().moveTo(parent, moveSpeed);
			}
		}

		@Override
		public boolean canContinueToUse() {
			return canUse();
		}

		@Override
		public void stop() {
			this.circleTicks = 0;
			super.stop();
		}
	}

	public <T extends AbstractShoggothEntity> AbstractShoggothEntity findClosestParent(AbstractShoggothEntity child, Class<? extends T> parentType) {
		List<AbstractShoggothEntity> nearbyParents = child.level.getEntitiesOfClass(parentType, child.getBoundingBox().inflate(10));
		AtomicReference<AbstractShoggothEntity> closestParent = null;

		if (nearbyParents.isEmpty()) {
			return null;
		} else {
			nearbyParents.forEach((AbstractShoggothEntity) -> {
				if (closestParent.get() == null) {
					closestParent.set(AbstractShoggothEntity);
				} else if (closestParent.get().distanceTo(child) > AbstractShoggothEntity.distanceTo(child)) {
					closestParent.set(AbstractShoggothEntity);
				}
			});
			return closestParent.get();
		}
	}

	public void setParent(ShoggothHeadEntity parent) {
		this.parent = parent;
	}

	public void circleEntity(Entity entityIn, float radius, float speed, boolean direction, int circleFrame, float offset, float moveSpeed) {
		int directionInt = direction ? 1 : -1;
		double circleMovement = directionInt * circleFrame * 0.5 * speed / radius + offset;
		this.getNavigation().moveTo(entityIn.getX() + radius * Math.cos(circleMovement), entityIn.getY(), entityIn.getZ() + radius * Math.sin(circleMovement), speed * moveSpeed);
	}
}
