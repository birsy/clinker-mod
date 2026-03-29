package birsy.clinker.common.world.entity.gnomad.gnomind.sensors;

import birsy.clinker.common.world.entity.gnomad.gnomind.LastKnownEntityPosition;
import birsy.clinker.common.world.entity.gnomad.gnomind.LastKnownEntityPositionsTracker;
import birsy.clinker.core.registry.entity.ClinkerSensors;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.NearestVisibleLivingEntities;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.tslat.smartbrainlib.api.core.sensor.ExtendedSensor;
import net.tslat.smartbrainlib.util.BrainUtils;
import org.apache.logging.log4j.util.TriConsumer;

import java.util.List;
import java.util.function.BiPredicate;

public abstract class LastKnownEntityPositionsSensor<E extends LivingEntity> extends ExtendedSensor<E> {
    private static final int SCAN_INTERVAL = 2;
    BiPredicate<E, LivingEntity> predicate = (me, entity) -> true;
    TriConsumer<E, LivingEntity, LastKnownEntityPosition.State> unseenCallback = (me, entity, state) -> {};

    int uncertaintyTime = 5 * 20, // default value: 5 seconds
        forgetTime = 3 * 60 * 20; // default value: 3 minutes

    public LastKnownEntityPositionsSensor() {
        this.setScanRate(entity -> SCAN_INTERVAL);
    }

    abstract MemoryModuleType<LastKnownEntityPositionsTracker> memoryType();

    @Override
    public List<MemoryModuleType<?>> memoriesUsed() {
        return List.of(memoryType());
    }

    public LastKnownEntityPositionsSensor<E> predicate(BiPredicate<E, LivingEntity> predicate) {
        this.predicate = predicate;
        return this;
    }
    public LastKnownEntityPositionsSensor<E> unseenCallback(TriConsumer<E, LivingEntity, LastKnownEntityPosition.State> unseenCallback) {
        this.unseenCallback = unseenCallback;
        return this;
    }
    public LastKnownEntityPositionsSensor<E> forgetTime(int time) {
        this.forgetTime = time;
        return this;
    }
    public LastKnownEntityPositionsSensor<E> uncertaintyTime(int time) {
        this.uncertaintyTime = time;
        return this;
    }

    @Override
    protected void doTick(ServerLevel level, E me) {
        LastKnownEntityPositionsTracker lastKnownEntityPositions = BrainUtils.getMemory(me, memoryType());
        if (lastKnownEntityPositions == null) {
            lastKnownEntityPositions = new LastKnownEntityPositionsTracker()
                    .forgetTime(forgetTime)
                    .uncertaintyTime(uncertaintyTime);
            BrainUtils.setMemory(me, memoryType(), lastKnownEntityPositions);
        }

        NearestVisibleLivingEntities nearbyEntities = BrainUtils.getMemory(me, MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES);
        if (nearbyEntities != null) {
            for (LivingEntity nearbyEntity : nearbyEntities.findAll((otherEntity) -> this.predicate.test(me, otherEntity))) {
                LastKnownEntityPosition.State lastState = lastKnownEntityPositions.updateTracking(level, nearbyEntity);
                // only run the callback for entities we don't already know about
                if (lastState != LastKnownEntityPosition.State.KNOWN) this.unseenCallback.accept(me, nearbyEntity, lastState);
            }
        }

        lastKnownEntityPositions.update(level);
    }
}
