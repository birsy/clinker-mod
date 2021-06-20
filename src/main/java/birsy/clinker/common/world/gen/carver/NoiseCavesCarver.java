package birsy.clinker.common.world.gen.carver;

import com.mojang.serialization.Codec;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.PerlinNoiseGenerator;
import net.minecraft.world.gen.carver.WorldCarver;
import net.minecraft.world.gen.feature.ProbabilityConfig;

import java.util.BitSet;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.IntStream;

public class NoiseCavesCarver extends WorldCarver<ProbabilityConfig> {
    private final PerlinNoiseGenerator noiseGeneratorXZ;
    private final PerlinNoiseGenerator noiseGeneratorY;

    public NoiseCavesCarver(Codec<ProbabilityConfig> codec, int maxHeight) {
        super(codec, maxHeight);
        SharedSeedRandom sharedseedrandom = new SharedSeedRandom(1337);
        this.noiseGeneratorXZ = new PerlinNoiseGenerator(sharedseedrandom, IntStream.rangeClosed(-3, 0));
        this.noiseGeneratorY = new PerlinNoiseGenerator(sharedseedrandom, IntStream.rangeClosed(-3, 0));
    }

    @Override
    public boolean carveRegion(IChunk chunk, Function<BlockPos, Biome> biomePos, Random rand, int seaLevel, int chunkXOffset, int chunkZOffset, int chunkX, int chunkZ, BitSet carvingMask, ProbabilityConfig config) {
        BlockPos.Mutable pos = new BlockPos.Mutable();
        for (int y = 255; y > 1; y--) {
            pos.setPos(chunkX * 16 + chunkXOffset, y, chunkZ * 16 + chunkZOffset);

        }
        return false;
    }

    @Override
    public boolean shouldCarve(Random rand, int chunkX, int chunkZ, ProbabilityConfig config) {
        return true;
    }

    @Override
    protected boolean func_222708_a(double p_222708_1_, double p_222708_3_, double p_222708_5_, int p_222708_7_) {
        return false;
    }


}
