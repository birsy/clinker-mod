package birsy.clinker.common.world.level.chunk.gen;

import birsy.clinker.core.util.noise.FastNoiseLite;
import birsy.clinker.core.util.noise.VoronoiGenerator;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.XoroshiroRandomSource;

/**
 * Samples the terrain noise's density at a given position, or set of positions.
 */
public class OthershoreNoiseSampler {
    private final long seed;
    private RandomSource random;

    /*//Noise determining where ash plains and oceans lie. Very, very large, sampled in 2D.
    public FastNoiseLite continentalNoiseGenerator;
    //Noise determining the basic height of the terrain. Ridged, sampled in 2D.
    public FastNoiseLite heightmapNoiseGenerator;
    //Noise determining the 3D terrain shape. Ridged, sampled in 3D.
    public FastNoiseLite terrainNoiseGenerator;
    //Noise determining where mountain peaks and river valleys lie. Ridged, sampled in 2D.
    public FastNoiseLite peakValleyNoiseGenerator;
    //Noise determining erosion levels for terrain generation. Sampled in 2D.
    public FastNoiseLite erosionNoiseGenerator;
    //Noise determining how much influence 3D noise has over the terrain. Large, sampled in 3D.
    public FastNoiseLite factorNoiseGenerator;

    //Noise determining the "terracing" effect along canyon.
    public FastNoiseLite terraceNoiseGenerator;
    //Noise determining the strength of the terraces across a large area.
    public FastNoiseLite terraceStrengthNoiseGenerator;
    //Noise for modulating the basic heightmap with overhangs.
    public FastNoiseLite overhangNoiseGenerator;*/

    public FastNoiseLite largeNoise;
    public FastNoiseLite detailNoise;
    public FastNoiseLite cellularNoise;
    public VoronoiGenerator voronoiGenerator;

    public OthershoreNoiseSampler(long seed) {
        this.seed = seed;
        this.random = new XoroshiroRandomSource(seed);
        this.initNoise((int) seed);
    }

    public OthershoreNoiseSampler setSeed(long seed) {
        this.random.setSeed(seed);
        this.largeNoise.SetSeed((int) seed);
        this.detailNoise.SetSeed((int) seed + 1);
        this.cellularNoise.SetSeed((int) seed + 2);
        this.voronoiGenerator.setSeed(seed);
        return this;
    }

    private void initNoise(int seed) {
        this.largeNoise = new FastNoiseLite(seed);
        this.largeNoise.SetNoiseType(FastNoiseLite.NoiseType.OpenSimplex2S);
        this.largeNoise.SetFrequency(0.04F);
        this.largeNoise.SetFractalType(FastNoiseLite.FractalType.FBm);
        this.largeNoise.SetFractalOctaves(1);
        this.largeNoise.SetFractalLacunarity(0.5);
        this.largeNoise.SetFractalGain(1.7F);
        this.largeNoise.SetFractalWeightedStrength(0.0F);

        this.detailNoise = new FastNoiseLite(seed + 1);
        this.detailNoise.SetNoiseType(FastNoiseLite.NoiseType.ValueCubic);
        this.detailNoise.SetFrequency(0.1F);
        this.detailNoise.SetFractalType(FastNoiseLite.FractalType.None);

        this.cellularNoise = new FastNoiseLite(seed + 2);
        this.cellularNoise.SetNoiseType(FastNoiseLite.NoiseType.Cellular);
        this.cellularNoise.SetFrequency(0.1F);
        this.cellularNoise.SetFractalType(FastNoiseLite.FractalType.None);

        this.voronoiGenerator = new VoronoiGenerator(seed);
        /*this.continentalNoiseGenerator = new FastNoiseLite((int) (seed + random.nextLong()));
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
        this.factorNoiseGenerator.SetFractalType(FastNoiseLite.FractalType.None);*/
    }
}
