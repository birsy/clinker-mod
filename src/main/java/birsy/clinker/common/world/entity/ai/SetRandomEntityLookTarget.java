package birsy.clinker.common.world.entity.ai;

import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.EntityTracker;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.tslat.smartbrainlib.api.core.behaviour.ExtendedBehaviour;
import net.tslat.smartbrainlib.util.BrainUtils;

import java.util.List;
import java.util.function.Predicate;

public class SetRandomEntityLookTarget<E extends LivingEntity> extends ExtendedBehaviour<E> {
    private static final List<Pair<MemoryModuleType<?>, MemoryStatus>> MEMORY_REQUIREMENTS = ObjectArrayList.of(Pair.of(MemoryModuleType.LOOK_TARGET, MemoryStatus.VALUE_ABSENT), Pair.of(MemoryModuleType.NEAREST_LIVING_ENTITIES, MemoryStatus.VALUE_PRESENT));
    protected Predicate<LivingEntity> predicate = pl -> true;
    protected float probability = 1.0F;
    protected LivingEntity target = null;

    public SetRandomEntityLookTarget<E> predicate(Predicate<LivingEntity> predicate) {
        this.predicate = predicate;
        return this;
    }

    public SetRandomEntityLookTarget<E> probabilityPerEntity(float probability) {
        this.probability = probability;
        return this;
    }

    @Override
    protected List<Pair<MemoryModuleType<?>, MemoryStatus>> getMemoryRequirements() {
        return MEMORY_REQUIREMENTS;
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel level, E entity) {
        for (LivingEntity potentialTargets : BrainUtils.getMemory(entity, MemoryModuleType.NEAREST_LIVING_ENTITIES)) {
            if (this.predicate.test(potentialTargets) && entity.getRandom().nextFloat() < this.probability) {
                this.target = potentialTargets;
                break;
            }
        }

        return this.target != null;
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
