package birsy.clinker.common.world.level.gen.content.surface.decorator;

import birsy.clinker.common.world.block.FallingLayerBlock;
import birsy.clinker.common.world.level.gen.OthershoreBiomeSource;
import birsy.clinker.common.world.level.gen.system.noise.NoiseContext;
import birsy.clinker.common.world.level.gen.system.noise.NoiseFieldCache;
import birsy.clinker.common.world.level.gen.system.surface.decorator.SurfaceDecorator;
import birsy.clinker.core.registry.ClinkerBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;

import static birsy.clinker.core.registry.worldgen.ClinkerNoiseComputers.BASE_NOISE_2D;
import static birsy.clinker.core.registry.worldgen.ClinkerNoiseComputers.BASE_NOISE_2D_ALT;

public class HeathSurfaceDecorator extends SurfaceDecorator {
    @Override
    public void prefillNoiseFields(NoiseFieldCache cache) {
        cache.fillNoiseField(BASE_NOISE_2D[4]);
        cache.fillNoiseField(BASE_NOISE_2D[5]);
        cache.fillNoiseField(BASE_NOISE_2D_ALT[5]);
        cache.fillNoiseField(BASE_NOISE_2D[6]);
        cache.fillNoiseField(BASE_NOISE_2D[7]);
        cache.fillNoiseField(BASE_NOISE_2D_ALT[7]);
        cache.fillNoiseField(BASE_NOISE_2D[8]);
    }

    @Override
    public void decorateSurface(ChunkAccess chunk, BlockPos.MutableBlockPos pos, int seaLevel, boolean canSeeSun, int depth, int maxElevationIncrease, int maxElevationDecrease, int surfaceHeight, double surfaceHeightGradient, NoiseContext context, RandomSource random) {
        int x = pos.getX(), z = pos.getZ();
        double wiggleNoise = context.retrieve(BASE_NOISE_2D[4], x, 0, z);

        double cliff0Fac = context.retrieve(BASE_NOISE_2D[6], x, 0, z);
        double cliff0 = Math.pow(Math.abs(cliff0Fac), 1 / 12.0) * Math.signum(cliff0Fac) * 8;

        double cliff1Fac = context.retrieve(BASE_NOISE_2D_ALT[7], x, 0, z) - 0.5;
        double cliff1 = Math.pow(Math.abs(cliff1Fac), 1 / 24.0) * Math.signum(cliff1Fac) * 30;
        double cliff1Mask = context.retrieve(BASE_NOISE_2D[8], x, 0, z) * 0.5 + 0.5;
        cliff1 *= cliff1Mask;

        double surfaceFac = context.retrieve(BASE_NOISE_2D[7], x, 0, z);
        double surface = surfaceFac * 8;

        boolean placeStone = false;

        if (cliff0 > surface && cliff0 > cliff1) {
            placeStone = cliff0Fac + wiggleNoise * 0.1 < 0.24;
        } else if (cliff1 > surface && cliff1 > cliff0) {
            placeStone = cliff1Fac + wiggleNoise * 0.1 < 0.2;
        }

        int offset = 1;
        if (!placeStone) {
            double erosionMask = context.retrieve(BASE_NOISE_2D[5], x, 0, z);
            erosionMask += random.triangle(0, 0.5);

            boolean placeSoil = (erosionMask < 0 || maxElevationDecrease < 1) && maxElevationDecrease <= 3;
            placeSoil &= context.retrieve(BASE_NOISE_2D_ALT[5], x, 0, z) + random.triangle(0, 0.2) < 0.5;

            BlockState soilState = ClinkerBlocks.PEAT_MOSS.get().defaultBlockState();
            if (wiggleNoise - 0.3 + random.triangle(0, 0.25) > 0) {
                soilState = ClinkerBlocks.ASHEN_REGOLITH.get().defaultBlockState();
            }
            chunk.setBlockState(pos, placeSoil ? soilState : ClinkerBlocks.BRIMSTONE.get().defaultBlockState(), false);

            if (maxElevationIncrease == 1) {
                double ashNoise = wiggleNoise + 0.5;
                ashNoise += random.triangle(0, 0.5);
                int ashAmount = (int) (Math.clamp(ashNoise, 0, 1) * 5);
                if (ashAmount > 0) {
                    pos.move(0, 1, 0);
                    chunk.setBlockState(pos,
                            ClinkerBlocks.ASH_LAYER.get().defaultBlockState()
                            .setValue(FallingLayerBlock.LAYERS, ashAmount),
                            false);
                    pos.move(0, -1, 0);
                }

            }

            if (placeSoil) {
                int soilDepth = random.nextIntBetweenInclusive(1, 2) - maxElevationDecrease;
                for (int i = 0; i < Math.min(soilDepth, depth); i++) {
                    pos.move(0, -1, 0);
                    offset++;
                    chunk.setBlockState(pos, soilState, false);
                }
            }
        }

//        int stoneDepth = Math.min(10, depth) - offset;
//        for (int i = 0; i < stoneDepth; i++) {
//            pos.move(0, -1, 0);
//            chunk.setBlockState(pos, ClinkerBlocks.CAPSTONE.get().defaultBlockState(), false);
//        }
    }

    public boolean shouldCalculateElevationChange(boolean canSeeSun, int y, int surfaceHeight) {
        return y >= surfaceHeight - 20;
    }
}
