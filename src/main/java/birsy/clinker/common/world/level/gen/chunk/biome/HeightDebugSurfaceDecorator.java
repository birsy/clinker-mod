package birsy.clinker.common.world.level.gen.chunk.biome;

import birsy.clinker.common.world.block.AshLayerBlock;
import birsy.clinker.core.registry.ClinkerBlocks;
import birsy.clinker.core.util.MathUtils;
import birsy.clinker.core.util.noise.FastNoiseLite;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;

public class HeightDebugSurfaceDecorator extends SurfaceDecorator {
    private static FastNoiseLite noise = Util.make(() -> {
        FastNoiseLite n = new FastNoiseLite();
        n.SetNoiseType(FastNoiseLite.NoiseType.OpenSimplex2S);
        n.SetFrequency(0.04F);
        n.SetFractalType(FastNoiseLite.FractalType.FBm);
        n.SetFractalOctaves(1);
        n.SetFractalLacunarity(0.5);
        n.SetFractalGain(1.7F);
        n.SetFractalWeightedStrength(0.0F);
        return n;
    });
    public HeightDebugSurfaceDecorator() {}

    @Override
    public void buildSurface(BlockPos.MutableBlockPos pos, int seaLevel, boolean canSeeSun, float noiseDerivative, int maxElevationIncrease, int maxElevationDecrease, ChunkAccess chunk, NoiseGeneratorSettings settings) {
        if (!canSeeSun && pos.getY() < 130) {
            return;
        }

        float ditherRandom = (this.random.nextFloat() * 2) - 1;
        ditherRandom *= 0.3F;
        boolean shouldPlaceAsh = this.noise.GetNoise(pos.getX(), pos.getZ()) + ditherRandom > -0.5;

        if (maxElevationDecrease > 0 && maxElevationDecrease < 2) {
            if (this.noise.GetNoise(pos.getX(), 1000, pos.getZ()) > 0 && shouldPlaceAsh) {
                chunk.setBlockState(pos, ClinkerBlocks.ASH.get().defaultBlockState(), false);
            }
        } else if (maxElevationDecrease < 2) {
            if (shouldPlaceAsh) {
                chunk.setBlockState(pos, ClinkerBlocks.ASH.get().defaultBlockState(), false);
            }
            if (maxElevationIncrease > 0) {
                float ditherRandomAshDuneAmount = this.random.nextFloat();
                ditherRandomAshDuneAmount *= -0.15F;
                double noiseSample = this.noise.GetNoise(pos.getX() * 5, 0, pos.getZ() * 5) + ditherRandomAshDuneAmount;
                int ashAmount = ((int) MathUtils.mapRange(-1.0, 1.0, -1, 6, noiseSample));
                if (ashAmount > 1) {
                    chunk.setBlockState(pos.above(), ClinkerBlocks.ASH_LAYER.get().defaultBlockState().setValue(AshLayerBlock.LAYERS, ashAmount), false);
                }
            }
        }
        //chunk.setBlockState(pos, elevationChangeToState[Mth.clamp(Math.abs(maxNeighborElevationChange), 0, elevationChangeToState.length - 1)], false);
    }
}
