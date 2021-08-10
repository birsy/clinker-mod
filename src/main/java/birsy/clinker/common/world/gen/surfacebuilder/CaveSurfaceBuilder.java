package birsy.clinker.common.world.gen.surfacebuilder;

import birsy.clinker.core.util.MathUtils;
import birsy.clinker.core.util.noise.FastNoiseLite;
import com.mojang.serialization.Codec;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.surfacebuilders.SurfaceBuilder;
import net.minecraft.world.gen.surfacebuilders.SurfaceBuilderConfig;

import java.util.Random;

public class CaveSurfaceBuilder extends SurfaceBuilder<SurfaceBuilderConfig> {
    private long seed;
    private FastNoiseLite spaghettiCaveNoise1;
    private FastNoiseLite spaghettiCaveNoise2;
    private FastNoiseLite caveSizeNoise;

    public CaveSurfaceBuilder(Codec<SurfaceBuilderConfig> codec) {
        super(codec);
    }

    @Override
    public void buildSurface(Random random, IChunk chunkIn, Biome biomeIn, int x, int z, int startHeight, double noise, BlockState defaultBlock, BlockState defaultFluid, int seaLevel, long seed, SurfaceBuilderConfig config) {
        BlockPos.Mutable blockPos = new BlockPos.Mutable(x, 255, z);
        float yMultiplier = 1.4F;

        final float averageCaveSize = 0.002F;
        final float minCaveSizeDeviation = -0.0019F;
        final float maxCaveSizeDeviation = 0.002F;

        int maxHeight = startHeight + 4;

        for (int y = 256; y > 0; y--) {
            blockPos.setPos(x, y, z);
            float bedrockDistance = MathHelper.clamp(MathUtils.mapRange(4.0F, 20.0F, 0.0F, 1.0F, y), 0, 1);

            float spaghettiSizeThreshold = this.caveSizeNoise.GetNoise(x, y, z);
            spaghettiSizeThreshold *= spaghettiSizeThreshold < 0 ? Math.abs(minCaveSizeDeviation) : Math.abs(maxCaveSizeDeviation);
            spaghettiSizeThreshold += averageCaveSize;
            spaghettiSizeThreshold *= bedrockDistance;

            float spaghettiCaveNoiseValue1 = this.spaghettiCaveNoise1.GetNoise(x, y * yMultiplier, z);
            float spaghettiCaveNoiseValue2 = this.spaghettiCaveNoise2.GetNoise(x, y * yMultiplier, z);

            //Some uhh math magic don't worry about it. Makes caves funny and noodly!
            float thresholdedCaveNoise = (this.invert(spaghettiCaveNoiseValue1 * spaghettiCaveNoiseValue1 + spaghettiCaveNoiseValue2 * spaghettiCaveNoiseValue2) - this.invert(spaghettiSizeThreshold)) / spaghettiSizeThreshold;

            if (thresholdedCaveNoise > 0.0F) {
                chunkIn.setBlockState(blockPos, defaultBlock, false);
            } else {
                chunkIn.setBlockState(blockPos, Blocks.CAVE_AIR.getDefaultState(), false);
            }
        }
    }

    @Override
    public void setSeed(long seed) {
        if (this.seed != seed || this.spaghettiCaveNoise1 == null || this.spaghettiCaveNoise2 == null || this.caveSizeNoise == null) {
            this.seed = seed;
            Random rand = new Random(seed);

            this.initNoise(rand);
        }
    }

    private void initNoise(Random rand) {
        this.spaghettiCaveNoise1 = new FastNoiseLite((int) (seed + rand.nextLong()));
        this.spaghettiCaveNoise1.SetNoiseType(FastNoiseLite.NoiseType.ValueCubic);
        this.spaghettiCaveNoise1.SetFrequency(0.04F);
        this.spaghettiCaveNoise1.SetFractalType(FastNoiseLite.FractalType.FBm);
        this.spaghettiCaveNoise1.SetFractalOctaves(2);
        this.spaghettiCaveNoise1.SetFractalGain(0.9F);
        this.spaghettiCaveNoise1.SetFractalWeightedStrength(0.8F);
        this.spaghettiCaveNoise1.SetDomainWarpType(FastNoiseLite.DomainWarpType.BasicGrid);
        this.spaghettiCaveNoise1.SetDomainWarpAmp(100.0F);
        this.spaghettiCaveNoise1.SetRotationType3D(FastNoiseLite.RotationType3D.ImproveXZPlanes);

        this.spaghettiCaveNoise2 = new FastNoiseLite((int) (seed + rand.nextLong()));
        this.spaghettiCaveNoise2.SetNoiseType(FastNoiseLite.NoiseType.ValueCubic);
        this.spaghettiCaveNoise2.SetFrequency(0.04F);
        this.spaghettiCaveNoise2.SetFractalType(FastNoiseLite.FractalType.FBm);
        this.spaghettiCaveNoise2.SetFractalOctaves(2);
        this.spaghettiCaveNoise2.SetFractalGain(0.9F);
        this.spaghettiCaveNoise2.SetFractalWeightedStrength(0.8F);
        this.spaghettiCaveNoise2.SetDomainWarpType(FastNoiseLite.DomainWarpType.BasicGrid);
        this.spaghettiCaveNoise2.SetDomainWarpAmp(100.0F);
        this.spaghettiCaveNoise2.SetRotationType3D(FastNoiseLite.RotationType3D.ImproveXZPlanes);

        this.caveSizeNoise = new FastNoiseLite((int) (seed + rand.nextLong()));
        this.caveSizeNoise.SetNoiseType(FastNoiseLite.NoiseType.ValueCubic);
        this.spaghettiCaveNoise2.SetFrequency(0.02F);
        this.caveSizeNoise.SetFractalType(FastNoiseLite.FractalType.None);
    }

    public static float invert(float input) {
        return (1 - input) * -1;
    }
}
