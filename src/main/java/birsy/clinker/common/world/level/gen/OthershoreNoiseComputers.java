package birsy.clinker.common.world.level.gen;

import birsy.clinker.common.world.level.gen.fluid.FluidFiller;
import birsy.clinker.common.world.level.gen.fluid.FluidLevel;
import birsy.clinker.common.world.level.gen.noise.CacheType;
import birsy.clinker.common.world.level.gen.noise.NoiseComputer;
import birsy.clinker.common.world.level.gen.noise.NoiseComputerExecutor;
import birsy.clinker.common.world.level.gen.noise.NoiseHolder;
import birsy.clinker.core.util.MathUtils;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.Blocks;

public class OthershoreNoiseComputers {
    private static NoiseComputer baseNoise(int index, double horizontalFrequency, double verticalFrequency, boolean twoDimensional) {
        String name = "base_noise_" + index;
        boolean useHighResCache = horizontalFrequency < 4;
        return new NoiseComputer(name,
                twoDimensional ? useHighResCache ? CacheType.INTERPOLATED_2D_FINE : CacheType.INTERPOLATED_2D_COARSE :
                                 useHighResCache ? CacheType.INTERPOLATED_FINE : CacheType.INTERPOLATED_COARSE,
                (x, y, z, context) -> {
                    NoiseHolder noise = context.noiseHolder();
                    noise.registerNoise(name);
                    return twoDimensional ?
                            noise.sample(name, x * horizontalFrequency, z * horizontalFrequency) : // 2D noise samples are faster
                            noise.sample(name, x * horizontalFrequency, y * verticalFrequency, z * horizontalFrequency);
        });
    }
    private static NoiseComputer[] noiseComputerArray(int offset, int length, boolean twoDimensional) {
        NoiseComputer[] noiseComputers = new NoiseComputer[length];
        for (int i = 0; i < length; i++) {
            int size = 1 << i;
            noiseComputers[i] = baseNoise(i + offset, 1.0 / size, 0.5 / size, twoDimensional);
        }
        return noiseComputers;
    }

    public static final NoiseComputer[] BASE_NOISE = noiseComputerArray(0, 10, false);
    public static final NoiseComputer[] BASE_NOISE_ALT = noiseComputerArray(4, 10, false);
    public static final NoiseComputer[] BASE_NOISE_2D = noiseComputerArray(0, 10, true);
    public static final NoiseComputer[] BASE_NOISE_2D_ALT = noiseComputerArray(4, 10, true);

    public static final NoiseComputer EMPTY = new NoiseComputer("empty", CacheType.NONE, (x, y, z, context) -> 1);

    public static final NoiseComputer SURFACE_HEIGHT_COMPUTER = new NoiseComputer("surface_height", CacheType.INTERPOLATED_2D_VERY_COARSE, (x, y, z, context) -> {
//        if (true)
//            return OthershoreBiomeSource.SEA_HEIGHT - 3;

        NoiseHolder noise = context.noiseHolder();
        noise.registerNoise("base_middle_shelf", 2, 4.0, 0.7, 0.0);
        noise.registerNoise("base_upper_shelf");
        noise.registerNoise("base_seas");
        noise.registerNoise("base_erosion");

        double scale = 1;//1 / 10.0;
        double frequency = (1 / 500.0) / scale;
        double val;
        double erosion = noise.sample("base_erosion", x * frequency, z * frequency);
        erosion = Mth.clampedMap(erosion, -1, 1, 0, 1);

        double middleShelf = noise.sample("base_middle_shelf", x * frequency * 0.25, z * frequency * 0.25);
        middleShelf = middleShelf * (1 / Mth.clampedMap(erosion, 0, 1, 0.5, 1));
        middleShelf = Mth.clampedMap(middleShelf, -1, -0.1, 0, 1);

        double seas = noise.sample("base_seas", x * frequency * 0.2, z * frequency * 0.2) - 0.5;
        seas = seas * (1 / Mth.clampedMap(erosion, 0, 1, 0.1, 0.2));
        seas = Math.clamp(seas / 2.0 + 0.5, 0, 1);

        val = Mth.clampedMap(middleShelf, 0, 1, 25 + OthershoreBiomeSource.SEA_HEIGHT, 5 + OthershoreBiomeSource.MIDDLE_SHELF_HEIGHT);
        val = Mth.lerp(seas, val, OthershoreBiomeSource.SEA_HEIGHT - 3);

        return ((val - OthershoreBiomeSource.SEA_HEIGHT) * scale) + OthershoreBiomeSource.SEA_HEIGHT;//Mth.clampedMap(val, -1, 1, 50 * scale, 300 * scale);
    });

    public static final NoiseComputer SPELEOTHEMS = new NoiseComputer("speleothems", CacheType.INTERPOLATED_FINE,  (x, y, z, context) -> {
        NoiseHolder noise = context.noiseHolder();
        noise.registerNoise("speleothem");
        double frequency = 1 / 12.0;
        double value = noise.sample("speleothem", x * frequency, y * frequency * 0.08, z * frequency) / frequency;
        value += 8;
        return value;
    });

