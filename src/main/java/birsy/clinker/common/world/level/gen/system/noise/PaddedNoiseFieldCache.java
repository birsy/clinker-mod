package birsy.clinker.common.world.level.gen.system.noise;

import birsy.clinker.common.world.level.gen.system.noise.field.NoiseField;

public final class PaddedNoiseFieldCache extends NoiseFieldCache {
    final int paddingSize;
    public PaddedNoiseFieldCache(int minX, int minY, int minZ, int chunkHeight, SeededNoiseHolder noiseHolder, int paddingSize) {
        super(minX, minY, minZ, chunkHeight, noiseHolder);
        this.paddingSize = paddingSize;
    }

    protected NoiseField createNoiseField(NoiseComputer computer) {
        return computer.fieldType.get().create(this.chunkHeight, this.paddingSize + 1);
    }
}
