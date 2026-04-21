package birsy.clinker.common.world.entity.system.chainlightning;

import birsy.clinker.client.particle.ChainLightningParticle;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
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
        damageCallback.accept(this, entity);
        visitedEntities.add(entity);
        pendingEntities.put(entity, 10 + level.random.nextIntBetweenInclusive(-5, 5));
    }

    void tick() {
        if (markedForRemoval) return;

        // decrement counters and try to shock neighbors!
        Set<Entity> entitiesToShock = new HashSet<>(4);
        pendingEntities.replaceAll((entity, ticks) -> ticks - 1);
        pendingEntities.entrySet().removeIf(entry -> {
            Entity entity = entry.getKey();
            if (entry.getValue() > 0) return false;

            if  (entity.isRemoved() || entity.isSpectator() ||
                !entity.isAddedToLevel() || entity.level() != level ||
                (entity instanceof LivingEntity living && living.isDeadOrDying())) return true;

            double conductionRadius = getConductionRadius(entity);
            if (conductionRadius <= 0.01) return true;

            List<Entity> nearbyEntities = EntityRetrievalUtil.getEntities(entity, conductionRadius, e -> !visitedEntities.contains(e));
            nearbyEntities.sort(Comparator.comparingDouble(e -> e.distanceTo(entity)));

            // we can shock a maximum of five neighbors.
            int entitiesToIterate = Math.min(5, nearbyEntities.size());
            for (int i = 0; i < entitiesToIterate; i++) {
                Entity nearby = nearbyEntities.get(i);
                entitiesToShock.add(nearby);
                spawnBoltParticle(
                        entity.getX(), entity.getY() + entity.getBbHeight() * 0.5, entity.getZ(),
                        nearby.getX(), nearby.getY() + nearby.getBbHeight() * 0.5, nearby.getZ()
                );
            }
            return true;
        });

        for (Entity entityToShock : entitiesToShock) shock(entityToShock);

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
                                   double x2, double y2, double z2) {
        double distance = Mth.length(x1 - x2, y1 - y2, z1 - z2);
        double particleCount = distance * 3;
        for (int i = 0; i < particleCount; i++) {
            double factor = i / particleCount;
            double x = Mth.lerp(factor, x1, x2), y = Mth.lerp(factor, y1, y2), z = Mth.lerp(factor, z1, z2);
            level.sendParticles(
                    ParticleTypes.END_ROD,
                x, y, z, 1, 0, 0, 0, 0);
        }
//
//        double x = (x1 + x2) * 0.5, y = (y1 + y2) * 0.5, z = (z1 + z2) * 0.5;
//        level.sendParticles(
//                new ChainLightningParticle.Options(x1, y1, z1, x2, y2, z2),
//                x, y, z, 1, 0, 0, 0, 0);
    }
}
