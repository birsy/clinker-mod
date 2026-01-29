package birsy.clinker.common.world.level.gen.content.surface.shaper;

import birsy.clinker.common.world.level.gen.OthershoreGenerationConstants;
import birsy.clinker.common.world.level.gen.system.noise.NoiseContext;
import birsy.clinker.common.world.level.gen.system.noise.NoiseFieldCache;
import birsy.clinker.core.registry.worldgen.ClinkerNoiseComputers;

public class SnakesSurfaceShaper extends SimpleSurfaceShaper {
    @Override
    public void prefillHeightmapNoiseFields(NoiseFieldCache cache) {}

    @Override
    public double getHeight(int x, int z, double weight, NoiseContext context) {
        return (OthershoreGenerationConstants.BASE_SEA_LEVEL + 1) * weight;
    }

    @Override
    public void prefillDensityNoiseFields(NoiseFieldCache cache, int minSurfaceHeight, int maxSurfaceHeight) {
        cache.fillNoiseField(minSurfaceHeight, maxSurfaceHeight, ClinkerNoiseComputers.BASE_NOISE_2D[6]);
        cache.fillNoiseField(minSurfaceHeight, maxSurfaceHeight, ClinkerNoiseComputers.BASE_NOISE_2D[7]);
        cache.fillNoiseField(minSurfaceHeight, maxSurfaceHeight, ClinkerNoiseComputers.BASE_NOISE_2D_ALT[6]);
        cache.fillNoiseField(minSurfaceHeight, maxSurfaceHeight, ClinkerNoiseComputers.BASE_NOISE_2D_ALT[7]);
    }

    @Override
    public double surfaceDensity(int x, int y, int z, double heightmapHeight, double heightmapGradient, double distanceToSurface, double biomeWeight, NoiseContext context) {
        double gN2 = context.retrieve(ClinkerNoiseComputers.BASE_NOISE_2D[7], x, y, z);
        double gN1 = context.retrieve(ClinkerNoiseComputers.BASE_NOISE_2D_ALT[6], x, y, z);


        double groundNoise = Math.min(Math.abs(gN2), 1.);
        groundNoise *= groundNoise*(3.-2.*groundNoise);
        groundNoise = 1.-groundNoise;

        double dGN = Math.abs((6.*gN2*gN2*gN2)/Math.abs(gN2) - 6.*gN2);

        groundNoise += gN1*dGN;
        groundNoise = Math.max(groundNoise, Math.abs(gN1)*2.);


        //groundNoise = 1.-groundNoise;
        groundNoise *= 20.;
        groundNoise -= 11.;
        groundNoise = Math.max(groundNoise, gN1*2.-5.);

        double density = distanceToSurface + /*Mth.lerp(flat, islandNoise, 0) +*/ groundNoise - 5;

        return density;
    }
}
