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
import net.tslat.smartbrainlib.util.BrainUtils;

import java.util.List;

public class InvalidateCurrentSquadTask<E extends GnomadEntity> extends ExtendedBehaviour<E> {
    private static final List<Pair<MemoryModuleType<?>, MemoryStatus>> MEMORY_REQUIREMENTS = ObjectArrayList.of(
            Pair.of(ClinkerMemoryModules.ACTIVE_SQUAD_TASK.get(), MemoryStatus.VALUE_PRESENT)
    );

    @Override
    public List<Pair<MemoryModuleType<?>, MemoryStatus>> getMemoryRequirements() {
        return MEMORY_REQUIREMENTS;
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel level, E entity) {
        GnomadSquadTask task = BrainUtils.getMemory(entity, ClinkerMemoryModules.ACTIVE_SQUAD_TASK.get());
        if (task.markedForRemoval) return true;
        if (!task.volunteers.containsKey(entity.getUUID())) return true;
        return false;
    }

    @Override
    protected void start(E entity) {
        GnomadSquadTask task = BrainUtils.getMemory(entity, ClinkerMemoryModules.ACTIVE_SQUAD_TASK.get());
        task.leave(entity);
        String reason = "";
        if (task.markedForRemoval) reason += "it was marked for removal";
        if (!task.volunteers.containsKey(entity.getUUID()) && task.markedForRemoval) reason += " and ";
        if (!task.volunteers.containsKey(entity.getUUID())) reason += "they were removed as a volunteer";
        Clinker.LOGGER.info("{} invalidated {} as {}", DebugEntityNameGenerator.getEntityName(entity), GnomadSquadTask.debugName(task), reason);
        BrainUtils.clearMemory(entity, ClinkerMemoryModules.ACTIVE_SQUAD_TASK.get());
    }
}
