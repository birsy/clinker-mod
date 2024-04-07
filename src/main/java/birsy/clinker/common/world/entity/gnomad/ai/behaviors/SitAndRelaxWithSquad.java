package birsy.clinker.common.world.entity.gnomad.ai.behaviors;

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
            Pair.of(ClinkerMemoryModules.RELAXATION_SPOT.get(), MemoryStatus.VALUE_PRESENT));
    protected Function<E, Float> sitRadius = (owner) -> 3f;

    public SitAndRelaxWithSquad<E> sitRadius(Function<E, Float> sitRadius) {
        this.sitRadius = sitRadius;
        return this;
    }

    @Override
    public List<Pair<MemoryModuleType<?>, MemoryStatus>> getMemoryRequirements() {
        return MEMORY_REQUIREMENTS;
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel level, E entity) {
        if (entity.isSitting()) return false;
        float sitRadius = this.sitRadius.apply(entity);
        if (entity.distanceToSqr(BrainUtils.getMemory(entity, ClinkerMemoryModules.RELAXATION_SPOT.get()).pos().getCenter()) <= sitRadius * sitRadius) {
            return true;
        }
        return false;
    }

    @Override
    protected void start(E entity) {
        super.start(entity);
        entity.setSitting(true);
    }

    @Override
    protected boolean shouldKeepRunning(E entity) {
        if (BrainUtils.hasMemory(entity, MemoryModuleType.PATH)) return false;
        return true;
    }

    @Override
    protected void stop(E entity) {
        super.stop(entity);
        entity.setSitting(false);
    }
}
