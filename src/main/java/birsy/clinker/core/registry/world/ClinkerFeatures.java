package birsy.clinker.core.registry.world;
import birsy.clinker.common.world.feature.enviornment.AshDuneFeature;
import birsy.clinker.common.world.feature.enviornment.CragrockFeature;
import birsy.clinker.common.world.feature.plant.BrambleRootsFeature;
import birsy.clinker.core.Clinker;
import net.minecraft.world.gen.feature.ColumnConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.placement.Placement;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ClinkerFeatures
{
	public static final DeferredRegister<Feature<?>> FEATURES = DeferredRegister.create(ForgeRegistries.FEATURES, Clinker.MOD_ID);
	public static final DeferredRegister<Placement<?>> PLACEMENT = DeferredRegister.create(ForgeRegistries.DECORATORS, Clinker.MOD_ID);
	public static void init()
	{
		FEATURES.register(FMLJavaModLoadingContext.get().getModEventBus());
		PLACEMENT.register(FMLJavaModLoadingContext.get().getModEventBus());
	}
	
	public static final RegistryObject<Feature<NoFeatureConfig>> BRAMBLE_ROOTS = FEATURES.register("bramble_roots", () -> new BrambleRootsFeature(NoFeatureConfig.field_236558_a_));
	public static final RegistryObject<Feature<ColumnConfig>> CRAGROCK = FEATURES.register("cragrock", () -> new CragrockFeature(ColumnConfig.CODEC));
	public static final RegistryObject<Feature<NoFeatureConfig>> ASH_DUNE = FEATURES.register("ash_dune", () -> new AshDuneFeature(NoFeatureConfig.field_236558_a_));
}