package birsy.clinker.core.registry.worldgen;

import birsy.clinker.common.world.level.gen.system.noise.NoiseComputer;
import birsy.clinker.common.world.level.gen.system.noise.NoiseFieldCache;
import birsy.clinker.common.world.level.gen.system.noise.field.NoiseFieldTypes;
import birsy.clinker.core.Clinker;
import birsy.clinker.core.registry.ClinkerRegistries;
import birsy.clinker.core.util.MathUtils;
import net.minecraft.util.Mth;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ClinkerNoiseComputers {
    public static final DeferredRegister<NoiseComputer> NOISE_COMPUTERS = DeferredRegister.create(ClinkerRegistries.NOISE_COMPUTER_REGISTRY, Clinker.MOD_ID);

    public static final Supplier<NoiseComputer> DENSITY_TEST = NOISE_COMPUTERS.register(
            "density_test",
            () -> new NoiseComputer(
                    () -> NoiseFieldTypes.FINE,
                    (dependencies, registry) -> {
                        registry.registerNoise("wobble");
                    },
                    (x, y, z, context) -> {
                        double wobble = context.sample("wobble", x / 32.0, y / 64.0, z / 32.0);
                        return y - 64 + wobble * 32;
                    }
            )
    );

    public static final Supplier<NoiseComputer> WATERFALL_PRESENCE = NOISE_COMPUTERS.register(
            "waterfall_presence",
            () -> new NoiseComputer(
                    () -> NoiseFieldTypes.COARSE_2D,
                    (dependencies, registry) -> {
                        registry.registerNoise("waterfall");
                    },
                    (x, y, z, context) -> {
                        double value = context.sample("waterfall", x / 32.0, z / 32.0);
                        return Math.clamp(value - 0.3, 0, 1) * 2;
                    }
            )
    );

    // surface
    public static final Supplier<NoiseComputer> BASE_SURFACE_HEIGHT = NOISE_COMPUTERS.register(
            "base_surface_height",
            () -> new NoiseComputer(
                    () -> NoiseFieldTypes.COARSE_2D,
                    (dependencies, registry) -> {
                        registry.registerNoise("elevation");
                    },
                    (x, y, z, context) -> {
                        double value = context.sample("elevation", x / 128.0, z / 128.0);
                        return 64 + Mth.map(value, -1, 1, -10, 64);
                    }
            )
    );

    // caves
    public static final Supplier<NoiseComputer> SPELEOTHEMS = NOISE_COMPUTERS.register("speleothems",
            () -> new NoiseComputer(
                    () -> NoiseFieldTypes.FINE,
                    (dependencies, registry) -> {
                        registry.registerNoise("speleothem");
                    },
                    (x, y, z, context) -> {
                        double frequency = 1 / 12.0;
                        double value = context.sample("speleothem", x * frequency, y * frequency * 0.08, z * frequency) / frequency;
                        value += 8;
                        return value;
                    }
            )
    );
    public static final Supplier<NoiseComputer> CAVE_NOODLES  = NOISE_COMPUTERS.register("cave_noodles",
            () -> new NoiseComputer(
                    () -> NoiseFieldTypes.FINE,
                    (dependencies, registry) -> {
                        dependencies.addDependency(SPELEOTHEMS);
                        registry.registerNoise("cave_a");
                        registry.registerNoise("cave_b");
                    },
                    (x, y, z, context) -> {
                        double frequency = 1.0 / 150.0;
                        double caveNoiseA = context.sample("cave_a", x * frequency, y * frequency, z * frequency);
                        double caveNoiseB = context.sample("cave_b", x * frequency, y * frequency * 2, z * frequency);
                        double sumOfSquares = Math.sqrt(caveNoiseA * caveNoiseA + caveNoiseB * caveNoiseB) / frequency;
                        sumOfSquares = 30 - sumOfSquares;

                        double speleothem = context.retrieve(SPELEOTHEMS, x, y, z);
                        speleothem = MathUtils.smoothMinExpo(speleothem, 0, 3);

                        double bedrockDistance = y;
                        return MathUtils.smoothMinExpo(sumOfSquares + speleothem * 5, bedrockDistance, 5);
                    }
            )
    );

    public static final Supplier<NoiseComputer> AQUIFER_CEILING_HEIGHT = NOISE_COMPUTERS.register("cave_aquifer_ceiling_height",
            () -> new NoiseComputer(
                    () -> NoiseFieldTypes.COARSE_2D,
                    (dependencies, registry) -> {
                        registry.registerNoise("aquifer_ceiling_height");
                    },
                    (x, y, z, context) -> {
                        return Mth.clampedMap(
                                context.sample("aquifer_ceiling_height", x / 64.0, z / 64.0),
                                -1, 1, -20, -3
                        );
                    }
            )
    );
    public static final Supplier<NoiseComputer> AQUIFER_ISLANDS = NOISE_COMPUTERS.register("cave_aquifer_islands",
            () -> new NoiseComputer(
                    () -> NoiseFieldTypes.COARSE_2D,
                    (dependencies, registry) -> {
                        registry.registerNoise("aquifer_islands", 2, 1.0, 2.5, 1, 0.5);
                    },
                    (x, y, z, context) -> {
                        return context.sample("aquifer_islands", x / 128.0, z / 128.0);
                    }
            )
    );
    public static final Supplier<NoiseComputer> AQUIFER_WALLS = NOISE_COMPUTERS.register("cave_aquifer_walls",
            () -> new NoiseComputer(
                    () -> NoiseFieldTypes.COARSE_2D,
                    (dependencies, registry) -> {
                        registry.registerNoise("aquifer_wall");
                        registry.registerNoise("aquifer_wall_holes");
                        registry.registerNoise("aquifer_wall_holes_small");
                    },
                    (x, y, z, context) -> {
                        double frequency = 1.0 / 190.0;
                        double aquiferWall = Math.abs(context.sample("aquifer_wall", x * frequency, z * frequency)) / frequency - 30;
                        frequency = 1.0 / 128.0;
                        double aquiferWallHoles = context.sample("aquifer_wall_holes", x * frequency, z * frequency) / frequency;
                        aquiferWallHoles = 30 - Math.abs(aquiferWallHoles);
                        double aquiferWallHolesSmall = context.sample("aquifer_wall_holes_small", x * frequency * 2, z * frequency * 2) / (frequency * 2);
                        aquiferWallHolesSmall = Math.max(0, 20 - Math.abs(aquiferWallHolesSmall));

                        aquiferWallHoles = aquiferWallHoles + aquiferWallHolesSmall * 8;

                        return Math.max(aquiferWall, aquiferWallHoles);
                    }
            )
    );

    public static final Supplier<NoiseComputer> CAVE_AQUIFER = NOISE_COMPUTERS.register("cave_aquifer",
            () -> new NoiseComputer(
                    () -> NoiseFieldTypes.COARSE,
                    (dependencies, registry) -> {
                        dependencies.addDependency(AQUIFER_CEILING_HEIGHT.get());
                        dependencies.addDependency(AQUIFER_ISLANDS.get());
                        dependencies.addDependency(AQUIFER_WALLS.get());
                        dependencies.addDependency(SPELEOTHEMS.get());
                    },
                    (x, y, z, context) -> {
                        if (y > 0) return -64;

                        double seaLevel = -40;
                        double ceilingHeight = context.retrieve(AQUIFER_CEILING_HEIGHT, x, y, z);

                        double heightDensity = y > seaLevel ?
                                Mth.map(y, seaLevel, ceilingHeight, 40, 0) :
                                Mth.map(y, -55, seaLevel, 0, 40);

                        double islands = context.retrieve(AQUIFER_ISLANDS, x, y, z) * 12 + 6;
                        double density = Math.min(heightDensity, y - (seaLevel - islands));

                        double aquiferWall = context.retrieve(AQUIFER_WALLS, x, y, z);

                        density = MathUtils.smoothMinExpo(density, aquiferWall, 5);

                        double speleothem = context.retrieve(SPELEOTHEMS, x, y, z) - 1;
                        speleothem = MathUtils.smoothMinExpo(speleothem, 0, 3);
                        double ceilingSpeleothems = speleothem * Mth.clampedMap(y, seaLevel, ceilingHeight, Math.max(0, -islands), 1);
                        double floorSpeleothems = speleothem * Mth.clampedMap(y, -55, seaLevel - 2, 1, 0);

                        return density + ceilingSpeleothems * 7 + floorSpeleothems * 5;
                    }
            )
    );

    public static final Supplier<NoiseComputer> CAVES = NOISE_COMPUTERS.register("caves",
            () -> new NoiseComputer(
                    () -> NoiseFieldTypes.FINE,
                    (dependencies, registry) -> {
                        dependencies.addDependency(CAVE_NOODLES.get());
                        dependencies.addDependency(CAVE_AQUIFER.get());
                    },
                    (x, y, z, context) -> {
                        double noodleCaves = context.retrieve(CAVE_NOODLES, x, y, z);
                        double aquifer = context.retrieve(CAVE_AQUIFER, x, y, z);
                        return Math.max(noodleCaves, aquifer);
                    }
            )
    );
}
