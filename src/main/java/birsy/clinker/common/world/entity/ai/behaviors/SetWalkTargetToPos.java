package birsy.clinker.common.world.entity.ai.behaviors;

import net.minecraft.core.GlobalPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.BlockPosTracker;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.phys.Vec3;
import net.tslat.smartbrainlib.object.FreePositionTracker;
import net.tslat.smartbrainlib.util.BrainUtils;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public class SetWalkTargetToPos<E extends LivingEntity> extends WalkTargetSetter<E> {
    final Function<E, @Nullable Vec3> targetMaker;
    Vec3 target;

    public SetWalkTargetToPos(Function<E, @Nullable Vec3> targetMaker) {
        this.targetMaker = targetMaker;
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel level, E entity) {
        this.target = targetMaker.apply(entity);
        return this.target != null;
    }

    @Override
    protected void start(E entity) {
        super.start(entity);
        BrainUtils.setMemory(entity, MemoryModuleType.WALK_TARGET,
                new WalkTarget(this.target, this.speedMod.apply(entity), this.closeEnoughWhen.apply(entity))
        );
        if (lookAtTarget.test(entity)) {
            BrainUtils.setMemory(entity, MemoryModuleType.LOOK_TARGET,
                    new FreePositionTracker(this.target)
            );
        }
    }
}
