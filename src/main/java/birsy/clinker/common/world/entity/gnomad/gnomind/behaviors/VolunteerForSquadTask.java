package birsy.clinker.common.world.entity.gnomad.gnomind.behaviors;

import birsy.clinker.common.world.entity.gnomad.GnomadEntity;
import birsy.clinker.common.world.entity.gnomad.gnomind.squad.squadtasks.GnomadSquadTask;
import birsy.clinker.core.Clinker;
import birsy.clinker.core.registry.entity.ClinkerMemoryModules;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.network.protocol.game.DebugEntityNameGenerator;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.tslat.smartbrainlib.api.core.behaviour.ExtendedBehaviour;

import java.util.List;
import java.util.function.BiPredicate;

public class VolunteerForSquadTask<E extends GnomadEntity> extends ExtendedBehaviour<E> {
    private static final List<Pair<MemoryModuleType<?>, MemoryStatus>> MEMORY_REQUIREMENTS = ObjectArrayList.of(
            Pair.of(ClinkerMemoryModules.ACTIVE_SQUAD_TASK.get(), MemoryStatus.VALUE_ABSENT)
    );
    protected BiPredicate<E, GnomadSquadTask> shouldVolunteer = (mob, task) -> true;

    private GnomadSquadTask task;

    public VolunteerForSquadTask<E> shouldVolunteer(BiPredicate<E, GnomadSquadTask> shouldVolunteer) {
        this.shouldVolunteer = shouldVolunteer;
        return this;
    }

    @Override
    public List<Pair<MemoryModuleType<?>, MemoryStatus>> getMemoryRequirements() {
        return MEMORY_REQUIREMENTS;
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel level, E entity) {
        for (GnomadSquadTask task : entity.squad.getTasksImmutable()) {
            if (shouldVolunteer.test(entity, task)) {
                this.task = task;
                return true;
            }
        }
        return false;
    }

    @Override
    protected void start(E entity) {
        entity.squad.volunteerForTask(entity, task);
        Clinker.LOGGER.info("{} volunteered for {}", DebugEntityNameGenerator.getEntityName(entity), GnomadSquadTask.debugName(task));
    }

    @Override
    protected void stop(E entity) {
        this.task = null;
    }
}
