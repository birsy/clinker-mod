package birsy.clinker.common.entity.ai.behaviors;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.EntityTracker;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.tslat.smartbrainlib.util.BrainUtils;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public class SetWalkTargetToEntity<E extends LivingEntity> extends WalkTargetSetter<E> {
    protected final Function<E, @Nullable Entity> targetGetter;
    private Entity target;

    public SetWalkTargetToEntity(Function<E, @Nullable Entity> targetGetter) {
        this.targetGetter = targetGetter;
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel level, E entity) {
        this.target = targetGetter.apply(entity);
        return this.target != null && this.target.isAlive();
    }

    @Override
    protected void start(E entity) {
        super.start(entity);
        BrainUtils.setMemory(entity, MemoryModuleType.WALK_TARGET,
                new WalkTarget(target, this.speedMod.apply(entity), this.closeEnoughWhen.apply(entity))
        );
        if (lookAtTarget.test(entity)) {
            BrainUtils.setMemory(entity, MemoryModuleType.LOOK_TARGET,
                    new EntityTracker(entity, true)
            );
        }
    }
}
