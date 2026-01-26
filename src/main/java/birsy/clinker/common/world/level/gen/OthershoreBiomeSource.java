package birsy.clinker.common.world.level.gen;

import birsy.clinker.common.world.level.gen.system.biome.*;
import birsy.clinker.common.world.level.gen.content.biome.BiomeLayerOperations;
import birsy.clinker.common.world.level.gen.system.biome.resolver.LayeredBiomeResolver;
import birsy.clinker.common.world.level.gen.system.biome.resolver.ProtoBiome;
import birsy.clinker.common.world.level.gen.system.noise.NoiseContext;
import birsy.clinker.common.world.level.gen.system.noise.NoiseFieldCache;
import birsy.clinker.common.world.level.gen.system.noise.SeededNoiseHolderHolder;
import birsy.clinker.common.world.level.gen.system.noise.UncachedNoiseContext;
import birsy.clinker.common.world.level.gen.system.surface.shaper.SurfaceShaperSystem;
import birsy.clinker.core.registry.ClinkerRegistries;
import birsy.clinker.core.registry.worldgen.ClinkerBiomes;
import birsy.clinker.core.registry.worldgen.ClinkerNoiseComputers;
import birsy.clinker.core.registry.worldgen.ClinkerProtoBiomes;
import com.google.common.collect.Sets;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.random.SimpleWeightedRandomList;
import net.minecraft.world.level.biome.*;
import net.minecraft.world.level.levelgen.PositionalRandomFactory;
import net.minecraft.world.level.levelgen.RandomState;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Stream;

