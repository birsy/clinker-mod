package birsy.clinker.common.world.entity.gnomad.gnomind.behaviors;

import birsy.clinker.common.world.entity.gnomad.GnomadEntity;
import birsy.clinker.core.registry.entity.ClinkerMemoryModules;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.tslat.smartbrainlib.api.core.behaviour.ExtendedBehaviour;
import net.tslat.smartbrainlib.util.BrainUtils;

import java.util.List;
import java.util.function.Function;

public class SitAndRelaxWithSquad<E extends GnomadEntity> extends ExtendedBehaviour<E> {
    private static final List<Pair<MemoryModuleType<?>, MemoryStatus>> MEMORY_REQUIREMENTS = ObjectArrayList.of(
            Pair.of(ClinkerMemoryModules.RELAXATION_SPOT.get(), MemoryStatus.VALUE_PRESENT),
            Pair.of(ClinkerMemoryModules.ACTIVE_SQUAD_TASK.get(), MemoryStatus.VALUE_PRESENT)
    );
    protected Function<E, Integer> sitRadius = (owner) -> 3;

    public SitAndRelaxWithSquad<E> sitRadius(Function<E, Integer> sitRadius) {
        this.sitRadius = sitRadius;
        return this;
    }

    @Override
    public List<Pair<MemoryModuleType<?>, MemoryStatus>> getMemoryRequirements() {
        return MEMORY_REQUIREMENTS;
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel level, E entity) {
        float sitRadius = this.sitRadius.apply(entity);
        return entity.position().distanceTo(BrainUtils.getMemory(entity, ClinkerMemoryModules.RELAXATION_SPOT.get()).pos().getCenter()) <= sitRadius;
    }

    @Override
    protected void start(E entity) {
        if (!entity.isSitting()) entity.setSitting(true);
        BrainUtils.clearMemory(entity, MemoryModuleType.WALK_TARGET);
    }
}
