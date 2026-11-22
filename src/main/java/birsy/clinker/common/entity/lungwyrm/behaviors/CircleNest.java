package birsy.clinker.common.entity.lungwyrm.behaviors;

import birsy.clinker.common.entity.lungwyrm.LungWyrmEntity;
import birsy.clinker.core.registry.entity.ClinkerMemoryModules;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.GlobalPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.phys.Vec3;
import net.tslat.smartbrainlib.api.core.behaviour.ExtendedBehaviour;
import net.tslat.smartbrainlib.object.MemoryTest;
import net.tslat.smartbrainlib.util.BrainUtils;

import java.util.List;

public class CircleNest extends ExtendedBehaviour<LungWyrmEntity> {
    private static final MemoryTest MEMORY_REQUIREMENTS = MemoryTest.builder(2)
            .hasMemory(MemoryModuleType.HOME)
            .noMemory(MemoryModuleType.WALK_TARGET);

    @Override
    protected List<Pair<MemoryModuleType<?>, MemoryStatus>> getMemoryRequirements() {
        return MEMORY_REQUIREMENTS;
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel level, LungWyrmEntity entity) {
        GlobalPos nestPos = BrainUtils.getMemory(entity, MemoryModuleType.HOME);
        return nestPos != null && nestPos.dimension() == level.dimension();
    }

    @Override
    protected void start(LungWyrmEntity entity) {
        super.start(entity);

        Vec3 nestPos = BrainUtils.getMemory(entity, MemoryModuleType.HOME).pos().getCenter();
        Vec3 wyrmPos = entity.getEyePosition();

        Vec3 offset = wyrmPos.subtract(nestPos);
        if (offset.x() == 0 && offset.z() == 0) return;

        double angle = Mth.atan2(offset.z(), offset.x());

        Vec3 perpendicularDirection = offset.cross(new Vec3(0, 1, 0));
        Vec3 lookDir = entity.calculateViewVector(0, entity.getYRot());
        double angleSign = 1;//-Math.signum(perpendicularDirection.dot(lookDir));
        if (angleSign == 0) angleSign = entity.getRandom().nextBoolean() ? 1 : -1;

        double targetAngle = angle + angleSign * Mth.PI * 0.1;
        double radius = 40;
        double targetDistance = entity.getRandom().triangle(
                Math.clamp(offset.multiply(1,0,1).length(), radius - 10, radius + 10), 5
        );

        Vec3 targetPos = nestPos.add(Math.cos(targetAngle) * targetDistance, 0, Math.sin(targetAngle) * targetDistance);

        BrainUtils.setMemory(entity, MemoryModuleType.WALK_TARGET, new WalkTarget(targetPos, 0.2F, 10));
    }
}
