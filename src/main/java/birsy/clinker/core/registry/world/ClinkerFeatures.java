package birsy.clinker.core.registry.world;
import birsy.clinker.common.world.feature.enviornment.*;
import birsy.clinker.common.world.feature.plant.BrambleRootsFeature;
import birsy.clinker.common.world.feature.plant.RootedAshFeature;
import birsy.clinker.core.Clinker;
import net.minecraft.world.gen.feature.*;
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

	// Soon, my sweet prince...!
	public static final RegistryObject<Feature<NoFeatureConfig>> BRAMBLE_ROOTS = FEATURES.register("bramble_roots", () -> new BrambleRootsFeature(NoFeatureConfig.field_236558_a_));
	public static final RegistryObject<Feature<NoFeatureConfig>> ROOTED_ASH = FEATURES.register("rooted_ash", () -> new RootedAshFeature(NoFeatureConfig.field_236558_a_));

	public static final RegistryObject<Feature<ColumnConfig>> CRAGROCK = FEATURES.register("cragrock", () -> new CragrockFeature(ColumnConfig.CODEC));
	public static final RegistryObject<Feature<BlockStateFeatureConfig>> BOULDER = FEATURES.register("boulder", () -> new BoulderFeature(BlockStateFeatureConfig.field_236455_a_));

	public static final RegistryObject<Feature<NoFeatureConfig>> ASH_DUNE = FEATURES.register("ash_dune", () -> new AshDuneFeature(NoFeatureConfig.field_236558_a_));
	public static final RegistryObject<Feature<BlobReplacementConfig>> CAVE_PUDDLE = FEATURES.register("cave_puddle", () -> new CavePuddleFeature(BlobReplacementConfig.CODEC));
	public static final RegistryObject<Feature<BlobReplacementConfig>> CAVE_FLOOR = FEATURES.register("cave_floor", () -> new CaveFloorFeature(BlobReplacementConfig.CODEC));
	public static final RegistryObject<Feature<BlobReplacementConfig>> ASH_GEODE = FEATURES.register("ash_geode", () -> new LayeredBlobFeature(BlobReplacementConfig.CODEC));
	public static final RegistryObject<Feature<BlockStateFeatureConfig>> AQUIFER = FEATURES.register("aquifer", () -> new AquiferFeature(BlockStateFeatureConfig.field_236455_a_));

	public static final RegistryObject<Feature<NoFeatureConfig>> SPELEOTHEM = FEATURES.register("speleothem", () -> new SpeleothemFeature(NoFeatureConfig.field_236558_a_));
}