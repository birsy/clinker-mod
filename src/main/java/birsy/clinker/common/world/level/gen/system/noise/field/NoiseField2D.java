package birsy.clinker.common.world.level.gen.system.noise.field;

public abstract class NoiseField2D extends NoiseField {
    protected NoiseField2D(int blockPadding, int cellPadding) {
        super(0, blockPadding, cellPadding);
    }
}
