package birsy.clinker.common.world.level.gen;

import birsy.clinker.common.world.level.gen.system.biome.*;
import birsy.clinker.common.world.level.gen.content.biome.BiomeLayerOperations;
import birsy.clinker.common.world.level.gen.system.biome.resolver.LayeredBiomeResolver;
import birsy.clinker.common.world.level.gen.system.biome.resolver.ProtoBiome;
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
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;
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
       return LayeredBiomeResolver.builder(1)
               .layer(new BiomeLayerOperations.Replace(Set.of(UNINITIALIZED.get()), HEATH.get()))
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
        return getSurfaceBiome(qX, qZ);
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
        return surfaceBiomeCache == null ? getSurfaceBiome(qX, qZ) : surfaceBiomeCache.retrieve(qX, qZ);
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
