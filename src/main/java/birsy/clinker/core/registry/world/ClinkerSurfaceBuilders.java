package birsy.clinker.core.registry.world;

import birsy.clinker.common.world.gen.surfacebuilder.*;
import birsy.clinker.core.Clinker;
import birsy.clinker.core.registry.ClinkerBlocks;
import net.minecraft.world.level.levelgen.surfacebuilders.SurfaceBuilder;
import net.minecraft.world.level.levelgen.surfacebuilders.SurfaceBuilderBaseConfiguration;
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
	
	public static final RegistryObject<SurfaceBuilder<SurfaceBuilderBaseConfiguration>> ASH_STEPPES_BUILDER = SURFACE_BUILDERS.register("ash_steppes_builder", () -> new CaveSurfaceBuilder(SurfaceBuilderBaseConfiguration.CODEC)); /*() -> new StrataSurfaceBuilder(SurfaceBuilderConfig.CODEC,
			ClinkerBlocks.SHALE.get().getDefaultState(),
			ClinkerBlocks.BRIMSTONE.get().getDefaultState(),
			ClinkerBlocks.PACKED_ASH.get().getDefaultState(),
			ClinkerBlocks.BRIMSTONE.get().getDefaultState(),
			ClinkerBlocks.SMOOTH_SHALE.get().getDefaultState(),
			ClinkerBlocks.BRIMSTONE.get().getDefaultState(),
			ClinkerBlocks.ASH.get().getDefaultState(),
			ClinkerBlocks.BRIMSTONE.get().getDefaultState(),
			ClinkerBlocks.SHALE.get().getDefaultState(),
			ClinkerBlocks.BRIMSTONE.get().getDefaultState(),
			ClinkerBlocks.SMOOTH_SHALE.get().getDefaultState(),
			ClinkerBlocks.BRIMSTONE.get().getDefaultState(),
			ClinkerBlocks.PACKED_ASH.get().getDefaultState(),
			ClinkerBlocks.BRIMSTONE.get().getDefaultState(),
			ClinkerBlocks.SMOOTH_SHALE.get().getDefaultState(),
			ClinkerBlocks.BRIMSTONE.get().getDefaultState(),
			ClinkerBlocks.ASH.get().getDefaultState(),
			ClinkerBlocks.BRIMSTONE.get().getDefaultState(),
			ClinkerBlocks.PACKED_ASH.get().getDefaultState(),
			ClinkerBlocks.BRIMSTONE.get().getDefaultState(),
			ClinkerBlocks.SMOOTH_SHALE.get().getDefaultState(),
			ClinkerBlocks.BRIMSTONE.get().getDefaultState(),
			ClinkerBlocks.ASH.get().getDefaultState()));
	*/


	public static final RegistryObject<SurfaceBuilder<SurfaceBuilderBaseConfiguration>> CARVED_SPRINGS_BUILDER = SURFACE_BUILDERS.register("carved_springs_builder", () -> new CarvedSpringsSurfaceBuilder(SurfaceBuilderBaseConfiguration.CODEC));
}
