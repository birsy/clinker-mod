package birsy.clinker.core.registry.world;

import birsy.clinker.common.level.feature.AshBuildupFeature;
import birsy.clinker.core.Clinker;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraftforge.fmllegacy.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ClinkerFeatures {
    public static final DeferredRegister<Feature<?>> FEATURES = DeferredRegister.create(ForgeRegistries.FEATURES, Clinker.MOD_ID);

    public static final RegistryObject<Feature<NoneFeatureConfiguration>> ASH_LAYER = FEATURES.register("ash_layer", () -> new AshBuildupFeature(NoneFeatureConfiguration.CODEC));
}
