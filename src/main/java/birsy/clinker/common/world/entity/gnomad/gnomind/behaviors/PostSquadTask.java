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

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class PostSquadTask<E extends LivingEntity & SquadMember<E>, T extends SquadTask> extends ExtendedBehaviour<E> {
    private static final MemoryTest MEMORY_REQUIREMENTS = MemoryTest.builder(2)
                    .hasMemory(ClinkerMemoryModules.SQUAD.get())
                    .usesMemory(ClinkerMemoryModules.SQUAD_TASKS_I_HAVE_POSTED.get());
    final Class<T> taskClass;
    final Function<E, T> taskFactory;

    public PostSquadTask(Class<T> taskClass, Function<E, T> taskFactory) {
        this.taskClass = taskClass;
        this.taskFactory = taskFactory;
    }

    @Override
    protected List<Pair<MemoryModuleType<?>, MemoryStatus>> getMemoryRequirements() {
        return MEMORY_REQUIREMENTS;
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel level, E mob) {
        // ensure we haven't posted this squad task recently
        List<SquadTask> postedTasks = BrainUtils.getMemory(mob, ClinkerMemoryModules.SQUAD_TASKS_I_HAVE_POSTED.get());
        if (postedTasks == null) postedTasks = List.of();
        return postedTasks.stream().noneMatch(taskClass::isInstance);
    }

    @Override
    protected void start(E mob) {
        T newTask = taskFactory.apply(mob);
        Squad squad = BrainUtils.getMemory(mob, ClinkerMemoryModules.SQUAD.get());
        squad.addTask(newTask);

        // update the current posted tasks memory so no weirdness happens.
        List<SquadTask> postedTasks = BrainUtils.getMemory(mob, ClinkerMemoryModules.SQUAD_TASKS_I_HAVE_POSTED.get());
        if (postedTasks == null) postedTasks = List.of();
        List<SquadTask> newPostedTasks = new ArrayList<>(postedTasks);
        newPostedTasks.add(newTask);
        BrainUtils.setMemory(mob, ClinkerMemoryModules.SQUAD_TASKS_I_HAVE_POSTED.get(), newPostedTasks);
        Clinker.LOGGER.info("{} just posted task {} to squad {}", DebugEntityNameGenerator.getEntityName(mob.getUUID()), newTask, squad.uuid);
    }
}
