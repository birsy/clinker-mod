package birsy.clinker.common.world.level.gen.surfaceshaper;

import birsy.clinker.common.world.level.gen.OthershoreBiomeSource;
import birsy.clinker.common.world.level.gen.OthershoreNoiseComputers;
import birsy.clinker.common.world.level.gen.noise.NoiseComputerContext;
import birsy.clinker.common.world.level.gen.noise.NoiseComputerExecutor;
import birsy.clinker.common.world.level.gen.noise.NoiseHolder;
import birsy.clinker.core.registry.ClinkerBlocks;
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

        double biggerIslandsN = executor.compute(x, y, z, OthershoreNoiseComputers.BASE_NOISE_2D[7]);
        biggerIslandsN += executor.compute(x, y, z, OthershoreNoiseComputers.BASE_NOISE_2D[5]) * 0.1;
        biggerIslandsN += executor.compute(x, y, z, OthershoreNoiseComputers.BASE_NOISE_2D[4]) * 0.05;

        biggerIslandsN /= 1.15;
        double biggerIslands = Mth.map(biggerIslandsN, 0.4, 1.0, 0.0, 1.0);
        biggerIslands = Math.max(biggerIslands, 0);
        if (biggerIslands > 0) biggerIslands += Mth.map(executor.compute(x, y, z, OthershoreNoiseComputers.BASE_NOISE_2D[6]), -1, 1, 4.0, 12.0);

        double bigOpens = executor.compute(x, y, z, OthershoreNoiseComputers.BASE_NOISE_2D_ALT[7]) * 1;
        bigOpens = Mth.map(bigOpens, 0.9, 1.0, 0.0, -1.0);
        bigOpens = Math.min(bigOpens, 0);

        double flat = executor.compute(x, y, z, OthershoreNoiseComputers.BASE_NOISE_2D_ALT[6]);
        //flat = Mth.map(flat, -1, 1, 0.0, 1.0);
        flat = Math.clamp(flat * 3, 0, 1) * 0.9;

        double surfaceHeight = baseSurfaceHeight + islandNoise + bigOpens;
        surfaceHeight = Mth.lerp(flat, surfaceHeight, OthershoreBiomeSource.SEA_HEIGHT - 3) + biggerIslands;

        double density = y - surfaceHeight;
        //density += Math.min(executor.compute(x, y, z, OthershoreNoiseComputers.SPELEOTHEMS) * 5, 0);

        double erosionHeight = (OthershoreBiomeSource.SEA_HEIGHT + 3);
        double erosion = y - erosionHeight;
        if (erosion < 0) erosion *= 4;
        erosion = Math.abs(erosion);

        double erosionDepth = executor.compute(x, y, z, OthershoreNoiseComputers.BASE_NOISE_2D_ALT[4]);
        erosionDepth = Mth.map(erosionDepth, -1, 1, 0, 12);
        erosionDepth *= Mth.map(executor.compute(x, y, z, OthershoreNoiseComputers.BASE_NOISE_2D_ALT[6]) * 2, -1, 1, 0, 1);
        erosion = Mth.clampedMap(erosion, 0, 7, erosionDepth, 0);

        density += erosion;

        double pillar = executor.compute(x, y, z, OthershoreNoiseComputers.BASE_NOISE_2D_ALT[6]);
        pillar = Mth.map(pillar, 0.93, 1, 0, -1) * 8;
        double erosion2 = y - (erosionHeight + 6);
        if (erosion < 0) erosion *= 0.5;
        erosion2 = Math.abs(erosion2);
        erosion2 = Mth.clampedMap(erosion2, 0, 7, erosionDepth / 6.0 + 3, 0);
        pillar += erosion2;

        double pillarTop = Math.max(0, y - (erosionHeight + 12));
        pillar += pillarTop * 4;
        pillar = Math.min((pillar * 10) + Math.max((biggerIslandsN * 0.5 + 0.5) * 300, 0), 0);

        density += pillar;

        return density;
    }
}
