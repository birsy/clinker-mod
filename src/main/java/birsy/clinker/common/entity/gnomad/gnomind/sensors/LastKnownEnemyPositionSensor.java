package birsy.clinker.common.entity.gnomad.gnomind.sensors;

import birsy.clinker.common.entity.gnomad.gnomind.LastKnownEntityPositionsTracker;
import birsy.clinker.common.entity.system.squad.SquadMember;
import birsy.clinker.core.registry.entity.ClinkerMemoryModules;
import birsy.clinker.core.registry.entity.ClinkerSensors;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.tslat.smartbrainlib.api.core.sensor.ExtendedSensor;

public class LastKnownEnemyPositionSensor<E extends LivingEntity & SquadMember<E>> extends LastKnownEntityPositionsSensor<E> {
    public LastKnownEnemyPositionSensor() {
        super();
        this.predicate(this::shouldTrack);
    }

    boolean shouldTrack(E me, LivingEntity other) {
        if (other instanceof SquadMember<?> otherSquadMember) return me.getSquad() != otherSquadMember.getSquad();
        return true;
    }

    @Override
    MemoryModuleType<LastKnownEntityPositionsTracker> memoryType() {
        return ClinkerMemoryModules.LAST_KNOWN_ENEMY_POSITIONS.get();
    }

    @Override
    public SensorType<? extends ExtendedSensor<?>> type() {
        return ClinkerSensors.LAST_KNOWN_ENEMY_POSITIONS.get();
    }
}
