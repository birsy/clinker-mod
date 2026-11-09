package birsy.clinker.common.entity.ai.behaviors;

import birsy.clinker.common.entity.GroundLocomotionEntity;
import birsy.clinker.common.entity.ai.LookTargetController;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.tslat.smartbrainlib.api.core.behaviour.custom.look.LookAtTarget;
import net.tslat.smartbrainlib.util.BrainUtils;

import java.util.function.Function;

public class LocomotorLookAtTarget<E extends GroundLocomotionEntity> extends LookAtTarget<E> {
    private Function<E, Float> transitionSpeedGetter = entity -> 1.0F;
    private Function<E, Float> lookSpeedGetter = entity -> 1.0F;
    private Function<E, Integer> priorityGetter = entity -> 0;

    public LocomotorLookAtTarget() { super(); }

    public LocomotorLookAtTarget<E> transitionSpeed(Function<E, Float> transitionSpeed) {
        this.transitionSpeedGetter = transitionSpeed;
        return this;
    }
    public LocomotorLookAtTarget<E> transitionSpeed(float transitionSpeed) {
        return transitionSpeed(e -> transitionSpeed);
    }

    public LocomotorLookAtTarget<E> lookSpeed(Function<E, Float> lookSpeed) {
        this.lookSpeedGetter = lookSpeed;
        return this;
    }
    public LocomotorLookAtTarget<E> lookSpeed(float lookSpeed) {
        return lookSpeed(e -> lookSpeed);
    }

    public LocomotorLookAtTarget<E> targetPriority(Function<E, Integer> priority) {
        this.priorityGetter = priority;
        return this;
    }
    public LocomotorLookAtTarget<E> targetPriority(int priority) {
        return targetPriority(e -> priority);
    }

    int lookPriority;

    @Override
    protected void start(E entity) {
        super.start(entity);
        this.lookPriority = priorityGetter.apply(entity);
    }

    @Override
    protected void tick(E entity) {
        BrainUtils.withMemory(entity, MemoryModuleType.LOOK_TARGET,
                target -> {
                    float lookSpeed = lookSpeedGetter.apply(entity);
                    float transitionSpeed = transitionSpeedGetter.apply(entity);
                    entity.getLookControl().lookTargetController.lookAt(
                            this.lookPriority,
                            target.currentPosition().x, target.currentPosition().y, target.currentPosition().z,
                            lookSpeed, transitionSpeed
                    );
                }
        );
    }
}
