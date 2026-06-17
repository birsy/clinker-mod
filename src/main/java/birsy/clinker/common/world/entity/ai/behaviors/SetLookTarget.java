package birsy.clinker.common.world.entity.ai.behaviors;

import com.mojang.datafixers.util.Pair;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.PositionTracker;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.tslat.smartbrainlib.api.core.behaviour.ExtendedBehaviour;
import net.tslat.smartbrainlib.object.MemoryTest;
import net.tslat.smartbrainlib.util.BrainUtils;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Function;

public class SetLookTarget<E extends LivingEntity> extends ExtendedBehaviour<E> {
    private static final List<Pair<MemoryModuleType<?>, MemoryStatus>> MEMORY_REQUIREMENTS = MemoryTest.builder(1)
            .usesMemory(MemoryModuleType.LOOK_TARGET);
    final Function<E, @Nullable PositionTracker> targetMaker;
    int expirationTime = -1;

    public SetLookTarget(Function<E, @Nullable PositionTracker> targetMaker) {
        this.targetMaker = targetMaker;
    }

    public SetLookTarget<E> expirationTime(int time) {
        this.expirationTime = time;
        return this;
    }

    @Override
    protected List<Pair<MemoryModuleType<?>, MemoryStatus>> getMemoryRequirements() {
        return MEMORY_REQUIREMENTS;
    }

    @Override
    protected void start(E entity) {
        super.start(entity);
        PositionTracker tracker = targetMaker.apply(entity);
        if (tracker == null) return;
        if (expirationTime > 0) {
            BrainUtils.setForgettableMemory(entity, MemoryModuleType.LOOK_TARGET, tracker, expirationTime);
        } else {
            BrainUtils.setMemory(entity, MemoryModuleType.LOOK_TARGET, tracker);
        }
    }
}
