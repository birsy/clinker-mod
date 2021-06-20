package birsy.clinker.common.world.gen.surfacebuilder;

import com.mojang.serialization.Codec;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.PerlinNoiseGenerator;
import net.minecraft.world.gen.surfacebuilders.SurfaceBuilder;
import net.minecraft.world.gen.surfacebuilders.SurfaceBuilderConfig;

import java.util.Random;
import java.util.stream.IntStream;

public class LayeredSurfaceBuilder extends SurfaceBuilder<SurfaceBuilderConfig> {
    private static final BlockState AIR = Blocks.AIR.getDefaultState();
    protected long seed;
    private PerlinNoiseGenerator canyonNoiseGenerator;

    public LayeredSurfaceBuilder(Codec<SurfaceBuilderConfig> codec) {
        super(codec);
    }

    @Override
    public void buildSurface(Random random, IChunk chunkIn, Biome biomeIn, int x, int z, int startHeight, double noise, BlockState defaultBlock, BlockState defaultFluid, int seaLevel, long seed, SurfaceBuilderConfig config) {
        int canyonDepth = 15;
        int cliffHeight = 15;

        double canyonNoise = this.canyonNoiseGenerator.noiseAt(x * 0.25, z * 0.25, false);



        for (int y = 255; y >= startHeight - canyonDepth; y--) {
            BlockPos pos = new BlockPos(x, y, z);
            if (canyonNoise > -0.3) {
                if (y >= startHeight) {
                    chunkIn.setBlockState(pos, AIR, false);
                }
            }
        }

        for (int y = startHeight + cliffHeight; y > startHeight; y--) {
            BlockPos pos = new BlockPos(x, y, z);
            if (canyonNoise < 0.3) {
                chunkIn.setBlockState(pos, defaultBlock, false);
            }
        }
    }

    @Override
    public void setSeed(long seed) {
        if (this.seed != seed || this.canyonNoiseGenerator == null) {
                    SharedSeedRandom sharedseedrandom = new SharedSeedRandom(seed);

            this.canyonNoiseGenerator = new PerlinNoiseGenerator(sharedseedrandom, IntStream.rangeClosed(-3, 0));
        }
        super.setSeed(seed);
    }
}
