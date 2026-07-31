package birsy.clinker.common.entity.gnomad.gnomind.sensors;

import birsy.clinker.common.entity.system.squad.SquadMember;
import birsy.clinker.core.registry.entity.ClinkerMemoryModules;
import birsy.clinker.core.registry.entity.ClinkerSensors;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.tslat.smartbrainlib.api.core.sensor.ExtendedSensor;
import net.tslat.smartbrainlib.util.BrainUtils;

import java.util.List;

public class SquadSensor<E extends LivingEntity & SquadMember> extends ExtendedSensor<E> {
    private static final List<MemoryModuleType<?>> MEMORIES = ObjectArrayList.of(ClinkerMemoryModules.SQUAD.get());

    public SquadSensor() {
        this.setScanRate(entity -> 1);
    }

    @Override
    public List<MemoryModuleType<?>> memoriesUsed() {
        return MEMORIES;
    }

    @Override
    public SensorType<? extends ExtendedSensor<?>> type() {
        return ClinkerSensors.GNOMAD_SQUAD.get();
    }

    @Override
    protected void doTick(ServerLevel level, E entity) {
        if (entity.getSquad() == null) {
            BrainUtils.clearMemory(entity, ClinkerMemoryModules.SQUAD.get());
        } else {
            BrainUtils.setMemory(entity, ClinkerMemoryModules.SQUAD.get(), entity.getSquad());
        }
    }
}
