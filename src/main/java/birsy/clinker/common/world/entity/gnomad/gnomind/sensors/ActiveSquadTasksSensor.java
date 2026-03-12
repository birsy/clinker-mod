package birsy.clinker.common.world.entity.gnomad.gnomind.sensors;

import birsy.clinker.common.world.entity.gnomad.gnomind.squad.Squad;
import birsy.clinker.common.world.entity.gnomad.gnomind.squad.SquadMember;
import birsy.clinker.common.world.entity.gnomad.gnomind.squad.SquadTask;
import birsy.clinker.core.registry.entity.ClinkerMemoryModules;
import birsy.clinker.core.registry.entity.ClinkerSensors;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Unit;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.tslat.smartbrainlib.api.core.sensor.ExtendedSensor;
import net.tslat.smartbrainlib.util.BrainUtils;

import java.util.List;

public class ActiveSquadTasksSensor<E extends LivingEntity & SquadMember<?>> extends ExtendedSensor<E> {
    private static final List<MemoryModuleType<?>> MEMORIES = ObjectArrayList.of(
            ClinkerMemoryModules.CURRENTLY_ASSIGNED_SQUAD_TASK.get(),
            ClinkerMemoryModules.IS_CURRENTLY_ASSIGNED_SQUAD_TASK_ACTIVE.get()
    );

    public ActiveSquadTasksSensor() {
        this.setScanRate(entity -> 1);
    }

    @Override
    public List<MemoryModuleType<?>> memoriesUsed() {
        return MEMORIES;
    }

    @Override
    public SensorType<? extends ExtendedSensor<?>> type() {
        return ClinkerSensors.ACTIVE_SQUAD_TASK.get();
    }

    @Override
    protected void doTick(ServerLevel level, E entity) {
        if (!BrainUtils.hasMemory(entity, ClinkerMemoryModules.SQUAD.get())) {
            BrainUtils.clearMemory(entity, ClinkerMemoryModules.CURRENTLY_ASSIGNED_SQUAD_TASK.get());
            BrainUtils.clearMemory(entity, ClinkerMemoryModules.IS_CURRENTLY_ASSIGNED_SQUAD_TASK_ACTIVE.get());
            return;
        }

        SquadTask currentSquadTask = BrainUtils.getMemory(entity, ClinkerMemoryModules.CURRENTLY_ASSIGNED_SQUAD_TASK.get());
        if (currentSquadTask != null && currentSquadTask.isFinished()) {
            // stop doing tasks that are already done
            BrainUtils.clearMemory(entity, ClinkerMemoryModules.CURRENTLY_ASSIGNED_SQUAD_TASK.get());
            BrainUtils.clearMemory(entity, ClinkerMemoryModules.IS_CURRENTLY_ASSIGNED_SQUAD_TASK_ACTIVE.get());
        } else if (currentSquadTask != null && currentSquadTask.isActive()) {
            // if our task is active then activate the marker memory
            BrainUtils.setMemory(entity, ClinkerMemoryModules.IS_CURRENTLY_ASSIGNED_SQUAD_TASK_ACTIVE.get(), Unit.INSTANCE);
        }
    }
}
