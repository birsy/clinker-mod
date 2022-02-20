package birsy.clinker.common.level;

import birsy.clinker.core.Clinker;
import birsy.clinker.core.util.MathUtils;
import birsy.clinker.core.util.noise.FastNoiseLite;
import birsy.clinker.core.util.noise.VoronoiGenerator;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.surfacebuilders.NetherForestSurfaceBuilder;
import net.minecraft.world.level.levelgen.surfacebuilders.SurfaceBuilderBaseConfiguration;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

import java.util.Random;

public class GenerationTestBuilder extends NetherForestSurfaceBuilder {
    private long seed;
    private FastNoiseLite noise1;

    private VoronoiGenerator voronoiNoise = new VoronoiGenerator(1337);

    public GenerationTestBuilder(Codec<SurfaceBuilderBaseConfiguration> config) {
        super(config);
    }

    public void apply(Random pRandom, ChunkAccess chunkIn, Biome pBiome, int x, int z, int pHeight, double pNoise, BlockState defaultBlock, BlockState defaultFluid, int pSeaLevel, int pMinSurfaceLevel, long pSeed, SurfaceBuilderBaseConfiguration pConfig) {
        voronoiNoise.setOffsetAmount(0.0);

        double scale = 15;
        Pair<double[], Vec2> voronoiSample = voronoiNoise.get2(x / scale, z / scale);
        double sample = (MathUtils.biasTowardsExtreme(noise1.GetNoise(voronoiSample.getSecond().x * scale, voronoiSample.getSecond().y * scale), 0.5, 2) * 60) + pSeaLevel;

        BlockPos.MutableBlockPos blockPos = new BlockPos.MutableBlockPos(x, chunkIn.getMaxBuildHeight(), z);
        for (int y = chunkIn.getMaxBuildHeight(); y > chunkIn.getMinBuildHeight(); y--) {
            blockPos.setY(y);

            if (y < sample) {
                chunkIn.setBlockState(blockPos, defaultBlock, false);
            } else {
                chunkIn.setBlockState(blockPos, Blocks.AIR.defaultBlockState(), false);
            }
        }

        Clinker.LOGGER.info("test");

    }

    @Override
    public void initNoise(long seed) {
        super.initNoise(seed);
        if (this.seed != seed || this.noise1 == null || this.voronoiNoise == null) {
            this.seed = seed;
            Random rand = new Random(seed);

            this.setupNoise(rand);
        }
    }

    private void setupNoise(Random rand) {
        Clinker.LOGGER.info("setting up noise!");

        this.noise1 = new FastNoiseLite((int) (seed + rand.nextLong()));
        this.noise1.SetNoiseType(FastNoiseLite.NoiseType.OpenSimplex2S);
        this.noise1.SetFrequency(0.04F);
        this.noise1.SetFractalType(FastNoiseLite.FractalType.FBm);
        this.noise1.SetFractalOctaves(3);
        this.noise1.SetFractalLacunarity(0.5);
        this.noise1.SetFractalGain(1.7F);
        this.noise1.SetFractalWeightedStrength(0.0F);

        this.voronoiNoise = new VoronoiGenerator(seed);
    }
}
