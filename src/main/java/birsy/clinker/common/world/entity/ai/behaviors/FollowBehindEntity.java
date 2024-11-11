package birsy.clinker.common.world.entity.ai.behaviors;

import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.EntityTracker;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.tslat.smartbrainlib.api.core.behaviour.ExtendedBehaviour;
import net.tslat.smartbrainlib.util.BrainUtils;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

public class FollowBehindEntity<E extends PathfinderMob> extends ExtendedBehaviour<E> {
    private static final List<Pair<MemoryModuleType<?>, MemoryStatus>> MEMORY_REQUIREMENTS = ObjectArrayList.of(Pair.of(MemoryModuleType.WALK_TARGET, MemoryStatus.REGISTERED));

    protected Function<E, Entity> entityProvider = (owner) -> null;
    protected Function<E, Boolean> shouldFollow = (owner) -> true;
    protected BiFunction<E, Entity, Float> speedMod = (owner, target) -> 1f;
    protected BiFunction<E, Entity, Integer> closeEnoughWhen = (owner, target) -> 3;

    protected Entity target;

    @Override
    protected List<Pair<MemoryModuleType<?>, MemoryStatus>> getMemoryRequirements() {
        return null;
    }

    public FollowBehindEntity<E> entityProvider(Function<E, Entity> entityProvider) {
        this.entityProvider = entityProvider;
        return this;
    }

    public FollowBehindEntity<E> shouldFollow(Function<E, Boolean> shouldFollow) {
        this.shouldFollow = shouldFollow;
        return this;
    }

    public FollowBehindEntity<E> speedMod(BiFunction<E, Entity, Float> speedModifier) {
        this.speedMod = speedModifier;
        return this;
    }

    public FollowBehindEntity<E> closeEnoughDist(BiFunction<E, Entity, Integer> closeEnoughMod) {
        this.closeEnoughWhen = closeEnoughMod;
        return this;
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel level, E entity) {
        this.target = entityProvider.apply(entity);
        if (this.target == null) return false;

        return super.checkExtraStartConditions(level, entity);
    }

    @Override
    protected void start(E entity) {
        Brain<?> brain = entity.getBrain();
        BrainUtils.setMemory(brain, MemoryModuleType.WALK_TARGET,
                new WalkTarget (
                        new EntityTracker(this.target, false),
                        this.speedMod.apply(entity, this.target),
                        this.closeEnoughWhen.apply(entity, this.target)
                )
        );
    }
}
