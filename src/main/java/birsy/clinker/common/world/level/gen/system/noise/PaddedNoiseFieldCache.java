package birsy.clinker.common.world.level.gen.system.noise;

import birsy.clinker.common.world.level.gen.system.noise.field.FieldFactory;
import birsy.clinker.common.world.level.gen.system.noise.field.NoiseField;

public final class PaddedNoiseFieldCache extends NoiseFieldCache {
    final int paddingSize;
    public PaddedNoiseFieldCache(int minX, int minY, int minZ, int chunkHeight, SeededNoiseHolder noiseHolder, int paddingSize) {
        super(minX, minY, minZ, chunkHeight, noiseHolder);
        this.paddingSize = paddingSize;
    }

    @Override
    protected NoiseField createStandardNoiseField(NoiseComputer computer, FieldFactory.Standard factory) {
        return factory.fieldType().create(this.chunkHeight, this.paddingSize + 1);
    }
}
