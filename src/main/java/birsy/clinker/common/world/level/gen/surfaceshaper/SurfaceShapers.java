package birsy.clinker.common.world.level.gen.surfaceshaper;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;

import java.util.HashMap;
import java.util.Map;

public class SurfaceShapers {
    private static final Map<ResourceKey<Biome>, SurfaceShaper> registry = new HashMap<>();
    private static final SurfaceShaper DEFAULT = (x, y, z, context) -> 0;



    public static SurfaceShaper register(ResourceKey<Biome> biome, SurfaceShaper surfaceShaper) {
        registry.put(biome, surfaceShaper);
        return surfaceShaper;
    }

    public static SurfaceShaper retrieve(ResourceKey<Biome> biome) {
        return registry.getOrDefault(biome, DEFAULT);
    }
}
