package birsy.clinker.common.world.level.gen.chunk.biome;

import birsy.clinker.common.world.level.gen.chunk.biome.terrainprovider.DefaultTerrainProvider;
import birsy.clinker.common.world.level.gen.chunk.biome.terrainprovider.TerrainProvider;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;

public class TerrainProviders {
    public static final HashMap<ResourceLocation, TerrainProvider> BIOME_TO_TERRAIN_PROVIDER = new HashMap<>();
    private static final TerrainProvider DEFAULT_PROVIDER = new DefaultTerrainProvider();

    //new BasicSurfaceDecorator(ClinkerBlocks.ASH.get().defaultBlockState(), ClinkerBlocks.BRIMSTONE.get().defaultBlockState(), ClinkerBlocks.BRIMSTONE.get().defaultBlockState(), 0)

    public static void register(ResourceLocation biome, TerrainProvider provider) {
        BIOME_TO_TERRAIN_PROVIDER.put(biome, provider);
    }
    public static TerrainProvider getTerrainProvider(ResourceLocation biomeLocation) {
        return BIOME_TO_TERRAIN_PROVIDER.getOrDefault(biomeLocation, DEFAULT_PROVIDER);
    }
}
