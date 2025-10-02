package birsy.clinker.core.registry.world;

import birsy.clinker.common.world.level.gen.feature.*;
import birsy.clinker.common.world.level.gen.worldfeature.WorldFeature;
import birsy.clinker.common.world.level.gen.worldfeature.WorldFeatureType;
import birsy.clinker.core.Clinker;
import birsy.clinker.core.registry.ClinkerRegistries;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.BlockStateConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ClinkerWorldFeatures {
    public static final DeferredRegister<WorldFeatureType> WORLD_FEATURES = DeferredRegister.create(ClinkerRegistries.WORLD_FEATURE_REGISTRY, Clinker.MOD_ID);

}
