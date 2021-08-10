package birsy.clinker.common.entity.monster;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.world.World;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public abstract class AbstractShoggothEntity extends MonsterEntity
{
	@SuppressWarnings("unused")
	public ShoggothHeadEntity parent;

	public AbstractShoggothEntity(EntityType<? extends MonsterEntity> type, World worldIn) {
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
		public boolean shouldExecute() {
			return child.parent != null;
		}

		@Override
		public void tick() {
			if(child.getDistance(parent) < 0.5) {
				this.circleTicks++;
				circleEntity(parent, 7, 0.3f, true, circleTicks, 0, 1.75f);
			} else {
				this.circleTicks = 0;
				this.child.getNavigator().tryMoveToEntityLiving(parent, moveSpeed);
			}
		}

		@Override
		public boolean shouldContinueExecuting() {
			return shouldExecute();
		}

		@Override
		public void resetTask() {
			this.circleTicks = 0;
			super.resetTask();
		}
	}

	public <T extends AbstractShoggothEntity> AbstractShoggothEntity findClosestParent(AbstractShoggothEntity child, Class<? extends T> parentType) {
		List<AbstractShoggothEntity> nearbyParents = child.world.getEntitiesWithinAABB(parentType, child.getBoundingBox().grow(10));
		AtomicReference<AbstractShoggothEntity> closestParent = null;

		if (nearbyParents.isEmpty()) {
			return null;
		} else {
			nearbyParents.forEach((AbstractShoggothEntity) -> {
				if (closestParent.get() == null) {
					closestParent.set(AbstractShoggothEntity);
				} else if (closestParent.get().getDistance(child) > AbstractShoggothEntity.getDistance(child)) {
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
		this.getNavigator().tryMoveToXYZ(entityIn.getPosX() + radius * Math.cos(circleMovement), entityIn.getPosY(), entityIn.getPosZ() + radius * Math.sin(circleMovement), speed * moveSpeed);
	}
}
