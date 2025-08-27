package birsy.clinker.common.world.level.gen;

import birsy.clinker.common.world.level.gen.noise.CacheType;
import birsy.clinker.common.world.level.gen.noise.NoiseComputer;
import birsy.clinker.common.world.level.gen.noise.NoiseComputerExecutor;
import birsy.clinker.common.world.level.gen.noise.NoiseHolder;
import birsy.clinker.core.util.MathUtils;
import net.minecraft.util.Mth;

public class OthershoreNoiseComputers {
    public static final NoiseComputer SURFACE_HEIGHT_COMPUTER = new NoiseComputer("surface_height", CacheType.INTERPOLATED_2D_COARSE, (x, y, z, context) -> {
        NoiseHolder noise = context.noiseHolder();
        noise.registerNoise("base_plateaus", 2, 4.0, 0.7, 0.0);
        noise.registerNoise("base_upper_shelf");
        noise.registerNoise("base_seas");
        noise.registerNoise("base_erosion");

        double scale = 1;
        double frequency = (1 / 400.0) / scale;
        double val;
        double erosion = noise.sample("base_erosion", x * frequency, z * frequency);
        erosion = Mth.clampedMap(erosion, -1, 1, 0.5, 1);

        double plateaus = noise.sample("base_plateaus", x * frequency * 0.25, z * frequency * 0.25) + 0.2;
        plateaus = plateaus * (1 / erosion);
        plateaus = MathUtils.smoothMinExpo(plateaus, 1, 0.2);
        plateaus = -MathUtils.smoothMinExpo(-plateaus, 1, 0.5);
        val = plateaus;

        double upperShelf = noise.sample("base_upper_shelf", x * frequency, z * frequency) - 0.7;
        upperShelf = upperShelf * (1 / (erosion * 0.25));
        upperShelf = Math.clamp(upperShelf, 0, 1);
        upperShelf = upperShelf * 0.5 + 0.5;
        val = Mth.clampedLerp(Mth.clampedMap(val, -1, 1, -1, -0.2), 1, upperShelf + Math.min(plateaus, 0) * 5);
        val = Mth.clampedMap(val, -1, 1, -0.8, 1);

        double seas = noise.sample("base_seas", x * frequency * 0.2, z * frequency * 0.2) - 0.5;
        seas = seas * (1 / (erosion * 0.2));
        seas = Math.clamp(seas, -1, 1);
        val = Mth.clampedLerp(val, -1, (seas * 0.5 + 0.5));

        return Mth.clampedMap(val, -1, 1, 50 * scale, 300 * scale);
    });

    public static final NoiseComputer SPELEOTHEMS = new NoiseComputer("speleothems", CacheType.INTERPOLATED_FINE,  (x, y, z, context) -> {
        NoiseHolder noise = context.noiseHolder();
        noise.registerNoise("speleothem");
        double frequency = 1 / 12.0;
        double value = noise.sample("speleothem", x * frequency, y * frequency * 0.08, z * frequency) / frequency;
        value += 8;
        return value;
    });

    public static final NoiseComputer CAVE_NOODLES  = new NoiseComputer("cave_noodles", CacheType.INTERPOLATED_FINE,  (x, y, z, context) -> {
        NoiseHolder noise = context.noiseHolder();
        NoiseComputerExecutor executor = context.noiseComputerExecutor();

        noise.registerNoise("cave_a");
        noise.registerNoise("cave_b");

        double adjustedY = Math.floorMod(y, 16) / 16.0;
        adjustedY = Mth.lerp(0.25, adjustedY, Mth.smoothstep(adjustedY));
        adjustedY = adjustedY * 16 + Math.floor(y / 16.0) * 16.0;

        double frequency = 1.0 / 150.0;
        double caveNoiseA = noise.sample("cave_a", x * frequency, adjustedY * frequency, z * frequency);
        double caveNoiseB = noise.sample("cave_b", x * frequency, adjustedY * frequency * 2, z * frequency);
        double sumOfSquares = Math.sqrt(caveNoiseA * caveNoiseA + caveNoiseB * caveNoiseB) / frequency;
        sumOfSquares = 30 - sumOfSquares;

        double speleothem = executor.compute(x, y, z, SPELEOTHEMS);
        speleothem = MathUtils.smoothMinExpo(speleothem, 0, 3);

        double bedrockDistance = y;
        return  MathUtils.smoothMinExpo(sumOfSquares + speleothem * 5, bedrockDistance, 5);//Math.min(speleothem, sumOfSquares);
    });
}
