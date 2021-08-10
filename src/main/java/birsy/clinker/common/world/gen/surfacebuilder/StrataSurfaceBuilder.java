package birsy.clinker.common.world.gen.surfacebuilder;

import birsy.clinker.core.util.MathUtils;
import com.mojang.serialization.Codec;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.synth.PerlinSimplexNoise;
import net.minecraft.world.level.levelgen.surfacebuilders.SurfaceBuilderBaseConfiguration;

import java.util.ArrayList;
import java.util.Random;
import java.util.stream.IntStream;

import BlockState;
import PerlinSimplexNoise;

public class StrataSurfaceBuilder extends TerracedTerrainSurfaceBuilder {
    private final BlockState[] strataBlocks;
    private final int layerNumber;
    protected long seed;
    private PerlinSimplexNoise strataNoiseGenerator;
    private PerlinSimplexNoise strataStartNoiseGenerator;

    private final int minHeight = 47;
    private final int maxHeight = 150;

    public StrataSurfaceBuilder(Codec<SurfaceBuilderBaseConfiguration> codec, BlockState... strataLayers) {
        super(codec);
        strataBlocks = strataLayers;
        layerNumber = strataLayers.length;
    }

    @Override
    public void apply(Random random, ChunkAccess chunkIn, Biome biomeIn, int x, int z, int startHeight, double noise, BlockState defaultBlock, BlockState defaultFluid, int seaLevel, long seed, SurfaceBuilderBaseConfiguration config) {
        super.apply(random, chunkIn, biomeIn, x, z, startHeight, noise, defaultBlock, defaultFluid, seaLevel, seed, config);

        Random rand = new Random(1337);
        int layerSize = (maxHeight - minHeight) / layerNumber;

        int strataStart = (int) (minHeight + strataStartNoiseGenerator.getValue(x * 0.125f, z * 0.125f, false) * 1.25);
        int totalHeight = strataStart;
        ArrayList<Integer> strataHeights = new ArrayList<>();

        for (int i = 0; i < layerNumber; i++) {
            double strataNoise = strataStartNoiseGenerator.getValue((x + (6 * layerNumber)) * 0.125f, (z + (6 * layerNumber)) * 0.125f, false) * 1.25;
            int strataHeight = (int) (layerSize + strataNoise + MathUtils.getRandomFloatBetween(rand, layerSize * -0.9F, layerSize * 0.1F));
            totalHeight += strataHeight;
            strataHeights.add(i, totalHeight);
        }

        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos(x, 0, z);
        int strataHeightIndex = 0;
        for (int y = strataStart; y < startHeight; y++) {
            pos.set(x, y, z);

            if (y < strataHeights.get(strataHeightIndex)) {
                if (chunkIn.getBlockState(pos) == defaultBlock) {
                    chunkIn.setBlockState(pos, strataBlocks[strataHeightIndex], false);
                }
            } else if (strataHeightIndex < strataHeights.size() - 1){
                strataHeightIndex++;
            }
        }
    }

    @Override
    public void initNoise(long seed) {
        if (this.seed != seed || this.strataNoiseGenerator == null || this.strataStartNoiseGenerator == null) {
            WorldgenRandom sharedseedrandom = new WorldgenRandom(seed);
            this.strataNoiseGenerator = new PerlinSimplexNoise(sharedseedrandom, IntStream.rangeClosed(-3, 0));
            this.strataStartNoiseGenerator = new PerlinSimplexNoise(sharedseedrandom, IntStream.rangeClosed(-3, 0));
        }
        super.initNoise(seed);
    }
}
