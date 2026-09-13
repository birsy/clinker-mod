package birsy.clinker.common.world.level.gen;

import birsy.clinker.common.world.level.gen.content.biome.placement.MutateOperation;
import birsy.clinker.common.world.level.gen.content.biome.placement.BiomeLayerOperations;
import birsy.clinker.common.world.level.gen.system.biome.BiomeGenerationInfo;
import birsy.clinker.common.world.level.gen.system.biome.placement.BiomeCache2d;
import birsy.clinker.common.world.level.gen.system.biome.placement.BiomeList;
import birsy.clinker.common.world.level.gen.system.biome.placement.resolver.LayeredBiomeResolver;
import birsy.clinker.common.world.level.gen.system.biome.placement.resolver.ProtoBiome;
import birsy.clinker.core.Clinker;
import birsy.clinker.core.registry.ClinkerRegistries;
import birsy.clinker.core.registry.worldgen.ClinkerBiomes;
import com.google.common.collect.Sets;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.*;
import net.minecraft.world.level.levelgen.PositionalRandomFactory;
import net.minecraft.world.level.levelgen.RandomState;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static birsy.clinker.core.registry.worldgen.ClinkerProtoBiomes.*;

public class OthershoreBiomeSource extends BiomeSource {
    public static final MapCodec<OthershoreBiomeSource> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(RegistryOps.retrieveGetter(Registries.BIOME))
                        .apply(instance, instance.stable(OthershoreBiomeSource::new))
    );
    private final HolderGetter<Biome> biomeGetter;
    private final Holder<Biome>[] biomeByProtoBiomeId;
    // placeholder, todo: underground biome source!
    // somehow adapt it to 3d...
    private final Holder<Biome> voidBiome, underground, aquifer;
    final BiomeList biomeList;
    private LayeredBiomeResolver surfaceBiomeResolver;

    public OthershoreBiomeSource(HolderGetter<Biome> biomeGetter) {
        super();
        this.biomeGetter = biomeGetter;
        biomeByProtoBiomeId = new Holder[ClinkerRegistries.PROTO_BIOME_REGISTRY.size()];
        underground = biomeGetter.getOrThrow(ClinkerBiomes.UNDERGROUND);
        aquifer = biomeGetter.getOrThrow(ClinkerBiomes.AQUIFER);
        voidBiome = biomeGetter.getOrThrow(Biomes.THE_VOID);
        for (ProtoBiome protoBiome : ClinkerRegistries.PROTO_BIOME_REGISTRY) {
            biomeByProtoBiomeId[protoBiome.id] = protoBiome.biome.map(biomeGetter::getOrThrow).orElse((Holder.Reference<Biome>) voidBiome);
        }

        this.biomeList = new BiomeList(this);
    }

    public void initRandomState(RandomState randomState) {
        this.surfaceBiomeResolver = createSurfaceBiomeResolver(randomState::getOrCreateRandomFactory);
    }

    public void initFromChunkGenerator(OthershoreChunkGenerator chunkGenerator) {

    }

    public static LayeredBiomeResolver createSurfaceBiomeResolver(Function<ResourceLocation, PositionalRandomFactory> randomState) {
        LayeredBiomeResolver.Builder builder = LayeredBiomeResolver.builder(8);
        BiomeLayerOperations.MutateBuilder baseMutationBuilder = new BiomeLayerOperations.MutateBuilder(UNINITIALIZED.get());
        for (int i = 0; i < 4; i++) {
            Supplier<ProtoBiome> baseSection = BASE_SECTIONS[i];
            baseMutationBuilder.entry(baseSection.get(), 10);
        }
        baseMutationBuilder.entry(SEA.get(), 1);

        return builder
                .layer(baseMutationBuilder.build())
                .zoom()
                .layer(new BiomeLayerOperations.RandomizeIntoNeighbor(1))
                .layer(new BiomeLayerOperations.Expand(SEA.get()))
                .layer(new BiomeLayerOperations.Smooth())
                .zoom()
                .layer(new BiomeLayerOperations.RandomizeIntoNeighbor(1))
                .layer(new BiomeLayerOperations.Smooth())
                .layer(new BiomeLayerOperations.MutateBuilder(SEA.get())
                        .entry(SEA.get(), 48)
                        .entry(ISLAND.get(), 1)
                        .build())
                .zoom()
                .layer(new BiomeLayerOperations.CreateBorders(
                                Set.of(BASE_SECTIONS[0].get(), BASE_SECTIONS[1].get()),
                                Set.of(BASE_SECTIONS[2].get(), BASE_SECTIONS[3].get()),
                                LOWER_SHELF.get()
                        ),
                        new BiomeLayerOperations.Replace(
                                Arrays.stream(BASE_SECTIONS)
                                        .map(Supplier::get)
                                        .collect(Collectors.toUnmodifiableSet()),
                                UPPER_SHELF.get()
                        )
                )
                .layer(new BiomeLayerOperations.Expand(LOWER_SHELF.get(), HEATH_THICKET.get()),
                        new BiomeLayerOperations.Expand(SEA.get()),
                        new BiomeLayerOperations.Expand(ISLAND.get()))
                .layer(new BiomeLayerOperations.RandomizeIntoNeighbor(1),
                        new BiomeLayerOperations.MutateBuilder(ISLAND.get())
                                .entry(SEA.get(), 1)
                                .entry(ISLAND.get(), 1)
                                .build())
                .layer(new BiomeLayerOperations.Smooth())
                .layer(new BiomeLayerOperations.CreateBorders(
                                Set.of(LOWER_SHELF.get()),
                                Set.of(UPPER_SHELF.get(), ASH_STEPPE.get()),
                                UNINITIALIZED.get()),
                        new BiomeLayerOperations.MutateBuilder(UNINITIALIZED.get())
                                .entry(LOWER_SHELF.get(), 1)
                                .entry(UPPER_SHELF.get(), 2)
                                .build())
                .layer(new BiomeLayerOperations.CreateBorders(
                                Set.of(UPPER_SHELF.get(), HEATH.get(), ASH_STEPPE.get()),
                                Set.of(LOWER_SHELF.get()),
                                SHELF_BORDER.get()),
                        new BiomeLayerOperations.CreateBorders(
                                Set.of(UPPER_SHELF.get(), LOWER_SHELF.get(), SHELF_BORDER.get(), HEATH.get(), ASH_STEPPE.get()),
                                Set.of(SEA.get()),
                                BEACH.get()
                        )
                )
                .layer(new BiomeLayerOperations.MutateBuilder(SHELF_BORDER.get())
                                .entry(SHELF_BORDER_CRACKLE.get(), 5)
                                .entry(SHELF_BORDER.get(), 6)
                                .entry(LOWER_SHELF.get(), 1)
                                .entry(UPPER_SHELF.get(), 1)
                                .build(),
                        new BiomeLayerOperations.MutateBuilder(LOWER_SHELF.get())
                                .entry(LOWER_SHELF.get(), 48)
                                .entry(UPPER_SHELF.get(), 1)
                                .build(),
                        new BiomeLayerOperations.MutateBuilder(UPPER_SHELF.get())
                                .entry(UPPER_SHELF.get(), 48)
                                .entry(UPPER_SHELF_PLATEAU.get(), 1)
                                .build()
                )
                .layer(new BiomeLayerOperations.CreateBorders(LOWER_SHELF.get(), UPPER_SHELF.get(), UNINITIALIZED.get()),
                        new BiomeLayerOperations.MutateBuilder(UNINITIALIZED.get())
                                .entry(LOWER_SHELF.get(), 2)
                                .entry(UPPER_SHELF.get(), 1)
                                .build(),
                        new BiomeLayerOperations.CreateBorders(UPPER_SHELF.get(), UPPER_SHELF_PLATEAU.get(), UNINITIALIZED.get()),
                        new BiomeLayerOperations.MutateBuilder(UNINITIALIZED.get())
                                .entry(UPPER_SHELF.get(), 2)
                                .entry(UPPER_SHELF_PLATEAU.get(), 1)
                                .build(),
                        new BiomeLayerOperations.CreateBorders(UPPER_SHELF.get(), BEACH.get(), BEACH.get()),
                        new BiomeLayerOperations.MutateBuilder(BEACH.get())
                                .entry(BEACH.get(), 2)
                                .entry(LOWER_SHELF.get(), 1)
                                .entry(UPPER_SHELF.get(), 1)
                                .build()
                )
                .layer(new BiomeLayerOperations.CreateBorders(
                                UPPER_SHELF.get(),
                                SEA.get(),
                                BEACH.get()
                        ),
                        MutateOperation.builder(0L)
                                .scale(8, randomState.apply(Clinker.resource("upper_shelf_biomes")))
                                .set(1)
                                .entry(UPPER_SHELF.get()).result(HEATH.get()).endEntry()
                                .entry(LOWER_SHELF.get(), SHELF_BORDER.get(), SHELF_BORDER_CRACKLE.get()).result(HEATH_THICKET.get()).endEntry()
                                .endSet()
                                .set(3)
                                .entry(UPPER_SHELF.get()).result(ASH_STEPPE.get()).endEntry()
                                .endSet()
                                .build(),
                        MutateOperation.builder(1L)
                                .scale(8, randomState.apply(Clinker.resource("sea_biomes")))
                                .set(1).endSet()
                                .set(1)
                                .entry(SEA.get()).result(BRINE_SWAMP.get()).endEntry()
                                .endSet()
                                .build()
                )
                .zoom()
                .layer(new BiomeLayerOperations.RandomizeIntoNeighbor(1))
                .layer(new BiomeLayerOperations.Smooth())
                .zoom()
                .layer(new BiomeLayerOperations.RandomizeIntoNeighbor(1))
                .layer(new BiomeLayerOperations.Smooth())
                .build(randomState);
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
        return getNoiseBiome(qX, qY, qZ, (BiomeCache2d) null);
    }
    @Override
    public Set<Holder<Biome>> getBiomesWithin(int x, int y, int z, int radius, Climate.Sampler sampler) {
        return this.getBiomesWithin(x, y, z, x + radius, y + radius, z + radius);
    }
    @Override
    protected MapCodec<? extends BiomeSource> codec() {
        return CODEC;
    }

    public BiomeCache2d createSurfaceBiomeCache(int minQX, int minQZ, int maxQX, int maxQZ) {
        BiomeCache2d cache = new BiomeCache2d(minQX, minQZ, maxQX, maxQZ);
        ProtoBiome[] protoBiomes = surfaceBiomeResolver.getProtoBiomeArea(minQX, minQZ, maxQX, maxQZ);
        for (int i = 0; i < protoBiomes.length; i++) {
            Holder<Biome> biome = biomeByProtoBiomeId[protoBiomes[i].id];
            cache.biomes[i] = biome;
            cache.containedBiomes.add(biome);
        }
        return cache;
    }

    public Holder<Biome> getSurfaceBiome(int qX, int qZ) {
        return biomeByProtoBiomeId[surfaceBiomeResolver.getProtoBiome(qX, qZ).id];
    }
    public Holder<Biome> getNoiseBiome(int qX, int qY, int qZ, @Nullable BiomeCache2d surfaceBiomeCache) {
        if (qY < 0) return aquifer;
        Holder<Biome> surfaceBiome = surfaceBiomeCache == null ? getSurfaceBiome(qX, qZ) : surfaceBiomeCache.retrieve(qX, qZ);
        int baseHeight = BiomeGenerationInfo.fromBiome(surfaceBiome).shaper().baseHeight;
        if (qY < QuartPos.fromBlock(baseHeight) - 4) return underground;
        return surfaceBiome;
    }
    public Set<Holder<Biome>> getBiomesWithin(int bX1, int bY1, int bZ1, int bX2, int bY2, int bZ2) {
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
                    set.add(this.getNoiseBiome(qX, qY, qZ, surfaceCache));
                }
            }
        }
        return set;
    }
}
