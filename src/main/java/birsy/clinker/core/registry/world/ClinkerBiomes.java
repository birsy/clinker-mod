package birsy.clinker.core.registry.world;

import birsy.clinker.core.Clinker;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;

public class ClinkerBiomes {
    public static final ResourceKey<Biome> ASH_STEPPE = register("ash_steppe");

    private static ResourceKey<Biome> register(String pKey) {
        return ResourceKey.create(Registries.BIOME, name(pKey));
    }

    private static ResourceLocation name(String name) {
        return new ResourceLocation(Clinker.MOD_ID, name);
    }
}
