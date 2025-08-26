package birsy.clinker.common.world.level.gen.surfacedecorator;

import birsy.clinker.common.world.level.gen.surfaceshaper.SurfaceShaper;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;

import java.util.HashMap;
import java.util.Map;

public class SurfaceDecorators {
    private static final Map<ResourceKey<Biome>, SurfaceDecorator> registry = new HashMap<>();
    private static final SurfaceDecorator DEFAULT = new AshSteppeSurfaceDecorator();

    public static SurfaceDecorator register(ResourceKey<Biome> biome, SurfaceDecorator surfaceDecorator) {
        registry.put(biome, surfaceDecorator);
        return surfaceDecorator;
    }

    public static SurfaceDecorator retrieve(ResourceKey<Biome> biome) {
        return registry.getOrDefault(biome, DEFAULT);
    }
}
