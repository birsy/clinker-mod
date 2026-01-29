package birsy.clinker.common.world.level.gen.content.surface.decorator;

import birsy.clinker.common.world.block.FallingLayerBlock;
import birsy.clinker.common.world.level.gen.system.noise.NoiseContext;
import birsy.clinker.common.world.level.gen.system.noise.NoiseFieldCache;
import birsy.clinker.common.world.level.gen.system.surface.decorator.SurfaceDecorationContext;
import birsy.clinker.common.world.level.gen.system.surface.decorator.SurfaceDecorator;
import birsy.clinker.core.registry.ClinkerBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.material.Fluids;

import static birsy.clinker.core.registry.worldgen.ClinkerNoiseComputers.*;

public class AshSteppeSurfaceDecorator extends SurfaceDecorator {

    public AshSteppeSurfaceDecorator() {}

    @Override
    public void prefillNoiseFields(NoiseFieldCache cache) {
        cache.fillNoiseField(BASE_NOISE_2D[6]);
        cache.fillNoiseField(BASE_NOISE_2D_ALT[6]);
        cache.fillNoiseField(BASE_NOISE_2D[3]);
    }

    @Override
    public void decorateSurface(BlockPos.MutableBlockPos pos, int seaLevel, ChunkAccess chunk, NoiseContext noiseContext, RandomSource random, SurfaceDecorationContext surfaceContext) {
        if (pos.getY() < (surfaceContext.surfaceHeight() - 20)) return;

        float ditherRandom = (random.nextFloat() * 2) - 1;
        ditherRandom *= 0.3F;

        double ashGroundNoise = noiseContext.retrieve(BASE_NOISE_2D[6], pos.getX(), pos.getY(), pos.getZ());
        boolean shouldPlaceAsh = ashGroundNoise + ditherRandom > -0.5;

        int maxElevationDecrease = surfaceContext.maxElevationDecrease(),
            maxElevationIncrease = surfaceContext.maxElevationIncrease();
        if (maxElevationDecrease == 1) {
            double ashBorderNoise = noiseContext.retrieve(BASE_NOISE_2D_ALT[6], pos.getX(), pos.getY(), pos.getZ());
            if (ashBorderNoise > 0 && shouldPlaceAsh) {
                chunk.setBlockState(pos, ClinkerBlocks.ASH.get().defaultBlockState(), false);
            }
        } else if (maxElevationDecrease < 2) {
            if (shouldPlaceAsh) {
                chunk.setBlockState(pos, ClinkerBlocks.ASH.get().defaultBlockState(), false);
            }
            if (maxElevationIncrease > 0) {
                float ditherRandomAshDuneAmount = random.nextFloat();
                ditherRandomAshDuneAmount *= -0.15F;

                double ashNoiseSample = noiseContext.retrieve(BASE_NOISE_2D[3], pos.getX(), pos.getY(), pos.getZ());
                ashNoiseSample += ditherRandomAshDuneAmount;

                int ashAmount = ((int) Mth.map(ashNoiseSample, -1.0, 1.0, -1, 6));
                if (ashAmount > 1) {
                    BlockPos ashPos = pos.above();
                    boolean waterlogged = chunk.getFluidState(ashPos).is(Fluids.WATER);
                    chunk.setBlockState(ashPos,
                            ClinkerBlocks.ASH_LAYER.get().defaultBlockState()
                                    .setValue(FallingLayerBlock.LAYERS, ashAmount)
                                    .setValue(FallingLayerBlock.WATERLOGGED, waterlogged),
                            false);
                }

            }
        }
    }

    public boolean shouldCalculateElevationChange(boolean visibleToSky, int y, double surfaceHeight) {
        return y >= (surfaceHeight - 20);
    }
}
