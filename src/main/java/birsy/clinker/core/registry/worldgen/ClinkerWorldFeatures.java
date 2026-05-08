package birsy.clinker.core.registry.worldgen;

import birsy.clinker.common.world.level.gen.content.worldfeatures.OreVeinWorldFeature;
import birsy.clinker.common.world.level.gen.content.worldfeatures.RiverWorldFeature;
import birsy.clinker.common.world.level.gen.content.worldfeatures.UndergroundLakeWorldFeature;
import birsy.clinker.common.world.level.gen.system.metachunk.worldfeature.WorldFeatureSpawnSet;
import birsy.clinker.common.world.level.gen.system.metachunk.worldfeature.WorldFeatureType;
import birsy.clinker.core.Clinker;
import birsy.clinker.core.registry.ClinkerBlocks;
import birsy.clinker.core.registry.ClinkerRegistries;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.util.valueproviders.UniformInt;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public final class ClinkerWorldFeatures {
    public static final class Types {
        public static final DeferredRegister<WorldFeatureType<?>> WORLD_FEATURE_TYPES = DeferredRegister.create(ClinkerRegistries.WORLD_FEATURE_REGISTRY, Clinker.MOD_ID);

        public static final Supplier<WorldFeatureType<UndergroundLakeWorldFeature>> UNDERGROUND_LAKE =
                WORLD_FEATURE_TYPES.register("underground_lake", () -> new WorldFeatureType<>(0, 0, UndergroundLakeWorldFeature::realize));
        public static final Supplier<WorldFeatureType<RiverWorldFeature>> RIVER =
                WORLD_FEATURE_TYPES.register("river", () -> new WorldFeatureType<>(100, 0, RiverWorldFeature::realize));

        public static final Supplier<WorldFeatureType<OreVeinWorldFeature>> LEAD_ORE_VEIN =
                WORLD_FEATURE_TYPES.register("lead_ore_vein", () -> new WorldFeatureType<>(100, 0,
                        OreVeinWorldFeature.fromConfig(
                                new OreVeinWorldFeature.Configuration(
                                        ClinkerBlocks.LEAD_ORE.get().defaultBlockState(),
                                        ClinkerBlocks.RAW_LEAD_BLOCK.get().defaultBlockState(),
                                        UniformInt.of(0, 130),
                                        UniformInt.of(60, 80),
                                        UniformInt.of(30, 30)
                                )
                        ))
                );
    }

    public static final class Spawns {
        public static final DeferredRegister<WorldFeatureSpawnSet> WORLD_FEATURE_SPAWNS = DeferredRegister.create(ClinkerRegistries.WORLD_FEATURE_SPAWN_SET_REGISTRY, Clinker.MOD_ID);

        public static final Supplier<WorldFeatureSpawnSet> UNDERGROUND_LAKE =
                WORLD_FEATURE_SPAWNS.register("underground_lake",
                        () -> WorldFeatureSpawnSet.builder(4)
                                .add(Types.UNDERGROUND_LAKE.get(), 4, 12)
                                .build()
                );

        public static final Supplier<WorldFeatureSpawnSet> RIVER =
                WORLD_FEATURE_SPAWNS.register("river",
                        () -> WorldFeatureSpawnSet.builder(5)
                                .add(Types.RIVER.get(), 0, 3)
                                .build()
                );

        public static final Supplier<WorldFeatureSpawnSet> LEAD_ORE_VEIN =
                WORLD_FEATURE_SPAWNS.register("lead_ore_vein",
                        () -> WorldFeatureSpawnSet.builder(4)
                                .add(Types.LEAD_ORE_VEIN.get(), 1, 2)
                                .build()
                );
    }
}
