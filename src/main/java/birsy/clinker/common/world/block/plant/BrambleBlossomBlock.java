package birsy.clinker.common.world.block.plant;

import birsy.clinker.core.registry.ClinkerBlocks;
import birsy.clinker.core.registry.ClinkerTags;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.component.SuspiciousStewEffects;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.FlowerBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class BrambleBlossomBlock extends FlowerBlock {
    public BrambleBlossomBlock(SuspiciousStewEffects suspiciousStewEffects, Properties properties) {
        super(suspiciousStewEffects, properties);
    }

    @Override
    protected boolean mayPlaceOn(BlockState state, BlockGetter level, BlockPos pos) {
        return super.mayPlaceOn(state, level, pos) || state.is(ClinkerTags.OTHERSHORE_SOIL) || state.is(ClinkerBlocks.THORNY_STEM.get());
    }


    @Override
    protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        super.entityInside(state, level, pos, entity);

        boolean inside = false;
        AABB entityBounds = entity.getBoundingBox().move(-pos.getX(), -pos.getY(), -pos.getZ());
        VoxelShape shape = this.getShape(state, level, pos, CollisionContext.of(entity));
        for (AABB aabb : shape.toAabbs()) {
            if (entityBounds.intersects(aabb)) {
                inside = true;
                break;
            }
        }
        if (inside) {
            this.applyToInsideEntities(state, level, pos, entity);
        }
    }

    void applyToInsideEntities(BlockState state, Level level, BlockPos pos, Entity entity) {
        entity.hurt(level.damageSources().cactus(), 1);
        boolean onClimbable = entity instanceof LivingEntity livingEntity && livingEntity.onClimbable();
        if (entity.getDeltaMovement().y < 0 || onClimbable) {
            entity.makeStuckInBlock(state, new Vec3(1, 0.8, 1));
        }
    }
}
