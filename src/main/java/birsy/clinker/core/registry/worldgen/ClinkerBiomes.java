package birsy.clinker.core.registry.worldgen;

import birsy.clinker.common.world.level.gen.content.surface.decorator.AshSteppeSurfaceDecorator;
import birsy.clinker.common.world.level.gen.content.surface.shaper.AshSteppeSurfaceShaper;
import birsy.clinker.common.world.level.gen.system.surface.decorator.BiomeSurfaceDecorator;
import birsy.clinker.common.world.level.gen.system.surface.shaper.BiomeSurfaceShaper;
import birsy.clinker.core.Clinker;
import birsy.clinker.core.registration.ClinkerBiome;
import birsy.clinker.core.registry.ClinkerMusic;
import birsy.clinker.core.registry.ClinkerRegistries;
import birsy.clinker.core.registry.ClinkerSounds;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.biome.AmbientAdditionsSettings;
import net.minecraft.world.level.biome.AmbientMoodSettings;
import net.minecraft.world.level.biome.AmbientParticleSettings;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.ArrayList;
import java.util.List;

public class ClinkerBiomes {
    public static final List<ClinkerBiome> BIOMES = new ArrayList<>();
    public static final DeferredRegister<BiomeSurfaceShaper> SURFACE_SHAPERS =
            DeferredRegister.create(ClinkerRegistries.SURFACE_SHAPER_REGISTRY, Clinker.MOD_ID);
    public static final DeferredRegister<BiomeSurfaceDecorator> SURFACE_DECORATORS =
            DeferredRegister.create(ClinkerRegistries.SURFACE_DECORATOR_REGISTRY, Clinker.MOD_ID);

    public static final ClinkerBiome ASH_PLAINS = register(ClinkerBiome.builder("ash_plains")
            .loopSound(ClinkerSounds.AMBIENT_ASH_PLAINS_LOOP)
            .moodSound(new AmbientMoodSettings(
                    SoundEvents.AMBIENT_BASALT_DELTAS_MOOD,
                    6000, 8, 2.0))
            .additionsSound(new AmbientAdditionsSettings(
                    ClinkerSounds.AMBIENT_ASH_PLAINS_ADDITIONS,
                    0.0005))
            .music(ClinkerMusic.OTHERSHORE_SURFACE)
            .particle(new AmbientParticleSettings(ParticleTypes.WHITE_ASH, 0.01f))
            .addFeatures(GenerationStep.Decoration.LOCAL_MODIFICATIONS,
                    ResourceKey.create(Registries.PLACED_FEATURE, Clinker.resource("surface_blob_peat_moss")))
            .addFeatures(GenerationStep.Decoration.VEGETAL_DECORATION,
                    ResourceKey.create(Registries.PLACED_FEATURE, Clinker.resource("patch_mud_reeds")),
                    ResourceKey.create(Registries.PLACED_FEATURE, Clinker.resource("dried_clovers")),
                    ResourceKey.create(Registries.PLACED_FEATURE, Clinker.resource("blue_rose")))
            .surfaceShaper(new AshSteppeSurfaceShaper())
            .surfaceDecorator(new AshSteppeSurfaceDecorator())
            .build());

    public static final ResourceKey<Biome> ASH_STEPPE = register("ash_steppe");
    public static final ResourceKey<Biome> HEATH = register("heath");
    public static final ResourceKey<Biome> HEATH_THICKET = register("heath_thicket");

    public static final ResourceKey<Biome> SHORE = register("shore");
    public static final ResourceKey<Biome> BRINE_SWAMP = register("brine_swamp");
    public static final ResourceKey<Biome> BRINE_SNAKES = register("brine_snakes");

    public static final ResourceKey<Biome> UNDERGROUND = register("underground");
    public static final ResourceKey<Biome> AQUIFER = register("aquifer");

    public static final ResourceKey<Biome> PLACEHOLDER_UPPER_SHELF = register("placeholder_upper_shelf");
    public static final ResourceKey<Biome> PLACEHOLDER_LOWER_SHELF = register("placeholder_lower_shelf");
    public static final ResourceKey<Biome> PLACEHOLDER_SHELF_BORDER = register("placeholder_shelf_border");

    private static ClinkerBiome register(ClinkerBiome biome) {
        BIOMES.add(biome);
        return biome;
    }
    private static ResourceKey<Biome> register(String name) {
        return ResourceKey.create(Registries.BIOME, Clinker.resource(name));
    }

    public static void bootstrap(IEventBus modEventBus) {
        for (ClinkerBiome biome : BIOMES) {
            if (biome.surfaceShaper() != null)
                SURFACE_SHAPERS.register(biome.key().location().getPath() + "_shaper", () -> new BiomeSurfaceShaper(biome.key(), biome.surfaceShaper()));
            if (biome.surfaceDecorator() != null)
                SURFACE_DECORATORS.register(biome.key().location().getPath() + "_decorator", () -> new BiomeSurfaceDecorator(biome.key(), biome.surfaceDecorator()));
        }
        SURFACE_SHAPERS.register(modEventBus);
        SURFACE_DECORATORS.register(modEventBus);
    }
}
