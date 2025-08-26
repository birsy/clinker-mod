package birsy.clinker.common.world.level.gen;

import birsy.clinker.common.world.level.gen.noise.NoiseComputerExecutor;
import birsy.clinker.core.registry.world.ClinkerBiomes;
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
    private static final int UPPER_SHELF_HEIGHT = 280,
            MIDDLE_SHELF_HEIGHT = 200,
            SEA_HEIGHT = 60;

    private Holder<Biome> voidBiome;
    private Holder<Biome> plateau, ashSteppe, cliffside, lowerShelf, brineSwamp, underground;


    public OthershoreBiomeSource(HolderGetter<Biome> biomeGetter) {
        voidBiome = biomeGetter.getOrThrow(Biomes.THE_VOID);

        plateau = biomeGetter.getOrThrow(ClinkerBiomes.PLATEAU);
        ashSteppe = biomeGetter.getOrThrow(ClinkerBiomes.ASH_STEPPE);
        cliffside = biomeGetter.getOrThrow(ClinkerBiomes.CLIFFSIDE);
        lowerShelf = biomeGetter.getOrThrow(ClinkerBiomes.LOWER_SHELF);
        brineSwamp = biomeGetter.getOrThrow(ClinkerBiomes.BRINE_SWAMP);
        underground = biomeGetter.getOrThrow(ClinkerBiomes.UNDERGROUND);
    }

    @Override
    protected Stream<Holder<Biome>> collectPossibleBiomes() {
        return Stream.of(voidBiome, plateau, ashSteppe, cliffside, lowerShelf, brineSwamp, underground);
    }

    // todo: find a way to create a temporary noise executor for this
    @Override
    public Holder<Biome> getNoiseBiome(int x, int y, int z, Climate.Sampler sampler) {return voidBiome;}

    public Holder<Biome> getNoiseBiome(int x, int y, int z, NoiseComputerExecutor noiseExecutor) {
        double surfaceHeight = noiseExecutor.compute(x, y, z, OthershoreNoiseComputers.SURFACE_HEIGHT_COMPUTER);
        if (y < surfaceHeight - 20) return underground;

        if (surfaceHeight > UPPER_SHELF_HEIGHT) return plateau;
        if (surfaceHeight > MIDDLE_SHELF_HEIGHT) return ashSteppe;

        if (surfaceHeight > SEA_HEIGHT) {
            // checks to make sure it still works along chunk borders
            int gradientOffsetX = Math.floorMod(x, 16) < 15 ? 1 : -1;
            int gradientOffsetZ = Math.floorMod(z, 16) < 15 ? 1 : -1;

            double gradientX = (surfaceHeight - noiseExecutor.compute(x + gradientOffsetX, y, z, OthershoreNoiseComputers.SURFACE_HEIGHT_COMPUTER)) / gradientOffsetX;
            double gradientZ = (surfaceHeight - noiseExecutor.compute(x, y, z + gradientOffsetZ, OthershoreNoiseComputers.SURFACE_HEIGHT_COMPUTER)) / gradientOffsetZ;
            double steepness = Math.sqrt(gradientX * gradientX + gradientZ * gradientZ);

            return steepness > 1.5 ? cliffside : lowerShelf;
        }

        return brineSwamp;
    }

    @Override
    protected MapCodec<? extends BiomeSource> codec() {
        return CODEC;
    }
}
