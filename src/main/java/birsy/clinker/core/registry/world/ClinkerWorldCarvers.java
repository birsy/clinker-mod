package birsy.clinker.core.registry.world;

import birsy.clinker.common.world.gen.carver.CrackleCarver;
import birsy.clinker.common.world.gen.carver.OthershoreCanyonCaveCarver;
import birsy.clinker.common.world.gen.carver.worleyimplementation.WorleyCarver;
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
	
	public static final RegistryObject<WorldCarver<ProbabilityConfig>> OTHERSHORE_NOODLE_CAVES = WORLD_CARVERS.register("othershore_noodle_caves", () -> new WorleyCarver(ProbabilityConfig.CODEC, 256, -0.22f));

	public static final RegistryObject<WorldCarver<ProbabilityConfig>> OTHERSHORE_CANYON_CAVE = WORLD_CARVERS.register("othershore_canyon_cave", () -> new OthershoreCanyonCaveCarver(ProbabilityConfig.CODEC));
}
