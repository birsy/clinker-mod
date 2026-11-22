package birsy.clinker.common.entity.lungwyrm.behaviors;

import birsy.clinker.common.entity.lungwyrm.LungWyrmEntity;
import com.mojang.datafixers.util.Pair;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.phys.Vec3;
import net.tslat.smartbrainlib.api.core.behaviour.ExtendedBehaviour;
import net.tslat.smartbrainlib.object.MemoryTest;
import net.tslat.smartbrainlib.util.BrainUtils;

import java.util.List;

public class FloatDirectlyTowardsWalkTarget extends ExtendedBehaviour<LungWyrmEntity> {
    private static final MemoryTest MEMORY_REQUIREMENTS = MemoryTest.builder(1)
            .hasMemory(MemoryModuleType.WALK_TARGET);

    @Override
    protected List<Pair<MemoryModuleType<?>, MemoryStatus>> getMemoryRequirements() {
        return MEMORY_REQUIREMENTS;
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel level, LungWyrmEntity entity) {
        WalkTarget target = BrainUtils.getMemory(entity, MemoryModuleType.WALK_TARGET);
        if (hasReachedTarget(entity, target)) {
            BrainUtils.clearMemories(entity, MemoryModuleType.WALK_TARGET);
            return false;
        }
        return true;
    }

    @Override
    protected void tick(LungWyrmEntity entity) {
        WalkTarget target = BrainUtils.getMemory(entity, MemoryModuleType.WALK_TARGET);
        Vec3 pos = target.getTarget().currentPosition();
        entity.getMoveControl().setWantedPosition(pos.x(), pos.y(), pos.z(), target.getSpeedModifier());
    }

    @Override
    protected boolean shouldKeepRunning(LungWyrmEntity entity) {
        return !hasReachedTarget(entity, BrainUtils.getMemory(entity, MemoryModuleType.WALK_TARGET));
    }

    @Override
    protected void stop(LungWyrmEntity entity) {
        BrainUtils.clearMemories(entity, MemoryModuleType.WALK_TARGET);
        entity.getMoveControl().clearWanted();
    }

    boolean hasReachedTarget(LungWyrmEntity entity, WalkTarget target) {
        if (target == null) return true;
        return target.getTarget().currentBlockPosition().distManhattan(entity.blockPosition()) <= target.getCloseEnoughDist();
    }
}
