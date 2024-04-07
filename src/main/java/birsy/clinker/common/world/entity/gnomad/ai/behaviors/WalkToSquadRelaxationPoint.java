package birsy.clinker.common.world.entity.gnomad.ai.behaviors;

import birsy.clinker.common.world.entity.gnomad.GnomadEntity;
import birsy.clinker.core.registry.entity.ClinkerMemoryModules;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.tslat.smartbrainlib.api.core.behaviour.ExtendedBehaviour;
import net.tslat.smartbrainlib.util.BrainUtils;

import java.util.List;
import java.util.function.Function;

public class WalkToSquadRelaxationPoint<E extends GnomadEntity> extends ExtendedBehaviour<E> {
    private static final List<Pair<MemoryModuleType<?>, MemoryStatus>> MEMORY_REQUIREMENTS = ObjectArrayList.of(
            Pair.of(ClinkerMemoryModules.RELAXATION_SPOT.get(), MemoryStatus.VALUE_PRESENT),
            Pair.of(MemoryModuleType.WALK_TARGET, MemoryStatus.REGISTERED));
    protected Function<E, Float> speedMod = (owner) -> 1f;
    protected Function<E, Integer> sitRadius = (owner) -> 3;

    public WalkToSquadRelaxationPoint<E> speedMod(Function<E, Float> speedModifier) {
        this.speedMod = speedModifier;
        return this;
    }

    public WalkToSquadRelaxationPoint<E> sitRadius(Function<E, Integer> sitRadius) {
        this.sitRadius = sitRadius;
        return this;
    }

    @Override
    public List<Pair<MemoryModuleType<?>, MemoryStatus>> getMemoryRequirements() {
        return MEMORY_REQUIREMENTS;
    }

    @Override
    protected void start(E entity) {
        super.start(entity);
        BrainUtils.setMemory(entity, MemoryModuleType.WALK_TARGET, new WalkTarget(BrainUtils.getMemory(entity, ClinkerMemoryModules.RELAXATION_SPOT.get()).pos().getCenter(), this.speedMod.apply(entity), this.sitRadius.apply(entity)));
    }
}
