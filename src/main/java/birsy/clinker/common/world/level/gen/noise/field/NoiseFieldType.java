package birsy.clinker.common.world.level.gen.noise.field;

import birsy.clinker.common.world.level.gen.noise.NoiseComputerExecutor;

public interface NoiseFieldType {
    NoiseField create(NoiseComputerExecutor executor, int paddingBlocks);
}
