package birsy.clinker.common.block.plant;

import birsy.clinker.core.registry.ClinkerBlocks;
import birsy.clinker.core.registry.ClinkerTags;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class WitheringBrambleBlossomBlock extends BrambleBlossomBlock {
    public static final MapCodec<WitheringBrambleBlossomBlock> CODEC = simpleCodec(WitheringBrambleBlossomBlock::new);

    public WitheringBrambleBlossomBlock(Properties properties) {
        super(makeEffectList(MobEffects.WITHER, 12.0F), properties);
    }

    @Override
    protected boolean mayPlaceOn(BlockState state, BlockGetter level, BlockPos pos) {
        return super.mayPlaceOn(state, level, pos) || state.is(ClinkerTags.Blocks.OTHERSHORE_SOIL) || state.is(ClinkerBlocks.THORNY_STEM.get());
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        VoxelShape voxelshape = this.getShape(state, level, pos, CollisionContext.empty());
        Vec3 shapeCenter = voxelshape.bounds().getCenter();
        double centerX = pos.getX() + shapeCenter.x;
        double centerZ = pos.getZ() + shapeCenter.z;

        for (int i = 0; i < 3; i++) {
            if (random.nextBoolean()) {
                level.addParticle(
                        ParticleTypes.SMOKE,
                        centerX + random.nextDouble() / 5.0,
                        pos.getY() + 0.5,
                        centerZ + random.nextDouble() / 5.0,
                        0.0,
                        0.0,
                        0.0
                );
            }
        }
    }

    @Override
    void applyToInsideEntities(BlockState state, Level level, BlockPos pos, Entity entity) {
        if (!level.isClientSide && level.getDifficulty() != Difficulty.PEACEFUL) {
            if (entity instanceof LivingEntity livingentity && !livingentity.isInvulnerableTo(level.damageSources().wither())) {
                livingentity.addEffect(new MobEffectInstance(MobEffects.WITHER, 40));
            }
        }
        boolean onClimbable = entity instanceof LivingEntity livingEntity && livingEntity.onClimbable();
        if (entity.getDeltaMovement().y < 0 || onClimbable) {
            entity.makeStuckInBlock(state, new Vec3(1, 0.8, 1));
        }
    }

    @Override
    public MapCodec<? extends WitheringBrambleBlossomBlock> codec() {
        return CODEC;
    }
}
