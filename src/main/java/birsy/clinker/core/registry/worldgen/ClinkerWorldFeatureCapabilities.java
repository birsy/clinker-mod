package birsy.clinker.core.registry.worldgen;

import birsy.clinker.common.world.level.gen.system.metachunk.worldfeature.capabilities.*;
import birsy.clinker.core.Clinker;
import birsy.clinker.core.registry.ClinkerRegistries;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public final class ClinkerWorldFeatureCapabilities {
    public static final DeferredRegister<Class<? extends WorldFeatureCapability>> WORLD_FEATURE_CAPABILITIES = DeferredRegister.create(ClinkerRegistries.WORLD_FEATURE_CAPABILITY_REGISTRY, Clinker.MOD_ID);

    public static final Supplier<Class<ModifiesBiome>> MODIFIES_BIOME = WORLD_FEATURE_CAPABILITIES.register("modifies_biome", () -> ModifiesBiome.class);

    public static final Supplier<Class<ModifiesSurfaceSynthesizer>> MODIFIES_SURFACE_DENSITY = WORLD_FEATURE_CAPABILITIES.register("modifies_surface_density", () -> ModifiesSurfaceSynthesizer.class);
    public static final Supplier<Class<ModifiesCaveSynthesizer>> MODIFIES_CAVE_DENSITY = WORLD_FEATURE_CAPABILITIES.register("modifies_cave_density", () -> ModifiesCaveSynthesizer.class);
    public static final Supplier<Class<ModifiesFinalSynthesizer>> MODIFIES_FINAL_DENSITY = WORLD_FEATURE_CAPABILITIES.register("modifies_final_density", () -> ModifiesFinalSynthesizer.class);

    public static final Supplier<Class<ModifiesFluids>> MODIFIES_FLUIDS = WORLD_FEATURE_CAPABILITIES.register("modifies_fluids", () -> ModifiesFluids.class);
    public static final Supplier<Class<ModifiesWaterfallSynthesizer>> MODIFIES_WATERFALL_PRESENCE = WORLD_FEATURE_CAPABILITIES.register("modifies_waterfall_presence", () -> ModifiesWaterfallSynthesizer.class);

    public static final Supplier<Class<ModifiesSurfaceDecoration>> MODIFIES_SURFACE_DECORATION = WORLD_FEATURE_CAPABILITIES.register("modifies_surface_decoration", () -> ModifiesSurfaceDecoration.class);
}
