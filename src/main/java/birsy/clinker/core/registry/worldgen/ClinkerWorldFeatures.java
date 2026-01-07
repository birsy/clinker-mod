package birsy.clinker.core.registry.worldgen;

import birsy.clinker.common.world.level.gen.system.worldfeature.WorldFeatureType;
import birsy.clinker.core.Clinker;
import birsy.clinker.core.registry.ClinkerRegistries;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ClinkerWorldFeatures {
    public static final DeferredRegister<WorldFeatureType> WORLD_FEATURES = DeferredRegister.create(ClinkerRegistries.WORLD_FEATURE_REGISTRY, Clinker.MOD_ID);

}
