package birsy.clinker.common.world.level.gen.content.surface.shaper;

import birsy.clinker.common.world.level.gen.OthershoreGenerationConstants;
import birsy.clinker.common.world.level.gen.system.noise.NoiseContext;
import birsy.clinker.common.world.level.gen.system.noise.NoiseFieldCache;
import birsy.clinker.core.registry.worldgen.ClinkerNoiseComputers;
import birsy.clinker.core.util.MathUtils;
import net.minecraft.util.Mth;

public class SnakesSurfaceShaper extends SimpleSurfaceShaper {
    @Override
    public void prefillHeightmapNoiseFields(NoiseFieldCache cache) {}

    @Override
    public double getHeight(int x, int z, double weight, NoiseContext context) {
        return (OthershoreGenerationConstants.BASE_SEA_LEVEL - 2) * weight;
    }

    @Override
    public void prefillDensityNoiseFields(NoiseFieldCache cache, int minSurfaceHeight, int maxSurfaceHeight) {
        cache.fillNoiseField(minSurfaceHeight, maxSurfaceHeight, ClinkerNoiseComputers.BASE_NOISE_2D[4]);
        cache.fillNoiseField(minSurfaceHeight, maxSurfaceHeight, ClinkerNoiseComputers.BASE_NOISE_2D[5]);
        cache.fillNoiseField(minSurfaceHeight, maxSurfaceHeight, ClinkerNoiseComputers.BASE_NOISE_2D[7]);
        cache.fillNoiseField(minSurfaceHeight, maxSurfaceHeight, ClinkerNoiseComputers.SHATTERED_ISLANDS);
        cache.fillNoiseField(minSurfaceHeight, maxSurfaceHeight, ClinkerNoiseComputers.BIG_ISLANDS);
    }

    @Override
    public double surfaceDensity(int x, int y, int z, double heightmapHeight, double heightmapGradient, double distanceToSurface, double biomeWeight, NoiseContext context) {
        int seaFloorHeight = OthershoreGenerationConstants.BASE_SEA_LEVEL - 2;

        double path = context.retrieve(ClinkerNoiseComputers.BASE_NOISE_2D[7], x, y, z);
        path = Math.abs(path);

        double noise5 = context.retrieve(ClinkerNoiseComputers.BASE_NOISE_2D[5], x, y, z);
        double noise4 = context.retrieve(ClinkerNoiseComputers.BASE_NOISE_2D[4], x, y, z);
        double noise7 = context.retrieve(ClinkerNoiseComputers.BASE_NOISE_2D[7], x, y, z);

        double chainedIslands = (path * 64) - 10;
        double shatteredIslandsBase = context.retrieve(ClinkerNoiseComputers.SHATTERED_ISLANDS, x, y, z);
        double shatteredIslands = shatteredIslandsBase;
        //shatteredIslands += noise4 * 0.1;
        double shatteredIslandHeightOffset = Math.signum(shatteredIslands);

        shatteredIslands = Math.abs(shatteredIslands) * -16 + 2.5;
        chainedIslands = Math.max(chainedIslands, shatteredIslands);

        double yFac = y - heightmapHeight - 5;
        yFac -= shatteredIslandHeightOffset;
        yFac *= 3;
        yFac = Math.max(yFac, 0);

        chainedIslands += yFac;

        double underSeaRocks = (path * 64) - 30;
        underSeaRocks = Math.max(underSeaRocks, shatteredIslands);
        underSeaRocks += Math.max(0, y - heightmapHeight + 2) * 2;

        chainedIslands = Math.min(chainedIslands, underSeaRocks);

        double bigIslandsBase = context.retrieve(ClinkerNoiseComputers.BIG_ISLANDS, x, y, z);
        double bigIslands = bigIslandsBase + noise5 * 6 + noise4 * 2;
        double bigIslandHeight = seaFloorHeight + 7 + noise7 * 10;

        bigIslands += Math.max(y - bigIslandHeight, 0) * 2;

        double erosionHeight = seaFloorHeight + 5;
        double erosion = Math.abs(y - erosionHeight);
        erosion /= 10.0F;
        erosion = Mth.smoothstep(erosion);
        erosion *= 10.0F;
        bigIslands += Math.max(10.0F - erosion, 0) * 0.4;

        double spires = Mth.map(bigIslandsBase, -5, -10, 0, -1) + noise5 * 0.2;
        spires = Math.max(spires, shatteredIslandsBase);
        spires = Math.min(spires * 35, this.upperBound());
        spires += Math.max(y - heightmapHeight, 0) * 0.8;


        // combine everything
        bigIslands = MathUtils.smoothMinExpo(bigIslands, spires, 3);
        double shores = distanceToSurface + path * 2 + noise5;
        shores = MathUtils.smoothMinExpo(shores, underSeaRocks, 2);
        double rocks = MathUtils.smoothMinExpo(bigIslands, chainedIslands, 2);
        return Math.min(shores, rocks);
    }

    @Override
    public int upperBound() {
        return 48;
    }
}
