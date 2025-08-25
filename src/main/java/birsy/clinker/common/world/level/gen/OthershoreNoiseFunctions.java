package birsy.clinker.common.world.level.gen;

import birsy.clinker.common.world.level.gen.noise.CacheType;
import birsy.clinker.common.world.level.gen.noise.NoiseComputer;
import net.minecraft.util.Mth;

public class OthershoreNoiseFunctions {
//    protected static final NoiseComputer surfaceHeightComputer = new NoiseComputer("surface_height", CacheType.INTERPOLATED_2D_VERY_COARSE, (x, y, z, nCache) -> {
//        double frequency = 1 / 512.0;
//        double val;
//        double plateaus = noise.GetNoise(x * frequency, 0, z * frequency) + 0.1;
//        plateaus = Math.pow(Math.abs(plateaus), 0.3) * Math.signum(plateaus);
//        val = Mth.clampedMap(plateaus, -1, 1, -0.8, 0.7);
//
//        double upperShelf = noise.GetNoise(x * frequency * 1.2, -1000, z * frequency * 1.2) - 0.7;
//        upperShelf = Math.pow(Math.abs(upperShelf), 0.2) * Math.signum(upperShelf);
//        val = Mth.clampedLerp(val, 1, upperShelf * 0.5 + 0.5);
//
//        double seas = noise.GetNoise(x * frequency * 0.5, 1000, z * frequency * 0.5) + 0.1;
//        seas = Math.pow(Math.abs(seas), 0.15) * Math.signum(seas);
//        val = Mth.clampedLerp(val, -1, seas * 0.5 + 0.5);
//
//        return Mth.clampedMap(val, -1, 1, 50, 256);
//    });
}
