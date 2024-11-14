package birsy.clinker.common.world.entity.gnomad.gnomind.behaviors;

import birsy.clinker.common.world.entity.gnomad.GnomadEntity;
import birsy.clinker.common.world.entity.gnomad.gnomind.squad.squadtasks.GnomadSquadTask;
import birsy.clinker.core.registry.entity.ClinkerMemoryModules;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.phys.Vec3;
import net.tslat.smartbrainlib.api.core.behaviour.ExtendedBehaviour;
import net.tslat.smartbrainlib.util.BrainUtils;
import net.tslat.smartbrainlib.util.RandomUtil;

import java.util.List;
import java.util.function.Function;

public class WalkToSquadRelaxationPoint<E extends GnomadEntity> extends ExtendedBehaviour<E> {
    private static final List<Pair<MemoryModuleType<?>, MemoryStatus>> MEMORY_REQUIREMENTS = ObjectArrayList.of(
            Pair.of(ClinkerMemoryModules.ACTIVE_SQUAD_TASK.get(), MemoryStatus.VALUE_PRESENT),
            Pair.of(ClinkerMemoryModules.RELAXATION_SPOT.get(), MemoryStatus.REGISTERED),
            Pair.of(MemoryModuleType.WALK_TARGET, MemoryStatus.REGISTERED)
    );
    protected Function<E, Float> speedMod = (owner) -> 1f;
    protected Function<E, Integer> sitRadius = (owner) -> 8;

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
    protected boolean checkExtraStartConditions(ServerLevel level, E entity) {
        Vec3 pos = BrainUtils.getMemory(entity, ClinkerMemoryModules.RELAXATION_SPOT.get()).pos().getCenter();
        return entity.position().distanceTo(pos) > sitRadius.apply(entity);
    }

    @Override
    protected void start(E entity) {
        super.start(entity);
        entity.setSitting(false);
        GnomadSquadTask task = BrainUtils.getMemory(entity, ClinkerMemoryModules.ACTIVE_SQUAD_TASK.get());
        RandomSource tempRandom = RandomSource.create(entity.getUUID().getMostSignificantBits() + task.taskmaster.getUUID().getMostSignificantBits());
        float angle = tempRandom.nextFloat() * Mth.TWO_PI;
        float radius = sitRadius.apply(entity) - 1.5F;
        Vec3 offset = new Vec3(Mth.cos(angle) * radius, 0, Mth.sin(angle) * radius);

        BrainUtils.setMemory(entity,
                MemoryModuleType.WALK_TARGET,
                new WalkTarget(
                        BrainUtils.getMemory(entity, ClinkerMemoryModules.RELAXATION_SPOT.get()).pos().getCenter().add(offset),
                        this.speedMod.apply(entity),
                        5
                )
        );
    }
}
