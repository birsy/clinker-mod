package birsy.clinker.core.registry;

import birsy.clinker.core.Clinker;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeMaker;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ClinkerBiomes
{
	public static final DeferredRegister<Biome> BIOMES = DeferredRegister.create(ForgeRegistries.BIOMES, Clinker.MOD_ID);

    static {
    	BIOMES.register("ash_steppes", BiomeMaker::makeVoidBiome);
    }
}