public class OthershoreBiomeSource extends BiomeSource {
    public static final MapCodec<OthershoreBiomeSource> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(RegistryOps.retrieveGetter(Registries.BIOME))
                        .apply(instance, instance.stable(OthershoreBiomeSource::new))
    );
    @Deprecated(forRemoval = true)
    public static final int UPPER_SHELF_HEIGHT = 230,
                            MIDDLE_SHELF_HEIGHT = 180;
    private final HolderGetter<Biome> biomeGetter;
    private final Holder<Biome>[] biomeByProtoBiomeId;
    private Holder<Biome> voidBiome;
    private Holder<Biome> underground, aquifer;

    final BiomeList biomeList;

    private LayeredBiomeResolver surfaceBiomeResolver;
    private UncachedNoiseContext uncachedNoiseContext;

    private BiomeBlender biomeBlender;
    private SurfaceShaperSystem surfaceShaperSystem;

    private final ThreadLocal<double[]> biomeBlendWeights;

    public OthershoreBiomeSource(HolderGetter<Biome> biomeGetter) {
        super();
        this.biomeGetter = biomeGetter;
        biomeByProtoBiomeId = new Holder[ClinkerRegistries.PROTO_BIOME_REGISTRY.size()];
        // fill in biomes
        Holder.Reference<Biome> voidBiome = biomeGetter.getOrThrow(Biomes.THE_VOID);
        for (ProtoBiome protoBiome : ClinkerRegistries.PROTO_BIOME_REGISTRY) {
            biomeByProtoBiomeId[protoBiome.id] = protoBiome.biome.map(biomeGetter::getOrThrow).orElse(voidBiome);
        }
        underground = biomeGetter.getOrThrow(ClinkerBiomes.UNDERGROUND);
        aquifer = biomeGetter.getOrThrow(ClinkerBiomes.AQUIFER);

        this.biomeList = new BiomeList(this);
        this.biomeBlendWeights = ThreadLocal.withInitial(() -> new double[this.biomeList.maxId() + 1]);
    }

    public void initRandomState(RandomState randomState) {
        this.uncachedNoiseContext = new UncachedNoiseContext(((SeededNoiseHolderHolder)(Object) randomState).clinker$noiseHolder());
        this.surfaceBiomeResolver = createSurfaceBiomeResolver(randomState::getOrCreateRandomFactory, this.uncachedNoiseContext);
    }

    public void initFromChunkGenerator(OthershoreChunkGenerator chunkGenerator) {
        this.biomeBlender = chunkGenerator.biomeBlender;
        this.surfaceShaperSystem = chunkGenerator.surfaceShaperSystem;
    }

    public static LayeredBiomeResolver createSurfaceBiomeResolver(Function<ResourceLocation, PositionalRandomFactory> randomState, UncachedNoiseContext noiseContext) {
        return LayeredBiomeResolver.builder(8)
                .layer((x, z, current, neighborhood, random, context) -> {
                    double surfaceHeight = context.retrieve(ClinkerNoiseComputers.BASE_SURFACE_HEIGHT, x, 0, z);
                    if (surfaceHeight < OthershoreGenerationConstants.BASE_SEA_LEVEL + 20)
                        return ClinkerProtoBiomes.LOWER_SHELF.get();
                    return ClinkerProtoBiomes.UPPER_SHELF.get();
                })
                .layer(new BiomeLayerOperations.Mutate(ClinkerProtoBiomes.UPPER_SHELF.get(),
                          SimpleWeightedRandomList.<ProtoBiome>builder()
                                  .add(ClinkerProtoBiomes.UPPER_SHELF.get(), 10)
                                  .add(ClinkerProtoBiomes.HEATH.get(), 7)
                                  .build()
                ))
                .layer(new BiomeLayerOperations.Smooth())
                .layer(new BiomeLayerOperations.Smooth())
                .zoom()
                .layer(new BiomeLayerOperations.RandomizeIntoNeighbor(1))
                .layer(new BiomeLayerOperations.Smooth())
                .zoom()
                .layer(new BiomeLayerOperations.RandomizeIntoNeighbor(1))
                .zoom()
                .layer(new BiomeLayerOperations.RandomizeIntoNeighbor(1))
                .layer(new BiomeLayerOperations.Smooth())
                .zoom()
                .layer(new BiomeLayerOperations.RandomizeIntoNeighbor(1))
                .layer(new BiomeLayerOperations.Smooth())
                .layer(new BiomeLayerOperations.Smooth())
                .zoom()
                .layer(new BiomeLayerOperations.RandomizeIntoNeighbor(1))
                .zoom()
                .layer(new BiomeLayerOperations.RandomizeIntoNeighbor(1))
                .layer(new BiomeLayerOperations.Smooth())
                .build(randomState, noiseContext);
    }


    @Override
    protected Stream<Holder<Biome>> collectPossibleBiomes() {
        Set<Holder<Biome>> possibleBiomes = new HashSet<>();
        Holder.Reference<Biome> voidBiome = biomeGetter.getOrThrow(Biomes.THE_VOID);
        for (ProtoBiome protoBiome : ClinkerRegistries.PROTO_BIOME_REGISTRY) {
            possibleBiomes.add(protoBiome.biome.map(biomeGetter::getOrThrow).orElse(voidBiome));
        }
        Collections.addAll(possibleBiomes, underground, aquifer);
        return possibleBiomes.stream();
    }
    @Override
    public Holder<Biome> getNoiseBiome(int qX, int qY, int qZ, Climate.Sampler sampler) {
        return this.getNoiseBiome(qX, qY, qZ, null, uncachedNoiseContext);
    }
    @Override
    public Set<Holder<Biome>> getBiomesWithin(int x, int y, int z, int radius, Climate.Sampler sampler) {
        return this.getBiomesWithin(x, y, z, x + radius, y + radius, z + radius, uncachedNoiseContext);
    }
    @Override
    protected MapCodec<? extends BiomeSource> codec() {
        return CODEC;
    }


    public Holder<Biome> getSurfaceBiome(int qX, int qZ) {
        return biomeByProtoBiomeId[surfaceBiomeResolver.getProtoBiome(qX, qZ).id];
    }
    public Set<Holder<Biome>> getSurfaceBiomesWithin(int x1, int z1, int x2, int z2, NoiseContext context) {
        int minQX = QuartPos.fromBlock(Math.min(x1, x2)),
            minQZ = QuartPos.fromBlock(Math.min(z1, z2));
        int maxQX = QuartPos.fromBlock(Math.max(x1, x2)) + 1,
            maxQZ = QuartPos.fromBlock(Math.max(z1, z2)) + 1;
        Set<Holder<Biome>> set = new HashSet<>(3);
        for (int qX = minQX; qX <= maxQX; qX++) {
            for (int qZ = minQZ; qZ < maxQZ; qZ++) {
                set.add(getSurfaceBiome(qX, qZ));
            }
        }
        return set;
    }
    public BiomeCache2d createSurfaceBiomeCache(int minQX, int minQZ, int maxQX, int maxQZ) {
        BiomeCache2d cache = new BiomeCache2d(minQX, minQZ, maxQX, maxQZ);
        int index = 0;
        for (int z = 0; z < cache.sizeZ; z++) {
            int globalQZ = cache.minQuartZ + z;
            for (int x = 0; x < cache.sizeX; x++) {
                int globalQX = cache.minQuartX + x;
                Holder<Biome> biome = getSurfaceBiome(globalQX, globalQZ);
                cache.biomes[index++] = biome;
                cache.containedBiomes.add(biome);
            }
        }
        return cache;
    }


    public void prefillNoiseFields(NoiseFieldCache cache) {}
    public Holder<Biome> getNoiseBiome(int qX, int qY, int qZ, @Nullable BiomeCache2d surfaceBiomeCache, UncachedNoiseContext context) {
        int bX = QuartPos.toBlock(qX), bY = QuartPos.toBlock(qY), bZ = QuartPos.toBlock(qZ);
        if (bY < 0) return aquifer;
        double[] weights = this.biomeBlender.getBiomeBlendingWeights(biomeBlendWeights.get(), bX, bZ);
        double surfaceHeight = surfaceShaperSystem.getHeight(weights, bX, bZ, context);
        if (bY < surfaceHeight - 10) return underground;
        return surfaceBiomeCache == null ? getSurfaceBiome(qX, qZ) : surfaceBiomeCache.retrieve(qX, qZ);
    }
    public Holder<Biome> getNoiseBiome(int qX, int qY, int qZ, BiomeCache2d surfaceBiomeCache, SurfaceShaperSystem.ChunkSurfaceHeightmap heightmapInfo, NoiseContext context) {
        int bX = QuartPos.toBlock(qX), bY = QuartPos.toBlock(qY), bZ = QuartPos.toBlock(qZ);
        if (bY < 0) return aquifer;
        int localX = SectionPos.sectionRelative(bX), localZ = SectionPos.sectionRelative(bZ);
        double surfaceHeight = heightmapInfo.field().retrieve(localX, 0, localZ) - 10;
        if (bY < surfaceHeight) return underground;
        return surfaceBiomeCache.retrieve(qX, qZ);
    }
    public Set<Holder<Biome>> getBiomesWithin(int bX1, int bY1, int bZ1, int bX2, int bY2, int bZ2, UncachedNoiseContext context) {
        int minQX = QuartPos.fromBlock(Math.min(bX1, bX2)),
            minQY = QuartPos.fromBlock(Math.min(bY1, bY2)),
            minQZ = QuartPos.fromBlock(Math.min(bZ1, bZ2));
        int maxQX = QuartPos.fromBlock(Math.max(bX1, bX2)) + 1,
            maxQY = QuartPos.fromBlock(Math.max(bY1, bY2)) + 1,
            maxQZ = QuartPos.fromBlock(Math.max(bZ1, bZ2)) + 1;
        BiomeCache2d surfaceCache = createSurfaceBiomeCache(minQX, minQZ, maxQX, maxQZ);
        Set<Holder<Biome>> set = Sets.newHashSet();
        for (int qX = minQX; qX <= maxQX; qX++) {
            for (int qY = minQY; qY <= maxQY; qY++) {
                for (int qZ = minQZ; qZ <= maxQZ; qZ++) {
                    set.add(this.getNoiseBiome(qX, qY, qZ, surfaceCache, context));
                }
            }
        }
        return set;
    }
}
