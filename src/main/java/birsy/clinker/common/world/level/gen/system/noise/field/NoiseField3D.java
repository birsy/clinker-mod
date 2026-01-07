package birsy.clinker.common.world.level.gen.system.noise.field;

public abstract class NoiseField3D extends NoiseField {
    final int chunkHeight;
    protected NoiseField3D(int chunkHeight, int blockPadding, int cellPadding) {
        super(chunkHeight - 1, blockPadding, cellPadding);
        this.chunkHeight = chunkHeight;
    }
}