    private static final NoiseComputer CAVE_NOODLES  = new NoiseComputer("cave_noodles", CacheType.INTERPOLATED_FINE,  (x, y, z, context) -> {
        NoiseHolder noise = context.noiseHolder();
        NoiseComputerExecutor executor = context.noiseComputerExecutor();

        noise.registerNoise("cave_a");
        noise.registerNoise("cave_b");

        double frequency = 1.0 / 150.0;
        double caveNoiseA = noise.sample("cave_a", x * frequency, y * frequency, z * frequency);
        double caveNoiseB = noise.sample("cave_b", x * frequency, y * frequency * 2, z * frequency);
        double sumOfSquares = Math.sqrt(caveNoiseA * caveNoiseA + caveNoiseB * caveNoiseB) / frequency;
        sumOfSquares = 30 - sumOfSquares;

        double speleothem = executor.compute(x, y, z, SPELEOTHEMS);
        speleothem = MathUtils.smoothMinExpo(speleothem, 0, 3);

        double bedrockDistance = y;
        return  MathUtils.smoothMinExpo(sumOfSquares + speleothem * 5, bedrockDistance, 5);//Math.min(speleothem, sumOfSquares);
    });

    private static final NoiseComputer AQUIFER_CEILING_HEIGHT = new NoiseComputer("cave_aquifer_ceiling_height", CacheType.INTERPOLATED_2D_COARSE, (x, y, z, context) -> {
        NoiseHolder noise = context.noiseHolder();
        noise.registerNoise("aquifer_ceiling_height");
        return Mth.clampedMap(noise.sample("aquifer_ceiling_height", x / 64.0, z / 64.0), -1, 1, -20, -3);
    });
    private static final NoiseComputer AQUIFER_ISLANDS = new NoiseComputer("cave_aquifer_islands", CacheType.INTERPOLATED_2D_COARSE, (x, y, z, context) -> {
        NoiseHolder noise = context.noiseHolder();
        noise.registerNoise("aquifer_islands", 2, 2.5, 1, 0.5);
        return noise.sample("aquifer_islands", x / 128.0, z / 128.0);
    });
    private static final NoiseComputer AQUIFER_WALLS = new NoiseComputer("cave_aquifer_islands", CacheType.INTERPOLATED_2D_COARSE, (x, y, z, context) -> {
        NoiseHolder noise = context.noiseHolder();
        noise.registerNoise("aquifer_wall");
        noise.registerNoise("aquifer_wall_holes");
        noise.registerNoise("aquifer_wall_holes_small");

        double frequency = 1.0 / 190.0;
        double aquiferWall = Math.abs(noise.sample("aquifer_wall", x * frequency, z * frequency)) / frequency - 30;
        frequency = 1.0 / 128.0;
        double aquiferWallHoles = noise.sample("aquifer_wall_holes", x * frequency, z * frequency) / frequency;
        aquiferWallHoles = 30 - Math.abs(aquiferWallHoles);
        double aquiferWallHolesSmall = noise.sample("aquifer_wall_holes_small", x * frequency * 2, z * frequency * 2) / (frequency * 2);
        aquiferWallHolesSmall = Math.max(0, 20 - Math.abs(aquiferWallHolesSmall));

        aquiferWallHoles = aquiferWallHoles + aquiferWallHolesSmall * 8;

        return Math.max(aquiferWall, aquiferWallHoles);
    });

    private static final NoiseComputer CAVE_AQUIFER = new NoiseComputer("cave_aquifer", CacheType.INTERPOLATED_COARSE, (x, y, z, context) -> {
        if (y > 0) return -64;
        NoiseHolder noise = context.noiseHolder();
        NoiseComputerExecutor executor = context.noiseComputerExecutor();

        noise.registerNoise("aquifer_wall");
        noise.registerNoise("aquifer_wall_holes");

        double seaLevel = -40;
        double ceilingHeight = executor.compute(x, y, z, AQUIFER_CEILING_HEIGHT);

        double heightDensity = y > seaLevel ?
                Mth.map(y, seaLevel, ceilingHeight, 40, 0) :
                Mth.map(y, -55, seaLevel, 0, 40);

        double islands = executor.compute(x, y, z, AQUIFER_ISLANDS) * 12 + 6;
        double density = Math.min(heightDensity, y - (seaLevel - islands));

        double aquiferWall = executor.compute(x, y, z, AQUIFER_WALLS);

        density = MathUtils.smoothMinExpo(density, aquiferWall, 5);

        double speleothem = executor.compute(x, y, z, SPELEOTHEMS) - 1;
        speleothem = MathUtils.smoothMinExpo(speleothem, 0, 3);
        double ceilingSpeleothems = speleothem * Mth.clampedMap(y, seaLevel, ceilingHeight, Math.max(0, -islands), 1);
        double floorSpeleothems = speleothem * Mth.clampedMap(y, -55, seaLevel - 2, 1, 0);

        return density + ceilingSpeleothems * 7 + floorSpeleothems * 5;
    });

    public static final NoiseComputer CAVES = new NoiseComputer("caves", CacheType.INTERPOLATED_FINE, (x, y, z, context) -> {
        NoiseHolder noise = context.noiseHolder();
        NoiseComputerExecutor executor = context.noiseComputerExecutor();

        double noodleCaves = executor.compute(x, y, z, CAVE_NOODLES);
        double aquifer = executor.compute(x, y, z, CAVE_AQUIFER);

        return Math.max(noodleCaves, aquifer);
    });

    public static final FluidFiller FLUID_FILLER = (x, y, z, context) -> {
        NoiseComputerExecutor executor = context.noiseComputerExecutor();
        double surfaceHeight = executor.compute(x, y, z, OthershoreNoiseComputers.SURFACE_HEIGHT_COMPUTER);
        // sea level
        if (y > (surfaceHeight - 15)) {
            return new FluidLevel(OthershoreBiomeSource.SEA_HEIGHT, Blocks.WATER.defaultBlockState());
        }
        // the aquifer
        if (y < 0) {
            return new FluidLevel(-40, Blocks.WATER.defaultBlockState());
        }
        return FluidLevel.EMPTY;
    };
}
