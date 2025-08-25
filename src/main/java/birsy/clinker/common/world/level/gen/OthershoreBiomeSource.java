package birsy.clinker.common.world.level.gen;

import birsy.clinker.common.world.level.gen.noise.CachedNoiseComputerExecutor;
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
    protected NoiseComputerExecutor executor;
    private Holder<Biome> voidBiome, testBiomeA, testBiomeB;

    public OthershoreBiomeSource(HolderGetter<Biome> biomeGetter) {
        voidBiome = biomeGetter.getOrThrow(Biomes.THE_VOID);
        testBiomeA = biomeGetter.getOrThrow(ClinkerBiomes.TEST_A);
        testBiomeB = biomeGetter.getOrThrow(ClinkerBiomes.TEST_B);
    }

    @Override
    protected Stream<Holder<Biome>> collectPossibleBiomes() {
        return Stream.empty();
    }

    @Override
    public Holder<Biome> getNoiseBiome(int x, int y, int z, Climate.Sampler sampler) {
//        if (noiseCache == null)
//            return voidBiome;
        return voidBiome;
    }

    @Override
    protected MapCodec<? extends BiomeSource> codec() {
        return CODEC;
    }
}
