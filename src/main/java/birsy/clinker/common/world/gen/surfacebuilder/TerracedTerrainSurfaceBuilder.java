package birsy.clinker.common.world.gen.surfacebuilder;

import birsy.clinker.core.registry.ClinkerBlocks;
import birsy.clinker.core.util.MathUtils;
import com.ibm.icu.impl.Pair;
import net.minecraft.block.BlockState;
import net.minecraft.world.biome.Biome;


import java.util.Random;
import java.util.stream.IntStream;

public class TerracedTerrainSurfaceBuilder extends NetherForestSurfaceBuilder {
    protected long seed;

    private PerlinSimplexNoise terrainNoiseGenerator;
    private PerlinSimplexNoise terraceNoiseGenerator;
    private PerlinSimplexNoise erosionNoiseGenerator;

    public TerracedTerrainSurfaceBuilder(Codec<SurfaceBuilderBaseConfiguration> codec) {
        super(codec);
    }

    @Override
    public void apply(Random random, ChunkAccess chunkIn, Biome biomeIn, int x, int z, int startHeight, double noise, BlockState defaultBlock, BlockState defaultFluid, int seaLevel, long seed, SurfaceBuilderBaseConfiguration config) {
        double baseNoise = (terrainNoiseGenerator.getValue(x * 0.06f, z * 0.06f, false) + 1) * 0.5;
        double terraceNoise = MathUtils.mapRange(-1, 1, 0.07F, 0.6F, (float) terraceNoiseGenerator.getValue(x * 0.00625f, z * 0.00625f, false));
        double erosionNoise = MathUtils.mapRange(-1, 1, 0.05F, 0.5F, (float) erosionNoiseGenerator.getValue(x * 0.05f, z * 0.05f, false));

        double shorelineNoise = MathUtils.mapRange(0.05F, 0.5F, 0, 1, (float) erosionNoise);

        Pair<Float, Boolean> terrace = MathUtils.terrace((float) baseNoise, (float) terraceNoise, (float) erosionNoise, MathUtils.getRandomFloatBetween(random, 0.8F, 1.0F));
        double finalNoise = (terrace.first * 2) - 1;
        boolean isTerrace = terrace.second;
        double terrainHeight = (finalNoise * 70) + 55;

        float oceanFloorFlatness = 5;
        if (terrainHeight < (seaLevel - 1)) {
            terrainHeight = ((terrainHeight - (seaLevel - 1)) * (1 / oceanFloorFlatness)) + (seaLevel - 1);
        }

        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos(x, 0, z);
        for (int y = 0; y < 256; y++) {
            pos.set(x, y, z);
            if (y < terrainHeight) {
                chunkIn.setBlockState(pos, defaultBlock, false);
            } else {
                if (y < seaLevel) {
                    chunkIn.setBlockState(pos, defaultFluid, false);
                } else {
                    chunkIn.setBlockState(pos, Blocks.AIR.defaultBlockState(), false);
                }
            }
        }

        float shorelineHeight = (float) (seaLevel + 5 + (shorelineNoise * 3));

        if (terrainHeight > shorelineHeight) {
            if (isTerrace) {
                super.apply(random, chunkIn, biomeIn, x, z, startHeight, noise, defaultBlock, defaultFluid, seaLevel, seed, config);
            } else {
                super.apply(random, chunkIn, biomeIn, x, z, startHeight, noise, defaultBlock, defaultFluid, seaLevel, seed, new SurfaceBuilderBaseConfiguration(defaultBlock, defaultBlock, ClinkerBlocks.SHALE.get().defaultBlockState()));
            }
        } else {
            super.apply(random, chunkIn, biomeIn, x, z, startHeight, noise, defaultBlock, defaultFluid, seaLevel, seed, new SurfaceBuilderBaseConfiguration(ClinkerBlocks.SALT_BLOCK.get().defaultBlockState(),
                    ClinkerBlocks.SCORSTONE.get().defaultBlockState(),
                    ClinkerBlocks.SALT_BLOCK.get().defaultBlockState()));
        }
    }

    private boolean isOnEdge (ChunkAccess world, BlockPos pos) {
        for (Direction direction : Direction.Plane.HORIZONTAL) {
            if (!world.getBlockState(pos.relative(direction)).canOcclude() && !world.getBlockState(pos.relative(direction)).is(Blocks.WATER)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void initNoise(long seed) {
        if (this.seed != seed || this.terrainNoiseGenerator == null || this.terraceNoiseGenerator == null || this.erosionNoiseGenerator == null) {
            this.seed = seed;
            WorldgenRandom sharedseedrandom = new WorldgenRandom(seed);

            this.terrainNoiseGenerator = new PerlinSimplexNoise(sharedseedrandom, IntStream.rangeClosed(-4, 0));
            this.terraceNoiseGenerator = new PerlinSimplexNoise(sharedseedrandom, IntStream.rangeClosed(-3, 0));
            this.erosionNoiseGenerator = new PerlinSimplexNoise(sharedseedrandom, IntStream.rangeClosed(-3, 0));
        }
        super.initNoise(seed);
    }
}
