package birsy.clinker.common.entity.lungwyrm.behaviors;

import birsy.clinker.common.entity.lungwyrm.LungWyrmEntity;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.phys.Vec3;
import net.tslat.smartbrainlib.api.core.behaviour.ExtendedBehaviour;
import net.tslat.smartbrainlib.object.MemoryTest;
import net.tslat.smartbrainlib.util.BrainUtils;

import java.util.List;

public class SetRandomFloatTarget extends ExtendedBehaviour<LungWyrmEntity> {
    private static final MemoryTest MEMORY_REQUIREMENTS = MemoryTest.builder(1)
            .noMemory(MemoryModuleType.WALK_TARGET);

    @Override
    protected List<Pair<MemoryModuleType<?>, MemoryStatus>> getMemoryRequirements() {
        return MEMORY_REQUIREMENTS;
    }

    @Override
    protected void start(LungWyrmEntity entity) {
        LungWyrmEntity.LungWyrmJoint head = entity.joints.getFirst();
        double targetY = Mth.lerp(0.75F, head.groundY, head.ceilingY);
        if (Double.isNaN(targetY) || Double.isInfinite(targetY))
            targetY = head.y;
        Vec3 lookDir = entity.calculateViewVector(0, entity.getYRot());
        Vec3 searchPos = entity.getEyePosition().add(lookDir.scale(30));
        for (int i = 0; i < 16; i++) {
            Vec3 targetPos = searchPos
                    .add(entity.getRandom().triangle(0, 50), 0,
                         entity.getRandom().triangle(0, 50));
            BlockPos targetBlockPos = BlockPos.containing(targetPos.x(), targetY, targetPos.z());
            if (entity.level().getBlockState(targetBlockPos).isAir()) {
                BrainUtils.setMemory(entity, MemoryModuleType.WALK_TARGET, new WalkTarget(targetPos, 0.1F, 10));
            }
        }

    }
}
