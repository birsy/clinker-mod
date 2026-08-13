package birsy.clinker.common.block.plant;

import birsy.clinker.common.block.BidirectionalPipeBlock;
import birsy.clinker.core.registry.ClinkerBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class TaprootsBlock extends BidirectionalPipeBlock {
    public TaprootsBlock(Properties properties) {
        super(properties, 6 / 16F, 4 / 16F);
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        super.animateTick(state, level, pos, random);
        if (state.getValue(WATERLOGGED)) return;

        BlockPos.MutableBlockPos mPos = pos.mutable();
        Direction inDir = state.getValue(INPUT_FACE),
                outDir = state.getValue(OUTPUT_FACE);
        BlockState inState = level.getBlockState(mPos.set(pos).move(inDir)),
                outState = level.getBlockState(mPos.set(pos).move(outDir));
        boolean connectedIn = (inState.is(this) && (inState.getValue(INPUT_FACE) == inDir.getOpposite() || inState.getValue(OUTPUT_FACE) == inDir.getOpposite())) | inState.is(ClinkerBlocks.TAPROOT_BURL),
                connectedOut = (outState.is(this) && (outState.getValue(INPUT_FACE) == outDir.getOpposite() || outState.getValue(OUTPUT_FACE) == outDir.getOpposite())) | outState.is(ClinkerBlocks.TAPROOT_BURL);
        if (connectedIn == connectedOut) return;

        BlockState dripState = connectedIn ? outState : inState;
        if (dripState.isSolid()) return;

        Direction dripDir = connectedIn ? outDir : inDir;
        if (dripDir == Direction.UP) return;

        Direction localXDir = dripDir.getAxis().isHorizontal() ? dripDir.getClockWise() : Direction.EAST;
        Direction localYDir = dripDir.getAxis().isHorizontal() ? Direction.UP : Direction.NORTH;

        int particleCount = dripDir.getAxis().isHorizontal() ?
                (random.nextInt(8) == 0 ? 1 : 0) :
                (random.nextInt(5) == 0 ? 1 : 0);
        for (int i = 0; i < particleCount; i++) {
            int tubeX = random.nextBoolean() ? -1 : 1, tubeY = random.nextBoolean() ? -1 : 1;
            double localX = random.triangle(tubeX * 0.25 , 1.0/16.0),
                    localY = random.triangle(tubeY * 0.25, 1.0/16.0);

            double x = pos.getX() + localX * localXDir.getStepX() + localY * localYDir.getStepX() + ((dripDir.getStepX() * 1.05) * 0.5 + 0.5),
                    y = pos.getY() + localX * localXDir.getStepY() + localY * localYDir.getStepY() + ((dripDir.getStepY() * 1.05) * 0.5 + 0.5) - 0.125,
                    z = pos.getZ() + localX * localXDir.getStepZ() + localY * localYDir.getStepZ() + ((dripDir.getStepZ() * 1.05) * 0.5 + 0.5);
            level.addParticle(
                    ParticleTypes.DRIPPING_HONEY,
                    x, y, z, 0, 0, 0
            );
        }
    }
}
