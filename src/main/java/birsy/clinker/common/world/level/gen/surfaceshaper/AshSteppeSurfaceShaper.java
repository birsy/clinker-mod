package birsy.clinker.common.world.level.gen.surfaceshaper;

import birsy.clinker.common.world.level.gen.OthershoreNoiseComputers;
import birsy.clinker.common.world.level.gen.noise.NoiseComputerContext;
import birsy.clinker.common.world.level.gen.noise.NoiseComputerExecutor;
import birsy.clinker.common.world.level.gen.noise.NoiseHolder;

public class AshSteppeSurfaceShaper implements SurfaceShaper {
    @Override
    public double surfaceDensity(int x, int y, int z, double biomeContribution, NoiseComputerContext context) {
        NoiseHolder noise = context.noiseHolder();
        NoiseComputerExecutor executor = context.noiseComputerExecutor();

        double baseSurfaceHeight = executor.compute(x, y, z, OthershoreNoiseComputers.SURFACE_HEIGHT_COMPUTER);

        double ashDunes = Math.abs(executor.compute(x, y, z, OthershoreNoiseComputers.BASE_NOISE_2D[6])) * -2 - 1;
        ashDunes += (Math.abs(executor.compute(x, y, z, OthershoreNoiseComputers.BASE_NOISE_2D[5])) * -2 - 1) * 0.5;
        double weirdCliffs = executor.compute(x, y, z, OthershoreNoiseComputers.BASE_NOISE_2D[7]);
        weirdCliffs = (1 - Math.abs(weirdCliffs)) * Math.signum(weirdCliffs) * 5;
        weirdCliffs = weirdCliffs * (executor.compute(x, y, z, OthershoreNoiseComputers.BASE_NOISE_2D_ALT[7]) * 0.5 + 0.5);


        double surfaceHeight = baseSurfaceHeight + ashDunes * 2 + weirdCliffs + 5;

        double surfaceDensity = y - surfaceHeight;

        return surfaceDensity;
    }
}
