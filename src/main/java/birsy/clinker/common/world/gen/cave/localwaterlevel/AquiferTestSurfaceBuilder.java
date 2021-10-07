package birsy.clinker.common.world.gen.cave.localwaterlevel;

import birsy.clinker.common.world.gen.cave.localwaterlevel.LocalWaterLevelHex;
import birsy.clinker.core.util.Hex;
import birsy.clinker.core.util.noise.FastNoiseLite;
import com.mojang.serialization.Codec;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.surfacebuilders.SurfaceBuilder;
import net.minecraft.world.gen.surfacebuilders.SurfaceBuilderConfig;

import java.util.Random;

public class AquiferTestSurfaceBuilder extends SurfaceBuilder<SurfaceBuilderConfig> {
    private long seed;
    private float hexSize = 30;
    private int borderSize = 3;
    private FastNoiseLite noise;
    public AquiferTestSurfaceBuilder(Codec<SurfaceBuilderConfig> codec) {
        super(codec);
    }

    @Override
    public void buildSurface(Random random, IChunk chunkIn, Biome biomeIn, int x, int z, int startHeight, double noise, BlockState defaultBlock, BlockState defaultFluid, int seaLevel, long seed, SurfaceBuilderConfig config) {
        float noiseOffset = this.noise.GetNoise(x, z) * 32;
        BlockPos.Mutable placementPos = new BlockPos.Mutable(x, 0, z);
        BlockPos.Mutable samplePos = new BlockPos.Mutable(x + noiseOffset, 0, z + noiseOffset);

        LocalWaterLevelHex sampleHex = new LocalWaterLevelHex(Hex.blockToHex(samplePos.getX(), samplePos.getZ(), LocalWaterLevelHex.hexSize), seed);


        for (int y = 0; y < 255; y++) {
            placementPos.setY(y);
            samplePos.setY(y);

            chunkIn.setBlockState(placementPos, Blocks.AIR.getDefaultState(), false);
            LocalWaterLevelHex.buildAquiferAtPos(placementPos, samplePos, sampleHex, chunkIn, defaultBlock, defaultFluid, seed);
        }
    }

    @Override
    public void setSeed(long seed) {
        if (this.seed != seed || this.noise != noise) {
            this.seed = seed;

            this.noise = new FastNoiseLite((int) (seed));
            this.noise.SetNoiseType(FastNoiseLite.NoiseType.ValueCubic);
            this.noise.SetFrequency(0.03F);
        }
    }
}
