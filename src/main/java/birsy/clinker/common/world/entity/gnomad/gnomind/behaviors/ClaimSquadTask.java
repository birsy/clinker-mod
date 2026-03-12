package birsy.clinker.common.world.entity.gnomad.gnomind.behaviors;

import birsy.clinker.common.world.entity.gnomad.gnomind.squad.Squad;
import birsy.clinker.common.world.entity.gnomad.gnomind.squad.SquadMember;
import birsy.clinker.common.world.entity.gnomad.gnomind.squad.SquadTask;
import birsy.clinker.core.Clinker;
import birsy.clinker.core.registry.entity.ClinkerMemoryModules;
import com.mojang.datafixers.util.Pair;
import net.minecraft.network.protocol.game.DebugEntityNameGenerator;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Unit;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.tslat.smartbrainlib.api.core.behaviour.ExtendedBehaviour;
import net.tslat.smartbrainlib.object.MemoryTest;
import net.tslat.smartbrainlib.util.BrainUtils;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public class ClaimSquadTask<E extends LivingEntity & SquadMember<E>> extends ExtendedBehaviour<E> {
    private static final MemoryTest MEMORY_REQUIREMENTS = MemoryTest.builder(3)
            .hasMemory(ClinkerMemoryModules.SQUAD.get())
            .usesMemory(ClinkerMemoryModules.CURRENTLY_ASSIGNED_SQUAD_TASK.get())
            .noMemory(ClinkerMemoryModules.IS_CURRENTLY_ASSIGNED_SQUAD_TASK_ACTIVE.get());
    Predicate<SquadTask> filter;
    SquadTask currentTask;

    @Override
    protected List<Pair<MemoryModuleType<?>, MemoryStatus>> getMemoryRequirements() { return MEMORY_REQUIREMENTS; }
    public ClaimSquadTask<E> of(Predicate<SquadTask> task) {
        this.filter = task;
        return this;
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel level, E mob) {
        Squad squad = BrainUtils.getMemory(mob, ClinkerMemoryModules.SQUAD.get());
        Optional<SquadTask> task = squad.findTask(mob, filter);
        if (task.isEmpty()) return false;

        // if we're already waiting on a task and that task has a higher priority, then don't claim this one
        SquadTask taskInBrain = BrainUtils.getMemory(mob, ClinkerMemoryModules.CURRENTLY_ASSIGNED_SQUAD_TASK.get());
        if (taskInBrain != null && taskInBrain.priority >= task.get().priority) return false;

        currentTask = task.get();
        return true;
    }

    @Override
    protected void start(E mob) {
        // leave whatever task we had before
        SquadTask taskInBrain = BrainUtils.getMemory(mob, ClinkerMemoryModules.CURRENTLY_ASSIGNED_SQUAD_TASK.get());
        if (taskInBrain != null) taskInBrain.unassign(mob);

        // assign us to the new task
        currentTask.assign(mob);
        BrainUtils.setMemory(mob, ClinkerMemoryModules.CURRENTLY_ASSIGNED_SQUAD_TASK.get(), currentTask);
        if (currentTask.isActive()) {
            BrainUtils.setMemory(mob, ClinkerMemoryModules.IS_CURRENTLY_ASSIGNED_SQUAD_TASK_ACTIVE.get(), Unit.INSTANCE);
        } else {
            BrainUtils.clearMemory(mob, ClinkerMemoryModules.IS_CURRENTLY_ASSIGNED_SQUAD_TASK_ACTIVE.get());
        }
        Clinker.LOGGER.info("{} claimed task {}!", DebugEntityNameGenerator.getEntityName(mob.getUUID()), currentTask);
    }
}
