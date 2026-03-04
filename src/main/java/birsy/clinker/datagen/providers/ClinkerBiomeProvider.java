package birsy.clinker.datagen.providers;

import birsy.clinker.core.registration.ClinkerBiome;
import birsy.clinker.core.registry.worldgen.ClinkerBiomes;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.world.level.biome.Biome;

public class ClinkerBiomeProvider {
    public static void addBiomes(BootstrapContext<Biome> context) {
        for (ClinkerBiome biome : ClinkerBiomes.BIOMES) biome.register(context);
    }
}
