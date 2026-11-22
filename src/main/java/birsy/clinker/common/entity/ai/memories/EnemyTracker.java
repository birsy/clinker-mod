package birsy.clinker.common.entity.ai.memories;

import net.minecraft.world.entity.LivingEntity;
import net.tslat.smartbrainlib.util.SensoryUtils;

import java.util.*;

public class EnemyTracker {
    final LivingEntity tracker;
    final Set<TrackedEnemy> enemies = new HashSet<>();

    public EnemyTracker(LivingEntity tracker) {
        this.tracker = tracker;
    }

    public boolean isValid(LivingEntity entity) {
        return SensoryUtils.isEntityAttackableIgnoringLineOfSight(tracker, entity);
    }

    public void filter(long currentTime, long maximumTime) {
        this.enemies.removeIf(enemy -> !isValid(enemy.entity) || currentTime - enemy.lastSeenTimestamp > maximumTime);
    }

    public void track(LivingEntity entity, long currentTime) {
        if (isValid(entity))
            this.enemies.add(new TrackedEnemy(entity, currentTime));
    }

    public List<LivingEntity> enemies() {
        return enemies.stream().map(TrackedEnemy::entity).toList();
    }

    public boolean isEmpty() {
        return enemies.isEmpty();
    }

    record TrackedEnemy(LivingEntity entity, long lastSeenTimestamp) {
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            TrackedEnemy that = (TrackedEnemy) o;
            return Objects.equals(entity, that.entity);
        }
        @Override
        public int hashCode() {
            return entity.hashCode();
        }
    }
}
