package birsy.clinker.core.registry.world;

import birsy.clinker.common.world.gen.carver.OthershoreCaveWorldCarver;
import birsy.clinker.core.Clinker;
import net.minecraft.world.gen.carver.WorldCarver;
import net.minecraft.world.gen.feature.ProbabilityConfig;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ClinkerWorldCarvers {
	public static final DeferredRegister<WorldCarver<?>> WORLD_CARVERS = DeferredRegister.create(ForgeRegistries.WORLD_CARVERS, Clinker.MOD_ID);
	public static void init()
	{	
		WORLD_CARVERS.register(FMLJavaModLoadingContext.get().getModEventBus());
	}
	
	public static final RegistryObject<WorldCarver<ProbabilityConfig>> LARGE_CANYON_CAVE = WORLD_CARVERS.register("large_canyon_cave", () -> new OthershoreCaveWorldCarver(ProbabilityConfig.CODEC, 256));
}
