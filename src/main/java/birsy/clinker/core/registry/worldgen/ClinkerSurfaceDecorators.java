package birsy.clinker.core.registry.worldgen;

import birsy.clinker.common.world.level.gen.OthershoreGenerationConstants;
import birsy.clinker.common.world.level.gen.content.surface.decorator.AshSteppeSurfaceDecorator;
import birsy.clinker.common.world.level.gen.content.surface.decorator.BrineSwampSurfaceDecorator;
import birsy.clinker.common.world.level.gen.content.surface.decorator.HeathSurfaceDecorator;
import birsy.clinker.common.world.level.gen.content.surface.decorator.SnakesSurfaceDecorator;
import birsy.clinker.common.world.level.gen.system.surface.decorator.BiomeSurfaceDecorator;
import birsy.clinker.common.world.level.gen.system.surface.decorator.SurfaceDecorator;
import birsy.clinker.core.Clinker;
import birsy.clinker.core.registry.ClinkerRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ClinkerSurfaceDecorators {
    public static final DeferredRegister<BiomeSurfaceDecorator> SURFACE_DECORATORS =
            DeferredRegister.create(ClinkerRegistries.SURFACE_DECORATOR_REGISTRY, Clinker.MOD_ID);

    public static final Supplier<BiomeSurfaceDecorator> ASH_STEPPE =
            register("ash_steppe", ClinkerBiomes.ASH_STEPPE, new AshSteppeSurfaceDecorator());
    public static final Supplier<BiomeSurfaceDecorator> BRINE_SWAMP =
            register("brine_swamp", ClinkerBiomes.BRINE_SWAMP, new BrineSwampSurfaceDecorator(OthershoreGenerationConstants.SEA_HEIGHT));
    public static final Supplier<BiomeSurfaceDecorator> BEACH =
            register("beach", ClinkerBiomes.TEMPLATE_BEACH, new BrineSwampSurfaceDecorator(OthershoreGenerationConstants.SEA_HEIGHT));
    public static final Supplier<BiomeSurfaceDecorator> HEATH =
            register("heath", ClinkerBiomes.HEATH, new HeathSurfaceDecorator());
    public static final Supplier<BiomeSurfaceDecorator> HEATH_THICKET =
            register("heath_thicket", ClinkerBiomes.HEATH_THICKET, new HeathSurfaceDecorator());
    public static final Supplier<BiomeSurfaceDecorator> BRINE_SNAKES =
            register("brine_snakes", ClinkerBiomes.BRINE_SNAKES, new SnakesSurfaceDecorator(OthershoreGenerationConstants.SEA_HEIGHT));

    public static Supplier<BiomeSurfaceDecorator> register(String name, ResourceKey<Biome> biome, SurfaceDecorator decorator) {
        return SURFACE_DECORATORS.register(name, () -> new BiomeSurfaceDecorator(biome, decorator));
    }

    public static Supplier<BiomeSurfaceDecorator> register(String name, TagKey<Biome> biomeTag, SurfaceDecorator decorator) {
        return SURFACE_DECORATORS.register(name, () -> new BiomeSurfaceDecorator(biomeTag, decorator));
    }
}
