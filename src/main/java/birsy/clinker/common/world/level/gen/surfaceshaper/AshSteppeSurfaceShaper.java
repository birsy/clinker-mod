package birsy.clinker.common.world.level.gen.surfaceshaper;

import birsy.clinker.common.world.level.gen.OthershoreBiomeSource;
import birsy.clinker.common.world.level.gen.OthershoreNoiseComputers;
import birsy.clinker.common.world.level.gen.noise.NoiseComputerContext;
import birsy.clinker.common.world.level.gen.noise.NoiseComputerExecutor;
import birsy.clinker.common.world.level.gen.noise.NoiseHolder;
import net.minecraft.util.Mth;

public class AshSteppeSurfaceShaper implements SurfaceShaper {
    @Override
    public double surfaceDensity(int x, int y, int z, double biomeContribution, NoiseComputerContext context) {
        NoiseHolder noise = context.noiseHolder();
        NoiseComputerExecutor executor = context.noiseComputerExecutor();

        double baseSurfaceHeight = executor.compute(x, y, z, OthershoreNoiseComputers.SURFACE_HEIGHT_COMPUTER);

        double ashDunes = Math.abs(executor.compute(x, y, z, OthershoreNoiseComputers.BASE_NOISE_2D[6])) * -2 - 1;
        ashDunes += (Math.abs(executor.compute(x, y, z, OthershoreNoiseComputers.BASE_NOISE_2D[5])) * -2 - 1) * 0.5;
        double weirdCliffs = executor.compute(x, y, z, OthershoreNoiseComputers.BASE_NOISE_2D[7]);
        weirdCliffs = Mth.lerp(biomeContribution, 0, weirdCliffs);
        weirdCliffs = (1 - Math.abs(weirdCliffs)) * Math.signum(weirdCliffs) * 5;
        weirdCliffs *= (executor.compute(x, y, z, OthershoreNoiseComputers.BASE_NOISE_2D_ALT[7]) * 0.5 + 0.5);
        double surfaceHeight = baseSurfaceHeight + ashDunes * 2 + weirdCliffs + 5;
        double ashDunesHeight = Math.min(surfaceHeight, OthershoreBiomeSource.MIDDLE_SHELF_HEIGHT + 30);

        double surfaceDensity = y - surfaceHeight;

        double cliffExistenceNoise = executor.compute(x, y, z, OthershoreNoiseComputers.BASE_NOISE_2D_ALT[8]);
        double threshold = 0.9;
        cliffExistenceNoise = Mth.map(cliffExistenceNoise, threshold, 1, 0, -1);

        double cliffDensity = Double.MAX_VALUE;
        for (int i = 0; i < 3; i++) {
            double stepHeight = 5 + i * 6;
            int terracedY = (int) (Math.floor(y / stepHeight) * stepHeight);
            terracedY = (int)(terracedY + ashDunes * 2);
            double terraceShape = (y - terracedY) / stepHeight;//Mth.frac(y / (double)stepHeight);
            terraceShape *= terraceShape;
            double terracedCliffHeight = Mth.map(terracedY + terraceShape * stepHeight * 0.5, ashDunesHeight, ashDunesHeight + 40, 0, 1);
            terracedCliffHeight *= biomeContribution;
            terracedCliffHeight = Mth.map(terracedCliffHeight, 0, 1, -1, 0);

            double cliffMask = (terracedCliffHeight + cliffExistenceNoise * 0.5) * 40;
            if (cliffMask > 20) continue;
            double cliffHighFreq = executor.compute(x, y, z, OthershoreNoiseComputers.BASE_NOISE[4]) * 0.5;
            double cliffNoise = i % 2 == 0 ?
                    executor.compute(x, terracedY, z, OthershoreNoiseComputers.BASE_NOISE[5]) + cliffHighFreq :
                    executor.compute(x, terracedY, z, OthershoreNoiseComputers.BASE_NOISE_ALT[5]) + cliffHighFreq;
            cliffDensity = Math.min(cliffDensity, cliffMask - cliffNoise * 25);
        }

        return Math.min(surfaceDensity, cliffDensity);
    }

    private static double terrace(double noiseVal, double stepCount, double lowerSteepness, double upperSteepness) {
        double steps = stepCount * 0.5;
        double x = noiseVal * steps + 0.5;
        double expBase = (2 * (x - Math.round(x)));
        double steepness = expBase > 0.0 ? lowerSteepness : upperSteepness;
        double expPower = 1.0 / (1.0 - steepness);
        return ((Math.round(x) + 0.5 * (Math.pow(Math.abs(expBase), expPower) * Math.signum(expBase))) - 0.5) / steps;
    }
}
