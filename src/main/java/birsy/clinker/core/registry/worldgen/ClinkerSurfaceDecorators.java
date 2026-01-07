package birsy.clinker.core.registry.worldgen;

import birsy.clinker.common.world.level.gen.content.surface.AshSteppeSurfaceDecorator;
import birsy.clinker.common.world.level.gen.content.surface.BrineSwampSurfaceDecorator;
import birsy.clinker.common.world.level.gen.content.surface.DefaultSurfaceDecorator;
import birsy.clinker.common.world.level.gen.system.surface.BiomeSurfaceDecorator;
import birsy.clinker.common.world.level.gen.system.surface.SurfaceDecorator;
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

//    public static final Supplier<BiomeSurfaceDecorator> ASH_STEPPE =
//            register("ash_steppe", ClinkerBiomes.ASH_STEPPE, new AshSteppeSurfaceDecorator());
//    public static final Supplier<BiomeSurfaceDecorator> BRINE_SWAMP =
//            register("brine_swamp", ClinkerBiomes.BRINE_SWAMP, new BrineSwampSurfaceDecorator());
//    public static final Supplier<BiomeSurfaceDecorator> UNDERGROUND =
//            register("underground", ClinkerBiomes.UNDERGROUND, new DefaultSurfaceDecorator());
//    public static final Supplier<BiomeSurfaceDecorator> AQUIFER =
//            register("aquifer", ClinkerBiomes.AQUIFER, new DefaultSurfaceDecorator());

    public static Supplier<BiomeSurfaceDecorator> register(String name, ResourceKey<Biome> biome, SurfaceDecorator decorator) {
        return SURFACE_DECORATORS.register(name, () -> new BiomeSurfaceDecorator(biome, decorator));
    }

    public static Supplier<BiomeSurfaceDecorator> register(String name, TagKey<Biome> biomeTag, SurfaceDecorator decorator) {
        return SURFACE_DECORATORS.register(name, () -> new BiomeSurfaceDecorator(biomeTag, decorator));
    }
}
