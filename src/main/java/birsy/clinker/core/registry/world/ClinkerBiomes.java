package birsy.clinker.core.registry.world;

import birsy.clinker.core.Clinker;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.Registry;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.data.worldgen.biome.VanillaBiomes;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeManager;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ClinkerBiomes
{
    public static final DeferredRegister<Biome> BIOMES = DeferredRegister.create(ForgeRegistries.BIOMES, Clinker.MOD_ID);

    public static final RegistryObject<Biome> ASH_STEPPES = BIOMES.register("ash_steppes", () -> VanillaBiomes.theVoidBiome());

    public static void registerBiomes() {
        registerBiome("ash_steppes", ASH_STEPPES.get(), BiomeManager.BiomeType.DESERT, BiomeDictionary.Type.DRY, BiomeDictionary.Type.DEAD, BiomeDictionary.Type.MAGICAL, BiomeDictionary.Type.WASTELAND);
    }

    public static void registerBiome(String name, Biome biome, BiomeManager.BiomeType type, BiomeDictionary.Type... types) {
        BiomeManager.addBiome(type, new BiomeManager.BiomeEntry(ResourceKey.create(Registry.BIOME_REGISTRY, new ResourceLocation(Clinker.MOD_ID, name)), 5));
    }
}