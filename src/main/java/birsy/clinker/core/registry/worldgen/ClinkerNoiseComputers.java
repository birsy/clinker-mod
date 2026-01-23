package birsy.clinker.core.registry.worldgen;

import birsy.clinker.common.world.level.gen.OthershoreBiomeSource;
import birsy.clinker.common.world.level.gen.system.noise.NoiseComputer;
import birsy.clinker.common.world.level.gen.system.noise.field.NoiseFieldFiller;
import birsy.clinker.common.world.level.gen.system.noise.field.NoiseFieldType;
import birsy.clinker.common.world.level.gen.system.noise.field.NoiseFieldTypes;
import birsy.clinker.core.Clinker;
import birsy.clinker.core.registry.ClinkerRegistries;
import birsy.clinker.core.util.MathUtils;
import net.minecraft.util.Mth;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ClinkerNoiseComputers {
    public static final DeferredRegister<NoiseComputer> NOISE_COMPUTERS = DeferredRegister.create(ClinkerRegistries.NOISE_COMPUTER_REGISTRY, Clinker.MOD_ID);

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
                    () -> NoiseFieldTypes.VERY_COARSE_2D,
                    (dependencies, registry) -> {
                        registry.registerNoise("base_middle_shelf",
                                2, 1.0, 4.0, 0.7, 0.0);
                        registry.registerNoise("base_upper_shelf");
                        registry.registerNoise("base_seas");
                        registry.registerNoise("base_erosion");
                    },
                    (x, y, z, context) -> {
                        double scale = 1;
                        double frequency = (1 / 500.0) / scale;
                        double val;
                        double erosion = context.sample("base_erosion", x * frequency, z * frequency);
                        erosion = Mth.clampedMap(erosion, -1, 1, 0, 1);

                        double middleShelf = context.sample("base_middle_shelf", x * frequency * 0.25, z * frequency * 0.25);
                        middleShelf = middleShelf * (1 / Mth.clampedMap(erosion, 0, 1, 0.5, 1));
                        middleShelf = Mth.clampedMap(middleShelf, -1, -0.1, 0, 1);

                        double seas = context.sample("base_seas", x * frequency * 0.2, z * frequency * 0.2) - 0.5;
                        seas = seas * (1 / Mth.clampedMap(erosion, 0, 1, 0.1, 0.2));
                        seas = Math.clamp(seas / 2.0 + 0.5, 0, 1);

                        val = Mth.clampedMap(middleShelf, 0, 1, 25 + OthershoreBiomeSource.SEA_HEIGHT, 5 + OthershoreBiomeSource.MIDDLE_SHELF_HEIGHT);
                        val = Mth.lerp(seas, val, OthershoreBiomeSource.SEA_HEIGHT - 3);

                        return ((val - OthershoreBiomeSource.SEA_HEIGHT) * scale) + OthershoreBiomeSource.SEA_HEIGHT;
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
    public static final Supplier<NoiseComputer> CAVE_ENTRANCE_MASK  = NOISE_COMPUTERS.register("cave_entrance",
            () -> new NoiseComputer(
                    () -> NoiseFieldTypes.COARSE_2D,
                    (dependencies, registry) -> registry.registerNoise("cave_entrance"),
                    (x, y, z, context) ->
                            Mth.clampedMap(context.sample("cave_entrance", x / 128.0, z / 128.0),
                                    0.55, 0.8, 0.0, 1.0)
            )
    );

    private static final double CAVE_NOODLE_FREQUENCY = 1 / 150.0;
    public static final Supplier<NoiseComputer> CAVE_NOODLE_A  = NOISE_COMPUTERS.register("cave_noodle_a",
            () -> new NoiseComputer(
                    () -> NoiseFieldTypes.COARSE,
                    (dependencies, registry) -> registry.registerNoise("cave_noodle_a"),
                    (x, y, z, context) -> context.sample("cave_noodle_a", x * CAVE_NOODLE_FREQUENCY, y * CAVE_NOODLE_FREQUENCY, z * CAVE_NOODLE_FREQUENCY)
            )
    );
    public static final Supplier<NoiseComputer> CAVE_NOODLE_B  = NOISE_COMPUTERS.register("cave_noodle_b",
            () -> new NoiseComputer(
                    () -> NoiseFieldTypes.COARSE,
                    (dependencies, registry) -> registry.registerNoise("cave_noodle_b"),
                    (x, y, z, context) -> context.sample("cave_noodle_b", x * CAVE_NOODLE_FREQUENCY, y * CAVE_NOODLE_FREQUENCY * 2, z * CAVE_NOODLE_FREQUENCY)
            )
    );
    public static final Supplier<NoiseComputer> CAVE_NOODLES  = NOISE_COMPUTERS.register("cave_noodles",
            () -> new NoiseComputer(
                    () -> NoiseFieldTypes.FINE,
                    (dependencies, registry) -> {
                        dependencies.addDependency(SPELEOTHEMS);
                        dependencies.addDependency(CAVE_NOODLE_A);
                        dependencies.addDependency(CAVE_NOODLE_B);
                    },
                    (x, y, z, context) -> {
                        double frequency = 1.0 / 150.0;
                        double caveNoiseA = context.retrieve(CAVE_NOODLE_A, x, y, z);
                        double caveNoiseB = context.retrieve(CAVE_NOODLE_B, x, y, z);
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
                                -1, 1, -15, 3
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

    // reusable noise arrays
    public static final Supplier<NoiseComputer>[] BASE_NOISE = baseNoiseArray("base_noise", 10, false);
    public static final Supplier<NoiseComputer>[] BASE_NOISE_ALT = baseNoiseArray("base_noise_alt", 10, false);
    public static final Supplier<NoiseComputer>[] BASE_NOISE_2D = baseNoiseArray("base_noise_2d", 10, true);
    public static final Supplier<NoiseComputer>[] BASE_NOISE_2D_ALT = baseNoiseArray("base_noise_2d_alt", 10, true);
    private static Supplier<NoiseComputer> baseNoise(String name, int index, double horizontalFrequency, double verticalFrequency, boolean twoDimensional) {
        String concatenatedName = name + "_" + index;
        Supplier<NoiseFieldType> fieldType;
        boolean useHighResCache = horizontalFrequency < 4;
        if (twoDimensional) {
            fieldType = useHighResCache ?
                    (() -> NoiseFieldTypes.FINE_2D) :
                    (() -> NoiseFieldTypes.COARSE_2D);
        } else {
            fieldType = useHighResCache ?
                    (() -> NoiseFieldTypes.FINE) :
                    (() -> NoiseFieldTypes.COARSE);
        }
        NoiseFieldFiller filler;
        if (twoDimensional) {
            filler = (x, y, z, context) ->
                    context.sample(concatenatedName, x * horizontalFrequency, z * horizontalFrequency);
        } else {
            filler = (x, y, z, context) ->
                    context.sample(concatenatedName, x * horizontalFrequency, y * verticalFrequency, z * horizontalFrequency);
        }
        return NOISE_COMPUTERS.register(
                concatenatedName,
                () -> new NoiseComputer(
                        fieldType, (dependencies, registry) -> registry.registerNoise(concatenatedName), filler
                )
        );
    }
    private static Supplier<NoiseComputer>[] baseNoiseArray(String name, int length, boolean twoDimensional) {
        Supplier<NoiseComputer>[] noiseComputers = new Supplier[length];
        for (int i = 0; i < length; i++) {
            int size = 1 << i;
            noiseComputers[i] = baseNoise(name, i, 1.0 / size, 0.5 / size, twoDimensional);
        }
        return noiseComputers;
    }
}
