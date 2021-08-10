package birsy.clinker.core.registry.world;
import birsy.clinker.common.world.feature.enviornment.*;
import birsy.clinker.common.world.feature.plant.BrambleBlobFeature;
import birsy.clinker.common.world.feature.plant.BrambleRootsFeature;
import birsy.clinker.common.world.feature.plant.RootGrassFeature;
import birsy.clinker.common.world.feature.plant.RootedAshFeature;
import birsy.clinker.core.Clinker;
import net.minecraft.world.gen.feature.*;
import net.minecraft.world.level.levelgen.placement.FeatureDecorator;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.BlockStateConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.ColumnFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.ReplaceSphereConfiguration;

public class ClinkerFeatures
{
	public static final DeferredRegister<Feature<?>> FEATURES = DeferredRegister.create(ForgeRegistries.FEATURES, Clinker.MOD_ID);
	public static final DeferredRegister<FeatureDecorator<?>> PLACEMENT = DeferredRegister.create(ForgeRegistries.DECORATORS, Clinker.MOD_ID);
	public static void init()
	{
		FEATURES.register(FMLJavaModLoadingContext.get().getModEventBus());
		PLACEMENT.register(FMLJavaModLoadingContext.get().getModEventBus());
	}

	public static final RegistryObject<Feature<NoneFeatureConfiguration>> BRAMBLE_BLOB = FEATURES.register("bramble_blob", () -> new BrambleBlobFeature(NoneFeatureConfiguration.CODEC));
	public static final RegistryObject<Feature<NoneFeatureConfiguration>> ROOTED_ASH = FEATURES.register("rooted_ash", () -> new RootedAshFeature(NoneFeatureConfiguration.CODEC));

	public static final RegistryObject<Feature<ColumnFeatureConfiguration>> CRAGROCK = FEATURES.register("cragrock", () -> new CragrockFeature(ColumnFeatureConfiguration.CODEC));
	public static final RegistryObject<Feature<BlockStateConfiguration>> BOULDER = FEATURES.register("boulder", () -> new BoulderFeature(BlockStateConfiguration.CODEC));

	public static final RegistryObject<Feature<NoneFeatureConfiguration>> ASH_DUNE = FEATURES.register("ash_dune", () -> new AshDuneFeature(NoneFeatureConfiguration.CODEC));
	public static final RegistryObject<Feature<ReplaceSphereConfiguration>> CAVE_PUDDLE = FEATURES.register("cave_puddle", () -> new CavePuddleFeature(ReplaceSphereConfiguration.CODEC));
	public static final RegistryObject<Feature<ReplaceSphereConfiguration>> CAVE_FLOOR = FEATURES.register("cave_floor", () -> new CaveFloorFeature(ReplaceSphereConfiguration.CODEC));
	public static final RegistryObject<Feature<ReplaceSphereConfiguration>> ASH_GEODE = FEATURES.register("ash_geode", () -> new LayeredBlobFeature(ReplaceSphereConfiguration.CODEC));
	public static final RegistryObject<Feature<BlockStateConfiguration>> AQUIFER = FEATURES.register("aquifer", () -> new AquiferFeature(BlockStateConfiguration.CODEC));

	public static final RegistryObject<Feature<ReplaceSphereConfiguration>> SHORELINE_PUDDLE = FEATURES.register("shoreline_puddle", () -> new ShorelinePuddleFeature(ReplaceSphereConfiguration.CODEC));

	public static final RegistryObject<Feature<NoneFeatureConfiguration>> SPELEOTHEM = FEATURES.register("speleothem", () -> new SpeleothemFeature(NoneFeatureConfiguration.CODEC));
}