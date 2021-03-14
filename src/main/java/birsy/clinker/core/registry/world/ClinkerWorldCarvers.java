package birsy.clinker.core.registry.world;

import birsy.clinker.common.world.gen.carver.OthershoreCanyonCaveCarver;
import birsy.clinker.common.world.gen.carver.OthershoreNoodleCaveCarver;
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
	
	public static final RegistryObject<WorldCarver<ProbabilityConfig>> OTHERSHORE_NOODLE_CAVE = WORLD_CARVERS.register("othershore_noodle_cave", () -> new OthershoreNoodleCaveCarver(ProbabilityConfig.CODEC, 256));
	public static final RegistryObject<WorldCarver<ProbabilityConfig>> OTHERSHORE_CANYON_CAVE = WORLD_CARVERS.register("othershore_canyon_cave", () -> new OthershoreCanyonCaveCarver(ProbabilityConfig.CODEC));
}
