package birsy.clinker.common.level.chunk;

import birsy.clinker.core.util.MathUtils;
import birsy.clinker.core.util.noise.FastNoiseLite;
import com.ibm.icu.impl.Pair;
import net.minecraft.util.Mth;

import java.util.Random;

/**
 * Samples the terrain noise's density at a given position, or set of positions.
 */
public class OthershoreNoiseSampler {
    private final long seed;
    private Random random;

    //Noise determining where ash plains and oceans lie. Very, very large, sampled in 2D.
    private FastNoiseLite continentalNoiseGenerator;
    //Noise determining the basic height of the terrain. Ridged, sampled in 2D.
    private FastNoiseLite heightmapNoiseGenerator;
    //Noise determining the 3D terrain shape. Ridged, sampled in 3D.
    private FastNoiseLite terrainNoiseGenerator;
    //Noise determining where mountain peaks and river valleys lie. Ridged, sampled in 2D.
    private FastNoiseLite peakValleyNoiseGenerator;
    //Noise determining erosion levels for terrain generation. Sampled in 2D.
    private FastNoiseLite erosionNoiseGenerator;
    //Noise determining how much influence 3D noise has over the terrain. Large, sampled in 3D.
    private FastNoiseLite factorNoiseGenerator;

    //Noise determining the "terracing" effect along canyon.
    private FastNoiseLite terraceNoiseGenerator;
    //Noise determining the strength of the terraces across a large area.
    private FastNoiseLite terraceStrengthNoiseGenerator;
    //Noise for modulating the basic heightmap with overhangs.
    private FastNoiseLite overhangNoiseGenerator;

    public OthershoreNoiseSampler(long seed) {
        this.seed = seed;
        this.random = new Random(seed);

        this.initNoise((int) seed);
    }

    private void initNoise(int seed) {
        this.continentalNoiseGenerator = new FastNoiseLite((int) (seed + random.nextLong()));
        this.continentalNoiseGenerator.SetNoiseType(FastNoiseLite.NoiseType.OpenSimplex2);
        this.continentalNoiseGenerator.SetFrequency(0.002F);
        this.continentalNoiseGenerator.SetFractalType(FastNoiseLite.FractalType.None);

        this.heightmapNoiseGenerator = new FastNoiseLite((int) (seed + random.nextLong()));
        this.heightmapNoiseGenerator.SetNoiseType(FastNoiseLite.NoiseType.OpenSimplex2);
        this.heightmapNoiseGenerator.SetFrequency(0.04F);
        this.heightmapNoiseGenerator.SetFractalType(FastNoiseLite.FractalType.FBm);
        this.heightmapNoiseGenerator.SetFractalOctaves(2);
        this.heightmapNoiseGenerator.SetFractalGain(0.9F);
        this.heightmapNoiseGenerator.SetFractalWeightedStrength(0.8F);
        this.heightmapNoiseGenerator.SetDomainWarpType(FastNoiseLite.DomainWarpType.BasicGrid);
        this.heightmapNoiseGenerator.SetDomainWarpAmp(100.0F);

        this.terrainNoiseGenerator = new FastNoiseLite((int) (seed + random.nextLong()));
        this.terrainNoiseGenerator.SetNoiseType(FastNoiseLite.NoiseType.ValueCubic);
        this.terrainNoiseGenerator.SetFrequency(0.03F);
        this.terrainNoiseGenerator.SetFractalType(FastNoiseLite.FractalType.FBm);
        this.terrainNoiseGenerator.SetFractalOctaves(3);
        this.terrainNoiseGenerator.SetFractalGain(0.5F);
        this.terrainNoiseGenerator.SetFractalWeightedStrength(0.5F);
        this.terrainNoiseGenerator.SetDomainWarpType(FastNoiseLite.DomainWarpType.BasicGrid);
        this.terrainNoiseGenerator.SetDomainWarpAmp(100.0F);
        this.terrainNoiseGenerator.SetRotationType3D(FastNoiseLite.RotationType3D.ImproveXZPlanes);

        this.peakValleyNoiseGenerator = new FastNoiseLite((int) (seed + random.nextLong()));
        this.peakValleyNoiseGenerator.SetNoiseType(FastNoiseLite.NoiseType.ValueCubic);
        this.peakValleyNoiseGenerator.SetFrequency(0.0002F);
        this.peakValleyNoiseGenerator.SetFractalType(FastNoiseLite.FractalType.Ridged);
        this.peakValleyNoiseGenerator.SetFractalOctaves(4);
        this.peakValleyNoiseGenerator.SetFractalLacunarity(1.5F);
        this.peakValleyNoiseGenerator.SetFractalGain(0.5F);
        this.peakValleyNoiseGenerator.SetDomainWarpType(FastNoiseLite.DomainWarpType.BasicGrid);
        this.peakValleyNoiseGenerator.SetDomainWarpAmp(35.0F);

        this.erosionNoiseGenerator = new FastNoiseLite((int) (seed + random.nextLong()));
        this.erosionNoiseGenerator.SetNoiseType(FastNoiseLite.NoiseType.ValueCubic);
        this.erosionNoiseGenerator.SetFrequency(0.02F);
        this.erosionNoiseGenerator.SetFractalType(FastNoiseLite.FractalType.None);

        this.factorNoiseGenerator = new FastNoiseLite((int) (seed + random.nextLong()));
        this.factorNoiseGenerator.SetNoiseType(FastNoiseLite.NoiseType.ValueCubic);
        this.factorNoiseGenerator.SetFrequency(0.02F);
        this.factorNoiseGenerator.SetFractalType(FastNoiseLite.FractalType.None);
    }

