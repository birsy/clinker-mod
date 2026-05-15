package birsy.clinker.core.registry.worldgen;

import birsy.clinker.common.world.level.gen.OthershoreGenerationConstants;
import birsy.clinker.common.world.level.gen.system.noise.NoiseComputer;
import birsy.clinker.common.world.level.gen.system.noise.field.FieldFactory;
import birsy.clinker.common.world.level.gen.system.noise.field.NoiseFieldFiller;
import birsy.clinker.common.world.level.gen.system.noise.field.NoiseFieldTypes;
import birsy.clinker.common.world.level.gen.system.noise.voronoi.VoronoiDefinition;
import birsy.clinker.core.Clinker;
import birsy.clinker.core.registry.ClinkerRegistries;
import birsy.clinker.core.util.MathUtils;
import net.minecraft.util.Mth;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ClinkerNoiseComputers {
    public static final DeferredRegister<NoiseComputer> NOISE_COMPUTERS = DeferredRegister.create(ClinkerRegistries.NOISE_COMPUTER_REGISTRY, Clinker.MOD_ID);

    // reusable noise arrays
    public static final Supplier<NoiseComputer>[] BASE_NOISE = baseNoiseArray("base_noise", 10, false);
    public static final Supplier<NoiseComputer>[] BASE_NOISE_ALT = baseNoiseArray("base_noise_alt", 10, false);
    public static final Supplier<NoiseComputer>[] BASE_NOISE_2D = baseNoiseArray("base_noise_2d", 10, true);
    public static final Supplier<NoiseComputer>[] BASE_NOISE_2D_ALT = baseNoiseArray("base_noise_2d_alt", 10, true);

    public static final Supplier<NoiseComputer> SURFACE_DECORATOR_OFFSET_X = NOISE_COMPUTERS.register(
            "surface_decorator_offset_x",
            () -> new NoiseComputer(
                    () -> FieldFactory.standard(NoiseFieldTypes.DIRECT_2D),
                    (dependencies, registry) -> {
                        registry.registerNoise("surface_decorator_offset_x");
                        registry.registerNoise("surface_decorator_offset_x_fine");
                    },
                    (x, y, z, context) ->
                            context.sample("surface_decorator_offset_x", x / 24.0, z / 24.0) * 8 +
                                    context.sample("surface_decorator_offset_x_fine", x, z)
            )
    );
    public static final Supplier<NoiseComputer> SURFACE_DECORATOR_OFFSET_Z = NOISE_COMPUTERS.register(
            "surface_decorator_offset_z",
            () -> new NoiseComputer(
                    () -> FieldFactory.standard(NoiseFieldTypes.DIRECT_2D),
                    (dependencies, registry) -> {
                        registry.registerNoise("surface_decorator_offset_z");
                        registry.registerNoise("surface_decorator_offset_z_fine");
                    },
                    (x, y, z, context) ->
                            context.sample("surface_decorator_offset_z", x / 24.0, z / 24.0) * 8 +
                            context.sample("surface_decorator_offset_z_fine", x, z)
            )
    );


    public static final Supplier<NoiseComputer> STRATIFIED_Y_COARSE = NOISE_COMPUTERS.register(
            "stratified_y_coarse",
            () -> new NoiseComputer(
                    () -> FieldFactory.voronoi3d(64, 6),
                    (dependencies, registry) -> {},
                    (x, y, z, context) -> y
            )
    );
    public static final Supplier<NoiseComputer> STRATIFIED_Y_FINE = NOISE_COMPUTERS.register(
            "stratified_y_fine",
            () -> new NoiseComputer(
                    () -> FieldFactory.voronoi3d(32, 3),
                    (dependencies, registry) -> {},
                    (x, y, z, context) -> y
            )
    );

    public static final Supplier<NoiseComputer> CLIFF_STRATIFIED_Y = NOISE_COMPUTERS.register(
            "cliff_stratified_y",
            () -> new NoiseComputer(
                    () -> FieldFactory.standard(NoiseFieldTypes.FINE_Y),
                    (dependencies, registry) -> {
                        dependencies.addDependency(STRATIFIED_Y_COARSE);
                        dependencies.addDependency(STRATIFIED_Y_FINE);
                        dependencies.addDependency(BASE_NOISE_2D[7]);
                    },
                    (x, y, z, context) -> {
                        double erosion = context.retrieve(BASE_NOISE_2D[7], x, y, z);
                        return Mth.clampedMap((erosion - 0.5) * 5, -1, 1,
                                context.retrieve(STRATIFIED_Y_COARSE, x, y, z), context.retrieve(STRATIFIED_Y_FINE, x, y, z));
                    }
            )
    );

    public static final Supplier<NoiseComputer> WATERFALL_PRESENCE = NOISE_COMPUTERS.register(
            "waterfall_presence",
            () -> new NoiseComputer(
                    () -> FieldFactory.standard(NoiseFieldTypes.COARSE_2D),
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
    public static final Supplier<NoiseComputer> BASE_ELEVATION = NOISE_COMPUTERS.register(
            "base_elevation",
            () -> new NoiseComputer(
                    () -> FieldFactory.standard(NoiseFieldTypes.VERY_COARSE_2D),
                    (dependencies, registry) -> {
                        registry.registerNoise("base_elevation", 2, 1.0, 4.0, 0.25, 0.0);
                    },
                    (x, y, z, context) -> {
                        return Mth.clampedMap(
                                context.sample("base_elevation", x / 3000.0, z / 3000.0),
                                -0.5, 0.5,
                                OthershoreGenerationConstants.SEA_HEIGHT, OthershoreGenerationConstants.UPPER_SHELF_HEIGHT - 20
                        );
                    }
            )
    );
    public static final Supplier<NoiseComputer> UPPER_SHELF_ELEVATION = NOISE_COMPUTERS.register(
            "upper_shelf_elevation",
            () -> new NoiseComputer(
                    () -> FieldFactory.standard(NoiseFieldTypes.VERY_COARSE_2D),
                    (dependencies, registry) -> {
                        dependencies.addDependency(BASE_ELEVATION);
                        registry.registerNoise("upper_shelf_elevation");
                    },
                    (x, y, z, context) -> {
                        return Math.max(Mth.clampedMap(
                                context.sample("upper_shelf_elevation", x / 200.0, z / 200.0),
                                -1.0, 1.0,
                                OthershoreGenerationConstants.UPPER_SHELF_HEIGHT - 30, OthershoreGenerationConstants.UPPER_SHELF_HEIGHT
                        ), context.retrieve(BASE_ELEVATION, x, y, z));
                    }
            )
    );

    // placeholder gen stuff
    public static final Supplier<NoiseComputer> UPPER_SHELF_HEIGHT = NOISE_COMPUTERS.register(
            "upper_shelf_height",
            () -> new NoiseComputer(
                    () -> FieldFactory.standard(NoiseFieldTypes.VERY_COARSE_2D),
                    (dependencies, registry) -> {},
                    (x, y, z, context) -> OthershoreGenerationConstants.UPPER_SHELF_HEIGHT
            )
    );
    public static final Supplier<NoiseComputer> LOWER_SHELF_HEIGHT = NOISE_COMPUTERS.register(
            "lower_shelf_height",
            () -> new NoiseComputer(
                    () -> FieldFactory.standard(NoiseFieldTypes.VERY_COARSE_2D),
                    (dependencies, registry) -> {},
                    (x, y, z, context) -> OthershoreGenerationConstants.SEA_HEIGHT + 30
            )
    );
    public static final Supplier<NoiseComputer> BEACH_HEIGHT = NOISE_COMPUTERS.register(
            "beach_height",
            () -> new NoiseComputer(
                    () -> FieldFactory.standard(NoiseFieldTypes.VERY_COARSE_2D),
                    (dependencies, registry) -> {},
                    (x, y, z, context) -> OthershoreGenerationConstants.SEA_HEIGHT + 1
            )
    );
    public static final Supplier<NoiseComputer> SEA_FLOOR_HEIGHT = NOISE_COMPUTERS.register(
            "sea_floor_height",
            () -> new NoiseComputer(
                    () -> FieldFactory.standard(NoiseFieldTypes.VERY_COARSE_2D),
                    (dependencies, registry) -> {},
                    (x, y, z, context) -> OthershoreGenerationConstants.SEA_HEIGHT - 5
            )
    );



    private static final double CLIFF_ROCK_FREQUENCY = 1 / 20.0;
    public static final Supplier<NoiseComputer> CLIFF_ROCKS  = NOISE_COMPUTERS.register("cliff_rocks",
            () -> new NoiseComputer(
                    () -> FieldFactory.standard(NoiseFieldTypes.COARSE),
                    (dependencies, registry) -> registry.registerNoise("cliff_rocks"),
                    (x, y, z, context) -> context.sample("cliff_rocks", x * CLIFF_ROCK_FREQUENCY * 0.5, y * CLIFF_ROCK_FREQUENCY * 0.4, z * CLIFF_ROCK_FREQUENCY * 0.5)
            )
    );

    public static final Supplier<NoiseComputer> BIG_CRACKLE = NOISE_COMPUTERS.register(
            "big_crackle",
            () -> new NoiseComputer(
                    () -> FieldFactory.standard(NoiseFieldTypes.FINE_2D),
                    (dependencies, registry) -> {
                        registry.registerVoronoi("big_crackle", () -> VoronoiDefinition.twoDimensional(40));
                    },
                    (x, y, z, context) -> context.getVoronoi("big_crackle").distanceToBorder(x, y, z)
            )
    );

    public static final Supplier<NoiseComputer> SHATTERED_ISLANDS = NOISE_COMPUTERS.register("shattered_islands",
            () -> new NoiseComputer(
                    () -> FieldFactory.standard(NoiseFieldTypes.FINE_2D),
                    (dependencies, registry) -> {
                        registry.registerNoise("shattered_islands");
                    },
                    (x, y, z, context) -> {
                        return context.sample("shattered_islands", x / 24.0, z / 24.0);
                    }
            )
    );
    public static final Supplier<NoiseComputer> BIG_ISLANDS = NOISE_COMPUTERS.register("big_islands",
            () -> new NoiseComputer(
                    () -> FieldFactory.standard(NoiseFieldTypes.COARSE_2D),
                    (dependencies, registry) -> {
                        registry.registerNoise("big_islands");
                    },
                    (x, y, z, context) -> {
                        return Mth.map(
                                context.sample("big_islands", x / 200.0, z / 200.0),
                                -0.7, -1.0, 0, -1)
                                * 10;
                    }
            )
    );

    // caves
    public static final Supplier<NoiseComputer> SPELEOTHEMS = NOISE_COMPUTERS.register("speleothems",
            () -> new NoiseComputer(
                    () -> FieldFactory.standard(NoiseFieldTypes.COARSE_Y),
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
                    () -> FieldFactory.standard(NoiseFieldTypes.COARSE_2D),
                    (dependencies, registry) -> registry.registerNoise("cave_entrance"),
                    (x, y, z, context) ->
                            Mth.clampedMap(context.sample("cave_entrance", x / 128.0, z / 128.0),
                                    0.55, 0.8, 0.0, 1.0)
            )
    );

    public static final Supplier<NoiseComputer> CAVE_NOODLES  = NOISE_COMPUTERS.register("cave_noodles",
            () -> new NoiseComputer(
                    () -> FieldFactory.standard(NoiseFieldTypes.COARSE),
                    (dependencies, registry) -> {
                        dependencies.addDependency(SPELEOTHEMS);
                        registry.registerNoise("cave_noodle_a");
                        registry.registerNoise("cave_noodle_b");
                        registry.registerNoise("cave_noodle_c");
                        registry.registerNoise("cave_noodle_d");
                    },
                    (x, y, z, context) -> {
                        final double frequency = 1.0 / 150.0;
                        double caveNoiseA = context.sample("cave_noodle_a", x * frequency, y * frequency, z * frequency);
                        double caveNoiseB = context.sample("cave_noodle_b", x * frequency, y * frequency * 2, z * frequency);
                        double caveNoiseC = context.sample("cave_noodle_c", x * frequency, y * frequency, z * frequency);
                        double caveNoiseD = context.sample("cave_noodle_d", x * frequency, y * frequency * 2, z * frequency);

                        double sumOfSquaresA = Math.sqrt(caveNoiseA * caveNoiseA + caveNoiseB * caveNoiseB) / frequency;
                        sumOfSquaresA = 30 - sumOfSquaresA;
                        double sumOfSquaresB = Math.sqrt(caveNoiseC * caveNoiseC + caveNoiseD * caveNoiseD) / frequency;
                        sumOfSquaresB = 25 - sumOfSquaresB;

                        double noodleCaves = Math.max(sumOfSquaresA, sumOfSquaresB);

                        double speleothem = context.retrieve(SPELEOTHEMS, x, y, z);
                        speleothem = MathUtils.smoothMinExpo(speleothem, 0, 3);

                        double bedrockDistance = y;
                        return MathUtils.smoothMinExpo(noodleCaves + speleothem * 5, bedrockDistance, 5);
                    }
            )
    );

    public static final Supplier<NoiseComputer> AQUIFER_CEILING_HEIGHT = NOISE_COMPUTERS.register("cave_aquifer_ceiling_height",
            () -> new NoiseComputer(
                    () -> FieldFactory.standard(NoiseFieldTypes.COARSE_2D),
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
                    () -> FieldFactory.standard(NoiseFieldTypes.COARSE_2D),
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
                    () -> FieldFactory.standard(NoiseFieldTypes.COARSE_2D),
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
                    () -> FieldFactory.standard(NoiseFieldTypes.COARSE),
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
                    () -> FieldFactory.standard(NoiseFieldTypes.COARSE),
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

    public static final double ORE_VEIN_FREQUENCY = 1.0 / 45.0;
    public static final Supplier<NoiseComputer> ORE_VEIN_A  = NOISE_COMPUTERS.register("ore_vein_a",
            () -> new NoiseComputer(
                    () -> FieldFactory.standard(NoiseFieldTypes.COARSE),
                    (dependencies, registry) -> registry.registerNoise("ore_vein_a"),
                    (x, y, z, context) -> context.sample("ore_vein_a", x * ORE_VEIN_FREQUENCY, y * ORE_VEIN_FREQUENCY, z * ORE_VEIN_FREQUENCY)
            )
    );
    public static final Supplier<NoiseComputer> ORE_VEIN_B  = NOISE_COMPUTERS.register("ore_vein_b",
            () -> new NoiseComputer(
                    () -> FieldFactory.standard(NoiseFieldTypes.COARSE),
                    (dependencies, registry) -> registry.registerNoise("ore_vein_b"),
                    (x, y, z, context) -> context.sample("ore_vein_b", x * ORE_VEIN_FREQUENCY, y * ORE_VEIN_FREQUENCY, z * ORE_VEIN_FREQUENCY)
            )
    );

    private static Supplier<NoiseComputer> baseNoise(String name, int index, int size, boolean twoDimensional) {
        String concatenatedName = name + "_" + index;
        Supplier<FieldFactory> fieldType;

        double horizontalFrequency = 1.0 / size, verticalFrequency = 0.5 / size;

        int cacheResolution = 0;
        if (size >= 3) cacheResolution = 1;
        if (size >= 32) cacheResolution = 2;

        if (twoDimensional) {
            fieldType = switch (cacheResolution) {
                case 0 -> () -> FieldFactory.standard(NoiseFieldTypes.FINE_2D);
                case 1 -> () -> FieldFactory.standard(NoiseFieldTypes.COARSE_2D);
                default -> () -> FieldFactory.standard(NoiseFieldTypes.VERY_COARSE_2D);
            };
        } else {
            fieldType = switch (cacheResolution) {
                case 0 -> () -> FieldFactory.standard(NoiseFieldTypes.FINE);
                case 1 -> () -> FieldFactory.standard(NoiseFieldTypes.COARSE);
                default -> () -> FieldFactory.standard(NoiseFieldTypes.VERY_COARSE);
            };
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
            noiseComputers[i] = baseNoise(name, i, size, twoDimensional);
        }
        return noiseComputers;
    }
}
