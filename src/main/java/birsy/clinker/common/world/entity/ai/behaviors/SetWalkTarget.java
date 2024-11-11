package birsy.clinker.common.world.entity.ai.behaviors;

import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.phys.Vec3;
import net.tslat.smartbrainlib.api.core.behaviour.ExtendedBehaviour;
import net.tslat.smartbrainlib.util.BrainUtils;

import java.util.function.Function;

public abstract class SetWalkTarget<E extends PathfinderMob> extends ExtendedBehaviour<E> {
    protected Function<E, Float> speedMod = (owner) -> 1f;
    protected Function<E, Integer> closeEnoughWhen = (owner) -> 1;
    protected Function<E, Vec3> position = (owner) -> owner.position();

    public SetWalkTarget<E> position(Function<E, Vec3> position) {
        this.position = position;
        return this;
    }

    public SetWalkTarget<E> speedMod(Function<E, Float> speedModifier) {
        this.speedMod = speedModifier;
        return this;
    }

    public SetWalkTarget<E> closeEnoughWhen(Function<E, Integer> closeEnoughWhen) {
        this.closeEnoughWhen = closeEnoughWhen;
        return this;
    }

    @Override
    protected void start(E entity) {
        super.start(entity);
        BrainUtils.setMemory(entity, MemoryModuleType.WALK_TARGET, new WalkTarget(this.position.apply(entity), this.speedMod.apply(entity), this.closeEnoughWhen.apply(entity)));
    }
}