    private float sampleNoise(int x, int y, int z) {
        Pair<Float, Float> surfaceNoise = sampleTerracedSurfaceNoise(x, y, z);
<<<<<<< Updated upstream
        float overhangNoise = overhangNoiseGenerator.GetNoise(x, y, z);
=======
        float overhangNoise = (float) overhangNoiseGenerator.GetNoise(x, y, z);
>>>>>>> Stashed changes
        float overhangNoiseIntensity = surfaceNoise.second * MathUtils.invert(Mth.clamp(Math.abs(y - surfaceNoise.first) * 0.125F, 0, 1));

        return surfaceNoise.first * (overhangNoise * overhangNoiseIntensity);
    }

    //This base layer will return a distance from Y. This is later modulated with some funky shit to allow overhangs, if only slightly. Should work!
    private Pair<Float, Float> sampleTerracedSurfaceNoise(int x, int y, int z) {
<<<<<<< Updated upstream
        float baseNoise = (heightmapNoiseGenerator.GetNoise(x, z) + 1) * 0.5F;
        float terraceNoise = MathUtils.mapRange(-1, 1, 0.07F, 0.6F, terraceNoiseGenerator.GetNoise(x, z));
        float terraceStrengthGenerator = MathUtils.mapRange(-1, 1, 0.05F, 0.5F, terraceStrengthNoiseGenerator.GetNoise(x, z));
=======
        float baseNoise = (float) ((heightmapNoiseGenerator.GetNoise(x, z) + 1) * 0.5F);
        float terraceNoise = MathUtils.mapRange(-1, 1, 0.07F, 0.6F, (float) terraceNoiseGenerator.GetNoise(x, z));
        float terraceStrengthGenerator = MathUtils.mapRange(-1, 1, 0.05F, 0.5F, (float) terraceStrengthNoiseGenerator.GetNoise(x, z));
>>>>>>> Stashed changes

        Pair<Float, Float> terrace = MathUtils.terrace(baseNoise, terraceNoise, terraceStrengthGenerator);
        float final2DNoise = (terrace.first * 2) - 1;
        float terrainHeight = (final2DNoise * 70) + 55;

        return Pair.of((terrainHeight - y) * 0.05F, terrace.second);
    }

    public enum HeightType {
        MOUNTAIN,
        PLATEAU,
        SURFACE,
        CANYON,
        COASTAL,
        WATER;
    }
}
