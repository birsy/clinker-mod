package birsy.clinker.common.world.entity.ai.behaviors;

import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.phys.Vec3;
import net.tslat.smartbrainlib.api.core.behaviour.custom.target.SetRandomLookTarget;
import net.tslat.smartbrainlib.object.FreePositionTracker;
import net.tslat.smartbrainlib.util.BrainUtils;

public class SetRandomLookTargetImproved<E extends Mob> extends SetRandomLookTarget<E> {
    @Override
    protected void start(E entity) {
        float yaw = Mth.TWO_PI * entity.getRandom().nextFloat(),
              pitch = (float) (entity.getRandom().nextGaussian() * 0.2 - 0.1);
        BrainUtils.setForgettableMemory(entity, MemoryModuleType.LOOK_TARGET,
                new FreePositionTracker(entity.getEyePosition().add(calculateViewVector(yaw, pitch))),
                this.lookTime.apply(entity));
    }

    protected static Vec3 calculateViewVector(float yaw, float pitch) {
        return new Vec3(Mth.sin(yaw) * Mth.cos(pitch), -Mth.sin(pitch), Mth.cos(yaw) * Mth.cos(pitch));
    }
}
