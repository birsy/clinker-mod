package birsy.clinker.common.world.level.gen.content.surface.shaper;

public class CliffSurfaceShaper {//extends BiomeShaper {
//    private static final int[] terraceSizes = {32, 13};
//
//    @Override
//    public double surfaceDensity(int x, int y, int z, double biomeContribution, NoiseComputerContext context) {
//        SeededNoiseHolder noise = context.noiseHolder();
//        NoiseFieldCache executor = context.noiseComputerExecutor();
//
//        double baseSurfaceHeight = executor.compute(x, y, z, OthershoreNoiseComputers.SURFACE_HEIGHT_COMPUTER);
//        double generalShape = executor.compute(x, y, z, OthershoreNoiseComputers.BASE_NOISE_2D[7]);
//        baseSurfaceHeight = baseSurfaceHeight + generalShape * 20;
//
//        double surfaceDensity = Double.MAX_VALUE;
//        for (int i = 0; i < terraceSizes.length; i++) {
//            double stepHeight = terraceSizes[i];
//            double terracedY = Math.ceil(y / stepHeight) * stepHeight;
//            double terraceShape = (y - terracedY) / stepHeight;
//            terraceShape *= terraceShape;
//            terracedY -= terraceShape * stepHeight * 0.5;
//
//            double cliffNoise =  i % 2 == 0 ?
//                    executor.compute(x, y, z, OthershoreNoiseComputers.BASE_NOISE_2D[6]) :
//                    executor.compute(x, y, z, OthershoreNoiseComputers.BASE_NOISE_2D_ALT[6]);
//            double surfaceHeight = baseSurfaceHeight + cliffNoise * 10;
//            surfaceDensity = Math.min(surfaceDensity, terracedY - surfaceHeight);
//        }
//
//        return surfaceDensity;
//    }
}
