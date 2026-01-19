package birsy.clinker.common.world.level.gen;

import birsy.clinker.common.world.level.gen.system.biome.BiomeCache2d;
import birsy.clinker.common.world.level.gen.system.noise.NoiseContext;
import birsy.clinker.common.world.level.gen.system.noise.NoiseFieldCache;
import birsy.clinker.common.world.level.gen.system.noise.SeededNoiseHolderHolder;
import birsy.clinker.common.world.level.gen.system.noise.UncachedNoiseContext;
import birsy.clinker.core.Clinker;
import birsy.clinker.core.registry.worldgen.ClinkerBiomes;
import birsy.clinker.core.registry.worldgen.ClinkerNoiseComputers;
import birsy.clinker.core.registry.worldgen.ClinkerWorld;
import com.google.common.collect.Sets;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.QuartPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.RegistryOps;
import net.minecraft.world.level.biome.*;
import net.minecraft.world.level.levelgen.RandomState;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

public class OthershoreBiomeSource extends BiomeSource {
    public static final MapCodec<OthershoreBiomeSource> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(RegistryOps.retrieveGetter(Registries.BIOME))
                        .apply(instance, instance.stable(OthershoreBiomeSource::new))
    );
    public static final int UPPER_SHELF_HEIGHT = 230,
            MIDDLE_SHELF_HEIGHT = 180,
            SEA_HEIGHT = 64;

    private Holder<Biome> voidBiome;
    private Holder<Biome> plateau, ashSteppe, cliffside, lowerShelf, brineSwamp, underground, aquifer;
    private UncachedNoiseContext uncachedNoiseContext;

    public OthershoreBiomeSource(HolderGetter<Biome> biomeGetter) {
        voidBiome = biomeGetter.getOrThrow(Biomes.THE_VOID);

        plateau = biomeGetter.getOrThrow(ClinkerBiomes.PLATEAU);
        ashSteppe = biomeGetter.getOrThrow(ClinkerBiomes.ASH_STEPPE);
        cliffside = biomeGetter.getOrThrow(ClinkerBiomes.CLIFFSIDE);
        lowerShelf = biomeGetter.getOrThrow(ClinkerBiomes.LOWER_SHELF);
        brineSwamp = biomeGetter.getOrThrow(ClinkerBiomes.BRINE_SWAMP);
        underground = biomeGetter.getOrThrow(ClinkerBiomes.UNDERGROUND);
        aquifer = biomeGetter.getOrThrow(ClinkerBiomes.AQUIFER);
    }

    @Override
    protected Stream<Holder<Biome>> collectPossibleBiomes() {
        return Stream.of(voidBiome, plateau, ashSteppe, cliffside, lowerShelf, brineSwamp, underground, aquifer);
    }
    @Override
    public Holder<Biome> getNoiseBiome(int qX, int qY, int qZ, Climate.Sampler sampler) {
        return this.getNoiseBiome(qX, qY, qZ, getUncachedContext());
    }
    @Override
    public Set<Holder<Biome>> getBiomesWithin(int x, int y, int z, int radius, Climate.Sampler sampler) {
        return this.getBiomesWithin(x, y, z, x + radius, y + radius, z + radius, getUncachedContext());
    }
    @Override
    protected MapCodec<? extends BiomeSource> codec() {
        return CODEC;
    }

    public void prefillSurfaceNoiseFields(NoiseFieldCache cache) {
        cache.fillNoiseField(ClinkerNoiseComputers.BASE_SURFACE_HEIGHT);
    }
    public Holder<Biome> getSurfaceBiome(int qX, int qZ, NoiseContext context) {
        double surfaceHeight = context.retrieve(ClinkerNoiseComputers.BASE_SURFACE_HEIGHT, QuartPos.toBlock(qX), 0, QuartPos.toBlock(qZ));
        if (surfaceHeight < SEA_HEIGHT + 20) return brineSwamp;
        return ashSteppe;
    }
    public Set<Holder<Biome>> getSurfaceBiomesWithin(int x1, int z1, int x2, int z2, NoiseContext context) {
        int minQX = QuartPos.fromBlock(Math.min(x1, x2)),
            minQZ = QuartPos.fromBlock(Math.min(z1, z2));
        int maxQX = QuartPos.fromBlock(Math.max(x1, x2)) + 1,
            maxQZ = QuartPos.fromBlock(Math.max(z1, z2)) + 1;
        Set<Holder<Biome>> set = new HashSet<>(3);
        for (int qX = minQX; qX <= maxQX; qX++) {
            for (int qZ = minQZ; qZ < maxQZ; qZ++) {
                set.add(getSurfaceBiome(qX, qZ, context));
            }
        }
        return set;
    }
    public BiomeCache2d createSurfaceBiomeCache(int minQX, int minQZ, int maxQX, int maxQZ, NoiseContext context) {
        BiomeCache2d cache = new BiomeCache2d(minQX, minQZ, maxQX, maxQZ);
        int index = 0;
        for (int z = 0; z < cache.sizeZ; z++) {
            int globalQZ = cache.minQuartZ + z;
            for (int x = 0; x < cache.sizeX; x++) {
                int globalQX = cache.minQuartX + x;
                Holder<Biome> biome = getSurfaceBiome(globalQX, globalQZ, context);
                cache.biomes[index++] = biome;
                cache.containedBiomes.add(biome);
            }
        }
        return cache;
    }

    public void prefillNoiseFields(NoiseFieldCache cache) {
        this.prefillSurfaceNoiseFields(cache);
        cache.fillNoiseField(ClinkerNoiseComputers.BASE_SURFACE_HEIGHT);
    }
    public Holder<Biome> getNoiseBiome(int qX, int qY, int qZ, NoiseContext context) {
        int bX = QuartPos.toBlock(qX), bY = QuartPos.toBlock(qY), bZ = QuartPos.toBlock(qZ);
        if (bY < 0) return aquifer;
        double groundHeight = context.retrieve(ClinkerNoiseComputers.BASE_SURFACE_HEIGHT, bX, bY, bZ) - 10;
        if (bY < groundHeight) return underground;
        return getSurfaceBiome(qX, qZ, context);
    }
    public Holder<Biome> getNoiseBiome(int qX, int qY, int qZ, BiomeCache2d surfaceBiomeCache, NoiseContext context) {
        int bX = QuartPos.toBlock(qX), bY = QuartPos.toBlock(qY), bZ = QuartPos.toBlock(qZ);
        if (bY < 0) return aquifer;
        double groundHeight = context.retrieve(ClinkerNoiseComputers.BASE_SURFACE_HEIGHT, bX, bY, bZ) - 10;
        if (bY < groundHeight) return underground;
        return surfaceBiomeCache.retrieve(qX, qZ);
    }
    public Set<Holder<Biome>> getBiomesWithin(int bX1, int bY1, int bZ1, int bX2, int bY2, int bZ2, NoiseContext context) {
        int minQX = QuartPos.fromBlock(Math.min(bX1, bX2)),
            minQY = QuartPos.fromBlock(Math.min(bY1, bY2)),
            minQZ = QuartPos.fromBlock(Math.min(bZ1, bZ2));
        int maxQX = QuartPos.fromBlock(Math.max(bX1, bX2)) + 1,
            maxQY = QuartPos.fromBlock(Math.max(bY1, bY2)) + 1,
            maxQZ = QuartPos.fromBlock(Math.max(bZ1, bZ2)) + 1;
        BiomeCache2d surfaceCache = createSurfaceBiomeCache(minQX, minQZ, maxQX, maxQZ, context);
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

    private NoiseContext getUncachedContext() {
        if (this.uncachedNoiseContext == null) {
            RandomState randomState = ServerLifecycleHooks.getCurrentServer()
                            .getLevel(ClinkerWorld.OTHERSHORE)
                            .getChunkSource().chunkMap.randomState();
            this.uncachedNoiseContext = new UncachedNoiseContext(((SeededNoiseHolderHolder)(Object)randomState).clinker$noiseHolder());
        }
        return this.uncachedNoiseContext;
    }

//    public Holder<Biome> getNoiseBiome(int x, int y, int z, NoiseFieldCache noiseExecutor) {
//        double surfaceHeight = noiseExecutor.compute(x, y, z, OthershoreNoiseComputers.SURFACE_HEIGHT_COMPUTER);
//
//        if (y < surfaceHeight - 20) {
//            if (y < 0) return aquifer;
//            return underground;
//        }
//
//        if (surfaceHeight > UPPER_SHELF_HEIGHT) return plateau;
//        if (surfaceHeight > MIDDLE_SHELF_HEIGHT) return ashSteppe;
//
//        if (surfaceHeight > SEA_HEIGHT) {
//            // checks to make sure it still works along chunk borders
//            int gradientOffsetX = Math.floorMod(x, 16) < 15 ? 1 : -1;
//            int gradientOffsetZ = Math.floorMod(z, 16) < 15 ? 1 : -1;
//
//            double gradientX = (surfaceHeight - noiseExecutor.compute(x + gradientOffsetX, y, z, OthershoreNoiseComputers.SURFACE_HEIGHT_COMPUTER)) / gradientOffsetX;
//            double gradientZ = (surfaceHeight - noiseExecutor.compute(x, y, z + gradientOffsetZ, OthershoreNoiseComputers.SURFACE_HEIGHT_COMPUTER)) / gradientOffsetZ;
//            double steepness = Math.sqrt(gradientX * gradientX + gradientZ * gradientZ);
//
//            return steepness > 0.7 ? cliffside : lowerShelf;
//        }
//
//        return brineSwamp;
//    }
}
