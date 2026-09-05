package birsy.clinker.core.registry.worldgen;

import birsy.clinker.core.Clinker;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;

public class ClinkerBiomes {
    public static final ResourceKey<Biome> ASH_STEPPE = register("ash_steppe");
    public static final ResourceKey<Biome> HEATH = register("heath");
    public static final ResourceKey<Biome> HEATH_THICKET = register("heath_thicket");

    public static final ResourceKey<Biome> SHORE = register("shore");
    public static final ResourceKey<Biome> BRINE_SWAMP = register("brine_swamp");
    public static final ResourceKey<Biome> BRINE_SNAKES = register("brine_snakes");

    public static final ResourceKey<Biome> UNDERGROUND = register("underground");
    public static final ResourceKey<Biome> AQUIFER = register("aquifer");

    public static final ResourceKey<Biome> TEMPLATE_UPPER_SHELF_PLATEAU = register("template_upper_shelf_plateau");
    public static final ResourceKey<Biome> TEMPLATE_UPPER_SHELF = register("template_upper_shelf");
    public static final ResourceKey<Biome> TEMPLATE_LOWER_SHELF = register("template_lower_shelf");
    public static final ResourceKey<Biome> TEMPLATE_SHELF_BORDER = register("template_shelf_border");
    public static final ResourceKey<Biome> TEMPLATE_SHELF_BORDER_CRACKLE = register("template_shelf_border_crackle");
    public static final ResourceKey<Biome> TEMPLATE_BEACH = register("template_beach");
    public static final ResourceKey<Biome> TEMPLATE_SEA = register("template_sea");

    private static ResourceKey<Biome> register(String name) {
        return ResourceKey.create(Registries.BIOME, Clinker.resource(name));
    }
}
