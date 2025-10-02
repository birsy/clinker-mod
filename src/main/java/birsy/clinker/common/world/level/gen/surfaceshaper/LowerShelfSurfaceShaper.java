package birsy.clinker.common.world.level.gen.surfaceshaper;

import birsy.clinker.common.world.level.gen.OthershoreBiomeSource;
import birsy.clinker.common.world.level.gen.OthershoreNoiseComputers;
import birsy.clinker.common.world.level.gen.noise.NoiseComputerContext;
import birsy.clinker.common.world.level.gen.noise.NoiseComputerExecutor;
import birsy.clinker.common.world.level.gen.noise.NoiseHolder;
import net.minecraft.util.Mth;

public class LowerShelfSurfaceShaper implements SurfaceShaper {
    @Override
    public double surfaceDensity(int x, int y, int z, double biomeContribution, NoiseComputerContext context) {
        NoiseHolder noise = context.noiseHolder();
        NoiseComputerExecutor executor = context.noiseComputerExecutor();

        double baseSurfaceHeight = executor.compute(x, y, z, OthershoreNoiseComputers.SURFACE_HEIGHT_COMPUTER);
        double density = y - baseSurfaceHeight;

        density += executor.compute(x, y, z, OthershoreNoiseComputers.BASE_NOISE_2D[7]) * 8;
        density += executor.compute(x, y, z, OthershoreNoiseComputers.BASE_NOISE_2D[5]) * 3;
        density += executor.compute(x, y, z, OthershoreNoiseComputers.BASE_NOISE_2D[4]) * 1;

        double weirdCliffs = executor.compute(x, y, z, OthershoreNoiseComputers.BASE_NOISE_2D_ALT[7]);
        weirdCliffs += executor.compute(x, y, z, OthershoreNoiseComputers.BASE_NOISE_2D[5]) * 0.5;
        weirdCliffs = Mth.lerp(biomeContribution, 0, weirdCliffs);
        weirdCliffs = (1 - Math.abs(weirdCliffs)) * Math.signum(weirdCliffs) * 5;
        //weirdCliffs *= (executor.compute(x, y, z, OthershoreNoiseComputers.BASE_NOISE_2D_ALT[7]) * 0.5 + 0.5);

        density += weirdCliffs;

        return density;
    }
}
