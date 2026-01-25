package birsy.clinker.common.world.level.gen.content.surface.shaper;

import birsy.clinker.common.world.level.gen.OthershoreBiomeSource;
import birsy.clinker.common.world.level.gen.OthershoreGenerationConstants;
import birsy.clinker.common.world.level.gen.system.noise.NoiseContext;
import birsy.clinker.common.world.level.gen.system.noise.NoiseFieldCache;
import birsy.clinker.core.registry.worldgen.ClinkerNoiseComputers;
import birsy.clinker.core.util.MathUtils;
import net.minecraft.util.Mth;

public class BrineSwampSurfaceShaper extends SimpleSurfaceShaper {
    @Override
    public void prefillHeightmapNoiseFields(NoiseFieldCache cache) {}

    @Override
    public double getHeight(int x, int z, double weight, NoiseContext context) {
        return (OthershoreGenerationConstants.BASE_SEA_LEVEL - 1) * weight;
    }

    @Override
    public void prefillDensityNoiseFields(NoiseFieldCache cache, int minSurfaceHeight, int maxSurfaceHeight) {
        cache.fillNoiseField(minSurfaceHeight, maxSurfaceHeight, ClinkerNoiseComputers.BASE_NOISE_2D[3]);
        cache.fillNoiseField(minSurfaceHeight, maxSurfaceHeight, ClinkerNoiseComputers.BASE_NOISE_2D[4]);
        cache.fillNoiseField(minSurfaceHeight, maxSurfaceHeight, ClinkerNoiseComputers.BASE_NOISE_2D[5]);
        cache.fillNoiseField(minSurfaceHeight, maxSurfaceHeight, ClinkerNoiseComputers.BASE_NOISE_2D[6]);
        cache.fillNoiseField(minSurfaceHeight, maxSurfaceHeight, ClinkerNoiseComputers.BASE_NOISE_2D[7]);
        cache.fillNoiseField(minSurfaceHeight, maxSurfaceHeight, ClinkerNoiseComputers.BASE_NOISE_2D[8]);
        cache.fillNoiseField(minSurfaceHeight, maxSurfaceHeight, ClinkerNoiseComputers.BASE_NOISE_2D_ALT[6]);
        cache.fillNoiseField(minSurfaceHeight, maxSurfaceHeight, ClinkerNoiseComputers.BASE_NOISE_2D_ALT[7]);
        cache.fillNoiseField(minSurfaceHeight, maxSurfaceHeight, ClinkerNoiseComputers.BASE_NOISE_2D_ALT[8]);
    }

    @Override
    public double surfaceDensity(int x, int y, int z, double heightmapHeight, double heightmapGradient, double distanceToSurface, double biomeWeight, NoiseContext context) {
        int seaHeight = OthershoreGenerationConstants.BASE_SEA_LEVEL + 1;

        double islandNoise = context.retrieve(ClinkerNoiseComputers.BASE_NOISE_2D[5], x, y, z) * 2;
        if (islandNoise < 0) islandNoise *= 1.5;
        islandNoise += context.retrieve(ClinkerNoiseComputers.BASE_NOISE_2D[4], x, y, z);

        double flat = context.retrieve(ClinkerNoiseComputers.BASE_NOISE_2D_ALT[6], x, y, z);
        flat = Math.clamp(flat * 3, 0, 1) * 0.9;

        double density = distanceToSurface + Mth.lerp(flat, islandNoise, 0);

        double cragHeight = Math.abs(context.retrieve(ClinkerNoiseComputers.BASE_NOISE_2D_ALT[8], x, y, z));
        cragHeight = Mth.clampedMap(cragHeight, -1, 1, 0, 1);
        cragHeight *= cragHeight;
        cragHeight = Mth.lerp(cragHeight, -5, 15);
        double cragHeightDensity = Math.max(0, y - (seaHeight + cragHeight));

        double erosion = Math.abs(y - (seaHeight + cragHeight * 0.5));
        erosion = Mth.clampedMap(erosion, 0, 5, 11, 0);
        double columnNoise = context.retrieve(ClinkerNoiseComputers.BASE_NOISE_2D[4], x, y, z);
        columnNoise += context.retrieve(ClinkerNoiseComputers.BASE_NOISE_2D[3], x, y, z) * 0.25;
        columnNoise /= 1.25;
        erosion *= columnNoise * 0.5 + 0.5;
        erosion *= Mth.clampedMap(context.retrieve(ClinkerNoiseComputers.BASE_NOISE_2D_ALT[7], x, y, z), -1, 1, 0.5, 1);

        double craggyIslandNoise = context.retrieve(ClinkerNoiseComputers.BASE_NOISE_2D[7], x, y, z);
        craggyIslandNoise += context.retrieve(ClinkerNoiseComputers.BASE_NOISE_2D[6], x, y, z) * 0.7;
        craggyIslandNoise = Mth.map(craggyIslandNoise, -1.0, -0.8, -1.0, 0.0);
        double craggyIslands = craggyIslandNoise * 20;
        craggyIslands += cragHeightDensity * 20;
        craggyIslands += erosion * 4;

        double craggyWall = Math.abs(context.retrieve(ClinkerNoiseComputers.BASE_NOISE_2D[8], x, y, z)) * 80;
        craggyWall -= 4;
        craggyWall += cragHeightDensity * 2.2;
        craggyWall += Mth.clampedMap(craggyIslandNoise, 1.0, 1.2, 0, 12);
        craggyWall += erosion;

        craggyIslands = MathUtils.smoothMinExpo(craggyIslands, craggyWall, 3);

        density = MathUtils.smoothMinExpo(density, craggyIslands, 3);

        return density;
    }
}
