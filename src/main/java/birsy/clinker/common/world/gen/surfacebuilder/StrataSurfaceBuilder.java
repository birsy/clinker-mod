package birsy.clinker.common.world.gen.surfacebuilder;

import birsy.clinker.core.util.MathUtils;
import com.mojang.serialization.Codec;
import net.minecraft.block.BlockState;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.PerlinNoiseGenerator;
import net.minecraft.world.gen.surfacebuilders.SurfaceBuilderConfig;

import java.util.ArrayList;
import java.util.Random;
import java.util.stream.IntStream;

public class StrataSurfaceBuilder extends TerracedTerrainSurfaceBuilder {
    private final BlockState[] strataBlocks;
    private final int layerNumber;
    protected long seed;
    private PerlinNoiseGenerator strataNoiseGenerator;
    private PerlinNoiseGenerator strataStartNoiseGenerator;

    private final int minHeight = 47;
    private final int maxHeight = 150;

    public StrataSurfaceBuilder(Codec<SurfaceBuilderConfig> codec, BlockState... strataLayers) {
        super(codec);
        strataBlocks = strataLayers;
        layerNumber = strataLayers.length;
    }

    @Override
    public void buildSurface(Random random, IChunk chunkIn, Biome biomeIn, int x, int z, int startHeight, double noise, BlockState defaultBlock, BlockState defaultFluid, int seaLevel, long seed, SurfaceBuilderConfig config) {
        super.buildSurface(random, chunkIn, biomeIn, x, z, startHeight, noise, defaultBlock, defaultFluid, seaLevel, seed, config);

        Random rand = new Random(1337);
        int layerSize = (maxHeight - minHeight) / layerNumber;

        int strataStart = (int) (minHeight + strataStartNoiseGenerator.noiseAt(x * 0.125f, z * 0.125f, false) * 1.25);
        int totalHeight = strataStart;
        ArrayList<Integer> strataHeights = new ArrayList<>();

        for (int i = 0; i < layerNumber; i++) {
            double strataNoise = strataStartNoiseGenerator.noiseAt((x + (6 * layerNumber)) * 0.125f, (z + (6 * layerNumber)) * 0.125f, false) * 1.25;
            int strataHeight = (int) (layerSize + strataNoise + MathUtils.getRandomFloatBetween(rand, layerSize * -0.9F, layerSize * 0.1F));
            totalHeight += strataHeight;
            strataHeights.add(i, totalHeight);
        }

        BlockPos.Mutable pos = new BlockPos.Mutable(x, 0, z);
        int strataHeightIndex = 0;
        for (int y = strataStart; y < startHeight; y++) {
            pos.setPos(x, y, z);

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
    public void setSeed(long seed) {
        if (this.seed != seed || this.strataNoiseGenerator == null || this.strataStartNoiseGenerator == null) {
            SharedSeedRandom sharedseedrandom = new SharedSeedRandom(seed);
            this.strataNoiseGenerator = new PerlinNoiseGenerator(sharedseedrandom, IntStream.rangeClosed(-3, 0));
            this.strataStartNoiseGenerator = new PerlinNoiseGenerator(sharedseedrandom, IntStream.rangeClosed(-3, 0));
        }
        super.setSeed(seed);
    }
}
