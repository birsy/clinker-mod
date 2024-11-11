package birsy.clinker.common.world.entity.ai.behaviors;

import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.phys.Vec3;
import net.tslat.smartbrainlib.api.core.behaviour.custom.path.SetRandomWalkTarget;
import net.tslat.smartbrainlib.util.BrainUtils;

import java.util.function.Function;

public class SetRandomWalkTargetCloseEnough<E extends PathfinderMob> extends SetRandomWalkTarget<E> {
    protected Function<E, Integer> closeEnoughDist;

    public SetRandomWalkTargetCloseEnough<?> closeEnoughDist(int dist) {
        return this.closeEnoughDist((entity) -> dist);
    }

    public SetRandomWalkTargetCloseEnough<?> closeEnoughDist(Function<E, Integer> closeEnoughDist) {
        this.closeEnoughDist = closeEnoughDist;
        return this;
    }

    @Override
    protected void start(E entity) {
        Vec3 targetPos = getTargetPos(entity);

        if (!this.positionPredicate.test(entity, targetPos))
            targetPos = null;

        if (targetPos == null) {
            BrainUtils.clearMemory(entity, MemoryModuleType.WALK_TARGET);
        }
        else {
            BrainUtils.setMemory(entity, MemoryModuleType.WALK_TARGET,
                    new WalkTarget(targetPos,
                            this.speedModifier.apply(entity, targetPos),
                            this.closeEnoughDist.apply(entity))
            );
        }
    }
}
