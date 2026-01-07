package birsy.clinker.common.world.level.gen;

import birsy.clinker.core.registry.worldgen.ClinkerBiomes;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.RegistryOps;
import net.minecraft.world.level.biome.*;

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
    public Holder<Biome> getNoiseBiome(int x, int y, int z, Climate.Sampler sampler) {
        return ashSteppe;
        //return this.getNoiseBiome(x, y, z, createNoiseComputerExecutor());
    }

//    @Override
//    public Set<Holder<Biome>> getBiomesWithin(int x, int y, int z, int radius, Climate.Sampler sampler) {
//        return this.getBiomesWithin(
//                x - radius, y - radius, z - radius,
//                x + radius, y + radius, z + radius,
//                createNoiseComputerExecutor()
//        );
//    }

//    public Set<Holder<Biome>> getBiomesWithin(int x1, int y1, int z1, int x2, int y2, int z2, NoiseFieldCache executor) {
//        int minX = QuartPos.fromBlock(Math.min(x1, x2)),
//            minY = QuartPos.fromBlock(Math.min(y1, y2)),
//            minZ = QuartPos.fromBlock(Math.min(z1, z2));
//        int maxX = QuartPos.fromBlock(Math.max(x1, x2)) + 1,
//            maxY = QuartPos.fromBlock(Math.max(y1, y2)) + 1,
//            maxZ = QuartPos.fromBlock(Math.max(z1, z2)) + 1;
//        Set<Holder<Biome>> set = Sets.newHashSet();
//
//        for (int x = minX; x <= maxX; x++) {
//            for (int y = minY; y <= maxY; y++) {
//                for (int z = minZ; z <= maxZ; z++) {
//                    set.add(this.getNoiseBiome(x << 2, y << 2, z << 2, executor));
//                }
//            }
//        }
//        return set;
//    }

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

    @Override
    protected MapCodec<? extends BiomeSource> codec() {
        return CODEC;
    }
//
//    protected static NoiseFieldCache createNoiseComputerExecutor() {
//        RandomState randomState = ServerLifecycleHooks.getCurrentServer()
//                .getLevel(ClinkerWorld.OTHERSHORE)
//                .getChunkSource().chunkMap.randomState();
//        return new UncachedNoiseComputerExecutor(
//                ((SeededNoiseHolderHolder)(Object)randomState).clinker$noiseHolder()
//        );
//    }
}
