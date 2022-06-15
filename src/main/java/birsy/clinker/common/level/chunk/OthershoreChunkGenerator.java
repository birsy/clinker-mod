package birsy.clinker.common.level.chunk;

import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.StructureSet;

import java.util.Optional;

public abstract class OthershoreChunkGenerator extends ChunkGenerator {
    public OthershoreChunkGenerator(Registry<StructureSet> pStructureSets, Optional<HolderSet<StructureSet>> pStructureOverrides, BiomeSource pBiomeSource) {
        super(pStructureSets, pStructureOverrides, pBiomeSource);
    }
    /*public static final Codec<OthershoreChunkGenerator> CODEC = RecordCodecBuilder.create((codec) -> {
        return codec.group(BiomeSource.CODEC.fieldOf("biome_source").forGetter((chunkGenerator) -> {
            return chunkGenerator.biomeSource;
        }), NoiseGeneratorSettings.CODEC.fieldOf("settings").forGetter((chunkGenerator) -> {
            return chunkGenerator.settings;
        }), Codec.LONG.fieldOf("seed").stable().forGetter((chunkGenerator) -> {
            return chunkGenerator.seed;
        })).apply(codec, codec.stable(OthershoreChunkGenerator::new));
    });

    private static final BlockState AIR = Blocks.AIR.defaultBlockState();

    private final long seed;
    protected final Supplier<NoiseGeneratorSettings> settings;
    private final OthershoreNoiseSampler noiseSampler;
    private final BaseStoneSource baseStoneSource;
    protected final BlockState defaultBlock;
    protected final BlockState defaultFluid;
    private final int height;

    private final SurfaceNoise surfaceNoise;

    public OthershoreChunkGenerator(BiomeSource biomeSource, BiomeSource runtimeBiomeSource, Supplier<NoiseGeneratorSettings> settings, long seed) {
        super(biomeSource, runtimeBiomeSource, settings.get().structureSettings(), seed);
        this.seed = seed;
        this.noiseSampler = new OthershoreNoiseSampler((int) this.seed);
        NoiseGeneratorSettings noisegeneratorsettings = settings.get();
        this.settings = settings;
        NoiseSettings noisesettings = noisegeneratorsettings.noiseSettings();
        this.height = noisesettings.height();
        this.defaultBlock = noisegeneratorsettings.getDefaultBlock();
        this.defaultFluid = noisegeneratorsettings.getDefaultFluid();
        this.baseStoneSource = new SingleBaseStoneSource(this.defaultBlock);

        WorldgenRandom worldgenrandom = new WorldgenRandom(seed);
        this.surfaceNoise = noisesettings.useSimplexSurfaceNoise() ? new PerlinSimplexNoise(worldgenrandom, IntStream.rangeClosed(-3, 0)) : new PerlinNoise(worldgenrandom, IntStream.rangeClosed(-3, 0));
    }

    public OthershoreChunkGenerator(BiomeSource biomeSource, Supplier<NoiseGeneratorSettings> settings, long seed) {
        this(biomeSource, biomeSource, settings, seed);
    }

    private double evaluateNoise (int x, int y, int z) {
        double cliffHeight = 120;
        double surfaceHeight = MathUtils.biasTowardsExtreme(noiseSampler.continentalNoiseGenerator.GetNoise(x, z), 1, 3) * (cliffHeight / 2) + settings.get().getMinSurfaceLevel() + settings.get().seaLevel() + (cliffHeight * (2/3));
        return Mth.clamp((surfaceHeight - y) / 8.0D, -1, 1);
    }

    @Override
    public Codec<? extends ChunkGenerator> codec() {
        return CODEC;
    }

    @Override
    public ChunkGenerator withSeed(long pSeed) {
        return new OthershoreChunkGenerator(biomeSource, runtimeBiomeSource, settings, pSeed);
    }

    @Override
    public void buildSurfaceAndBedrock(WorldGenRegion pLevel, ChunkAccess pChunk) {
        ChunkPos chunkpos = pChunk.getPos();
        int chunkX = chunkpos.x;
        int chunkZ = chunkpos.z;
        WorldgenRandom worldgenrandom = new WorldgenRandom();
        worldgenrandom.setBaseChunkSeed(chunkX, chunkZ);

        int minX = chunkpos.getMinBlockX();
        int minZ = chunkpos.getMinBlockZ();

        BlockPos.MutableBlockPos blockPos = new BlockPos.MutableBlockPos();

        for(int chunkXOffset = 0; chunkXOffset < 16; ++chunkXOffset) {
            for(int chunkZOffset = 0; chunkZOffset < 16; ++chunkZOffset) {
                int x = minX + chunkXOffset;
                int z = minZ + chunkZOffset;
                int y = pChunk.getHeight(Heightmap.Types.WORLD_SURFACE_WG, chunkXOffset, chunkZOffset) + 1;
                double surfaceNoise = this.surfaceNoise.getSurfaceNoiseValue((double)x * 0.0625D, (double)z * 0.0625D, 0.0625D, (double)chunkXOffset * 0.0625D) * 15.0D;
                int minSurfaceLevel = this.settings.get().getMinSurfaceLevel();
                pLevel.getBiome(blockPos.set(minX + chunkXOffset, y, minZ + chunkZOffset)).buildSurfaceAt(worldgenrandom, pChunk, x, z, y, surfaceNoise, this.defaultBlock, this.defaultFluid, this.getSeaLevel(), minSurfaceLevel, pLevel.getSeed());
            }
        }

        this.setBedrock(pChunk, worldgenrandom);
    }

    private void setBedrock(ChunkAccess pChunk, Random pRandom) {
        BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();
        int i = pChunk.getPos().getMinBlockX();
        int j = pChunk.getPos().getMinBlockZ();
        NoiseGeneratorSettings noisegeneratorsettings = this.settings.get();
        int k = noisegeneratorsettings.noiseSettings().minY();
        int l = k + noisegeneratorsettings.getBedrockFloorPosition();
        int i1 = this.height - 1 + k - noisegeneratorsettings.getBedrockRoofPosition();
        int j1 = 5;
        int k1 = pChunk.getMinBuildHeight();
        int l1 = pChunk.getMaxBuildHeight();
        boolean flag = i1 + 5 - 1 >= k1 && i1 < l1;
        boolean flag1 = l + 5 - 1 >= k1 && l < l1;
        if (flag || flag1) {
            for(BlockPos blockpos : BlockPos.betweenClosed(i, 0, j, i + 15, 0, j + 15)) {
                if (flag) {
                    for(int i2 = 0; i2 < 5; ++i2) {
                        if (i2 <= pRandom.nextInt(5)) {
                            pChunk.setBlockState(blockpos$mutableblockpos.set(blockpos.getX(), i1 - i2, blockpos.getZ()), Blocks.BEDROCK.defaultBlockState(), false);
                        }
                    }
                }

                if (flag1) {
                    for(int j2 = 4; j2 >= 0; --j2) {
                        if (j2 <= pRandom.nextInt(5)) {
                            pChunk.setBlockState(blockpos$mutableblockpos.set(blockpos.getX(), l + j2, blockpos.getZ()), Blocks.BEDROCK.defaultBlockState(), false);
                        }
                    }
                }
            }

        }
    }

    @Override
    public CompletableFuture<ChunkAccess> fillFromNoise(Executor pExecutor, StructureFeatureManager pStructureFeatureManager, ChunkAccess pChunk) {
        BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();
        Heightmap oceanFloorHeightmap = pChunk.getOrCreateHeightmapUnprimed(Heightmap.Types.OCEAN_FLOOR_WG);
        Heightmap worldSurfaceHeightmap = pChunk.getOrCreateHeightmapUnprimed(Heightmap.Types.WORLD_SURFACE_WG);

        for(int x = 0; x < 16; x++) {
            for(int z = 0; z < 16; z++) {
                double cliffHeight = 120;
                double surfaceHeight = MathUtils.biasTowardsExtreme(noiseSampler.continentalNoiseGenerator.GetNoise(x, z), 1, 3) * (cliffHeight / 2) + settings.get().getMinSurfaceLevel() + settings.get().seaLevel() + (cliffHeight * (2/3));

                for (int y = pChunk.getMinBuildHeight(); y < pChunk.getMaxBuildHeight(); y++) {
                    if (Mth.clamp((surfaceHeight - y) / 8.0D, -1, 1) > 0) {
                        pChunk.setBlockState(blockpos$mutableblockpos.set(x, y, z), defaultBlock, false);
                        oceanFloorHeightmap.update(x, y, z, defaultBlock);
                        worldSurfaceHeightmap.update(x, y, z, defaultBlock);
                    } else {
                        BlockState emptyState = y > settings.get().seaLevel() ? AIR : defaultFluid;
                        pChunk.setBlockState(blockpos$mutableblockpos.set(x, y, z), emptyState, false);
                    }
                }
            }
        }

        return CompletableFuture.completedFuture(pChunk);
    }

    @Override
    public int getBaseHeight(int x, int z, Heightmap.Types pType, LevelHeightAccessor pLevel) {
        double cliffHeight = 120;
        return (int) (MathUtils.biasTowardsExtreme(noiseSampler.continentalNoiseGenerator.GetNoise(x, z), 1, 3) * (cliffHeight / 2) + settings.get().getMinSurfaceLevel() + settings.get().seaLevel() + (cliffHeight * (2/3)));
    }

    @Override
    public NoiseColumn getBaseColumn(int x, int z, LevelHeightAccessor pLevel) {
        BlockState[] column = new BlockState[pLevel.getMaxBuildHeight() - pLevel.getMinBuildHeight()];

        double cliffHeight = 120;
        double surfaceHeight = MathUtils.biasTowardsExtreme(noiseSampler.continentalNoiseGenerator.GetNoise(x, z), 1, 3) * (cliffHeight / 2) + settings.get().getMinSurfaceLevel() + settings.get().seaLevel() + (cliffHeight * (2/3));
        for (int y = pLevel.getMinBuildHeight(); y < pLevel.getMaxBuildHeight(); y++) {
            int index = y - pLevel.getMinBuildHeight();
            if (Mth.clamp((surfaceHeight - y) / 8.0D, -1, 1) > 0) {
                column[index] = defaultBlock;
            } else {
                column[index] = y > settings.get().seaLevel() ? AIR : defaultFluid;
            }
        }

        return new NoiseColumn(pLevel.getMinBuildHeight(), column);
    }*/
}
