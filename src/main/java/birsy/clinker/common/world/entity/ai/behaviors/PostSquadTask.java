package birsy.clinker.common.world.entity.ai.behaviors;

import birsy.clinker.common.world.entity.system.squad.Squad;
import birsy.clinker.common.world.entity.system.squad.SquadMember;
import birsy.clinker.common.world.entity.system.squad.SquadTask;
import birsy.clinker.core.registry.entity.ClinkerMemoryModules;
import com.mojang.datafixers.util.Pair;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.tslat.smartbrainlib.api.core.behaviour.ExtendedBehaviour;
import net.tslat.smartbrainlib.object.MemoryTest;
import net.tslat.smartbrainlib.util.BrainUtils;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class PostSquadTask<E extends LivingEntity & SquadMember<E>, T extends SquadTask> extends ExtendedBehaviour<E> {
    private static final MemoryTest MEMORY_REQUIREMENTS = MemoryTest.builder(2)
                    .hasMemory(ClinkerMemoryModules.SQUAD.get())
                    .usesMemory(ClinkerMemoryModules.POSTED_SQUAD_TASKS.get());
    final Class<T> taskClass;
    final Function<E, @Nullable T> taskFactory;
    protected BiConsumer<E, T> onStart = (e, t) -> {};
    protected T task;

    public PostSquadTask(Class<T> taskClass, Function<E, @Nullable T> taskFactory) {
        this.taskClass = taskClass;
        this.taskFactory = taskFactory;
    }

    public PostSquadTask<E, T> onStart(BiConsumer<E, T> callback) {
        this.onStart = callback;
        return this;
    }

    @Override
    protected List<Pair<MemoryModuleType<?>, MemoryStatus>> getMemoryRequirements() {
        return MEMORY_REQUIREMENTS;
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel level, E mob) {
        // ensure we haven't posted this squad task recently
        Set<SquadTask> postedTasks = BrainUtils.memoryOrDefault(mob, ClinkerMemoryModules.POSTED_SQUAD_TASKS.get(), Set::of);
        for (SquadTask postedTask : postedTasks)
            if (taskClass.isInstance(postedTask)) return false;

        T newTask = taskFactory.apply(mob);
        if (newTask == null) return false;

        task = newTask;
        return true;
    }

    @Override
    protected void start(E mob) {
        Squad squad = BrainUtils.getMemory(mob, ClinkerMemoryModules.SQUAD.get());
        if (squad == null) return;
        squad.addTask(task);
        onStart.accept(mob, task);
    }
}
