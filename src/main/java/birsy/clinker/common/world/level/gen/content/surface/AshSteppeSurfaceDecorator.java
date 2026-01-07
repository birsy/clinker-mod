package birsy.clinker.common.world.level.gen.content.surface;

import birsy.clinker.common.world.level.gen.OthershoreBiomeSource;
import birsy.clinker.common.world.level.gen.system.noise.NoiseFieldCache;
import birsy.clinker.common.world.level.gen.system.surface.SurfaceDecorator;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.chunk.ChunkAccess;

public class AshSteppeSurfaceDecorator {//extends SurfaceDecorator {

    public AshSteppeSurfaceDecorator() {}

    public void buildSurface(ChunkAccess chunk, BlockPos.MutableBlockPos pos, int seaLevel, boolean canSeeSun, int depth, int maxElevationIncrease, int maxElevationDecrease, int surfaceHeight, NoiseFieldCache cache, RandomSource random) {
//        NoiseFieldCache executor = noiseContext.noiseComputerExecutor();
//        if (!canSeeSun && pos.getY() < 130) return;
//
//        float ditherRandom = (random.nextFloat() * 2) - 1;
//        ditherRandom *= 0.3F;
//
//        double ashGroundNoise = executor.compute(pos.getX(), pos.getY(), pos.getZ(), OthershoreNoiseComputers.BASE_NOISE_2D[6]);
//        boolean shouldPlaceAsh = ashGroundNoise + ditherRandom > -0.5;
//
//        if (maxElevationDecrease == 1) {
//            double ashBorderNoise = executor.compute(pos.getX(), pos.getY(), pos.getZ(), OthershoreNoiseComputers.BASE_NOISE_2D_ALT[6]);
//            if (ashBorderNoise > 0 && shouldPlaceAsh) {
//                chunk.setBlockState(pos, ClinkerBlocks.ASH.get().defaultBlockState(), false);
//            }
//        } else if (maxElevationDecrease < 2) {
//            if (shouldPlaceAsh) {
//                chunk.setBlockState(pos, ClinkerBlocks.ASH.get().defaultBlockState(), false);
//            }
//            if (maxElevationIncrease > 0) {
//                float ditherRandomAshDuneAmount = random.nextFloat();
//                ditherRandomAshDuneAmount *= -0.15F;
//
//                double ashNoiseSample = executor.compute(pos.getX(), pos.getY(), pos.getZ(), OthershoreNoiseComputers.BASE_NOISE_2D[3]);
//                ashNoiseSample += ditherRandomAshDuneAmount;
//
//                int ashAmount = ((int) MathUtils.mapRange(-1.0, 1.0, -1, 6, ashNoiseSample));
//                if (ashAmount > 1) chunk.setBlockState(pos.above(), ClinkerBlocks.ASH_LAYER.get().defaultBlockState().setValue(FallingLayerBlock.LAYERS, ashAmount), false);
//            }
//        }
    }

    public boolean shouldCalculateElevationChange(boolean canSeeSun, int y) {
        return canSeeSun || y >= OthershoreBiomeSource.MIDDLE_SHELF_HEIGHT - 20;
    }
}
