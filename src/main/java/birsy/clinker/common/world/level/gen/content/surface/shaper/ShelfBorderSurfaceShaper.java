package birsy.clinker.common.world.level.gen.content.surface.shaper;

import birsy.clinker.common.world.level.gen.system.noise.NoiseContext;
import birsy.clinker.common.world.level.gen.system.noise.NoiseFieldCache;
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
//        cache.fillNoiseField(UPPER_SHELF_HEIGHT);
//        cache.fillNoiseField(LOWER_SHELF_HEIGHT);
//        cache.fillNoiseField(CLIFF_STRATIFIED_Y);
//        cache.fillNoiseField(BASE_NOISE[5]);
//        cache.fillNoiseField(BASE_NOISE_2D[5]);
    }
    @Override
    public double surfaceDensity(int x, int y, int z, double heightmapHeight, double heightmapGradient, double distanceToSurface, double biomeWeight, NoiseContext context) {
//        double upperShelfHeight = context.retrieve(UPPER_SHELF_HEIGHT, x, y, z);
//        double lowerShelfHeight = context.retrieve(LOWER_SHELF_HEIGHT, x, y, z);
//        double cliffY = context.retrieve(CLIFF_STRATIFIED_Y, x, y, z);
//        double baseNoise = context.retrieve(BASE_NOISE[5], x, y, z);
//        double baseNoiseAlt = context.retrieve(BASE_NOISE_2D[5], x, y, z);
//
//        double bigCrackle = Math.abs(baseNoiseAlt);
//        double crackTopHeight = Mth.lerp(biomeWeight, lowerShelfHeight, upperShelfHeight - 10);
//
//        double xzDist = bigCrackle * -40 + 6;
//        double lengthUpPillar = Mth.clampedMap(cliffY, lowerShelfHeight, crackTopHeight, 0, 1);
//        xzDist += Mth.lerp(lengthUpPillar, 3, 0);
//        xzDist += baseNoise * 5;
//
//        double yDist = y - crackTopHeight;
//        yDist = Math.max(yDist, -1) * 0.25;
//        double bigPillars = Math.max(xzDist, yDist);

        return distanceToSurface;
    }

    @Override
    public int upperBound() {
        return 80;
    }
}
