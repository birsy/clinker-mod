package birsy.clinker.common.world.level.gen.surfaceshaper;

import birsy.clinker.common.world.level.gen.OthershoreBiomeSource;
import birsy.clinker.common.world.level.gen.OthershoreNoiseComputers;
import birsy.clinker.common.world.level.gen.noise.NoiseComputerContext;
import birsy.clinker.common.world.level.gen.noise.NoiseComputerExecutor;
import birsy.clinker.common.world.level.gen.noise.NoiseHolder;
import birsy.clinker.core.Clinker;
import birsy.clinker.core.registry.ClinkerBlocks;
import birsy.clinker.core.util.MathUtils;
import net.minecraft.util.Mth;

public class BrineSwampSurfaceShaper implements SurfaceShaper {
    @Override
    public double surfaceDensity(int x, int y, int z, double biomeContribution, NoiseComputerContext context) {
        NoiseHolder noise = context.noiseHolder();
        NoiseComputerExecutor executor = context.noiseComputerExecutor();

        double baseSurfaceHeight = executor.compute(x, y, z, OthershoreNoiseComputers.SURFACE_HEIGHT_COMPUTER);

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

        double erosion = Math.abs(y - (OthershoreBiomeSource.SEA_HEIGHT + cragHeight * 0.5));
        erosion = Mth.clampedMap(erosion, 0, 5, 11, 0);
        double columnNoise = executor.compute(x, y, z, OthershoreNoiseComputers.BASE_NOISE_2D[4]);
        columnNoise += executor.compute(x, y, z, OthershoreNoiseComputers.BASE_NOISE_2D[3]) * 0.25;
        columnNoise /= 1.25;
        erosion *= columnNoise * 0.5 + 0.5;
        erosion *= Mth.clampedMap(executor.compute(x, y, z, OthershoreNoiseComputers.BASE_NOISE_2D_ALT[7]), -1, 1, 0.5, 1);

        double craggyIslandNoise = executor.compute(x, y, z, OthershoreNoiseComputers.BASE_NOISE_2D[7]);
        craggyIslandNoise += executor.compute(x, y, z, OthershoreNoiseComputers.BASE_NOISE_2D[6]) * 0.5;
        craggyIslandNoise = Mth.map(craggyIslandNoise, -1.0, -0.8, -1.0, 0.0);
        double craggyIslands = craggyIslandNoise * 20;
        craggyIslands += Math.max(0, y - (OthershoreBiomeSource.SEA_HEIGHT + cragHeight)) * 35;
        craggyIslands += erosion * 4;

        double craggyWall = Math.abs(executor.compute(x, y, z, OthershoreNoiseComputers.BASE_NOISE_2D[8])) * 80;
        craggyWall -= 4;
        craggyWall += Math.max(0, y - (OthershoreBiomeSource.SEA_HEIGHT + cragHeight)) * 2.2;
        craggyWall += Mth.clampedMap(craggyIslandNoise, 1.0, 1.2, 0, 12);
        craggyWall += erosion;

        craggyIslands = MathUtils.smoothMinExpo(craggyIslands, craggyWall, 3);

        density = MathUtils.smoothMinExpo(density, craggyIslands, 3);

        return density;
    }
}
