package birsy.clinker.common.level;

import birsy.clinker.core.Clinker;
import birsy.clinker.core.util.noise.FastNoiseLite;
import birsy.clinker.core.util.noise.IterativePsuedoEroder;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;

import java.util.Random;

public class GenerationTestBuilder {
    private long seed;
    private float frequency = 1.0F;

    private FastNoiseLite noise1;
    private IterativePsuedoEroder noise2 = new IterativePsuedoEroder((coords, seed) -> (float) noise1.GetNoise(coords.x * frequency, coords.y * frequency), seed);

    public GenerationTestBuilder() {
    }

    public void apply(Random pRandom, ChunkAccess chunkIn, Biome pBiome, int x, int z, int pHeight, double pNoise, BlockState defaultBlock, BlockState defaultFluid, int pSeaLevel, int pMinSurfaceLevel, long pSeed) {
        float terrainHeight = (noise2.at(x, z) - 0.5F) * 2.0F;
        terrainHeight *= 32;
        terrainHeight += pSeaLevel;
        //Clinker.LOGGER.info(terrainHeight);

        BlockPos.MutableBlockPos pos = new BlockPos(x, 0, z).mutable();
        for (int y = chunkIn.getMaxBuildHeight(); y > chunkIn.getMinBuildHeight(); y--) {
            pos.setY(y);
            if (y < terrainHeight) {
                chunkIn.setBlockState(pos, defaultBlock, false);
            } else {
                chunkIn.setBlockState(pos, Blocks.AIR.defaultBlockState(), false);
            }
        }
    }

    public void initNoise(long seed) {
        if (this.seed != seed || this.noise1 == null || this.noise2 == null) {
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

        noise2.setSeed(seed);
    }
}
