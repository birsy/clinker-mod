package birsy.clinker.common.world.entity.ai.behaviors;

import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.EntityTracker;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.tslat.smartbrainlib.api.core.behaviour.ExtendedBehaviour;
import net.tslat.smartbrainlib.util.BrainUtils;
import net.tslat.smartbrainlib.util.SensoryUtils;

import java.util.List;
import java.util.function.BiPredicate;

public class InvalidateLookAtTarget<E extends LivingEntity> extends ExtendedBehaviour<E> {
    private static final List<Pair<MemoryModuleType<?>, MemoryStatus>> MEMORY_REQUIREMENTS = ObjectArrayList.of(
            Pair.of(MemoryModuleType.LOOK_TARGET, MemoryStatus.VALUE_PRESENT)
    );
    private BiPredicate<E, Entity> shouldInvalidate = ((me, entity) -> {
        boolean result = entity.isSpectator() || !SensoryUtils.hasLineOfSight(me, entity);
        if (entity instanceof LivingEntity livingEntity) return result || livingEntity.isDeadOrDying();
        return result;
    });

    public InvalidateLookAtTarget<E> shouldInvalidate(BiPredicate<E, Entity> shouldInvalidate) {
        this.shouldInvalidate = shouldInvalidate;
        return this;
    }

    @Override
    protected List<Pair<MemoryModuleType<?>, MemoryStatus>> getMemoryRequirements() {
        return MEMORY_REQUIREMENTS;
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel level, E entity) {
        return BrainUtils.getMemory(entity, MemoryModuleType.LOOK_TARGET) instanceof EntityTracker;
    }

    @Override
    protected boolean shouldKeepRunning(E entity) {
        return BrainUtils.hasMemory(entity, MemoryModuleType.LOOK_TARGET) && BrainUtils.getMemory(entity, MemoryModuleType.LOOK_TARGET) instanceof EntityTracker;
    }

    @Override
    protected void tick(E entity) {
        if (shouldInvalidate.test(entity, ((EntityTracker) BrainUtils.getMemory(entity, MemoryModuleType.LOOK_TARGET)).getEntity())) BrainUtils.clearMemory(entity, MemoryModuleType.LOOK_TARGET);
    }
}
