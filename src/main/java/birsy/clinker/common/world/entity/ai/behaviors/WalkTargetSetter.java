package birsy.clinker.common.world.entity.ai.behaviors;

import com.mojang.datafixers.util.Pair;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.phys.Vec3;
import net.tslat.smartbrainlib.api.core.behaviour.ExtendedBehaviour;
import net.tslat.smartbrainlib.object.MemoryTest;
import net.tslat.smartbrainlib.util.BrainUtils;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

public abstract class WalkTargetSetter<E extends LivingEntity> extends ExtendedBehaviour<E> {
    static final List<Pair<MemoryModuleType<?>, MemoryStatus>> MEMORY_REQUIREMENTS = MemoryTest.builder(1)
            .usesMemory(MemoryModuleType.WALK_TARGET);
    protected Function<E, Float> speedMod = (owner) -> 1f;
    protected Function<E, Integer> closeEnoughWhen = (owner) -> 1;
    protected Predicate<E> lookAtTarget = (owner) -> true;

    @Override
    protected List<Pair<MemoryModuleType<?>, MemoryStatus>> getMemoryRequirements() {
        return MEMORY_REQUIREMENTS;
    }

    public WalkTargetSetter<E> speedMod(Function<E, Float> speedModifier) {
        this.speedMod = speedModifier;
        return this;
    }
    public WalkTargetSetter<E> speedMod(float speedModifier) {
        return this.speedMod((e) -> speedModifier);
    }

    public WalkTargetSetter<E> closeEnoughWhen(Function<E, Integer> closeEnoughWhen) {
        this.closeEnoughWhen = closeEnoughWhen;
        return this;
    }
    public WalkTargetSetter<E> closeEnoughWhen(int closeEnoughWhen) {
        return this.closeEnoughWhen((e) -> closeEnoughWhen);
    }

    public WalkTargetSetter<E> lookAtTarget(Predicate<E> shouldLook) {
        this.lookAtTarget = shouldLook;
        return this;
    }
    public WalkTargetSetter<E> lookAtTarget(boolean shouldLook) {
        return this.lookAtTarget((e) -> shouldLook);
    }
}
