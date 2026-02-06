package birsy.clinker.core.registry.worldgen;

import birsy.clinker.common.world.level.gen.system.metachunk.worldfeature.capabilities.*;
import birsy.clinker.core.Clinker;
import birsy.clinker.core.registry.ClinkerRegistries;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public final class ClinkerWorldFeatureCapabilities {
    public static final DeferredRegister<Class<? extends WorldFeatureCapability>> WORLD_FEATURE_CAPABILITIES = DeferredRegister.create(ClinkerRegistries.WORLD_FEATURE_CAPABILITY_REGISTRY, Clinker.MOD_ID);

    public static final Supplier<Class<ModifiesBiome>> MODIFIES_BIOME = WORLD_FEATURE_CAPABILITIES.register("modifies_biome", () -> ModifiesBiome.class);

    public static final Supplier<Class<ModifiesHeightmap>> MODIFIES_HEIGHTMAP = WORLD_FEATURE_CAPABILITIES.register("modifies_heightmap", () -> ModifiesHeightmap.class);
    public static final Supplier<Class<ModifiesSurfaceDensity>> MODIFIES_SURFACE_DENSITY = WORLD_FEATURE_CAPABILITIES.register("modifies_surface_density", () -> ModifiesSurfaceDensity.class);
    public static final Supplier<Class<ModifiesCaveDensity>> MODIFIES_CAVE_DENSITY = WORLD_FEATURE_CAPABILITIES.register("modifies_cave_density", () -> ModifiesCaveDensity.class);
    public static final Supplier<Class<ModifiesFinalDensity>> MODIFIES_FINAL_DENSITY = WORLD_FEATURE_CAPABILITIES.register("modifies_final_density", () -> ModifiesFinalDensity.class);

    public static final Supplier<Class<ModifiesFluids>> MODIFIES_FLUIDS = WORLD_FEATURE_CAPABILITIES.register("modifies_fluids", () -> ModifiesFluids.class);
    public static final Supplier<Class<ModifiesWaterfallPresence>> MODIFIES_WATERFALL_PRESENCE = WORLD_FEATURE_CAPABILITIES.register("modifies_waterfall_presence", () -> ModifiesWaterfallPresence.class);
}
