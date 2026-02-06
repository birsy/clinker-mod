package birsy.clinker.core.registry.worldgen;

import birsy.clinker.common.world.level.gen.content.worldfeatures.UndergroundLakeWorldFeature;
import birsy.clinker.common.world.level.gen.system.metachunk.worldfeature.WorldFeatureSpawnSet;
import birsy.clinker.common.world.level.gen.system.metachunk.worldfeature.WorldFeatureType;
import birsy.clinker.core.Clinker;
import birsy.clinker.core.registry.ClinkerRegistries;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public final class ClinkerWorldFeatures {
    public static final class Types {
        public static final DeferredRegister<WorldFeatureType<?>> WORLD_FEATURE_TYPES = DeferredRegister.create(ClinkerRegistries.WORLD_FEATURE_REGISTRY, Clinker.MOD_ID);

        public static final Supplier<WorldFeatureType<UndergroundLakeWorldFeature>> UNDERGROUND_LAKE =
                WORLD_FEATURE_TYPES.register("underground_lake", () -> new WorldFeatureType<>(0, 0, UndergroundLakeWorldFeature::realize));
    }

    public static final class Spawns {
        public static final DeferredRegister<WorldFeatureSpawnSet> WORLD_FEATURE_SPAWNS = DeferredRegister.create(ClinkerRegistries.WORLD_FEATURE_SPAWN_SET_REGISTRY, Clinker.MOD_ID);

        public static final Supplier<WorldFeatureSpawnSet> UNDERGROUND_LAKE =
                WORLD_FEATURE_SPAWNS.register("underground_lake",
                        () -> WorldFeatureSpawnSet.builder(4)
                                .add(Types.UNDERGROUND_LAKE.get(), 4, 12)
                                .build()
                );
    }
}
