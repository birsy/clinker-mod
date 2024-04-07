package birsy.clinker.common.world.entity.ai;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.PositionTracker;
import net.minecraft.world.phys.Vec3;

import java.util.function.Function;

public class LookTarget<E extends LivingEntity> implements PositionTracker {
    private final E looker;
    protected Function<E, Vec3> positionSupplier;

    public LookTarget(E looker) {
        this.looker = looker;
    }

    public LookTarget<E> target(Function<E, Vec3> positionSupplier) {
        this.positionSupplier = positionSupplier;
        return this;
    }

    @Override
    public Vec3 currentPosition() {
        return this.positionSupplier.apply(looker);
    }

    @Override
    public BlockPos currentBlockPosition() {
        return BlockPos.containing(this.positionSupplier.apply(looker));
    }

    @Override
    public boolean isVisibleBy(LivingEntity p_23739_) {
        return true;
    }
}
