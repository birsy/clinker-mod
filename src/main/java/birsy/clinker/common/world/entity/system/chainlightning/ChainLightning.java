package birsy.clinker.common.world.entity.system.chainlightning;

import birsy.clinker.client.particle.ChainLightningBoltParticle;
import net.minecraft.Util;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.FastColor;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.tslat.smartbrainlib.util.EntityRetrievalUtil;

import java.util.*;
import java.util.function.BiConsumer;

public class ChainLightning {
    public static final int CONDUCTION_DELAY = 25;

    final UUID id;
    final ServerLevel level;
    final BiConsumer<ChainLightning, Entity> damageCallback;
    final Set<Entity> visitedEntities = new HashSet<>();
    final Map<Entity, Integer> pendingEntities = new HashMap<>();
    boolean markedForRemoval = false;

    ChainLightning(UUID id, ServerLevel level) {
        this(id, level, ChainLightning::damage);
    }

    ChainLightning(UUID id, ServerLevel level, BiConsumer<ChainLightning, Entity> damageCallback) {
        this.id = id;
        this.level = level;
        this.damageCallback = damageCallback;
    }

    public void shock(Entity entity) {
        if (visitedEntities.contains(entity)) return;
        addEntity(entity, 0);
    }

    void addEntity(Entity entity, int travelTime) {
        pendingEntities.put(entity, travelTime);
        visitedEntities.add(entity);
    }

    void tick() {
        if (markedForRemoval) return;

        // decrement counters
        pendingEntities.replaceAll((entity, ticks) -> ticks - 1);

        // if an entity has 0 left on their counter, remove them from
        // pending entities set and make them shock
        Set<Entity> shockers = new HashSet<>(4);
        pendingEntities.entrySet().removeIf(entry -> {
            Entity entity = entry.getKey();
            if (entry.getValue() > 0) return false;
            // entity is invalid, skip
            if ( entity.isRemoved() || entity.isSpectator() ||
                !entity.isAddedToLevel() || entity.level() != level ||
                (entity instanceof LivingEntity living && living.isDeadOrDying()))
                return true;

            shockers.add(entity);
            return true;
        });

        for (Entity shocker : shockers) {
            damageCallback.accept(this, shocker);

            double conductionRadius = getConductionRadius(shocker);
            if (conductionRadius <= 0.01) continue;

            List<Entity> nearbyEntities = EntityRetrievalUtil.getEntities(shocker, conductionRadius, e -> !visitedEntities.contains(e));

            int maximumShockableNeighbors = 3;
            if (nearbyEntities.size() > maximumShockableNeighbors) Util.shuffle(nearbyEntities, level.getRandom());
            int entitiesToIterate = Math.min(maximumShockableNeighbors, nearbyEntities.size());

            for (int i = 0; i < entitiesToIterate; i++) {
                Entity nearby = nearbyEntities.get(i);
                int travelTime = 5 + level.random.nextIntBetweenInclusive(-3, 3);
                addEntity(nearby, travelTime);
                spawnBoltParticle(
                        shocker.getX(), shocker.getY(0.5), shocker.getZ(),
                        nearby.getX(), nearby.getY(0.5), nearby.getZ(),
                        travelTime
                );
            }
        }

        if (pendingEntities.isEmpty())
            markedForRemoval = true;
    }

    // todo: chain lightning apply effects, damage immunity, etc
    public void damage(Entity entity) {
        entity.hurt(entity.damageSources().lightningBolt(), 2.0F);
    }

    // todo: fill this in w/ entity conductivity affecting radius
    public double getConductionRadius(Entity entity) {
        return 5.0;
    }

    private void spawnBoltParticle(double x1, double y1, double z1,
                                   double x2, double y2, double z2,
                                   int travelTicks) {
        double x = (x1 + x2) * 0.5, y = (y1 + y2) * 0.5, z = (z1 + z2) * 0.5;
        level.sendParticles(
                new ChainLightningBoltParticle.Options(x1, y1, z1, x2, y2, z2, FastColor.ARGB32.colorFromFloat(0.2F, 1.0F, 1.0F, 1.0F), travelTicks),
                x, y, z, 1, 0, 0, 0, 0);
    }
}
