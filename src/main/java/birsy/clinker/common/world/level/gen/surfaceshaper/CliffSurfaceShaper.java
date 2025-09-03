package birsy.clinker.common.world.level.gen.surfaceshaper;

import birsy.clinker.common.world.level.gen.OthershoreNoiseComputers;
import birsy.clinker.common.world.level.gen.noise.NoiseComputerContext;
import birsy.clinker.common.world.level.gen.noise.NoiseComputerExecutor;
import birsy.clinker.common.world.level.gen.noise.NoiseHolder;
import net.minecraft.util.Mth;

public class CliffSurfaceShaper implements SurfaceShaper {
    @Override
    public double surfaceDensity(int x, int y, int z, double biomeContribution, NoiseComputerContext context) {
        NoiseHolder noise = context.noiseHolder();
        NoiseComputerExecutor executor = context.noiseComputerExecutor();

        double baseSurfaceHeight = executor.compute(x, y, z, OthershoreNoiseComputers.SURFACE_HEIGHT_COMPUTER);
        double generalShape = executor.compute(x, y, z, OthershoreNoiseComputers.BASE_NOISE_2D[7]);
        baseSurfaceHeight = baseSurfaceHeight + generalShape * 20;

        double surfaceDensity = Double.MAX_VALUE;
        int[] terraceSizes = {32, 13};
        for (int i = 0; i < terraceSizes.length; i++) {
            double stepHeight = terraceSizes[i];
            double terracedY = Math.ceil(y / stepHeight) * stepHeight;
            double terraceShape = (y - terracedY) / stepHeight;
            terraceShape *= terraceShape;
            terracedY -= terraceShape * stepHeight * 0.5;

            double cliffNoise =  i % 2 == 0 ?
                    executor.compute(x, y, z, OthershoreNoiseComputers.BASE_NOISE_2D[6]) :
                    executor.compute(x, y, z, OthershoreNoiseComputers.BASE_NOISE_2D_ALT[6]);
            double surfaceHeight = baseSurfaceHeight + cliffNoise * 10;
            surfaceDensity = Math.min(surfaceDensity, terracedY - surfaceHeight);
            //terracedHeight = Math.ceil(terracedHeight / stepHeight) * stepHeight;
            //surfaceHeight = Math.max(baseSurfaceHeight, terracedHeight);
//            double stepHeight = terraceSizes[i];
//            int terracedY = (int) (Math.floor(y / stepHeight) * stepHeight);
//            double terraceShape = (y - terracedY) / stepHeight;//Mth.frac(y / (double)stepHeight);
//            terraceShape *= terraceShape;
//            double terracedCliffHeight = Mth.map(terracedY + terraceShape * stepHeight * 0.5,
//                    surfaceHeight, surfaceHeight + 20, 0, 1);
//            terracedCliffHeight = Mth.map(terracedCliffHeight, 0, 1, -1, 0);
//            terracedCliffHeight *= 40;
//
//            double cliffHighFreq = executor.compute(x, y, z, OthershoreNoiseComputers.BASE_NOISE[5]) * 0.5;
//            double cliffNoise = i % 2 == 0 ?
//                    executor.compute(x, terracedY, z, OthershoreNoiseComputers.BASE_NOISE_2D[6]) + cliffHighFreq :
//                    executor.compute(x, terracedY, z, OthershoreNoiseComputers.BASE_NOISE_2D_ALT[6]) + cliffHighFreq;
//            cliffDensity = Math.min(cliffDensity, terracedCliffHeight - cliffNoise * 25);
        }

        return surfaceDensity;
    }
}
