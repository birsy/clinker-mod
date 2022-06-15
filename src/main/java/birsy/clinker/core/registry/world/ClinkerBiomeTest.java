package birsy.clinker.core.registry.world;

import birsy.clinker.core.Clinker;
import net.minecraft.world.level.biome.Biome;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ClinkerBiomeTest {
    public static final DeferredRegister<Biome> BIOMES = DeferredRegister.create(ForgeRegistries.BIOMES, Clinker.MOD_ID);

    // Dummy biomes to make BiomeManager not blow up and for the json biomes to overwrite biome in world.
    /*
    static {
        BIOMES.register("overworld_two", VanillaBiomes::theVoidBiome);

        BiomeManager.addBiome(BiomeManager.BiomeType.WARM,
                new BiomeManager.BiomeEntry(ResourceKey.create(Registry.BIOME_REGISTRY,
                        new ResourceLocation(Clinker.MOD_ID, "overworld_two")),
                        5));
    }*/
}
