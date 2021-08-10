package birsy.clinker.common.world.gen.carver;

import com.mojang.serialization.Codec;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.synth.PerlinSimplexNoise;
import net.minecraft.world.level.levelgen.carver.WorldCarver;
import net.minecraft.world.level.levelgen.feature.configurations.ProbabilityFeatureConfiguration;

import java.util.BitSet;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.IntStream;

public class NoiseCavesCarver extends WorldCarver<ProbabilityFeatureConfiguration> {
    private final PerlinSimplexNoise noiseGeneratorXZ;
    private final PerlinSimplexNoise noiseGeneratorY;

    public NoiseCavesCarver(Codec<ProbabilityFeatureConfiguration> codec, int maxHeight) {
        super(codec, maxHeight);
        WorldgenRandom sharedseedrandom = new WorldgenRandom(1337);
        this.noiseGeneratorXZ = new PerlinSimplexNoise(sharedseedrandom, IntStream.rangeClosed(-3, 0));
        this.noiseGeneratorY = new PerlinSimplexNoise(sharedseedrandom, IntStream.rangeClosed(-3, 0));
    }

    @Override
    public boolean carve(ChunkAccess chunk, Function<BlockPos, Biome> biomePos, Random rand, int seaLevel, int chunkXOffset, int chunkZOffset, int chunkX, int chunkZ, BitSet carvingMask, ProbabilityFeatureConfiguration config) {
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        for (int y = 255; y > 1; y--) {
            pos.set(chunkX * 16 + chunkXOffset, y, chunkZ * 16 + chunkZOffset);

        }
        return false;
    }

    @Override
    public boolean isStartChunk(Random rand, int chunkX, int chunkZ, ProbabilityFeatureConfiguration config) {
        return true;
    }

    @Override
    protected boolean skip(double p_222708_1_, double p_222708_3_, double p_222708_5_, int p_222708_7_) {
        return false;
    }


}
