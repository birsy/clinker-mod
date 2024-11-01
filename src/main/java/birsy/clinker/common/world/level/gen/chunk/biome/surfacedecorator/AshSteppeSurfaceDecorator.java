package birsy.clinker.common.world.level.gen.chunk.biome.surfacedecorator;

import birsy.clinker.common.world.block.AshLayerBlock;
import birsy.clinker.core.registry.ClinkerBlocks;
import birsy.clinker.core.util.MathUtils;
import birsy.clinker.core.util.noise.FastNoiseLite;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.chunk.ChunkAccess;

public class AshSteppeSurfaceDecorator extends SurfaceDecorator {
    private static FastNoiseLite noise = Util.make(() -> {
        FastNoiseLite n = new FastNoiseLite();
        n.SetNoiseType(FastNoiseLite.NoiseType.OpenSimplex2S);
        n.SetFrequency(0.04F);
        n.SetFractalType(FastNoiseLite.FractalType.FBm);
        n.SetFractalOctaves(1);
        n.SetFractalLacunarity(0.5F);
        n.SetFractalGain(1.7F);
        n.SetFractalWeightedStrength(0.0F);
        return n;
    });
    public AshSteppeSurfaceDecorator() {}

    @Override
    public void buildSurface(ChunkAccess chunk, BlockPos.MutableBlockPos pos, int seaLevel, boolean canSeeSun, int depth, int maxElevationIncrease, int maxElevationDecrease, DerivativeProvider noiseDerivative) {
        if (!canSeeSun && pos.getY() < 130) return;

        float ditherRandom = (this.random.nextFloat() * 2) - 1;
        ditherRandom *= 0.3F;
        boolean shouldPlaceAsh = noise.GetNoise(pos.getX(), pos.getZ()) + ditherRandom > -0.5;

        if (maxElevationDecrease == 1) {
            if (noise.GetNoise(pos.getX(), 1000, pos.getZ()) > 0 && shouldPlaceAsh) {
                chunk.setBlockState(pos, ClinkerBlocks.ASH.get().defaultBlockState(), false);
            }
        } else if (maxElevationDecrease < 2) {
            if (shouldPlaceAsh) {
                chunk.setBlockState(pos, ClinkerBlocks.ASH.get().defaultBlockState(), false);
            }
            if (maxElevationIncrease > 0) {
                float ditherRandomAshDuneAmount = this.random.nextFloat();
                ditherRandomAshDuneAmount *= -0.15F;
                double noiseSample = noise.GetNoise(pos.getX() * 5, 0, pos.getZ() * 5) + ditherRandomAshDuneAmount;
                int ashAmount = ((int) MathUtils.mapRange(-1.0, 1.0, -1, 6, noiseSample));
                if (ashAmount > 1) chunk.setBlockState(pos.above(), ClinkerBlocks.ASH_LAYER.get().defaultBlockState().setValue(AshLayerBlock.LAYERS, ashAmount), false);
            }
        }
    }

    @Override
    public boolean shouldCalculateElevationChange(boolean canSeeSun, int y) {
        return canSeeSun || y >= 130;
    }
}
