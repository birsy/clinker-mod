package birsy.clinker.common.world.level.gen.surfacedecorator;

import birsy.clinker.common.world.level.gen.surfaceshaper.SurfaceShaper;
import birsy.clinker.core.registry.world.ClinkerBiomes;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;

import java.util.HashMap;
import java.util.Map;

public class SurfaceDecorators {
    private static final Map<ResourceKey<Biome>, SurfaceDecorator> registry = new HashMap<>();
    private static final SurfaceDecorator DEFAULT = new DefaultSurfaceDecorator();

    public static final SurfaceDecorator ASH_STEPPE = register(ClinkerBiomes.ASH_STEPPE, new AshSteppeSurfaceDecorator());
    public static final SurfaceDecorator BRINE_SWAMP = register(ClinkerBiomes.BRINE_SWAMP, new BrineSwampSurfaceDecorator());

    public static final SurfaceDecorator UNDERGROUND = register(ClinkerBiomes.UNDERGROUND, new DefaultSurfaceDecorator());
    public static final SurfaceDecorator AQUIFER = register(ClinkerBiomes.AQUIFER, new DefaultSurfaceDecorator());

    public static SurfaceDecorator register(ResourceKey<Biome> biome, SurfaceDecorator surfaceDecorator) {
        registry.put(biome, surfaceDecorator);
        return surfaceDecorator;
    }

    public static SurfaceDecorator retrieve(ResourceKey<Biome> biome) {
        return registry.getOrDefault(biome, DEFAULT);
    }
}
