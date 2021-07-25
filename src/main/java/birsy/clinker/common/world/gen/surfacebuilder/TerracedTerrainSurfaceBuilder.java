package birsy.clinker.common.world.gen.surfacebuilder;

import birsy.clinker.core.registry.ClinkerBlocks;
import birsy.clinker.core.util.MathUtils;
import com.ibm.icu.impl.Pair;
import com.mojang.serialization.Codec;
import de.articdive.jnoise.JNoise;
import de.articdive.jnoise.distance_functions.DistanceFunctionType;
import de.articdive.jnoise.interpolation.InterpolationType;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SlabBlock;
import net.minecraft.util.Direction;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.PerlinNoiseGenerator;
import net.minecraft.world.gen.surfacebuilders.NetherForestsSurfaceBuilder;
import net.minecraft.world.gen.surfacebuilders.SurfaceBuilderConfig;

import java.util.ArrayList;
import java.util.Random;
import java.util.stream.IntStream;

public class TerracedTerrainSurfaceBuilder extends NetherForestsSurfaceBuilder {
    protected long seed;

    private JNoise worleyNoise;

    private PerlinNoiseGenerator terrainNoiseGenerator;
    private PerlinNoiseGenerator terraceNoiseGenerator;
    private PerlinNoiseGenerator erosionNoiseGenerator;

    public TerracedTerrainSurfaceBuilder(Codec<SurfaceBuilderConfig> codec) {
        super(codec);
    }

    @Override
    public void buildSurface(Random random, IChunk chunkIn, Biome biomeIn, int x, int z, int startHeight, double noise, BlockState defaultBlock, BlockState defaultFluid, int seaLevel, long seed, SurfaceBuilderConfig config) {
        //double baseNoise = (terrainNoiseGenerator.noiseAt(x * 0.06f, z * 0.06f, false) + 1) * 0.5;
        double baseNoise = ((terrainNoiseGenerator.noiseAt(x * 0.005f, z * 0.005f, false) + 1) * 0.5;
        double terraceNoise = MathUtils.mapRange(-1, 1, 0.07F, 0.6F, (float) terraceNoiseGenerator.noiseAt(x * 0.00625f, z * 0.00625f, false));
        double erosionNoise = MathUtils.mapRange(-1, 1, 0.05F, 0.5F, (float) erosionNoiseGenerator.noiseAt(x * 0.05f, z * 0.05f, false));

        double shorelineNoise = MathUtils.mapRange(0.05F, 0.5F, 0, 1, (float) erosionNoise);

        Pair<Float, Boolean> terrace = MathUtils.terrace((float) baseNoise, (float) terraceNoise, (float) erosionNoise, MathUtils.getRandomFloatBetween(random, 0.8F, 1.0F));
        double finalNoise = (terrace.first * 2) - 1;
        boolean isTerrace = terrace.second;
        double terrainHeight = (finalNoise * 70) + 55;

        BlockPos.Mutable pos = new BlockPos.Mutable(x, 0, z);
        for (int y = 0; y < 256; y++) {
            pos.setPos(x, y, z);
            if (y < terrainHeight) {
                chunkIn.setBlockState(pos, defaultBlock, false);
            } else {
                if (y < seaLevel) {
                    chunkIn.setBlockState(pos, defaultFluid, false);
                } else {
                    chunkIn.setBlockState(pos, Blocks.AIR.getDefaultState(), false);
                }
            }
        }

        float shorelineHeight = (float) (seaLevel + 5 + (shorelineNoise * 3));

        if (terrainHeight > shorelineHeight) {
            if (isTerrace) {
                super.buildSurface(random, chunkIn, biomeIn, x, z, startHeight, noise, defaultBlock, defaultFluid, seaLevel, seed, config);
            } else {
                super.buildSurface(random, chunkIn, biomeIn, x, z, startHeight, noise, defaultBlock, defaultFluid, seaLevel, seed, new SurfaceBuilderConfig(defaultBlock, defaultBlock, ClinkerBlocks.SHALE.get().getDefaultState()));
            }
        } else {
            super.buildSurface(random, chunkIn, biomeIn, x, z, startHeight, noise, defaultBlock, defaultFluid, seaLevel, seed, new SurfaceBuilderConfig(ClinkerBlocks.SALT_BLOCK.get().getDefaultState(),
                    ClinkerBlocks.SCORSTONE.get().getDefaultState(),
                    ClinkerBlocks.SALT_BLOCK.get().getDefaultState()));
        }
    }

    private boolean isOnEdge (IChunk world, BlockPos pos) {
        for (Direction direction : Direction.Plane.HORIZONTAL) {
            if (!world.getBlockState(pos.offset(direction)).isSolid() && !world.getBlockState(pos.offset(direction)).matchesBlock(Blocks.WATER)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void setSeed(long seed) {
        if (this.seed != seed || this.terrainNoiseGenerator == null || this.terraceNoiseGenerator == null || this.erosionNoiseGenerator == null) {
            this.seed = seed;
            SharedSeedRandom sharedseedrandom = new SharedSeedRandom(seed);

            this.terrainNoiseGenerator = new PerlinNoiseGenerator(sharedseedrandom, IntStream.rangeClosed(-4, 0));
            this.terraceNoiseGenerator = new PerlinNoiseGenerator(sharedseedrandom, IntStream.rangeClosed(-3, 0));
            this.erosionNoiseGenerator = new PerlinNoiseGenerator(sharedseedrandom, IntStream.rangeClosed(-3, 0));

            this.worleyNoise = JNoise.newBuilder().worley().setDistanceFunction(DistanceFunctionType.EUCLIDEAN).setFrequency(1.25F).setSeed(seed).build();
        }
        super.setSeed(seed);
    }
}
