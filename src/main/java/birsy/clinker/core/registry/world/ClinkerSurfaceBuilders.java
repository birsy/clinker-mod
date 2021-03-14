package birsy.clinker.core.registry.world;

import birsy.clinker.common.world.gen.surfacebuilder.AshSteppesSurfaceBuilder;
import birsy.clinker.core.Clinker;
import net.minecraft.world.gen.surfacebuilders.SurfaceBuilder;
import net.minecraft.world.gen.surfacebuilders.SurfaceBuilderConfig;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ClinkerSurfaceBuilders
{	
	public static final DeferredRegister<SurfaceBuilder<?>> SURFACE_BUILDERS = DeferredRegister.create(ForgeRegistries.SURFACE_BUILDERS, Clinker.MOD_ID);
	public static void init()
	{
		SURFACE_BUILDERS.register(FMLJavaModLoadingContext.get().getModEventBus());
	}
	
	public static final RegistryObject<SurfaceBuilder<SurfaceBuilderConfig>> ASH_STEPPES_BUILDER = SURFACE_BUILDERS.register("ash_steppes_builder", () -> new AshSteppesSurfaceBuilder(SurfaceBuilderConfig.field_237203_a_));	
	
	/**
	public static final SurfaceBuilder<SurfaceBuilderConfig> ASH_PLAINS_BUILDER2 = register("ash_plains_builder", new AshPlainsSurfaceBuilder(SurfaceBuilderConfig.field_237203_a_));
	
	@SuppressWarnings("deprecation")
	private static <C extends ISurfaceBuilderConfig, F extends SurfaceBuilder<C>> F register(String key, F builderIn) {
		return Registry.register(Registry.SURFACE_BUILDER, key, builderIn);
	}
	*/
}