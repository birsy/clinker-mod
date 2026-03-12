package birsy.clinker.common.world.entity.gnomad.gnomind.sensors;

import birsy.clinker.common.world.entity.gnomad.gnomind.squad.Squad;
import birsy.clinker.common.world.entity.gnomad.gnomind.squad.SquadMember;
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

public class PostedSquadTasksSensor<E extends LivingEntity & SquadMember<?>> extends ExtendedSensor<E> {
    private static final List<MemoryModuleType<?>> MEMORIES = ObjectArrayList.of(
            ClinkerMemoryModules.SQUAD_TASKS_I_HAVE_POSTED.get()
    );

    public PostedSquadTasksSensor() {
        this.setScanRate(entity -> 20);
    }

    @Override
    public List<MemoryModuleType<?>> memoriesUsed() {
        return MEMORIES;
    }

    @Override
    public SensorType<? extends ExtendedSensor<?>> type() {
        return ClinkerSensors.POSTED_SQUAD_TASKS.get();
    }

    @Override
    protected void doTick(ServerLevel level, E entity) {
        Squad squad = BrainUtils.getMemory(entity, ClinkerMemoryModules.SQUAD.get());
        if (squad == null) {
            BrainUtils.setMemory(entity, ClinkerMemoryModules.SQUAD_TASKS_I_HAVE_POSTED.get(), List.of());
        } else {
            BrainUtils.setMemory(entity, ClinkerMemoryModules.SQUAD_TASKS_I_HAVE_POSTED.get(), squad.getTasksPostedBy(entity));
        }
    }
}
