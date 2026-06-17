package birsy.clinker.common.world.entity.gnomad.gnomind.behaviors;

import birsy.clinker.common.world.entity.system.squad.Squad;
import birsy.clinker.common.world.entity.system.squad.SquadMember;
import birsy.clinker.common.world.entity.system.squad.SquadTask;
import birsy.clinker.core.Clinker;
import birsy.clinker.core.registry.entity.ClinkerMemoryModules;
import com.mojang.datafixers.util.Pair;
import net.minecraft.network.protocol.game.DebugEntityNameGenerator;
import net.minecraft.server.level.ServerLevel;
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
    private static final MemoryTest MEMORY_REQUIREMENTS = MemoryTest.builder(2)
            .hasMemory(ClinkerMemoryModules.SQUAD.get())
            .usesMemory(ClinkerMemoryModules.ASSIGNED_SQUAD_TASK.get());
    final Predicate<SquadTask> filter;
    SquadTask task;

    public ClaimSquadTask(Predicate<SquadTask> filter) {
        this.filter = filter;
    }

    @Override
    protected List<Pair<MemoryModuleType<?>, MemoryStatus>> getMemoryRequirements() { return MEMORY_REQUIREMENTS; }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel level, E mob) {
        Squad squad = BrainUtils.getMemory(mob, ClinkerMemoryModules.SQUAD.get());
        Optional<SquadTask> task = squad.findTask(mob, filter);
        if (task.isEmpty()) return false;

        // if we're already waiting on a task and that task has a higher priority, then don't claim this one
        SquadTask taskInBrain = BrainUtils.getMemory(mob, ClinkerMemoryModules.ASSIGNED_SQUAD_TASK.get());
        if (taskInBrain != null && taskInBrain.priority >= task.get().priority) return false;

        this.task = task.get();
        return true;
    }

    @Override
    protected void start(E mob) {
        // leave whatever task we had before
        SquadTask taskInBrain = BrainUtils.getMemory(mob, ClinkerMemoryModules.ASSIGNED_SQUAD_TASK.get());
        if (taskInBrain != null) taskInBrain.unassign(mob);

        // assign us to the new task
        task.assign(mob);
        Clinker.LOGGER.info("{} claimed task {}!", DebugEntityNameGenerator.getEntityName(mob.getUUID()), task);
    }
}
