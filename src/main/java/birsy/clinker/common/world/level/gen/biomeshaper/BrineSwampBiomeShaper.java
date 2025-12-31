package birsy.clinker.common.world.level.gen.biomeshaper;

import birsy.clinker.common.world.level.gen.OthershoreBiomeSource;
import birsy.clinker.common.world.level.gen.OthershoreNoiseComputers;
import birsy.clinker.common.world.level.gen.noise.NoiseComputerContext;
import birsy.clinker.common.world.level.gen.noise.NoiseComputerExecutor;
import birsy.clinker.common.world.level.gen.noise.NoiseHolder;
import birsy.clinker.core.util.MathUtils;
import net.minecraft.util.Mth;

public class BrineSwampBiomeShaper implements BiomeShaper {
    @Override
    public double surfaceDensity(int x, int y, int z, double biomeContribution, NoiseComputerContext context) {
        int seaHeight = OthershoreBiomeSource.SEA_HEIGHT + 1;
        NoiseHolder noise = context.noiseHolder();
        NoiseComputerExecutor executor = context.noiseComputerExecutor();

        double baseSurfaceHeight = executor.compute(x, y, z, OthershoreNoiseComputers.SURFACE_HEIGHT_COMPUTER) + 2;

        double islandNoise = executor.compute(x, y, z, OthershoreNoiseComputers.BASE_NOISE_2D[5]) * 2;
        if (islandNoise < 0) islandNoise *= 2;
        islandNoise += executor.compute(x, y, z, OthershoreNoiseComputers.BASE_NOISE_2D[4]);

        double flat = executor.compute(x, y, z, OthershoreNoiseComputers.BASE_NOISE_2D_ALT[6]);
        flat = Math.clamp(flat * 3, 0, 1) * 0.9;

        double surfaceHeight = baseSurfaceHeight + Mth.lerp(flat, islandNoise, 0);
        double density = y - surfaceHeight;

        double cragHeight = Math.abs(executor.compute(x, y, z, OthershoreNoiseComputers.BASE_NOISE_2D_ALT[8]));
        cragHeight = Mth.clampedMap(cragHeight, -1, 1, 0, 1);
        cragHeight *= cragHeight;
        cragHeight = Mth.lerp(cragHeight, -5, 15);
        double cragHeightDensity = Math.max(0, y - (seaHeight + cragHeight));

        double erosion = Math.abs(y - (seaHeight + cragHeight * 0.5));
        erosion = Mth.clampedMap(erosion, 0, 5, 11, 0);
        double columnNoise = executor.compute(x, y, z, OthershoreNoiseComputers.BASE_NOISE_2D[4]);
        columnNoise += executor.compute(x, y, z, OthershoreNoiseComputers.BASE_NOISE_2D[3]) * 0.25;
        columnNoise /= 1.25;
        erosion *= columnNoise * 0.5 + 0.5;
        erosion *= Mth.clampedMap(executor.compute(x, y, z, OthershoreNoiseComputers.BASE_NOISE_2D_ALT[7]), -1, 1, 0.5, 1);

        double craggyIslandNoise = executor.compute(x, y, z, OthershoreNoiseComputers.BASE_NOISE_2D[7]);
        craggyIslandNoise += executor.compute(x, y, z, OthershoreNoiseComputers.BASE_NOISE_2D[6]) * 0.7;
        craggyIslandNoise = Mth.map(craggyIslandNoise, -1.0, -0.8, -1.0, 0.0);
        double craggyIslands = craggyIslandNoise * 20;
        craggyIslands += cragHeightDensity * 20;
        craggyIslands += erosion * 4;

        double craggyWall = Math.abs(executor.compute(x, y, z, OthershoreNoiseComputers.BASE_NOISE_2D[8])) * 80;
        craggyWall -= 4;
        craggyWall += cragHeightDensity * 2.2;
        craggyWall += Mth.clampedMap(craggyIslandNoise, 1.0, 1.2, 0, 12);
        craggyWall += erosion;

        craggyIslands = MathUtils.smoothMinExpo(craggyIslands, craggyWall, 3);

//        double funnyPillars = executor.compute(x, y, z, OthershoreNoiseComputers.BASE_NOISE_2D_ALT[7]);
//        funnyPillars = Mth.map(funnyPillars, -0.95, -1.0, 0.0, -1.0);
//        funnyPillars += Math.max(0, y - (seaHeight + cragHeight * 1.5));
//        funnyPillars *= 8;
//        funnyPillars += erosion * 0.5;
//
//        craggyIslands = Math.min(craggyIslands, funnyPillars);

        density = MathUtils.smoothMinExpo(density, craggyIslands, 3);

        return density;
    }
}
