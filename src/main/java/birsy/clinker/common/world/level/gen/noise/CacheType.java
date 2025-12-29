package birsy.clinker.common.world.level.gen.noise;

import java.util.function.Function;
import birsy.clinker.common.world.level.gen.noise.CachedNoiseComputerExecutor.*;
public enum CacheType {
    NONE((executor) -> null),
    DIRECT((executor) -> new DirectNoiseMap(executor.height)),
    TWO_DIMENSIONAL((executor) -> new TwoDimensionalNoiseMap()),
    INTERPOLATED_COARSE((executor) -> new InterpolatedNoiseMap(executor.height, 4, 2, false)),
    INTERPOLATED_2D_COARSE((executor) -> new Interpolated2DNoiseMap(4, false)),
    INTERPOLATED_FINE((executor) -> new InterpolatedNoiseMap(executor.height, 2, 4, false)),
    INTERPOLATED_2D_FINE((executor) -> new Interpolated2DNoiseMap(2, false)),
    INTERPOLATED_VERY_COARSE((executor) -> new InterpolatedNoiseMap(executor.height, 16, 16, false)),
    INTERPOLATED_2D_VERY_COARSE((executor) -> new Interpolated2DNoiseMap(16, false)),
    FINAL_DENSITY((executor) -> new InterpolatedNoiseMap(executor.height, 2, 4, true));

    private final Function<CachedNoiseComputerExecutor, CachedNoiseComputerExecutor.NoiseMap> mapConstructor;

    CacheType(Function<CachedNoiseComputerExecutor, CachedNoiseComputerExecutor.NoiseMap> mapConstructor) {
        this.mapConstructor = mapConstructor;
    }

    CachedNoiseComputerExecutor.NoiseMap create(CachedNoiseComputerExecutor executor) {
        return mapConstructor.apply(executor);
    }
}
