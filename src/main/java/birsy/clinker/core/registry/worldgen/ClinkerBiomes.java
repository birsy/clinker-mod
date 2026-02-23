package birsy.clinker.core.registry.worldgen;

import birsy.clinker.core.Clinker;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;

import java.util.ArrayList;
import java.util.List;

public class ClinkerBiomes {
    public static final List<ResourceKey<Biome>> BIOMES = new ArrayList<>();

    public static final ResourceKey<Biome> ASH_STEPPE = register("ash_steppe");
    public static final ResourceKey<Biome> HEATH = register("heath");
    public static final ResourceKey<Biome> HEATH_THICKET = register("heath_thicket");

    public static final ResourceKey<Biome> SHORE = register("shore");
    public static final ResourceKey<Biome> BRINE_SWAMP = register("brine_swamp");
    public static final ResourceKey<Biome> BRINE_SNAKES = register("brine_snakes");

    public static final ResourceKey<Biome> UNDERGROUND = register("underground");
    public static final ResourceKey<Biome> AQUIFER = register("aquifer");

    private static ResourceKey<Biome> register(String pKey) {
        ResourceKey<Biome> key = ResourceKey.create(Registries.BIOME, Clinker.resource(pKey));
        BIOMES.add(key);
        return key;
    }
}
