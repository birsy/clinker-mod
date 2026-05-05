package birsy.clinker.common.world.level.gen.content.surface.decorator;

import birsy.clinker.common.world.block.FallingLayerBlock;
import birsy.clinker.common.world.level.gen.system.noise.CachedNoiseContext;
import birsy.clinker.common.world.level.gen.system.noise.NoiseFieldCache;
import birsy.clinker.common.world.level.gen.system.surface.decorator.SurfaceDecorator;
import birsy.clinker.core.registry.ClinkerBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
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
    public void decorateSurface(BlockPos.MutableBlockPos pos, Direction surfaceNormal,
                                int maxUpwardsOffset, int maxDownwardsOffset, int maximumDepth, boolean visibleToSky,
                                WorldGenLevel level, ChunkAccess chunk, CachedNoiseContext context, RandomSource random) {
        if (surfaceNormal != Direction.DOWN) return;

        float ditherRandom = (random.nextFloat() * 2 - 1) * 0.1F;

        double ashGroundNoise = context.retrieve(BASE_NOISE_2D[6], pos.getX(), pos.getY(), pos.getZ());
        boolean shouldPlaceAsh = ashGroundNoise + ditherRandom > -0.5;

        if (maxDownwardsOffset == 1) {
            double ashBorderNoise = context.retrieve(BASE_NOISE_2D_ALT[6], pos.getX(), pos.getY(), pos.getZ());
            if (ashBorderNoise > 0 && shouldPlaceAsh)
                chunk.setBlockState(pos, ClinkerBlocks.ASH.get().defaultBlockState(), false);
        } else if (maxDownwardsOffset < 2) {
            if (shouldPlaceAsh)
                chunk.setBlockState(pos, ClinkerBlocks.ASH.get().defaultBlockState(), false);

            if (maxUpwardsOffset > 0) {
                float ditherRandomAshDuneAmount = random.nextFloat() * -0.15F;
                double ashNoiseSample = context.retrieve(BASE_NOISE_2D[3], pos.getX(), pos.getY(), pos.getZ());
                ashNoiseSample += ditherRandomAshDuneAmount;

                int ashAmount = (int) Mth.map(ashNoiseSample, -1.0, 1.0, -1, 6);
                if (ashAmount > 1) {
                    BlockPos ashPos = pos.above();
                    boolean waterlogged = level.getFluidState(ashPos).is(Fluids.WATER);
                    chunk.setBlockState(ashPos,
                            ClinkerBlocks.ASH_LAYER.get().defaultBlockState()
                                    .setValue(FallingLayerBlock.LAYERS, ashAmount)
                                    .setValue(FallingLayerBlock.WATERLOGGED, waterlogged),
                            false);
                }
            }
        }
    }
}
