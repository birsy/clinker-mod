package birsy.clinker.common.world.block.plant;

import birsy.clinker.common.world.block.AbstractDirectionalStemBlock;
import birsy.clinker.core.registry.ClinkerBlocks;
import birsy.clinker.core.registry.ClinkerTags;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.FlowerBlock;
import net.minecraft.world.level.block.SupportType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.common.IShearable;

public class BrambleBlossomBlock extends FlowerBlock {
    public static final MapCodec<BrambleBlossomBlock> CODEC = simpleCodec(BrambleBlossomBlock::new);

    public BrambleBlossomBlock(Properties properties) {
        super(makeEffectList(MobEffects.SATURATION, 30.0F), properties);
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
            entity.hurt(level.damageSources().cactus(), 1);
            boolean onClimbable = entity instanceof LivingEntity livingEntity && livingEntity.onClimbable();
            if (entity.getDeltaMovement().y < 0 || onClimbable) {
                entity.makeStuckInBlock(state, new Vec3(1, 0.8, 1));
            }
        }
    }

    @Override
    public MapCodec<? extends BrambleBlossomBlock> codec() {
        return CODEC;
    }
}
