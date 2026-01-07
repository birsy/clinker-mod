package birsy.clinker.common.world.level.gen.content.biomeshaper;

import birsy.clinker.common.world.level.gen.system.BiomeShaper;

public class LowerShelfBiomeShaper {//extends BiomeShaper {
//    @Override
//    public double surfaceDensity(int x, int y, int z, double biomeContribution, NoiseComputerContext context) {
//        SeededNoiseHolder noise = context.noiseHolder();
//        NoiseFieldCache executor = context.noiseComputerExecutor();
//
//        double baseSurfaceHeight = executor.compute(x, y, z, OthershoreNoiseComputers.SURFACE_HEIGHT_COMPUTER);
//        double smallNoise = executor.compute(x, y, z, OthershoreNoiseComputers.BASE_NOISE_2D[6]) * 0.5 + 0.5;
//        baseSurfaceHeight += (smallNoise * 2 - 1) * 10;
//
//        double terraceHeight = 40,
//               heightPerTerrace = (baseSurfaceHeight - OthershoreBiomeSource.MIDDLE_SHELF_HEIGHT) / terraceHeight,
//               terracedBaseHeight = Math.floor(heightPerTerrace);
//        terracedBaseHeight += Mth.smoothstep(Math.clamp((heightPerTerrace - terracedBaseHeight) * 6, 0, 1));
//        terracedBaseHeight *= terraceHeight;
//        terracedBaseHeight += OthershoreBiomeSource.MIDDLE_SHELF_HEIGHT;
//
//        double terraceHeight2 = 15,
//               heightPerTerrace2 = baseSurfaceHeight / terraceHeight2,
//               terracedBaseHeight2 = Math.floor(heightPerTerrace2);
//        terracedBaseHeight2 += Mth.smoothstep(Math.clamp((heightPerTerrace2 - terracedBaseHeight2) * 3, 0, 1));
//        terracedBaseHeight2 *= terraceHeight2;
//
//        double subtraction = executor.compute(x, y, z, OthershoreNoiseComputers.BASE_NOISE_2D_ALT[8]);
//        terracedBaseHeight = Math.max(terracedBaseHeight, terracedBaseHeight2 + subtraction * 10);
//
//        double erosion = executor.compute(x, y, z, OthershoreNoiseComputers.BASE_NOISE_2D[7]) * 0.5 + 0.5;
//        terracedBaseHeight = Mth.lerp(erosion * 0.3, terracedBaseHeight, baseSurfaceHeight);
//
//        double density = y - terracedBaseHeight;
//        double lumpiness = Mth.map(
//                executor.compute(x, y, z, OthershoreNoiseComputers.BASE_NOISE_2D_ALT[7]),
//                -1, 1,
//                3, 5
//        );
//        density += executor.compute(x, y, z, OthershoreNoiseComputers.BASE_NOISE[6]) * lumpiness;
//
//        double cracks = Math.abs(executor.compute(x, y, z, OthershoreNoiseComputers.BASE_NOISE_ALT[5]));
//        cracks = 1 - Math.pow(1 - cracks, 8);
//        cracks *= Mth.lerp(erosion, 1, 0);
//        density -= cracks * 8;
//
//        return density;
//    }
}
