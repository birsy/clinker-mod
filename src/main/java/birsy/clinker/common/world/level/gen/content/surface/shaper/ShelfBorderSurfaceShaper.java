package birsy.clinker.common.world.level.gen.content.surface.shaper;

import birsy.clinker.common.world.level.gen.system.noise.NoiseContext;
import birsy.clinker.common.world.level.gen.system.noise.NoiseFieldCache;
import birsy.clinker.core.util.MathUtils;
import net.minecraft.util.Mth;

import static birsy.clinker.core.registry.worldgen.ClinkerNoiseComputers.*;

public class ShelfBorderSurfaceShaper extends SimpleSurfaceShaper {
    @Override
    public void prefillHeightmapNoiseFields(NoiseFieldCache cache) {
        cache.fillNoiseField(LOWER_SHELF_HEIGHT);
    }
    @Override
    public double getHeight(int x, int z, double weight, NoiseContext context) {
        return context.retrieve(LOWER_SHELF_HEIGHT, x, 0, z) + 10;
    }

    @Override
    public void prefillDensityNoiseFields(NoiseFieldCache cache, int minSurfaceHeight, int maxSurfaceHeight) {
        cache.fillNoiseField(UPPER_SHELF_HEIGHT);
        cache.fillNoiseField(CLIFF_STRATIFIED_Y);
        cache.fillNoiseField(BASE_NOISE[4]);
        cache.fillNoiseField(BASE_NOISE_2D[6]);

        cache.fillNoiseField(BIG_CRACKLE);
    }
    @Override
    public double surfaceDensity(int x, int y, int z, double heightmapHeight, double heightmapGradient, double distanceToSurface, double biomeWeight, NoiseContext context) {
        double upperShelfHeight = context.retrieve(UPPER_SHELF_HEIGHT, x, y, z) - 20;
        double cliffY = context.retrieve(CLIFF_STRATIFIED_Y, x, y, z);
        double baseNoise = context.retrieve(BASE_NOISE[4], x, y, z);
        double baseNoise2d = context.retrieve(BASE_NOISE_2D[6], x, y, z);

        double bigCrackle = context.retrieve(BIG_CRACKLE, x, y, z) + baseNoise * 0.1;
        bigCrackle = Math.min(bigCrackle, 0.5);

        double xzDist = bigCrackle * -40 + Mth.clampedMap(baseNoise2d, -1, 1, 5, 12) + (1 - biomeWeight) * 50;
        double lengthUpPillar = Mth.clampedMap(cliffY, heightmapHeight, upperShelfHeight, 0, 1);

        double yDist = y - upperShelfHeight;
        if (yDist < 0) yDist = 0;

        double bigPillars = xzDist + yDist * 5;

        bigPillars += Mth.lerp(lengthUpPillar, 5, 0);
        bigPillars += baseNoise * 8;

        return MathUtils.smoothMinExpo(distanceToSurface, bigPillars, 4);
    }

    @Override
    public int upperBound() {
        return 80;
    }
}
