package birsy.clinker.common.world.gen.cave.cavenoisesampler;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.IChunk;

import java.util.Random;

public abstract class CaveNoiseSampler {
    public long seed;
    public static final BlockState AIR = Blocks.CAVE_AIR.getDefaultState();

    public CaveNoiseSampler() {}

    public abstract float sampleCaveNoise(Random random, IChunk chunkIn, Biome biomeIn, int x, int y, int z, int surfaceHeight, int seaLevel, long seed);

    public void setSeed(long seed) {
        if (this.seed != seed) {
            this.seed = seed;
        }
    }
}
