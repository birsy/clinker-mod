package birsy.clinker.core.registry.world;

import birsy.clinker.core.Clinker;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeMaker;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeManager;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ClinkerBiomes
{
    public static final DeferredRegister<Biome> BIOMES = DeferredRegister.create(ForgeRegistries.BIOMES, Clinker.MOD_ID);

    public static final RegistryObject<Biome> ASH_STEPPES = BIOMES.register("ash_steppes", () -> BiomeMaker.makeVoidBiome());

    public static void registerBiomes() {
        registerBiome("ash_steppes", ASH_STEPPES.get(), BiomeManager.BiomeType.DESERT, BiomeDictionary.Type.DRY, BiomeDictionary.Type.DEAD, BiomeDictionary.Type.MAGICAL, BiomeDictionary.Type.WASTELAND);
    }

    public static void registerBiome(String name, Biome biome, BiomeManager.BiomeType type, BiomeDictionary.Type... types) {
        BiomeManager.addBiome(type, new BiomeManager.BiomeEntry(RegistryKey.getOrCreateKey(Registry.BIOME_KEY, new ResourceLocation(Clinker.MOD_ID, name)), 5));
    }
}