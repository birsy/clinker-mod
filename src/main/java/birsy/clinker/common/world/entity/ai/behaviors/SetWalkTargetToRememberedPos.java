package birsy.clinker.common.world.entity.ai.behaviors;

import net.minecraft.core.GlobalPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.BlockPosTracker;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.tslat.smartbrainlib.util.BrainUtils;

public class SetWalkTargetToRememberedPos<E extends LivingEntity> extends WalkTargetSetter<E> {
    final MemoryModuleType<GlobalPos> posMemory;

    public SetWalkTargetToRememberedPos(MemoryModuleType<GlobalPos> posMemory) {
        this.posMemory = posMemory;
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel level, E entity) {
        if (!BrainUtils.hasMemory(entity, posMemory)) return false;
        GlobalPos pos = BrainUtils.getMemory(entity, posMemory);
        return pos != null && pos.dimension() == level.dimension();
    }

    @Override
    protected void start(E entity) {
        super.start(entity);
        GlobalPos pos = BrainUtils.getMemory(entity, posMemory);
        BrainUtils.setMemory(entity, MemoryModuleType.WALK_TARGET,
                new WalkTarget(pos.pos(), this.speedMod.apply(entity), this.closeEnoughWhen.apply(entity))
        );
        if (lookAtTarget.test(entity)) {
            BrainUtils.setMemory(entity, MemoryModuleType.LOOK_TARGET,
                    new BlockPosTracker(pos.pos())
            );
        }
    }
}
