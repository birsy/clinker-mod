package birsy.clinker.common.world.level.gen.chunk.biome.terrainprovider;

import birsy.clinker.common.world.level.gen.MetaChunk;
import birsy.clinker.common.world.level.gen.NoiseCache;
import birsy.clinker.common.world.level.gen.NoiseProviders;
import birsy.clinker.core.util.MathUtils;
import birsy.clinker.core.util.noise.CachedFastNoise;
import birsy.clinker.core.util.noise.FastNoiseLite;
import net.minecraft.Util;
import net.minecraft.world.level.chunk.ChunkAccess;

public class AshSteppeTerrainProvider extends TerrainProvider {
    private static final CachedFastNoise NOISE = Util.make(() -> {
        FastNoiseLite n = new FastNoiseLite();
        n.SetNoiseType(FastNoiseLite.NoiseType.OpenSimplex2S);
        n.SetFrequency(0.04F);
        n.SetFractalType(FastNoiseLite.FractalType.FBm);
        n.SetFractalOctaves(1);
        n.SetFractalLacunarity(0.5F);
        n.SetFractalGain(1.7F);
        n.SetFractalWeightedStrength(0.0F);
        return new CachedFastNoise(n);
    });
    
    @Override
    public float sample(ChunkAccess chunk, long seed, double x, double y, double z, NoiseCache sampler) {
        NOISE.getNoise().SetSeed((int) seed);

        double unmodifiedY = y;
        float heightFactor = 0.3F;

        float yScale = 0.2F;
        float scaledSeaLevel = 150*yScale;
        y *= yScale;

        if (y - scaledSeaLevel > 2.0F / heightFactor) return -1.0F;

        float freq = 0.2F;
        float baseNoise = (float) NOISE.get(x * freq, y * freq, z * freq);
        freq = 0.8F;
        float detailNoise = (float) NOISE.get(x * freq, y * freq + 800.0F, z * freq);

        float totalNoise = (1.0F - Math.abs(detailNoise))*0.2F - 1.0F;

        freq = 0.2F;
        float cliffExistenceNoise = (float) NOISE.get(x * freq, z * freq);
        float threshold = 0.7F;
        cliffExistenceNoise -= threshold;
        if (cliffExistenceNoise < 0.0) {
            cliffExistenceNoise *= 3.0F;
        }

        // terraces
        for (int i = 2; i < 5; i++) {
            float tFreq = 0.6F;
            float terracedNoise = (float) (baseNoise + NOISE.get(x * tFreq, y * tFreq + (i * 500), z * tFreq));
            tFreq = 1.0F;
            terracedNoise += (float) (NOISE.get(x * tFreq, y * tFreq + (i * 500), z * tFreq) * 0.4F);
            terracedNoise = terrace(terracedNoise + cliffExistenceNoise, i, 0.2F, 0.95F);
            if (terracedNoise > totalNoise) {
                totalNoise = terracedNoise;
                // set surface flag for not ash dunes
            }
        }

        float surfaceNoise = (float) ((totalNoise + baseNoise * 0.1F + detailNoise * 0.1F) - ((y - scaledSeaLevel) * heightFactor));
        return surfaceNoise;
    }

    private static float terrace(float noiseVal, float stepCount, float lowerSteepness, float upperSteepness) {
        float steps = stepCount * 0.5F;
        float x = noiseVal * steps + 0.5F;
        float expBase = (2*(x - Math.round(x)));
        float steepness = expBase > 0.0 ? lowerSteepness : upperSteepness;
        float expPower = 1.0f / (1.0f - steepness);
        return (float) ((Math.round(x) + 0.5 * (Math.pow(Math.abs(expBase), expPower) * Math.signum(expBase))) - 0.5F) / steps;
    }
}
