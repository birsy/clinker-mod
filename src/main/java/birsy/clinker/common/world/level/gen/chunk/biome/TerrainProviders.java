package birsy.clinker.common.world.level.gen.chunk.biome;

import birsy.clinker.common.world.level.gen.chunk.biome.surfacedecorator.AshSteppeSurfaceDecorator;
import birsy.clinker.common.world.level.gen.chunk.biome.terrainprovider.*;
import birsy.clinker.core.Clinker;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;

public class TerrainProviders {
    public static final HashMap<ResourceLocation, TerrainProvider> BIOME_TO_TERRAIN_PROVIDER = new HashMap<>();
    private static final TerrainProvider DEFAULT_PROVIDER = new DefaultTerrainProvider();

    static {
        register(new ResourceLocation(Clinker.MOD_ID, "ash_steppe"),
                new AshSteppeTerrainProvider());
        register(new ResourceLocation(Clinker.MOD_ID, "test_biome_a"),
                new TestTerrainProviderA());
        register(new ResourceLocation(Clinker.MOD_ID, "test_biome_b"),
                new TestTerrainProviderB());
        register(new ResourceLocation(Clinker.MOD_ID, "test_biome_c"),
                new TestTerrainProviderC());
    }

    public static void register(ResourceLocation biome, TerrainProvider provider) {
        BIOME_TO_TERRAIN_PROVIDER.put(biome, provider);
    }
    public static TerrainProvider getTerrainProvider(ResourceLocation biomeLocation) {
        return BIOME_TO_TERRAIN_PROVIDER.getOrDefault(biomeLocation, DEFAULT_PROVIDER);
    }
}
