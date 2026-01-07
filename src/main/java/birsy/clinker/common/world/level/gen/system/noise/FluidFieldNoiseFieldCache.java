package birsy.clinker.common.world.level.gen.system.noise;

import birsy.clinker.common.world.level.gen.system.noise.field.NoiseField;

public class FluidFieldNoiseFieldCache extends NoiseFieldCache {
    final int fluidCellSize;
    public FluidFieldNoiseFieldCache(int minX, int minY, int minZ, int chunkHeight, SeededNoiseHolder noiseHolder, int fluidCellSize) {
        super(minX, minY, minZ, chunkHeight, noiseHolder);
        this.fluidCellSize = fluidCellSize;
    }

    protected NoiseField createNoiseField(NoiseComputer computer) {
        return computer.fieldType.get().create(this.chunkHeight, this.fluidCellSize + 1);
    }
}
