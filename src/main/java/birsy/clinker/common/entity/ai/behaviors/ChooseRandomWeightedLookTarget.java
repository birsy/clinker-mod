package birsy.clinker.common.entity.ai.behaviors;

import com.mojang.datafixers.util.Pair;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.random.SimpleWeightedRandomList;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.EntityTracker;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.NearestVisibleLivingEntities;
import net.tslat.smartbrainlib.api.core.behaviour.ExtendedBehaviour;
import net.tslat.smartbrainlib.object.MemoryTest;
import net.tslat.smartbrainlib.util.BrainUtils;

import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

public class ChooseRandomWeightedLookTarget<E extends LivingEntity> extends ExtendedBehaviour<E> {
    private static final MemoryTest MEMORY_REQUIREMENTS = MemoryTest.builder(2)
            .hasMemory(MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES)
            .noMemory(MemoryModuleType.LOOK_TARGET);
    protected BiFunction<E, LivingEntity, Integer> lookWeight = (me, entity) -> 1;
    protected BiPredicate<E, LivingEntity> lookPredicate = (me, entity) -> true;

    protected LivingEntity target = null;

    public ChooseRandomWeightedLookTarget<E> lookWeight(BiFunction<E, LivingEntity, Integer> weighter) {
        this.lookWeight = weighter;
        return this;
    }
    public ChooseRandomWeightedLookTarget<E> lookPredicate(BiPredicate<E, LivingEntity> predicate) {
        this.lookPredicate = predicate;
        return this;
    }

    @Override
    protected List<Pair<MemoryModuleType<?>, MemoryStatus>> getMemoryRequirements() {
        return MEMORY_REQUIREMENTS;
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel level, E entity) {
        NearestVisibleLivingEntities nearbyEntities =
                BrainUtils.getMemory(entity, MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES);

        Predicate<LivingEntity> predicate = otherEntity -> lookPredicate.test(entity, otherEntity);
        if (!nearbyEntities.contains(predicate))
            return false;

        SimpleWeightedRandomList.Builder<LivingEntity> builder = SimpleWeightedRandomList.builder();
        for (LivingEntity nearbyEntity : nearbyEntities.findAll(predicate)) {
            int weight = lookWeight.apply(entity, nearbyEntity);
            if (weight > 0) builder.add(nearbyEntity, weight);
        }
        Optional<LivingEntity> newTarget = builder.build().getRandomValue(entity.getRandom());
        if (newTarget.isEmpty()) return false;

        this.target = newTarget.get();
        return true;
    }

    @Override
    protected void start(E entity) {
        BrainUtils.setMemory(entity, MemoryModuleType.LOOK_TARGET, new EntityTracker(this.target, true));
    }

    @Override
    protected void stop(E entity) {
        this.target = null;
    }
}
