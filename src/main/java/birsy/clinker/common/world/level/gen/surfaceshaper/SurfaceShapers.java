package birsy.clinker.common.world.level.gen.surfaceshaper;

import birsy.clinker.common.world.level.gen.OthershoreNoiseComputers;
import birsy.clinker.core.registry.world.ClinkerBiomes;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;

import java.util.HashMap;
import java.util.Map;

public class SurfaceShapers {
    private static final Map<ResourceKey<Biome>, SurfaceShaper> registry = new HashMap<>();
    private static final SurfaceShaper DEFAULT = (x, y, z, biomeContribution, context) -> y - context.noiseComputerExecutor().compute(x, y, z, OthershoreNoiseComputers.SURFACE_HEIGHT_COMPUTER);

    public static final SurfaceShaper ASH_STEPPE = register(ClinkerBiomes.ASH_STEPPE, new AshSteppeSurfaceShaper());
    public static final SurfaceShaper BRINE_SWAMP = register(ClinkerBiomes.BRINE_SWAMP, new BrineSwampSurfaceShaper());

    public static final SurfaceShaper CLIFFSIDE = register(ClinkerBiomes.CLIFFSIDE, new LowerShelfSurfaceShaper());
    public static final SurfaceShaper LOWER_SHELF = register(ClinkerBiomes.LOWER_SHELF, new LowerShelfSurfaceShaper());

    public static SurfaceShaper register(ResourceKey<Biome> biome, SurfaceShaper surfaceShaper) {
        registry.put(biome, surfaceShaper);
        return surfaceShaper;
    }

    public static SurfaceShaper retrieve(ResourceKey<Biome> biome) {
        return registry.getOrDefault(biome, DEFAULT);
    }
}
