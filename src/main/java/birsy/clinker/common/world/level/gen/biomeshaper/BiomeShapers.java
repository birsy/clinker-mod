package birsy.clinker.common.world.level.gen.biomeshaper;

import birsy.clinker.common.world.level.gen.OthershoreNoiseComputers;
import birsy.clinker.core.registry.world.ClinkerBiomes;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;

import java.util.HashMap;
import java.util.Map;

public class BiomeShapers {
    private static final Map<ResourceKey<Biome>, BiomeShaper> registry = new HashMap<>();
    private static final BiomeShaper DEFAULT =
            (x, y, z, biomeContribution, context) -> y - context.noiseComputerExecutor().compute(x, y, z, OthershoreNoiseComputers.SURFACE_HEIGHT_COMPUTER);

    public static final BiomeShaper ASH_STEPPE = register(ClinkerBiomes.ASH_STEPPE, new AshSteppeBiomeShaper());
    public static final BiomeShaper BRINE_SWAMP = register(ClinkerBiomes.BRINE_SWAMP, new BrineSwampBiomeShaper());

    public static final BiomeShaper CLIFFSIDE = register(ClinkerBiomes.CLIFFSIDE, new LowerShelfBiomeShaper());
    public static final BiomeShaper LOWER_SHELF = register(ClinkerBiomes.LOWER_SHELF, new LowerShelfBiomeShaper());

    public static BiomeShaper register(ResourceKey<Biome> biome, BiomeShaper biomeShaper) {
        registry.put(biome, biomeShaper);
        return biomeShaper;
    }

    public static BiomeShaper retrieve(ResourceKey<Biome> biome) {
        return registry.getOrDefault(biome, DEFAULT);
    }
}
