package birsy.clinker.common.entity.ai.behaviors;

import birsy.clinker.common.entity.GroundLocomotionEntity;
import birsy.clinker.common.entity.ai.LookTargetController;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.tslat.smartbrainlib.api.core.behaviour.custom.look.LookAtTarget;
import net.tslat.smartbrainlib.util.BrainUtils;

import java.util.function.Function;

public class LocomotorLookAtTarget<E extends GroundLocomotionEntity> extends LookAtTarget<E> {
    private LookTargetController.LookTargetHandle handle;
    private float enterSpeed = 0.2F,
                  exitSpeed = 0.2F;
    private Function<E, Float> lookSpeed = entity -> 1.0F;
    private Function<E, Integer> priority = entity -> 0;

    public LocomotorLookAtTarget() { super(); }

    public LocomotorLookAtTarget<E> transitionSpeed(float transitionSpeed) {
        this.enterSpeed = transitionSpeed;
        this.exitSpeed = transitionSpeed;
        return this;
    }
    public LocomotorLookAtTarget<E> enterSpeed(float enterSpeed) {
        this.enterSpeed = enterSpeed;
        return this;
    }
    public LocomotorLookAtTarget<E> exitSpeed(float exitSpeed) {
        this.exitSpeed = exitSpeed;
        return this;
    }
    public LocomotorLookAtTarget<E> lookSpeed(Function<E, Float> lookSpeed) {
        this.lookSpeed = lookSpeed;
        return this;
    }
    public LocomotorLookAtTarget<E> targetPriority(Function<E, Integer> priority) {
        this.priority = priority;
        return this;
    }

    @Override
    protected void start(E entity) {
        super.start(entity);
        this.handle = entity.getLookControl().lookTargetController.createHandle(this.lookSpeed.apply(entity), this.priority.apply(entity));
        this.handle.fadeIn(this.enterSpeed, true);
    }

    @Override
    protected void tick(E entity) {
        BrainUtils.withMemory(entity, MemoryModuleType.LOOK_TARGET, target -> handle.face(target.currentPosition().x, target.currentPosition().y, target.currentPosition().z));
    }

    @Override
    protected void stop(E entity) {
        super.stop(entity);
        this.handle.fadeOut(this.exitSpeed, false);
        this.handle = null;
    }
}
