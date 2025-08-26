package birsy.clinker.core.registry.world;

import birsy.clinker.core.Clinker;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;

public class ClinkerBiomes {
    public static final ResourceKey<Biome> PLATEAU = register("plateau");
    public static final ResourceKey<Biome> ASH_STEPPE = register("ash_steppe");
    public static final ResourceKey<Biome> CLIFFSIDE = register("cliffside");
    public static final ResourceKey<Biome> LOWER_SHELF = register("lower_shelf");
    public static final ResourceKey<Biome> BRINE_SWAMP = register("brine_swamp");
    public static final ResourceKey<Biome> UNDERGROUND = register("underground");

    private static ResourceKey<Biome> register(String pKey) {
        return ResourceKey.create(Registries.BIOME, name(pKey));
    }

    private static ResourceLocation name(String name) {
        return Clinker.resource(name);
    }
}
