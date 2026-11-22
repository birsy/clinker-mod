package birsy.clinker.common.entity.lungwyrm.sensors;

import birsy.clinker.common.entity.ai.memories.EnemyTracker;
import birsy.clinker.common.entity.lungwyrm.LungWyrmEntity;
import birsy.clinker.core.registry.entity.ClinkerMemoryModules;
import birsy.clinker.core.registry.entity.ClinkerSensors;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.GlobalPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.tslat.smartbrainlib.api.core.sensor.ExtendedSensor;
import net.tslat.smartbrainlib.util.BrainUtils;
import net.tslat.smartbrainlib.util.EntityRetrievalUtil;

import java.util.List;

public class LungWyrmTargetsSensor extends ExtendedSensor<LungWyrmEntity> {
    private static final List<MemoryModuleType<?>> MEMORIES = ObjectArrayList.of(
            MemoryModuleType.HOME,
            ClinkerMemoryModules.TRACKED_ENEMIES.get()
    );
    long maximumAggressionTime = 3 * 60 * 20;

    @Override
    public List<MemoryModuleType<?>> memoriesUsed() {
        return MEMORIES;
    }

    @Override
    protected void doTick(ServerLevel level, LungWyrmEntity entity) {
        super.doTick(level, entity);

        EnemyTracker enemyTracker = BrainUtils.getMemory(entity, ClinkerMemoryModules.TRACKED_ENEMIES.get());
        if (enemyTracker == null) enemyTracker = new EnemyTracker(entity);

        long currentTime = level.getGameTime();

        enemyTracker.filter(maximumAggressionTime, currentTime);

        // attack whoever hurt us last
        DamageSource damageSource = entity.getLastDamageSource();
        if (damageSource != null && damageSource.getEntity() instanceof LivingEntity livingEntity) {
            enemyTracker.track(livingEntity, currentTime);
        }

        // attack whoever gets near our nest
        GlobalPos nestPos = BrainUtils.getMemory(entity, MemoryModuleType.HOME);
        if (nestPos != null && level.dimension() == nestPos.dimension()) {
            List<LivingEntity> entitiesNearNest = EntityRetrievalUtil.getEntities(level, nestPos.pos().getCenter(), 50, LivingEntity.class);
            for (LivingEntity livingEntity : entitiesNearNest) enemyTracker.track(livingEntity, currentTime);
        }

        if (enemyTracker.isEmpty()) BrainUtils.clearMemory(entity, ClinkerMemoryModules.TRACKED_ENEMIES.get());
        else BrainUtils.setMemory(entity, ClinkerMemoryModules.TRACKED_ENEMIES.get(), enemyTracker);
    }

    @Override
    public SensorType<? extends ExtendedSensor<?>> type() {
        return ClinkerSensors.ENEMIES_NEAR_HOME.get();
    }
}
